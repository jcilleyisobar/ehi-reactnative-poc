#
# This ProGuard configuration file illustrates how to process Android
# applications.
# Usage:
#     java -jar proguard.jar @android.pro
#
# If you're using the Android SDK (version 2.3 or higher), the android tool
# already creates a file like this in your project, called proguard.cfg.
# It should contain the settings of this file, minus the input and output paths
# (-injars, -outjars, -libraryjars, -printmapping, and -printseeds).
# The generated Ant build file automatically sets these paths.

# Specify the input jars, output jars, and library jars.
# Note that ProGuard works with Java bytecode (.class),
# before the dex compiler converts it into Dalvik code (.dex).

# injars/outjars specificed by default android proguard file


# Save the obfuscation mapping to a file, so you can de-obfuscate any stack
# traces later on.

#-printmapping bin/classes-processed.map

# You can print out the seeds that are matching the keep options below.

#-printseeds bin/classes-processed.seeds

# Preverification is irrelevant for the dex compiler and the Dalvik VM.

-dontpreverify

# want proguard as an obfuscator and not ineterested in its other 'features'
#-dontshrink # we need to shrink app to fix Galaxy Ace problem.
-dontoptimize

# ProGuard runs only when you build your application in release mode, so strip Log statements
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
    public static *** i(...);
}

# Reduce the size of the output some more.

-repackageclasses ''
-allowaccessmodification

# Switch off some optimizations that trip older versions of the Dalvik VM.

-optimizations !code/simplification/arithmetic

# RemoteViews might need annotations.

-dontskipnonpubliclibraryclassmembers
-keepattributes *Annotations*,Signature,EnclosingMethod

# Preserve all fundamental application classes.

-keep class android.support.v4.** { *; }
-keep interface android.support.v4.app.** { *; }
-keep public class * extends android.app.Fragment
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider

# Preserve all View implementations, their special context constructors, and
# their setters.

-keep public class * extends android.view.View {
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
    public void set*(...);
}

# Preserve all classes that have special context constructors, and the
# constructors themselves.

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}

# Preserve all classes that have special context constructors, and the
# constructors themselves.

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

# Preserve the special fields of all Parcelable implementations.

-keepclassmembers class * implements android.os.Parcelable {
    static android.os.Parcelable$Creator CREATOR;
}

# Preserve static fields of inner classes of R classes that might be accessed
# through introspection.

-keepclassmembers class **.R$* {
  public static <fields>;
}

# Preserve the required interface from the License Verification Library
# (but don't nag the developer if the library is not used at all).

-keep public interface com.android.vending.licensing.ILicensingService

-dontnote com.android.vending.licensing.ILicensingService

# The Android Compatibility library references some classes that may not be
# present in all versions of the API, but we know that's ok.

-dontwarn android.support.**

-dontwarn android.util.FloatMath

# Preserve all native method names and the names of their classes.

-keepclasseswithmembernames class * {
    native <methods>;
}

# Preserve the special static methods that are required in all enumeration
# classes.

-keepclassmembers class * extends java.lang.Enum {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# Explicitly preserve all serialization members. The Serializable interface
# is only a marker interface, so it wouldn't save them.
# You can comment this out if your application doesn't use serialization.
# If your code contains serializable classes that have to be backward
# compatible, please refer to the manual.

-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

# Your application may contain more items that need to be preserved;
# typically classes that are dynamically created using Class.forName:

# -keep public class mypackage.MyClass
# -keep public interface mypackage.MyInterface
# -keep public class * implements mypackage.MyInterface

# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/dentringer/Library/Android/sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

#OkHttp, Gson
-keepattributes *Annotation*
-keepattributes Signature
-keep class com.squareup.okhttp.** { *; }
-keep interface com.squareup.okhttp.** { *; }
-dontwarn com.squareup.okhttp.**
-dontwarn rx.**

#required for okhttp 3.8.0 +
-dontwarn okio.**
-dontwarn javax.annotation.**

#Retrofit
# Platform calls Class.forName on types which do not exist on Android to determine platform.
-dontnote retrofit2.Platform
# Platform used when running on Java 8 VMs. Will not be used at runtime.
-dontwarn retrofit2.Platform$Java8
# Retain generic type information for use by reflection by converters and adapters.
-keepattributes Signature
# Retain declared checked exceptions for use by a Proxy instance.
-keepattributes Exceptions


-keep class sun.misc.Unsafe { *; }
-dontwarn java.nio.file.*
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement

# Crittercism
-dontwarn com.crittercism.**
-keep public class com.crittercism.**
-keepclassmembers public class com.crittercism.* {
    *;
}
-keepattributes SourceFile, LineNumberTable

# Appsee
-keep class com.appsee.** { *; }
-dontwarn com.appsee.**
-keep class android.support.** { *; }
-keep interface android.support.** { *; }

# Keep a fixed source file attribute and all line number tables to get line
# numbers in the stack traces.
# You can comment this out if you're not interested in stack traces.
-keepattributes SourceFile, LineNumberTable


-dontwarn sun.misc.**
-keep class sun.misc.**
# Dagger
#-keep class * extends dagger.internal.Binding
#-keep class * extends dagger.internal.ModuleAdapter
#-keep class * extends dagger.internal.StaticInjectionï»¿
#-keep class * extends dagger.internal.BindingsGroup
#-dontwarn dagger.internal.**

# don't obfuscate localytics
-keep class com.localytics.android.** { *; }

-keepattributes JavascriptInterface
-dontwarn com.localytics.android.**

# google maps for work : as seen here:https://github.com/google/iosched/blob/2531cbdbe27e5795eb78bf47d27e8c1be494aad4/android/proguard-project.txt#L120
-dontwarn com.google.common.cache.**

-keep class com.facebook.stetho.** { *; }
-dontwarn com.facebook.stetho.**

-keep class io.card.**
-keepclassmembers class io.card.** {
    *;
}

-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}

# ForeSee
-keep class com.foresee.** { *;}
-keep class fs.** { *; }
-keepattributes *Annotation*
-keepattributes Signature

#GSON
-keep class com.google.gson.** { *; }

#to avoid removing mapped but not used fields from models and responces.
-keepclassmembers class com.ehi.enterprise.android.models.** {
    private <fields>;
}

-keepclassmembers class com.ehi.enterprise.android.network.** {
    private <fields>;
}