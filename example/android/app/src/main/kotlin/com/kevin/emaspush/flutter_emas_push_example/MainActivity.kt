package com.kevin.emaspush.flutter_emas_push_example

import android.content.Intent
import android.os.Bundle
import com.kevin.emaspush.flutter_emas_push.LogUtils
import io.flutter.embedding.android.FlutterActivity

class MainActivity : FlutterActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LogUtils.d("FlutterEmasPushPlugin", "onCreate${intent}")
        if (intent != null) {
            val extras = intent.extras
            if (extras != null) {
                val keys = extras.keySet()
                for (key: String in keys) {
                    LogUtils.d("FlutterEmasPushPlugin", "onCreate key=$key,value=${extras[key]}")
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        LogUtils.d("FlutterEmasPushPlugin", "onNewIntent${intent}")
        setIntent(intent)
        LogUtils.d("FlutterEmasPushPlugin", "new onNewIntent${intent}")
        val extras = intent.extras
        if (extras != null) {
            val keys = extras.keySet()
            for (key: String in keys) {
                LogUtils.d("FlutterEmasPushPlugin", "onNewIntent key=$key,value=${extras[key]}")
            }
        }
    }
}
