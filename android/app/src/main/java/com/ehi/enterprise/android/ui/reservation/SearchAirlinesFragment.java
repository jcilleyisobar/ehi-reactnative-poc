package com.ehi.enterprise.android.ui.reservation;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.databinding.SearchFragmentBinding;
import com.ehi.enterprise.android.models.reservation.EHIAirlineDetails;
import com.ehi.enterprise.android.ui.activity.ModalActivity;
import com.ehi.enterprise.android.ui.fragment.DataBindingViewModelFragment;
import com.ehi.enterprise.android.utils.EHIBundle;
import com.ehi.enterprise.android.utils.FragmentUtils;
import com.isobar.android.newinstancer.Extra;
import com.isobar.android.viewmodel.ViewModel;

import java.util.List;

import io.dwak.reactor.ReactorComputation;
import io.dwak.reactor.interfaces.ReactorComputationFunction;

@ViewModel(AirlinesListViewModel.class)
public class SearchAirlinesFragment
        extends DataBindingViewModelFragment<AirlinesListViewModel, SearchFragmentBinding>
        implements ModalActivity.SearchListener {

    @Extra(value = List.class, type = EHIAirlineDetails.class)
    public static final String FLIGHT_DETAILS = "EXTRA_FLIGHT_DETAILS";

    private AirlinesListRecyclerAdapter mAdapter;

    private AirlinesListRecyclerAdapter.OnAirlineClickListener onAirlineClickListener = new AirlinesListRecyclerAdapter.OnAirlineClickListener() {
        @Override
        public void onAirlineClicked(EHIAirlineDetails selectedDetails) {
            Intent resultIntent = new Intent();
            final Bundle bundle = new EHIBundle.Builder()
                    .putEHIModel(FlightDetailsFragment.KEY_AIRLINE_DETAILS, selectedDetails)
                    .createBundle();
            resultIntent.putExtras(bundle);
            getActivity().setResult(Activity.RESULT_OK, resultIntent);
            getActivity().finish();
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final SearchAirlinesFragmentHelper.Extractor extractor = new SearchAirlinesFragmentHelper.Extractor(this);
        getViewModel().setAirlines(extractor.flightDetails());
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
        bind(FragmentUtils.progress(getViewModel().progress, getActivity()));

        addReaction(new ReactorComputationFunction() {
            @Override
            public void react(ReactorComputation reactorComputation) {
                List<EHIAirlineDetails> airlines = getViewModel().getAirlines();
                if (airlines != null) {
                    mAdapter = new AirlinesListRecyclerAdapter(airlines, getViewModel().getOther());
                    mAdapter.setOnAirlineClickListener(onAirlineClickListener);
                    getViewBinding().recyclerView.setAdapter(mAdapter);
                    mAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    public void onSearchTerm(String term) {
        mAdapter.getFilter().filter(term);
    }
}
