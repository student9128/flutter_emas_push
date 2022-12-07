package com.kevin.emaspush.flutter_emas_push

import android.app.*
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.annotation.NonNull
import com.alibaba.sdk.android.push.CloudPushService
import com.alibaba.sdk.android.push.CommonCallback
import com.alibaba.sdk.android.push.huawei.HuaWeiRegister
import com.alibaba.sdk.android.push.noonesdk.PushServiceFactory
import com.alibaba.sdk.android.push.notification.CPushMessage
import com.alibaba.sdk.android.push.register.*

import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import org.json.JSONObject

/** FlutterEmasPushPlugin */
class FlutterEmasPushPlugin : FlutterPlugin, MethodCallHandler, ActivityAware {
    private lateinit var mContext: Context
    private var mPushService: CloudPushService? = null

    companion object {
        private lateinit var activity: Activity
        const val TAG = "FlutterEmasPushPlugin"
        private var channel: MethodChannel? = null
        private var channelID: String? = null
        private var channelName: String? = null
        fun showNotificationN(context: Context, title: String, summary: String) {
            LogUtils.e(
                TAG,
                "FlutterEmasPushPlugin: your channel id is $channelID,channel name is $channelName "
            )
            showNotification(context, title, summary, channelID ?: "", channelName ?: "")
        }

        fun onNotificationReceived(
            title: String,
            summary: String,
            extraMap: Map<String, String>
        ) {
            var json = JSONObject()
            var jsonExt = JSONObject()
            for ((key, value) in extraMap) {
                jsonExt.put(key, value)
            }
            json.put("title", title)
            json.put("content", summary)
            json.put("extraParams", jsonExt.toString())
            channel?.invokeMethod("onNotificationReceived", json.toString())

        }

        /**
         * no permission show notification,
         *
         * send message to flutter, your can show some tips to tell user
         */

        fun onNotificationDenied() {
            channel?.invokeMethod("onNotificationDenied", null)
        }

        fun onNotificationOpened(title: String, summary: String, extraMap: String) {
            var json = JSONObject()
            json.put("title", title)
            json.put("content", summary)
            json.put("extraParams", JSONObject(extraMap))
            LogUtils.i(TAG, "notificationOpened====${json}")
            channel?.invokeMethod("onNotificationOpened", json.toString())
        }

        fun onMessageReceived(cPushMessage: CPushMessage) {

        }

        fun onNotificationClickedWithNoAction(
            title: String,
            summary: String,
            extraMap: String
        ) {
        }


        fun launchApp() {
//            Toast.makeText(activity, "打开app", Toast.LENGTH_SHORT).show()
            var intent = Intent(Intent.ACTION_MAIN, null)
            intent.addCategory(Intent.CATEGORY_LAUNCHER)
            intent.setPackage(activity.packageName)
            val packageManager = activity.packageManager
            val activities = packageManager.queryIntentActivities(intent, 0)
            val resolveInfo = activities.iterator().next()
            if (resolveInfo != null) {
                val packageName = resolveInfo.activityInfo.packageName
                val name = resolveInfo.activityInfo.name
                var cn = ComponentName(packageName, name)
                intent.component = cn
            }
            activity.startActivity(intent)
        }

    }

