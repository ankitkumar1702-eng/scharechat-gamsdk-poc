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
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : BaseActivity() {
    
    @Inject
    lateinit var adRepository: AdRepositoryImpl
    
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        // Set activity context for showing ads
        adRepository.setCurrentActivity(this)
        
        setTracedContent {
            GamSdkPocTheme {
                MainScreen(
                    viewModel = viewModel,
                    adRepository = adRepository
                )
            }
        }
    }
    
    override fun onResume() {
        super.onResume()
        // Update activity context
        adRepository.setCurrentActivity(this)
    }
    
    override fun onPause() {
        super.onPause()
        // Clear activity context to prevent memory leaks
        adRepository.setCurrentActivity(null)
    }
}

@Composable
fun MainScreen(
    viewModel: MainViewModel,
    adRepository: AdRepositoryImpl
) {
    AppTracer.startTrace("MainScreen_Compose")
    
    val uiState by viewModel.uiState.collectAsState()
    val bannerAdState by viewModel.bannerAdState.collectAsState()
    val interstitialAdState by viewModel.interstitialAdState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Show snackbar for messages
    LaunchedEffect(uiState.showMessage) {
        uiState.showMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.onUserAction(UserAction.ClearMessage)
        }
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
            // Title
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

            // Banner Ad Section
            AdStatusCard(
                title = "Banner Ad",
                adState = bannerAdState,
                onLoadClick = { viewModel.onUserAction(UserAction.LoadBannerAd) }
            )
            
            // Banner Ad Container - Show the actual banner ad when loaded
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

            // Interstitial Ad Section
            AdStatusCard(
                title = "Interstitial Ad",
                adState = interstitialAdState,
                onLoadClick = { viewModel.onUserAction(UserAction.LoadInterstitialAd) },
                onShowClick = { viewModel.onUserAction(UserAction.ShowInterstitialAd) }
            )

            Spacer(modifier = Modifier.weight(1f))
            
            // Test Activity Button
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

            // Performance Info
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
    
    AppTracer.stopTrace()
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
            
            // Ad Status
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
            
            // Action Buttons
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {
                        AppTracer.startTrace("User_Click_LoadAd", mapOf("adType" to title))
                        onLoadClick()
                        AppTracer.stopTrace()
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
                            AppTracer.stopTrace()
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
        // Preview with mock data
        AdStatusCard(
            title = "Banner Ad",
            adState = AdLoadResult.Success,
            onLoadClick = { }
        )
    }
}
