package com.example.gamsdkpoc.core.tracing

import android.app.ActivityManager
import android.content.Context
import android.os.Build
import android.os.Debug
import android.os.Process
import android.os.Trace
import android.util.Log
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong

object AppTracer {

    private val asyncTraceCookies = ConcurrentHashMap<String, Int>()
    private val asyncTraceFullTags = ConcurrentHashMap<String, String>()
    private val traceMetrics = ConcurrentHashMap<String, TraceMetrics>()
    private val stateChanges = ConcurrentHashMap<String, StateChangeInfo>()
    private val activeTraces = ConcurrentHashMap<String, Long>()
    private val lastTraceTime = ConcurrentHashMap<String, Long>()
    private val traceLock = Any()
    private val traceCounter = AtomicLong(0)
    private const val DUPLICATE_THRESHOLD_MS = 1000L
    private const val TAG = "AppTracer"
    private const val PERF_TAG = "AppTracer_Performance"
    private const val STATE_TAG = "AppTracer_StateChange"
    private const val ERROR_TAG = "AppTracer_Error"
    
    private var appContext: Context? = null
    private val timeFormatter = SimpleDateFormat("HH:mm:ss.SSS", Locale.getDefault())

    data class TraceMetrics(
        val startTime: Long,
        val startMemory: Long,
        val threadName: String,
        val threadId: Long,
        var endTime: Long = 0,
        var endMemory: Long = 0,
        var duration: Long = 0,
        var memoryDelta: Long = 0
    )

    data class StateChangeInfo(
        val timestamp: Long,
        val fromState: String?,
        val toState: String,
        val context: String,
        val additionalData: Map<String, String>
    )

    fun initialize(context: Context) {
        appContext = context.applicationContext
        Log.i(TAG, "🚀 AppTracer initialized with enhanced app-sensitive tracking")
        Log.i(TAG, "📊 Features: Performance, Memory, State Changes, Errors, User Actions")
    }

    fun startTrace(tag: String, attributes: Map<String, String>? = null) {
        try {
            synchronized(traceLock) {
                val currentTime = System.currentTimeMillis()
                val lastTime = lastTraceTime[tag] ?: 0
                
                // Check for rapid duplicates within threshold
                if (currentTime - lastTime < DUPLICATE_THRESHOLD_MS) {
                    Log.d(TAG, "⚠️ Duplicate trace within ${DUPLICATE_THRESHOLD_MS}ms, skipping: $tag")
                    return
                }
                
                // Check if trace is already active
                if (activeTraces.containsKey(tag)) {
                    Log.d(TAG, "⚠️ Trace already active, skipping: $tag")
                    return
                }
                
                val traceId = traceCounter.incrementAndGet()
                val currentThread = Thread.currentThread()
                val startTime = currentTime
                val startMemory = getMemoryUsage()
                
                // Update last trace time and mark as active
                lastTraceTime[tag] = currentTime
                activeTraces[tag] = startTime
                
                val enhancedAttributes = buildEnhancedAttributes(attributes, traceId, currentThread)
                val fullTag = buildTag(tag, enhancedAttributes)
                
                traceMetrics[tag] = TraceMetrics(
                    startTime = startTime,
                    startMemory = startMemory,
                    threadName = currentThread.name,
                    threadId = currentThread.id
                )
                
                Log.d(TAG, "🔥 Starting trace: $fullTag")
                Log.d(PERF_TAG, "📊 Trace[$traceId] START: $tag")
                Log.d(PERF_TAG, "   ⏰ Time: ${timeFormatter.format(Date(startTime))}")
                Log.d(PERF_TAG, "   🧵 Thread: ${currentThread.name} (ID: ${currentThread.id})")
                Log.d(PERF_TAG, "   💾 Memory: ${formatMemory(startMemory)}")
                
                // Use regular section for same-thread operations
                Trace.beginSection(fullTag)
            }
        } catch (e: Exception) {
            Log.e(ERROR_TAG, "❌ Error starting trace: $tag", e)
        }
    }

