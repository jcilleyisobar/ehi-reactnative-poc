package com.ehi.enterprise.android.ui.dashboard;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.databinding.SearchFragmentBinding;
import com.ehi.enterprise.android.models.profile.EHICountry;
import com.ehi.enterprise.android.ui.activity.ModalActivity;
import com.ehi.enterprise.android.ui.fragment.DataBindingViewModelFragment;
import com.ehi.enterprise.android.utils.DialogUtils;
import com.ehi.enterprise.android.utils.FragmentUtils;
import com.isobar.android.newinstancer.NoExtras;
import com.isobar.android.viewmodel.ViewModel;

import java.util.List;

import io.dwak.reactor.ReactorComputation;
import io.dwak.reactor.interfaces.ReactorComputationFunction;

@NoExtras
@ViewModel(CountriesListViewModel.class)
public class CountriesListFragment
        extends DataBindingViewModelFragment<CountriesListViewModel, SearchFragmentBinding>
        implements ModalActivity.SearchListener {

    private CountriesListRecyclerAdapter mAdapter;

    private CountriesListRecyclerAdapter.OnCountryClickListener mOnCountryClickListener = new CountriesListRecyclerAdapter.OnCountryClickListener() {
        @Override
        public void onCountryClicked(EHICountry country) {
            getViewModel().setPreferredRegion(country.getCountryCode());
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getViewModel().populateCountriesList();
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = createViewBinding(inflater, R.layout.fr_search_list, container);
        initViews();
        return rootView;
    }

    private void initViews() {
        getViewBinding().recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    @Override
    protected void initDependencies() {
        super.initDependencies();
        bind(DialogUtils.errorDialog(getViewModel().errorWrapper, getActivity()));
        bind(FragmentUtils.progress(getViewModel().progress, getActivity()));

        addReaction(new ReactorComputationFunction() {
            @Override
            public void react(ReactorComputation reactorComputation) {
                List<EHICountry> countries = getViewModel().getCountriesList().getValue();
                if (countries != null) {
                    mAdapter = new CountriesListRecyclerAdapter(countries);
                    mAdapter.setOnCountryClickListener(mOnCountryClickListener);
                    getViewBinding().recyclerView.setAdapter(mAdapter);
                    mAdapter.notifyDataSetChanged();
                }
            }
        });

        addReaction(new ReactorComputationFunction() {
            @Override
            public void react(ReactorComputation reactorComputation) {
                if (getViewModel().isWeekendSpecialContractRequestDone()) {
                    getActivity().finish();
                }
            }
        });
    }

    @Override
    public void onSearchTerm(String term) {
        if (mAdapter == null) {
            return;
        }

        mAdapter.getFilter().filter(term);
    }
}
