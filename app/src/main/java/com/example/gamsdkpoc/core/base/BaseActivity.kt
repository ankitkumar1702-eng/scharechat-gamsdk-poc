package com.example.gamsdkpoc.core.base

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import com.example.gamsdkpoc.core.tracing.AppTracer

abstract class BaseActivity : ComponentActivity() {

    private val activityName = this::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        AppTracer.startAsyncTrace("${activityName}_onCreate", mapOf(
            "activity" to activityName,
            "lifecycle" to "onCreate"
        ))
        
        super.onCreate(savedInstanceState)
        
        AppTracer.stopAsyncTrace("${activityName}_onCreate")
    }

    override fun onStart() {
        AppTracer.startAsyncTrace("${activityName}_onStart", mapOf(
            "activity" to activityName,
            "lifecycle" to "onStart"
        ))
        
        super.onStart()
        
        AppTracer.stopAsyncTrace("${activityName}_onStart")
    }

    override fun onResume() {
        AppTracer.startAsyncTrace("${activityName}_onResume", mapOf(
            "activity" to activityName,
            "lifecycle" to "onResume"
        ))
        
        super.onResume()
        
        AppTracer.stopAsyncTrace("${activityName}_onResume")
    }

    override fun onPause() {
        AppTracer.startAsyncTrace("${activityName}_onPause", mapOf(
            "activity" to activityName,
            "lifecycle" to "onPause"
        ))
        
        super.onPause()
        
        AppTracer.stopAsyncTrace("${activityName}_onPause")
    }

    override fun onStop() {
        AppTracer.startAsyncTrace("${activityName}_onStop", mapOf(
            "activity" to activityName,
            "lifecycle" to "onStop"
        ))
        
        super.onStop()
        
        AppTracer.stopAsyncTrace("${activityName}_onStop")
    }

    override fun onDestroy() {
        AppTracer.startAsyncTrace("${activityName}_onDestroy", mapOf(
            "activity" to activityName,
            "lifecycle" to "onDestroy"
        ))
        
        super.onDestroy()
        
        AppTracer.stopAsyncTrace("${activityName}_onDestroy")
    }

    protected fun setTracedContent(content: @Composable () -> Unit) {
        AppTracer.startAsyncTrace("${activityName}_setContent", mapOf(
            "activity" to activityName,
            "operation" to "compose_setup"
        ))
        
        setContent {
            content()
        }
        
        AppTracer.stopAsyncTrace("${activityName}_setContent")
    }
}
