# GAM SDK Complete Test Commands

## 🚀 Recommended: Complete Cold Start Logcat Command

**Use this command to capture the full initialization, lifecycle, and tracing logs for the GAM SDK, including Application and MainActivity startup.**

```bash
adb shell am force-stop com.example.gamsdkpoc && adb logcat -c && adb logcat -s "GAM_SDK_INIT" "GAM_SDK_LIFECYCLE" "GAM_SDK_VALIDATION" "AppTracer" "AppTracer_Performance" "AppTracer_StateChange" "AppTracer_Error" & sleep 2 && adb shell am start -n com.example.gamsdkpoc/.MainActivity
```

**Explanation:**
- This command force-stops the app, clears the logcat buffer, starts logcat with all relevant tags, waits 2 seconds, and then launches the app for a true cold start.
- This ensures you capture all logs from the very beginning, including GAM SDK initialization and enhanced tracing.

---

# 🔥 Complete GAM SDK Testing Commands



## 🎯 Complete Testing Solution:

### **Step 1: Force Fresh App Start**
```bash
# Kill the app completely
adb shell am force-stop com.example.gamsdkpoc

# Clear all logs
adb logcat -c

# Start comprehensive logging BEFORE launching app
adb logcat -s "GAM_SDK_INIT" "GAM_SDK_LIFECYCLE" "GAM_SDK_VALIDATION" "GAM_SDK" "GAM_SDK_THREAD" "AD_THREAD" "AppTracer" "AppTracer_Performance" "AppTracer_StateChange" "AppTracer_Error" &

# Wait 2 seconds, then launch app
sleep 2 && adb shell am start -n com.example.gamsdkpoc/.MainActivity
```

### **Step 2: Alternative - Capture ALL Logs**
```bash
# Kill app
adb shell am force-stop com.example.gamsdkpoc

# Clear logs
adb logcat -c

# Capture ALL logs and filter later
adb logcat | grep -E "(GAM_SDK|AppTracer|AD_THREAD)" &

# Launch app
adb shell am start -n com.example.gamsdkpoc/.MainActivity
```

### **Step 3: Debug - Check What Tags Are Actually Being Used**
```bash
# Kill app
adb shell am force-stop com.example.gamsdkpoc

# Clear logs
adb logcat -c

# Capture everything for 30 seconds after app launch
adb logcat > full_logs.txt &
LOGCAT_PID=$!

# Launch app
adb shell am start -n com.example.gamsdkpoc/.MainActivity

# Wait 30 seconds
sleep 30

# Kill logcat
kill $LOGCAT_PID

# Search for GAM SDK related logs
grep -i "gam\|mobile\|ads\|init" full_logs.txt
```

## 🔍 Expected Missing Logs:

### **Application Startup (Should appear first):**
```
I/GAM_SDK_LIFECYCLE: 🚀 Application.onCreate() started at: 12:43:52.123
I/AppTracer: 🚀 AppTracer initialized with enhanced app-sensitive tracking
I/AppTracer_StateChange: 🔄 STATE CHANGE: Application
I/AppTracer_StateChange:    📍 From: null → To: CREATING
```

### **MainActivity Creation:**
```
I/AppTracer_StateChange: 🔄 STATE CHANGE: MainActivity
I/AppTracer_StateChange:    📍 From: null → To: CREATING
D/AppTracer_Performance: 📊 Trace[1] START: MainActivity_SuperOnCreate
```

### **GAM SDK Initialization:**
```
I/GAM_SDK_INIT: 🔥 GAM SDK INITIALIZATION STARTED (MainActivity)
I/GAM_SDK_INIT: 📍 Location: MainActivity.initializeGamSdk()
I/GAM_SDK_INIT: 🧵 Called on Thread: main (ID: 2)
I/GAM_SDK_INIT: 🚀 Coroutine launched after 5ms
I/GAM_SDK_VALIDATION: 🔍 VALIDATING BACKGROUND THREAD EXECUTION (MainActivity):
I/GAM_SDK_VALIDATION: ✅ COMPLIANCE: GAM SDK initializing on BACKGROUND THREAD
```

## 🚀 Quick Fix Commands:

### **Command 1: Complete Fresh Start**
```bash
adb shell am force-stop com.example.gamsdkpoc && adb logcat -c && adb logcat -s "GAM_SDK_INIT" "GAM_SDK_LIFECYCLE" "GAM_SDK_VALIDATION" "AppTracer" "AppTracer_Performance" "AppTracer_StateChange" "AppTracer_Error" & sleep 2 && adb shell am start -n com.example.gamsdkpoc/.MainActivity
```

### **Command 2: Capture Everything**
```bash
adb shell am force-stop com.example.gamsdkpoc && adb logcat -c && adb logcat | grep -E "(GAM_SDK|AppTracer|AD_THREAD|MobileAds)" & sleep 2 && adb shell am start -n com.example.gamsdkpoc/.MainActivity
```

### **Command 3: Debug Mode**
```bash
adb shell am force-stop com.example.gamsdkpoc && adb logcat -c && adb logcat -v time | tee gam_debug.log & sleep 2 && adb shell am start -n com.example.gamsdkpoc/.MainActivity
```

## 🔧 Troubleshooting:

### **If Still No GAM SDK Logs:**

1. **Check if app is actually calling GAM SDK initialization:**
```bash
# Look for any MobileAds related logs
adb logcat | grep -i "mobileads"
```

2. **Check for Android system logs:**
```bash
# Look for any ads-related system logs
adb logcat | grep -i "ads"
```

3. **Verify app is starting fresh:**
```bash
# Check app process
adb shell ps | grep gamsdkpoc
```

### **Alternative Logging Strategy:**
```bash
# Use broader filter
adb logcat -s "System.out" "System.err" "GAM_SDK_INIT" "GAM_SDK_LIFECYCLE" "GAM_SDK_VALIDATION" "AppTracer" "AppTracer_Performance" "AppTracer_StateChange" "AppTracer_Error"
```

## 📱 Expected Complete Flow:

1. **App Launch** → Application.onCreate() logs
2. **AppTracer Init** → Enhanced tracing setup
3. **MainActivity** → Activity creation with state changes
4. **GAM SDK Init** → Background thread initialization
5. **GAM SDK Callback** → Initialization completion
6. **Ad Loading** → Banner/Interstitial ad operations

## 🎯 Current Issue Analysis:

आपके logs में trace ID 52+ से शुरू हो रहे हैं, जिसका मतलब है:
- App पहले से running था
- Initial startup logs miss हो गए
- GAM SDK already initialized था

**Solution:** Force fresh start with proper logging sequence!

## 🚀 Final Test Command:
```bash
# Complete test with timing
adb shell am force-stop com.example.gamsdkpoc
sleep 1
adb logcat -c
echo "Starting logcat..."
adb logcat -s "GAM_SDK_INIT" "GAM_SDK_LIFECYCLE" "GAM_SDK_VALIDATION" "GAM_SDK" "AppTracer" "AppTracer_Performance" "AppTracer_StateChange" "AppTracer_Error" &
sleep 2
echo "Launching app..."
adb shell am start -n com.example.gamsdkpoc/.MainActivity
echo "App launched! Watch for logs..."
```

**अब आपको complete GAM SDK initialization sequence दिखना चाहिए! 🎯**
