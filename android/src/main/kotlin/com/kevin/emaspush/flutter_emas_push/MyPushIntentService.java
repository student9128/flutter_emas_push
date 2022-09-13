package com.kevin.emaspush.flutter_emas_push;

import android.content.Context;
import android.util.Log;

import com.alibaba.sdk.android.push.AliyunMessageIntentService;
import com.alibaba.sdk.android.push.notification.CPushMessage;

import java.util.Map;

public class MyPushIntentService extends AliyunMessageIntentService {
    @Override
    protected void onNotification(Context context, String title, String summary, Map<String, String> map) {
        Log.e("MyMessageReceiver", "service Receive notification, title: " + title + ", summary: " + summary + ", extraMap: " + map);
    }

    @Override
    protected void onMessage(Context context, CPushMessage cPushMessage) {
        Log.e("MyMessageReceiver", "service onMessage, messageId: " + cPushMessage.getMessageId() + ", title: " + cPushMessage.getTitle() + ", content:" + cPushMessage.getContent());
    }

    @Override
    protected void onNotificationOpened(Context context, String title, String summary, String extraMap) {
        Log.e("MyMessageReceiver", "service onNotificationOpened, title: " + title + ", summary: " + summary + ", extraMap:" + extraMap);
    }

    @Override
    protected void onNotificationClickedWithNoAction(Context context, String title, String summary, String extraMap) {
        Log.e("MyMessageReceiver", "service onNotificationClickedWithNoAction, title: " + title + ", summary: " + summary + ", extraMap:" + extraMap);
    }

    @Override
    protected void onNotificationRemoved(Context context, String s) {
        Log.e("MyMessageReceiver", "service onNotificationRemoved");
    }

    @Override
    protected void onNotificationReceivedInApp(Context context, String title, String summary, Map<String, String> map, int openType, String openActivity, String openUrl) {
        Log.e("MyMessageReceiver", "service onNotificationReceivedInApp, title: " + title + ", summary: " + summary + ", extraMap:" + map + ", openType:" + openType + ", openActivity:" + openActivity + ", openUrl:" + openUrl);

    }
}
