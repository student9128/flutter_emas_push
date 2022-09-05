package com.kevin.emaspush.flutter_emas_push

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.alibaba.sdk.android.push.MessageReceiver
import com.alibaba.sdk.android.push.notification.CPushMessage


class MyMessageReceiver : MessageReceiver() {
    public override fun onNotification(
        context: Context,
        title: String,
        summary: String,
        extraMap: Map<String, String>
    ) {

        var localReceiver = MyLocalReceiver()
        val localBroadcastManager = LocalBroadcastManager.getInstance(context)
        val filter = IntentFilter()
        localBroadcastManager.registerReceiver(localReceiver,filter)
        // TODO处理推送通知
        Log.e(TAG, "Receive notification, title: $title, summary: $summary, extraMap: $extraMap")
//        context.sendBroadcast()
        val intent = Intent()
        intent.setPackage(context.packageName)
        val pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        } else {
            PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        }
        val mNManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notification2: Notification
        val builder: Notification.Builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification.Builder(context, "123456")
        } else {
            Notification.Builder(context)
        }
        //设置标题
        builder.setContentTitle(title)
        //设置内容
        builder.setContentText(summary)
        //设置状态栏显示的图标，建议图标颜色透明
        builder.setSmallIcon(context.applicationInfo.icon)
        // 设置通知灯光（LIGHTS）、铃声（SOUND）、震动（VIBRATE）、（ALL 表示都设置）
        builder.setDefaults(Notification.DEFAULT_ALL)
        //灯光三个参数，颜色（argb）、亮时间（毫秒）、暗时间（毫秒）,灯光与设备有关
        builder.setLights(Color.RED, 200, 200)
        // 铃声,传入铃声的 Uri（可以本地或网上）我这没有铃声就不传了
        builder.setSound(Uri.parse(""))
        // 震动，传入一个 long 型数组，表示 停、震、停、震 ... （毫秒）
        builder.setVibrate(longArrayOf(0, 200, 200, 200, 200, 200))
        // 通知栏点击后自动消失
        builder.setAutoCancel(true)
        // 简单通知栏设置 Intent
        builder.setContentIntent(pendingIntent)
        builder.setPriority(Notification.PRIORITY_HIGH)

        //设置下拉之后显示的图片
//        builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel =
                NotificationChannel("123456", "安卓10a", NotificationManager.IMPORTANCE_DEFAULT)
            channel.enableLights(true) //是否在桌面icon右上角展示小红点
            channel.lightColor = Color.GREEN //小红点颜色
            channel.setShowBadge(false) //是否在久按桌面图标时显示此渠道的通知
            mNManager.createNotificationChannel(channel)
        }
        notification2 = builder.build()
        mNManager.notify(2, notification2)
    }

    public override fun onMessage(context: Context, cPushMessage: CPushMessage) {
        Log.e(
            TAG,
            "onMessage, messageId: " + cPushMessage.messageId + ", title: " + cPushMessage.title + ", content:" + cPushMessage.content
        )
    }

    public override fun onNotificationOpened(
        context: Context,
        title: String,
        summary: String,
        extraMap: String
    ) {
        Log.e(TAG, "onNotificationOpened, title: $title, summary: $summary, extraMap:$extraMap")
    }

    override fun onNotificationClickedWithNoAction(
        context: Context,
        title: String,
        summary: String,
        extraMap: String
    ) {
        Log.e(
            TAG,
            "onNotificationClickedWithNoAction, title: $title, summary: $summary, extraMap:$extraMap"
        )
    }

    override fun onNotificationReceivedInApp(
        context: Context,
        title: String,
        summary: String,
        extraMap: Map<String, String>,
        openType: Int,
        openActivity: String,
        openUrl: String
    ) {
        Log.e(
            TAG,
            "onNotificationReceivedInApp, title: $title, summary: $summary, extraMap:$extraMap, openType:$openType, openActivity:$openActivity, openUrl:$openUrl"
        )
    }

    override fun onNotificationRemoved(context: Context, messageId: String) {
        Log.e(TAG, "onNotificationRemoved")
    }

    companion object {
        const val TAG = "FlutterEmasPushPlugin"

        // 消息接收部分的LOG_TAG
        const val REC_TAG = "receiver"
    }
}