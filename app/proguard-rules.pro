# Gson specific rules
-keepattributes Signature
-keepattributes *Annotation*
-keepattributes EnclosingMethod
-keepattributes InnerClasses
-keep class com.google.gson.reflect.TypeToken { *; }
-keep class * extends com.google.gson.reflect.TypeToken
-keep public class * implements com.google.gson.TypeAdapterFactory
-keep public class * implements com.google.gson.JsonSerializer
-keep public class * implements com.google.gson.JsonDeserializer
-keepclassmembers class com.google.gson.internal.bind.ReflectiveTypeAdapterFactory$Adapter {
    private final java.util.Map boundFields;
}

# Prevent R8 from stripping generic signatures of your model classes
-keep class com.example.movieslistapp.data.model.** { *; }
-keepclassmembers class com.example.movieslistapp.data.model.** { *; }

# Room
-keep class * extends androidx.room.RoomDatabase
-dontwarn androidx.room.paging.**

# Retrofit
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations
-keepclassmembers interface * {
    @retrofit2.http.* <methods>;
}

# OkHttp
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn javax.annotation.**
-dontwarn org.conscrypt.**
-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase

# Koin
-keep class io.insertkoin.** { *; }
-dontwarn io.insertkoin.**

# Kotlin Coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherLoader
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler
-keepnames class kotlinx.coroutines.android.AndroidExceptionPreHandler
-keepnames class kotlinx.coroutines.android.AndroidDispatcherFactory
-dontwarn kotlinx.coroutines.**
