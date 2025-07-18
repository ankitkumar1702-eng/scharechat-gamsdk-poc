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
        
        AppTracer.startAsyncTrace("App_Startup", mapOf("phase" to "application_create"))
        
        android.util.Log.i("GAM_SDK_LIFECYCLE", "🔧 Initializing Enhanced AppTracer...")
        AppTracer.initialize(this)
        AppTracer.traceStateChange("Application", null, "CREATING", mapOf(
            "process_id" to android.os.Process.myPid().toString(),
            "thread" to Thread.currentThread().name
        ))
        
        android.util.Log.i("GAM_SDK_LIFECYCLE", "⏳ Calling super.onCreate()...")
        super.onCreate()
        android.util.Log.i("GAM_SDK_LIFECYCLE", "✅ super.onCreate() completed")
        
        AppTracer.traceStateChange("Application", "CREATING", "CREATED", mapOf(
            "startup_time" to "${System.currentTimeMillis() - startTime}ms"
        ))
        
        android.util.Log.i("GAM_SDK_LIFECYCLE", "ℹ️ GAM SDK initialization moved to MainActivity for better performance")
        
        val endTime = System.currentTimeMillis()
        android.util.Log.i("GAM_SDK_LIFECYCLE", "🏁 Application.onCreate() completed in ${endTime - startTime}ms at: ${java.text.SimpleDateFormat("HH:mm:ss.SSS", java.util.Locale.getDefault()).format(java.util.Date(endTime))}")
        
        AppTracer.stopAsyncTrace("App_Startup")
    }

    // GAM SDK initialization moved to MainActivity for better performance
    // See GAM_SDK_ANALYSIS_AND_LEARNINGS.md for detailed analysis

    override fun onTerminate() {
        super.onTerminate()
        AppTracer.clearAsyncTraces()
    }

    override fun onLowMemory() {
        AppTracer.startAsyncTrace("App_LowMemory")
        super.onLowMemory()
        AppTracer.stopAsyncTrace("App_LowMemory")
    }

    override fun onTrimMemory(level: Int) {
        AppTracer.startAsyncTrace("App_TrimMemory", mapOf("level" to level.toString()))
        super.onTrimMemory(level)
        AppTracer.stopAsyncTrace("App_TrimMemory")
    }
}
