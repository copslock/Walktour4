# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in D:\IDE\sdk/tools/proguard/proguard-android.txt
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

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile
# 代码混淆压缩比，在0和7之间，默认为5，一般不需要改
-optimizationpasses 5

# 混淆时不使用大小写混合，混淆后的类名为小写
-dontusemixedcaseclassnames

# 指定不去忽略非公共的库的类
-dontskipnonpubliclibraryclasses

# 指定不去忽略非公共的库的类的成员
-dontskipnonpubliclibraryclassmembers

# 不做预校验，preverify是proguard的4个步骤之一
# Android不需要preverify，去掉这一步可加快混淆速度
-dontpreverify

# 有了verbose这句话，混淆后就会生成映射文件
# 包含有类名->混淆后类名的映射关系
# 然后使用printmapping指定映射文件的名称
-verbose
# 指定混淆时采用的算法，后面的参数是一个过滤器
# 这个过滤器是谷歌推荐的算法，一般不改变
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*
# 保护代码中的Annotation不被混淆，这在JSON实体映射时非常重要，比如fastJson
-keepattributes *Annotation*

# 避免混淆泛型，这在JSON实体映射时非常重要，比如fastJson
-keepattributes Signature

#抛出异常时保留代码行号，在异常分析中可以方便定位
-keepattributes SourceFile,LineNumberTable
# 保留所有的本地native方法不被混淆
-keepclasseswithmembernames class * {
    native <methods>;
}
#-dontoptimize
#忽略警告信息
-dontwarn
-ignorewarnings
-renamesourcefileattribute SourceFile
#忽略jar包
-keep class com.dingli.samsungvolte.**{*;}
-keep class io.vov.**{	*;	}
-keep class com.dinglicom.autotesthandy.script.**{*;}
-keep class com.android.**{	*; 	}
-keep class com.walktour.setting.MyProvider
-keep class com.tencent.**  {* ;}
-keep class com.qq.**  {* ;}
-keep class okio.** { *;}
-keep class okhttp3.** { *;}
-keep class com.sun.** { *;}
-keep class javax.** { *;}
-keep class com.jg.** { *;}
-keep class org.** { *;}
-keep class com.innsmap.** { *;}
-keep class com.google.**{	*;	}
-keep class com.datatests.** { *; }
-keep class com.dingli.app.** { *; }
-keep class cn.dolit.**{	*;	}
-keep class com.dingli.dmplayer.**{	*;	}
-keep class com.huawei.**{	*;	}

-keep class vi.com.gdi.bgl.android.**{*;}
-keep class com.dt.**{	*;	}
-dontwarn android.**
-keep class android.** { *; }
-keep public class * extends android.**
-keep class com.alibaba.**{*;}
-keep class myjava.**{*;}
-keep class butterknife.**{*;}
-keep class dagger.**{*;}
-keep class com.squareup.**{*;}
-keep class junit.**{*;}
-keep class retrofit2.**{*;}

-keep public class de.opticom.**{  *;}
-keep public class com.dingli.scanner.**{ *;}
-keep public class com.dingli.wlan.**{ *;}
-keep public class com.walktour.wifip2p.**{ *;}
-keep public class com.walktour.gui.upgrade.**{ *;}
-keep public class com.walktour.gui.R**{ *;}

-keep public class com.walktour.framework.view.dragsortlistview.**{ *;}
-keep public class com.walktour.framework.ui.**{ *;}
-keep class com.walktour.wifip2p.WiFiDirectActivity$*{ *;}
-keep class com.walktour.wifip2p.DeviceListFragment$*{ *;}
-keep class com.dingli.player.**{ *;}
-keep public class com.dinglicom.data.**{ *;}

-keep class com.android.internal.telephony.ITelephony { *; }

#AndroidManifest.xml 配置文件中配置的类不能混淆
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.preference.Preference
#-keep public class * extends android.app.Fragment
-keep public class * extends android.widget.TabHost
#-keep public class * extends android.app.ListFragment
#-keep public class * extends android.app.DialogFragment
-keep public class * extends android.widget.FrameLayout
#-keep public class * extends com.walktour.gui.applet.ControlBar
-keep public class * extends android.support.v7.widget.AppCompatImageView
-keep public class * extends android.view.View

-keep public class com.walktour.Utils.StructParseUtil
-keep abstract class java.util.LinkedHashMap.BaseStructParseModel
-keep public class * extends java.util.LinkedHashMap.BaseStructParseModel
-keep public class com.walktour.gui.task.activity.scanner.model.**{ *;}
#网络请求实体类不能混淆
-keep public interface com.walktour.base.gui.model.BaseNetModel{public *;}
-keep class * implements com.walktour.base.gui.model.BaseNetModel {
<methods>;
<fields>;
}
-keep class com.walktour.gui.singlestation.net.model.**{* ;}
#-keep class com.tencent.mid.**  {* ;}
-keep class com.walktour.gui.share.**{* ;}
# eventBus传递的对象不混淆
-keep class com.walktour.base.model.**{* ;}
-keep public class * extends com.walktour.base.model.result.BaseQueryResult{ *;}
-keep public class * extends com.walktour.base.model.params.BaseQueryParams{ *;}
#greendao不混淆
-keepclassmembers class * extends org.greenrobot.greendao.AbstractDao {
public static java.lang.String TABLENAME;
}
-keep class **$Properties
# 以下类事件的命名和配置文件对应，不能混淆
-keep public class com.dinglicom.dataset.model.DataSetEvent{  *;}

