package com.ehi.enterprise.android.utils;

import android.util.Log;

import com.ehi.enterprise.android.app.Settings;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DLog {
	private static final Pattern ANONYMOUS_CLASS = Pattern.compile("(\\$\\d+)+$");
	private static boolean SHOW_LOGS;

	static {
		SHOW_LOGS = Settings.SHOW_LOGS;
	}

    public static void v(String msg) {
        log(Log.VERBOSE, getTag(), msg);
    }

    public static void v(String tag, String msg) {
        log(Log.VERBOSE, tag, msg);
    }

    public static void v(String msg, Throwable tr) {
        log(Log.VERBOSE, getTag(), msg, tr);
    }

    public static void v(String tag, String msg, Throwable tr) {
        log(Log.VERBOSE, tag, msg, tr);
    }

    public static void d(CharSequence msg) {
        log(Log.DEBUG, getTag(), msg.toString());
    }

    public static void d(String msg) {
        log(Log.DEBUG, getTag(), msg);
    }

    public static void d(String tag, String msg) {
        log(Log.DEBUG, tag, msg);
    }

    public static void d(String msg, Throwable tr) {
        log(Log.DEBUG, getTag(), msg, tr);
    }

    public static void d(String tag, String msg, Throwable tr) {
        log(Log.DEBUG, tag, msg, tr);
    }

    public static void i(String msg) {
        log(Log.INFO, getTag(), msg);
    }

	public static void i(String tag, String msg) {
        log(Log.INFO, tag, msg);
    }

    public static void i(String msg, Throwable tr) {
        log(Log.INFO, getTag(), msg, tr);
    }

	public static void i(String tag, String msg, Throwable tr) {
        log(Log.INFO, tag, msg, tr);
    }

    public static void w(String msg) {
        log(Log.WARN, getTag(), msg);
    }

	public static void w(String tag, String msg) {
        log(Log.WARN, tag, msg);
    }

	public static void w(String tag, String msg, Throwable tr) {
        log(Log.WARN, tag, msg, tr);
    }

	public static void w(String tag, Throwable tr) {
        log(Log.WARN, tag, tr);
    }

    public static void e(String msg) {
        log(Log.ERROR, getTag(), msg);
    }

	public static void e(String tag, String msg) {
        log(Log.ERROR, tag, msg);
    }

    public static void e(String msg, Throwable tr) {
        log(Log.ERROR, getTag(), msg, tr);
    }

	public static void e(String tag, String msg, Throwable tr) {
        log(Log.ERROR, tag, msg, tr);
    }

	private static String getTag(){
		StackTraceElement[] stackTrace = new Throwable().getStackTrace();
		if (stackTrace.length <= 2) {
			throw new IllegalStateException(
					"Synthetic stacktrace didn't have enough elements: are you using proguard?");
		}
		return createStackElementTag(stackTrace[2]);
	}

	protected static String createStackElementTag(StackTraceElement element) {
		String tag = element.getClassName();
		Matcher m = ANONYMOUS_CLASS.matcher(tag);
		if (m.find()) {
			tag = m.replaceAll("");
		}
		return tag.substring(tag.lastIndexOf('.') + 1);
	}

    private static void log(int priority, String tag, Throwable tr) {
        log(priority, tag, "", tr);
    }

    private static void log(int priority, String tag, String msg) {
        log(priority, tag, msg, null);
    }

    private static void log(int priority, String tag, String msg, Throwable tr) {
        if(!SHOW_LOGS){
            return;
        }
        switch (priority) {
            case Log.VERBOSE:
                Log.v(tag, msg, tr);
                break;
            case Log.DEBUG:
                Log.d(tag, msg, tr);
                break;
            case Log.WARN:
                Log.w(tag, msg, tr);
                break;
            case Log.ERROR:
                Log.e(tag, msg, tr);
                break;
            case Log.INFO:
                Log.i(tag, msg, tr);
                break;
            default:
                Log.i(tag, msg, tr);
        }

    }
}
