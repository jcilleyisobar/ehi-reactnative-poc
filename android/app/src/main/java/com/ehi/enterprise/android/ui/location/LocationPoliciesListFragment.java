package com.ehi.enterprise.android.ui.location;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.databinding.LocationPoliciesListFragmentBinding;
import com.ehi.enterprise.android.models.location.EHIPolicy;
import com.ehi.enterprise.android.ui.adapter.SectionHeader;
import com.ehi.enterprise.android.ui.adapter.SectionedRecyclerViewAdapter;
import com.ehi.enterprise.android.ui.fragment.DataBindingViewModelFragment;
import com.ehi.enterprise.android.ui.viewmodel.ManagersAccessViewModel;
import com.ehi.enterprise.android.utils.DLog;
import com.ehi.enterprise.android.utils.analytics.EHIAnalytics;
import com.ehi.enterprise.android.utils.analytics.EHIAnalyticsEvent;
import com.ehi.enterprise.android.utils.exceptions.NoArgumentsFoundException;
import com.isobar.android.newinstancer.Extra;
import com.isobar.android.viewmodel.ViewModel;

import java.util.List;

@ViewModel(ManagersAccessViewModel.class)
public class LocationPoliciesListFragment
        extends DataBindingViewModelFragment<ManagersAccessViewModel, LocationPoliciesListFragmentBinding> {

    public static final String TAG = "LocationPoliciesListFragment";
    public static final String SCREEN_NAME = "LocationPoliciesListFragment";

    @Extra(value = List.class, type = EHIPolicy.class, required = false)
    public static final String EXTRA_POLICIES = "ehi.EXTRA_POLICIES";

    private List<EHIPolicy> mPolicies;

    private PoliciesAdapter mAdapter;
    private SectionedRecyclerViewAdapter mSectionsAdapter;

    private AdapterView.OnItemClickListener mOnItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent = new PolicyDetailsActivityHelper.Builder()
                    .extraPolicy(mPolicies.get(position))
                    .build(getActivity());

            startActivity(intent);
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return createViewBinding(inflater, R.layout.fr_location_policies_list, container);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        LocationPoliciesListFragmentHelper.Extractor extractor = new LocationPoliciesListFragmentHelper.Extractor(this);
        if (extractor.extraPolicies() == null) {
            DLog.e(TAG, new NoArgumentsFoundException());
            getActivity().finish();
            return;
        }

        mPolicies = extractor.extraPolicies();

        initAdapter();
    }

    private void initAdapter() {
        mAdapter = new PoliciesAdapter(mPolicies);
        mAdapter.setOnItemClickListener(mOnItemClickListener);

        mSectionsAdapter = new SectionedRecyclerViewAdapter(getActivity(), mAdapter);
        mSectionsAdapter.setSections(SectionHeader.Builder
                        .atPosition(0)
                        .setTitle(getString(R.string.policies_title).toUpperCase())
                .build());

        getViewBinding().recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));
        getViewBinding().recyclerView.setAdapter(mSectionsAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        EHIAnalyticsEvent.create()
                .screen(EHIAnalytics.Screen.SCREEN_LOCATIONS.value, LocationPoliciesListFragment.SCREEN_NAME)
                .state(EHIAnalytics.State.STATE_POLICIES.value)
                .tagScreen()
                .tagEvent();
    }
}
