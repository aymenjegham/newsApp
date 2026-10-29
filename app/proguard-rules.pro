# Kotlinx Serialization
-keepattributes *Annotation*,InnerClasses,Signature,EnclosingMethod

-keepclassmembers class * {
    @kotlinx.serialization.Serializable <fields>;
}

-keep,allowobfuscation @kotlinx.serialization.Serializable class * {
    *;
}

-keepclassmembers class * {
    kotlinx.serialization.KSerializer serializer(...);
}

# Navigation 3 NavKey implementations (preserve class names for reflection via Class.forName in NavKeySerializer)
-keep class * implements androidx.navigation3.runtime.NavKey {
    <init>();
    *;
}

# Room
-keep class com.angelstudio.newsapp.data.db.** { *; }
-keepclassmembers class com.angelstudio.newsapp.data.db.entity.** {
    *;
}

# Gson / Retrofit models
-keep class com.angelstudio.newsapp.data.network.response.** { *; }
-keep class com.angelstudio.newsapp.domain.model.** { *; }

# Preserve line numbers and source files for stack traces debugging
-keepattributes SourceFile,LineNumberTable
