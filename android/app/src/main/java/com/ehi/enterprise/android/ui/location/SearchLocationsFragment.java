package com.ehi.enterprise.android.ui.location;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.app.EHIStringToken;
import com.ehi.enterprise.android.databinding.SearchLocationsFragmentBinding;
import com.ehi.enterprise.android.models.location.EHICityLocation;
import com.ehi.enterprise.android.models.location.solr.EHISolrLocation;
import com.ehi.enterprise.android.network.responses.location.solr.EHIPostalCodeLocation;
import com.ehi.enterprise.android.ui.adapter.SectionHeaderViewHolder;
import com.ehi.enterprise.android.ui.fragment.DataBindingViewModelFragment;
import com.ehi.enterprise.android.ui.location.interfaces.ISearchByQueryDelegate;
import com.ehi.enterprise.android.ui.location.interfaces.OnClearRecentActivityListener;
import com.ehi.enterprise.android.ui.location.interfaces.OnSolrLocationInfoClickListener;
import com.ehi.enterprise.android.utils.DialogUtils;
import com.ehi.enterprise.android.utils.FragmentUtils;
import com.ehi.enterprise.android.utils.IntentUtils;
import com.ehi.enterprise.android.utils.analytics.EHIAnalytics;
import com.ehi.enterprise.android.utils.analytics.EHIAnalyticsDictionaryUtils;
import com.ehi.enterprise.android.utils.analytics.EHIAnalyticsEvent;
import com.ehi.enterprise.android.utils.exceptions.NotImplementedException;
import com.isobar.android.newinstancer.Extra;
import com.isobar.android.tokenizedstring.TokenizedString;
import com.isobar.android.viewmodel.ViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.Timer;
import java.util.TimerTask;

import io.dwak.reactor.ReactorComputation;
import io.dwak.reactor.interfaces.ReactorComputationFunction;

