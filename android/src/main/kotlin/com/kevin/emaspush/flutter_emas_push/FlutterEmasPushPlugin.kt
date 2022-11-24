package com.kevin.emaspush.flutter_emas_push

import android.app.*
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.NonNull
import com.alibaba.sdk.android.push.CloudPushService
import com.alibaba.sdk.android.push.CommonCallback
import com.alibaba.sdk.android.push.huawei.HuaWeiRegister
import com.alibaba.sdk.android.push.noonesdk.PushServiceFactory
import com.alibaba.sdk.android.push.register.*

import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result

/** FlutterEmasPushPlugin */
class FlutterEmasPushPlugin : FlutterPlugin, MethodCallHandler, ActivityAware {
    /// The MethodChannel that will the communication between Flutter and native Android
    ///
    /// This local reference serves to register the plugin with the Flutter Engine and unregister it
    /// when the Flutter Engine is detached from the Activity
    private lateinit var context: Context

    companion object {
        private lateinit var activity: Activity
        val TAG = "FlutterEmasPushPlugin"
        private var channel: MethodChannel? = null
        private var channelID: String? = null
        private var channelName: String? = null
        fun showNotificationN(context: Context, title: String, summary: String) {
            Log.e(
                TAG,
                "FlutterEmasPushPlugin: your channel id is $channelID,channel name is $channelName "
            )
            showNotification(context, title, summary, channelID ?: "", channelName ?: "")
        }

        /**
         * no permission show notification,
         *
         * send message to flutter, your can show some tips to tell user
         */

        fun notificationDenied() {
            channel?.invokeMethod("notificationDenied", null)
        }

        fun launchApp() {
            Toast.makeText(activity, "打开app", Toast.LENGTH_SHORT).show()
//            var intent = Intent(Intent.ACTION_MAIN, null)
//            intent.addCategory(Intent.CATEGORY_LAUNCHER)
//            intent.setPackage(activity.packageName)
//            val packageManager = activity.packageManager
//            val activities = packageManager.queryIntentActivities(intent, 0)
//            val resolveInfo = activities.iterator().next()
//            if (resolveInfo != null) {
//                val packageName = resolveInfo.activityInfo.packageName
//                val name = resolveInfo.activityInfo.name
//                var cn = ComponentName(packageName, name)
//                intent.component = cn
//            }
//            activity.startActivity(intent)
        }

    }

