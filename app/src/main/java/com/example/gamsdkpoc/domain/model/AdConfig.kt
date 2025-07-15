package com.example.gamsdkpoc.domain.model

/**
 * Configuration class for ad units.
 * 
 * @param adUnitId The ad unit ID from AdMob/GAM
 * @param adType The type of ad
 * @param isTestAd Whether this is a test ad unit
 */
data class AdConfig(
    val adUnitId: String,
    val adType: AdType,
    val isTestAd: Boolean = true
) {
    companion object {
        // Test ad unit IDs from Google
        const val TEST_BANNER_AD_UNIT_ID = "ca-app-pub-3940256099942544/9214589741"
        const val TEST_INTERSTITIAL_AD_UNIT_ID = "ca-app-pub-3940256099942544/1033173712"
        const val TEST_REWARDED_AD_UNIT_ID = "ca-app-pub-3940256099942544/5224354917"
        const val TEST_REWARDED_INTERSTITIAL_AD_UNIT_ID = "ca-app-pub-3940256099942544/5354046379"
        const val TEST_NATIVE_AD_UNIT_ID = "ca-app-pub-3940256099942544/2247696110"
        const val TEST_APP_OPEN_AD_UNIT_ID = "ca-app-pub-3940256099942544/9257395921"
        
        /**
         * Get default test ad configurations.
         */
        fun getTestAdConfigs(): Map<AdType, AdConfig> = mapOf(
            AdType.BANNER to AdConfig(TEST_BANNER_AD_UNIT_ID, AdType.BANNER, true),
            AdType.INTERSTITIAL to AdConfig(TEST_INTERSTITIAL_AD_UNIT_ID, AdType.INTERSTITIAL, true),
            AdType.REWARDED to AdConfig(TEST_REWARDED_AD_UNIT_ID, AdType.REWARDED, true),
            AdType.REWARDED_INTERSTITIAL to AdConfig(TEST_REWARDED_INTERSTITIAL_AD_UNIT_ID, AdType.REWARDED_INTERSTITIAL, true),
            AdType.NATIVE to AdConfig(TEST_NATIVE_AD_UNIT_ID, AdType.NATIVE, true),
            AdType.APP_OPEN to AdConfig(TEST_APP_OPEN_AD_UNIT_ID, AdType.APP_OPEN, true)
        )
    }
}
