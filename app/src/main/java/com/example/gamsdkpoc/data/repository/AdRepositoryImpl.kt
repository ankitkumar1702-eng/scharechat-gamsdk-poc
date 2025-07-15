package com.example.gamsdkpoc.data.repository

import android.app.Activity
import android.content.Context
import com.example.gamsdkpoc.core.tracing.AppTracer
import com.example.gamsdkpoc.domain.model.AdConfig
import com.example.gamsdkpoc.domain.model.AdLoadResult
import com.example.gamsdkpoc.domain.model.AdType
import com.example.gamsdkpoc.domain.repository.AdRepository
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOf
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of AdRepository with GAM SDK integration and performance tracing.
 */
@Singleton
class AdRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : AdRepository {

    // Cache for loaded ads
    private val loadedInterstitialAds = ConcurrentHashMap<AdType, InterstitialAd>()
    private val loadedRewardedAds = ConcurrentHashMap<AdType, RewardedAd>()
    private val loadedBannerAds = ConcurrentHashMap<AdType, AdView>()
    private val adConfigs = ConcurrentHashMap<AdType, AdConfig>()
    
    // Store current activity reference for showing ads
    private var currentActivity: Activity? = null

    init {
        // Initialize with test ad configurations
        AdConfig.getTestAdConfigs().forEach { (adType, config) ->
            adConfigs[adType] = config
        }
    }
    
    /**
     * Set the current activity for showing ads.
     * This should be called from the activity's onCreate/onResume.
     */
    fun setCurrentActivity(activity: Activity?) {
        currentActivity = activity
    }

    override fun loadBannerAd(adConfig: AdConfig): Flow<AdLoadResult> = callbackFlow {
        AppTracer.startTrace("AdRepository_LoadBanner", mapOf(
            "adUnitId" to adConfig.adUnitId,
            "isTestAd" to adConfig.isTestAd.toString()
        ))

        trySend(AdLoadResult.Loading)

        try {
            val adView = AdView(context).apply {
                setAdUnitId(adConfig.adUnitId)
                setAdSize(AdSize.BANNER)
                
                adListener = object : AdListener() {
                    override fun onAdLoaded() {
                        AppTracer.startTrace("AdRepository_LoadBanner_Success")
                        loadedBannerAds[adConfig.adType] = this@apply
                        trySend(AdLoadResult.Success)
                        AppTracer.stopTrace()
                    }
                    
                    override fun onAdFailedToLoad(adError: LoadAdError) {
                        AppTracer.startTrace("AdRepository_LoadBanner_Failed", mapOf(
                            "errorCode" to adError.code.toString(),
                            "errorMessage" to adError.message
                        ))
                        trySend(AdLoadResult.Error(adError.code, adError.message))
                        AppTracer.stopTrace()
                    }
                    
                    override fun onAdClicked() {
                        AppTracer.startTrace("BannerAd_Clicked")
                        AppTracer.stopTrace()
                    }
                    
                    override fun onAdImpression() {
                        AppTracer.startTrace("BannerAd_Impression")
                        AppTracer.stopTrace()
                    }
                }
            }

            val adRequest = AdRequest.Builder().build()
            adView.loadAd(adRequest)
            
        } catch (e: Exception) {
            AppTracer.startTrace("AdRepository_LoadBanner_Error", mapOf(
                "error" to e.message.orEmpty()
            ))
            trySend(AdLoadResult.Error(-1, e.message ?: "Unknown error"))
            AppTracer.stopTrace()
        } finally {
            AppTracer.stopTrace()
        }

        awaitClose { }
    }

    override fun loadInterstitialAd(adConfig: AdConfig): Flow<AdLoadResult> = callbackFlow {
        AppTracer.startTrace("AdRepository_LoadInterstitial", mapOf(
            "adUnitId" to adConfig.adUnitId,
            "isTestAd" to adConfig.isTestAd.toString()
        ))

        trySend(AdLoadResult.Loading)

        val adRequest = AdRequest.Builder().build()

        InterstitialAd.load(
            context,
            adConfig.adUnitId,
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    AppTracer.startTrace("AdRepository_LoadInterstitial_Failed", mapOf(
                        "errorCode" to adError.code.toString(),
                        "errorMessage" to adError.message
                    ))
                    trySend(AdLoadResult.Error(adError.code, adError.message))
                    AppTracer.stopTrace()
                    AppTracer.stopTrace() // End main trace
                }

                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    AppTracer.startTrace("AdRepository_LoadInterstitial_Success")
                    
                    // Cache the loaded ad
                    loadedInterstitialAds[adConfig.adType] = interstitialAd
                    
                    // Set up full screen content callback for show events
                    interstitialAd.fullScreenContentCallback = object : FullScreenContentCallback() {
                        override fun onAdClicked() {
                            AppTracer.startTrace("InterstitialAd_Clicked")
                            AppTracer.stopTrace()
                        }

                        override fun onAdDismissedFullScreenContent() {
                            AppTracer.startTrace("InterstitialAd_Dismissed")
                            // Remove from cache after showing
                            loadedInterstitialAds.remove(adConfig.adType)
                            AppTracer.stopTrace()
                        }

                        override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                            AppTracer.startTrace("InterstitialAd_ShowFailed", mapOf(
                                "errorCode" to adError.code.toString(),
                                "errorMessage" to adError.message
                            ))
                            loadedInterstitialAds.remove(adConfig.adType)
                            AppTracer.stopTrace()
                        }

                        override fun onAdImpression() {
                            AppTracer.startTrace("InterstitialAd_Impression")
                            AppTracer.stopTrace()
                        }

                        override fun onAdShowedFullScreenContent() {
                            AppTracer.startTrace("InterstitialAd_Showed")
                            AppTracer.stopTrace()
                        }
                    }
                    