@ViewModel(SearchLocationsFragmentViewModel.class)
public class SearchLocationsFragment
        extends DataBindingViewModelFragment<SearchLocationsFragmentViewModel, SearchLocationsFragmentBinding>
        implements ISearchByQueryDelegate, OnClearRecentActivityListener {

    public static final String SCREEN_NAME = "SearchLocationsFragment";

    @Extra(int.class)
    public static final String EXTRA_FLOW = "ehi.EXTRA_FLOW";

    private static final int SEARCH_STRING_MIN_LENGTH = 3;
    private static final int SEARCH_DELAY = 600;
    public static final int SPINNER_DELAY = 1000 + SEARCH_DELAY;

    public static final String SEARCH_RESULTS_REACTION = "SEARCH_RESULTS_REACTION";
    public static final String NO_RESULTS_REACTION = "NO_RESULTS_REACTION";
    public static final String ERROR_REACTION = "ERROR_REACTION";
    public static final String RECENT_LOCATIONS_REACTION = "RECENT_LOCATION_REACTION";
    public static final String SEARCH_TERM = "SEARCH_TERM";


    private FavoritesRecentAdapter mFavoritesRecentAdapter;
    private SearchSolrLocationsAdapter mSolrSearchLocationsAdapter;

    private Timer mTimer;
    private TimerTask mTimerTask;
    private Timer mProgressSpinnerTimer;
    private TimerTask mProgressSpinnerTimeTask;

    private String mSearchTerm;

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view == getViewBinding().nearMeButton) {
                ((OnSolrLocationInfoClickListener) getActivity()).onShowCityLocation(null);
                EHIAnalyticsEvent.create()
                        .screen(EHIAnalytics.Screen.SCREEN_LOCATIONS.value, SearchLocationsFragment.SCREEN_NAME)
                        .state(EHIAnalytics.State.STATE_SEARCH_NO_LOCATIONS.value)
                        .action(EHIAnalytics.Motion.MOTION_TAP.value, EHIAnalytics.Action.ACTION_NEARBY.value)
                        .addDictionary(EHIAnalyticsDictionaryUtils.locationSearch(mSearchTerm, isZeroResult()))
                        .tagScreen()
                        .tagEvent();

            } else if (view == getViewBinding().nearbyLocationsContainer) {
                ((OnSolrLocationInfoClickListener) getActivity()).onShowCityLocation(null);
                EHIAnalyticsEvent.create()
                        .screen(EHIAnalytics.Screen.SCREEN_LOCATIONS.value, SearchLocationsFragment.SCREEN_NAME)
                        .state(EHIAnalytics.State.STATE_SEARCH.value)
                        .action(EHIAnalytics.Motion.MOTION_TAP.value, EHIAnalytics.Action.ACTION_NEARBY.value)
                        .addDictionary(EHIAnalyticsDictionaryUtils.locationSearch(mSearchTerm, isZeroResult()))
                        .tagScreen()
                        .tagEvent();
            } else if (view == getViewBinding().callUsButton) {
                IntentUtils.callNumber(getActivity(), getViewModel().getSupportPhoneNumber());
                EHIAnalyticsEvent.create()
                        .screen(EHIAnalytics.Screen.SCREEN_LOCATIONS.value, SearchLocationsFragment.SCREEN_NAME)
                        .state(EHIAnalytics.State.STATE_SEARCH_NO_LOCATIONS.value)
                        .action(EHIAnalytics.Motion.MOTION_TAP.value, EHIAnalytics.Action.ACTION_CALL_US.value)
                        .addDictionary(EHIAnalyticsDictionaryUtils.locationSearch(mSearchTerm, isZeroResult()))
                        .tagScreen()
                        .tagEvent();
            }
        }
    };

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (getViewModel().getFlow() != 0) {
            outState.putInt(EXTRA_FLOW, getViewModel().getFlow());
        }

        if (!TextUtils.isEmpty(mSearchTerm)) {
            outState.putString(SEARCH_TERM, mSearchTerm);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = createViewBinding(inflater, R.layout.fr_search_locations, container);
        setHasOptionsMenu(true);
        initViews();
        initAdapters();
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        EHIAnalyticsEvent.create()
                .screen(EHIAnalytics.Screen.SCREEN_LOCATIONS.value, SearchLocationsFragment.SCREEN_NAME)
                .state(EHIAnalytics.State.STATE_SEARCH.value)
                .macroEvent(EHIAnalytics.MacroEvent.MACRO_LOCATION_SEARCH.value)
                .addDictionary(EHIAnalyticsDictionaryUtils.locationSearch(mSearchTerm, isZeroResult()))
                .tagScreen()
                .tagEvent()
                .tagMacroEvent();
    }

    @Override
    public void onPause() {
        super.onPause();
        stopTimerTask();
    }

    public boolean isZeroResult() {
        return getViewBinding().noSearchResultsContainer != null
                && getViewBinding().noSearchResultsContainer.getVisibility() == View.VISIBLE;
    }


    private void initViews() {
        getViewBinding().nearbyLocationsContainer.setOnClickListener(mOnClickListener);
        getViewBinding().callUsButton.setOnClickListener(mOnClickListener);
        getViewBinding().nearMeButton.setOnClickListener(mOnClickListener);
        getViewBinding().searchLocationsRecycler.setHasFixedSize(true);
        getViewBinding().searchLocationsRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    private void initAdapters() {
        mFavoritesRecentAdapter = new FavoritesRecentAdapter(getActivity(),
                (OnSolrLocationInfoClickListener) getActivity(),
                new FavoritesRecentAdapter.OnRecentsClearListener() {
                    @Override
                    public void onClearRecents() {
                        clearRecentActivity();
                    }
                });

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            boolean isFavorite = false;

            @Override
            public int getSwipeDirs(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                if (viewHolder instanceof SectionHeaderViewHolder) { // make sure we can't swipe the headers
                    return 0;
                }

                if (!getViewBinding().searchLocationsRecycler.getAdapter().equals(mFavoritesRecentAdapter)) {
                    return 0;
                }
                return super.getSwipeDirs(recyclerView, viewHolder);
            }

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                final EHISolrLocation locationForRemoval = mFavoritesRecentAdapter.markPositionForRemoval((viewHolder.getAdapterPosition()));

                if (getViewModel().isFavoriteLocation(locationForRemoval)) {
                    getViewModel().removeFavoriteLocation(locationForRemoval);
                    isFavorite = true;
                } else {
                    getViewModel().removeRecentLocation(locationForRemoval);
                    isFavorite = false;
                }
                Snackbar.make(getViewBinding().getRoot(), R.string.delete_quickstart_reservation_title, Snackbar.LENGTH_LONG)
                        .setActionTextColor(getResources().getColor(R.color.ehi_primary))
                        .setAction(R.string.standard_undo_button, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mFavoritesRecentAdapter.undoLocationRemoval();
                                if (isFavorite) {
                                    getViewModel().addFavoriteLocation(locationForRemoval);
                                } else {
                                    getViewModel().addRecentLocation(locationForRemoval);
                                }
                            }
                        })
                        .show();
            }
        });
        itemTouchHelper.attachToRecyclerView(getViewBinding().searchLocationsRecycler);

        mSolrSearchLocationsAdapter = new SearchSolrLocationsAdapter(getActivity(), (OnSolrLocationInfoClickListener) getActivity());
        getViewBinding().searchLocationsRecycler.setAdapter(mSolrSearchLocationsAdapter);
    }

    @Override
    protected void initDependencies() {
        addReaction(SEARCH_RESULTS_REACTION, new ReactorComputationFunction() {
            @Override
            public void react(ReactorComputation reactorComputation) {
                final List<EHISolrLocation> airportLocations = getViewModel().getAirportLocations();
                final List<EHISolrLocation> branchLocations = getViewModel().getBranchLocations();
                final List<EHICityLocation> cityLocations = getViewModel().getCityLocations();
                final List<EHIPostalCodeLocation> postalCodeLocations = getViewModel().getPostalCodeLocations();
                if (airportLocations != null
                        && branchLocations != null
                        && cityLocations != null) {
                    stopTimerTask();
                    populateSolrSearchList(airportLocations, branchLocations, cityLocations, postalCodeLocations);
                    FragmentUtils.removeProgressFragment(getActivity());
                }
            }
        });

        addReaction(NO_RESULTS_REACTION, new ReactorComputationFunction() {
            @Override
            public void react(ReactorComputation reactorComputation) {
                stopTimerTask();
                if (getViewModel().getShowNoResults()) {
                    FragmentUtils.removeProgressFragment(getActivity());
                    getViewBinding().searchLocationsRecycler.setVisibility(View.GONE);
                    getViewBinding().noSearchResultsContainer.setVisibility(View.VISIBLE);
                    CharSequence noMatchFor = new TokenizedString.Formatter<EHIStringToken>(getResources())
                            .formatString(R.string.locations_empty_query_title)
                            .addTokenAndValue(EHIStringToken.QUERY, mSearchTerm)
                            .format();
                    getViewBinding().sorryNoResultsTextView.setText(noMatchFor);

                    EHIAnalyticsEvent.create()
                            .screen(EHIAnalytics.Screen.SCREEN_LOCATIONS.value, SearchLocationsFragment.SCREEN_NAME)
                            .state(EHIAnalytics.State.STATE_SEARCH_NO_LOCATIONS.value)
                            .addDictionary(EHIAnalyticsDictionaryUtils.locationSearch(mSearchTerm, isZeroResult()))
                            .tagScreen()
                            .tagEvent();
                } else {
                    getViewBinding().noSearchResultsContainer.setVisibility(View.GONE);
                    getViewBinding().searchLocationsRecycler.setVisibility(View.VISIBLE);
                }
            }
        });


        addReaction(RECENT_LOCATIONS_REACTION, new ReactorComputationFunction() {
            @Override
            public void react(ReactorComputation reactorComputation) {
                Map<String, EHISolrLocation> mFavoritesMap = getViewModel().getFavoriteLocations();
                Stack<EHISolrLocation> mRecentHistoryStack = getViewModel().getRecentLocations();
                if (TextUtils.isEmpty(mSearchTerm)) {
                    populateFavoritesRecentActivityList(mFavoritesMap, mRecentHistoryStack);
                }
            }
        });


        addReaction(ERROR_REACTION, new ReactorComputationFunction() {
            @Override
            public void react(ReactorComputation reactorComputation) {
                FragmentUtils.removeProgressFragment(getActivity());
                stopTimerTask();
                if (getViewModel().getErrorResponseWrapper() != null) {
                    DialogUtils.showErrorDialog(getActivity(), getViewModel().getErrorResponseWrapper());
                    getViewModel().setErrorResponseWrapper(null);
                }
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (!(getActivity() instanceof OnSolrLocationInfoClickListener)) {
            throw new NotImplementedException();
        }

        SearchLocationsFragmentHelper.Extractor extractor = new SearchLocationsFragmentHelper.Extractor(this);

        getViewModel().setFlow(extractor.extraFlow());

        if (getArguments().containsKey(SEARCH_TERM)) {
            mSearchTerm = getArguments().getString(SEARCH_TERM);
            searchByQuery(mSearchTerm);
        }
    }

    private void populateFavoritesRecentActivityList(Map<String, EHISolrLocation> favoritesMap, Stack<EHISolrLocation> recentHistoryStack) {
        getViewBinding().nearbyLocationsContainer.setVisibility(View.VISIBLE);
        mFavoritesRecentAdapter.clear();
        getViewBinding().searchLocationsRecycler.setAdapter(mFavoritesRecentAdapter);


        if (favoritesMap != null && !favoritesMap.isEmpty()) {
            final List<EHISolrLocation> favoriteList = new ArrayList<>(favoritesMap.values());
            for (EHISolrLocation favoriteLocation : favoriteList) {
                mFavoritesRecentAdapter.addFavoriteLocation(favoriteLocation);
            }
        }

        if (recentHistoryStack != null && !recentHistoryStack.isEmpty()) {
            for (EHISolrLocation location : recentHistoryStack) {
                mFavoritesRecentAdapter.addRecentItem(location);
            }
        }
    }

    private void populateSolrSearchList(List<EHISolrLocation> airportLocations,
                                        List<EHISolrLocation> branchLocations,
                                        List<EHICityLocation> cityLocations,
                                        List<EHIPostalCodeLocation> postalCodeLocations) {

        getViewBinding().nearbyLocationsContainer.setVisibility(View.GONE);
        mSolrSearchLocationsAdapter.clear();

        getViewBinding().searchLocationsRecycler.setAdapter(mSolrSearchLocationsAdapter);


        if (!airportLocations.isEmpty()) {
            mSolrSearchLocationsAdapter.addLocations(airportLocations);
        }

        if (!branchLocations.isEmpty()) {
            mSolrSearchLocationsAdapter.addLocations(branchLocations);
        }

        if (cityLocations != null && !cityLocations.isEmpty()) {
            mSolrSearchLocationsAdapter.addCities(cityLocations);
        }

        if (postalCodeLocations != null && !postalCodeLocations.isEmpty()) {
            mSolrSearchLocationsAdapter.addPostalLocations(postalCodeLocations);
        }

        mSolrSearchLocationsAdapter.notifyDataSetChanged();
    }

    public void startTimer(String query) {
        mTimer = new Timer();
        mProgressSpinnerTimer = new Timer();
        initializeTimerTask(query);
        mTimer.schedule(mTimerTask, SEARCH_DELAY); //
        mProgressSpinnerTimer.schedule(mProgressSpinnerTimeTask, SPINNER_DELAY);
    }

    public void stopTimerTask() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
        if (mProgressSpinnerTimer != null) {
            mProgressSpinnerTimer.cancel();
            mProgressSpinnerTimer = null;
        }
    }

    public void initializeTimerTask(final String query) {
        mTimerTask = new TimerTask() {
            public void run() {
                getViewModel().searchForSolrLocation(query);
                mSearchTerm = query;
            }
        };
        mProgressSpinnerTimeTask = new TimerTask() {
            @Override
            public void run() {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        FragmentUtils.addProgressFragment(getActivity());
                    }
                });
            }
        };
    }

    @Override
    public void searchByQuery(String query) {
        if (query.length() >= SEARCH_STRING_MIN_LENGTH) {
            stopTimerTask();
            startTimer(query);
        } else if (query.length() == 0) {
            //reset recycler
            stopTimerTask();
            mSearchTerm = null;
            mSolrSearchLocationsAdapter.clear();
            getViewBinding().nearbyLocationsContainer.setVisibility(View.VISIBLE);
            getViewBinding().searchLocationsRecycler.setAdapter(mFavoritesRecentAdapter);
            getViewModel().clearQuery();
            FragmentUtils.removeProgressFragment(getActivity());
        }
    }

    @Override
    public void clearRecentActivity() {
        new AlertDialog.Builder(getActivity())
                .setTitle(R.string.locations_confirm_delete_recents_title)
                .setMessage(R.string.locations_confirm_delete_recents_message)
                .setPositiveButton(getActivity().getResources().getString(R.string.alert_okay_title),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                getViewModel().clearRecentLocations();
                                dialog.dismiss();
                            }
                        })
                .setNegativeButton(getActivity().getResources().getString(R.string.standard_button_cancel), null)
                .create()
                .show();
    }
}