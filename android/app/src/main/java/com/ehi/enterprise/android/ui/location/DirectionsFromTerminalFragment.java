package com.ehi.enterprise.android.ui.location;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.databinding.DirectionsFromTerminalFragmentBinding;
import com.ehi.enterprise.android.models.location.EHIWayfindingStep;
import com.ehi.enterprise.android.ui.adapter.SectionHeader;
import com.ehi.enterprise.android.ui.adapter.SectionedRecyclerViewAdapter;
import com.ehi.enterprise.android.ui.fragment.DataBindingViewModelFragment;
import com.ehi.enterprise.android.utils.analytics.EHIAnalytics;
import com.ehi.enterprise.android.utils.analytics.EHIAnalyticsEvent;
import com.isobar.android.newinstancer.Extra;
import com.isobar.android.viewmodel.ViewModel;

import java.util.List;

@ViewModel(DirectionsFromTerminalViewModel.class)
public class DirectionsFromTerminalFragment extends DataBindingViewModelFragment<DirectionsFromTerminalViewModel, DirectionsFromTerminalFragmentBinding> {

    public static final String SCREEN_NAME = "DirectionsFromTerminalFragment";

    @Extra(value = List.class, type = EHIWayfindingStep.class)
    public static final String WAYFINDING_STEPS = "ehi.WAYFINDING_STEPS";

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return createViewBinding(inflater, R.layout.fr_directions_from_terminal, container);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getViewModel().setWayfindingSteps(new DirectionsFromTerminalFragmentHelper.Extractor(this).wayfindingSteps());
        initAdapter();
    }

    private void initAdapter() {
        final WayfindingAdapter adapter = new WayfindingAdapter(getViewModel().getWayfindingSteps());

        final SectionedRecyclerViewAdapter sectionsAdapter = new SectionedRecyclerViewAdapter(getActivity(), adapter);

        sectionsAdapter.setSections(SectionHeader.Builder
                        .atPosition(0)
                        .setTitle(getString(R.string.terminal_directions_title).toUpperCase())
                        .build());

        final RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        getViewBinding().frDirectionsFromTerminalList.setLayoutManager(layoutManager);
        getViewBinding().frDirectionsFromTerminalList.setAdapter(sectionsAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        EHIAnalyticsEvent.create()
                .screen(EHIAnalytics.Screen.SCREEN_LOCATIONS.value, DirectionsFromTerminalFragment.SCREEN_NAME)
                .state(EHIAnalytics.State.STATE_WAYFINDING.value)
                .tagScreen()
                .tagEvent();
    }
}
