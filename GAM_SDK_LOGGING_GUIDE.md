# 🚀 GAM SDK Enhanced Logging Guide

## 📋 Complete ADB Logcat Commands for Testing

### **🔥 Primary GAM SDK Testing Command:**
```bash
adb logcat -s "GAM_SDK_INIT" "GAM_SDK_LIFECYCLE" "GAM_SDK_VALIDATION" "GAM_SDK" "GAM_SDK_THREAD" "AD_THREAD" "AppTracer" "AppTracer_Performance" "AppTracer_StateChange" "AppTracer_Error"
```

### **🎯 Focused Commands for Specific Testing:**

#### **1. GAM SDK Initialization Only:**
```bash
adb logcat -s "GAM_SDK_INIT" "GAM_SDK_VALIDATION"
```

#### **2. Thread Analysis:**
```bash
adb logcat -s "AD_THREAD" "GAM_SDK_THREAD" "AppTracer_Performance"
```

#### **3. Performance Monitoring:**
```bash
adb logcat -s "AppTracer_Performance" "AppTracer_Error"
```

#### **4. State Changes:**
```bash
adb logcat -s "AppTracer_StateChange" "GAM_SDK_LIFECYCLE"
```

#### **5. Complete App Tracing:**
```bash
adb logcat -s "AppTracer" "AppTracer_Performance" "AppTracer_StateChange" "AppTracer_Error"
```

---

## 📊 Expected Log Output Examples

### **🚀 Application Startup Logs:**
```
I/GAM_SDK_LIFECYCLE: 🚀 Application.onCreate() started at: 12:23:42.123
I/GAM_SDK_LIFECYCLE: 📱 App Process: 12345, Thread: main
I/AppTracer: 🚀 AppTracer initialized with enhanced app-sensitive tracking
I/AppTracer: 📊 Features: Performance, Memory, State Changes, Errors, User Actions
I/AppTracer_StateChange: 🔄 STATE CHANGE: Application
I/AppTracer_StateChange:    📍 From: null → To: CREATING
I/AppTracer_StateChange:    ⏰ Time: 12:23:42.124
I/GAM_SDK_LIFECYCLE: ✅ super.onCreate() completed
I/AppTracer_StateChange: 🔄 STATE CHANGE: Application
I/AppTracer_StateChange:    📍 From: CREATING → To: CREATED
I/AppTracer_StateChange:    ⏰ Time: 12:23:42.125
I/GAM_SDK_LIFECYCLE: 🏁 Application.onCreate() completed in 15ms
```

### **🏠 MainActivity Creation Logs:**
```
I/AppTracer_StateChange: 🔄 STATE CHANGE: MainActivity
I/AppTracer_StateChange:    📍 From: null → To: CREATING
I/AppTracer_StateChange:    ⏰ Time: 12:23:42.200
D/AppTracer_Performance: 📊 Trace[1] START: MainActivity_SuperOnCreate
D/AppTracer_Performance:    ⏰ Time: 12:23:42.201
D/AppTracer_Performance:    🧵 Thread: main (ID: 2)
D/AppTracer_Performance:    💾 Memory: 45MB
D/AppTracer_Performance: 📊 Trace COMPLETE: MainActivity_SuperOnCreate
D/AppTracer_Performance:    ⏱️ Duration: 12ms
D/AppTracer_Performance:    💾 Memory Delta: +2MB
I/AppTracer_StateChange: 🔄 STATE CHANGE: MainActivity
I/AppTracer_StateChange:    📍 From: CREATING → To: INITIALIZING_GAM_SDK
```

### **🔥 GAM SDK Initialization Logs:**
```
I/GAM_SDK_INIT: 🔥 GAM SDK INITIALIZATION STARTED (MainActivity)
I/GAM_SDK_INIT: 📍 Location: MainActivity.initializeGamSdk()
I/GAM_SDK_INIT: ⏰ Start Time: 12:23:42.250
I/GAM_SDK_INIT: 🧵 Called on Thread: main (ID: 2)
I/GAM_SDK_INIT: 🎯 Using Dispatcher: Dispatchers.IO
I/GAM_SDK_INIT: 📦 GAM SDK Version: 24.4.0
I/GAM_SDK_INIT: 🏗️ Context: Activity Context (Optimized)
I/GAM_SDK_INIT: 🚀 Coroutine launched after 5ms
I/GAM_SDK_INIT: 🔄 Switched to Background Thread: DefaultDispatcher-worker-1 (ID: 123)
D/AppTracer: 🔄 Starting ASYNC trace: GAM_SDK_MainActivity_Init
D/AppTracer_Performance: 📊 AsyncTrace[2] START: GAM_SDK_MainActivity_Init
D/AppTracer_Performance:    ⏰ Time: 12:23:42.255
D/AppTracer_Performance:    🧵 Thread: DefaultDispatcher-worker-1 (ID: 123)
D/AppTracer_Performance:    💾 Memory: 47MB
D/AppTracer_Performance:    🔄 Type: ASYNC (Thread-switching capable)
```

