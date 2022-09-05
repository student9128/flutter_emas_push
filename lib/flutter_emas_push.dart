import 'dart:async';

import 'package:flutter/services.dart';

class FlutterEmasPush {
  static const MethodChannel _channel = MethodChannel('flutter_emas_push');

  static MethodChannel get channel => _channel;

  static Future<String?> get platformVersion async {
    final String? version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }

  static initPush() async {
    await _channel.invokeMethod("initPush");
  }
  static registerOppo() async{
    await _channel.invokeMethod("registerOppo");
  }
  static testPush() async{
    await _channel.invokeMethod("testPush");
  }
}
