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

@HiltViewModel
class MainViewModel @Inject constructor(
    private val loadBannerAdUseCase: LoadBannerAdUseCase,
    private val loadInterstitialAdUseCase: LoadInterstitialAdUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    private val _bannerAdState = MutableStateFlow<AdLoadResult>(AdLoadResult.Loading)
    val bannerAdState: StateFlow<AdLoadResult> = _bannerAdState.asStateFlow()

    private val _interstitialAdState = MutableStateFlow<AdLoadResult>(AdLoadResult.Loading)
    val interstitialAdState: StateFlow<AdLoadResult> = _interstitialAdState.asStateFlow()

    init {
        AppTracer.startTrace("MainViewModel_Init")
        loadBannerAd()
        loadInterstitialAd()
        AppTracer.stopTrace()
    }

    fun loadBannerAd() {
        AppTracer.startTrace("MainViewModel_LoadBannerAd")
        
        viewModelScope.launch {
            AppTracer.startTrace("MainViewModel_LoadBannerAd_Coroutine")
            loadBannerAdUseCase.loadTestBannerAd()
                .collect { result ->
                    AppTracer.startTrace("MainViewModel_BannerAdResult", mapOf(
                        "result" to result::class.java.simpleName
                    ))
                    _bannerAdState.value = result
                    AppTracer.stopTrace("MainViewModel_BannerAdResult")
                }
            AppTracer.stopTrace("MainViewModel_LoadBannerAd_Coroutine")
        }
        
        AppTracer.stopTrace("MainViewModel_LoadBannerAd")
    }

    fun loadInterstitialAd() {
        AppTracer.startTrace("MainViewModel_LoadInterstitialAd")
        
        viewModelScope.launch {
            AppTracer.startTrace("MainViewModel_LoadInterstitialAd_Coroutine")
            loadInterstitialAdUseCase.loadTestInterstitialAd()
                .collect { result ->
                    AppTracer.startTrace("MainViewModel_InterstitialAdResult", mapOf(
                        "result" to result::class.java.simpleName
                    ))
                    _interstitialAdState.value = result
                    AppTracer.stopTrace("MainViewModel_InterstitialAdResult")
                }
            AppTracer.stopTrace("MainViewModel_LoadInterstitialAd_Coroutine")
        }
        
        AppTracer.stopTrace("MainViewModel_LoadInterstitialAd")
    }

    fun showInterstitialAd() {
        AppTracer.startTrace("MainViewModel_ShowInterstitialAd")
        
        viewModelScope.launch {
            AppTracer.startTrace("MainViewModel_ShowInterstitialAd_Coroutine")
            
            AppTracer.startTrace("MainViewModel_CheckAdReady")
            val isReady = loadInterstitialAdUseCase.isAdReady()
            AppTracer.stopTrace("MainViewModel_CheckAdReady")
            
            if (isReady) {
                AppTracer.startTrace("MainViewModel_ShowAd_Flow")
                loadInterstitialAdUseCase.showAd()
                    .collect { result ->
                        AppTracer.startTrace("MainViewModel_ShowInterstitialResult", mapOf(
                            "result" to result::class.java.simpleName
                        ))
                        
                        _uiState.value = _uiState.value.copy(
                            showMessage = when (result) {
                                is AdLoadResult.Success -> "Interstitial ad shown successfully"
                                is AdLoadResult.Error -> "Failed to show ad: ${result.message}"
                                is AdLoadResult.Loading -> "Showing ad..."
                            }
                        )
                        
                        AppTracer.stopTrace("MainViewModel_ShowInterstitialResult")
                    }
                AppTracer.stopTrace("MainViewModel_ShowAd_Flow")
            } else {
                AppTracer.startTrace("MainViewModel_AdNotReady_Reload")
                _uiState.value = _uiState.value.copy(
                    showMessage = "Interstitial ad not ready. Loading..."
                )
                loadInterstitialAd()
                AppTracer.stopTrace("MainViewModel_AdNotReady_Reload")
            }
            AppTracer.stopTrace("MainViewModel_ShowInterstitialAd_Coroutine")
        }
        
        AppTracer.stopTrace("MainViewModel_ShowInterstitialAd")
    }

    fun onUserAction(action: UserAction) {
        AppTracer.startTrace("MainViewModel_UserAction", mapOf(
            "action" to action::class.java.simpleName
        ))
        
        when (action) {
            is UserAction.LoadBannerAd -> {
                AppTracer.startTrace("MainViewModel_UserAction_LoadBanner")
                loadBannerAd()
                AppTracer.stopTrace("MainViewModel_UserAction_LoadBanner")
            }
            is UserAction.LoadInterstitialAd -> {
                AppTracer.startTrace("MainViewModel_UserAction_LoadInterstitial")
                loadInterstitialAd()
                AppTracer.stopTrace("MainViewModel_UserAction_LoadInterstitial")
            }
            is UserAction.ShowInterstitialAd -> {
                AppTracer.startTrace("MainViewModel_UserAction_ShowInterstitial")
                showInterstitialAd()
                AppTracer.stopTrace("MainViewModel_UserAction_ShowInterstitial")
            }
            is UserAction.ClearMessage -> {
                AppTracer.startTrace("MainViewModel_UserAction_ClearMessage")
                _uiState.value = _uiState.value.copy(showMessage = null)
                AppTracer.stopTrace("MainViewModel_UserAction_ClearMessage")
            }
        }
        
        AppTracer.stopTrace("MainViewModel_UserAction")
    }

    override fun onCleared() {
        AppTracer.startTrace("MainViewModel_Cleared")
        super.onCleared()
        AppTracer.stopTrace("MainViewModel_Cleared")
    }
}

data class MainUiState(
    val showMessage: String? = null,
    val isLoading: Boolean = false
)

sealed class UserAction {
    object LoadBannerAd : UserAction()
    object LoadInterstitialAd : UserAction()
    object ShowInterstitialAd : UserAction()
    object ClearMessage : UserAction()
}
