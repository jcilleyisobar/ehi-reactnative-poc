package com.ehi.enterprise.android.ui.location.widgets.components;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.databinding.TimeConflictComponentViewBinding;
import com.ehi.enterprise.android.models.location.solr.EHISolrLocation;
import com.ehi.enterprise.android.models.location.solr.EHISolrLocationValidity;
import com.ehi.enterprise.android.ui.location.LocationsOnMapActivity;
import com.ehi.enterprise.android.ui.view.DataBindingViewModelView;
import com.ehi.enterprise.android.utils.analytics.EHIAnalytics;
import com.ehi.enterprise.android.utils.analytics.EHIAnalyticsDictionaryUtils;
import com.ehi.enterprise.android.utils.analytics.EHIAnalyticsEvent;
import com.isobar.android.viewmodel.ViewModel;

import java.util.Date;

import io.dwak.reactorbinding.view.ReactorView;
import io.dwak.reactorbinding.widget.ReactorTextView;

@ViewModel(TimeConflictComponentViewModel.class)
public class TimeConflictComponentView extends DataBindingViewModelView<TimeConflictComponentViewModel, TimeConflictComponentViewBinding> {

    private boolean mFromList;

    private final OnClickListener mOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (getViewBinding().greenArrowImageView == v || getViewBinding().titleTextView == v) {
                trackShowHideHours();
                getViewModel().toggleContainer();
            }
        }
    };

    public TimeConflictComponentView(Context context) {
        this(context, null);
    }

    public TimeConflictComponentView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TimeConflictComponentView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        createViewBinding(R.layout.v_time_conflict_component);
        init();
    }

    private void init() {
        getViewBinding().greenArrowImageView.setOnClickListener(mOnClickListener);
        getViewBinding().titleTextView.setOnClickListener(mOnClickListener);
    }

    @Override
    protected void initDependencies() {
        super.initDependencies();
        bind(ReactorView.visibility(getViewModel().rootView.visibility(), getViewBinding().getRoot()));
        bind(ReactorView.visibility(getViewModel().collapsibleContainer.visibility(), getViewBinding().collapsibleContainer));
        bind(ReactorView.visibility(getViewModel().firstTimeView.visibility(), getViewBinding().firstTimeView));
        bind(ReactorView.visibility(getViewModel().secondTimeView.visibility(), getViewBinding().secondTimeView));
        bind(ReactorTextView.text(getViewModel().titleView.textCharSequence(), getViewBinding().titleTextView));
        bind(ReactorTextView.text(getViewModel().hoursDetailsView.text(), getViewBinding().hoursDetailsTextView));
        bind(ReactorView.rotation(getViewModel().arrowView.rotation(), getViewBinding().greenArrowImageView));
    }

    public void onViewCollapsed() {
        getViewModel().collapseContainer();
    }

    public void setData(@Nullable Date pickupDate, @Nullable Date dropoffDate, EHISolrLocation solrLocation, String searchArea, int flow, boolean fromList) {
        getViewModel().setPickupDate(pickupDate);
        getViewModel().setDropoffDate(dropoffDate);
        getViewModel().setSolrLocation(solrLocation);
        getViewModel().setSearchArea(searchArea);
        getViewModel().setFlow(flow);
        mFromList = fromList;

        final Date firstTimeViewDate = getViewModel().getDateForFirstTimeView();
        final EHISolrLocationValidity firstTimeViewValidity = getViewModel().getValidityForFirstTimeView();
        final Date secondTimeViewDate = getViewModel().getDateForSecondTimeView();
        final EHISolrLocationValidity secondTimeViewValidity = getViewModel().getValidityForSecondTimeView();

        getViewBinding().firstTimeView.setFormattedText(firstTimeViewDate, firstTimeViewValidity);
        getViewBinding().firstTimeView.setTextColor(R.color.bubble_map_gray);
        getViewBinding().secondTimeView.setFormattedText(secondTimeViewDate, secondTimeViewValidity);
        getViewBinding().secondTimeView.setTextColor(R.color.bubble_map_gray);
    }

    private void trackShowHideHours() {
        EHIAnalyticsEvent event = EHIAnalyticsEvent.create()
                .screen(EHIAnalytics.Screen.SCREEN_LOCATIONS.value, LocationsOnMapActivity.SCREEN_NAME)
                .addDictionary(EHIAnalyticsDictionaryUtils.locationMap(getViewModel().getFlow(), getViewModel().getLocation(), getViewModel().getSearchArea(), 0,0,""));

        if (mFromList) {
            event.state(EHIAnalytics.State.STATE_LIST.value);
        } else {
            event.state(EHIAnalytics.State.STATE_MAP.value);
        }

        if (getViewModel().isContainerVisible()) {
            event.action(EHIAnalytics.Motion.MOTION_TAP.value, EHIAnalytics.Action.ACTION_HIDE_HOURS.value);
        } else {
            event.action(EHIAnalytics.Motion.MOTION_TAP.value, EHIAnalytics.Action.ACTION_SHOW_HOURS.value);
        }

        event.tagScreen().tagEvent();
    }
}
