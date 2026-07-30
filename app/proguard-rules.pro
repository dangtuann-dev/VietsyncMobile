# ====================================================================
# ProGuard & R8 Obfuscation Rules for VietsyncMobile
# ====================================================================

# Keep General Android Components
-keepclassmembers class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}
-keepclassmembers class * extends android.app.Activity {
    public void *(android.view.View);
}
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# --------------------------------------------------------------------
# 1. Retrofit Rules
# --------------------------------------------------------------------
-keepattributes Signature, InnerClasses, EnclosingMethod
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations
-keepattributes AnnotationDefault

-keep class retrofit2.** { *; }
-keepclasseswithmembers interface * {
    @retrofit2.http.* <methods>;
}
-dontwarn retrofit2.**

# --------------------------------------------------------------------
# 2. Gson & SerializedName Models Rules
# --------------------------------------------------------------------
-keepattributes *Annotation*
-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
}
-keep class com.google.gson.** { *; }

# Preserve all App Data Models (Supabase & REST)
-keep class com.app.learning.data.model.** { *; }
-keepclassmembers class com.app.learning.data.model.** { *; }

# --------------------------------------------------------------------
# 3. Glide Rules
# --------------------------------------------------------------------
-keep public class * extends com.bumptech.glide.module.AppGlideModule
-keep class * implements com.bumptech.glide.module.GlideModule
-keepclassmembers class * implements com.bumptech.glide.module.GlideModule {
    public <init>(...);
}
-dontwarn com.bumptech.glide.**

# --------------------------------------------------------------------
# 4. Media3 / ExoPlayer Rules
# --------------------------------------------------------------------
-keep class androidx.media3.exoplayer.** { *; }
-keep class androidx.media3.common.** { *; }
-dontwarn androidx.media3.**

# --------------------------------------------------------------------
# 5. Room Database Rules
# --------------------------------------------------------------------
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-dontwarn androidx.room.**

# --------------------------------------------------------------------
# 6. Lottie Animation Rules
# --------------------------------------------------------------------
-keep class com.airbnb.lottie.** { *; }

# --------------------------------------------------------------------
# 7. Firebase Cloud Messaging (FCM) Rules
# --------------------------------------------------------------------
-keep class com.google.firebase.messaging.** { *; }
-keep class com.app.learning.data.notification.MyFirebaseMessagingService { *; }
-dontwarn com.google.firebase.**

# --------------------------------------------------------------------
# 8. MPAndroidChart Rules
# --------------------------------------------------------------------
-keep class com.github.mikephil.charting.** { *; }
-dontwarn com.github.mikephil.charting.**

# --------------------------------------------------------------------
# 9. ZXing QR Rules
# --------------------------------------------------------------------
-keep class com.google.zxing.** { *; }

# Preserve Source File & Line Numbers for Mapping File Debugging
-renamesourcefileattribute SourceFile
-keepattributes SourceFile,LineNumberTable
