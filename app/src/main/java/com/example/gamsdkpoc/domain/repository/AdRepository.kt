package com.example.gamsdkpoc.domain.repository

import com.example.gamsdkpoc.domain.model.AdConfig
import com.example.gamsdkpoc.domain.model.AdLoadResult
import com.example.gamsdkpoc.domain.model.AdType
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for ad operations.
 * Defines the contract for ad loading and management operations.
 */
interface AdRepository {
    
    /**
     * Load a banner ad.
     * 
     * @param adConfig Configuration for the banner ad
     * @return Flow emitting the ad load result
     */
    fun loadBannerAd(adConfig: AdConfig): Flow<AdLoadResult>
    
    /**
     * Load an interstitial ad.
     * 
     * @param adConfig Configuration for the interstitial ad
     * @return Flow emitting the ad load result
     */
    fun loadInterstitialAd(adConfig: AdConfig): Flow<AdLoadResult>
    
    /**
     * Show an interstitial ad.
     * 
     * @param adType The type of ad to show
     * @return Flow emitting the show result
     */
    fun showInterstitialAd(adType: AdType): Flow<AdLoadResult>
    
    /**
     * Load a rewarded ad.
     * 
     * @param adConfig Configuration for the rewarded ad
     * @return Flow emitting the ad load result
     */
    fun loadRewardedAd(adConfig: AdConfig): Flow<AdLoadResult>
    
    /**
     * Show a rewarded ad.
     * 
     * @return Flow emitting the show result with reward information
     */
    fun showRewardedAd(): Flow<AdLoadResult>
    
    /**
     * Check if an ad is loaded and ready to show.
     * 
     * @param adType The type of ad to check
     * @return True if the ad is ready to show
     */
    suspend fun isAdReady(adType: AdType): Boolean
    
    /**
     * Get ad configuration for a specific ad type.
     * 
     * @param adType The type of ad
     * @return AdConfig for the specified type
     */
    suspend fun getAdConfig(adType: AdType): AdConfig?
    
    /**
     * Preload ads for better performance.
     * 
     * @param adTypes List of ad types to preload
     */
    suspend fun preloadAds(adTypes: List<AdType>)
}
