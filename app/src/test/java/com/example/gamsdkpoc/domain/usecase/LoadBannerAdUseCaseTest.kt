package com.example.gamsdkpoc.domain.usecase

import com.example.gamsdkpoc.domain.model.AdConfig
import com.example.gamsdkpoc.domain.model.AdLoadResult
import com.example.gamsdkpoc.domain.model.AdType
import com.example.gamsdkpoc.domain.repository.AdRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.Assert.assertTrue
import org.junit.Assert.assertEquals
import org.junit.Before
import io.mockk.every
import io.mockk.verify
import app.cash.turbine.test

/**
 * Unit tests for LoadBannerAdUseCase.
 */
class LoadBannerAdUseCaseTest {

    private lateinit var adRepository: AdRepository
    private lateinit var loadBannerAdUseCase: LoadBannerAdUseCase

    @Before
    fun setup() {
        adRepository = mockk()
        loadBannerAdUseCase = LoadBannerAdUseCase(adRepository)
    }

    @Test
    fun `invoke should return success when repository returns success`() = runTest {
        // Given
        val adConfig = AdConfig(
            adUnitId = "test-ad-unit-id",
            adType = AdType.BANNER,
            isTestAd = true
        )
        every { adRepository.loadBannerAd(adConfig) } returns flowOf(AdLoadResult.Success)

        // When & Then
        loadBannerAdUseCase(adConfig).test {
            val result = awaitItem()
            assertTrue(result is AdLoadResult.Success)
            awaitComplete()
        }

        verify { adRepository.loadBannerAd(adConfig) }
    }

    @Test
    fun `invoke should return error when repository returns error`() = runTest {
        // Given
        val adConfig = AdConfig(
            adUnitId = "test-ad-unit-id",
            adType = AdType.BANNER,
            isTestAd = true
        )
        val expectedError = AdLoadResult.Error(404, "Ad not found")
        every { adRepository.loadBannerAd(adConfig) } returns flowOf(expectedError)

        // When & Then
        loadBannerAdUseCase(adConfig).test {
            val result = awaitItem()
            assertTrue(result is AdLoadResult.Error)
            assertEquals(404, (result as AdLoadResult.Error).errorCode)
            assertEquals("Ad not found", result.message)
            awaitComplete()
        }

        verify { adRepository.loadBannerAd(adConfig) }
    }

    @Test
    fun `invoke should return loading when repository returns loading`() = runTest {
        // Given
        val adConfig = AdConfig(
            adUnitId = "test-ad-unit-id",
            adType = AdType.BANNER,
            isTestAd = true
        )
        every { adRepository.loadBannerAd(adConfig) } returns flowOf(AdLoadResult.Loading)

        // When & Then
        loadBannerAdUseCase(adConfig).test {
            val result = awaitItem()
            assertTrue(result is AdLoadResult.Loading)
            awaitComplete()
        }

        verify { adRepository.loadBannerAd(adConfig) }
    }

    @Test
    fun `loadTestBannerAd should use test configuration`() = runTest {
        // Given
        val testConfig = AdConfig.getTestAdConfigs()[AdType.BANNER]!!
        every { adRepository.loadBannerAd(testConfig) } returns flowOf(AdLoadResult.Success)

        // When & Then
        loadBannerAdUseCase.loadTestBannerAd().test {
            val result = awaitItem()
            assertTrue(result is AdLoadResult.Success)
            awaitComplete()
        }

        verify { adRepository.loadBannerAd(testConfig) }
    }

    @Test
    fun `invoke should handle multiple emissions from repository`() = runTest {
        // Given
        val adConfig = AdConfig(
            adUnitId = "test-ad-unit-id",
            adType = AdType.BANNER,
            isTestAd = true
        )
        every { adRepository.loadBannerAd(adConfig) } returns flowOf(
            AdLoadResult.Loading,
            AdLoadResult.Success
        )

        // When & Then
        loadBannerAdUseCase(adConfig).test {
            val loadingResult = awaitItem()
            assertTrue(loadingResult is AdLoadResult.Loading)
            
            val successResult = awaitItem()
            assertTrue(successResult is AdLoadResult.Success)
            
            awaitComplete()
        }

        verify { adRepository.loadBannerAd(adConfig) }
    }
}
