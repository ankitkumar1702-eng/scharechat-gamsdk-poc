# GAM SDK PoC - Clean Architecture Android App

A production-ready Android application demonstrating Google Ad Manager (GAM) SDK integration with Clean Architecture, Perfetto tracing, and comprehensive performance monitoring.

## 🏗️ Architecture Overview

This project implements **Clean Architecture** principles with the following layers:

### 📁 Project Structure
```
app/src/main/java/com/example/gamsdkpoc/
├── core/                           # Core utilities and base classes
│   ├── base/BaseActivity.kt        # Base activity with tracing
│   ├── di/AdModule.kt              # Dependency injection
│   └── tracing/AppTracer.kt        # Production-grade Perfetto tracing
├── data/                           # Data layer
│   └── repository/AdRepositoryImpl.kt
├── domain/                         # Business logic layer
│   ├── model/                      # Domain models
│   ├── repository/AdRepository.kt  # Repository interface
│   └── usecase/                    # Use cases
├── presentation/                   # Presentation layer
│   └── viewmodel/MainViewModel.kt
├── GamSdkApplication.kt           # Application class
└── MainActivity.kt                # Main activity
```

## 🚀 Key Features

### ✅ GAM SDK Integration
- **Non-blocking initialization** on background thread
- **Test ad units** for development
- **Production ad configurations**
- **Banner and Interstitial** ad support
- **Error handling** and retry mechanisms

### ✅ Performance Monitoring
- **Perfetto-based tracing** with `AppTracer` utility
- **Synchronous and asynchronous** trace sections
- **Rich metadata** support with attributes
- **Thread-safe** cookie management
- **Debug/Release** build differentiation

### ✅ Clean Architecture
- **Separation of concerns** across layers
- **Dependency injection** with Hilt
- **Repository pattern** for data access
- **Use cases** for business logic
- **MVVM** with ViewModels

### ✅ Modern Android Development
- **Kotlin** with coroutines
- **Jetpack Compose** UI
- **Flow** for reactive programming
- **Hilt** for dependency injection
- **Material Design 3**

## 🔧 Setup Instructions

### Prerequisites
- Android Studio Hedgehog or later
- Android SDK 34+
- Minimum SDK 23
- Kotlin 2.0+

### 1. Clone and Build
```bash
git clone <repository-url>
cd GamSdkPoc
./gradlew assembleDebug
```

### 2. GAM SDK Configuration
1. **Get your AdMob App ID** from the AdMob console
2. **Update AndroidManifest.xml**:
   ```xml
   <meta-data
       android:name="com.google.android.gms.ads.APPLICATION_ID"
       android:value="YOUR_ACTUAL_APP_ID"/>
   ```
3. **Update ad unit IDs** in `AdConfig.kt` for production

### 3. Enable Performance Tracing
The app includes production-grade Perfetto tracing. To view traces:

1. **Enable tracing** in Android Studio Profiler
2. **Use Chrome://tracing** or Perfetto UI
3. **Look for custom trace sections** like:
   - `GAM_SDK_Initialization`
   - `MainActivity_onCreate`
   - `LoadBannerAd`
   - `User_Interaction`

## 📊 AppTracer Usage

### Synchronous Tracing
```kotlin
AppTracer.startTrace("Operation_Name", mapOf("param" to "value"))
// ... do work ...
AppTracer.stopTrace()
```

### Asynchronous Tracing
```kotlin
val cookie = AppTracer.beginAsyncSection("Async_Operation")
// ... async work ...
AppTracer.endAsyncSection("Async_Operation")
```

### Inline Tracing
```kotlin
val result = AppTracer.trace("Database_Query") {
    database.query()
}
```

### Coroutine Tracing
```kotlin
val result = AppTracer.traceSuspend("Network_Call") {
    apiService.getData()
}
```

## 🎯 GAM SDK Best Practices

### ✅ Initialization
- ✅ Initialize on **background thread**
- ✅ Wait for completion before loading ads
- ✅ Handle initialization failures gracefully

### ✅ Ad Loading
- ✅ Use **test ad units** during development
- ✅ Implement **proper error handling**
- ✅ Add **loading states** for better UX
- ✅ **Cache ad configurations**

### ✅ Performance
- ✅ **Non-blocking** ad operations
- ✅ **Trace ad loading** performance
- ✅ **Monitor memory usage**
- ✅ **Handle ad lifecycle** properly

## 🧪 Testing

### Unit Tests
```bash
./gradlew testDebugUnitTest
```

### Integration Tests
```bash
./gradlew connectedDebugAndroidTest
```

### Test Coverage
- ✅ Use case testing with MockK
- ✅ Repository testing
- ✅ ViewModel testing
- ✅ Flow testing with Turbine

## 📈 Performance Monitoring

### Trace Categories
| Category | Description |
|----------|-------------|
| `App_Startup` | Application initialization |
| `GAM_SDK_*` | GAM SDK operations |
| `Network_*` | API calls and responses |
| `UI_*` | User interface operations |
| `User_*` | User interactions |

### Key Metrics
- **App startup time**
- **Ad loading duration**
- **Memory usage patterns**
- **UI responsiveness**
- **Network performance**

## 🔍 Debugging

### GAM SDK Debug
1. **Enable test ads** during development
2. **Check logcat** for GAM SDK logs
3. **Use demo ad units** for testing
4. **Verify app ID** configuration

### Performance Debug
1. **Use Android Studio Profiler**
2. **Enable Perfetto tracing**
3. **Monitor trace sections**
4. **Check for ANRs**

## 📚 Dependencies

### Core Dependencies
```kotlin
// GAM SDK
implementation("com.google.android.gms:play-services-ads:24.4.0")

// Architecture
implementation("androidx.hilt:hilt-android:2.51.1")
implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.9.1")

// Coroutines
implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1")

// Compose
implementation("androidx.compose.ui:ui:1.7.8")
implementation("androidx.compose.material3:material3:1.3.1")
```

### Testing Dependencies
```kotlin
testImplementation("junit:junit:4.13.2")
testImplementation("io.mockk:mockk:1.13.12")
testImplementation("app.cash.turbine:turbine:1.1.0")
testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.8.1")
```

## 🚀 Production Checklist

### Before Release
- [ ] Replace test ad units with production IDs
- [ ] Update AdMob app ID
- [ ] Remove debug tracing (optional)
- [ ] Test on multiple devices
- [ ] Verify ad loading performance
- [ ] Check memory usage
- [ ] Test offline scenarios

### Performance Optimization
- [ ] Minimize app startup time
- [ ] Optimize ad loading
- [ ] Reduce memory footprint
- [ ] Implement proper caching
- [ ] Monitor crash rates

## 📄 License

This project is for demonstration purposes. Please ensure compliance with Google AdMob policies and terms of service.

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests
5. Submit a pull request

## 📞 Support

For issues related to:
- **GAM SDK**: Check [Google AdMob documentation](https://developers.google.com/admob/android)
- **Clean Architecture**: Review architecture guidelines
- **Performance**: Use Android Studio Profiler and Perfetto

---

**Built with ❤️ using Clean Architecture and modern Android development practices**
# scharechat-gamsdk-poc
