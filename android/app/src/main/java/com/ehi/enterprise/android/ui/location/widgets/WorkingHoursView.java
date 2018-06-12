package com.ehi.enterprise.android.ui.location.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.ehi.enterprise.android.models.location.EHIWorkingDayInfo;
import com.ehi.enterprise.android.utils.DisplayUtils;

import java.util.List;

public class WorkingHoursView extends LinearLayout {

	private static final String TAG = WorkingHoursView.class.getSimpleName();

	private List<EHIWorkingDayInfo> mWorkingDays;

	public WorkingHoursView(Context context) {
		super(context);
		initViewParams();
	}

	public WorkingHoursView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initViewParams();
	}

	public WorkingHoursView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		initViewParams();
	}

	private void initViewParams() {
		setOrientation(VERTICAL);
	}

	public void setWorkingHoursInfo(List<EHIWorkingDayInfo> hoursInfo) {
		mWorkingDays = hoursInfo;
		removeAllViewsInLayout();
		if (mWorkingDays != null) {
			addDaysSubviews();
		}
	}

	private void addDaysSubviews() {
		for (int i = 0; i < mWorkingDays.size(); i++) {
			EHIWorkingDayInfo dayInfo = mWorkingDays.get(i);
			WorkingDayView dayView = new WorkingDayView(getContext());
			if (i == 0) {
				dayView.setTodayDay(true);
			}
			dayView.setDayInfo(dayInfo);

			addView(dayView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

			if (i != mWorkingDays.size() - 1) {
				FrameLayout divider = new FrameLayout(getContext());
				addView(divider, ViewGroup.LayoutParams.MATCH_PARENT, (int) DisplayUtils.dipToPixels(getContext(), 10));
			}
		}
		invalidate();
	}


}
