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
import kotlinx.coroutines.withTimeoutOrNull
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
        
        super.onCreate(savedInstanceState)
        
        AppTracer.startAsyncTrace("MainActivity_EnableEdgeToEdge")
        enableEdgeToEdge()
        AppTracer.stopAsyncTrace("MainActivity_EnableEdgeToEdge")
        
        AppTracer.startAsyncTrace("MainActivity_SetActivity")
        adRepository.setCurrentActivity(this)
        AppTracer.stopAsyncTrace("MainActivity_SetActivity")
        
        AppTracer.traceStateChange("MainActivity", "CREATING", "INITIALIZING_GAM_SDK")
        
        AppTracer.startAsyncTrace("MainActivity_InitializeGamSdk")
        initializeGamSdk()
        AppTracer.stopAsyncTrace("MainActivity_InitializeGamSdk")
        
        AppTracer.traceStateChange("MainActivity", "INITIALIZING_GAM_SDK", "SETTING_CONTENT")
        
        setTracedContent {
            GamSdkPocTheme {
                MainScreen(
                    viewModel = viewModel,
                    adRepository = adRepository
                )
            }
        }
        
        AppTracer.traceStateChange("MainActivity", "SETTING_CONTENT", "CREATED")
    }
    
    override fun onResume() {
        super.onResume()
        
        AppTracer.startAsyncTrace("MainActivity_SetActivityOnResume")
        adRepository.setCurrentActivity(this)
        AppTracer.stopAsyncTrace("MainActivity_SetActivityOnResume")
    }
    
    override fun onPause() {
        super.onPause()
        
        AppTracer.startAsyncTrace("MainActivity_ClearActivityOnPause")
        adRepository.setCurrentActivity(null)
        AppTracer.stopAsyncTrace("MainActivity_ClearActivityOnPause")
    }
    
    private fun initializeGamSdk() {
        val initStartTime = System.currentTimeMillis()
        val mainThread = Thread.currentThread()
        
        android.util.Log.i("GAM_SDK_THREAD", "🔥 GAM SDK THREAD ANALYSIS STARTED")
        android.util.Log.i("GAM_SDK_THREAD", "📍 PHASE 1: Method Call Thread")
        android.util.Log.i("GAM_SDK_THREAD", "   🧵 Thread: ${mainThread.name} (ID: ${mainThread.id})")
        android.util.Log.i("GAM_SDK_THREAD", "   🏷️ Type: ${if (mainThread.name == "main") "MAIN THREAD" else "BACKGROUND THREAD"}")
        
        // 🎯 MAIN GAM SDK INITIALIZATION TRACE - COVERS ENTIRE LIFECYCLE
        AppTracer.startAsyncTrace("GAM_SDK_Complete_Initialization", mapOf(
            "start_thread" to mainThread.name,
            "start_time" to initStartTime.toString(),
            "sdk_version" to "24.4.0",
            "context_type" to "Activity"
        ))
        
        CoroutineScope(Dispatchers.IO).launch {
            val backgroundThread = Thread.currentThread()
            
            android.util.Log.i("GAM_SDK_THREAD", "📍 PHASE 2: Coroutine Launch Thread")
            android.util.Log.i("GAM_SDK_THREAD", "   🧵 Thread: ${backgroundThread.name} (ID: ${backgroundThread.id})")
            android.util.Log.i("GAM_SDK_THREAD", "   🏷️ Type: ${if (backgroundThread.name == "main") "MAIN THREAD" else "BACKGROUND THREAD"}")
            android.util.Log.i("GAM_SDK_THREAD", "   🎯 Dispatcher: Dispatchers.IO")
            
            // Thread analysis trace (separate from main GAM SDK trace)
            AppTracer.startAsyncTrace("GAM_SDK_Thread_Analysis", mapOf(
                "phase" to "initialization",
                "calling_thread" to mainThread.name,
                "execution_thread" to backgroundThread.name,
                "thread_switch" to (mainThread.name != backgroundThread.name).toString()
            ))
            
            try {
                val sdkCallThread = Thread.currentThread()
                
                android.util.Log.i("GAM_SDK_THREAD", "📍 PHASE 3: MobileAds.initialize() Call Thread")
                android.util.Log.i("GAM_SDK_THREAD", "   🧵 Thread: ${sdkCallThread.name} (ID: ${sdkCallThread.id})")
                android.util.Log.i("GAM_SDK_THREAD", "   🏷️ Type: ${if (sdkCallThread.name == "main") "MAIN THREAD ❌" else "BACKGROUND THREAD ✅"}")
                
                // 🚀 GAM SDK ACTUAL INITIALIZATION CALL
                MobileAds.initialize(this@MainActivity) { initializationStatus ->
                    val callbackThread = Thread.currentThread()
                    
                    android.util.Log.i("GAM_SDK_THREAD", "📍 PHASE 4: GAM SDK Callback Thread")
                    android.util.Log.i("GAM_SDK_THREAD", "   🧵 Thread: ${callbackThread.name} (ID: ${callbackThread.id})")
                    android.util.Log.i("GAM_SDK_THREAD", "   🏷️ Type: ${if (callbackThread.name == "main") "MAIN THREAD" else "BACKGROUND THREAD"}")
                    android.util.Log.i("GAM_SDK_THREAD", "   🔄 Thread Switch: ${sdkCallThread.name} → ${callbackThread.name}")
                    
                    CoroutineScope(Dispatchers.IO).launch {
                        val processingThread = Thread.currentThread()
                        val completionTime = System.currentTimeMillis()
                        
                        android.util.Log.i("GAM_SDK_THREAD", "📍 PHASE 5: Callback Processing Thread")
                        android.util.Log.i("GAM_SDK_THREAD", "   🧵 Thread: ${processingThread.name} (ID: ${processingThread.id})")
                        android.util.Log.i("GAM_SDK_THREAD", "   🏷️ Type: ${if (processingThread.name == "main") "MAIN THREAD" else "BACKGROUND THREAD"}")
                        android.util.Log.i("GAM_SDK_THREAD", "   🔄 Thread Switch: ${callbackThread.name} → ${processingThread.name}")
                        
                        android.util.Log.i("GAM_SDK_THREAD", "")
                        android.util.Log.i("GAM_SDK_THREAD", "📊 THREAD ANALYSIS SUMMARY:")
                        android.util.Log.i("GAM_SDK_THREAD", "   1️⃣ Method Call: ${mainThread.name}")
                        android.util.Log.i("GAM_SDK_THREAD", "   2️⃣ Coroutine Launch: ${backgroundThread.name}")
                        android.util.Log.i("GAM_SDK_THREAD", "   3️⃣ SDK Initialize Call: ${sdkCallThread.name}")
                        android.util.Log.i("GAM_SDK_THREAD", "   4️⃣ SDK Callback: ${callbackThread.name}")
                        android.util.Log.i("GAM_SDK_THREAD", "   5️⃣ Callback Processing: ${processingThread.name}")
                        android.util.Log.i("GAM_SDK_THREAD", "")
                        android.util.Log.i("GAM_SDK_THREAD", "⏱️ Total GAM SDK Initialization Time: ${completionTime - initStartTime}ms")
                        android.util.Log.i("GAM_SDK_THREAD", "✅ GAM SDK THREAD ANALYSIS COMPLETED")
                        
                        // Stop both traces
                        AppTracer.stopAsyncTrace("GAM_SDK_Thread_Analysis")
                        AppTracer.stopAsyncTrace("GAM_SDK_Complete_Initialization")
                    }
                }
                
            } catch (e: Exception) {
                android.util.Log.e("GAM_SDK_THREAD", "❌ Error during GAM SDK initialization: ${e.message}")
                AppTracer.stopAsyncTrace("GAM_SDK_Thread_Analysis")
                AppTracer.stopAsyncTrace("GAM_SDK_Complete_Initialization")
            }
        }
    }
}