                    trySend(AdLoadResult.Success)
                    AppTracer.stopTrace()
                    AppTracer.stopTrace() // End main trace
                }
            }
        )

        awaitClose { }
    }

    override fun showInterstitialAd(adType: AdType): Flow<AdLoadResult> = callbackFlow {
        AppTracer.startTrace("AdRepository_ShowInterstitial", mapOf(
            "adType" to adType.name
        ))

        val interstitialAd = loadedInterstitialAds[adType]
        val activity = currentActivity
        
        when {
            activity == null -> {
                trySend(AdLoadResult.Error(-1, "No activity context available"))
            }
            interstitialAd != null -> {
                try {
                    // Actually show the interstitial ad
                    interstitialAd.show(activity)
                    trySend(AdLoadResult.Success)
                } catch (e: Exception) {
                    trySend(AdLoadResult.Error(-1, e.message ?: "Failed to show ad"))
                }
            }
            else -> {
                trySend(AdLoadResult.Error(-1, "No loaded ad available"))
            }
        }

        AppTracer.stopTrace()
        awaitClose { }
    }

    override fun loadRewardedAd(adConfig: AdConfig): Flow<AdLoadResult> = callbackFlow {
        AppTracer.startTrace("AdRepository_LoadRewarded", mapOf(
            "adUnitId" to adConfig.adUnitId,
            "isTestAd" to adConfig.isTestAd.toString()
        ))

        trySend(AdLoadResult.Loading)

        val adRequest = AdRequest.Builder().build()

        RewardedAd.load(
            context,
            adConfig.adUnitId,
            adRequest,
            object : RewardedAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    AppTracer.startTrace("AdRepository_LoadRewarded_Failed", mapOf(
                        "errorCode" to adError.code.toString(),
                        "errorMessage" to adError.message
                    ))
                    trySend(AdLoadResult.Error(adError.code, adError.message))
                    AppTracer.stopTrace()
                    AppTracer.stopTrace() // End main trace
                }

                override fun onAdLoaded(rewardedAd: RewardedAd) {
                    AppTracer.startTrace("AdRepository_LoadRewarded_Success")
                    
                    // Cache the loaded ad
                    loadedRewardedAds[adConfig.adType] = rewardedAd
                    
                    trySend(AdLoadResult.Success)
                    AppTracer.stopTrace()
                    AppTracer.stopTrace() // End main trace
                }
            }
        )

        awaitClose { }
    }

    override fun showRewardedAd(): Flow<AdLoadResult> = callbackFlow {
        AppTracer.startTrace("AdRepository_ShowRewarded")

        val rewardedAd = loadedRewardedAds[AdType.REWARDED]
        val activity = currentActivity
        
        when {
            activity == null -> {
                trySend(AdLoadResult.Error(-1, "No activity context available"))
            }
            rewardedAd != null -> {
                try {
                    // Show the rewarded ad with reward callback
                    rewardedAd.show(activity) { rewardItem ->
                        AppTracer.startTrace("RewardedAd_UserEarnedReward", mapOf(
                            "type" to rewardItem.type,
                            "amount" to rewardItem.amount.toString()
                        ))
                        AppTracer.stopTrace()
                    }
                    trySend(AdLoadResult.Success)
                } catch (e: Exception) {
                    trySend(AdLoadResult.Error(-1, e.message ?: "Failed to show rewarded ad"))
                }
            }
            else -> {
                trySend(AdLoadResult.Error(-1, "No loaded rewarded ad available"))
            }
        }

        AppTracer.stopTrace()
        awaitClose { }
    }

    override suspend fun isAdReady(adType: AdType): Boolean {
        return AppTracer.trace("AdRepository_IsAdReady", mapOf("adType" to adType.name)) {
            when (adType) {
                AdType.INTERSTITIAL, AdType.REWARDED_INTERSTITIAL -> {
                    loadedInterstitialAds.containsKey(adType)
                }
                AdType.REWARDED -> {
                    loadedRewardedAds.containsKey(adType)
                }
                AdType.BANNER -> {
                    loadedBannerAds.containsKey(adType)
                }
                else -> false
            }
        }
    }

    override suspend fun getAdConfig(adType: AdType): AdConfig? {
        return AppTracer.trace("AdRepository_GetAdConfig", mapOf("adType" to adType.name)) {
            adConfigs[adType]
        }
    }

    override suspend fun preloadAds(adTypes: List<AdType>) {
        AppTracer.startTrace("AdRepository_PreloadAds", mapOf(
            "adTypes" to adTypes.joinToString(",") { it.name },
            "count" to adTypes.size.toString()
        ))

        adTypes.forEach { adType ->
            val config = adConfigs[adType]
            if (config != null) {
                when (adType) {
                    AdType.INTERSTITIAL, AdType.REWARDED_INTERSTITIAL -> {
                        loadInterstitialAd(config)
                    }
                    AdType.REWARDED -> {
                        loadRewardedAd(config)
                    }
                    AdType.BANNER -> {
                        loadBannerAd(config)
                    }
                    else -> {
                        // Other ad types
                    }
                }
            }
        }

        AppTracer.stopTrace()
    }
    
    /**
     * Get the loaded banner AdView for display in the UI.
     */
    fun getBannerAdView(adType: AdType): AdView? {
        return loadedBannerAds[adType]
    }
}
