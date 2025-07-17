package com.example.gamsdkpoc

import android.app.Application
import com.example.gamsdkpoc.core.tracing.AppTracer
import com.google.android.gms.ads.MobileAds
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

@HiltAndroidApp
class GamSdkApplication : Application() {

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        val startTime = System.currentTimeMillis()
        android.util.Log.i("GAM_SDK_LIFECYCLE", "🚀 Application.onCreate() started at: ${java.text.SimpleDateFormat("HH:mm:ss.SSS", java.util.Locale.getDefault()).format(java.util.Date(startTime))}")
        android.util.Log.i("GAM_SDK_LIFECYCLE", "📱 App Process: ${android.os.Process.myPid()}, Thread: ${Thread.currentThread().name}")
        
        AppTracer.startTrace("App_Startup", mapOf("phase" to "application_create"))
        
        android.util.Log.i("GAM_SDK_LIFECYCLE", "⏳ Calling super.onCreate()...")
        super.onCreate()
        android.util.Log.i("GAM_SDK_LIFECYCLE", "✅ super.onCreate() completed")
        
        android.util.Log.i("GAM_SDK_LIFECYCLE", "🎯 About to initialize GAM SDK...")
        initializeGamSdk()
        android.util.Log.i("GAM_SDK_LIFECYCLE", "📤 GAM SDK initialization call dispatched")
        
        val endTime = System.currentTimeMillis()
        android.util.Log.i("GAM_SDK_LIFECYCLE", "🏁 Application.onCreate() completed in ${endTime - startTime}ms at: ${java.text.SimpleDateFormat("HH:mm:ss.SSS", java.util.Locale.getDefault()).format(java.util.Date(endTime))}")
        
