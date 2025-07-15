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
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

/**
 * Simple test activity to verify GAM SDK ad display functionality
 */
class AdTestActivity : ComponentActivity() {
    
    private lateinit var adContainer: FrameLayout
    private var bannerAdView: AdView? = null
    private var interstitialAd: InterstitialAd? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppTracer.startTrace("AdTestActivity_onCreate")
        
        // Create simple UI
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(32, 32, 32, 32)
        }
        
        // Banner ad container
        adContainer = FrameLayout(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }
        
        // Load Banner button
        val loadBannerBtn = Button(this).apply {
            text = "Load Test Banner Ad"
            setOnClickListener { loadBannerAd() }
        }
        
        // Load Interstitial button
        val loadInterstitialBtn = Button(this).apply {
            text = "Load Test Interstitial Ad"
            setOnClickListener { loadInterstitialAd() }
        }
        
        // Show Interstitial button
        val showInterstitialBtn = Button(this).apply {
            text = "Show Interstitial Ad"
            setOnClickListener { showInterstitialAd() }
        }
        
        layout.addView(loadBannerBtn)
        layout.addView(loadInterstitialBtn)
        layout.addView(showInterstitialBtn)
        layout.addView(adContainer)
        
        setContentView(layout)
        AppTracer.stopTrace()
    }
    
    private fun loadBannerAd() {
        AppTracer.startTrace("AdTest_LoadBanner")
        
        // Remove previous ad if exists
        bannerAdView?.destroy()
        adContainer.removeAllViews()
        
        // Create new AdView
        bannerAdView = AdView(this).apply {
            adUnitId = "ca-app-pub-3940256099942544/9214589741" // Test banner ad unit
            setAdSize(AdSize.BANNER)
            
            adListener = object : AdListener() {
                override fun onAdLoaded() {
                    Log.d("AdTest", "Banner ad loaded successfully")
                    Toast.makeText(this@AdTestActivity, "Banner Ad Loaded!", Toast.LENGTH_SHORT).show()
                    AppTracer.startTrace("AdTest_BannerLoaded")
                    AppTracer.stopTrace()
                }
                
                override fun onAdFailedToLoad(error: LoadAdError) {
                    Log.e("AdTest", "Banner ad failed to load: ${error.message}")
                    Toast.makeText(this@AdTestActivity, "Banner Failed: ${error.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
        
        // Add to container
        adContainer.addView(bannerAdView)
        
        // Load ad
        val adRequest = AdRequest.Builder().build()
        bannerAdView?.loadAd(adRequest)
        
        AppTracer.stopTrace()
    }
    
    private fun loadInterstitialAd() {
        AppTracer.startTrace("AdTest_LoadInterstitial")
        
        val adRequest = AdRequest.Builder().build()
        
        InterstitialAd.load(
            this,
            "ca-app-pub-3940256099942544/1033173712", // Test interstitial ad unit
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    Log.d("AdTest", "Interstitial ad loaded successfully")
                    Toast.makeText(this@AdTestActivity, "Interstitial Ad Loaded!", Toast.LENGTH_SHORT).show()
                    interstitialAd = ad
                    AppTracer.startTrace("AdTest_InterstitialLoaded")
                    AppTracer.stopTrace()
                }
                
                override fun onAdFailedToLoad(error: LoadAdError) {
                    Log.e("AdTest", "Interstitial ad failed to load: ${error.message}")
                    Toast.makeText(this@AdTestActivity, "Interstitial Failed: ${error.message}", Toast.LENGTH_LONG).show()
                    interstitialAd = null
                }
            }
        )
        
        AppTracer.stopTrace()
    }
    
    private fun showInterstitialAd() {
        AppTracer.startTrace("AdTest_ShowInterstitial")
        
        if (interstitialAd != null) {
            interstitialAd?.show(this)
            Toast.makeText(this, "Showing Interstitial Ad", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "No Interstitial Ad Loaded", Toast.LENGTH_SHORT).show()
        }
        
        AppTracer.stopTrace()
    }
    
    override fun onDestroy() {
        bannerAdView?.destroy()
        super.onDestroy()
    }
}