@Composable
fun MainScreen(
    viewModel: MainViewModel,
    adRepository: AdRepositoryImpl
) {
    AppTracer.startAsyncTrace("MainScreen_StateCollection")
    val uiState by viewModel.uiState.collectAsState()
    val bannerAdState by viewModel.bannerAdState.collectAsState()
    val interstitialAdState by viewModel.interstitialAdState.collectAsState()
    AppTracer.stopAsyncTrace("MainScreen_StateCollection")
    
    AppTracer.startAsyncTrace("MainScreen_RememberSnackbar")
    val snackbarHostState = remember { SnackbarHostState() }
    AppTracer.stopAsyncTrace("MainScreen_RememberSnackbar")

    LaunchedEffect(uiState.showMessage) {
        AppTracer.startAsyncTrace("MainScreen_SnackbarEffect")
        uiState.showMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.onUserAction(UserAction.ClearMessage)
        }
        AppTracer.stopAsyncTrace("MainScreen_SnackbarEffect")
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
                        AppTracer.startAsyncTrace("User_Click_LoadAd", mapOf("adType" to title))
                        onLoadClick()
                        AppTracer.stopAsyncTrace("User_Click_LoadAd")
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Load $title")
                }
                
                onShowClick?.let { showClick ->
                    Button(
                        onClick = {
                            AppTracer.startAsyncTrace("User_Click_ShowAd", mapOf("adType" to title))
                            showClick()
                            AppTracer.stopAsyncTrace("User_Click_ShowAd")
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
