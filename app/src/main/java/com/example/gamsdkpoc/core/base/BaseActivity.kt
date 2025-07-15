package com.example.gamsdkpoc.core.base

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import com.example.gamsdkpoc.core.tracing.AppTracer

/**
 * Base activity class with integrated performance tracing.
 * 
 * Features:
 * - Automatic lifecycle tracing for performance monitoring
 * - Compose integration with tracing support
 * - Consistent activity setup across the app
 * - Error handling for tracing operations
 */
abstract class BaseActivity : ComponentActivity() {

    private val activityName = this::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        AppTracer.startTrace("${activityName}_onCreate", mapOf(
            "activity" to activityName,
            "lifecycle" to "onCreate"
        ))
        
        super.onCreate(savedInstanceState)
        
        AppTracer.stopTrace()
    }

    override fun onStart() {
        AppTracer.startTrace("${activityName}_onStart", mapOf(
            "activity" to activityName,
            "lifecycle" to "onStart"
        ))
        
        super.onStart()
        
        AppTracer.stopTrace()
    }

    override fun onResume() {
        AppTracer.startTrace("${activityName}_onResume", mapOf(
            "activity" to activityName,
            "lifecycle" to "onResume"
        ))
        
        super.onResume()
        
        AppTracer.stopTrace()
    }

    override fun onPause() {
        AppTracer.startTrace("${activityName}_onPause", mapOf(
            "activity" to activityName,
            "lifecycle" to "onPause"
        ))
        
        super.onPause()
        
        AppTracer.stopTrace()
    }

    override fun onStop() {
        AppTracer.startTrace("${activityName}_onStop", mapOf(
            "activity" to activityName,
            "lifecycle" to "onStop"
        ))
        
        super.onStop()
        
        AppTracer.stopTrace()
    }

    override fun onDestroy() {
        AppTracer.startTrace("${activityName}_onDestroy", mapOf(
            "activity" to activityName,
            "lifecycle" to "onDestroy"
        ))
        
        super.onDestroy()
        
        AppTracer.stopTrace()
    }

    /**
     * Set Compose content with tracing support.
     * Automatically traces the composition process.
     * 
     * @param content The Composable content to set
     */
    protected fun setTracedContent(content: @Composable () -> Unit) {
        AppTracer.startTrace("${activityName}_setContent", mapOf(
            "activity" to activityName,
            "operation" to "compose_setup"
        ))
        
        setContent {
            AppTracer.startTrace("${activityName}_Compose", mapOf(
                "activity" to activityName,
                "operation" to "compose_render"
            ))
            
            content()
            
            AppTracer.stopTrace()
        }
        
        AppTracer.stopTrace()
    }
}
