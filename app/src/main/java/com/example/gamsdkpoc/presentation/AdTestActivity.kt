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
        AppTracer.startAsyncTrace("AdTestActivity_onCreate")
        super.onCreate(savedInstanceState)

        AppTracer.startAsyncTrace("AdTestActivity_CreateLayout")
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(32, 32, 32, 32)
        }
        AppTracer.stopAsyncTrace("AdTestActivity_CreateLayout")
        
        AppTracer.startAsyncTrace("AdTestActivity_CreateAdContainer")
        adContainer = FrameLayout(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }
        AppTracer.stopAsyncTrace("AdTestActivity_CreateAdContainer")
        
        AppTracer.startAsyncTrace("AdTestActivity_CreateButtons")
        val loadBannerBtn = Button(this).apply {
            text = "Load Test Banner Ad"
            setOnClickListener { 
                AppTracer.startAsyncTrace("AdTestActivity_BannerButtonClick")
                loadBannerAd()
                AppTracer.stopAsyncTrace("AdTestActivity_BannerButtonClick")
            }
        }
        
        val loadInterstitialBtn = Button(this).apply {
            text = "Load Test Interstitial Ad"
            setOnClickListener { 
                AppTracer.startAsyncTrace("AdTestActivity_InterstitialButtonClick")
                loadInterstitialAd()
                AppTracer.stopAsyncTrace("AdTestActivity_InterstitialButtonClick")
            }
        }
        
        val showInterstitialBtn = Button(this).apply {
            text = "Show Interstitial Ad"
            setOnClickListener { 
                AppTracer.startAsyncTrace("AdTestActivity_ShowButtonClick")
                showInterstitialAd()
                AppTracer.stopAsyncTrace("AdTestActivity_ShowButtonClick")
            }
        }
        AppTracer.stopAsyncTrace("AdTestActivity_CreateButtons")
        
        AppTracer.startAsyncTrace("AdTestActivity_AddViewsToLayout")
        layout.addView(loadBannerBtn)
        layout.addView(loadInterstitialBtn)
        layout.addView(showInterstitialBtn)
        layout.addView(adContainer)
        AppTracer.stopAsyncTrace("AdTestActivity_AddViewsToLayout")
        
        AppTracer.startAsyncTrace("AdTestActivity_SetContentView")
        setContentView(layout)
        AppTracer.stopAsyncTrace("AdTestActivity_SetContentView")
        
        AppTracer.stopAsyncTrace("AdTestActivity_onCreate")
    }
    
    private fun loadBannerAd() {
        AppTracer.startAsyncTrace("AdTest_LoadBanner")
        
        AppTracer.startAsyncTrace("AdTest_CleanupPreviousAd")
        bannerAdView?.destroy()
        adContainer.removeAllViews()
        AppTracer.stopAsyncTrace("AdTest_CleanupPreviousAd")
        
        AppTracer.startAsyncTrace("AdTest_CreateBannerAdView")
        bannerAdView = AdView(this).apply {
            AppTracer.startAsyncTrace("AdTest_SetBannerAdUnitId")
            adUnitId = "ca-app-pub-3940256099942544/9214589741"
            AppTracer.stopAsyncTrace("AdTest_SetBannerAdUnitId")
            
            AppTracer.startAsyncTrace("AdTest_SetBannerAdSize")
            setAdSize(AdSize.BANNER)
            AppTracer.stopAsyncTrace("AdTest_SetBannerAdSize")
            
            AppTracer.startAsyncTrace("AdTest_SetBannerListener")
            adListener = object : AdListener() {
                override fun onAdLoaded() {
                    AppTracer.startAsyncTrace("AdTest_BannerLoaded")
                    Log.d("AdTest", "Banner ad loaded successfully")
                    Toast.makeText(this@AdTestActivity, "Banner Ad Loaded!", Toast.LENGTH_SHORT).show()
                    AppTracer.stopAsyncTrace("AdTest_BannerLoaded")
                }
                
                override fun onAdFailedToLoad(error: LoadAdError) {
                    AppTracer.startAsyncTrace("AdTest_BannerFailed", mapOf(
                        "errorCode" to error.code.toString(),
                        "errorMessage" to error.message
                    ))
                    Log.e("AdTest", "Banner ad failed to load: ${error.message}")
                    Toast.makeText(this@AdTestActivity, "Banner Failed: ${error.message}", Toast.LENGTH_LONG).show()
                    AppTracer.stopAsyncTrace("AdTest_BannerFailed")
                }
                
                override fun onAdClicked() {
                    AppTracer.startAsyncTrace("AdTest_BannerClicked")
                    AppTracer.stopAsyncTrace("AdTest_BannerClicked")
                }
                
                override fun onAdImpression() {
                    AppTracer.startAsyncTrace("AdTest_BannerImpression")
                    AppTracer.stopAsyncTrace("AdTest_BannerImpression")
                }
            }
            AppTracer.stopAsyncTrace("AdTest_SetBannerListener")
        }
        AppTracer.stopAsyncTrace("AdTest_CreateBannerAdView")
        
        AppTracer.startAsyncTrace("AdTest_AddBannerToContainer")
        adContainer.addView(bannerAdView)
        AppTracer.stopAsyncTrace("AdTest_AddBannerToContainer")
        
        AppTracer.startAsyncTrace("AdTest_CreateBannerRequest")
        val adRequest = AdRequest.Builder().build()
        AppTracer.stopAsyncTrace("AdTest_CreateBannerRequest")
        
        AppTracer.startAsyncTrace("AdTest_LoadBannerRequest")
        bannerAdView?.loadAd(adRequest)
        AppTracer.stopAsyncTrace("AdTest_LoadBannerRequest")
        
        AppTracer.stopAsyncTrace("AdTest_LoadBanner")
    }
    
    private fun loadInterstitialAd() {
        AppTracer.startAsyncTrace("AdTest_LoadInterstitial")
        
        AppTracer.startAsyncTrace("AdTest_CreateInterstitialRequest")
        val adRequest = AdRequest.Builder().build()
        AppTracer.stopAsyncTrace("AdTest_CreateInterstitialRequest")
        
        AppTracer.startAsyncTrace("AdTest_LoadInterstitialAd")
        InterstitialAd.load(
            this,
            "ca-app-pub-3940256099942544/1033173712",
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    AppTracer.startAsyncTrace("AdTest_InterstitialLoaded")
                    Log.d("AdTest", "Interstitial ad loaded successfully")
                    Toast.makeText(this@AdTestActivity, "Interstitial Ad Loaded!", Toast.LENGTH_SHORT).show()
                    interstitialAd = ad
                    
                    AppTracer.startAsyncTrace("AdTest_SetInterstitialCallbacks")
                    ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                        override fun onAdClicked() {
                            AppTracer.startAsyncTrace("AdTest_InterstitialClicked")
                            AppTracer.stopAsyncTrace("AdTest_InterstitialClicked")
                        }
                        
                        override fun onAdDismissedFullScreenContent() {
                            AppTracer.startAsyncTrace("AdTest_InterstitialDismissed")
                            interstitialAd = null
                            AppTracer.stopAsyncTrace("AdTest_InterstitialDismissed")
                        }
                        
                        override fun onAdImpression() {
                            AppTracer.startAsyncTrace("AdTest_InterstitialImpression")
                            AppTracer.stopAsyncTrace("AdTest_InterstitialImpression")
                        }
                        
                        override fun onAdShowedFullScreenContent() {
                            AppTracer.startAsyncTrace("AdTest_InterstitialShowed")
                            AppTracer.stopAsyncTrace("AdTest_InterstitialShowed")
                        }
                    }
                    AppTracer.stopAsyncTrace("AdTest_SetInterstitialCallbacks")
                    AppTracer.stopAsyncTrace("AdTest_InterstitialLoaded")
                }
                
                override fun onAdFailedToLoad(error: LoadAdError) {
                    AppTracer.startAsyncTrace("AdTest_InterstitialFailed", mapOf(
                        "errorCode" to error.code.toString(),
                        "errorMessage" to error.message
                    ))
                    Log.e("AdTest", "Interstitial ad failed to load: ${error.message}")
                    Toast.makeText(this@AdTestActivity, "Interstitial Failed: ${error.message}", Toast.LENGTH_LONG).show()
                    interstitialAd = null
                    AppTracer.stopAsyncTrace("AdTest_InterstitialFailed")
                }
            }
        )
        AppTracer.stopAsyncTrace("AdTest_LoadInterstitialAd")
        
        AppTracer.stopAsyncTrace("AdTest_LoadInterstitial")
    }
    
    private fun showInterstitialAd() {
        AppTracer.startAsyncTrace("AdTest_ShowInterstitial")
        
        AppTracer.startAsyncTrace("AdTest_CheckInterstitialAvailable")
        if (interstitialAd != null) {
            AppTracer.stopAsyncTrace("AdTest_CheckInterstitialAvailable")
            
            AppTracer.startAsyncTrace("AdTest_ShowInterstitialAd")
            interstitialAd?.show(this)
            Toast.makeText(this, "Showing Interstitial Ad", Toast.LENGTH_SHORT).show()
            AppTracer.stopAsyncTrace("AdTest_ShowInterstitialAd")
        } else {
            AppTracer.stopAsyncTrace("AdTest_CheckInterstitialAvailable")
            
            AppTracer.startAsyncTrace("AdTest_NoInterstitialAvailable")
            Toast.makeText(this, "No Interstitial Ad Loaded", Toast.LENGTH_SHORT).show()
            AppTracer.stopAsyncTrace("AdTest_NoInterstitialAvailable")
        }
        
        AppTracer.stopAsyncTrace("AdTest_ShowInterstitial")
    }
    
    override fun onDestroy() {
        AppTracer.startAsyncTrace("AdTestActivity_onDestroy")
        
        AppTracer.startAsyncTrace("AdTestActivity_DestroyBannerAd")
        bannerAdView?.destroy()
        AppTracer.stopAsyncTrace("AdTestActivity_DestroyBannerAd")
        
        super.onDestroy()
        AppTracer.stopAsyncTrace("AdTestActivity_onDestroy")
    }
}
