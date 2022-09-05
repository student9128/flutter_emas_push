package com.kevin.emaspush.flutter_emas_push

import android.app.*
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.util.Log
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
    private lateinit var channel: MethodChannel
    private lateinit var activity: Activity
    private lateinit var context: Context
    private var channelID:String?=null
    private var channelName:String?=null

    companion object {
        val TAG = "FlutterEmasPushPlugin"
    }

    override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        channel = MethodChannel(flutterPluginBinding.binaryMessenger, "flutter_emas_push")
        channel.setMethodCallHandler(this)
        context = flutterPluginBinding.applicationContext
    }

    override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {
        when (call.method) {
            "getPlatformVersion"->result.success("Android ${android.os.Build.VERSION.RELEASE}")
            "initPush" -> {
                initPush()
            }
            "setNotificationChannelIdAndName"->{
                channelID = call.argument<String>("channelId")
                channelName = call.argument<String>("channelName")
            }
            "registerHuawei"->registerHuawei()
            "registerXiaomi"->{
                val xiaomiId = call.argument<String>("xiaomiId")
                val xiaomiKey = call.argument<String>("xiaomiKey")
                registerXiaomi(xiaomiId?:"",xiaomiKey?:"")
            }
            "registerOppo"->{
                Log.d(TAG, "init cloudchannel  oppo")
                val appKey = call.argument<String>("appKey")
                val appSecret = call.argument<String>("appSecret")
                registerOppo(appKey?:"",appSecret?:"")
            }
            "registerVivo"->registerVivo()
            "registerMeizu"->{
                val appId = call.argument<String>("appId")
                val appKey = call.argument<String>("appKey")
                registerMeizu(appId?:"",appKey?:"")
            }
            "registerGCM"->{
                val sendId = call.argument<String>("sendId")
                val applicationId = call.argument<String>("applicationId")
                val projectId = call.argument<String>("projectId")
                val apiKey = call.argument<String>("apiKey")
                registerGCM(sendId?:"",applicationId?:"",projectId?:"",apiKey?:"")
            }
            "testPush"->{
                testPush()
            }
            else -> result.notImplemented()
        }
    }

    override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
        channel.setMethodCallHandler(null)
    }
    fun showNotificationN(title:String,summary:String){
        showNotification(context,title,summary,channelID?:"",channelName?:"")
    }
    fun testPush(){
        var intent = Intent(Intent.ACTION_MAIN, null)
        intent.addCategory(Intent.CATEGORY_LAUNCHER)
        intent.setPackage(context.packageName)
        val packageManager = activity.packageManager
        val activities = packageManager.queryIntentActivities(intent, 0)
        val resolveInfo = activities.iterator().next()
        if(resolveInfo!=null){
            val packageName = resolveInfo.activityInfo.packageName
            val name = resolveInfo.activityInfo.name
            var cn = ComponentName(packageName,name)
            intent.component=cn
        }
//        val intent = Intent()
        val pendingIntent =  if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.S) PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        else PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        val mNManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notification2: Notification
        val builder: Notification.Builder
        builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification.Builder(context, "12345667")
        } else {
            Notification.Builder(context)
        }
        //设置标题
        //设置标题
        builder.setContentTitle("test")
        //设置内容
        //设置内容
        builder.setContentText("EmasPush test")
        //设置状态栏显示的图标，建议图标颜色透明
        //设置状态栏显示的图标，建议图标颜色透明
        builder.setSmallIcon(context.applicationInfo.icon)
        // 设置通知灯光（LIGHTS）、铃声（SOUND）、震动（VIBRATE）、（ALL 表示都设置）
        // 设置通知灯光（LIGHTS）、铃声（SOUND）、震动（VIBRATE）、（ALL 表示都设置）
        builder.setDefaults(Notification.DEFAULT_ALL)
        //灯光三个参数，颜色（argb）、亮时间（毫秒）、暗时间（毫秒）,灯光与设备有关
        //灯光三个参数，颜色（argb）、亮时间（毫秒）、暗时间（毫秒）,灯光与设备有关
        builder.setLights(Color.RED, 200, 200)
        // 铃声,传入铃声的 Uri（可以本地或网上）我这没有铃声就不传了
        // 铃声,传入铃声的 Uri（可以本地或网上）我这没有铃声就不传了
        builder.setSound(Uri.parse(""))
        // 震动，传入一个 long 型数组，表示 停、震、停、震 ... （毫秒）
        // 震动，传入一个 long 型数组，表示 停、震、停、震 ... （毫秒）
        builder.setVibrate(longArrayOf(0, 200, 200, 200, 200, 200))
        // 通知栏点击后自动消失
        // 通知栏点击后自动消失
        builder.setAutoCancel(true)
        // 简单通知栏设置 Intent
        // 简单通知栏设置 Intent
        builder.setContentIntent(pendingIntent)
        builder.setPriority(Notification.PRIORITY_HIGH)

        //设置下拉之后显示的图片
//        builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher));

        //设置下拉之后显示的图片
//        builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel =
                NotificationChannel("12345667", "安卓10aa", NotificationManager.IMPORTANCE_DEFAULT)
            channel.enableLights(true) //是否在桌面icon右上角展示小红点
            channel.lightColor = Color.GREEN //小红点颜色
            channel.setShowBadge(false) //是否在久按桌面图标时显示此渠道的通知
            mNManager.createNotificationChannel(channel)
        }
        notification2 = builder.build()
        mNManager.notify(2, notification2)
    }

    private fun initPush() {
        PushServiceFactory.init(activity)
        val pushService = PushServiceFactory.getCloudPushService()
        pushService.setLogLevel(CloudPushService.LOG_DEBUG) //仅适用于Debug包，正式包不需要此行
        pushService.register(activity, object : CommonCallback {
            override fun onSuccess(response: String?) {
                var deviceId = pushService.deviceId
                Log.d(TAG, "init cloudchannel success==$response,,,deviceID=$deviceId")
            }

            override fun onFailed(errorCode: String, errorMessage: String) {
                Log.d(TAG,"init cloudchannel failed -- errorcode:$errorCode -- errorMessage:$errorMessage"
                )
            }
        })
    }
    fun registerHuawei(){
        Log.d(TAG,"init cloudchannel  -- registerHuawei")
        HuaWeiRegister.register(activity.application)
    }
    fun registerXiaomi(xiaomiId:String,xiaomiKey:String){
        Log.d(TAG,"init cloudchannel  -- registerXiaomi")
        MiPushRegister.register(activity,xiaomiId,xiaomiKey)
    }
    fun registerOppo(appKey:String,appSecret:String){
        Log.d(TAG,"init cloudchannel  -- registerOppo")
        OppoRegister.register(activity,appKey,appSecret)
    }
    fun registerVivo(){
        VivoRegister.register(activity)
    }
    fun registerMeizu(appId:String,appKey:String){
        MeizuRegister.register(activity,appId,appKey)
    }
    fun registerGCM(sendId:String, applicationId:String, projectId:String, apiKey:String){
        //GCM/FCM辅助通道注册
        GcmRegister.register(activity, sendId, applicationId, projectId, apiKey); //sendId/applicationId/projectId/apiKey为已获得的参数
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