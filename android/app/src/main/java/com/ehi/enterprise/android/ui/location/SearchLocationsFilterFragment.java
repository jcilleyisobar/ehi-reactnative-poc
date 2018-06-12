package com.ehi.enterprise.android.ui.location;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.databinding.SearchLocationFilterFragmentBinding;
import com.ehi.enterprise.android.ui.adapter.SectionHeader;
import com.ehi.enterprise.android.ui.adapter.SectionedRecyclerViewAdapter;
import com.ehi.enterprise.android.ui.fragment.DataBindingViewModelFragment;
import com.ehi.enterprise.android.utils.DialogUtils;
import com.ehi.enterprise.android.utils.EHIBundle;
import com.ehi.enterprise.android.utils.analytics.EHIAnalytics;
import com.ehi.enterprise.android.utils.analytics.EHIAnalyticsDictionaryUtils;
import com.ehi.enterprise.android.utils.analytics.EHIAnalyticsEvent;
import com.ehi.enterprise.android.utils.filters.EHIFilterList;
import com.isobar.android.newinstancer.Extra;
import com.isobar.android.viewmodel.ViewModel;

import java.util.ArrayList;
import java.util.Date;

import io.dwak.reactor.ReactorComputation;
import io.dwak.reactor.interfaces.ReactorComputationFunction;