# 该类会被传入JNI层 	不能混淆
-keep public class com.walktour.control.bean.packet_dissect_info{ *;}
-keep public class com.dingli.service.test.tcpipmonitorsojni{ *;}
-keep public class com.walktour.control.bean.tcpipmonitorso_start_params{ *;}
-keep public class com.dingli.service.test.network_diagnose_tree{ *;}
-keep public class com.walktour.model.DecoderModel{ *;}
-keep public class com.walktour.model.DecoderModel{ *;}
-keep public class com.dingli.scanner.service.DecoderModel{ *;}
-keep public class com.walktour.model.ResultModel{ *;}
-keep public class com.walktour.model.TcpModel{ *;}
-keep public class com.walktour.service.FleetService{ *;}
-keep public class com.walktour.service.WalktourClient{ *;}
-keep public class com.walktour.service.iPackTerminal{*;}
-keep public class com.dingli.scanner.service.ScannerTestConfig{*;}
-keep public class com.walktour.service.app.License.Client{ *;}
-keep public class com.dinglicom.dataset.DataCenter{*;}
-keep public class com.dingli.watcher.model.MetroGPS{ *;}
-keep public class com.dingli.watcher.model.MetroRunParamInfo{ *;}
-keep public class com.dingli.watcher.jni.GNVControllerJNI{ *;}
-keep public class com.dingli.watcher.model.GNVRoomPoint{ *;}
-keep public class com.dingli.watcher.model.GNVMetroGPS{ *;}
-keep public class com.dingli.watcher.model.GNVAcceleration{ *;}
# JNI回调函数  	不能混淆
-keep public class com.dinglicom.DataSetLib{
    public void  dsCallBack(java.lang.String);
}
-keep public class com.dinglicom.fleet.FleetSocket{
    public void  callback(int,java.lang.String);
}
-keep public class com.walktour.service.TraceInfo{
    public void  decodercallback(java.lang.String);
}
-keep public class com.walktour.service.app.FleetNewService{
    public void fleetcallback(int,int);
    public void fleetstrcallback(java.lang.String);
}
-keep public class com.walktour.service.FtpTest{
    public void callback(java.lang.String);
}
    -keep public class com.dingli.scanner.service.ScannerTest{
    public void decodercallback(java.lang.String);
}
-keep public class com.walktour.service.HttpTest{
    public void callback(java.lang.String);
}
-keep public class com.walktour.service.MmsTest{
    public void callback(java.lang.String);
}
-keep public class com.walktour.service.app.VersionUpgradeService{
    public void fleetcallback(int,int);
}
-keep public class com.walktour.service.WapTest{
    public void callback(java.lang.String);
}
-keep public class com.walktour.service.Scanfile{
    public void callback(java.lang.String);
}
-keep public class com.dingli.service.test.ipc2jni{
    public void msg_callback(int,int,java.lang.String,int,int,java.lang.String,int);
}
-keep public class com.dingli.service.test.filetransjni{ *;}
-keep public class com.dingli.service.test.fd_init_params{ *;}
-keep public class com.dingli.service.test.fd_file_item{ *;}
-keep public class com.dinglicom.btu.comlib{ *;}
-keep public class com.dingli.watcher.jni.MetroJNI{ *;}

# View层构造方法 	不能混淆
-keepclasseswithmembernames class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}
-keepclasseswithmembernames class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

#保持EventBus的方法不混淆
-keepclassmembers class ** {
    @org.greenrobot.eventbus.Subscribe <methods>;
}
-keep enum org.greenrobot.eventbus.ThreadMode { *; }

# Only required if you use AsyncExecutor
-keepclassmembers class * extends org.greenrobot.eventbus.util.ThrowableFailureEvent {
    <init>(Java.lang.Throwable);
}
# 枚举类valueOf 	不能混淆
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# 保留Parcelable序列化的类不被混淆
-keep class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}

# 保留Serializable序列化的类不被混淆
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}
#高德地图
 -keep class com.amap.api.maps.**{*;}
 -keep class com.autonavi.**{*;}
 -keep class com.amap.api.trace.**{*;}
 -keep class com.amap.api.location.**{*;}
 -keep class com.amap.api.fence.**{*;}
 -keep class com.autonavi.aps.amapapi.model.**{*;}
 -keep class com.amap.api.services.**{*;}
-keep class com.alibaba.idst.nls.** {*;}
-keep class com.google.**{*;}
-keep class com.nlspeech.nlscodec.** {*;}
-keep class com.amap.api.navi.**{*;}
-keep class com.autonavi.**{*;}
-keep class com.jcraft.jsch.**{*;}
# 百度地图
-keep class com.baidu.** { *; }
-keep class mapsdkvi.com.** {*;}
-dontwarn com.baidu.**
