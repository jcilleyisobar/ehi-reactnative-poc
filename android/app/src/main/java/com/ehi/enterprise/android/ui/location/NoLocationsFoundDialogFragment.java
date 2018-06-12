package com.ehi.enterprise.android.ui.location;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.databinding.NoLocationsDialogFragmentBinding;
import com.ehi.enterprise.android.ui.fragment.DataBindingViewModelFragment;
import com.ehi.enterprise.android.ui.viewmodel.ManagersAccessViewModel;
import com.ehi.enterprise.android.utils.analytics.EHIAnalytics;
import com.ehi.enterprise.android.utils.analytics.EHIAnalyticsDictionaryUtils;
import com.ehi.enterprise.android.utils.analytics.EHIAnalyticsEvent;
import com.isobar.android.newinstancer.Extra;
import com.isobar.android.viewmodel.ViewModel;

import java.util.ArrayList;

@ViewModel(ManagersAccessViewModel.class)
public class NoLocationsFoundDialogFragment extends DataBindingViewModelFragment<ManagersAccessViewModel, NoLocationsDialogFragmentBinding> {

    public static final String SCREEN_NAME = "NoLocationsFoundDialogFragment";

    @Extra(value = ArrayList.class, type = Integer.class)
    public static final String EXTRA_FILTERS = "ehi.EXTRA_FILTERS";

    private ArrayList<Integer> filters;

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v == getViewBinding().editFiltersButton) {
                EHIAnalyticsEvent.create()
                        .screen(EHIAnalytics.Screen.SCREEN_LOCATIONS.value, SCREEN_NAME)
                        .state(EHIAnalytics.State.STATE_SEARCH_FILTER_NO_RESULTS.value)
                        .action(EHIAnalytics.Motion.MOTION_TAP.value, EHIAnalytics.Action.ACTION_EDIT_FILTERS.value)
                        .addDictionary(EHIAnalyticsDictionaryUtils.locationsFilter(filters, getResources()))
                        .tagScreen()
                        .tagEvent();
                getActivity().setResult(Activity.RESULT_OK);
                getActivity().finish();
            } else if (v == getViewBinding().clearFiltersButton) {
                EHIAnalyticsEvent.create()
                        .screen(EHIAnalytics.Screen.SCREEN_LOCATIONS.value, SCREEN_NAME)
                        .state(EHIAnalytics.State.STATE_SEARCH_FILTER_NO_RESULTS.value)
                        .action(EHIAnalytics.Motion.MOTION_TAP.value, EHIAnalytics.Action.ACTION_CLEAR_ALL_FILTERS.value)
                        .addDictionary(EHIAnalyticsDictionaryUtils.locationsFilter(filters, getResources()))
                        .tagScreen()
                        .tagEvent();
                getActivity().setResult(Activity.RESULT_FIRST_USER);
                getActivity().finish();
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        NoLocationsFoundDialogFragmentHelper.Extractor extractor = new NoLocationsFoundDialogFragmentHelper.Extractor(this);
        filters = extractor.extraFilters();
    }

    @Override
    public void onStart() {
        super.onStart();
        EHIAnalyticsEvent.create()
                .screen(EHIAnalytics.Screen.SCREEN_LOCATIONS.value, SCREEN_NAME)
                .state(EHIAnalytics.State.STATE_SEARCH_FILTER_NO_RESULTS.value)
                .addDictionary(EHIAnalyticsDictionaryUtils.locationsFilter(filters, getResources()))
                .tagScreen()
                .tagEvent();
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        createViewBinding(inflater, R.layout.fr_no_locations_dialog, container);
        initViews();
        return getViewBinding().getRoot();
    }

    private void initViews() {
        getActivity().setTitle(R.string.no_locations_modal_title);

        getViewBinding().editFiltersButton.setOnClickListener(mOnClickListener);
        getViewBinding().clearFiltersButton.setOnDisabledClickListener(mOnClickListener);
    }

}
