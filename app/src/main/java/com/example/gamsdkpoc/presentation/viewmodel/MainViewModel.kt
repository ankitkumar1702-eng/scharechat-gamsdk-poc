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
        AppTracer.startTrace("MainViewModel_LoadBannerInit")
        loadBannerAd()
        AppTracer.stopTrace("MainViewModel_LoadBannerInit")
        
        AppTracer.startTrace("MainViewModel_LoadInterstitialInit")
        loadInterstitialAd()
        AppTracer.stopTrace("MainViewModel_LoadInterstitialInit")
    }

    fun loadBannerAd() {
        AppTracer.startTrace("MainViewModel_LaunchBannerCoroutine")
        viewModelScope.launch {
            AppTracer.startTrace("MainViewModel_CallBannerUseCase")
            loadBannerAdUseCase.loadTestBannerAd()
                .collect { result ->
                    AppTracer.startTrace("MainViewModel_UpdateBannerState", mapOf(
                        "result" to result::class.java.simpleName
                    ))
                    _bannerAdState.value = result
                    AppTracer.stopTrace("MainViewModel_UpdateBannerState")
                }
            AppTracer.stopTrace("MainViewModel_CallBannerUseCase")
        }
        AppTracer.stopTrace("MainViewModel_LaunchBannerCoroutine")
    }

    fun loadInterstitialAd() {
        AppTracer.startTrace("MainViewModel_LaunchInterstitialCoroutine")
        viewModelScope.launch {
            AppTracer.startTrace("MainViewModel_CallInterstitialUseCase")
            loadInterstitialAdUseCase.loadTestInterstitialAd()
                .collect { result ->
                    AppTracer.startTrace("MainViewModel_UpdateInterstitialState", mapOf(
                        "result" to result::class.java.simpleName
                    ))
                    _interstitialAdState.value = result
                    AppTracer.stopTrace("MainViewModel_UpdateInterstitialState")
                }
            AppTracer.stopTrace("MainViewModel_CallInterstitialUseCase")
        }
        AppTracer.stopTrace("MainViewModel_LaunchInterstitialCoroutine")
    }

    fun showInterstitialAd() {
        AppTracer.startTrace("MainViewModel_LaunchShowCoroutine")
        viewModelScope.launch {
            AppTracer.startTrace("MainViewModel_CheckAdReady")
            val isReady = loadInterstitialAdUseCase.isAdReady()
            AppTracer.stopTrace("MainViewModel_CheckAdReady")
            
            if (isReady) {
                AppTracer.startTrace("MainViewModel_CallShowUseCase")
                loadInterstitialAdUseCase.showAd()
                    .collect { result ->
                        AppTracer.startTrace("MainViewModel_UpdateShowMessage", mapOf(
                            "result" to result::class.java.simpleName
                        ))
                        
                        _uiState.value = _uiState.value.copy(
                            showMessage = when (result) {
                                is AdLoadResult.Success -> "Interstitial ad shown successfully"
                                is AdLoadResult.Error -> "Failed to show ad: ${result.message}"
                                is AdLoadResult.Loading -> "Showing ad..."
                            }
                        )
                        
                        AppTracer.stopTrace("MainViewModel_UpdateShowMessage")
                    }
                AppTracer.stopTrace("MainViewModel_CallShowUseCase")
            } else {
                AppTracer.startTrace("MainViewModel_AdNotReady")
                _uiState.value = _uiState.value.copy(
                    showMessage = "Interstitial ad not ready. Loading..."
                )
                AppTracer.stopTrace("MainViewModel_AdNotReady")
                
                AppTracer.startTrace("MainViewModel_ReloadAd")
                loadInterstitialAd()
                AppTracer.stopTrace("MainViewModel_ReloadAd")
            }
        }
        AppTracer.stopTrace("MainViewModel_LaunchShowCoroutine")
    }

    fun onUserAction(action: UserAction) {
        when (action) {
            is UserAction.LoadBannerAd -> {
                AppTracer.startTrace("UserAction_LoadBanner")
                loadBannerAd()
                AppTracer.stopTrace("UserAction_LoadBanner")
            }
            is UserAction.LoadInterstitialAd -> {
                AppTracer.startTrace("UserAction_LoadInterstitial")
                loadInterstitialAd()
                AppTracer.stopTrace("UserAction_LoadInterstitial")
            }
            is UserAction.ShowInterstitialAd -> {
                AppTracer.startTrace("UserAction_ShowInterstitial")
                showInterstitialAd()
                AppTracer.stopTrace("UserAction_ShowInterstitial")
            }
            is UserAction.ClearMessage -> {
                AppTracer.startTrace("UserAction_ClearMessage")
                _uiState.value = _uiState.value.copy(showMessage = null)
                AppTracer.stopTrace("UserAction_ClearMessage")
            }
        }
    }

    override fun onCleared() {
        AppTracer.startTrace("MainViewModel_OnCleared")
        super.onCleared()
        AppTracer.stopTrace("MainViewModel_OnCleared")
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
