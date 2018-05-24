package com.ehi.enterprise.android.ui.reservation.history;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.databinding.MyRentalsFragmentBinding;
import com.ehi.enterprise.android.models.reservation.EHIReservation;
import com.ehi.enterprise.android.models.reservation.EHITripSummary;
import com.ehi.enterprise.android.ui.confirmation.ConfirmationActivityHelper;
import com.ehi.enterprise.android.ui.fragment.DataBindingViewModelFragment;
import com.ehi.enterprise.android.ui.location.SearchLocationsActivity;
import com.ehi.enterprise.android.ui.location.SearchLocationsActivityHelper;
import com.ehi.enterprise.android.ui.notification.NotificationSchedulerServiceHelper;
import com.ehi.enterprise.android.utils.DialogUtils;
import com.ehi.enterprise.android.utils.FragmentUtils;
import com.ehi.enterprise.android.utils.IntentUtils;
import com.ehi.enterprise.android.utils.ListUtils;
import com.ehi.enterprise.android.utils.analytics.EHIAnalytics;
import com.ehi.enterprise.android.utils.analytics.EHIAnalyticsDictionaryUtils;
import com.ehi.enterprise.android.utils.analytics.EHIAnalyticsEvent;
import com.ehi.enterprise.android.utils.analytics.IRootMenuScreen;
import com.isobar.android.newinstancer.NoExtras;
import com.isobar.android.viewmodel.ViewModel;

import java.util.Calendar;
import java.util.List;

import io.dwak.reactor.ReactorComputation;
import io.dwak.reactor.interfaces.ReactorComputationFunction;

