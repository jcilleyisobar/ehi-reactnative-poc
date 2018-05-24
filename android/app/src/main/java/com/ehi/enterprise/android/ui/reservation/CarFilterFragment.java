package com.ehi.enterprise.android.ui.reservation;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.app.EHIStringToken;
import com.ehi.enterprise.android.databinding.CarFilterFragmentBinding;
import com.ehi.enterprise.android.models.reservation.EHIAvailableCarFilters;
import com.ehi.enterprise.android.models.reservation.EHICarClassDetails;
import com.ehi.enterprise.android.models.reservation.EHIFilterValue;
import com.ehi.enterprise.android.ui.fragment.DataBindingViewModelFragment;
import com.ehi.enterprise.android.ui.reservation.widget.DropDownFilterView;
import com.ehi.enterprise.android.ui.reservation.widget.FilterCheckRowView;
import com.ehi.enterprise.android.utils.analytics.EHIAnalytics;
import com.ehi.enterprise.android.utils.analytics.EHIAnalyticsDictionaryUtils;
import com.ehi.enterprise.android.utils.analytics.EHIAnalyticsEvent;
import com.isobar.android.newinstancer.Extra;
import com.isobar.android.tokenizedstring.TokenizedString;
import com.isobar.android.viewmodel.ViewModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import io.dwak.reactor.ReactorComputation;
import io.dwak.reactor.interfaces.ReactorComputationFunction;

@ViewModel(CarFilterViewModel.class)
public class CarFilterFragment extends DataBindingViewModelFragment<CarFilterViewModel, CarFilterFragmentBinding> {

    public static final String SCREEN_NAME = "CarFilterFragment";

    @Extra(value = List.class, type = EHICarClassDetails.class)
    public static String CAR_FILTER_KEY_DETAILS = "CAR_FILTER_KEY_DETAILS";

    public static String REACTION_CAR_COUNT = "REACTION_CAR_COUNT";

    private List<DropDownFilterView> mDropdownViews;
    private List<FilterCheckRowView> mFilterCheckRows;

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view == getViewBinding().applyFilterButton) {
                EHIAnalyticsEvent.create()
                        .screen(EHIAnalytics.Screen.SCREEN_CLASS.value, CarFilterFragment.SCREEN_NAME)
                        .state(EHIAnalytics.State.STATE_FILTER.value)
                        .action(EHIAnalytics.Motion.MOTION_TAP.value, EHIAnalytics.Action.ACTION_APPLY_FILTER.value)
                        .addDictionary(EHIAnalyticsDictionaryUtils.carClassFilter(EHIAvailableCarFilters.getActiveFilters(getViewModel().getFilters())))
                        .tagScreen()
                        .tagEvent();

                Intent intent = new Intent();
                getActivity().setResult(Activity.RESULT_OK, intent);
                getViewModel().commitFilters();
                getActivity().finish();
            }
        }
    };

    private IFilterChanged mFilterCallback = new IFilterChanged() {
        @Override
        public void filterChanged() {
            getViewModel().applyFilters();
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getViewModel().setListOfCars(new CarFilterFragmentHelper.Extractor(this).carFilterKeyDetails());
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        createViewBinding(inflater, R.layout.fr_car_filter, container);
        initViews();
        return getViewBinding().getRoot();
    }


    private void initViews() {
        getViewBinding().applyFilterButton.setOnClickListener(mOnClickListener);
        mDropdownViews = new ArrayList<>(3);
        mFilterCheckRows = new ArrayList<>(7);
        EHIAvailableCarFilters filter;
        for (int i = 0; i < getViewModel().getFilters().size(); i++) {
            filter = getViewModel().getFilters().get(i);

            if (!filter.isValid()) {
                continue;
            }

            if (filter.isNotCarTypeFilter()) {
                mDropdownViews.add(new DropDownFilterView(getContext(),
                        filter,
                        filter.getDefaultOption(getResources()),
                        mFilterCallback));
                getViewBinding().dropDownContainer.addView(mDropdownViews.get(mDropdownViews.size() - 1));
            } else {
                List<EHIFilterValue> sorted = filter.getFilterValues();
                Collections.sort(sorted, new Comparator<EHIFilterValue>() {
                    @Override
                    public int compare(EHIFilterValue lhs, EHIFilterValue rhs) {
                        int res = String.CASE_INSENSITIVE_ORDER.compare(lhs.getDescription(), rhs.getDescription());

                        return (res == 0) ? res = lhs.getDescription().compareTo(rhs.getDescription()) : res;
                    }
                });

                for (int j = 0; j < filter.getFilterValues().size(); j++) {
                    mFilterCheckRows.add(new FilterCheckRowView(getContext(),
                            sorted.get(j),
                            mFilterCallback));

                    getViewBinding().checkRowContainer.addView(mFilterCheckRows.get(mFilterCheckRows.size() - 1));
                }
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        EHIAnalyticsEvent.create()
                .screen(EHIAnalytics.Screen.SCREEN_CLASS.value, CarFilterFragment.SCREEN_NAME)
                .state(EHIAnalytics.State.STATE_FILTER.value)
                .addDictionary(EHIAnalyticsDictionaryUtils.reservation())
                .tagScreen()
                .tagEvent();
    }

    @Override
    protected void initDependencies() {
        super.initDependencies();
        addReaction(REACTION_CAR_COUNT, new ReactorComputationFunction() {
            @Override
            public void react(ReactorComputation reactorComputation) {
                if (getViewModel().getListOfCars() == null) {
                    return;
                }
                getViewBinding().filterCountView.setText(new TokenizedString.Formatter<EHIStringToken>(getResources())
                        .formatString(R.string.class_select_filter_apply_filters_button_title_suffix)
                        .addTokenAndValue(EHIStringToken.VEHICLES, String.valueOf(getViewModel().getListOfCars().size()))
                        .format());
            }
        });


    }

    @Override
    public void onStart() {
        super.onStart();
        getActivity().setTitle(R.string.class_select_filter_screen_title);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.filter_menu, menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.filter_clear_option) {
            getViewModel().clearFilters();
            for (int i = 0; i < mDropdownViews.size(); i++) {
                mDropdownViews.get(i).reset();
            }

            for (int i = 0; i < mFilterCheckRows.size(); i++) {
                mFilterCheckRows.get(i).setChecked(false);
            }
            return true;
        }

        return false;
    }

    public interface IFilterChanged {
        void filterChanged();
    }
}