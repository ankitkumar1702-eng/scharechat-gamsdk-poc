package com.example.gamsdkpoc.core.tracing

import android.os.Trace
import android.util.Log
import java.util.concurrent.ConcurrentHashMap

object AppTracer {

    private val asyncTraceCookies = ConcurrentHashMap<String, Int>()
    private val traceLock = Any()
    private const val TAG = "AppTracer"

    fun startTrace(tag: String, attributes: Map<String, String>? = null) {
        try {
            val fullTag = buildTag(tag, attributes)
            Log.d(TAG, "Starting trace: $fullTag")
            Trace.beginSection(fullTag)
            beginAsyncSection(tag, attributes)
        } catch (e: Exception) {
            Log.e(TAG, "Error starting trace: $tag", e)
        }
    }

    fun stopTrace() {
        try {
            Log.d(TAG, "Stopping trace (no tag)")
            Trace.endSection()
        } catch (e: Exception) {
            Log.e(TAG, "Error ending trace", e)
        }
    }

    fun stopTrace(tag: String) {
        try {
            Log.d(TAG, "Stopping trace: $tag")
            Trace.endSection()
            endAsyncSection(tag)
        } catch (e: Exception) {
            Log.e(TAG, "Error ending trace: $tag", e)
        }
    }

    fun beginAsyncSection(tag: String, attributes: Map<String, String>? = null): Int {
        return try {
            synchronized(traceLock) {
                val cookie = generateCookie(tag)
                val fullTag = buildTag(tag, attributes)
                Log.d(TAG, "Beginning async section: $fullTag with cookie: $cookie")
                
                Trace.beginAsyncSection(fullTag, cookie)
                asyncTraceCookies[tag] = cookie
                Log.d(TAG, "Async section started successfully: $fullTag")
                cookie
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error beginning async trace: $tag", e)
            -1
        }
    }

    fun endAsyncSection(tag: String) {
        try {
            synchronized(traceLock) {
                val cookie = asyncTraceCookies.remove(tag)
                if (cookie != null) {
                    Log.d(TAG, "Ending async section: $tag with cookie: $cookie")
                    val fullTag = buildTag(tag, null)
                    Trace.endAsyncSection(fullTag, cookie)
                    Log.d(TAG, "Async section ended successfully: $fullTag")
                } else {
                    Log.w(TAG, "No cookie found for async trace: $tag")
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error ending async trace: $tag", e)
        }
    }

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

    private fun generateCookie(tag: String): Int {
        return tag.hashCode() xor System.nanoTime().toInt()
    }

    private fun buildTag(tag: String, attributes: Map<String, String>?): String {
        if (attributes.isNullOrEmpty()) return tag
        val attrString = attributes.entries.joinToString(", ") { "${it.key}=${it.value}" }
        return "$tag [$attrString]"
    }

    fun clearAsyncTraces() {
        synchronized(traceLock) {
            asyncTraceCookies.clear()
        }
    }

    fun getActiveAsyncTraceCount(): Int {
        return asyncTraceCookies.size
    }
}
