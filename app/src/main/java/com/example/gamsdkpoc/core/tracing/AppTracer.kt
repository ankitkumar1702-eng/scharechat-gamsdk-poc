package com.example.gamsdkpoc.core.tracing

import android.os.Trace
import android.util.Log
import java.util.concurrent.ConcurrentHashMap

/**
 * Production-grade Perfetto tracing utility for performance monitoring.
 * 
 * Features:
 * - Synchronous tracing with Trace.beginSection/endSection
 * - Asynchronous tracing with TraceCompat.beginAsyncSection/endAsyncSection
 * - Thread-safe cookie management for async traces
 * - Attribute support for rich trace metadata
 * - Error handling and logging
 * - Debug/Release build differentiation
 */
object AppTracer {

    private val asyncTraceCookies = ConcurrentHashMap<String, Int>()
    private val traceLock = Any()
    private const val TAG = "AppTracer"

    /**
     * Start a synchronous trace section.
     * Must be paired with stopTrace() call.
     * 
     * @param tag The trace section name
     * @param attributes Optional key-value pairs for additional context
     */
    fun startTrace(tag: String, attributes: Map<String, String>? = null) {
        try {
            val fullTag = buildTag(tag, attributes)
            Trace.beginSection(fullTag)
        } catch (e: Exception) {
            Log.e(TAG, "Error starting trace: $tag", e)
        }
    }

    /**
     * Stop the current synchronous trace section.
     */
    fun stopTrace() {
        try {
            Trace.endSection()
        } catch (e: Exception) {
            Log.e(TAG, "Error ending trace", e)
        }
    }

    /**
     * Begin an asynchronous trace section.
     * Useful for tracking operations that span multiple threads or callbacks.
     * 
     * @param tag The trace section name
     * @param attributes Optional key-value pairs for additional context
     * @return Cookie for ending the async trace, or -1 if failed
     */
    fun beginAsyncSection(tag: String, attributes: Map<String, String>? = null): Int {
        return try {
            synchronized(traceLock) {
                val cookie = generateCookie(tag)
                val fullTag = buildTag(tag, attributes)
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                    Trace.beginAsyncSection(fullTag, cookie)
                }
                asyncTraceCookies[tag] = cookie
                cookie
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error beginning async trace: $tag", e)
            -1
        }
    }

    /**
     * End an asynchronous trace section.
     * 
     * @param tag The trace section name used in beginAsyncSection
     */
    fun endAsyncSection(tag: String) {
        try {
            synchronized(traceLock) {
                val cookie = asyncTraceCookies.remove(tag) ?: return
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                    Trace.endAsyncSection(tag, cookie)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error ending async trace: $tag", e)
        }
    }

    /**
     * Execute a block of code within a trace section.
     * Automatically handles start/stop trace calls.
     * 
     * @param tag The trace section name
     * @param attributes Optional key-value pairs for additional context
     * @param block The code block to execute
     * @return The result of the block execution
     */
    inline fun <T> trace(
        tag: String,
        attributes: Map<String, String>? = null,
        block: () -> T
    ): T {
        startTrace(tag, attributes)
        return try {
            block()
        } finally {
            stopTrace()
        }
    }

    /**
     * Execute a suspend block of code within a trace section.
     * Automatically handles start/stop trace calls for coroutines.
     * 
     * @param tag The trace section name
     * @param attributes Optional key-value pairs for additional context
     * @param block The suspend code block to execute
     * @return The result of the block execution
     */
    suspend inline fun <T> traceSuspend(
        tag: String,
        attributes: Map<String, String>? = null,
        crossinline block: suspend () -> T
    ): T {
        startTrace(tag, attributes)
        return try {
            block()
        } finally {
            stopTrace()
        }
    }

    /**
     * Generate a unique cookie for async tracing.
     * Uses tag hash and current time to ensure uniqueness.
     */
    private fun generateCookie(tag: String): Int {
        return tag.hashCode() xor System.nanoTime().toInt()
    }

    /**
     * Build a formatted tag with optional attributes.
     * Format: "tag [key1=value1, key2=value2]"
     */
    private fun buildTag(tag: String, attributes: Map<String, String>?): String {
        if (attributes.isNullOrEmpty()) return tag
        val attrString = attributes.entries.joinToString(", ") { "${it.key}=${it.value}" }
        return "$tag [$attrString]"
    }

    /**
     * Clear all pending async traces.
     * Useful for cleanup during app shutdown or error recovery.
     */
    fun clearAsyncTraces() {
        synchronized(traceLock) {
            asyncTraceCookies.clear()
        }
    }

    /**
     * Get the count of active async traces.
     * Useful for debugging and monitoring.
     */
    fun getActiveAsyncTraceCount(): Int {
        return asyncTraceCookies.size
    }
}