### **✅ GAM SDK Validation Logs:**
```
I/GAM_SDK_VALIDATION: 🔍 VALIDATING BACKGROUND THREAD EXECUTION (MainActivity):
I/GAM_SDK_VALIDATION:    ✓ Thread Name: DefaultDispatcher-worker-1
I/GAM_SDK_VALIDATION:    ✓ Is Main Thread: false
I/GAM_SDK_VALIDATION:    ✓ Is Background Thread: true
I/GAM_SDK_VALIDATION:    ✓ Thread ID: 123
I/GAM_SDK_VALIDATION:    ✓ Thread Priority: 5
I/GAM_SDK_VALIDATION:    ✓ Context Type: Activity (Google Recommended)
I/GAM_SDK_VALIDATION: ✅ COMPLIANCE: GAM SDK initializing on BACKGROUND THREAD
I/GAM_SDK_VALIDATION: ✅ Using Activity Context (Google Best Practice)
I/GAM_SDK_VALIDATION: ✅ Non-blocking initialization confirmed
```

### **🎉 GAM SDK Callback Logs:**
```
I/GAM_SDK_INIT: 📞 Calling MobileAds.initialize() at: 12:23:42.260
I/GAM_SDK_INIT: 🧵 MobileAds.initialize() called on thread: DefaultDispatcher-worker-1
I/GAM_SDK_INIT: 📤 MobileAds.initialize() call dispatched in 2ms
I/GAM_SDK_INIT: ⏳ Waiting for initialization callback (Activity Context)...
I/GAM_SDK_INIT: 🎉 GAM SDK INITIALIZATION CALLBACK RECEIVED (MainActivity)!
I/GAM_SDK_INIT: ⏰ Callback Time: 12:23:42.450
I/GAM_SDK_INIT: ⏱️ Total Initialization Time: 200ms
I/GAM_SDK_INIT: 🧵 Callback Processing Thread: DefaultDispatcher-worker-2 (ID: 124)
I/GAM_SDK_INIT: 🎯 Callback kept on BACKGROUND THREAD (Non-blocking)
I/GAM_SDK_INIT: 📊 Initialization Status:
I/GAM_SDK_INIT:    - Total Adapters: 3
I/GAM_SDK_INIT:    📱 GoogleMobileAdsAdapter: READY
I/GAM_SDK_INIT:    📱 AdMobAdapter: READY
I/GAM_SDK_INIT:    📱 TestAdapter: NOT_READY
I/GAM_SDK_INIT: 📈 Summary: 2 Ready, 1 Not Ready
I/GAM_SDK_INIT: ✅ GAM SDK INITIALIZATION COMPLETED SUCCESSFULLY (MainActivity)!
I/GAM_SDK_INIT: 🧵 Processing completed on: DefaultDispatcher-worker-2
I/GAM_SDK_INIT: 🚀 Performance: Activity Context + Background Processing
```

### **🔄 Thread Switch Detection:**
```
D/AppTracer_Performance: 📊 AsyncTrace COMPLETE: GAM_SDK_MainActivity_Init
D/AppTracer_Performance:    ⏱️ Duration: 200ms
D/AppTracer_Performance:    💾 Memory Delta: +3MB
D/AppTracer_Performance:    🧵 Start Thread: DefaultDispatcher-worker-1
D/AppTracer_Performance:    🧵 End Thread: DefaultDispatcher-worker-2
I/AppTracer_Performance: 🔄 THREAD SWITCH DETECTED: DefaultDispatcher-worker-1 → DefaultDispatcher-worker-2
```

### **📱 Ad Loading Logs:**
```
D/AD_THREAD: loadBannerAd() called on thread: main (ID: 2)
D/AppTracer_Performance: 📊 Trace[3] START: AdRepository_LoadBanner
I/AppTracer_StateChange: 🔄 STATE CHANGE: BannerAd
I/AppTracer_StateChange:    📍 From: LOADING → To: LOADED
D/AD_THREAD: Banner onAdLoaded() callback on thread: main (ID: 2)
D/AppTracer_Performance: 📊 Trace COMPLETE: AdRepository_LoadBanner
D/AppTracer_Performance:    ⏱️ Duration: 1200ms
```

### **👆 User Action Logs:**
```
I/AppTracer: 👆 USER ACTION: LoadBannerAd
I/AppTracer:    🎯 Component: MainViewModel
I/AppTracer:    ⏰ Time: 12:23:45.123
I/AppTracer:    🧵 Thread: main
I/AppTracer_StateChange: 🔄 STATE CHANGE: BannerAd
I/AppTracer_StateChange:    📍 From: Success → To: LOADING
```

