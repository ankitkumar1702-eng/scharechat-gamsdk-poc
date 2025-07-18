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
        AppTracer.startAsyncTrace("BannerUseCase_CallRepository", mapOf(
            "adUnitId" to adConfig.adUnitId,
            "adType" to adConfig.adType.name
        ))
        
        val flow = adRepository.loadBannerAd(adConfig)
            .onEach { result ->
                AppTracer.startAsyncTrace("BannerUseCase_ProcessResult", mapOf(
                    "result" to result::class.java.simpleName,
                    "adType" to adConfig.adType.name
                ))
                AppTracer.stopAsyncTrace("BannerUseCase_ProcessResult")
            }
        
        AppTracer.stopAsyncTrace("BannerUseCase_CallRepository")
        return flow
    }
    
    fun loadTestBannerAd(): Flow<AdLoadResult> {
        AppTracer.startAsyncTrace("BannerUseCase_GetTestConfig")
        val testConfig = AdConfig.getTestAdConfigs()[AdType.BANNER]!!
        AppTracer.stopAsyncTrace("BannerUseCase_GetTestConfig")
        
        AppTracer.startAsyncTrace("BannerUseCase_InvokeWithTestConfig")
        val result = invoke(testConfig)
        AppTracer.stopAsyncTrace("BannerUseCase_InvokeWithTestConfig")
        
        return result
    }
}
