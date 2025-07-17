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

@Singleton
class AdRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : AdRepository {

    private val loadedInterstitialAds = ConcurrentHashMap<AdType, InterstitialAd>()
    private val loadedRewardedAds = ConcurrentHashMap<AdType, RewardedAd>()
    private val loadedBannerAds = ConcurrentHashMap<AdType, AdView>()
    private val adConfigs = ConcurrentHashMap<AdType, AdConfig>()
    
    private var currentActivity: Activity? = null

    init {
        AdConfig.getTestAdConfigs().forEach { (adType, config) ->
            adConfigs[adType] = config
        }
    }
    
    fun setCurrentActivity(activity: Activity?) {
        AppTracer.traceStateChange(
            context = "AdRepository",
            fromState = currentActivity?.javaClass?.simpleName,
            toState = activity?.javaClass?.simpleName ?: "null",
            additionalData = mapOf(
                "previous_activity" to (currentActivity?.javaClass?.simpleName ?: "null"),
                "new_activity" to (activity?.javaClass?.simpleName ?: "null")
            )
        )
        
        AppTracer.startTrace("Repository_SetActivity", mapOf(
            "activity" to (activity?.javaClass?.simpleName ?: "null")
        ))
        currentActivity = activity
        AppTracer.stopTrace("Repository_SetActivity")
    }

    override fun loadBannerAd(adConfig: AdConfig): Flow<AdLoadResult> = callbackFlow {
        val currentThread = Thread.currentThread()
        android.util.Log.d("AD_THREAD", "loadBannerAd() called on thread: ${currentThread.name} (ID: ${currentThread.id})")
        
        AppTracer.startTrace("AdRepository_LoadBanner", mapOf(
            "adUnitId" to adConfig.adUnitId,
            "isTestAd" to adConfig.isTestAd.toString(),
            "thread" to currentThread.name,
            "thread_id" to currentThread.id.toString()
        ))

        AppTracer.startTrace("AdRepository_LoadBanner_SendLoading")
        trySend(AdLoadResult.Loading)
        AppTracer.stopTrace("AdRepository_LoadBanner_SendLoading")

        try {
            AppTracer.startTrace("Repository_CreateBannerAdView")
            val adView = AdView(context).apply {
                AppTracer.startTrace("Repository_SetBannerAdUnitId")
                setAdUnitId(adConfig.adUnitId)
                AppTracer.stopTrace("Repository_SetBannerAdUnitId")
                
                AppTracer.startTrace("Repository_SetBannerAdSize")
                setAdSize(AdSize.BANNER)
                AppTracer.stopTrace("Repository_SetBannerAdSize")
                
                AppTracer.startTrace("Repository_SetBannerListener")
                adListener = object : AdListener() {
                    override fun onAdLoaded() {
                        val callbackThread = Thread.currentThread()
                        android.util.Log.d("AD_THREAD", "Banner onAdLoaded() callback on thread: ${callbackThread.name} (ID: ${callbackThread.id})")
                        
                        AppTracer.traceStateChange("BannerAd", "LOADING", "LOADED", mapOf(
                            "ad_type" to adConfig.adType.name,
                            "callback_thread" to callbackThread.name
                        ))
                        
                        AppTracer.startTrace("BannerAd_OnLoaded", mapOf(
                            "callback_thread" to callbackThread.name,
                            "callback_thread_id" to callbackThread.id.toString()
                        ))
                        loadedBannerAds[adConfig.adType] = this@apply
                        trySend(AdLoadResult.Success)
                        AppTracer.stopTrace("BannerAd_OnLoaded")
                    }
                    
                    override fun onAdFailedToLoad(adError: LoadAdError) {
                        val callbackThread = Thread.currentThread()
                        android.util.Log.d("AD_THREAD", "Banner onAdFailedToLoad() callback on thread: ${callbackThread.name} (ID: ${callbackThread.id})")
                        
                        AppTracer.startTrace("BannerAd_OnFailed", mapOf(
                            "errorCode" to adError.code.toString(),
                            "errorMessage" to adError.message,
                            "callback_thread" to callbackThread.name
                        ))
                        trySend(AdLoadResult.Error(adError.code, adError.message))
                        AppTracer.stopTrace("BannerAd_OnFailed")
                    }
                    
                    override fun onAdClicked() {
                        AppTracer.startTrace("BannerAd_OnClicked")
                        AppTracer.stopTrace("BannerAd_OnClicked")
                    }
                    
                    override fun onAdImpression() {
                        AppTracer.startTrace("BannerAd_OnImpression")
                        AppTracer.stopTrace("BannerAd_OnImpression")
                    }
                }
                AppTracer.stopTrace("Repository_SetBannerListener")
            }
            AppTracer.stopTrace("Repository_CreateBannerAdView")

            AppTracer.startTrace("Repository_CreateBannerRequest")
            val adRequest = AdRequest.Builder().build()
            AppTracer.stopTrace("Repository_CreateBannerRequest")
            
            AppTracer.startTrace("Repository_LoadBannerAd")
            adView.loadAd(adRequest)
            AppTracer.stopTrace("Repository_LoadBannerAd")
            
        } catch (e: Exception) {
            AppTracer.startTrace("Repository_BannerLoadError", mapOf(
                "error" to e.message.orEmpty()
            ))
            trySend(AdLoadResult.Error(-1, e.message ?: "Unknown error"))
            AppTracer.stopTrace("Repository_BannerLoadError")
        } finally {
            AppTracer.stopTrace("Repository_LoadBanner")
        }

        awaitClose { }
    }