    override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        channel = MethodChannel(flutterPluginBinding.binaryMessenger, "flutter_emas_push")
        channel?.setMethodCallHandler(this)
        context = flutterPluginBinding.applicationContext
    }

    override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {
        when (call.method) {
            "getPlatformVersion" -> result.success("Android ${android.os.Build.VERSION.RELEASE}")
            "initPush" -> {
                initPush()
            }
            "setNotificationChannelIdAndName" -> {
                channelID = call.argument<String>("channelId")
                channelName = call.argument<String>("channelName")
                Log.i(TAG, "channelId=$channelID,channelName=$channelName")
            }
            "registerWithMetaData" -> {
                Log.i(TAG, "you called registerWithMetaData")
                registerWithMetaData()
            }
            "registerHuawei" -> registerHuawei()
            "registerXiaomi" -> {
                val xiaomiId = call.argument<String>("xiaomiId")
                val xiaomiKey = call.argument<String>("xiaomiKey")
                registerXiaomi(xiaomiId ?: "", xiaomiKey ?: "")
            }
            "registerOppo" -> {
                Log.d(TAG, "init cloudchannel  oppo")
                val appKey = call.argument<String>("appKey")
                val appSecret = call.argument<String>("appSecret")
                registerOppo(appKey ?: "", appSecret ?: "")
            }
            "registerVivo" -> registerVivo()
            "registerMeizu" -> {
                val appId = call.argument<String>("appId")
                val appKey = call.argument<String>("appKey")
                registerMeizu(appId ?: "", appKey ?: "")
            }
            "registerGCM" -> {
                val sendId = call.argument<String>("sendId")
                val applicationId = call.argument<String>("applicationId")
                val projectId = call.argument<String>("projectId")
                val apiKey = call.argument<String>("apiKey")
                registerGCM(sendId ?: "", applicationId ?: "", projectId ?: "", apiKey ?: "")
            }
            "canShowNotification" -> {
                val checkCanShowNotification = checkCanShowNotification(context)
                Log.i(TAG, "you called canShowNotification,result=$checkCanShowNotification")
                result.success(checkCanShowNotification)
            }
            "goSettingPage" -> {
                goSettingPage(context)
            }
            "testPush" -> {
                val title = call.argument<String>("title")
                val content = call.argument<String>("content")
                Log.i(TAG, "you called testPush,title=$title,content=$content")
                testPush(title ?: "", content ?: "")
            }
            else -> result.notImplemented()
        }
    }

    override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
        channel?.setMethodCallHandler(null)
    }

    private fun testPush(title: String, content: String) {
        if (channelID.isNullOrEmpty() || channelName.isNullOrEmpty()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Toast.makeText(
                    activity,
                    "you must set notification channel id and name",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(
                    activity,
                    "you must set notification channel id and name in Android O or higher",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        showNotificationN(context, title, content)
//        var intent = Intent(Intent.ACTION_MAIN, null)
//        intent.addCategory(Intent.CATEGORY_LAUNCHER)
//        intent.setPackage(context.packageName)
//        val packageManager = activity.packageManager
//        val activities = packageManager.queryIntentActivities(intent, 0)
//        val resolveInfo = activities.iterator().next()
//        if (resolveInfo != null) {
//            val packageName = resolveInfo.activityInfo.packageName
//            val name = resolveInfo.activityInfo.name
//            var cn = ComponentName(packageName, name)
//            intent.component = cn
//        }
//        val pendingIntent =
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) PendingIntent.getActivity(
//                context,
//                0,
//                intent,
//                PendingIntent.FLAG_IMMUTABLE
//            )
//            else PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
//        val mNManager =
//            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//        val notification2: Notification
//        val builder: Notification.Builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            Notification.Builder(context, "12345667")
//        } else {
//            Notification.Builder(context)
//        }
//        //设置标题
//        //设置标题
//        builder.setContentTitle("test")
//        //设置内容
//        //设置内容
//        builder.setContentText("EmasPush test")
//        //设置状态栏显示的图标，建议图标颜色透明
//        //设置状态栏显示的图标，建议图标颜色透明
//        builder.setSmallIcon(context.applicationInfo.icon)
//        // 设置通知灯光（LIGHTS）、铃声（SOUND）、震动（VIBRATE）、（ALL 表示都设置）
//        // 设置通知灯光（LIGHTS）、铃声（SOUND）、震动（VIBRATE）、（ALL 表示都设置）
//        builder.setDefaults(Notification.DEFAULT_ALL)
//        //灯光三个参数，颜色（argb）、亮时间（毫秒）、暗时间（毫秒）,灯光与设备有关
//        //灯光三个参数，颜色（argb）、亮时间（毫秒）、暗时间（毫秒）,灯光与设备有关
//        builder.setLights(Color.RED, 200, 200)
//        // 铃声,传入铃声的 Uri（可以本地或网上）我这没有铃声就不传了
//        // 铃声,传入铃声的 Uri（可以本地或网上）我这没有铃声就不传了
//        builder.setSound(Uri.parse(""))
//        // 震动，传入一个 long 型数组，表示 停、震、停、震 ... （毫秒）
//        // 震动，传入一个 long 型数组，表示 停、震、停、震 ... （毫秒）
//        builder.setVibrate(longArrayOf(0, 200, 200, 200, 200, 200))
//        // 通知栏点击后自动消失
//        // 通知栏点击后自动消失
//        builder.setAutoCancel(true)
//        // 简单通知栏设置 Intent
//        // 简单通知栏设置 Intent
//        builder.setContentIntent(pendingIntent)
//        builder.setPriority(Notification.PRIORITY_HIGH)
//
//        //设置下拉之后显示的图片
////        builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher));
//
//        //设置下拉之后显示的图片
////        builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher));
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            val channel =
//                NotificationChannel("12345667", "安卓10aa", NotificationManager.IMPORTANCE_DEFAULT)
//            channel.enableLights(true) //是否在桌面icon右上角展示小红点
//            channel.lightColor = Color.GREEN //小红点颜色
//            channel.setShowBadge(false) //是否在久按桌面图标时显示此渠道的通知
//            mNManager.createNotificationChannel(channel)
//        }
//        notification2 = builder.build()
//        mNManager.notify(2, notification2)
    }

    private fun initPush() {
        PushServiceFactory.init(activity)
        val pushService = PushServiceFactory.getCloudPushService()
        pushService.setLogLevel(CloudPushService.LOG_DEBUG) //仅适用于Debug包，正式包不需要此行
        pushService.register(activity, object : CommonCallback {
            override fun onSuccess(response: String?) {
                var deviceId = pushService.deviceId
                Log.d(TAG, "init push success: $response,deviceID=$deviceId")
            }

            override fun onFailed(errorCode: String, errorMessage: String) {
                Log.d(
                    TAG, "init push failed: errorCode:$errorCode, errorMessage:$errorMessage"
                )
            }
        })
    }

    /**
     * config the key on AndroidManifest.xml,
     *
     * then called this method.
     */
    fun registerWithMetaData() {
        val applicationInfo = activity.packageManager.getApplicationInfo(
            activity.packageName,
            PackageManager.GET_META_DATA
        )
        val metaData = applicationInfo.metaData
        if (RomUtil.isEmui) {
            val huaweiId = metaData.getString("com.huawei.hms.client.appid")
            if (!huaweiId.isNullOrEmpty()) {
                registerHuawei()
            } else {
                Log.d(TAG, "register:no huawei push register,if you need,please config it.")
            }
        } else {
            Log.d(TAG, "This phone is not HUAWEI.")
        }
        if (RomUtil.isMiui) {
            val xiaomiId = metaData.getString("com.mi.push.app_id")
            val xiaomiKey = metaData.getString("com.mi.push.app_key")
            if (!xiaomiId.isNullOrEmpty() && !xiaomiKey.isNullOrEmpty()) {
                Log.d(TAG, "xiaomiId=$xiaomiId,xiaomiKey=$xiaomiKey")
                registerXiaomi("2882303761518975876", "5651897568876")
            } else {
                Log.d(TAG, "register:no xiaomi push register,if you need,please config it.")
            }
        } else {
            Log.d(TAG, "This phone is not XIAOMI.")
        }
        if (RomUtil.isOppo) {
            val oppoAppKey = metaData.getString("com.oppo.push.app_key")
            val oppoSecret = metaData.getString("com.oppo.push.app_secret")
            if (!oppoAppKey.isNullOrEmpty() && !oppoSecret.isNullOrEmpty()) {
                registerOppo(oppoAppKey, oppoSecret)
            } else {
                Log.d(TAG, "register:no oppo push register,if you need,please config it.")
            }
        } else {
            Log.d(TAG, "This phone is not OPPO.")
        }
        if (RomUtil.isVivo) {
            val vivoAppKey = metaData.getString("com.vivo.push.api_key")
            val vivoAppId = metaData.getString("com.vivo.push.app_id")
            if (!vivoAppKey.isNullOrEmpty() && !vivoAppId.toString().isNullOrEmpty()) {
                registerVivo()
            } else {
                Log.d(TAG, "register:no vivo push register,if you need,please config it.")
            }
        } else {
            Log.d(TAG, "This phone is not VIVO.")
        }
        if (RomUtil.isFlyme) {
            val meizuAppId = metaData.getString("com.meizu.push.app_id")
            val meizuAppKey = metaData.getString("com.meizu.push.app_key")
            if (!meizuAppId.isNullOrEmpty() && !meizuAppKey.isNullOrEmpty()) {
                registerMeizu(meizuAppId, meizuAppKey)
            } else {
                Log.d(TAG, "register:no meizu push register,if you need,please config it.")
            }
        } else {
            Log.d(TAG, "This phone is not MEIZU.")
        }
        val sendId = metaData.getString("com.google.firebase.send_id")
        val applicationId = metaData.getString("com.google.firebase.application_id")
        val projectId = metaData.getString("com.google.firebase.project_id")
        val apiKey = metaData.getString("com.google.firebase.api_key")
        if (!sendId.isNullOrEmpty() && !applicationId.isNullOrEmpty() && !projectId.isNullOrEmpty() && !apiKey.isNullOrEmpty()) {
            registerGCM(sendId, applicationId, projectId, apiKey)
        } else {
            Log.d(TAG, "register:no GCM push register,if you need,please config it.")
        }
    }

    private fun registerHuawei() {
        Log.d(TAG, "you called registerHuawei")
        HuaWeiRegister.register(activity.application)
    }

    private fun registerXiaomi(xiaomiId: String, xiaomiKey: String) {
        Log.d(TAG, "you called registerXiaomi,$xiaomiId,$xiaomiKey")
        MiPushRegister.register(activity.application, xiaomiId, xiaomiKey)
    }

    private fun registerOppo(appKey: String, appSecret: String) {
        Log.d(TAG, "you called registerOppo")
        OppoRegister.register(activity, appKey, appSecret)
    }

    private fun registerVivo() {
        Log.d(TAG, "you called registerVivo")
        VivoRegister.register(activity)
    }

    private fun registerMeizu(appId: String, appKey: String) {
        Log.d(TAG, "you called registerMeizu")
        MeizuRegister.register(activity, appId, appKey)
    }

    private fun registerGCM(sendId: String, applicationId: String, projectId: String, apiKey: String) {
        Log.d(TAG, "you called registerGCM")
        //GCM/FCM辅助通道注册
        GcmRegister.register(
            activity,
            sendId,
            applicationId,
            projectId,
            apiKey
        ); //sendId/applicationId/projectId/apiKey为已获得的参数
    }

    override fun onAttachedToActivity(binding: ActivityPluginBinding) {
        activity = binding.activity
    }

    override fun onDetachedFromActivityForConfigChanges() {
    }

    override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
    }

    override fun onDetachedFromActivity() {
    }

}