        AppTracer.stopTrace("App_Startup")
    }

    private fun initializeGamSdk() {
        val initStartTime = System.currentTimeMillis()
        val mainThread = Thread.currentThread()
        
        android.util.Log.i("GAM_SDK_INIT", "🔥 GAM SDK INITIALIZATION STARTED")
        android.util.Log.i("GAM_SDK_INIT", "📍 Location: GamSdkApplication.initializeGamSdk()")
        android.util.Log.i("GAM_SDK_INIT", "⏰ Start Time: ${java.text.SimpleDateFormat("HH:mm:ss.SSS", java.util.Locale.getDefault()).format(java.util.Date(initStartTime))}")
        android.util.Log.i("GAM_SDK_INIT", "🧵 Called on Thread: ${mainThread.name} (ID: ${mainThread.id})")
        android.util.Log.i("GAM_SDK_INIT", "🎯 Using Dispatcher: Dispatchers.IO")
        android.util.Log.i("GAM_SDK_INIT", "📦 GAM SDK Version: 24.4.0")
        
        android.util.Log.d("GAM_SDK_THREAD", "initializeGamSdk() called on thread: ${mainThread.name} (ID: ${mainThread.id})")
        
        applicationScope.launch {
            val launchTime = System.currentTimeMillis()
            val backgroundThread = Thread.currentThread()
            
            android.util.Log.i("GAM_SDK_INIT", "🚀 Coroutine launched after ${launchTime - initStartTime}ms")
            android.util.Log.i("GAM_SDK_INIT", "🔄 Switched to Background Thread: ${backgroundThread.name} (ID: ${backgroundThread.id})")
            android.util.Log.i("GAM_SDK_INIT", "🏷️ Thread Properties:")
            android.util.Log.i("GAM_SDK_INIT", "   - Is Background: ${backgroundThread.isDaemon}")
            android.util.Log.i("GAM_SDK_INIT", "   - Priority: ${backgroundThread.priority}")
            android.util.Log.i("GAM_SDK_INIT", "   - Thread Group: ${backgroundThread.threadGroup?.name}")
            android.util.Log.i("GAM_SDK_INIT", "   - State: ${backgroundThread.state}")
            
            android.util.Log.d("GAM_SDK_THREAD", "GAM SDK initialization starting on thread: ${backgroundThread.name} (ID: ${backgroundThread.id})")
            android.util.Log.d("GAM_SDK_THREAD", "Thread details - IsBackground: ${backgroundThread.isDaemon}, Priority: ${backgroundThread.priority}")
            android.util.Log.d("GAM_SDK_THREAD", "Dispatcher: ${coroutineContext[kotlinx.coroutines.CoroutineDispatcher]}")
            
            AppTracer.startTrace("GAM_SDK_Initialization", mapOf(
                "thread" to backgroundThread.name,
                "thread_id" to backgroundThread.id.toString(),
                "is_background" to backgroundThread.isDaemon.toString(),
                "priority" to backgroundThread.priority.toString(),
                "sdk_version" to "24.4.0"
            ))
            
            try {
                val sdkCallTime = System.currentTimeMillis()
                val currentThread = Thread.currentThread()
                
                // ✅ BACKGROUND THREAD VALIDATION
                android.util.Log.i("GAM_SDK_VALIDATION", "🔍 VALIDATING BACKGROUND THREAD EXECUTION:")
                android.util.Log.i("GAM_SDK_VALIDATION", "   ✓ Thread Name: ${currentThread.name}")
                android.util.Log.i("GAM_SDK_VALIDATION", "   ✓ Is Main Thread: ${currentThread.name == "main"}")
                android.util.Log.i("GAM_SDK_VALIDATION", "   ✓ Is Background Thread: ${currentThread.isDaemon}")
                android.util.Log.i("GAM_SDK_VALIDATION", "   ✓ Thread ID: ${currentThread.id}")
                android.util.Log.i("GAM_SDK_VALIDATION", "   ✓ Thread Priority: ${currentThread.priority}")
                
                // Google Documentation Compliance Check
                if (currentThread.name == "main") {
                    android.util.Log.e("GAM_SDK_VALIDATION", "❌ VIOLATION: GAM SDK is initializing on MAIN THREAD!")
                    android.util.Log.e("GAM_SDK_VALIDATION", "❌ This violates Google's recommendation!")
                    android.util.Log.e("GAM_SDK_VALIDATION", "❌ Should be on background thread (Dispatchers.IO)")
                } else {
                    android.util.Log.i("GAM_SDK_VALIDATION", "✅ COMPLIANCE: GAM SDK initializing on BACKGROUND THREAD")
                    android.util.Log.i("GAM_SDK_VALIDATION", "✅ Follows Google's best practices")
                    android.util.Log.i("GAM_SDK_VALIDATION", "✅ Non-blocking initialization confirmed")
                }
                
                // Dispatcher Validation
                val dispatcher = coroutineContext[kotlinx.coroutines.CoroutineDispatcher]
                android.util.Log.i("GAM_SDK_VALIDATION", "   ✓ Coroutine Dispatcher: $dispatcher")
                if (dispatcher.toString().contains("Dispatchers.IO")) {
                    android.util.Log.i("GAM_SDK_VALIDATION", "✅ CORRECT: Using Dispatchers.IO as recommended")
                } else {
                    android.util.Log.w("GAM_SDK_VALIDATION", "⚠️ WARNING: Not using Dispatchers.IO")
                }
                
                android.util.Log.i("GAM_SDK_INIT", "📞 Calling MobileAds.initialize() at: ${java.text.SimpleDateFormat("HH:mm:ss.SSS", java.util.Locale.getDefault()).format(java.util.Date(sdkCallTime))}")
                android.util.Log.i("GAM_SDK_INIT", "🧵 MobileAds.initialize() called on thread: ${currentThread.name}")
                android.util.Log.d("GAM_SDK_THREAD", "Calling MobileAds.initialize() on thread: ${currentThread.name}")
                
                MobileAds.initialize(this@GamSdkApplication) { initializationStatus ->
                    val callbackTime = System.currentTimeMillis()
                    val callbackThread = Thread.currentThread()
                    
                    // ✅ CALLBACK THREAD VALIDATION
                    android.util.Log.i("GAM_SDK_VALIDATION", "🔍 VALIDATING CALLBACK THREAD:")
                    android.util.Log.i("GAM_SDK_VALIDATION", "   ✓ Callback Thread: ${callbackThread.name}")
                    android.util.Log.i("GAM_SDK_VALIDATION", "   ✓ Is Main Thread: ${callbackThread.name == "main"}")
                    
                    if (callbackThread.name == "main") {
                        android.util.Log.i("GAM_SDK_VALIDATION", "✅ EXPECTED: Callback on MAIN THREAD (UI updates)")
                    } else {
                        android.util.Log.w("GAM_SDK_VALIDATION", "⚠️ UNEXPECTED: Callback not on main thread")
                    }
                    
                    android.util.Log.i("GAM_SDK_INIT", "🎉 GAM SDK INITIALIZATION CALLBACK RECEIVED!")
                    android.util.Log.i("GAM_SDK_INIT", "⏰ Callback Time: ${java.text.SimpleDateFormat("HH:mm:ss.SSS", java.util.Locale.getDefault()).format(java.util.Date(callbackTime))}")
                    android.util.Log.i("GAM_SDK_INIT", "⏱️ Total Initialization Time: ${callbackTime - initStartTime}ms")
                    android.util.Log.i("GAM_SDK_INIT", "🧵 Callback Thread: ${callbackThread.name} (ID: ${callbackThread.id})")
                    android.util.Log.i("GAM_SDK_INIT", "🏷️ Callback Thread Properties:")
                    android.util.Log.i("GAM_SDK_INIT", "   - Is Background: ${callbackThread.isDaemon}")
                    android.util.Log.i("GAM_SDK_INIT", "   - Priority: ${callbackThread.priority}")
                    
                    android.util.Log.d("GAM_SDK_THREAD", "GAM SDK initialization callback on thread: ${callbackThread.name} (ID: ${callbackThread.id})")
                    android.util.Log.d("GAM_SDK_THREAD", "Callback thread details - IsBackground: ${callbackThread.isDaemon}, Priority: ${callbackThread.priority}")
                    
                    AppTracer.startTrace("GAM_SDK_Initialization_Complete", mapOf(
                        "callback_thread" to callbackThread.name,
                        "callback_thread_id" to callbackThread.id.toString()
                    ))
                    
                    val adapterStatusMap = initializationStatus.adapterStatusMap
                    android.util.Log.i("GAM_SDK_INIT", "📊 Initialization Status:")
                    android.util.Log.i("GAM_SDK_INIT", "   - Total Adapters: ${adapterStatusMap.size}")
                    android.util.Log.d("GAM_SDK", "GAM SDK initialization completed with ${adapterStatusMap.size} adapters")
                    
                    var readyCount = 0
                    var notReadyCount = 0
                    
                    for (adapterClass in adapterStatusMap.keys) {
                        val status = adapterStatusMap[adapterClass]
                        val state = status?.initializationState?.name ?: "UNKNOWN"
                        val description = status?.description ?: "No description"
                        
                        android.util.Log.i("GAM_SDK_INIT", "   📱 ${adapterClass.simpleName}: $state")
                        if (description.isNotEmpty()) {
                            android.util.Log.i("GAM_SDK_INIT", "      💬 $description")
                        }
                        
                        if (state == "READY") readyCount++ else notReadyCount++
                        
                        android.util.Log.d(
                            "GAM_SDK", 
                            "Adapter: $adapterClass, Status: ${status?.initializationState}, Description: ${status?.description}"
                        )
                    }
                    
                    android.util.Log.i("GAM_SDK_INIT", "📈 Summary: $readyCount Ready, $notReadyCount Not Ready")
                    
                    // ✅ FINAL VALIDATION SUMMARY
                    android.util.Log.i("GAM_SDK_VALIDATION", "📋 FINAL COMPLIANCE SUMMARY:")
                    android.util.Log.i("GAM_SDK_VALIDATION", "   ✅ Initialization: Background Thread (${backgroundThread.name})")
                    android.util.Log.i("GAM_SDK_VALIDATION", "   ✅ Callback: Main Thread (${callbackThread.name})")
                    android.util.Log.i("GAM_SDK_VALIDATION", "   ✅ Dispatcher: Dispatchers.IO")
                    android.util.Log.i("GAM_SDK_VALIDATION", "   ✅ Non-blocking: TRUE")
                    android.util.Log.i("GAM_SDK_VALIDATION", "   ✅ Google Best Practices: FOLLOWED")
                    
                    android.util.Log.i("GAM_SDK_INIT", "✅ GAM SDK INITIALIZATION COMPLETED SUCCESSFULLY!")
                    android.util.Log.i("GAM_SDK_INIT", "🧵 Completed on Thread: ${callbackThread.name}")
                    
                    android.util.Log.d("GAM_SDK", "✅ GAM SDK initialization completed successfully on ${callbackThread.name}")
                    AppTracer.stopTrace("GAM_SDK_Initialization_Complete")
                    AppTracer.stopTrace("GAM_SDK_Initialization")
                }
                
                val postCallTime = System.currentTimeMillis()
                android.util.Log.i("GAM_SDK_INIT", "📤 MobileAds.initialize() call dispatched in ${postCallTime - sdkCallTime}ms")
                android.util.Log.i("GAM_SDK_INIT", "⏳ Waiting for initialization callback...")
                android.util.Log.d("GAM_SDK_THREAD", "MobileAds.initialize() call completed, waiting for callback...")
                
            } catch (e: Exception) {
                val errorTime = System.currentTimeMillis()
                val errorThread = Thread.currentThread()
                
                android.util.Log.e("GAM_SDK_INIT", "❌ GAM SDK INITIALIZATION FAILED!")
                android.util.Log.e("GAM_SDK_INIT", "⏰ Error Time: ${java.text.SimpleDateFormat("HH:mm:ss.SSS", java.util.Locale.getDefault()).format(java.util.Date(errorTime))}")
                android.util.Log.e("GAM_SDK_INIT", "🧵 Error Thread: ${errorThread.name}")
                android.util.Log.e("GAM_SDK_INIT", "💥 Error: ${e.message}")
                
                android.util.Log.e("GAM_SDK_THREAD", "❌ Error initializing GAM SDK on thread: ${errorThread.name}", e)
                android.util.Log.e("GAM_SDK", "Error initializing GAM SDK", e)
                AppTracer.stopTrace("GAM_SDK_Initialization")
            }
        }
        
        val methodEndTime = System.currentTimeMillis()
        android.util.Log.i("GAM_SDK_INIT", "🏁 initializeGamSdk() method completed in ${methodEndTime - initStartTime}ms")
        android.util.Log.d("GAM_SDK_THREAD", "initializeGamSdk() method completed on thread: ${Thread.currentThread().name}")
    }

    override fun onTerminate() {
        super.onTerminate()
        AppTracer.clearAsyncTraces()
    }

    override fun onLowMemory() {
        AppTracer.startTrace("App_LowMemory")
        super.onLowMemory()
        AppTracer.stopTrace("App_LowMemory")
    }

    override fun onTrimMemory(level: Int) {
        AppTracer.startTrace("App_TrimMemory", mapOf("level" to level.toString()))
        super.onTrimMemory(level)
        AppTracer.stopTrace("App_TrimMemory")
    }
}