    fun stopTrace() {
        try {
            Log.d(TAG, "⏹️ Stopping trace (no tag)")
            Trace.endSection()
        } catch (e: Exception) {
            Log.e(ERROR_TAG, "❌ Error ending trace", e)
        }
    }

    fun stopTrace(tag: String) {
        try {
            synchronized(traceLock) {
                // Check if trace is active
                if (!activeTraces.containsKey(tag)) {
                    Log.d(TAG, "⚠️ Trace not active, skipping stop: $tag")
                    return
                }
                
                val endTime = System.currentTimeMillis()
                val endMemory = getMemoryUsage()
                
                // Remove from active traces
                activeTraces.remove(tag)
                
                val metrics = traceMetrics[tag]
                if (metrics != null) {
                    metrics.endTime = endTime
                    metrics.endMemory = endMemory
                    metrics.duration = endTime - metrics.startTime
                    metrics.memoryDelta = endMemory - metrics.startMemory
                    
                    Log.d(TAG, "⏹️ Stopping trace: $tag")
                    Log.d(PERF_TAG, "📊 Trace COMPLETE: $tag")
                    Log.d(PERF_TAG, "   ⏱️ Duration: ${metrics.duration}ms")
                    Log.d(PERF_TAG, "   💾 Memory Delta: ${formatMemoryDelta(metrics.memoryDelta)}")
                    Log.d(PERF_TAG, "   🧵 Thread: ${metrics.threadName}")
                    
                    if (metrics.duration > 100) {
                        Log.w(PERF_TAG, "⚠️ SLOW OPERATION: $tag took ${metrics.duration}ms")
                    }
                    
                    if (metrics.memoryDelta > 1024 * 1024) { // 1MB
                        Log.w(PERF_TAG, "⚠️ HIGH MEMORY USAGE: $tag used ${formatMemory(metrics.memoryDelta)}")
                    }
                }
                
                Trace.endSection()
            }
        } catch (e: Exception) {
            Log.e(ERROR_TAG, "❌ Error ending trace: $tag", e)
        }
    }

    // Use this for operations that might switch threads (like coroutines, async operations)
    fun startAsyncTrace(tag: String, attributes: Map<String, String>? = null): Int {
        return try {
            synchronized(traceLock) {
                val currentTime = System.currentTimeMillis()
                val lastTime = lastTraceTime[tag] ?: 0
                
                // Check for rapid duplicates within threshold
                if (currentTime - lastTime < DUPLICATE_THRESHOLD_MS) {
                    Log.d(TAG, "⚠️ Duplicate async trace within ${DUPLICATE_THRESHOLD_MS}ms, skipping: $tag")
                    return -1
                }
                
                // Check if async trace is already active
                if (asyncTraceCookies.containsKey(tag)) {
                    Log.d(TAG, "⚠️ Async trace already active, skipping: $tag")
                    return asyncTraceCookies[tag] ?: -1
                }
                
                val traceId = traceCounter.incrementAndGet()
                val currentThread = Thread.currentThread()
                val startTime = currentTime
                val startMemory = getMemoryUsage()
                
                // Update last trace time
                lastTraceTime[tag] = currentTime
                
                val enhancedAttributes = buildEnhancedAttributes(attributes, traceId, currentThread)
                
                traceMetrics[tag] = TraceMetrics(
                    startTime = startTime,
                    startMemory = startMemory,
                    threadName = currentThread.name,
                    threadId = currentThread.id
                )
                
                Log.d(TAG, "🔄 Starting ASYNC trace: $tag")
                Log.d(PERF_TAG, "📊 AsyncTrace[$traceId] START: $tag")
                Log.d(PERF_TAG, "   ⏰ Time: ${timeFormatter.format(Date(startTime))}")
                Log.d(PERF_TAG, "   🧵 Thread: ${currentThread.name} (ID: ${currentThread.id})")
                Log.d(PERF_TAG, "   💾 Memory: ${formatMemory(startMemory)}")
                Log.d(PERF_TAG, "   🔄 Type: ASYNC (Thread-switching capable)")
                
                beginAsyncSection(tag, enhancedAttributes)
            }
        } catch (e: Exception) {
            Log.e(ERROR_TAG, "❌ Error starting async trace: $tag", e)
            -1
        }
    }

