package com.example.gamsdkpoc

import android.app.Application
import com.example.gamsdkpoc.core.tracing.AppTracer
import com.google.android.gms.ads.MobileAds
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * Application class with GAM SDK initialization and clean architecture setup.
 * 
 * Features:
 * - Non-blocking GAM SDK initialization on background thread
 * - Perfetto tracing integration for startup performance monitoring
 * - Hilt dependency injection setup
 * - Proper error handling and logging
 */
@HiltAndroidApp
class GamSdkApplication : Application() {

    // Application-scoped coroutine scope for background operations
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    override fun onCreate() {
        AppTracer.startTrace("App_Startup", mapOf("phase" to "application_create"))
        
        super.onCreate()
        
        // Initialize GAM SDK on background thread with tracing
        initializeGamSdk()
        
        AppTracer.stopTrace()
    }

    /**
     * Initialize Google Mobile Ads SDK on a background thread.
     * This prevents blocking the main thread during app startup.
     */
    private fun initializeGamSdk() {
        applicationScope.launch {
            AppTracer.startTrace("GAM_SDK_Initialization", mapOf(
                "thread" to Thread.currentThread().name,
                "sdk_version" to "24.4.0"
            ))
            
            try {
                // Initialize GAM SDK with completion callback
                MobileAds.initialize(this@GamSdkApplication) { initializationStatus ->
                    AppTracer.startTrace("GAM_SDK_Initialization_Complete")
                    
                    // Log initialization status for each adapter
                    val adapterStatusMap = initializationStatus.adapterStatusMap
                    for (adapterClass in adapterStatusMap.keys) {
                        val status = adapterStatusMap[adapterClass]
                        android.util.Log.d(
                            "GAM_SDK", 
                            "Adapter: $adapterClass, Status: ${status?.initializationState}, Description: ${status?.description}"
                        )
                    }
                    
                    android.util.Log.d("GAM_SDK", "GAM SDK initialization completed successfully")
                    AppTracer.stopTrace()
                }
                
            } catch (e: Exception) {
                android.util.Log.e("GAM_SDK", "Error initializing GAM SDK", e)
            } finally {
                AppTracer.stopTrace()
            }
        }
    }

    override fun onTerminate() {
        super.onTerminate()
        // Clean up any pending async traces
        AppTracer.clearAsyncTraces()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        AppTracer.startTrace("App_LowMemory")
        // Handle low memory situations
        AppTracer.stopTrace()
    }

    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        AppTracer.startTrace("App_TrimMemory", mapOf("level" to level.toString()))
        // Handle memory trimming
        AppTracer.stopTrace()
    }
}
