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
        AppTracer.startTrace("LoadInterstitialAd_UseCase_LoadAd", mapOf(
            "adUnitId" to adConfig.adUnitId,
            "isTestAd" to adConfig.isTestAd.toString()
        ))
        
        return adRepository.loadInterstitialAd(adConfig)
            .onEach { result ->
                AppTracer.startTrace("LoadInterstitialAd_Result", mapOf(
                    "result" to result::class.java.simpleName,
                    "adUnitId" to adConfig.adUnitId
                ))
                AppTracer.stopTrace("LoadInterstitialAd_Result")
            }
            .also {
                AppTracer.stopTrace("LoadInterstitialAd_UseCase_LoadAd")
            }
    }
    
    fun showAd(adType: AdType = AdType.INTERSTITIAL): Flow<AdLoadResult> {
        AppTracer.startTrace("ShowInterstitialAd_UseCase_ShowAd", mapOf(
            "adType" to adType.name
        ))
        
        return adRepository.showInterstitialAd(adType)
            .onEach { result ->
                AppTracer.startTrace("ShowInterstitialAd_Result", mapOf(
                    "result" to result::class.java.simpleName,
                    "adType" to adType.name
                ))
                AppTracer.stopTrace("ShowInterstitialAd_Result")
            }
            .also {
                AppTracer.stopTrace("ShowInterstitialAd_UseCase_ShowAd")
            }
    }
    
    fun loadTestInterstitialAd(): Flow<AdLoadResult> {
        AppTracer.startTrace("LoadInterstitialAd_UseCase_LoadTest")
        
        AppTracer.startTrace("LoadInterstitialAd_GetTestConfig")
        val testConfig = AdConfig.getTestAdConfigs()[AdType.INTERSTITIAL]!!
        AppTracer.stopTrace("LoadInterstitialAd_GetTestConfig")
        
        val result = loadAd(testConfig)
        AppTracer.stopTrace("LoadInterstitialAd_UseCase_LoadTest")
        return result
    }
    
    suspend fun isAdReady(): Boolean {
        AppTracer.startTrace("CheckInterstitialAd_Ready")
        val isReady = adRepository.isAdReady(AdType.INTERSTITIAL)
        AppTracer.stopTrace("CheckInterstitialAd_Ready")
        return isReady
    }
}
