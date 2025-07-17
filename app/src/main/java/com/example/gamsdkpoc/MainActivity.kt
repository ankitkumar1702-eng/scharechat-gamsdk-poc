package com.example.gamsdkpoc

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.gamsdkpoc.core.base.BaseActivity
import com.example.gamsdkpoc.core.tracing.AppTracer
import com.example.gamsdkpoc.data.repository.AdRepositoryImpl
import com.example.gamsdkpoc.domain.model.AdLoadResult
import com.example.gamsdkpoc.domain.model.AdType
import com.example.gamsdkpoc.presentation.viewmodel.MainViewModel
import com.example.gamsdkpoc.presentation.viewmodel.UserAction
import com.example.gamsdkpoc.ui.theme.GamSdkPocTheme
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : BaseActivity() {
    
    @Inject
    lateinit var adRepository: AdRepositoryImpl
    
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        AppTracer.traceStateChange("MainActivity", null, "CREATING", mapOf(
            "saved_state" to (savedInstanceState != null).toString()
        ))
        
        AppTracer.startTrace("MainActivity_SuperOnCreate")
        super.onCreate(savedInstanceState)
        AppTracer.stopTrace("MainActivity_SuperOnCreate")
        
        AppTracer.startTrace("MainActivity_EnableEdgeToEdge")
        enableEdgeToEdge()
        AppTracer.stopTrace("MainActivity_EnableEdgeToEdge")
        
        AppTracer.startTrace("MainActivity_SetActivity")
        adRepository.setCurrentActivity(this)
        AppTracer.stopTrace("MainActivity_SetActivity")
        
        AppTracer.traceStateChange("MainActivity", "CREATING", "INITIALIZING_GAM_SDK")
        
        AppTracer.startTrace("MainActivity_InitializeGamSdk")
        initializeGamSdk()
        AppTracer.stopTrace("MainActivity_InitializeGamSdk")
        
        AppTracer.traceStateChange("MainActivity", "INITIALIZING_GAM_SDK", "SETTING_CONTENT")
        
        AppTracer.startTrace("MainActivity_SetContent")
        setTracedContent {
            GamSdkPocTheme {
                MainScreen(
                    viewModel = viewModel,
                    adRepository = adRepository
                )
            }
        }
        AppTracer.stopTrace("MainActivity_SetContent")
        
        AppTracer.traceStateChange("MainActivity", "SETTING_CONTENT", "CREATED")
    }
    
    override fun onResume() {
        AppTracer.startTrace("MainActivity_SuperOnResume")
        super.onResume()
        AppTracer.stopTrace("MainActivity_SuperOnResume")
        
        AppTracer.startTrace("MainActivity_SetActivityOnResume")
        adRepository.setCurrentActivity(this)
        AppTracer.stopTrace("MainActivity_SetActivityOnResume")
    }
    
    override fun onPause() {
        AppTracer.startTrace("MainActivity_SuperOnPause")
        super.onPause()
        AppTracer.stopTrace("MainActivity_SuperOnPause")
        
        AppTracer.startTrace("MainActivity_ClearActivityOnPause")
        adRepository.setCurrentActivity(null)
        AppTracer.stopTrace("MainActivity_ClearActivityOnPause")
    }
    
    private fun initializeGamSdk() {
        val initStartTime = System.currentTimeMillis()
        val mainThread = Thread.currentThread()
        
        android.util.Log.i("GAM_SDK_INIT", "🔥 GAM SDK INITIALIZATION STARTED (MainActivity)")
        android.util.Log.i("GAM_SDK_INIT", "📍 Location: MainActivity.initializeGamSdk()")
        android.util.Log.i("GAM_SDK_INIT", "⏰ Start Time: ${java.text.SimpleDateFormat("HH:mm:ss.SSS", java.util.Locale.getDefault()).format(java.util.Date(initStartTime))}")
        android.util.Log.i("GAM_SDK_INIT", "🧵 Called on Thread: ${mainThread.name} (ID: ${mainThread.id})")
        android.util.Log.i("GAM_SDK_INIT", "🎯 Using Dispatcher: Dispatchers.IO")
        android.util.Log.i("GAM_SDK_INIT", "📦 GAM SDK Version: 24.4.0")
        android.util.Log.i("GAM_SDK_INIT", "🏗️ Context: Activity Context (Optimized)")
        
        CoroutineScope(Dispatchers.IO).launch {
            val launchTime = System.currentTimeMillis()
            val backgroundThread = Thread.currentThread()
            
            android.util.Log.i("GAM_SDK_INIT", "🚀 Coroutine launched after ${launchTime - initStartTime}ms")
            android.util.Log.i("GAM_SDK_INIT", "🔄 Switched to Background Thread: ${backgroundThread.name} (ID: ${backgroundThread.id})")
            android.util.Log.i("GAM_SDK_INIT", "🏷️ Thread Properties:")
            android.util.Log.i("GAM_SDK_INIT", "   - Is Background: ${backgroundThread.isDaemon}")
            android.util.Log.i("GAM_SDK_INIT", "   - Priority: ${backgroundThread.priority}")
            android.util.Log.i("GAM_SDK_INIT", "   - Thread Group: ${backgroundThread.threadGroup?.name}")
            android.util.Log.i("GAM_SDK_INIT", "   - State: ${backgroundThread.state}")
            
            AppTracer.startAsyncTrace("GAM_SDK_MainActivity_Init", mapOf(
                "context" to "Activity",
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
                android.util.Log.i("GAM_SDK_VALIDATION", "🔍 VALIDATING BACKGROUND THREAD EXECUTION (MainActivity):")
                android.util.Log.i("GAM_SDK_VALIDATION", "   ✓ Thread Name: ${currentThread.name}")
                android.util.Log.i("GAM_SDK_VALIDATION", "   ✓ Is Main Thread: ${currentThread.name == "main"}")
                android.util.Log.i("GAM_SDK_VALIDATION", "   ✓ Is Background Thread: ${currentThread.isDaemon}")
                android.util.Log.i("GAM_SDK_VALIDATION", "   ✓ Thread ID: ${currentThread.id}")
                android.util.Log.i("GAM_SDK_VALIDATION", "   ✓ Thread Priority: ${currentThread.priority}")
                android.util.Log.i("GAM_SDK_VALIDATION", "   ✓ Context Type: Activity (Google Recommended)")
                
                if (currentThread.name == "main") {
                    android.util.Log.e("GAM_SDK_VALIDATION", "❌ VIOLATION: GAM SDK is initializing on MAIN THREAD!")
                } else {
                    android.util.Log.i("GAM_SDK_VALIDATION", "✅ COMPLIANCE: GAM SDK initializing on BACKGROUND THREAD")
                    android.util.Log.i("GAM_SDK_VALIDATION", "✅ Using Activity Context (Google Best Practice)")
                    android.util.Log.i("GAM_SDK_VALIDATION", "✅ Non-blocking initialization confirmed")
                }
                
                android.util.Log.i("GAM_SDK_INIT", "📞 Calling MobileAds.initialize() at: ${java.text.SimpleDateFormat("HH:mm:ss.SSS", java.util.Locale.getDefault()).format(java.util.Date(sdkCallTime))}")
                android.util.Log.i("GAM_SDK_INIT", "🧵 MobileAds.initialize() called on thread: ${currentThread.name}")
                
                AppTracer.startTrace("MobileAds_Initialize_MainActivity", mapOf(
                    "context" to "Activity",
                    "thread" to currentThread.name,
                    "thread_id" to currentThread.id.toString(),
                    "sdk_version" to "24.4.0"
                ))
                
                MobileAds.initialize(this@MainActivity) { initializationStatus ->
                    // Keep callback processing on background thread
                    CoroutineScope(Dispatchers.IO).launch {
                        val callbackTime = System.currentTimeMillis()
                        val callbackThread = Thread.currentThread()
                        
                        android.util.Log.i("GAM_SDK_INIT", "🎉 GAM SDK INITIALIZATION CALLBACK RECEIVED (MainActivity)!")
                        android.util.Log.i("GAM_SDK_INIT", "⏰ Callback Time: ${java.text.SimpleDateFormat("HH:mm:ss.SSS", java.util.Locale.getDefault()).format(java.util.Date(callbackTime))}")
                        android.util.Log.i("GAM_SDK_INIT", "⏱️ Total Initialization Time: ${callbackTime - initStartTime}ms")
                        android.util.Log.i("GAM_SDK_INIT", "🧵 Callback Processing Thread: ${callbackThread.name} (ID: ${callbackThread.id})")
                        android.util.Log.i("GAM_SDK_INIT", "🎯 Callback kept on BACKGROUND THREAD (Non-blocking)")
                        
                        AppTracer.startTrace("GAM_SDK_ProcessCallback", mapOf(
                            "callback_thread" to callbackThread.name,
                            "processing_thread" to "background"
                        ))
                        
                        val adapterStatusMap = initializationStatus.adapterStatusMap
                        android.util.Log.i("GAM_SDK_INIT", "📊 Initialization Status:")
                        android.util.Log.i("GAM_SDK_INIT", "   - Total Adapters: ${adapterStatusMap.size}")
                        
                        var readyCount = 0
                        var notReadyCount = 0
                        
                        for (adapterClass in adapterStatusMap.keys) {
                            val status = adapterStatusMap[adapterClass]
                            val state = status?.initializationState?.name ?: "UNKNOWN"
                            val description = status?.description ?: "No description"
                            
                            android.util.Log.i("GAM_SDK_INIT", "   📱 $adapterClass: $state")
                            if (description.isNotEmpty()) {
                                android.util.Log.i("GAM_SDK_INIT", "      💬 $description")
                            }
                            
                            if (state == "READY") readyCount++ else notReadyCount++
                        }
                        
                        android.util.Log.i("GAM_SDK_INIT", "📈 Summary: $readyCount Ready, $notReadyCount Not Ready")
                        android.util.Log.i("GAM_SDK_INIT", "✅ GAM SDK INITIALIZATION COMPLETED SUCCESSFULLY (MainActivity)!")
                        android.util.Log.i("GAM_SDK_INIT", "🧵 Processing completed on: ${callbackThread.name}")
                        android.util.Log.i("GAM_SDK_INIT", "🚀 Performance: Activity Context + Background Processing")
                        
                        AppTracer.stopTrace("GAM_SDK_ProcessCallback")
                        AppTracer.stopTrace("MobileAds_Initialize_MainActivity")
                        AppTracer.stopAsyncTrace("GAM_SDK_MainActivity_Init")
                    }
                }
                
                val postCallTime = System.currentTimeMillis()
                android.util.Log.i("GAM_SDK_INIT", "📤 MobileAds.initialize() call dispatched in ${postCallTime - sdkCallTime}ms")
                android.util.Log.i("GAM_SDK_INIT", "⏳ Waiting for initialization callback (Activity Context)...")
                
            } catch (e: Exception) {
                val errorTime = System.currentTimeMillis()
                val errorThread = Thread.currentThread()
                
                android.util.Log.e("GAM_SDK_INIT", "❌ GAM SDK INITIALIZATION FAILED (MainActivity)!")
                android.util.Log.e("GAM_SDK_INIT", "⏰ Error Time: ${java.text.SimpleDateFormat("HH:mm:ss.SSS", java.util.Locale.getDefault()).format(java.util.Date(errorTime))}")
                android.util.Log.e("GAM_SDK_INIT", "🧵 Error Thread: ${errorThread.name}")
                android.util.Log.e("GAM_SDK_INIT", "💥 Error: ${e.message}")
                
                AppTracer.stopTrace("GAM_SDK_MainActivity_Init")
            }
        }
        
        val methodEndTime = System.currentTimeMillis()
        android.util.Log.i("GAM_SDK_INIT", "🏁 MainActivity.initializeGamSdk() method completed in ${methodEndTime - initStartTime}ms")
    }
}

