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

  /// register oppo push
  static registerOppo(String appKey,String appSecret) async {
    Map<String, String> params = {
      "appKey": appKey,
      "appSecret": appSecret
    };
    await _channel.invokeMethod("registerOppo",params);
  }

  /// register mi push
  static registerXiaomi(String xiaomiId,String xiaomiKey) async {
    Map<String, String> params = {
      "xiaomiId": xiaomiId,
      "xiaomiKey": xiaomiKey
    };
    await _channel.invokeMethod("registerXiaomi",params);
  }

  /// register huawei push
  static registerHuawei() async {
    await _channel.invokeMethod("registerHuawei");
  }

  /// register vivo push
  static registerVivo() async {
    await _channel.invokeMethod("registerVivo");
  }

  /// register meizu push
  static registerMeizu(String appId,String appKey) async {
    Map<String, String> params = {
      "appId": appId,
      "appKey": appKey
    };
    await _channel.invokeMethod("registerMeizu",params);
  }

  /// register GCM
  static registerGCM(String sendId,String applicationId,String projectId,String apiKey) async {
    Map<String, String> params = {
      "sendId": sendId,
      "applicationId": applicationId,
      "projectId": projectId,
      "apiKey": apiKey
    };
    await _channel.invokeMethod("registerGCM",params);
  }

  ///* channelId:  Notification channel id
  ///* channelName:  Notification channel name
  static setNotificationChannelIdAndName(
      String channelId, String channelName) async {
    Map<String, String> params = {
      "channelId": channelId,
      "channelName": channelName
    };
    await _channel.invokeMethod("setNotificationChannelIdAndName", params);
  }

  static testPush() async {
    await _channel.invokeMethod("testPush");
  }
}
