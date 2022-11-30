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

  static registerWithMetaData() async {
    await _channel.invokeMethod("registerWithMetaData");
  }

  /// register oppo push
  static registerOppo(String appKey, String appSecret) async {
    Map<String, String> params = {"appKey": appKey, "appSecret": appSecret};
    await _channel.invokeMethod("registerOppo", params);
  }

  /// register mi push
  static registerXiaomi(String xiaomiId, String xiaomiKey) async {
    Map<String, String> params = {"xiaomiId": xiaomiId, "xiaomiKey": xiaomiKey};
    await _channel.invokeMethod("registerXiaomi", params);
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
  static registerMeizu(String appId, String appKey) async {
    Map<String, String> params = {"appId": appId, "appKey": appKey};
    await _channel.invokeMethod("registerMeizu", params);
  }

  /// register GCM
  static registerGCM(String sendId, String applicationId, String projectId,
      String apiKey) async {
    Map<String, String> params = {
      "sendId": sendId,
      "applicationId": applicationId,
      "projectId": projectId,
      "apiKey": apiKey
    };
    await _channel.invokeMethod("registerGCM", params);
  }

  /// 将应用内账号和推送通道相关联，可以实现按账号的定点消息推送
  ///
  /// account 应用内账号
  static Future<String> bindAccount(String account) async {
    Map<String, String> params = {"account": account};
    return await _channel.invokeMethod("bindAccount", params);
  }

  static Future<String> unbindAccount() async {
    return await _channel.invokeMethod("unbindAccount");
  }

  /// 绑定标签到指定目标。
  /// * 支持向设备、账号和别名绑定标签，绑定类型有参数target指定。
  /// * App最多支持定义1万个标签，单个标签支持的最大长度为129字符。
  /// * 绑定标签在10分钟内生效。
  /// * 不建议在单个标签上绑定超过十万级设备，否则，发起对该标签的推送可能需要较长的处理时间，无法保障响应速度。
  ///
  /// 此种情况下，建议您采用全推方式，或将设备集合拆分到更细粒度的标签，多次调用推送接口分别推送给这些标签来避免此问题。
  ///
  /// target 目标类型可选值：1:本设备,2:本设备绑定的账号,3:别名
  ///
  ///
  static Future<String> bindTag(
      int target, List<String> tags, String? alias) async {
    Map<String, dynamic> params = {
      "tagTarget": target,
      "tags": tags,
      "alias": alias
    };
    return await _channel.invokeMethod("bindTag", params);
  }

  static Future<String> unbindTag(
      int target, List<String> tags, String? alias) async {
    Map<String, dynamic> params = {
      "tagTarget": target,
      "tags": tags,
      "alias": alias
    };
    return await _channel.invokeMethod("unbindTag", params);
  }

  /// 为设备添加别名。
  static Future<String> addAlias(String alias) async {
    Map<String, String> params = {"alias": alias};
    return await _channel.invokeMethod("addAlias", params);
  }

  static Future<String> removeAlias(String alias) async {
    Map<String, String> params = {"alias": alias};
    return await _channel.invokeMethod("removeAlias", params);
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

/// 判断设备是否对app开启通知权限
/// 
  static Future<bool> canShowNotification() async {
    return await _channel.invokeMethod("canShowNotification");
  }

/// 去设置页面
  static goSettingNotificationPage() async {
    await _channel.invokeMethod("goSettingPage");
  }

/// just for testing
/// 
  static Future testPush(String title, String content) async {
    Map<String, String> params = {"title": title, "content": content};
    await _channel.invokeMethod("testPush", params);
  }
}