@Composable
fun MainScreen(
    viewModel: MainViewModel,
    adRepository: AdRepositoryImpl
) {
    AppTracer.startTrace("MainScreen_StateCollection")
    val uiState by viewModel.uiState.collectAsState()
    val bannerAdState by viewModel.bannerAdState.collectAsState()
    val interstitialAdState by viewModel.interstitialAdState.collectAsState()
    AppTracer.stopTrace("MainScreen_StateCollection")
    
    AppTracer.startTrace("MainScreen_RememberSnackbar")
    val snackbarHostState = remember { SnackbarHostState() }
    AppTracer.stopTrace("MainScreen_RememberSnackbar")

    LaunchedEffect(uiState.showMessage) {
        AppTracer.startTrace("MainScreen_SnackbarEffect")
        uiState.showMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.onUserAction(UserAction.ClearMessage)
        }
        AppTracer.stopTrace("MainScreen_SnackbarEffect")
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "GAM SDK PoC",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            
            Text(
                text = "Clean Architecture + Perfetto Tracing",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            AdStatusCard(
                title = "Banner Ad",
                adState = bannerAdState,
                onLoadClick = { viewModel.onUserAction(UserAction.LoadBannerAd) }
            )
            
            if (bannerAdState is AdLoadResult.Success) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        adRepository.getBannerAdView(AdType.BANNER)?.let { adView ->
                            AndroidView(
                                factory = { adView },
                                modifier = Modifier.fillMaxSize()
                            )
                        } ?: Text(
                            text = "Banner Ad Placeholder",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            AdStatusCard(
                title = "Interstitial Ad",
                adState = interstitialAdState,
                onLoadClick = { viewModel.onUserAction(UserAction.LoadInterstitialAd) },
                onShowClick = { viewModel.onUserAction(UserAction.ShowInterstitialAd) }
            )

            Spacer(modifier = Modifier.weight(1f))
            
            val context = LocalContext.current
            Button(
                onClick = {
                    val intent = android.content.Intent(
                        context,
                        com.example.gamsdkpoc.presentation.AdTestActivity::class.java
                    )
                    context.startActivity(intent)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Open Simple Ad Test")
            }
            
            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Performance Monitoring",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "• AppTracer with Perfetto integration\n" +
                                "• Non-blocking GAM SDK initialization\n" +
                                "• Clean Architecture with MVVM\n" +
                                "• Comprehensive tracing coverage",
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        }
    }
    
}

@Composable
fun AdStatusCard(
    title: String,
    adState: AdLoadResult,
    onLoadClick: () -> Unit,
    onShowClick: (() -> Unit)? = null
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            val statusText = when (adState) {
                is AdLoadResult.Success -> "✅ Loaded Successfully"
                is AdLoadResult.Error -> "❌ Error: ${adState.message}"
                is AdLoadResult.Loading -> "⏳ Loading..."
            }
            
            val statusColor = when (adState) {
                is AdLoadResult.Success -> MaterialTheme.colorScheme.primary
                is AdLoadResult.Error -> MaterialTheme.colorScheme.error
                is AdLoadResult.Loading -> MaterialTheme.colorScheme.onSurfaceVariant
            }
            
            Text(
                text = statusText,
                style = MaterialTheme.typography.bodyMedium,
                color = statusColor
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {
                        AppTracer.startTrace("User_Click_LoadAd", mapOf("adType" to title))
                        onLoadClick()
                        AppTracer.stopTrace("User_Click_LoadAd")
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Load $title")
                }
                
                onShowClick?.let { showClick ->
                    Button(
                        onClick = {
                            AppTracer.startTrace("User_Click_ShowAd", mapOf("adType" to title))
                            showClick()
                            AppTracer.stopTrace("User_Click_ShowAd")
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = adState is AdLoadResult.Success
                    ) {
                        Text("Show $title")
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    GamSdkPocTheme {
        AdStatusCard(
            title = "Banner Ad",
            adState = AdLoadResult.Success,
            onLoadClick = { }
        )
    }
}
