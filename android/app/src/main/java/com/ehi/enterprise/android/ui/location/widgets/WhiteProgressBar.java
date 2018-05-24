package com.ehi.enterprise.android.ui.location.widgets;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.ProgressBar;

public class WhiteProgressBar extends ProgressBar {
	public WhiteProgressBar(Context context) {
		super(context);
		initColor();
	}

	public WhiteProgressBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		initColor();
	}

	public WhiteProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		initColor();
	}

	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
	public WhiteProgressBar(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
		initColor();
	}

	private void initColor() {
		getIndeterminateDrawable().setColorFilter(Color.WHITE, PorterDuff.Mode.MULTIPLY);
	}
}
