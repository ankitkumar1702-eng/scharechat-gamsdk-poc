package com.example.gamsdkpoc.domain.usecase

import com.example.gamsdkpoc.core.tracing.AppTracer
import com.example.gamsdkpoc.domain.model.AdConfig
import com.example.gamsdkpoc.domain.model.AdLoadResult
import com.example.gamsdkpoc.domain.model.AdType
import com.example.gamsdkpoc.domain.repository.AdRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

class LoadInterstitialAdUseCase @Inject constructor(
    private val adRepository: AdRepository
) {
    
    fun loadAd(adConfig: AdConfig): Flow<AdLoadResult> {
        AppTracer.startAsyncTrace("InterstitialUseCase_CallRepository", mapOf(
            "adUnitId" to adConfig.adUnitId,
            "adType" to adConfig.adType.name
        ))
        
        val flow = adRepository.loadInterstitialAd(adConfig)
            .onEach { result ->
                AppTracer.startAsyncTrace("InterstitialUseCase_ProcessResult", mapOf(
                    "result" to result::class.java.simpleName,
                    "adType" to adConfig.adType.name
                ))
                AppTracer.stopAsyncTrace("InterstitialUseCase_ProcessResult")
            }
        
        AppTracer.stopAsyncTrace("InterstitialUseCase_CallRepository")
        return flow
    }
    
    fun showAd(adType: AdType = AdType.INTERSTITIAL): Flow<AdLoadResult> {
        AppTracer.startAsyncTrace("InterstitialUseCase_CallShowRepository", mapOf(
            "adType" to adType.name
        ))
        
        val flow = adRepository.showInterstitialAd(adType)
            .onEach { result ->
                AppTracer.startAsyncTrace("InterstitialUseCase_ProcessShowResult", mapOf(
                    "result" to result::class.java.simpleName,
                    "adType" to adType.name
                ))
                AppTracer.stopAsyncTrace("InterstitialUseCase_ProcessShowResult")
            }
        
        AppTracer.stopAsyncTrace("InterstitialUseCase_CallShowRepository")
        return flow
    }
    
    fun loadTestInterstitialAd(): Flow<AdLoadResult> {
        AppTracer.startAsyncTrace("InterstitialUseCase_GetTestConfig")
        val testConfig = AdConfig.getTestAdConfigs()[AdType.INTERSTITIAL]!!
        AppTracer.stopAsyncTrace("InterstitialUseCase_GetTestConfig")
        
        AppTracer.startAsyncTrace("InterstitialUseCase_LoadWithTestConfig")
        val result = loadAd(testConfig)
        AppTracer.stopAsyncTrace("InterstitialUseCase_LoadWithTestConfig")
        
        return result
    }
    
    suspend fun isAdReady(): Boolean {
        AppTracer.startAsyncTrace("InterstitialUseCase_CheckReady")
        val isReady = adRepository.isAdReady(AdType.INTERSTITIAL)
        AppTracer.stopAsyncTrace("InterstitialUseCase_CheckReady")
        return isReady
    }
}