    override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        channel = MethodChannel(flutterPluginBinding.binaryMessenger, "flutter_emas_push")
        channel?.setMethodCallHandler(this)
        mContext = flutterPluginBinding.applicationContext
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
                LogUtils.i(TAG, "channelId=$channelID,channelName=$channelName")
            }
            "registerWithMetaData" -> {
                LogUtils.i(TAG, "you called registerWithMetaData")
                registerWithMetaData()
            }
            "registerHuawei" -> registerHuawei()
            "registerXiaomi" -> {
                val xiaomiId = call.argument<String>("xiaomiId")
                val xiaomiKey = call.argument<String>("xiaomiKey")
                registerXiaomi(xiaomiId ?: "", xiaomiKey ?: "")
            }
            "registerOppo" -> {
                LogUtils.d(TAG, "init cloudchannel  oppo")
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
            "bindAccount" -> {
                var account = call.argument<String>("account")
                bindAccount(account!!, result)
            }
            "unbindAccount" -> {
                unbindAccount(result)
            }
            "bindTag" -> {
                var tagTarget = call.argument<Int>("tagTarget")
                var tags = call.argument<Array<String>>("tags")
                var alias = call.argument<String>("alias")
                bindTag(tagTarget!!, tags!!, alias, result)

            }
            "unbindTag" -> {
                var tagTarget = call.argument<Int>("tagTarget")
                var tags = call.argument<Array<String>>("tags")
                var alias = call.argument<String>("alias")
                unbindTag(tagTarget!!, tags!!, alias, result)
            }
            "listTags" -> {
                listTags(result)
            }
            "addAlias" -> {
                var alias = call.argument<String>("alias")
                addAlias(alias!!, result)
            }
            "removeAlias" -> {
                var alias = call.argument<String>("alias")
                removeAlias(alias!!, result)
            }
            "canShowNotification" -> {
                val checkCanShowNotification = checkCanShowNotification(mContext)
                LogUtils.i(TAG, "you called canShowNotification,result=$checkCanShowNotification")
                result.success(checkCanShowNotification)
            }
            "goSettingPage" -> {
                goSettingPage(mContext)
            }
            "testPush" -> {
                val title = call.argument<String>("title")
                val content = call.argument<String>("content")
                LogUtils.i(TAG, "you called testPush,title=$title,content=$content")
                testPush(title ?: "", content ?: "")
            }
            "showNotification" -> {
                val title = call.argument<String>("title")
                val content = call.argument<String>("content")
                LogUtils.i(TAG, "you called showNotification,title=$title,content=$content")
                testPush(title ?: "", content ?: "")
            }
            "showLog" -> {
                val b = call.argument<Boolean>("showLog")
                LogUtils.setShowLog(b ?: false)
            }
            "setEMASLogLevel" -> {
                val level = call.argument<Int>("logLevel")
                mPushService?.let {
                    it.setLogLevel(level ?: -1)
                }
            }
            else -> result.notImplemented()
        }
    }

    override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
        channel?.setMethodCallHandler(null)
    }

    private fun showNotification(title: String, content: String) {
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
        showNotificationN(mContext, title, content)
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
        showNotificationN(mContext, title, content)
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
        mPushService = PushServiceFactory.getCloudPushService()
        mPushService?.let {
            it.register(activity, object : CommonCallback {
                override fun onSuccess(response: String?) {
                    var deviceId = it.deviceId
                    LogUtils.d(TAG, "init push success: $response,deviceID=$deviceId")
                }

                override fun onFailed(errorCode: String, errorMessage: String) {
                    LogUtils.d(
                        TAG, "init push failed: errorCode:$errorCode, errorMessage:$errorMessage"
                    )
                }
            })
        }
    }

    /**
     * config the key on AndroidManifest.xml,
     *
     * then called this method.
     */
    private fun registerWithMetaData() {
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
                LogUtils.d(TAG, "register:no huawei push register,if you need,please config it.")
            }
        } else {
            LogUtils.d(TAG, "This phone is not HUAWEI.")
        }
        if (RomUtil.isMiui) {
            val xiaomiId = metaData.getString("com.mi.push.app_id")
            val xiaomiKey = metaData.getString("com.mi.push.app_key")
            if (!xiaomiId.isNullOrEmpty() && !xiaomiKey.isNullOrEmpty()) {
                LogUtils.d(TAG, "xiaomiId=$xiaomiId,xiaomiKey=$xiaomiKey")
                registerXiaomi(xiaomiId, xiaomiKey)
            } else {
                LogUtils.d(TAG, "register:no xiaomi push register,if you need,please config it.")
            }
        } else {
            LogUtils.d(TAG, "This phone is not XIAOMI.")
        }
        if (RomUtil.isOppo) {
            val oppoAppKey = metaData.getString("com.oppo.push.app_key")
            val oppoSecret = metaData.getString("com.oppo.push.app_secret")
            if (!oppoAppKey.isNullOrEmpty() && !oppoSecret.isNullOrEmpty()) {
                registerOppo(oppoAppKey, oppoSecret)
            } else {
                LogUtils.d(TAG, "register:no oppo push register,if you need,please config it.")
            }
        } else {
            LogUtils.d(TAG, "This phone is not OPPO.")
        }
        if (RomUtil.isVivo) {
            val vivoAppKey = metaData.getString("com.vivo.push.api_key")
            val vivoAppId = metaData.getString("com.vivo.push.app_id")
            if (!vivoAppKey.isNullOrEmpty() && !vivoAppId.toString().isNullOrEmpty()) {
                registerVivo()
            } else {
                LogUtils.d(TAG, "register:no vivo push register,if you need,please config it.")
            }
        } else {
            LogUtils.d(TAG, "This phone is not VIVO.")
        }
        if (RomUtil.isFlyme) {
            val meizuAppId = metaData.getString("com.meizu.push.app_id")
            val meizuAppKey = metaData.getString("com.meizu.push.app_key")
            if (!meizuAppId.isNullOrEmpty() && !meizuAppKey.isNullOrEmpty()) {
                registerMeizu(meizuAppId, meizuAppKey)
            } else {
                LogUtils.d(TAG, "register:no meizu push register,if you need,please config it.")
            }
        } else {
            LogUtils.d(TAG, "This phone is not MEIZU.")
        }
        val sendId = metaData.getString("com.google.firebase.send_id")
        val applicationId = metaData.getString("com.google.firebase.application_id")
        val projectId = metaData.getString("com.google.firebase.project_id")
        val apiKey = metaData.getString("com.google.firebase.api_key")
        if (!sendId.isNullOrEmpty() && !applicationId.isNullOrEmpty() && !projectId.isNullOrEmpty() && !apiKey.isNullOrEmpty()) {
            registerGCM(sendId, applicationId, projectId, apiKey)
        } else {
            LogUtils.d(TAG, "register:no GCM push register,if you need,please config it.")
        }
    }

    private fun registerHuawei() {
        LogUtils.d(TAG, "you called registerHuawei")
        HuaWeiRegister.register(activity.application)
    }

    private fun registerXiaomi(xiaomiId: String, xiaomiKey: String) {
        LogUtils.d(TAG, "you called registerXiaomi,$xiaomiId,$xiaomiKey")
        MiPushRegister.register(activity.application, xiaomiId, xiaomiKey)
    }

    private fun registerOppo(appKey: String, appSecret: String) {
        LogUtils.d(TAG, "you called registerOppo")
        OppoRegister.register(activity, appKey, appSecret)
    }

    private fun registerVivo() {
        LogUtils.d(TAG, "you called registerVivo")
        VivoRegister.register(activity)
    }

    private fun registerMeizu(appId: String, appKey: String) {
        LogUtils.d(TAG, "you called registerMeizu")
        MeizuRegister.register(activity, appId, appKey)
    }

    private fun registerGCM(
        sendId: String,
        applicationId: String,
        projectId: String,
        apiKey: String
    ) {
        LogUtils.d(TAG, "you called registerGCM")
        //GCM/FCM辅助通道注册
        GcmRegister.register(
            activity,
            sendId,
            applicationId,
            projectId,
            apiKey
        ) //sendId/applicationId/projectId/apiKey为已获得的参数
    }

    /**
     * 将应用内账号和推送通道相关联，可以实现按账号的定点消息推送
     * @param account 应用内账号
     */
    private fun bindAccount(account: String, result: Result) {
        mPushService?.let {
            it.bindAccount(account, commonCallback(result))
        }
    }

    /**
     * 将应用内账号和推送通道取消关联。
     */
    private fun unbindAccount(result: Result) {
        mPushService?.let {
            it.unbindAccount(commonCallback(result))
        }
    }

    /**
     * 绑定标签到指定目标。
     *
     * - 支持向设备、账号和别名绑定标签，绑定类型有参数target指定。
     * - App最多支持定义1万个标签，单个标签支持的最大长度为129字符。
     * - 绑定标签在10分钟内生效。
     * - 不建议在单个标签上绑定超过十万级设备，否则，发起对该标签的推送可能需要较长的处理时间，无法保障响应速度。
     * 此种情况下，建议您采用全推方式，或将设备集合拆分到更细粒度的标签，多次调用推送接口分别推送给这些标签来避免此问题。
     *
     * @param target 目标类型可选值：
     * @see CloudPushService.DEVICE_TARGET int DEVICE_TARGET = 1; 本设备
     * @see CloudPushService.ACCOUNT_TARGET int ACCOUNT_TARGET = 2; 本设备绑定的账号
     * @see CloudPushService.ALIAS_TARGET int ALIAS_TARGET = 3; 别名
     *
     * @param tags 目标类型可选值：
     * @param alias 别名，仅当target=3时生效
     */
    private fun bindTag(target: Int, tags: Array<String>, alias: String?, result: Result) {
        mPushService?.let {
            it.bindTag(target, tags, alias, commonCallback(result))
        }
    }


    /**
     * 解绑指定目标的标签。
     *
     * - 支持解绑设备、账号和别名的标签，解绑类型有参数target指定。
     * - 解绑标签只是解除设备和标签的绑定关系，不等同于删除标签，即该App下标签依然存在，系统当前不支持删除标签。
     * - 解绑标签在10分钟内生效。
     *
     * @param target 目标类型可选值：
     * @see CloudPushService.DEVICE_TARGET int DEVICE_TARGET = 1; 本设备
     * @see CloudPushService.ACCOUNT_TARGET int ACCOUNT_TARGET = 2; 本设备绑定的账号
     * @see CloudPushService.ALIAS_TARGET int ALIAS_TARGET = 3; 别名
     *
     * @param tags 目标类型可选值：
     * @param alias 别名，仅当target=3时生效
     */
    private fun unbindTag(target: Int, tags: Array<String>, alias: String?, result: Result) {
        mPushService?.let {
            it.unbindTag(target, tags, alias, commonCallback(result))
        }
    }

    /**
     * 查询目标绑定的标签，当前仅支持查询设备标签。
     */
    private fun listTags(result: Result) {
        mPushService?.let {
            it.listTags(CloudPushService.DEVICE_TARGET, commonCallback(result));
        }
    }

    /**
     * 为设备添加别名。
     * @param alias 别名
     */
    private fun addAlias(alias: String, result: Result) {
        mPushService?.let {
            it.addAlias(alias, commonCallback(result))
        }

    }

    /**
     * 删除设备别名
     * @param alias 别名
     */
    private fun removeAlias(alias: String, result: Result) {
        mPushService?.let {
            it.removeAlias(alias, commonCallback(result))
        }
    }

    private fun commonCallback(result: Result) =
        object : CommonCallback {
            override fun onSuccess(s: String?) {
                var json = JSONObject()
                json.put("isSuccess", true)
                json.put("result", s ?: "")
                result.success(json.toString())
            }

            override fun onFailed(errorCode: String?, errorMsg: String?) {
                var json = JSONObject()
                json.put("isSuccess", false)
                json.put("errorCode", errorCode)
                json.put("errorMsg", errorMsg)
                result.success(json.toString())
            }
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
