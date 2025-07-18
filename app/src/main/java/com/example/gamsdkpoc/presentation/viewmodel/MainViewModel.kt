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
        AppTracer.startAsyncTrace("MainViewModel_LoadBannerInit")
        loadBannerAd()
        AppTracer.stopAsyncTrace("MainViewModel_LoadBannerInit")

        AppTracer.startAsyncTrace("MainViewModel_LoadInterstitialInit")
        loadInterstitialAd()
        AppTracer.stopAsyncTrace("MainViewModel_LoadInterstitialInit")
    }

    fun loadBannerAd() {
        AppTracer.startAsyncTrace("MainViewModel_LaunchBannerCoroutine")
        viewModelScope.launch {
            AppTracer.startAsyncTrace("MainViewModel_CallBannerUseCase")
            loadBannerAdUseCase.loadTestBannerAd()
                .collect { result ->
                    AppTracer.startAsyncTrace("MainViewModel_UpdateBannerState", mapOf(
                        "result" to result::class.java.simpleName
                    ))
                    _bannerAdState.value = result
                    AppTracer.stopAsyncTrace("MainViewModel_UpdateBannerState")
                }
            AppTracer.stopAsyncTrace("MainViewModel_CallBannerUseCase")
        }
        AppTracer.stopAsyncTrace("MainViewModel_LaunchBannerCoroutine")
    }

    fun loadInterstitialAd() {
        AppTracer.startAsyncTrace("MainViewModel_LaunchInterstitialCoroutine")
        viewModelScope.launch {
            AppTracer.startAsyncTrace("MainViewModel_CallInterstitialUseCase")
            loadInterstitialAdUseCase.loadTestInterstitialAd()
                .collect { result ->
                    AppTracer.startAsyncTrace("MainViewModel_UpdateInterstitialState", mapOf(
                        "result" to result::class.java.simpleName
                    ))
                    _interstitialAdState.value = result
                    AppTracer.stopAsyncTrace("MainViewModel_UpdateInterstitialState")
                }
            AppTracer.stopAsyncTrace("MainViewModel_CallInterstitialUseCase")
        }
        AppTracer.stopAsyncTrace("MainViewModel_LaunchInterstitialCoroutine")
    }

    fun showInterstitialAd() {
        AppTracer.startAsyncTrace("MainViewModel_LaunchShowCoroutine")
        viewModelScope.launch {
            AppTracer.startAsyncTrace("MainViewModel_CheckAdReady")
            val isReady = loadInterstitialAdUseCase.isAdReady()
            AppTracer.stopAsyncTrace("MainViewModel_CheckAdReady")
            
            if (isReady) {
                AppTracer.startAsyncTrace("MainViewModel_CallShowUseCase")
                loadInterstitialAdUseCase.showAd()
                    .collect { result ->
                        AppTracer.startAsyncTrace("MainViewModel_UpdateShowMessage", mapOf(
                            "result" to result::class.java.simpleName
                        ))
                        
                        _uiState.value = _uiState.value.copy(
                            showMessage = when (result) {
                                is AdLoadResult.Success -> "Interstitial ad shown successfully"
                                is AdLoadResult.Error -> "Failed to show ad: ${result.message}"
                                is AdLoadResult.Loading -> "Showing ad..."
                            }
                        )
                        
                        AppTracer.stopAsyncTrace("MainViewModel_UpdateShowMessage")
                    }
                AppTracer.stopAsyncTrace("MainViewModel_CallShowUseCase")
            } else {
                AppTracer.startAsyncTrace("MainViewModel_AdNotReady")
                _uiState.value = _uiState.value.copy(
                    showMessage = "Interstitial ad not ready. Loading..."
                )
                AppTracer.stopAsyncTrace("MainViewModel_AdNotReady")
                
                AppTracer.startAsyncTrace("MainViewModel_ReloadAd")
                loadInterstitialAd()
                AppTracer.stopAsyncTrace("MainViewModel_ReloadAd")
            }
        }
        AppTracer.stopAsyncTrace("MainViewModel_LaunchShowCoroutine")
    }

    fun onUserAction(action: UserAction) {
        AppTracer.traceUserAction(
            action = action::class.java.simpleName,
            component = "MainViewModel",
            additionalData = mapOf("action_type" to action.toString())
        )
        
        when (action) {
            is UserAction.LoadBannerAd -> {
                AppTracer.traceStateChange("BannerAd", _bannerAdState.value::class.java.simpleName, "LOADING")
                AppTracer.startAsyncTrace("UserAction_LoadBanner")
                loadBannerAd()
                AppTracer.stopAsyncTrace("UserAction_LoadBanner")
            }
            is UserAction.LoadInterstitialAd -> {
                AppTracer.traceStateChange("InterstitialAd", _interstitialAdState.value::class.java.simpleName, "LOADING")
                AppTracer.startAsyncTrace("UserAction_LoadInterstitial")
                loadInterstitialAd()
                AppTracer.stopAsyncTrace("UserAction_LoadInterstitial")
            }
            is UserAction.ShowInterstitialAd -> {
                AppTracer.traceStateChange("InterstitialAd", "LOADED", "SHOWING")
                AppTracer.startAsyncTrace("UserAction_ShowInterstitial")
                showInterstitialAd()
                AppTracer.stopAsyncTrace("UserAction_ShowInterstitial")
            }
            is UserAction.ClearMessage -> {
                AppTracer.traceStateChange("UIMessage", "VISIBLE", "CLEARED")
                AppTracer.startAsyncTrace("UserAction_ClearMessage")
                _uiState.value = _uiState.value.copy(showMessage = null)
                AppTracer.stopAsyncTrace("UserAction_ClearMessage")
            }
        }
    }

    override fun onCleared() {
        AppTracer.startAsyncTrace("MainViewModel_OnCleared")
        super.onCleared()
        AppTracer.stopAsyncTrace("MainViewModel_OnCleared")
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
