package com.example.gamsdkpoc.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gamsdkpoc.core.tracing.AppTracer
import com.example.gamsdkpoc.domain.model.AdLoadResult
import com.example.gamsdkpoc.domain.model.AdType
import com.example.gamsdkpoc.domain.usecase.LoadBannerAdUseCase
import com.example.gamsdkpoc.domain.usecase.LoadInterstitialAdUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the main screen with ad management functionality.
 */
@HiltViewModel
class MainViewModel @Inject constructor(
    private val loadBannerAdUseCase: LoadBannerAdUseCase,
    private val loadInterstitialAdUseCase: LoadInterstitialAdUseCase
) : ViewModel() {

    // UI State
    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    // Ad loading states
    private val _bannerAdState = MutableStateFlow<AdLoadResult>(AdLoadResult.Loading)
    val bannerAdState: StateFlow<AdLoadResult> = _bannerAdState.asStateFlow()

    private val _interstitialAdState = MutableStateFlow<AdLoadResult>(AdLoadResult.Loading)
    val interstitialAdState: StateFlow<AdLoadResult> = _interstitialAdState.asStateFlow()

    init {
        AppTracer.startTrace("MainViewModel_Init")
        // Preload ads on initialization
        loadBannerAd()
        loadInterstitialAd()
        AppTracer.stopTrace()
    }

    /**
     * Load a banner ad using the use case.
     */
    fun loadBannerAd() {
        AppTracer.startTrace("MainViewModel_LoadBannerAd")
        
        viewModelScope.launch {
            loadBannerAdUseCase.loadTestBannerAd()
                .collect { result ->
                    AppTracer.startTrace("MainViewModel_BannerAdResult", mapOf(
                        "result" to result::class.java.simpleName
                    ))
                    _bannerAdState.value = result
                    AppTracer.stopTrace()
                }
        }
        
        AppTracer.stopTrace()
    }

    /**
     * Load an interstitial ad using the use case.
     */
    fun loadInterstitialAd() {
        AppTracer.startTrace("MainViewModel_LoadInterstitialAd")
        
        viewModelScope.launch {
            loadInterstitialAdUseCase.loadTestInterstitialAd()
                .collect { result ->
                    AppTracer.startTrace("MainViewModel_InterstitialAdResult", mapOf(
                        "result" to result::class.java.simpleName
                    ))
                    _interstitialAdState.value = result
                    AppTracer.stopTrace()
                }
        }
        
        AppTracer.stopTrace()
    }

    /**
     * Show an interstitial ad if it's loaded.
     */
    fun showInterstitialAd() {
        AppTracer.startTrace("MainViewModel_ShowInterstitialAd")
        
        viewModelScope.launch {
            val isReady = loadInterstitialAdUseCase.isAdReady()
            
            if (isReady) {
                loadInterstitialAdUseCase.showAd()
                    .collect { result ->
                        AppTracer.startTrace("MainViewModel_ShowInterstitialResult", mapOf(
                            "result" to result::class.java.simpleName
                        ))
                        
                        // Update UI state based on show result
                        _uiState.value = _uiState.value.copy(
                            showMessage = when (result) {
                                is AdLoadResult.Success -> "Interstitial ad shown successfully"
                                is AdLoadResult.Error -> "Failed to show ad: ${result.message}"
                                is AdLoadResult.Loading -> "Showing ad..."
                            }
                        )
                        
                        AppTracer.stopTrace()
                    }
            } else {
                _uiState.value = _uiState.value.copy(
                    showMessage = "Interstitial ad not ready. Loading..."
                )
                // Reload the ad
                loadInterstitialAd()
            }
        }
        
        AppTracer.stopTrace()
    }

    /**
     * Handle user interactions with tracing.
     */
    fun onUserAction(action: UserAction) {
        AppTracer.startTrace("MainViewModel_UserAction", mapOf(
            "action" to action::class.java.simpleName
        ))
        
        when (action) {
            is UserAction.LoadBannerAd -> loadBannerAd()
            is UserAction.LoadInterstitialAd -> loadInterstitialAd()
            is UserAction.ShowInterstitialAd -> showInterstitialAd()
            is UserAction.ClearMessage -> {
                _uiState.value = _uiState.value.copy(showMessage = null)
            }
        }
        
        AppTracer.stopTrace()
    }

    override fun onCleared() {
        AppTracer.startTrace("MainViewModel_Cleared")
        super.onCleared()
        AppTracer.stopTrace()
    }
}

/**
 * UI state for the main screen.
 */
data class MainUiState(
    val showMessage: String? = null,
    val isLoading: Boolean = false
)

/**
 * User actions that can be performed on the main screen.
 */
sealed class UserAction {
    object LoadBannerAd : UserAction()
    object LoadInterstitialAd : UserAction()
    object ShowInterstitialAd : UserAction()
    object ClearMessage : UserAction()
}