    fun stopAsyncTrace(tag: String) {
        try {
            val endTime = System.currentTimeMillis()
            val endMemory = getMemoryUsage()
            val currentThread = Thread.currentThread()
            
            val metrics = traceMetrics[tag]
            if (metrics != null) {
                metrics.endTime = endTime
                metrics.endMemory = endMemory
                metrics.duration = endTime - metrics.startTime
                metrics.memoryDelta = endMemory - metrics.startMemory
                
                Log.d(TAG, "⏹️ Stopping ASYNC trace: $tag")
                Log.d(PERF_TAG, "📊 AsyncTrace COMPLETE: $tag")
                Log.d(PERF_TAG, "   ⏱️ Duration: ${metrics.duration}ms")
                Log.d(PERF_TAG, "   💾 Memory Delta: ${formatMemoryDelta(metrics.memoryDelta)}")
                Log.d(PERF_TAG, "   🧵 Start Thread: ${metrics.threadName}")
                Log.d(PERF_TAG, "   🧵 End Thread: ${currentThread.name}")
                
                if (metrics.threadName != currentThread.name) {
                    Log.i(PERF_TAG, "🔄 THREAD SWITCH DETECTED: ${metrics.threadName} → ${currentThread.name}")
                }
                
                if (metrics.duration > 100) {
                    Log.w(PERF_TAG, "⚠️ SLOW ASYNC OPERATION: $tag took ${metrics.duration}ms")
                }
                
                if (metrics.memoryDelta > 1024 * 1024) { // 1MB
                    Log.w(PERF_TAG, "⚠️ HIGH MEMORY USAGE: $tag used ${formatMemory(metrics.memoryDelta)}")
                }
            }
            
            endAsyncSection(tag)
        } catch (e: Exception) {
            Log.e(ERROR_TAG, "❌ Error ending async trace: $tag", e)
        }
    }