@ViewModel(SearchFilterViewModel.class)
public class SearchLocationsFilterFragment
        extends DataBindingViewModelFragment<SearchFilterViewModel, SearchLocationFilterFragmentBinding>
        implements SearchFilterAdapter.FilterCallback {

    public static final String SCREEN_NAME = "SearchLocationsFilterFragment";
    private static final int SELECT_DATE_CODE = 1223;
    private static final int SELECT_TIME_PICKUP= 1224;
    private static final int SELECT_TIME_RETURN = 1225;

    @Extra(value = ArrayList.class, type = Integer.class)
    public static final String FILTER_LIST_KEY = "filter_list_key";

    @Extra(value = Date.class, required = false)
    public static final String EXTRA_PICKUP_DATE = "ehi.EXTRA_PICKUP_DATE";
    @Extra(value = Date.class, required = false)
    public static final String EXTRA_RETURN_DATE = "ehi.EXTRA_RETURN_DATE";
    @Extra(value = Date.class, required = false)
    public static final String EXTRA_PICKUP_TIME = "ehi.EXTRA_PICKUP_TIME";
    @Extra(value = Date.class, required = false)
    public static final String EXTRA_RETURN_TIME = "ehi.EXTRA_RETURN_TIME";

    public static final String EXTRA_TIME_SELECT = "ehi.EXTRA_TIME_SELECT";


    public static final String REACTOR_ERROR_WRAPPER = "REACTOR_ERROR_WRAPPER_FILTERS";

    private SearchFilterAdapter mAdapter;
    //not using intdef to lower code clutter, 0 = nothing, 1 = api call... 2 = click while api call
    private int mLoadingStages = 0;

    private final View.OnClickListener mApplyClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mLoadingStages == 1) {
                mLoadingStages = 2;
                return;
            }

            EHIAnalyticsEvent.create()
                    .screen(EHIAnalytics.Screen.SCREEN_LOCATIONS.value, SearchLocationsFilterFragment.SCREEN_NAME)
                    .state(EHIAnalytics.State.STATE_SEARCH_FILTER.value)
                    .action(EHIAnalytics.Motion.MOTION_TAP.value, EHIAnalytics.Action.ACTION_APPLY_FILTER.value)
                    .addDictionary(EHIAnalyticsDictionaryUtils.locationsFilter(getViewModel().getFilterTypes(), getResources()))
                    .tagScreen()
                    .tagEvent();

            EHIBundle.Builder builder = new EHIBundle.Builder();
            Bundle bundle = builder.createBundle();
            Intent intent = new Intent();
            bundle.putIntegerArrayList(FILTER_LIST_KEY, getViewModel().getFilterTypes());
            if (getViewModel().getPickupDate() != null) {
                bundle.putSerializable(EXTRA_PICKUP_DATE, getViewModel().getPickupDate());
            }
            if (getViewModel().getReturnDate() != null) {
                bundle.putSerializable(EXTRA_RETURN_DATE, getViewModel().getReturnDate());
            }
            if (getViewModel().getPickupTime() != null) {
                bundle.putSerializable(EXTRA_PICKUP_TIME, getViewModel().getPickupTime());
            }
            if (getViewModel().getReturnTime() != null) {
                bundle.putSerializable(EXTRA_RETURN_TIME, getViewModel().getReturnTime());
            }

            intent.putExtras(bundle);
            getActivity().setResult(Activity.RESULT_OK, intent);
            getActivity().finish();
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = createViewBinding(inflater, R.layout.fr_search_locations_filter, container);

        SearchLocationsFilterFragmentHelper.Extractor extractor = new SearchLocationsFilterFragmentHelper.Extractor(this);

        getViewModel().setPickupDate(extractor.extraPickupDate());
        getViewModel().setPickupTime(extractor.extraPickupTime());
        getViewModel().setReturnDate(extractor.extraReturnDate());
        getViewModel().setReturnTime(extractor.extraReturnTime());

        getViewModel().setFilters(extractor.filterListKey(), getResources());
        initViews();
        setHasOptionsMenu(true);
        return rootView;
    }

    private void initViews() {
        getViewBinding().applyFilterButton.setOnClickListener(mApplyClickListener);

        final ArrayList<SearchLocationsFilterItem> mListOfItems = new ArrayList<>();

        mListOfItems.add(new SearchLocationsFilterItem(
                getResources().getString(R.string.location_filter_airport_title),
                R.drawable.icon_airport_smgray,
                getViewModel().isFilter(EHIFilterList.LOC_FILTER_TYPE_AIRPORT),
                SearchLocationsFilterItem.TYPE_PRIMARY_ITEM,
                EHIFilterList.LOC_FILTER_TYPE_AIRPORT));

        mListOfItems.add(new SearchLocationsFilterItem(
                getResources().getString(R.string.location_filter_port_of_call_title),
                R.drawable.icon_port_02,
                getViewModel().isFilter(EHIFilterList.LOC_FILTER_PORT_STATION),
                SearchLocationsFilterItem.TYPE_PRIMARY_ITEM,
                EHIFilterList.LOC_FILTER_PORT_STATION));

        mListOfItems.add(new SearchLocationsFilterItem(
                getResources().getString(R.string.location_filter_rail_station_title),
                R.drawable.icon_rail_02,
                getViewModel().isFilter(EHIFilterList.LOC_FILTER_RAIL_STATION),
                SearchLocationsFilterItem.TYPE_PRIMARY_ITEM,
                EHIFilterList.LOC_FILTER_RAIL_STATION));

        ArrayList<SectionHeader> sectionHeaders = new ArrayList<>();
        sectionHeaders.add(SectionHeader.Builder.atPosition(0).setTitle(getResources().getString(R.string.location_filter_location_type_header_title)).build());


        SearchFilterAdapter adapter = new SearchFilterAdapter(mListOfItems, this);
        mAdapter = adapter;
        SectionedRecyclerViewAdapter sectionedRecyclerViewAdapter = new SectionedRecyclerViewAdapter(getActivity(), adapter);
        sectionedRecyclerViewAdapter.setSections(sectionHeaders);
        getViewBinding().searchRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        getViewBinding().searchRecyclerView.addItemDecoration(new SegmentedAnimator(getResources().getColor(R.color.ehi_grey_header_bg)));
        getViewBinding().searchRecyclerView.setAdapter(sectionedRecyclerViewAdapter);

        getViewBinding().locationFilterView.setPickupDateListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment datePicker = new FilterDatePickerFragmentHelper.Builder()
                        .extraIsSelectPickup(true)
                        .extraFilters(getViewModel().getFilterTypes())
                        .extraPickupDate(getViewModel().getPickupDate())
                        .extraReturnDate(getViewModel().getReturnDate())
                        .build();
                showModalForResult(getActivity(), datePicker, SELECT_DATE_CODE);
            }
        });

        getViewBinding().locationFilterView.setPickupTimeListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment timePicker = new FilterTimePickerFragmentHelper.Builder()
                        .extraIsPickup(true)
                        .extraFilters(getViewModel().getFilterTypes())
                        .build();
                showModalForResult(getActivity(), timePicker, SELECT_TIME_PICKUP);
            }
        });

        getViewBinding().locationFilterView.setReturnDateListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment datePicker = new FilterDatePickerFragmentHelper.Builder()
                        .extraIsSelectPickup(false)
                        .extraFilters(getViewModel().getFilterTypes())
                        .extraPickupDate(getViewModel().getPickupDate())
                        .extraReturnDate(getViewModel().getReturnDate())
                        .build();
                showModalForResult(getActivity(), datePicker, SELECT_DATE_CODE);
            }
        });

        getViewBinding().locationFilterView.setReturnTimeListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment timePicker = new FilterTimePickerFragmentHelper.Builder()
                        .extraIsPickup(false)
                        .extraFilters(getViewModel().getFilterTypes())
                        .build();
                showModalForResult(getActivity(), timePicker, SELECT_TIME_RETURN);

            }
        });

        getViewBinding().locationFilterView.setResetPickupDateListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getViewModel().resetPickupDate();
            }
        });

        getViewBinding().locationFilterView.setResetReturnDateListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getViewModel().resetReturnDate();
            }
        });

        getViewBinding().locationFilterView.setResetPickupTimeListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getViewModel().resetPickupTime();
            }
        });

        getViewBinding().locationFilterView.setResetReturnTimeListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getViewModel().resetReturnTime();
            }
        });

        getViewBinding().locationFilterView.setFilterTypes(getViewModel().getFilterTypes());
    }


    @Override
    public void onStart() {
        super.onStart();
        getActivity().setTitle(R.string.filter_view_navigation_title);
    }

    @Override
    public void onResume() {
        super.onResume();
        EHIAnalyticsEvent.create()
                .screen(EHIAnalytics.Screen.SCREEN_LOCATIONS.value, SearchLocationsFilterFragment.SCREEN_NAME)
                .state(EHIAnalytics.State.STATE_SEARCH_FILTER.value)
                .addDictionary(EHIAnalyticsDictionaryUtils.locationsFilter(getViewModel().getFilterTypes(), getResources()))
                .tagScreen()
                .tagEvent();
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
            EHIAnalyticsEvent.create()
                    .screen(EHIAnalytics.Screen.SCREEN_LOCATIONS.value, SCREEN_NAME)
                    .state(EHIAnalytics.State.STATE_SEARCH_FILTER.value)
                    .action(EHIAnalytics.Motion.MOTION_TAP.value, EHIAnalytics.Action.ACTION_RESET_FILTER.value)
                    .addDictionary(EHIAnalyticsDictionaryUtils.locationsFilter(EHIFilterList.fetchActiveFilterTypes(getViewModel().getFilters()), getResources()))
                    .tagScreen()
                    .tagEvent();
            getViewModel().clearFilters();
            clearFiltersVisually();
            return true;
        }
        return false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_DATE_CODE) {
                getViewModel().setPickupDate((Date) data.getSerializableExtra(FilterDatePickerFragment.EXTRA_PICKUP_DATE));
                getViewModel().setReturnDate((Date) data.getSerializableExtra(FilterDatePickerFragment.EXTRA_RETURN_DATE));
            }
            if (requestCode == SELECT_TIME_PICKUP) {
                getViewModel().setPickupTime((Date) data.getSerializableExtra(EXTRA_TIME_SELECT));
            }
            if (requestCode == SELECT_TIME_RETURN) {
                getViewModel().setReturnTime((Date) data.getSerializableExtra(EXTRA_TIME_SELECT));
            }
        }
    }

    @Override
    protected void initDependencies() {
        addReaction(new ReactorComputationFunction() {
            @Override
            public void react(ReactorComputation reactorComputation) {
                if (getViewModel().isDateUpdated()) {
                    getViewBinding().locationFilterView.setPickupDateText(getViewModel().getPickupDate());
                    getViewBinding().locationFilterView.setPickupTimeText(getViewModel().getPickupTime());
                    getViewBinding().locationFilterView.setReturnDateText(getViewModel().getReturnDate());
                    getViewBinding().locationFilterView.setReturnTimeText(getViewModel().getReturnTime());
                }
            }
        });

        addReaction(REACTOR_ERROR_WRAPPER, new ReactorComputationFunction() {
            @Override
            public void react(ReactorComputation reactorComputation) {
                if (getViewModel().getErrorWrapper() != null) {
                    DialogUtils.showErrorDialog(getActivity(), getViewModel().getErrorWrapper());
                    getViewModel().setErrorWrapper(null);
                }
            }
        });
    }

    private void clearFiltersVisually() {
        for (int a = 0; a < mAdapter.getItemCount(); a++) {
            mAdapter.setCheckedItem(a, false);
        }
    }

    @Override
    public void itemClicked(int position, View clickedView) {
        SearchLocationsFilterItem item = mAdapter.getItem(position);
        int itemType = item.getFilterType();
        if (item.getType() == SearchLocationsFilterItem.TYPE_PRIMARY_ITEM) {
            CheckBox checkbox = (CheckBox) clickedView;
            if (!checkbox.isChecked()) {
                getViewModel().removeFilter(itemType);
                mAdapter.setCheckedItem(position, false);
            } else {
                getViewModel().addFilter(itemType, EHIFilterList.getFilter(itemType, getResources()));
                mAdapter.setCheckedItem(position, true);
            }
        }
    }

    public static class SegmentedAnimator extends RecyclerView.ItemDecoration {
        private int mColor;

        public SegmentedAnimator(int colorOfLine) {
            mColor = colorOfLine;
        }

        @Override
        public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
            final int left = parent.getPaddingLeft();
            final int right = parent.getWidth() - parent.getPaddingRight();

            final int childCount = parent.getChildCount();
            for (int i = 0; i < childCount; i++) {
                final View child = parent.getChildAt(i);
                final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                        .getLayoutParams();
                final int top = child.getBottom() + params.bottomMargin;
                Paint paint = new Paint();
                paint.setColor(mColor);
                c.drawLine((float) left, (float) top, (float) right, (float) top, paint);
            }
        }
    }

}