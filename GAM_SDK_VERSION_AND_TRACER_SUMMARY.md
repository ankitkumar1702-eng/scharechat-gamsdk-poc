# GAM SDK PoC - Version Information & Tracer Tags Summary

## 📱 **Android & SDK Versions**

### **Core Android Versions:**
- **Target SDK:** `35` (Android 15)
- **Min SDK:** `24` (Android 7.0)
- **Compile SDK:** `35` (Android 15)
- **Java Target:** `11`

### **🎯 GAM SDK Version:**
- **GAM SDK:** `24.4.0` (Latest stable)
- **Package:** `com.google.android.gms:play-services-ads:24.4.0`

### **⚡ Build Tools & Languages:**
- **AGP (Android Gradle Plugin):** `8.10.1`
- **Kotlin:** `2.1.0`
- **KSP:** `2.1.0-1.0.29`
- **Compose BOM:** `2024.12.01`

### **🏗️ Architecture Libraries:**
- **Hilt:** `2.53.1` (Dependency Injection)
- **Coroutines:** `1.8.1`
- **Lifecycle:** `2.9.1`
- **Navigation Compose:** `2.8.5`

### **📊 Performance Tracing:**
- **AndroidX Tracing:** `1.2.0`
- **Perfetto:** Native Android tracing (built-in)

---

## 🏷️ **AppTracer Tags Used in Project**

### **1. Application Lifecycle Tags:**
```kotlin
// App startup and lifecycle
"App_Startup"                    // Application onCreate
"App_LowMemory"                  // Low memory events
"App_TrimMemory"                 // Memory trimming events
```

### **2. GAM SDK Initialization Tags:**
```kotlin
// GAM SDK initialization tracking
"GAM_SDK_Initialization"         // Main GAM SDK init
"GAM_SDK_Initialization_Complete" // Init completion callback
```

### **3. Activity Lifecycle Tags:**
```kotlin
// Base activity lifecycle (dynamic with activity name)
"${activityName}_onCreate"       // Activity creation
"${activityName}_onStart"        // Activity start
"${activityName}_onResume"       // Activity resume
"${activityName}_onPause"        // Activity pause
"${activityName}_onStop"         // Activity stop
"${activityName}_onDestroy"      // Activity destruction
"${activityName}_setContent"     // Compose content setting
"${activityName}_Compose"        // Compose UI rendering
```

### **4. Ad Repository Operations:**
```kotlin
// Banner Ad Operations
"AdRepository_LoadBanner"        // Banner ad loading
"AdRepository_LoadBanner_Success" // Banner load success
"AdRepository_LoadBanner_Failed" // Banner load failure
"AdRepository_LoadBanner_Error"  // Banner load error

// Interstitial Ad Operations
"AdRepository_LoadInterstitial"  // Interstitial ad loading
"AdRepository_LoadInterstitial_Success" // Interstitial load success
"AdRepository_LoadInterstitial_Failed"  // Interstitial load failure
"AdRepository_ShowInterstitial"  // Interstitial ad showing

// Rewarded Ad Operations
"AdRepository_LoadRewarded"      // Rewarded ad loading
"AdRepository_LoadRewarded_Success" // Rewarded load success
"AdRepository_LoadRewarded_Failed"  // Rewarded load failure
"AdRepository_ShowRewarded"      // Rewarded ad showing

// General Repository Operations
"AdRepository_IsAdReady"         // Check ad readiness
"AdRepository_GetAdConfig"       // Get ad configuration
"AdRepository_PreloadAds"        // Preload multiple ads
```

### **5. Ad Event Tracking:**
```kotlin
// Banner Ad Events
"BannerAd_Clicked"              // Banner ad click
"BannerAd_Impression"           // Banner ad impression

// Interstitial Ad Events
"InterstitialAd_Clicked"        // Interstitial ad click
"InterstitialAd_Dismissed"      // Interstitial ad dismissed
"InterstitialAd_ShowFailed"     // Interstitial show failure
"InterstitialAd_Impression"     // Interstitial ad impression
"InterstitialAd_Showed"         // Interstitial ad shown

// Rewarded Ad Events
"RewardedAd_UserEarnedReward"   // User earned reward
```

### **6. ViewModel Operations:**
```kotlin
// ViewModel lifecycle
"MainViewModel_Init"            // ViewModel initialization
"MainViewModel_Cleared"         // ViewModel cleanup

// Ad loading operations
"MainViewModel_LoadBannerAd"    // Banner ad load request
"MainViewModel_LoadInterstitialAd" // Interstitial ad load request
"MainViewModel_ShowInterstitialAd" // Interstitial ad show request

// Result handling
"MainViewModel_BannerAdResult"  // Banner ad result processing
"MainViewModel_InterstitialAdResult" // Interstitial ad result processing
"MainViewModel_ShowInterstitialResult" // Show interstitial result

// User interactions
"MainViewModel_UserAction"      // User action processing
```

