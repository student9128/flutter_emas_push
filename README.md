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

