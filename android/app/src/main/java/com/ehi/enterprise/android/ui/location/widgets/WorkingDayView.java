package com.ehi.enterprise.android.ui.location.widgets;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v4.content.res.ResourcesCompat;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.databinding.WorkingDayViewBinding;
import com.ehi.enterprise.android.databinding.WorkingPeriodViewBinding;
import com.ehi.enterprise.android.models.location.EHIOpenCloseTime;
import com.ehi.enterprise.android.models.location.EHIWorkingDayInfo;
import com.ehi.enterprise.android.ui.view.DataBindingViewModelView;
import com.ehi.enterprise.android.utils.DLog;
import com.ehi.enterprise.android.utils.databinding.adapter.AppAdapters;
import com.isobar.android.viewmodel.ViewModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@ViewModel(WorkingDayViewModel.class)
public class WorkingDayView extends DataBindingViewModelView<WorkingDayViewModel, WorkingDayViewBinding> {

    private static final String TAG = WorkingDayView.class.getSimpleName();

    private EHIWorkingDayInfo mDayInfo;
    private boolean mTodayDay = false;

    public WorkingDayView(Context context) {
        this(context, null, 0);
    }

    public WorkingDayView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WorkingDayView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        createViewBinding(R.layout.v_working_day);
    }

    public void setDayInfo(@NonNull EHIWorkingDayInfo info) {
        mDayInfo = info;
        showDayInfo();
    }

    private void showDayInfo() {
        //date
        if (mTodayDay) {
            getViewBinding().dateView.setText(getResources().getString(R.string.location_details_today).toUpperCase());
            getViewBinding().executePendingBindings();
            getViewBinding().dateView.setTypeface(ResourcesCompat.getFont(getContext(), R.font.source_sans_bold));
        } else {
            String date;
            try {
                date = DateUtils.formatDateTime(getContext(), mDayInfo.getDateObject().getTime(),
                        DateUtils.FORMAT_SHOW_DATE
                                | DateUtils.FORMAT_SHOW_WEEKDAY
                                | DateUtils.FORMAT_ABBREV_WEEKDAY
                                | DateUtils.FORMAT_ABBREV_MONTH);
            } catch (IllegalArgumentException | NullPointerException e) {
                DLog.w(TAG, e);
                date = mDayInfo.getDayName() + " " + mDayInfo.getDate();
            }
            getViewBinding().dateView.setText(date);
            invalidate();
        }

        //time periods
        getViewBinding().timeViewGroup.removeAllViewsInLayout();
        WorkingPeriodViewBinding binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()),
                R.layout.v_working_period,
                getViewBinding().timeViewGroup,
                false);
        if (mDayInfo.isCloseAllDay()) {
            binding.openTime.setText(getResources().getString(R.string.location_details_hours_closed).toUpperCase());
            binding.dashView.setVisibility(View.GONE);
            binding.closeTime.setVisibility(View.GONE);
        } else if (mDayInfo.isOpenAllDay()) {
            binding.openTime.setText(getResources().getString(R.string.location_details_open_all_day).toUpperCase());
            binding.dashView.setVisibility(View.GONE);
            binding.closeTime.setVisibility(View.GONE);
        } else {
            for (EHIOpenCloseTime time : mDayInfo.getOpenCloseTimes()) {
                SimpleDateFormat localizedTime = (SimpleDateFormat) DateFormat.getTimeFormat(getContext());
                try {
                    Date openTime = time.getOpenTimeObject();
                    binding.openTime.setText(localizedTime.format(openTime));
                } catch (ParseException e) {
                    DLog.w(TAG, e);
                }
                try {
                    Date closedTime = time.getCloseTimeObject();
                    binding.closeTime.setText(localizedTime.format(closedTime));
                } catch (ParseException e) {
                    DLog.w(TAG, e);
                }
            }
        }

        binding.executePendingBindings();
        if (mTodayDay) {
            binding.openTime.setTypeface(ResourcesCompat.getFont(getContext(), R.font.source_sans_bold));
            binding.dashView.setTypeface(ResourcesCompat.getFont(getContext(), R.font.source_sans_bold));
            binding.closeTime.setTypeface(ResourcesCompat.getFont(getContext(), R.font.source_sans_bold));
        }
        getViewBinding().timeViewGroup.addView(binding.getRoot(), ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        getViewBinding().timeViewGroup.invalidate();
    }

    public void setTodayDay(boolean today) {
        mTodayDay = today;
    }
}
