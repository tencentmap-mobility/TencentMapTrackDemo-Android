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
## 轨迹
-keep class com.tencent.trackx.** { *; }
## 导航
-keep class com.tencent.navix.**{ *; }
## 定位
-keepattributes *Annotation*
-keepclassmembers class ** {
    public void on*Event(...);
}
-keep public class com.tencent.location.**{
    public protected *;
}
-keepclasseswithmembernames class * {
    native <methods>;
}
-keep class c.t.**{*;}
-keep class com.tencent.map.geolocation.**{*;}
-dontwarn  org.eclipse.jdt.annotation.**
-dontwarn  c.t.**
-dontwarn  android.location.Location
-dontwarn  android.net.wifi.WifiManager
-dontnote ct.**
## 地图
-keep public class com.tencent.lbssearch.** {*;}
-keep public class com.tencent.map.** {*;}
-keep public class com.tencent.mapsdk.** {*;}
-keep public class com.tencent.tencentmap.**{*;}
-keep public class com.tencent.tmsbeacon.**{*;}
-keep public class com.tencent.tmsbeacon.**{*;}
-dontwarn com.qq.**
-dontwarn com.tencent.**