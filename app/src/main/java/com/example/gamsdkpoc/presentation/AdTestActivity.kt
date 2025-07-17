package com.example.gamsdkpoc.presentation

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.example.gamsdkpoc.core.tracing.AppTracer
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

class AdTestActivity : ComponentActivity() {
    
    private lateinit var adContainer: FrameLayout
    private var bannerAdView: AdView? = null
    private var interstitialAd: InterstitialAd? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        AppTracer.startTrace("AdTestActivity_onCreate")
        super.onCreate(savedInstanceState)
        
        AppTracer.startTrace("AdTestActivity_CreateLayout")
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(32, 32, 32, 32)
        }
        AppTracer.stopTrace("AdTestActivity_CreateLayout")
        
        AppTracer.startTrace("AdTestActivity_CreateAdContainer")
        adContainer = FrameLayout(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }
        AppTracer.stopTrace("AdTestActivity_CreateAdContainer")
        
        AppTracer.startTrace("AdTestActivity_CreateButtons")
        val loadBannerBtn = Button(this).apply {
            text = "Load Test Banner Ad"
            setOnClickListener { 
                AppTracer.startTrace("AdTestActivity_BannerButtonClick")
                loadBannerAd()
                AppTracer.stopTrace("AdTestActivity_BannerButtonClick")
            }
        }
        
        val loadInterstitialBtn = Button(this).apply {
            text = "Load Test Interstitial Ad"
            setOnClickListener { 
                AppTracer.startTrace("AdTestActivity_InterstitialButtonClick")
                loadInterstitialAd()
                AppTracer.stopTrace("AdTestActivity_InterstitialButtonClick")
            }
        }
        
        val showInterstitialBtn = Button(this).apply {
            text = "Show Interstitial Ad"
            setOnClickListener { 
                AppTracer.startTrace("AdTestActivity_ShowButtonClick")
                showInterstitialAd()
                AppTracer.stopTrace("AdTestActivity_ShowButtonClick")
            }
        }
        AppTracer.stopTrace("AdTestActivity_CreateButtons")
        
        AppTracer.startTrace("AdTestActivity_AddViewsToLayout")
        layout.addView(loadBannerBtn)
        layout.addView(loadInterstitialBtn)
        layout.addView(showInterstitialBtn)
        layout.addView(adContainer)
        AppTracer.stopTrace("AdTestActivity_AddViewsToLayout")
        
        AppTracer.startTrace("AdTestActivity_SetContentView")
        setContentView(layout)
        AppTracer.stopTrace("AdTestActivity_SetContentView")
        
        AppTracer.stopTrace("AdTestActivity_onCreate")
    }
    
    private fun loadBannerAd() {
        AppTracer.startTrace("AdTest_LoadBanner")
        
        AppTracer.startTrace("AdTest_CleanupPreviousAd")
        bannerAdView?.destroy()
        adContainer.removeAllViews()
        AppTracer.stopTrace("AdTest_CleanupPreviousAd")
        
        AppTracer.startTrace("AdTest_CreateBannerAdView")
        bannerAdView = AdView(this).apply {
            AppTracer.startTrace("AdTest_SetBannerAdUnitId")
            adUnitId = "ca-app-pub-3940256099942544/9214589741"
            AppTracer.stopTrace("AdTest_SetBannerAdUnitId")
            
            AppTracer.startTrace("AdTest_SetBannerAdSize")
            setAdSize(AdSize.BANNER)
            AppTracer.stopTrace("AdTest_SetBannerAdSize")
            
            AppTracer.startTrace("AdTest_SetBannerListener")
            adListener = object : AdListener() {
                override fun onAdLoaded() {
                    AppTracer.startTrace("AdTest_BannerLoaded")
                    Log.d("AdTest", "Banner ad loaded successfully")
                    Toast.makeText(this@AdTestActivity, "Banner Ad Loaded!", Toast.LENGTH_SHORT).show()
                    AppTracer.stopTrace("AdTest_BannerLoaded")
                }
                
                override fun onAdFailedToLoad(error: LoadAdError) {
                    AppTracer.startTrace("AdTest_BannerFailed", mapOf(
                        "errorCode" to error.code.toString(),
                        "errorMessage" to error.message
                    ))
                    Log.e("AdTest", "Banner ad failed to load: ${error.message}")
                    Toast.makeText(this@AdTestActivity, "Banner Failed: ${error.message}", Toast.LENGTH_LONG).show()
                    AppTracer.stopTrace("AdTest_BannerFailed")
                }
                
                override fun onAdClicked() {
                    AppTracer.startTrace("AdTest_BannerClicked")
                    AppTracer.stopTrace("AdTest_BannerClicked")
                }
                
                override fun onAdImpression() {
                    AppTracer.startTrace("AdTest_BannerImpression")
                    AppTracer.stopTrace("AdTest_BannerImpression")
                }
            }
            AppTracer.stopTrace("AdTest_SetBannerListener")
        }
        AppTracer.stopTrace("AdTest_CreateBannerAdView")
        
        AppTracer.startTrace("AdTest_AddBannerToContainer")
        adContainer.addView(bannerAdView)
        AppTracer.stopTrace("AdTest_AddBannerToContainer")
        
        AppTracer.startTrace("AdTest_CreateBannerRequest")
        val adRequest = AdRequest.Builder().build()
        AppTracer.stopTrace("AdTest_CreateBannerRequest")
        
        AppTracer.startTrace("AdTest_LoadBannerRequest")
        bannerAdView?.loadAd(adRequest)
        AppTracer.stopTrace("AdTest_LoadBannerRequest")
        
        AppTracer.stopTrace("AdTest_LoadBanner")
    }
    
    private fun loadInterstitialAd() {
        AppTracer.startTrace("AdTest_LoadInterstitial")
        
        AppTracer.startTrace("AdTest_CreateInterstitialRequest")
        val adRequest = AdRequest.Builder().build()
        AppTracer.stopTrace("AdTest_CreateInterstitialRequest")
        
        AppTracer.startTrace("AdTest_LoadInterstitialAd")
        InterstitialAd.load(
            this,
            "ca-app-pub-3940256099942544/1033173712",
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    AppTracer.startTrace("AdTest_InterstitialLoaded")
                    Log.d("AdTest", "Interstitial ad loaded successfully")
                    Toast.makeText(this@AdTestActivity, "Interstitial Ad Loaded!", Toast.LENGTH_SHORT).show()
                    interstitialAd = ad
                    
                    AppTracer.startTrace("AdTest_SetInterstitialCallbacks")
                    ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                        override fun onAdClicked() {
                            AppTracer.startTrace("AdTest_InterstitialClicked")
                            AppTracer.stopTrace("AdTest_InterstitialClicked")
                        }
                        
                        override fun onAdDismissedFullScreenContent() {
                            AppTracer.startTrace("AdTest_InterstitialDismissed")
                            interstitialAd = null
                            AppTracer.stopTrace("AdTest_InterstitialDismissed")
                        }
                        
                        override fun onAdImpression() {
                            AppTracer.startTrace("AdTest_InterstitialImpression")
                            AppTracer.stopTrace("AdTest_InterstitialImpression")
                        }
                        
                        override fun onAdShowedFullScreenContent() {
                            AppTracer.startTrace("AdTest_InterstitialShowed")
                            AppTracer.stopTrace("AdTest_InterstitialShowed")
                        }
                    }
                    AppTracer.stopTrace("AdTest_SetInterstitialCallbacks")
                    AppTracer.stopTrace("AdTest_InterstitialLoaded")
                }
                
                override fun onAdFailedToLoad(error: LoadAdError) {
                    AppTracer.startTrace("AdTest_InterstitialFailed", mapOf(
                        "errorCode" to error.code.toString(),
                        "errorMessage" to error.message
                    ))
                    Log.e("AdTest", "Interstitial ad failed to load: ${error.message}")
                    Toast.makeText(this@AdTestActivity, "Interstitial Failed: ${error.message}", Toast.LENGTH_LONG).show()
                    interstitialAd = null
                    AppTracer.stopTrace("AdTest_InterstitialFailed")
                }
            }
        )
        AppTracer.stopTrace("AdTest_LoadInterstitialAd")
        
        AppTracer.stopTrace("AdTest_LoadInterstitial")
    }
    
    private fun showInterstitialAd() {
        AppTracer.startTrace("AdTest_ShowInterstitial")
        
        AppTracer.startTrace("AdTest_CheckInterstitialAvailable")
        if (interstitialAd != null) {
            AppTracer.stopTrace("AdTest_CheckInterstitialAvailable")
            
            AppTracer.startTrace("AdTest_ShowInterstitialAd")
            interstitialAd?.show(this)
            Toast.makeText(this, "Showing Interstitial Ad", Toast.LENGTH_SHORT).show()
            AppTracer.stopTrace("AdTest_ShowInterstitialAd")
        } else {
            AppTracer.stopTrace("AdTest_CheckInterstitialAvailable")
            
            AppTracer.startTrace("AdTest_NoInterstitialAvailable")
            Toast.makeText(this, "No Interstitial Ad Loaded", Toast.LENGTH_SHORT).show()
            AppTracer.stopTrace("AdTest_NoInterstitialAvailable")
        }
        
        AppTracer.stopTrace("AdTest_ShowInterstitial")
    }
    
    override fun onDestroy() {
        AppTracer.startTrace("AdTestActivity_onDestroy")
        
        AppTracer.startTrace("AdTestActivity_DestroyBannerAd")
        bannerAdView?.destroy()
        AppTracer.stopTrace("AdTestActivity_DestroyBannerAd")
        
        super.onDestroy()
        AppTracer.stopTrace("AdTestActivity_onDestroy")
    }
}
