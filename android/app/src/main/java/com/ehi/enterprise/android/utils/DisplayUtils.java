package com.ehi.enterprise.android.utils;

import android.content.Context;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

public final class DisplayUtils {
    public static DisplayMetrics getDisplayMetrics(final Context context) {
		final WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		final DisplayMetrics metrics = new DisplayMetrics();
		windowManager.getDefaultDisplay().getMetrics(metrics);
		return metrics;
	}

    public static int getScreenWidth(final Context context) {
		if (context == null) {
			return 0;
		}
		return getDisplayMetrics(context).widthPixels;
	}

    public static int getScreenHeight(final Context context) {
		if (context == null) {
			return 0;
		}
		return getDisplayMetrics(context).heightPixels;
	}

    public static int getStatusBarHeight(Context context) {
		int statusBarHeight = 0;
		int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
		if (resourceId > 0) {
			statusBarHeight = context.getResources().getDimensionPixelSize(resourceId);
		}
		return statusBarHeight;
	}

    public static float dipToPixels(Context context, float dipValue) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, metrics);
    }

    public static int calculateViewSizeWithText(ViewGroup view) {
		int height = 0;
		View childView;
		TextView textView;
		for (int i = 0; i < view.getChildCount(); i++) {
			childView = view.getChildAt(i);
			if (childView instanceof TextView) {
				textView = ((TextView) childView);
				int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(view.getWidth(), View.MeasureSpec.EXACTLY);
				int heightMeasure = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
				textView.measure(widthMeasureSpec, heightMeasure);

				height += textView.getMeasuredHeight();
			}
			else {
				height += view.getChildAt(i).getHeight();
			}
		}
		height += view.getPaddingTop();
		height += view.getPaddingBottom();
		return height;
	}

	public static boolean wasViewClicked(View v, int x, int y) {
		final Rect clickedRect = new Rect();
		v.getHitRect(clickedRect);
		return clickedRect.contains(x, y);
	}
}
