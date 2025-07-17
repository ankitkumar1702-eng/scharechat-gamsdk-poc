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
        AppTracer.startTrace("BannerUseCase_CallRepository", mapOf(
            "adUnitId" to adConfig.adUnitId,
            "isTestAd" to adConfig.isTestAd.toString()
        ))
        
        val flow = adRepository.loadBannerAd(adConfig)
            .onEach { result ->
                AppTracer.startTrace("BannerUseCase_ProcessResult", mapOf(
                    "result" to result::class.java.simpleName,
                    "adUnitId" to adConfig.adUnitId
                ))
                AppTracer.stopTrace("BannerUseCase_ProcessResult")
            }
        
        AppTracer.stopTrace("BannerUseCase_CallRepository")
        return flow
    }
    
    fun loadTestBannerAd(): Flow<AdLoadResult> {
        AppTracer.startTrace("BannerUseCase_GetTestConfig")
        val testConfig = AdConfig.getTestAdConfigs()[AdType.BANNER]!!
        AppTracer.stopTrace("BannerUseCase_GetTestConfig")
        
        AppTracer.startTrace("BannerUseCase_InvokeWithTestConfig")
        val result = invoke(testConfig)
        AppTracer.stopTrace("BannerUseCase_InvokeWithTestConfig")
        
        return result
    }
}
