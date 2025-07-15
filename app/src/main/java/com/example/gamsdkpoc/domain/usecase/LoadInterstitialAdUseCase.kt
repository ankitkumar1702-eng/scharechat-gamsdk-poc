package com.example.gamsdkpoc.domain.usecase

import com.example.gamsdkpoc.core.tracing.AppTracer
import com.example.gamsdkpoc.domain.model.AdConfig
import com.example.gamsdkpoc.domain.model.AdLoadResult
import com.example.gamsdkpoc.domain.model.AdType
import com.example.gamsdkpoc.domain.repository.AdRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

/**
 * Use case for loading and showing interstitial ads with performance tracing.
 */
class LoadInterstitialAdUseCase @Inject constructor(
    private val adRepository: AdRepository
) {
    
    /**
     * Load an interstitial ad with the specified configuration.
     * 
     * @param adConfig Configuration for the interstitial ad
     * @return Flow emitting the ad load result
     */
    fun loadAd(adConfig: AdConfig): Flow<AdLoadResult> {
        return AppTracer.trace("LoadInterstitialAd_UseCase", mapOf(
            "adUnitId" to adConfig.adUnitId,
            "isTestAd" to adConfig.isTestAd.toString()
        )) {
            adRepository.loadInterstitialAd(adConfig)
                .onEach { result ->
                    AppTracer.startTrace("LoadInterstitialAd_Result", mapOf(
                        "result" to result::class.java.simpleName,
                        "adUnitId" to adConfig.adUnitId
                    ))
                    AppTracer.stopTrace()
                }
        }
    }
    
    /**
     * Show a loaded interstitial ad.
     * 
     * @param adType The type of interstitial ad to show
     * @return Flow emitting the show result
     */
    fun showAd(adType: AdType = AdType.INTERSTITIAL): Flow<AdLoadResult> {
        return AppTracer.trace("ShowInterstitialAd_UseCase", mapOf(
            "adType" to adType.name
        )) {
            adRepository.showInterstitialAd(adType)
                .onEach { result ->
                    AppTracer.startTrace("ShowInterstitialAd_Result", mapOf(
                        "result" to result::class.java.simpleName,
                        "adType" to adType.name
                    ))
                    AppTracer.stopTrace()
                }
        }
    }
    
    /**
     * Load an interstitial ad using default test configuration.
     * 
     * @return Flow emitting the ad load result
     */
    fun loadTestInterstitialAd(): Flow<AdLoadResult> {
        val testConfig = AdConfig.getTestAdConfigs()[AdType.INTERSTITIAL]!!
        return loadAd(testConfig)
    }
    
    /**
     * Check if an interstitial ad is ready to show.
     * 
     * @return True if the ad is ready to show
     */
    suspend fun isAdReady(): Boolean {
        return AppTracer.trace("CheckInterstitialAd_Ready") {
            adRepository.isAdReady(AdType.INTERSTITIAL)
        }
    }
}
