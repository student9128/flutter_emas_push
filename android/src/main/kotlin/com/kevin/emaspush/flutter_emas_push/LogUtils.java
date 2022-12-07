package com.kevin.emaspush.flutter_emas_push;

import android.util.Log;

public class LogUtils {

    private static boolean showLog;

    public static boolean isShowLog() {
        return showLog;
    }

    public static void setShowLog(boolean b) {
        showLog = b;
    }

    public static void i(String tag, String s) {
        if (showLog) {
            Log.i(tag, s);
        }
    }

    public static void w(String tag, String s) {
        if (showLog) {
            Log.w(tag, s);
        }
    }

    public static void d(String tag, String s) {
        if (showLog) {
            Log.d(tag, s);
        }
    }

    public static void e(String tag, String s) {
        if (showLog) {
            Log.e(tag, s);
        }
    }

    public static void v(String tag, String s) {
        if (showLog) {
            Log.v(tag, s);
        }
    }
}
