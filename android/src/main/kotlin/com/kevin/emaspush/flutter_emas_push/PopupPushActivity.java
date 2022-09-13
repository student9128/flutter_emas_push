package com.kevin.emaspush.flutter_emas_push;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.alibaba.sdk.android.push.AndroidPopupActivity;

import java.util.Map;

public class PopupPushActivity extends AndroidPopupActivity {
    static final String TAG = "PopupPushActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    /**
      * 实现通知打开回调方法，获取通知相关信息。
     * @param title     标题
     * @param summary   内容
     * @param extMap    额外参数
     */
    @Override
    protected void onSysNoticeOpened(String title, String summary, Map<String, String> extMap) {
        Log.d(TAG,"OnMiPushSysNoticeOpened, title: " + title + ", content: " + summary + ", extMap: " + extMap);
//        FlutterEmasPushPlugin.Companion.launchApp();
        Toast.makeText(this,"onSysNoticeOpened===",Toast.LENGTH_SHORT).show();
    }
}