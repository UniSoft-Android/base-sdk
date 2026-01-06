# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

-keep public class com.module.ads.** {
    public *;
}

-keepclassmembers class com.module.ads.** {
    public <init>(...);
}

# Keep application
# keep everything in this package from being removed or renamed
-keep class com.module.ads.app.ModuleApplication { *; }

# keep everything in this package from being renamed only
-keepnames class com.module.ads.app.ModuleApplication

# keep interface nested Application.ActivityLifecycleCallbacks
-keep class android.app.Application$ActivityLifecycleCallbacks { *; }
-keep interface android.app.Application$ActivityLifecycleCallbacks { *; }


# Keep model
# keep everything in this package from being removed or renamed
-keep class com.module.ads.models.** { *; }

# keep everything in this package from being renamed only
-keepnames class com.module.ads.models.** { *; }

# Keep interface
# keep everything in this package from being removed or renamed
-keep class com.module.ads.callback.*

# keep everything in this package from being renamed only
-keepnames class com.module.ads.callback.** { *; }

-keep class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}

# Keep Anonation
-keepattributes *Annotation*
-keepattributes Signature
-keepattributes Exceptions
-keepattributes EnclosingMethod
-keepattributes InnerClasses

#Remove Logging code:
-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int v(...);
    public static int i(...);
    public static int w(...);
    public static int d(...);
    public static int e(...);
}

-assumenoexternalsideeffects class java.lang.StringBuilder {
    public java.lang.StringBuilder();
    public java.lang.StringBuilder(int);
    public java.lang.StringBuilder(java.lang.String);
    public java.lang.StringBuilder append(java.lang.Object);
    public java.lang.StringBuilder append(java.lang.String);
    public java.lang.StringBuilder append(java.lang.StringBuffer);
    public java.lang.StringBuilder append(char[]);
    public java.lang.StringBuilder append(char[], int, int);
    public java.lang.StringBuilder append(boolean);
    public java.lang.StringBuilder append(char);
    public java.lang.StringBuilder append(int);
    public java.lang.StringBuilder append(long);
    public java.lang.StringBuilder append(float);
    public java.lang.StringBuilder append(double);
    public java.lang.String toString();
}

-assumenoexternalreturnvalues public final class java.lang.StringBuilder {
    public java.lang.StringBuilder append(java.lang.Object);
    public java.lang.StringBuilder append(java.lang.String);
    public java.lang.StringBuilder append(java.lang.StringBuffer);
    public java.lang.StringBuilder append(char[]);
    public java.lang.StringBuilder append(char[], int, int);
    public java.lang.StringBuilder append(boolean);
    public java.lang.StringBuilder append(char);
    public java.lang.StringBuilder append(int);
    public java.lang.StringBuilder append(long);
    public java.lang.StringBuilder append(float);
    public java.lang.StringBuilder append(double);
}

# GG Libs: https://stackoverflow.com/a/24109609/938427
-keep public class com.google.android.gms.* { public *; }
-dontwarn com.google.android.gms.**

# Admob: https://github.com/googlesamples/android-ads/blob/master/admob/BannerExample/app/proguard-rules.pro
-keep public class com.google.android.gms.ads.**{
   public *;
}
-keep public class com.google.ads.**{
   public *;
}
-keep class * extends java.util.ListResourceBundle {
   protected Object[][] getContents();
}
-keep public class com.google.android.gms.common.internal.safeparcel.SafeParcelable {
   public static final *** NULL;
}
-keepnames @com.google.android.gms.common.annotation.KeepName class *
-keepclassmembernames class * {
   @com.google.android.gms.common.annotation.KeepName *;
}

-keep class com.google.android.gms.common.GooglePlayServicesUtil {*;}
-keep class com.google.android.gms.ads.identifier.AdvertisingIdClient {*;}
-keep class com.google.android.gms.ads.identifier.AdvertisingIdClient$Info {*;}

-keep class com.google.android.gms.** { *; }
-dontwarn com.google.android.gms.**
-keep class com.google.ads.** {*;}

# Firebase - Crashlytics: https://firebase.google.com/docs/crashlytics/get-deobfuscated-reports?platform=android
-keepattributes SourceFile,LineNumberTable
-keep public class * extends java.lang.Exception

# JSR 305 annotations are for embedding nullability information.
-dontwarn javax.annotation.**

# A resource is loaded with a relative path so the package of this class must be preserved.
-adaptresourcefilenames okhttp3/internal/publicsuffix/PublicSuffixDatabase.gz

# Animal Sniffer compileOnly dependency to ensure APIs are compatible with older versions of Java.
-dontwarn org.codehaus.mojo.animal_sniffer.*

# OkHttp platform used only on JVM and when Conscrypt and other security providers are available.
-dontwarn okhttp3.internal.platform.**
-dontwarn org.conscrypt.**
-dontwarn org.bouncycastle.**
-dontwarn org.openjsse.**

# Android X
-dontwarn androidx.**
-dontwarn androidx.**
-keep class androidx.**

