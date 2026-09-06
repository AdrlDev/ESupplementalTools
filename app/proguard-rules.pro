# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /home/adrielespiritu/android-sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.

# For more details, see
#   http://developer.android.com/guide/developing/tools-proguard.html

# Add any project specific keep rules here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# Hilt rules
-keep public class * extends android.app.Service
-keep public class * extends android.app.Application
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Fragment
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class * extends androidx.work.Worker

# Room rules
-keep class * extends androidx.room.RoomDatabase
-dontwarn androidx.room.paging.**

# Retrofit & OkHttp rules
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keepattributes Signature, Exceptions, *Annotation*, EnclosingMethod, InnerClasses
-dontwarn okio.**
-dontwarn javax.annotation.**

# Gson rules
-keep class com.google.gson.** { *; }
-keep class com.google.gson.reflect.TypeToken { *; }
-keep class * extends com.google.gson.TypeAdapter
# Keep anonymous subclasses of TypeToken
-keep class * extends com.google.gson.reflect.TypeToken

# Guava rules (some parts might still be used)
-keep class com.google.common.reflect.TypeToken { *; }
-keep class * extends com.google.common.reflect.TypeToken

# Keep models used with Gson
-keep class com.esupplemental.data.model.** { *; }
-keep class com.esupplemental.domain.model.** { *; }

# Keep all classes and members in com.esupplemental to preserve generic signatures
-keep class com.esupplemental.** { *; }
-keepclassmembers class com.esupplemental.** {
    <fields>;
    <methods>;
}

# Keep members with @SerializedName
-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

# Koin rules
-keep class org.koin.** { *; }

# Coil rules
-dontwarn coil.**
