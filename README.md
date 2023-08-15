# flutter_emas_push

A flutter plugin integrating EMAS push.

# How to use

> config the push keys in build.gradle

```groovy
 defaultConfig {
    applicationId "com.kevin.emaspush.flutter_emas_push_example"//申请key的包名
    ...
    manifestPlaceholders = [
            emasPushAppKey   : "",//阿里云的appKey
            emasPushAppSecret: "",//阿里云的appSecret
            huaweiAppId      : "",//华为的一定要配置签名文件
            miAppId          : "",
            miAppKey         : "",
            oppoAppKey       : "",
            oppoAppSecret    : "",
            vivoAppKey       : "",
            vivoAppId        : "",//数字的要用反斜杠\\123456
            meizuAppId       : "",
            meizuAppKey      : "",
            sendId           : "",//GCM
            applicatinoId    : "",//GCM
            projectId        : "",//GCM
            apiKey           : ""//GCM
    ]
}
```

> config the signature in build.gradle(huawei push needs it )

```groovy
 signingConfigs {
    debug {
        storeFile file('xxx')
        storePassword 'xxx'
        keyAlias 'xxx'
        keyPassword 'xxx'
    }
    release {
        storeFile file('xxx')
        storePassword 'xxx'
        keyAlias 'xxx'
        keyPassword 'xxx'
    }
}
```

> add this code in AndroidManifest.xml (vivo push needs `<data android:host="${applicationId}"
android:path="/thirdpush"
android:scheme="agoo" />`)

* if you find you can not receive push when offline, please check it.


```xml

<activity android:name=".PopupPushActivity" android:exported="true">
    <intent-filter>
        <action android:name="android.intent.action.VIEW" />

        <category android:name="android.intent.category.DEFAULT" />
        <category android:name="android.intent.category.BROWSABLE" />
        <!--vivo一定要配置applicationId-->
        <data android:host="${applicationId}" android:path="/thirdpush" android:scheme="agoo" />
    </intent-filter>
</activity>
```
if you need jump to your MainActivity when click notification, `PopupPushActivity` is not needed. we need following code.
```kotlin
   PopupNotifyClick { title, summary, extMap ->
    Log.d("MainActivity", "Receive notification, title: $title, content: $summary, extraMap:$extMap")}
    .onCreate(this,this.intent)

```

> initPush
> if you config the manifestPlaceholders ,you can use `registerWithMetaData`
```dart
    FlutterEmasPush.initPush();
    // android 8.0 or higher need notification channel id and channel name
    FlutterEmasPush.setNotificationChannelIdAndName("123456", "androidTest");
    FlutterEmasPush.registerWithMetaData();
```
>  register specified platform push
```dart
     FlutterEmasPush.registerOppo("","");
     FlutterEmasPush.registerXiaomi("","");
     FlutterEmasPush.registerHuawei();
     FlutterEmasPush.registerMeizu();
     FlutterEmasPush.registerGCM("","","","");

```
> bind or unbind account/tag/alias
```dart
     FlutterEmasPush.bindAccount('testAccount');
     FlutterEmasPush.unbindAccount();

     FlutterEmasPush.bindTag(1, ['tags'], 'alias');
     FlutterEmasPush.unbindTag(1, ['tags'], 'alias');

     FlutterEmasPush.addAlias('alias');
     FlutterEmasPush.removeAlias('alias');

```
> receive notification when online, listening `onNotificationOpened` like following code.
```dart
 _initPushMethodCallHandler() async{
    var channel = FlutterEmasPush.channel;
    channel.setMethodCallHandler((call)async{
      switch(call.method){
        case 'onNotificationOpened':
          debugPrint('notificationOpened===========${call.arguments}');
       
          break;
      }
    });

  }
```
> show log or set emas log level
```dart
FlutterEmasPush.showLog(kDebugMode);
FlutterEmasPush.showEMASLogLevel(2);

> cancel notification
```dart
FlutterEmasPush.cancelNotification();

FlutterEmasPush.cancelNotificationById("10");
```

```
> if you find app crash when releasing app with obfuscating. maybe you need add these code to your `proguard-rules.pro`
```java
    -keepclasseswithmembernames class ** {
        native <methods>;
        }
    -keepattributes Signature
    -keep class sun.misc.Unsafe { *; }
    -keep class com.taobao.** {*;}
    -keep class com.alibaba.** {*;}
    -keep class com.alipay.** {*;}
    -keep class com.ut.** {*;}
    -keep class com.ta.** {*;}
    -keep class anet.**{*;}
    -keep class anetwork.**{*;}
    -keep class org.android.spdy.**{*;}
    -keep class org.android.agoo.**{*;}
    -keep class android.os.**{*;}
    -keep class org.json.**{*;}
    -dontwarn com.taobao.**
    -dontwarn com.alibaba.**
    -dontwarn com.alipay.**
    -dontwarn anet.**
    -dontwarn org.android.spdy.**
    -dontwarn org.android.agoo.**
    -dontwarn anetwork.**
    -dontwarn com.ut.**
    -dontwarn com.ta.**


    # 华为通道
    -keep class com.huawei.** {*;}
    -dontwarn com.huawei.**

    # 小米通道
    -keep class com.xiaomi.** {*;}
    -dontwarn com.xiaomi.**

    # OPPO通道
    -keep public class * extends android.app.Service

    # 魅族通道
    -keep class com.meizu.cloud.** {*;}
    -dontwarn com.meizu.cloud.**

    # VIVO通道
    -keep class com.vivo.** {*;}
    -dontwarn com.vivo.**

    # GCM/FCM通道
    -keep class com.google.firebase.**{*;}
    -dontwarn com.google.firebase.**

```

