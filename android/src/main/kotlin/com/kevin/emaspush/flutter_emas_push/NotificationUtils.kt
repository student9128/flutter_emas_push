package com.kevin.emaspush.flutter_emas_push

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build

fun showNotification(context:Context,title:String,summary:String,channelID:String,channelName:String){
    val intent = Intent()
    intent.setPackage(context.packageName)
    val pendingIntent = createPendingIntent(context, intent)
    val mNManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    val builder: Notification.Builder = createNotificationBuilder(context, pendingIntent,title, summary,channelID)

    //设置下拉之后显示的图片
//        builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher));
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel =
            NotificationChannel(channelID, channelName, NotificationManager.IMPORTANCE_DEFAULT)
        channel.enableLights(true) //是否在桌面icon右上角展示小红点
        channel.lightColor = Color.GREEN //小红点颜色
        channel.setShowBadge(false) //是否在久按桌面图标时显示此渠道的通知
        mNManager.createNotificationChannel(channel)
    }
    val notification2 = builder.build()
    mNManager.notify(2, notification2)
}

private fun createNotificationBuilder(
    context: Context,
    pendingIntent: PendingIntent?,
    title:String,summary:String,channelID:String
): Notification.Builder {
    val builder: Notification.Builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        Notification.Builder(context, channelID)
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
    return builder
}

private fun createPendingIntent(
    context: Context,
    intent: Intent
): PendingIntent? {
    val pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
    } else {
        PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }
    return pendingIntent
}