    override fun loadInterstitialAd(adConfig: AdConfig): Flow<AdLoadResult> = callbackFlow {
        val currentThread = Thread.currentThread()
        android.util.Log.d("AD_THREAD", "loadInterstitialAd() called on thread: ${currentThread.name} (ID: ${currentThread.id})")
        
        AppTracer.startTrace("AdRepository_LoadInterstitial", mapOf(
            "adUnitId" to adConfig.adUnitId,
            "isTestAd" to adConfig.isTestAd.toString(),
            "thread" to currentThread.name,
            "thread_id" to currentThread.id.toString()
        ))

        trySend(AdLoadResult.Loading)

        val adRequest = AdRequest.Builder().build()
        android.util.Log.d("AD_THREAD", "Calling InterstitialAd.load() on thread: ${Thread.currentThread().name}")

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
                    val callbackThread = Thread.currentThread()
                    android.util.Log.d("AD_THREAD", "Interstitial onAdLoaded() callback on thread: ${callbackThread.name} (ID: ${callbackThread.id})")
                    
                    AppTracer.startTrace("AdRepository_LoadInterstitial_Success", mapOf(
                        "callback_thread" to callbackThread.name,
                        "callback_thread_id" to callbackThread.id.toString()
                    ))
                    
                    loadedInterstitialAds[adConfig.adType] = interstitialAd
                    
                    interstitialAd.fullScreenContentCallback = object : FullScreenContentCallback() {
                        override fun onAdClicked() {
                            AppTracer.startTrace("InterstitialAd_Clicked")
                            AppTracer.stopTrace()
                        }

                        override fun onAdDismissedFullScreenContent() {
                            AppTracer.startTrace("InterstitialAd_Dismissed")
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
                    AppTracer.stopTrace()
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
                    val callbackThread = Thread.currentThread()
                    android.util.Log.d("AD_THREAD", "Interstitial onAdFailedToLoad() callback on thread: ${callbackThread.name} (ID: ${callbackThread.id})")
                    
                    AppTracer.startTrace("AdRepository_LoadInterstitial_Failed", mapOf(
                        "errorCode" to adError.code.toString(),
                        "errorMessage" to adError.message,
                        "callback_thread" to callbackThread.name,
                        "callback_thread_id" to callbackThread.id.toString()
                    ))
                    trySend(AdLoadResult.Error(adError.code, adError.message))
                    AppTracer.stopTrace("AdRepository_LoadInterstitial_Failed")
                    AppTracer.stopTrace("AdRepository_LoadInterstitial")
                }

                override fun onAdLoaded(rewardedAd: RewardedAd) {
                    AppTracer.startTrace("AdRepository_LoadRewarded_Success")
                    
                    loadedRewardedAds[adConfig.adType] = rewardedAd
                    
                    trySend(AdLoadResult.Success)
                    AppTracer.stopTrace()
                    AppTracer.stopTrace()
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
                    }
                }
            }
        }

        AppTracer.stopTrace()
    }
    
    fun getBannerAdView(adType: AdType): AdView? {
        return loadedBannerAds[adType]
    }
}
