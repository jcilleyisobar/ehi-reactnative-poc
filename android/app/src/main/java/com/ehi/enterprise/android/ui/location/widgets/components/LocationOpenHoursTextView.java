package com.ehi.enterprise.android.ui.location.widgets.components;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.util.AttributeSet;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.databinding.LocationOpenHoursViewBinding;
import com.ehi.enterprise.android.models.location.solr.EHISolrLocationValidity;
import com.ehi.enterprise.android.models.location.solr.EHISolrTimeSpan;
import com.ehi.enterprise.android.ui.view.DataBindingViewModelView;
import com.ehi.enterprise.android.ui.viewmodel.ManagersAccessViewModel;
import com.isobar.android.viewmodel.ViewModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import io.dwak.reactor.BuildConfig;

@ViewModel(ManagersAccessViewModel.class)
public class LocationOpenHoursTextView extends DataBindingViewModelView<ManagersAccessViewModel, LocationOpenHoursViewBinding> {

    private final int WEEKDAY_MONTH_FLAGS = DateUtils.FORMAT_SHOW_DATE
            | DateUtils.FORMAT_SHOW_WEEKDAY
            | DateUtils.FORMAT_ABBREV_WEEKDAY
            | DateUtils.FORMAT_ABBREV_MONTH;

    public LocationOpenHoursTextView(Context context) {
        this(context, null);
    }

    public LocationOpenHoursTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LocationOpenHoursTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        createViewBinding(R.layout.v_location_open_hours);
    }

    public void setFormattedText(Date date, EHISolrLocationValidity validity) {
        if (date != null && validity != null) {
            final boolean isAllDayClosed = validity.isAllDayClosed();
            final String formattedDate = getWeekdayMonthDateTime(date.getTime());
            final String formattedTime;
            if (isAllDayClosed) {
                formattedTime = getResources().getString(R.string.location_details_hours_closed);
            } else {
                formattedTime = getTimeFormattedText(validity);
            }
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(formattedDate.toUpperCase());
            stringBuilder.append(": ");
            if (formattedTime != null) {
                stringBuilder.append(formattedTime);
            }
            getViewBinding().title.setText(stringBuilder.toString());
        }
    }

    @Nullable
    private String getTimeFormattedText(EHISolrLocationValidity validityType) {
        final SimpleDateFormat localizedTimeFormat = (SimpleDateFormat) DateFormat.getTimeFormat(getContext());
        if (validityType == null || validityType.getStandardOpenCloseHours() == null) {
            return null;
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < validityType.getStandardOpenCloseHours().size(); i++) {
            EHISolrTimeSpan timeSpan = validityType.getStandardOpenCloseHours().get(i);
            if (i > 0) {
                stringBuilder.append(", ");
            }
            try {
                stringBuilder.append(localizedTimeFormat.format(timeSpan.getOpenTimeDate()));
                stringBuilder.append(" - ");
                stringBuilder.append(localizedTimeFormat.format(timeSpan.getCloseTimeDate()));
            }
            catch (ParseException e) {
                if (BuildConfig.DEBUG) {
                    e.printStackTrace();
                }
            }
        }
        return stringBuilder.toString();
    }

    public void setTextColor(int textColor) {
        getViewBinding().title.setTextColor(textColor);
    }

    private String getWeekdayMonthDateTime(long date) {
        return DateUtils.formatDateTime(getContext(),
                date,
                WEEKDAY_MONTH_FLAGS);
    }
}
