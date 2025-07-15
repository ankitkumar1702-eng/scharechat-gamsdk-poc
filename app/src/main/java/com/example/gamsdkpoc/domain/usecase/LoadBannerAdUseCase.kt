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
 * Use case for loading banner ads with performance tracing.
 */
class LoadBannerAdUseCase @Inject constructor(
    private val adRepository: AdRepository
) {
    
    /**
     * Load a banner ad with the specified configuration.
     * 
     * @param adConfig Configuration for the banner ad
     * @return Flow emitting the ad load result
     */
    operator fun invoke(adConfig: AdConfig): Flow<AdLoadResult> {
        return AppTracer.trace("LoadBannerAd_UseCase", mapOf(
            "adUnitId" to adConfig.adUnitId,
            "isTestAd" to adConfig.isTestAd.toString()
        )) {
            adRepository.loadBannerAd(adConfig)
                .onEach { result ->
                    AppTracer.startTrace("LoadBannerAd_Result", mapOf(
                        "result" to result::class.java.simpleName,
                        "adUnitId" to adConfig.adUnitId
                    ))
                    AppTracer.stopTrace()
                }
        }
    }
    
    /**
     * Load a banner ad using default test configuration.
     * 
     * @return Flow emitting the ad load result
     */
    fun loadTestBannerAd(): Flow<AdLoadResult> {
        val testConfig = AdConfig.getTestAdConfigs()[AdType.BANNER]!!
        return invoke(testConfig)
    }
}