### **⚠️ Performance Warnings:**
```
W/AppTracer_Performance: ⚠️ SLOW OPERATION: AdRepository_LoadInterstitial took 2500ms
W/AppTracer_Performance: ⚠️ HIGH MEMORY USAGE: GAM_SDK_MainActivity_Init used 5MB
```

### **💥 Error Logs:**
```
E/AppTracer_Error: 💥 ERROR TRACED: AdRepository_LoadBanner
E/AppTracer_Error:    ⏰ Time: 12:23:46.789
E/AppTracer_Error:    🧵 Thread: main
E/AppTracer_Error:    💥 Error: Network timeout
E/AppTracer_Error:    📍 Stack: [full stack trace]
```

---

## 🎯 Testing Scenarios

### **1. App Launch Testing:**
```bash
# Clear logs and start fresh
adb logcat -c

# Start comprehensive logging
adb logcat -s "GAM_SDK_INIT" "GAM_SDK_LIFECYCLE" "GAM_SDK_VALIDATION" "AppTracer" "AppTracer_Performance" "AppTracer_StateChange" "AppTracer_Error"

# Launch app and observe initialization sequence
```

### **2. Ad Loading Testing:**
```bash
# Focus on ad-related logs
adb logcat -s "AD_THREAD" "AppTracer_Performance" "AppTracer_StateChange"

# Click "Load Banner Ad" and "Load Interstitial Ad" buttons
```

### **3. Performance Testing:**
```bash
# Monitor performance metrics
adb logcat -s "AppTracer_Performance" "AppTracer_Error"

# Look for slow operations and memory warnings
```

### **4. Thread Analysis:**
```bash
# Monitor thread switches
adb logcat -s "AD_THREAD" "GAM_SDK_THREAD" "AppTracer_Performance" | grep -E "(Thread|THREAD|thread)"
```

---

## 📈 Performance Benchmarks to Look For

### **✅ Good Performance Indicators:**
- GAM SDK initialization < 500ms
- Banner ad loading < 2000ms
- Interstitial ad loading < 3000ms
- Memory delta < 10MB per operation
- No thread violations (main thread blocking)

### **⚠️ Warning Signs:**
- Operations taking > 100ms get flagged
- Memory usage > 1MB gets flagged
- Thread switches are logged for analysis
- Any main thread blocking during GAM SDK init

### **❌ Critical Issues:**
- GAM SDK initializing on main thread
- Memory leaks (continuously increasing memory)
- Ad loading failures
- Crashes or exceptions

---

## 🔧 Advanced Filtering

### **Filter by Time Range:**
```bash
adb logcat -s "AppTracer_Performance" | grep "12:23:4[0-9]"
```

### **Filter by Thread:**
```bash
adb logcat -s "AppTracer_Performance" | grep "main"
adb logcat -s "AppTracer_Performance" | grep "DefaultDispatcher"
```

### **Filter by Memory Usage:**
```bash
adb logcat -s "AppTracer_Performance" | grep "Memory"
```

### **Filter by Slow Operations:**
```bash
adb logcat -s "AppTracer_Performance" | grep "SLOW OPERATION"
```

---

## 🚀 Quick Test Commands

### **Complete GAM SDK Test:**
```bash
adb logcat -c && adb logcat -s "GAM_SDK_INIT" "GAM_SDK_LIFECYCLE" "GAM_SDK_VALIDATION" "GAM_SDK" "GAM_SDK_THREAD" "AD_THREAD" "AppTracer" "AppTracer_Performance" "AppTracer_StateChange" "AppTracer_Error"
```

### **Performance Only:**
```bash
adb logcat -c && adb logcat -s "AppTracer_Performance" "AppTracer_Error"
```

### **State Changes Only:**
```bash
adb logcat -c && adb logcat -s "AppTracer_StateChange" "GAM_SDK_LIFECYCLE"
```

```bash
adb shell am force-stop com.example.gamsdkpoc && adb logcat -c && adb logcat -s "GAM_SDK_INIT" "GAM_SDK_LIFECYCLE" "GAM_SDK_VALIDATION" "AppTracer" "AppTracer_Performance" "AppTracer_StateChange" "AppTracer_Error" & sleep 2 && adb shell am start -n com.example.gamsdkpoc/.MainActivity
```
```bash
adb shell am force-stop com.example.gamsdkpoc && adb logcat -c
```

```bash
adb logcat -s "GAM_SDK_INIT" "GAM_SDK_LIFECYCLE" "GAM_SDK_VALIDATION" "AppTracer" "AppTracer_Performance" "AppTracer_StateChange" "AppTracer_Error" & sleep 2 && adb shell am start -n com.example.gamsdkpoc/.MainActivity
```