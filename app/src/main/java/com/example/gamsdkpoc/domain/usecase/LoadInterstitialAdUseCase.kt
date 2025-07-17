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
        AppTracer.startTrace("InterstitialUseCase_CallRepository", mapOf(
            "adUnitId" to adConfig.adUnitId,
            "isTestAd" to adConfig.isTestAd.toString()
        ))
        
        val flow = adRepository.loadInterstitialAd(adConfig)
            .onEach { result ->
                AppTracer.startTrace("InterstitialUseCase_ProcessResult", mapOf(
                    "result" to result::class.java.simpleName,
                    "adUnitId" to adConfig.adUnitId
                ))
                AppTracer.stopTrace("InterstitialUseCase_ProcessResult")
            }
        
        AppTracer.stopTrace("InterstitialUseCase_CallRepository")
        return flow
    }
    
    fun showAd(adType: AdType = AdType.INTERSTITIAL): Flow<AdLoadResult> {
        AppTracer.startTrace("InterstitialUseCase_CallShowRepository", mapOf(
            "adType" to adType.name
        ))
        
        val flow = adRepository.showInterstitialAd(adType)
            .onEach { result ->
                AppTracer.startTrace("InterstitialUseCase_ProcessShowResult", mapOf(
                    "result" to result::class.java.simpleName,
                    "adType" to adType.name
                ))
                AppTracer.stopTrace("InterstitialUseCase_ProcessShowResult")
            }
        
        AppTracer.stopTrace("InterstitialUseCase_CallShowRepository")
        return flow
    }
    
    fun loadTestInterstitialAd(): Flow<AdLoadResult> {
        AppTracer.startTrace("InterstitialUseCase_GetTestConfig")
        val testConfig = AdConfig.getTestAdConfigs()[AdType.INTERSTITIAL]!!
        AppTracer.stopTrace("InterstitialUseCase_GetTestConfig")
        
        AppTracer.startTrace("InterstitialUseCase_LoadWithTestConfig")
        val result = loadAd(testConfig)
        AppTracer.stopTrace("InterstitialUseCase_LoadWithTestConfig")
        
        return result
    }
    
    suspend fun isAdReady(): Boolean {
        AppTracer.startTrace("InterstitialUseCase_CheckReady")
        val isReady = adRepository.isAdReady(AdType.INTERSTITIAL)
        AppTracer.stopTrace("InterstitialUseCase_CheckReady")
        return isReady
    }
}