### **7. Use Case Operations:**
```kotlin
// Banner Ad Use Case
"LoadBannerAd_UseCase"          // Banner ad use case execution
"LoadBannerAd_Result"           // Banner ad use case result

// Interstitial Ad Use Case
"LoadInterstitialAd_UseCase"    // Interstitial ad use case execution
"LoadInterstitialAd_Result"     // Interstitial ad use case result
"ShowInterstitialAd_UseCase"    // Show interstitial use case
"ShowInterstitialAd_Result"     // Show interstitial result
"CheckInterstitialAd_Ready"     // Check interstitial readiness
```

### **8. UI Interaction Tags:**
```kotlin
// Main screen UI
"MainScreen_Compose"            // Main screen composition

// User interactions
"User_Click_LoadAd"             // User clicks load ad button
"User_Click_ShowAd"             // User clicks show ad button
```

### **9. Test Activity Tags:**
```kotlin
// Test activity operations
"AdTestActivity_onCreate"       // Test activity creation
"AdTest_LoadBanner"            // Test banner loading
"AdTest_LoadInterstitial"      // Test interstitial loading
"AdTest_ShowInterstitial"      // Test interstitial showing
"AdTest_BannerLoaded"          // Test banner loaded
"AdTest_InterstitialLoaded"    // Test interstitial loaded
```

---

## 🔍 **Perfetto Search Queries**

### **Primary Search Terms:**
```sql
-- GAM SDK specific traces
SELECT * FROM slice WHERE name LIKE 'GAM_%';

-- Ad repository operations
SELECT * FROM slice WHERE name LIKE 'AdRepository_%';

-- User interactions
SELECT * FROM slice WHERE name LIKE 'User_Click_%';

-- App lifecycle
SELECT * FROM slice WHERE name LIKE 'App_%';
```

### **Complete Analysis Query:**
```sql
SELECT 
    slice.name as trace_name,
    slice.ts / 1000000000.0 as start_time_seconds,
    slice.dur / 1000000.0 as duration_ms,
    thread.name as thread_name,
    process.name as process_name
FROM slice 
JOIN thread_track ON slice.track_id = thread_track.id
JOIN thread ON thread_track.utid = thread.utid
JOIN process ON thread.upid = process.upid
WHERE process.name = 'com.example.gamsdkpoc'
  AND (slice.name LIKE 'GAM_%' 
       OR slice.name LIKE 'AdRepository_%'
       OR slice.name LIKE 'User_%'
       OR slice.name LIKE 'App_%')
ORDER BY slice.ts;
```

---

## 📊 **Key Performance Metrics to Monitor**

### **GAM SDK Initialization:**
- **Target:** < 100ms on background thread
- **Critical:** Should not block main thread

### **Ad Loading Performance:**
- **Banner Ads:** < 500ms
- **Interstitial Ads:** < 1000ms
- **Rewarded Ads:** < 1500ms

### **UI Responsiveness:**
- **User Interactions:** < 16ms (60fps)
- **Compose Rendering:** < 50ms

---

## 🚀 **Usage Instructions**

### **1. Capture Trace:**
```bash
# Kill app and start fresh trace
adb shell am force-stop com.example.gamsdkpoc
adb shell perfetto -o /data/misc/perfetto-traces/gam_trace.pb -t 30s sched freq idle am wm gfx view binder_driver hal dalvik camera input res memory &
adb shell am start -n com.example.gamsdkpoc/.MainActivity
```

### **2. Search for GAM SDK Traces:**
- Search for: `GAM_SDK_Initialization`
- Expected duration: 45-100ms
- Expected thread: Background thread (not main)

### **3. Monitor Ad Performance:**
- Search for: `AdRepository_LoadBanner`
- Search for: `AdRepository_LoadInterstitial`
- Check duration and success/failure rates

---

## ⚠️ **Important Notes**

1. **GAM SDK 24.4.0** requires **Kotlin 2.1.0** and **KSP** instead of KAPT
2. **Min SDK 24** is required for GAM SDK 24.4.0
3. All traces are automatically captured when using **AppTracer** utility
4. Traces include rich metadata via attributes for better analysis
5. Both synchronous and asynchronous tracing supported
6. Production-ready with error handling and thread safety