@NoExtras
@ViewModel(MyRentalsViewModel.class)
public class MyRentalsFragment extends DataBindingViewModelFragment<MyRentalsViewModel, MyRentalsFragmentBinding>
        implements MyRentalsRecyclerAdapter.ReservationAdapterListener, IRootMenuScreen {

    public static final String TAG = "MyRentalsFragment";

    public static final String SELECTOR_REACTION = "SELECTOR_REACTION";
    public static final String CURRENT_UPCOMING_RENTALS_REACTION = "CURRENT_UPCOMING_RENTALS_REACTION";
    public static final String PAST_RENTALS = "PAST_RENTALS";
    public static final String ERROR_REACTION = "ERROR_REACTION";
    public static final String RESERVATION_REQUEST_REACTION = "RESERVATION_REQUEST_REACTION";
    public static final String NOTIFICATION_REACTION = "NOTIFICATION_REACTION";
    private MyRentalsRecyclerAdapter mPastTripsAdapter;
    private MyRentalsRecyclerAdapter mUpcomingRentalsAdapter;

    private View.OnClickListener mContactUsOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(@NonNull View view) {
            IntentUtils.callNumber(getActivity(), getViewModel().getSupportPhoneNumber());
            String state;
            if (getViewBinding().myRentalsRecycler.getAdapter() == mUpcomingRentalsAdapter) {
                state = EHIAnalytics.State.STATE_UPCOMING_RENTALS.value;
            } else {
                state = EHIAnalytics.State.STATE_PAST_RENTALS.value;
            }
            EHIAnalyticsEvent.create()
                    .screen(EHIAnalytics.Screen.SCREEN_MY_RENTALS.value, MyRentalsFragment.TAG)
                    .state(state)
                    .action(EHIAnalytics.Motion.MOTION_TAP.value, EHIAnalytics.Action.ACTION_CONTACT_US.value)
                    .addDictionary(EHIAnalyticsDictionaryUtils.signIn())
                    .tagScreen()
                    .tagEvent();

        }
    };
    private MyRentalsFooter mLookupRentalFooter = new MyRentalsFooter(
            0,
            0,
            R.string.rentals_footer_lookup_button_text,
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onLookUpRentalClicked();
                }
            }
    );
    private final View.OnClickListener mStartRentalOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            startActivity(new SearchLocationsActivityHelper.Builder()
                    .extraFlow(SearchLocationsActivity.FLOW_PICKUP_LOCATION_ROUND_TRIP)
                    .extraShowStartReservation(true)
                    .isModify(false)
                    .build(getActivity()));
        }
    };
    private final View.OnClickListener mDontSeeMyRentalsOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            showModalDialog(
                    getActivity(),
                    new DontSeeRentalModalDialogFragmentHelper.Builder().build()
            );
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        createViewBinding(inflater, R.layout.fr_my_rentals, container);
        initViews();
        return getViewBinding().getRoot();
    }

    private void initViews() {
        getViewBinding().myRentalsRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));

        MyRentalsFooter contactUsFooter = new MyRentalsFooter(
                0,
                R.drawable.icon_phone_02,
                R.string.rentals_footer_contact_button_text,
                mContactUsOnClickListener
        );

        final MyRentalsFooter startNewRentalFooter = new MyRentalsFooter(
                0,
                0,
                R.string.rentals_start_reservation_button,
                mStartRentalOnClickListener
        );

        final MyRentalsFooter dontSeeMyRentalFooter = new MyRentalsFooter(
                0,
                0,
                R.string.rentals_dont_see_rentals,
                mDontSeeMyRentalsOnClickListener
        );

        mPastTripsAdapter = new MyRentalsRecyclerAdapter(getActivity(), this);
        mPastTripsAdapter.addSelector();

        mPastTripsAdapter.addFooter(dontSeeMyRentalFooter);
        mPastTripsAdapter.addFooter(startNewRentalFooter);
        mPastTripsAdapter.addFooter(contactUsFooter);

        mUpcomingRentalsAdapter = new MyRentalsRecyclerAdapter(getActivity(), this);
        mUpcomingRentalsAdapter.addSelector();
        mUpcomingRentalsAdapter.addFooter(startNewRentalFooter);
        mUpcomingRentalsAdapter.addFooter(contactUsFooter);
    }

    @Override
    public void onStart() {
        super.onStart();
        getActivity().setTitle(R.string.rentals_navigation_title);
    }

    @Override
    public void trackScreenChange() {
        String state;
        if (getViewBinding().myRentalsRecycler.getAdapter() == mUpcomingRentalsAdapter) {
            state = EHIAnalytics.State.STATE_UPCOMING_RENTALS.value;
        } else {
            state = EHIAnalytics.State.STATE_PAST_RENTALS.value;
        }
        EHIAnalyticsEvent.create()
                .screen(EHIAnalytics.Screen.SCREEN_MY_RENTALS.value, MyRentalsFragment.TAG)
                .state(state)
                .addDictionary(EHIAnalyticsDictionaryUtils.signIn())
                .tagScreen()
                .tagEvent();
    }

    @Override
    protected void initDependencies() {
        super.initDependencies();
        bind(FragmentUtils.progress(getViewModel().progress, getActivity()));
        bind(DialogUtils.errorDialog(getViewModel().mErrorResponseWrapper, getActivity()));
        addReaction(SELECTOR_REACTION, new ReactorComputationFunction() {
            @Override
            public void react(ReactorComputation reactorComputation) {
                switch (getViewModel().getViewState()) {
                    case MyRentalsViewModel.UPCOMING:
                        EHIAnalyticsEvent.create()
                                .screen(EHIAnalytics.Screen.SCREEN_MY_RENTALS.value, MyRentalsFragment.TAG)
                                .state(EHIAnalytics.State.STATE_UPCOMING_RENTALS.value)
                                .addDictionary(EHIAnalyticsDictionaryUtils.signIn())
                                .tagScreen()
                                .tagEvent();
                        getViewBinding().myRentalsRecycler.swapAdapter(mUpcomingRentalsAdapter, false);

                        runNonReactive(new ReactorComputationFunction() {
                            @Override
                            public void react(ReactorComputation reactorComputation) {
                                if (getViewModel().getUpcomingRentals() == null &&
                                        getViewModel().getLoyaltyNumber() != null) {

                                    getViewModel().requestUpcomingRentals(getViewModel().getLoyaltyNumber(),
                                            getViewModel().getEndDate()
                                    );

                                    getViewModel().requestCurrentRentals(getViewModel().getLoyaltyNumber());
                                }
                            }
                        });
                        break;

                    case MyRentalsViewModel.PAST:
                        EHIAnalyticsEvent.create()
                                .screen(EHIAnalytics.Screen.SCREEN_MY_RENTALS.value, MyRentalsFragment.TAG)
                                .state(EHIAnalytics.State.STATE_PAST_RENTALS.value)
                                .addDictionary(EHIAnalyticsDictionaryUtils.signIn())
                                .tagScreen()
                                .tagEvent();
                        getViewBinding().myRentalsRecycler.swapAdapter(mPastTripsAdapter, false);

                        // This is just to align with iOS until the services catch up
                        final Calendar toCal = Calendar.getInstance();
                        toCal.set(Calendar.HOUR_OF_DAY, 0);
                        toCal.set(Calendar.MINUTE, 0);
                        final Calendar fromCal = Calendar.getInstance();
                        fromCal.add(Calendar.DAY_OF_YEAR, -360);
                        fromCal.set(Calendar.HOUR_OF_DAY, 0);
                        fromCal.set(Calendar.MINUTE, 0);

                        runNonReactive(new ReactorComputationFunction() {
                            @Override
                            public void react(ReactorComputation reactorComputation) {
                                if (getViewModel().getPastTripSummaries() == null
                                        && getViewModel().getLoyaltyNumber() != null) {
                                    getViewModel().requestPastRentals(
                                            getViewModel().getLoyaltyNumber(), fromCal.getTime(), toCal.getTime());
                                }
                            }
                        });
                        break;
                }
            }
        });

        addReaction(CURRENT_UPCOMING_RENTALS_REACTION, new ReactorComputationFunction() {
            @Override
            public void react(ReactorComputation reactorComputation) {
                if (getViewModel().getCurrentRentals() != null && !getViewModel().getCurrentRentals().isEmpty()) {
                    for (EHITripSummary ehiTripSummary : getViewModel().getCurrentRentals()) {
                        mUpcomingRentalsAdapter.addCurrentTrip(ehiTripSummary);
                    }

                    mUpcomingRentalsAdapter.addFooter(0, mLookupRentalFooter);
                }
                if (getViewModel().getUpcomingRentals() != null && !getViewModel().getUpcomingRentals().isEmpty()) {
                    mUpcomingRentalsAdapter.removeLoadMoreButton();

                    for (EHITripSummary ehiTripSummary : getViewModel().getUpcomingRentals()) {
                        mUpcomingRentalsAdapter.addUpcomingTrip(ehiTripSummary);
                    }

                    if (getViewModel().areMoreRecordsAvailable()) {
                        mUpcomingRentalsAdapter.addLoadMoreButton(getString(R.string.rentals_load_more_button));
                    }

                    mUpcomingRentalsAdapter.addFooter(0, mLookupRentalFooter);

                }

                if (getViewModel().getCurrentRentals() != null && getViewModel().getCurrentRentals().isEmpty()
                        && getViewModel().getUpcomingRentals() != null && getViewModel().getUpcomingRentals().isEmpty()) {
                    mUpcomingRentalsAdapter.showEmptyCell(getString(R.string.rentals_fallback_upcoming_text), true);
                }
            }
        });

        addReaction(PAST_RENTALS, new ReactorComputationFunction() {
            @Override
            public void react(ReactorComputation reactorComputation) {
                if (getViewModel().getPastTripSummaries() != null) {
                    if (getViewModel().getPastTripSummaries().isEmpty()) {
                        mPastTripsAdapter.showEmptyCell(getString(R.string.rentals_fallback_past_text), false);
                    } else {
                        for (EHITripSummary ehiTripSummary : getViewModel().getPastTripSummaries()) {
                            mPastTripsAdapter.addPastTrip(ehiTripSummary);
                        }
                    }
                }

            }
        });

        addReaction(RESERVATION_REQUEST_REACTION, new ReactorComputationFunction() {
            @Override
            public void react(ReactorComputation reactorComputation) {
                if (getViewModel().getRequestReservation() != null) {
                    Intent intent = new ConfirmationActivityHelper.Builder()
                            .extraReservation(getViewModel().getRequestReservation())
                            .isModify(false)
                            .exitGoesHome(false)
                            .build(getActivity());
                    startActivity(intent);
                    getViewModel().setRequestedReservation(null);
                }
            }
        });

        addReaction(NOTIFICATION_REACTION, new ReactorComputationFunction() {
            @Override
            public void react(final ReactorComputation reactorComputation) {
                if (getViewModel().shouldScheduleReturnNotifications()) {
                    setUpNotifications(getViewModel().getCurrentRentals());
                }

                if (getViewModel().shouldSchedulePickupNotifications()) {
                    setUpNotifications(getViewModel().getUpcomingRentals());
                }
            }
        });
    }

    private void setUpNotifications(List<EHITripSummary> trips) {
        if (ListUtils.isEmpty(trips)) return;
        // sending in small sizes to avoid android.os.TransactionTooLargeException
        final int size = 5;
        for (int i = 0; i < trips.size(); i += size) {
            try {
                getActivity().startService(
                        new NotificationSchedulerServiceHelper.Builder()
                                .upcomingRentals(trips.subList(i, Math.min(i + size, trips.size()) - 1))
                                .build(getActivity()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onSelectorChanged(int position) {
        getViewModel().setViewState(position);
    }

    @Override
    public void onReservationActionClicked(EHIReservation reservation) {

    }

    @Override
    public void onUpcomingRentalClicked(EHITripSummary ehiTripSummary) {
        trackViewDetailsEvent();
        getViewModel().requestReservation(ehiTripSummary);
    }

    @Override
    public void onTripClicked(EHITripSummary tripSummary) {
        Intent intent = new InvoiceActivityHelper.Builder()
                .invoiceNumber(tripSummary.getInvoiceNumber())
                .build(getActivity());
        startActivity(intent);
    }

    @Override
    public void onLoadMoreClicked() {
        if (getViewModel().getLoyaltyNumber() == null) {
            return;
        }

        getViewModel().requestLoadMoreUpcomingRentals(
                getViewModel().getLoyaltyNumber(),
                getViewModel().getEndDate()
        );
    }

    @Override
    public void onLookUpRentalClicked() {
        trackLookupEvent();
        showModal(getActivity(), new LookupRentalFragmentHelper.Builder().build());
    }

    @Override
    public void onMissingRentalsClicked() {
        showModalDialog(
                getActivity(),
                new DontSeeRentalModalDialogFragmentHelper.Builder().build()
        );
    }

    private void trackViewDetailsEvent() {
        String state;
        if (getViewBinding().myRentalsRecycler.getAdapter() == mUpcomingRentalsAdapter) {
            state = EHIAnalytics.State.STATE_UPCOMING_RENTALS.value;
        } else {
            state = EHIAnalytics.State.STATE_PAST_RENTALS.value;
        }
        EHIAnalyticsEvent.create()
                .screen(EHIAnalytics.Screen.SCREEN_MY_RENTALS.value, MyRentalsFragment.TAG)
                .state(state)
                .action(EHIAnalytics.Motion.MOTION_TAP.value, EHIAnalytics.Action.ACTION_VIEW_RECEIPT.value)
                .addDictionary(EHIAnalyticsDictionaryUtils.signIn())
                .tagScreen()
                .tagEvent();
    }

    private void trackLookupEvent() {
        String state;
        if (getViewBinding().myRentalsRecycler.getAdapter() == mUpcomingRentalsAdapter) {
            state = EHIAnalytics.State.STATE_UPCOMING_RENTALS.value;
        } else {
            state = EHIAnalytics.State.STATE_PAST_RENTALS.value;
        }
        EHIAnalyticsEvent.create()
                .screen(EHIAnalytics.Screen.SCREEN_MY_RENTALS.value, MyRentalsFragment.TAG)
                .state(state)
                .action(EHIAnalytics.Motion.MOTION_TAP.value, EHIAnalytics.Action.ACTION_LOOKUP.value)
                .addDictionary(EHIAnalyticsDictionaryUtils.signIn())
                .tagScreen()
                .tagEvent();
    }


}
