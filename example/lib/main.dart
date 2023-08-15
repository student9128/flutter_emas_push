import 'dart:math';

import 'package:flutter/material.dart';
import 'dart:async';

import 'package:flutter/services.dart';
import 'package:flutter_emas_push/flutter_emas_push.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatefulWidget {
  const MyApp({Key? key}) : super(key: key);

  @override
  State<MyApp> createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  String _platformVersion = 'Unknown';

  @override
  void initState() {
    super.initState();
    initPlatformState();
  }

  // Platform messages are asynchronous, so we initialize in an async method.
  Future<void> initPlatformState() async {
    String platformVersion;
    // Platform messages may fail, so we use a try/catch PlatformException.
    // We also handle the message potentially returning null.
    try {
      platformVersion =
          await FlutterEmasPush.platformVersion ?? 'Unknown platform version';
    } on PlatformException {
      platformVersion = 'Failed to get platform version.';
    }

    // If the widget was removed from the tree while the asynchronous platform
    // message was in flight, we want to discard the reply rather than calling
    // setState to update our non-existent appearance.
    if (!mounted) return;

    setState(() {
      _platformVersion = platformVersion;
    });
  }

  initPush() async {
    FlutterEmasPush.initPush();
    FlutterEmasPush.setNotificationChannelIdAndName("123456", "androidTest");
    FlutterEmasPush.registerWithMetaData();
  }

  registerOppo() async {
    FlutterEmasPush.registerOppo("", "");
  }

  bindAccount() async {
    FlutterEmasPush.bindAccount('testAccount');
  }

  unBindAccount() async {
    FlutterEmasPush.unbindAccount();
  }

  bindTag() async {
    FlutterEmasPush.bindTag(1, ['tags'], 'alias');
  }

  unbindTag() async {
    FlutterEmasPush.unbindTag(1, ['tags'], 'alias');
  }

  addAlias() async {
    FlutterEmasPush.addAlias('alias');
  }

  removeAlias() async {
    FlutterEmasPush.removeAlias('alias');
  }

  testPush() async {
    FlutterEmasPush.showLog(true);
    FlutterEmasPush.setNotificationChannelIdAndName("123456", "androidTest");
    var bool = await FlutterEmasPush.canShowNotification();
    print('hello===$bool');
    if (bool) {
      FlutterEmasPush.showNotification(
          "测试${Random().nextInt(100)}",
          "测试内容${Random().nextInt(100)}",
          "{\"_ALIYUN_NOTIFICATION_ID_\":\"542992\",\"mobileId\":\"27203753f1e1d4892143c6d8ce86867a\",\"_ALIYUN_NOTIFICATION_MSG_ID_\":\"8004229324234496\",\"id\":\"582\",\"businessType\":\"QEUBEELIVE\",\"_ALIYUN_NOTIFICATION_PRIORITY_\":\"1\"}",
          "${Random().nextInt(10000)}");
    } else {
      FlutterEmasPush.goSettingNotificationPage();
    }
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),
        ),
        body: Column(
          children: [
            Text('Running on: $_platformVersion\n'),
            ElevatedButton(
                onPressed: () {
                  initPush();
                },
                child: Text('initPush')),
            ElevatedButton(
                onPressed: () {
                  registerOppo();
                },
                child: Text('registerOppo')),
            ElevatedButton(
                onPressed: () {
                  testPush();
                },
                child: Text('testPush')),
            ElevatedButton(
                onPressed: () {
                  FlutterEmasPush.registerWithMetaData();
                },
                child: Text('registerWithMetaData')),
            ElevatedButton(
                onPressed: () {
                  FlutterEmasPush.cancelNotification();
                },
                child: Text('cancelNotification')),
            ElevatedButton(
                onPressed: () {
                  FlutterEmasPush.showNotification("测试指定Id","id是10","ext","10");
                },
                child: Text('showSpecificNotificationById')),
            ElevatedButton(
                onPressed: () {
                  FlutterEmasPush.cancelNotificationById("10");
                },
                child: Text('cancelNotificationById')),
          ],
        ),
      ),
    );
  }
}
