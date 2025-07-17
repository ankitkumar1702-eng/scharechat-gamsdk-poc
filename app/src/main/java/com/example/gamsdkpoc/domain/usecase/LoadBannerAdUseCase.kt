package com.example.gamsdkpoc.domain.usecase

import com.example.gamsdkpoc.core.tracing.AppTracer
import com.example.gamsdkpoc.domain.model.AdConfig
import com.example.gamsdkpoc.domain.model.AdLoadResult
import com.example.gamsdkpoc.domain.model.AdType
import com.example.gamsdkpoc.domain.repository.AdRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

class LoadBannerAdUseCase @Inject constructor(
    private val adRepository: AdRepository
) {
    
    operator fun invoke(adConfig: AdConfig): Flow<AdLoadResult> {
        AppTracer.startTrace("LoadBannerAd_UseCase_Invoke", mapOf(
            "adUnitId" to adConfig.adUnitId,
            "isTestAd" to adConfig.isTestAd.toString()
        ))
        
        return adRepository.loadBannerAd(adConfig)
            .onEach { result ->
                AppTracer.startTrace("LoadBannerAd_Result", mapOf(
                    "result" to result::class.java.simpleName,
                    "adUnitId" to adConfig.adUnitId
                ))
                AppTracer.stopTrace("LoadBannerAd_Result")
            }
            .also {
                AppTracer.stopTrace("LoadBannerAd_UseCase_Invoke")
            }
    }
    
    fun loadTestBannerAd(): Flow<AdLoadResult> {
        AppTracer.startTrace("LoadBannerAd_UseCase_LoadTest")
        
        AppTracer.startTrace("LoadBannerAd_GetTestConfig")
        val testConfig = AdConfig.getTestAdConfigs()[AdType.BANNER]!!
        AppTracer.stopTrace("LoadBannerAd_GetTestConfig")
        
        val result = invoke(testConfig)
        AppTracer.stopTrace("LoadBannerAd_UseCase_LoadTest")
        return result
    }
}