    fun traceStateChange(
        context: String,
        fromState: String?,
        toState: String,
        additionalData: Map<String, String> = emptyMap()
    ) {
        try {
            val timestamp = System.currentTimeMillis()
            val stateChangeInfo = StateChangeInfo(
                timestamp = timestamp,
                fromState = fromState,
                toState = toState,
                context = context,
                additionalData = additionalData
            )
            
            stateChanges["${context}_${timestamp}"] = stateChangeInfo
            
            Log.i(STATE_TAG, "🔄 STATE CHANGE: $context")
            Log.i(STATE_TAG, "   📍 From: ${fromState ?: "null"} → To: $toState")
            Log.i(STATE_TAG, "   ⏰ Time: ${timeFormatter.format(Date(timestamp))}")
            
            if (additionalData.isNotEmpty()) {
                Log.i(STATE_TAG, "   📋 Data: $additionalData")
            }
            
            val traceTag = "StateChange_${context}_${toState}"
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                Trace.beginAsyncSection(traceTag, timestamp.toInt())
                Trace.endAsyncSection(traceTag, timestamp.toInt())
            }
            
        } catch (e: Exception) {
            Log.e(ERROR_TAG, "❌ Error tracing state change: $context", e)
        }
    }

    fun traceUserAction(
        action: String,
        component: String,
        additionalData: Map<String, String> = emptyMap()
    ) {
        try {
            val timestamp = System.currentTimeMillis()
            val currentThread = Thread.currentThread()
            
            Log.i(TAG, "👆 USER ACTION: $action")
            Log.i(TAG, "   🎯 Component: $component")
            Log.i(TAG, "   ⏰ Time: ${timeFormatter.format(Date(timestamp))}")
            Log.i(TAG, "   🧵 Thread: ${currentThread.name}")
            
            if (additionalData.isNotEmpty()) {
                Log.i(TAG, "   📋 Data: $additionalData")
            }
            
            val traceTag = "UserAction_${action}_${component}"
            val enhancedData = additionalData + mapOf(
                "timestamp" to timestamp.toString(),
                "thread" to currentThread.name,
                "component" to component
            )
            
            startAsyncTrace(traceTag, enhancedData)
            stopAsyncTrace(traceTag)
            
        } catch (e: Exception) {
            Log.e(ERROR_TAG, "❌ Error tracing user action: $action", e)
        }
    }

    fun traceError(
        error: Throwable,
        context: String,
        additionalData: Map<String, String> = emptyMap()
    ) {
        try {
            val timestamp = System.currentTimeMillis()
            val currentThread = Thread.currentThread()
            
            Log.e(ERROR_TAG, "💥 ERROR TRACED: $context")
            Log.e(ERROR_TAG, "   ⏰ Time: ${timeFormatter.format(Date(timestamp))}")
            Log.e(ERROR_TAG, "   🧵 Thread: ${currentThread.name}")
            Log.e(ERROR_TAG, "   💥 Error: ${error.message}")
            Log.e(ERROR_TAG, "   📍 Stack: ${error.stackTraceToString()}")
            
            if (additionalData.isNotEmpty()) {
                Log.e(ERROR_TAG, "   📋 Data: $additionalData")
            }
            
            val traceTag = "Error_${context}_${error.javaClass.simpleName}"
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                Trace.beginAsyncSection(traceTag, timestamp.toInt())
                Trace.endAsyncSection(traceTag, timestamp.toInt())
            }
            
        } catch (e: Exception) {
            Log.e(ERROR_TAG, "❌ Error tracing error: $context", e)
        }
    }

    fun beginAsyncSection(tag: String, attributes: Map<String, String>? = null): Int {
        return try {
            synchronized(traceLock) {
                // Check if already exists to prevent duplicates
                if (asyncTraceCookies.containsKey(tag)) {
                    Log.w(TAG, "⚠️ Async trace already active: $tag")
                    return asyncTraceCookies[tag] ?: -1
                }
                
                val cookie = generateCookie(tag)
                val fullTag = buildTag(tag, attributes)
                Log.d(TAG, "🔄 Beginning async section: $fullTag with cookie: $cookie")
                
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    Trace.beginAsyncSection(fullTag, cookie)
                }
                asyncTraceCookies[tag] = cookie
                asyncTraceFullTags[tag] = fullTag
                Log.d(TAG, "✅ Async section started successfully: $fullTag")
                Log.d(TAG, "🔑 Stored cookie for '$tag': $cookie")
                cookie
            }
        } catch (e: Exception) {
            Log.e(ERROR_TAG, "❌ Error beginning async trace: $tag", e)
            -1
        }
    }

    fun endAsyncSection(tag: String) {
        try {
            synchronized(traceLock) {
                val cookie = asyncTraceCookies.remove(tag)
                val fullTag = asyncTraceFullTags.remove(tag)
                
                Log.d(TAG, "🔍 Attempting to end async section: $tag")
                Log.d(TAG, "🔑 Retrieved cookie for '$tag': $cookie")
                Log.d(TAG, "🏷️ Retrieved fullTag for '$tag': $fullTag")
                
                if (cookie != null && fullTag != null) {
                    Log.d(TAG, "⏹️ Ending async section: $tag with cookie: $cookie")
                    Log.d(TAG, "🔄 Using stored full tag: $fullTag")
                    
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        Trace.endAsyncSection(fullTag, cookie)
                    }
                    
                    Log.d(TAG, "✅ Async section ended successfully: $fullTag")
                    Log.d(TAG, "🎯 Perfetto should now show proper duration for: $tag")
                } else {
                    Log.w(TAG, "⚠️ Missing data for async trace: $tag")
                    Log.w(TAG, "   🔑 Cookie: $cookie")
                    Log.w(TAG, "   🏷️ FullTag: $fullTag")
                    Log.w(TAG, "   📊 Active async traces: ${asyncTraceCookies.keys}")
                }
            }
        } catch (e: Exception) {
            Log.e(ERROR_TAG, "❌ Error ending async trace: $tag", e)
        }
    }

    inline fun <T> trace(
        tag: String,
        attributes: Map<String, String>? = null,
        block: () -> T
    ): T {
        startAsyncTrace(tag, attributes)
        return try {
            block()
        } catch (e: Exception) {
            traceError(e, tag, attributes ?: emptyMap())
            throw e
        } finally {
            stopAsyncTrace(tag)
        }
    }

    suspend inline fun <T> traceSuspend(
        tag: String,
        attributes: Map<String, String>? = null,
        crossinline block: suspend () -> T
    ): T {
        startAsyncTrace(tag, attributes)
        return try {
            block()
        } catch (e: Exception) {
            traceError(e, tag, attributes ?: emptyMap())
            throw e
        } finally {
            stopAsyncTrace(tag)
        }
    }

    fun getPerformanceReport(): String {
        val report = StringBuilder()
        report.appendLine("📊 PERFORMANCE REPORT")
        report.appendLine("⏰ Generated: ${timeFormatter.format(Date())}")
        report.appendLine("🔢 Total Traces: ${traceMetrics.size}")
        report.appendLine("🔄 Active Async: ${asyncTraceCookies.size}")
        report.appendLine("🔄 State Changes: ${stateChanges.size}")
        report.appendLine()
        
        val sortedMetrics = traceMetrics.values.sortedByDescending { it.duration }
        report.appendLine("🐌 SLOWEST OPERATIONS:")
        sortedMetrics.take(10).forEach { metric ->
            report.appendLine("   ${metric.duration}ms - Thread: ${metric.threadName}")
        }
        
        return report.toString()
    }

    private fun buildEnhancedAttributes(
        attributes: Map<String, String>?,
        traceId: Long,
        thread: Thread
    ): Map<String, String> {
        val enhanced = mutableMapOf<String, String>()
        attributes?.let { enhanced.putAll(it) }
        
        enhanced["trace_id"] = traceId.toString()
        enhanced["thread_name"] = thread.name
        enhanced["thread_id"] = thread.id.toString()
        enhanced["timestamp"] = System.currentTimeMillis().toString()
        enhanced["memory_mb"] = (getMemoryUsage() / (1024 * 1024)).toString()
        
        return enhanced
    }

    private fun getMemoryUsage(): Long {
        return try {
            val memInfo = Debug.MemoryInfo()
            Debug.getMemoryInfo(memInfo)
            memInfo.totalPss * 1024L // Convert KB to bytes
        } catch (e: Exception) {
            Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()
        }
    }

    private fun formatMemory(bytes: Long): String {
        return when {
            bytes >= 1024 * 1024 -> "${bytes / (1024 * 1024)}MB"
            bytes >= 1024 -> "${bytes / 1024}KB"
            else -> "${bytes}B"
        }
    }

    private fun formatMemoryDelta(delta: Long): String {
        val sign = if (delta >= 0) "+" else ""
        return "$sign${formatMemory(kotlin.math.abs(delta))}"
    }

    private fun generateCookie(tag: String): Int {
        return tag.hashCode() xor System.nanoTime().toInt()
    }

    private fun buildTag(tag: String, attributes: Map<String, String>?): String {
        if (attributes.isNullOrEmpty()) return tag
        
        // Android Trace.beginSection() has a limit of ~127 characters
        val maxLength = 120
        val baseTag = tag
        
        if (baseTag.length >= maxLength) {
            return baseTag.take(maxLength)
        }
        
        val attrString = attributes.entries.joinToString(", ") { "${it.key}=${it.value}" }
        val fullTag = "$baseTag [$attrString]"
        
        return if (fullTag.length <= maxLength) {
            fullTag
        } else {
            // If too long, just use the base tag
            baseTag
        }
    }

    fun clearAsyncTraces() {
        synchronized(traceLock) {
            asyncTraceCookies.clear()
            asyncTraceFullTags.clear()
        }
    }

    fun getActiveAsyncTraceCount(): Int {
        return asyncTraceCookies.size
    }

    fun clearMetrics() {
        synchronized(traceLock) {
            traceMetrics.clear()
            stateChanges.clear()
            activeTraces.clear()
            lastTraceTime.clear()
            Log.i(TAG, "🧹 Metrics cleared")
        }
    }
}
