package com.ehi.enterprise.android.ui.reservation;

import android.animation.Animator;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Html;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.databinding.CarClassListFragmentBinding;
import com.ehi.enterprise.android.models.profile.EHILoyaltyData;
import com.ehi.enterprise.android.models.reservation.EHICarClassDetails;
import com.ehi.enterprise.android.models.reservation.EHIReservation;
import com.ehi.enterprise.android.ui.activity.ModalActivityHelper;
import com.ehi.enterprise.android.ui.fragment.DataBindingViewModelFragment;
import com.ehi.enterprise.android.ui.reservation.interfaces.ReservationFlowListener;
import com.ehi.enterprise.android.utils.DialogUtils;
import com.ehi.enterprise.android.utils.DisplayUtils;
import com.ehi.enterprise.android.utils.FragmentUtils;
import com.ehi.enterprise.android.utils.IntentUtils;
import com.ehi.enterprise.android.utils.analytics.EHIAnalytics;
import com.ehi.enterprise.android.utils.analytics.EHIAnalyticsDictionaryUtils;
import com.ehi.enterprise.android.utils.analytics.EHIAnalyticsEvent;
import com.ehi.enterprise.android.utils.exceptions.NotImplementedException;
import com.isobar.android.newinstancer.Extra;
import com.isobar.android.viewmodel.ViewModel;

import java.util.ArrayList;
import java.util.List;

import io.dwak.reactor.ReactorComputation;
import io.dwak.reactor.interfaces.ReactorComputationFunction;

@ViewModel(CarClassListViewModel.class)
public class CarClassListFragment extends DataBindingViewModelFragment<CarClassListViewModel, CarClassListFragmentBinding>
        implements ReservationFlowListener.AnimatingViewCallback {

    public static final String SCREEN_NAME = "CarClassListFragment";

    public static final String TAG = CarClassListFragment.class.getSimpleName();

    @Extra(boolean.class)
    public static final String IS_MODIFY = "ehi.EXTRA_IS_MODIFY";

    public static final String RESERVATION_REACTION = "RESERVATION_REACTION";
    public static final String CLASS_DETAIL_REACTION = "CLASS_DETAIL_REACTION";

    public static final int FILTER_REQUEST_CODE = 329;

    private static final long SLIDE_OFF_ANIMATION_DURATION = 300;
    private static final long SLIDE_UP_ANIMATION_DURATION = 500;
    private static final String SHOW_POINTS_REACTION = "SHOW_POINTS_REACTION";
    private static final String LOAD_EXTRAS_REACTION = "LOAD_EXTRAS_REACTION";

    private CarClassListAdapter mReservationClassSelectAdapter;

    private CarClassListAdapter.CarClassListAdapterListener mCarClassListAdapterListener = new CarClassListAdapter.CarClassListAdapterListener() {
        @Override
        public void onTotalCostClicked(EHICarClassDetails carClassDetails, int position) {
            if (carClassDetails.getStatus().equals(EHICarClassDetails.ON_REQUEST)
                    || carClassDetails.getStatus().equals(EHICarClassDetails.ON_REQUEST_AT_CONTRACT_RATE)
                    || carClassDetails.getStatus().equals(EHICarClassDetails.ON_REQUEST_AT_PROMOTIONAL_RATE)) {
                onRequestDialog(carClassDetails, position);
            } else if (carClassDetails.shouldShowCallForAvailability()) {
                if (!TextUtils.isEmpty(carClassDetails.getTruckUrl())) {
                    linkOutForTrucksDialog(carClassDetails.getTruckUrl());
                } else if (carClassDetails.getCallForAvailabilityPhoneNumber() != null) {
                    callLocation(carClassDetails);
                    EHIAnalyticsEvent.create()
                            .screen(EHIAnalytics.Screen.SCREEN_CLASS.value, CarClassListFragment.SCREEN_NAME)
                            .state(EHIAnalytics.State.STATE_LIST_CURRENCY.value)
                            .action(EHIAnalytics.Motion.MOTION_TAP.value, EHIAnalytics.Action.ACTION_CALL_FOR_AVAILABILITY.value)
                            .addDictionary(EHIAnalyticsDictionaryUtils.reservation())
                            .tagScreen()
                            .tagEvent();
                }
            } else if (carClassDetails.isTermsAndConditionsRequired()) {
                showTermsAndConditions(true, carClassDetails, position);
            } else {
                getViewModel().addOrUpdateSelectedCarClass(carClassDetails);
                detailsAnimation(carClassDetails, position);
                EHIAnalyticsEvent.create()
                        .screen(EHIAnalytics.Screen.SCREEN_CLASS.value, CarClassListFragment.SCREEN_NAME)
                        .state(EHIAnalytics.State.STATE_LIST_CURRENCY.value)
                        .action(EHIAnalytics.Motion.MOTION_TAP.value, EHIAnalytics.Action.ACTION_SELECT_CLASS.value)
                        .addDictionary(EHIAnalyticsDictionaryUtils.reservation())
                        .macroEvent(EHIAnalytics.MacroEvent.MACRO_CLASS_SELECTED.value)
                        .tagScreen()
                        .tagEvent()
                        .tagMacroEvent();
            }
        }

        @Override
        public void onClassImageClicked(EHICarClassDetails carClassDetails, int position) {
            if (carClassDetails.getStatus().equals(EHICarClassDetails.ON_REQUEST)
                    || carClassDetails.getStatus().equals(EHICarClassDetails.ON_REQUEST_AT_CONTRACT_RATE)
                    || carClassDetails.getStatus().equals(EHICarClassDetails.ON_REQUEST_AT_PROMOTIONAL_RATE)) {
                onRequestDialog(carClassDetails, position);
            } else if (carClassDetails.shouldShowCallForAvailability()) {
                if (!TextUtils.isEmpty(carClassDetails.getTruckUrl())) {
                    linkOutForTrucksDialog(carClassDetails.getTruckUrl());
                } else if (carClassDetails.getCallForAvailabilityPhoneNumber() != null) {
                    callLocation(carClassDetails);
                }
            } else if (carClassDetails.isTermsAndConditionsRequired()) {
                showTermsAndConditions(true, carClassDetails, position);
            } else {
                getViewModel().addOrUpdateSelectedCarClass(carClassDetails);
                detailsAnimation(carClassDetails, position);
            }

            EHIAnalyticsEvent.create()
                    .screen(EHIAnalytics.Screen.SCREEN_CLASS.value, CarClassListFragment.SCREEN_NAME)
                    .state(EHIAnalytics.State.STATE_LIST_CURRENCY.value)
                    .action(EHIAnalytics.Motion.MOTION_TAP.value, EHIAnalytics.Action.ACTION_SELECT_CLASS.value)
                    .addDictionary(EHIAnalyticsDictionaryUtils.reservation())
                    .macroEvent(EHIAnalytics.MacroEvent.MACRO_CLASS_SELECTED.value)
                    .tagScreen()
                    .tagEvent()
                    .tagMacroEvent();
        }

        @Override
        public void onMoreDetailsClicked(EHICarClassDetails carClassDetails) {
            if (carClassDetails.getStatus().equals(EHICarClassDetails.ON_REQUEST)
                    || carClassDetails.getStatus().equals(EHICarClassDetails.ON_REQUEST_AT_CONTRACT_RATE)
                    || carClassDetails.getStatus().equals(EHICarClassDetails.ON_REQUEST_AT_PROMOTIONAL_RATE)) {
                onRequestDialog(carClassDetails, -1);
            } else if (carClassDetails.shouldShowCallForAvailability()) {
                if (!TextUtils.isEmpty(carClassDetails.getTruckUrl())) {
                    linkOutForTrucksDialog(carClassDetails.getTruckUrl());
                } else if (carClassDetails.getCallForAvailabilityPhoneNumber() != null) {
                    callLocation(carClassDetails);
                }
            } else if (carClassDetails.isTermsAndConditionsRequired()) {
                showTermsAndConditions(false, carClassDetails, -1);
            } else {
                getViewModel().addOrUpdateSelectedCarClass(carClassDetails);
                getViewModel().selectCarClass(carClassDetails);
            }
        }

        @Override
        public void onFilterClearButtonClicked() {
            mReservationClassSelectAdapter.resetFilters();
            getViewModel().commitFilters(mReservationClassSelectAdapter.getFilters());
        }

        @Override
        public void onRentalTermsConditionsClicked() {
            showModal(getActivity(), new RentalTermsConditionsFragmentHelper.Builder().build());
        }
    };

    private View.OnClickListener mOnTermsClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            showModal(
                    getActivity(),
                    new HtmlParseFragmentHelper.Builder()
                            .title(getResources().getString(R.string.weekend_special_terms_and_conditions_navigation_ti))
                            .message(getViewModel().getTermsAndConditions())
                            .build()
            );
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getViewModel().setIsModify(new CarClassListFragmentHelper.Extractor(this).isModify());
        if (!(getActivity() instanceof ReservationFlowListener)) {
            throw new NotImplementedException();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        createViewBinding(inflater, R.layout.fr_car_class_list, container);
        initViews();
        return getViewBinding().getRoot();
    }

    private void initViews() {
        getViewBinding().classesRecyclerView.setHasFixedSize(true);
        getViewBinding().classesRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        ViewCompat.setNestedScrollingEnabled(getViewBinding().classesRecyclerView, false);
        
        setHasOptionsMenu(true);
        mReservationClassSelectAdapter = new CarClassListAdapter(getActivity(), mCarClassListAdapterListener, getViewModel().getShowClassTotalCostAsterisks());

        getViewBinding().classesRecyclerView.setAdapter(mReservationClassSelectAdapter);

        if (getViewModel().isUserLoggedIn() && !getViewModel().isModify()) {
            final EHILoyaltyData ehiLoyaltyData = getViewModel().getUserProfileCollection().getBasicProfile().getLoyaltyData();
            getViewBinding().appBarLayout.setVisibility(View.VISIBLE);
            final ViewGroup.LayoutParams params = getViewBinding().appBarLayout.getLayoutParams();
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            getViewBinding().appBarLayout.setLayoutParams(params);
            getViewBinding().epointsHeaderView.setTopRightText(getString(R.string.redemption_header_points_show_label),
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String notAllowedMessage = null;
                            final EHIReservation ehiReservation = getViewModel().getReservationObject();
                            if (ehiReservation != null) {
                                if (!ehiReservation.doesLocationSupportRedemption()) {
                                    notAllowedMessage = getString(R.string.currently_dont_allow_points_unsupported);
                                }
                                if (notAllowedMessage == null) {
                                    if (getViewModel().isShowingPoints()) {
                                        EHIAnalyticsEvent.create()
                                                .screen(EHIAnalytics.Screen.SCREEN_CLASS.value, CarClassListFragment.SCREEN_NAME)
                                                .state(EHIAnalytics.State.STATE_LIST_CURRENCY.value)
                                                .action(EHIAnalytics.Motion.MOTION_TAP.value, EHIAnalytics.Action.ACTION_HIDE_POINTS.value)
                                                .addDictionary(EHIAnalyticsDictionaryUtils.reservation())
                                                .tagScreen()
                                                .tagEvent();
                                    } else {
                                        EHIAnalyticsEvent.create()
                                                .screen(EHIAnalytics.Screen.SCREEN_CLASS.value, CarClassListFragment.SCREEN_NAME)
                                                .state(EHIAnalytics.State.STATE_LIST_CURRENCY.value)
                                                .action(EHIAnalytics.Motion.MOTION_TAP.value, EHIAnalytics.Action.ACTION_SHOW_POINTS.value)
                                                .addDictionary(EHIAnalyticsDictionaryUtils.reservation())
                                                .tagScreen()
                                                .tagEvent();
                                    }
                                    getViewModel().togglePointsVisibility();
                                } else {
                                    DialogUtils.showDialogWithTitleAndText(
                                            getActivity(), notAllowedMessage, ""
                                    );
                                }
                            }
                        }
                    })
                    .setTopLeftHeaderText(getString(R.string.redemption_header_points_header))
                    .setTopLeftPointsText(ehiLoyaltyData != null ? ehiLoyaltyData.getPointsToDate() : 0);
        } else {
            getViewBinding().appBarLayout.setVisibility(View.GONE);
            final ViewGroup.LayoutParams params = getViewBinding().appBarLayout.getLayoutParams();
            params.height = 0;
            getViewBinding().appBarLayout.setLayoutParams(params);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && requestCode == FILTER_REQUEST_CODE) {
            mReservationClassSelectAdapter.setFilters(getViewModel().getCurrentFilters());
        }
    }

    @Override
    protected void initDependencies() {
        super.initDependencies();
        bind(DialogUtils.errorDialog(getViewModel().errorResponse, getActivity()));
        bind(FragmentUtils.progress(getViewModel().progress, getActivity()));
        bind(FragmentUtils.progressDefinite(getViewModel().determinateLoader, getActivity()));

        addReaction(RESERVATION_REACTION, new ReactorComputationFunction() {
            @Override
            public void react(ReactorComputation reactorComputation) {
                EHIReservation reservationObject = getViewModel().getReservationObject();
                if (reservationObject != null) {
                    getViewModel().setIsPromotionAvailableAllCarClasses(reservationObject.getCarClasses());
                    getViewModel().setIsCorporateAvailableAllCarClasses(reservationObject.getCarClasses());
                    showCarClasses(reservationObject.getCarClasses());
                }
            }
        });
        addReaction(CLASS_DETAIL_REACTION, new ReactorComputationFunction() {
            @Override
            public void react(ReactorComputation reactorComputation) {
                EHICarClassDetails carClassDetails = getViewModel().getCarClassSelectResponse();
                if (carClassDetails != null) {
                    ((ReservationFlowListener) getActivity()).showCarDetails(carClassDetails);
                    getViewModel().resetCarClassSelectResponse();
                }
            }
        });
        addReaction(SHOW_POINTS_REACTION, new ReactorComputationFunction() {
            @Override
            public void react(ReactorComputation reactorComputation) {
                if (getViewModel().isShowingPoints()) {
                    getViewBinding().epointsHeaderView.setTopRightText(getString(R.string.redemption_header_points_hide_label), null);
                    mReservationClassSelectAdapter.showPoints(true, getViewModel().showPointsFromToggle());
                } else {
                    getViewBinding().epointsHeaderView.setTopRightText(getString(R.string.redemption_header_points_show_label), null);
                    mReservationClassSelectAdapter.showPoints(false, getViewModel().showPointsFromToggle());
                }
            }
        });
        addReaction(LOAD_EXTRAS_REACTION, new ReactorComputationFunction() {
            @Override
            public void react(ReactorComputation reactorComputation) {
                EHIReservation extrasReservation = getViewModel().getExtrasReservation();
                EHICarClassDetails chosenCar = getViewModel().getChosenCar();
                if (extrasReservation != null
                        && chosenCar != null) {
                    if (getViewModel().isModify()) {
                        //this is an edge case when changing from prepay location to pay later location
                        //reservation still have prepay as true but all car classes don't have prepay rates
                        //in this case we will change pay state during car class selection so we need update it
                        //in activity as well
                        if (extrasReservation.isPrepaySelected()) {
                            ((ReservationFlowListener) getActivity()).setPayState(ReservationFlowListener.PayState.PREPAY);
                        } else {
                            ((ReservationFlowListener) getActivity()).setPayState(ReservationFlowListener.PayState.PAY_LATER);
                        }
                    }
                    if (((ReservationFlowListener) getActivity()).needShowRateScreen(extrasReservation.getCarClassDetails(), getViewModel().getCorporateContractType())) {
                        ((ReservationFlowListener) getActivity()).showChooseYourRateScreen(chosenCar, false);
                        ((ReservationFlowListener) getActivity()).carListAnimationInProgress(false, CarClassListFragment.this);
                        getViewModel().setExtrasReservation(null);
                    } else {
                        getViewBinding().bottomSpinnerArea.animate()
                                .alpha(0)
                                .setDuration(200)
                                .setListener(new Animator.AnimatorListener() {
                                    @Override
                                    public void onAnimationStart(Animator animator) {
                                    }

                                    @Override
                                    public void onAnimationEnd(Animator animator) {
                                        final ReservationFlowListener reservationFlowListener = (ReservationFlowListener) getActivity();

                                        if (reservationFlowListener == null) {
                                            return;
                                        }
                                        reservationFlowListener.showCarExtras(
                                                getViewModel().getChosenCar(),
                                                false,
                                                reservationFlowListener.getPayState(),
                                                false
                                        );

                                        reservationFlowListener.carListAnimationInProgress(false, CarClassListFragment.this);

                                        getViewModel().setExtrasReservation(null);
                                    }

                                    @Override
                                    public void onAnimationCancel(Animator animator) {
                                    }

                                    @Override
                                    public void onAnimationRepeat(Animator animator) {
                                    }
                                });
                    }
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_filter) {
            Fragment fragment = new CarFilterFragmentHelper.Builder()
                    .carFilterKeyDetails(mReservationClassSelectAdapter.getUnfilteredCarClasses())
                    .build();

            Intent intent = new ModalActivityHelper.Builder()
                    .fragmentClass(fragment.getClass())
                    .fragmentArguments(fragment.getArguments())
                    .build(getActivity());

            startActivityForResult(
                    intent,
                    FILTER_REQUEST_CODE
            );
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        final MenuItem cancel = menu.findItem(R.id.action_cancel);
        if (cancel != null) {
            cancel.setVisible(false);
        }
        inflater.inflate(R.menu.car_search_menu, menu);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getViewModel().needToShowDNRDialog()) {
            showModalDialog(getActivity(), new ReservationDNRDialogFragmentHelper.Builder().build());
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getViewModel().isModify()) {
            getActivity().setTitle(R.string.reservation_modify_class_select_navigation_title);
        } else {
            getActivity().setTitle(R.string.reservation_class_select_navigation_title);
        }

//		if (mListener.getAnimationData().selectedViewPosition != -1) {
//			reverseDetailsAnimation();
//		}
        getViewModel().populateReservationObject();
    }

    @Override
    public void onResume() {
        super.onResume();
        EHIAnalyticsEvent.create()
                .screen(EHIAnalytics.Screen.SCREEN_CLASS.value, CarClassListFragment.SCREEN_NAME)
                .state(EHIAnalytics.State.STATE_LIST_CURRENCY.value)
                .addDictionary(EHIAnalyticsDictionaryUtils.reservation())
                .macroEvent(EHIAnalytics.MacroEvent.MACRO_CLASS_LOADED.value)
                .tagScreen()
                .tagEvent()
                .tagMacroEvent();
    }

    private void showCarClasses(List<EHICarClassDetails> ehiCarClasses) {
        mReservationClassSelectAdapter.clear();
        if (!ehiCarClasses.isEmpty()) {
            mReservationClassSelectAdapter.setFilters(getViewModel().getCurrentFilters());
            mReservationClassSelectAdapter.setCIDInfo(
                    getViewModel().getCorporateAccountName()
            );

            mReservationClassSelectAdapter.setPayState(getViewModel().getDefaultPayState(getViewModel().isModify()));

            mReservationClassSelectAdapter.setContractType(getViewModel().getCorporateContractType());
            mReservationClassSelectAdapter.setPromotionAvailability(getViewModel().isAvailableAtPromotionRate());
            mReservationClassSelectAdapter.setContractAvailability(getViewModel().isAvailableAtContractRate());
            if (getViewModel().getTermsAndConditions() != null) {
                mReservationClassSelectAdapter.setTermsAndConditionsListener(mOnTermsClickListener);
            }

            mReservationClassSelectAdapter.addCarClasses(ehiCarClasses, getViewModel().getReservationObject().shouldMoveVansToEndOfList());

            if (getViewModel().shouldShowCurrencyBanner()) {
                // we need the currency info inside any car class we have
                mReservationClassSelectAdapter.setUpAnotherCurrencyText(getViewModel().getCharge(ehiCarClasses.get(0)));
            }

            mReservationClassSelectAdapter.addHeader();
            mReservationClassSelectAdapter.addFooter();

            if (getViewModel().isLoggedIntoEmeraldClub()
                    && getViewModel().getReservationObject().getCorporateAccount() == null) {
                mReservationClassSelectAdapter.addInfoMessage(getString(R.string.reservation_review_emerald_club_active_message));
            }
        }
    }

    private void callLocation(EHICarClassDetails carClassDetails) {
        final EHICarClassDetails classDetails = carClassDetails;
        new AlertDialog.Builder(getActivity())
                .setTitle(R.string.reservation_call_for_availability_prompt_call_title)
                .setMessage(R.string.reservation_call_for_availability_prompt_call_message)
                .setPositiveButton(R.string.alert_okay_title, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String cfaPhone = classDetails.getCallForAvailabilityPhoneNumber();
                        if (TextUtils.isEmpty(cfaPhone)) {
                            cfaPhone = getViewModel().getReservationObject().getPickupLocation().getPrimaryPhoneNumber();
                        }
                        IntentUtils.callNumber(getActivity(), cfaPhone);
                    }
                })
                .setNegativeButton(R.string.alert_cancel_title, null)
                .create()
                .show();
    }

//    private void reverseDetailsAnimation() {
//        final DisplayMetrics metrics = new DisplayMetrics();
//        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
//        int containerHeight = metrics.heightPixels;
//        int containerWidth = metrics.widthPixels;
//
//        getViewBinding().carSelectTransitionView.setVisibility(View.VISIBLE);
//        getViewBinding().classesRecyclerView.setVisibility(View.GONE);
//
//        View selectedView = null;
//
//        final AnimationDataHolder holder = ((ReservationFlowListener) getActivity()).getAnimationData();
//        final int size = holder.viewHeightPositionY.size() + holder.startPosition;
//        for (int i = holder.startPosition; i < size; i++) {
//            final View view = mReservationClassSelectAdapter.getViewHolder(
//                    i,
//                    getViewBinding().carSelectTransitionView,
//                    true
//            ).itemView;
//            getViewBinding().carSelectTransitionView.addView(view);
//            if (i == holder.selectedViewPosition) {
//                selectedView = view;
//            }
//        }
//
//        boolean gravitateUp = true;
//        float movePositionTo;
//
//        final int childCount = getViewBinding().carSelectTransitionView.getChildCount();
//        for (int a = 0; a < childCount; a++) {
//            final View view = getViewBinding().carSelectTransitionView.getChildAt(a);
//
//            if (!view.equals(selectedView)) {
//                //we set positions of views to where they would've animated to
//                // and now we actually animate in reverse
//                movePositionTo = (gravitateUp) ?
//                        (-1 * (10 + holder.viewHeightPositionY.get(a).first)) :
//                        (containerHeight + 10);
//                view.setY(movePositionTo);
//
//                view.animate()
//                        .setStartDelay(SLIDE_UP_ANIMATION_DURATION)
//                        .setDuration(SLIDE_OFF_ANIMATION_DURATION)
//                        .y(holder.viewHeightPositionY.get(a).second)
//                        .setInterpolator(new AccelerateDecelerateInterpolator());
//                //we add a listener for after the animation is done, we don't want to add
//                //several listeners so i just use a boolean to check if i have on already
//                if (mFirstAnimation) {
//                    mFirstAnimation = false;
//                    view.animate().setListener(new Animator.AnimatorListener() {
//                        @Override
//                        public void onAnimationStart(Animator animator) {
//                        }
//
//                        @Override
//                        public void onAnimationEnd(Animator animator) {
//                            getViewBinding().carSelectTransitionView.setVisibility(View.GONE);
//                            getViewBinding().classesRecyclerView.setVisibility(View.VISIBLE);
//                            getViewBinding().carSelectTransitionView.removeAllViews();
//                            getViewBinding().classesRecyclerView.setAdapter(mReservationClassSelectAdapter);
//                        }
//
//                        @Override
//                        public void onAnimationCancel(Animator animator) {
//                        }
//
//                        @Override
//                        public void onAnimationRepeat(Animator animator) {
//                        }
//                    });
//                }
//            } else {
//                gravitateUp = false;
//                view.animate()
//                        .setDuration(SLIDE_UP_ANIMATION_DURATION)
//                        .y(holder.viewHeightPositionY.get(a).second)
//                        .setInterpolator(new AccelerateDecelerateInterpolator());
//
//                View viewMoreDetails = view.findViewById(R.id.class_details_container);
//                viewMoreDetails.setX(containerWidth + 30);
//                viewMoreDetails.animate()
//                        .translationXBy(-1 * (containerWidth + 30))
//                        .setInterpolator(new AccelerateDecelerateInterpolator())
//                        .setDuration(SLIDE_UP_ANIMATION_DURATION);
//
//
//                View viewDescription = view.findViewById(R.id.class_description_container);
//                viewDescription.setX(-1 * (holder.detailsViewWidth + 30));
//                viewDescription.animate()
//                        .translationXBy(holder.detailsViewWidth + 30)
//                        .setInterpolator(new AccelerateDecelerateInterpolator())
//                        .setDuration(SLIDE_UP_ANIMATION_DURATION);
//            }
//        }
//    }

    @Override
    public void backPressed() {
        ((ReservationFlowListener) getActivity()).carListAnimationInProgress(false, this);
        resetAnimation();
    }

    private void detailsAnimation(final EHICarClassDetails carClasses, final int position) {
        final int phoneScreenHeight = DisplayUtils.getScreenHeight(getActivity());

        ((ReservationFlowListener) getActivity()).carListAnimationInProgress(true, this);
        getViewModel().populateExtras(carClasses);

        // Height, PositionY
        final ArrayList<Pair<Integer, Float>> viewHeights = new ArrayList<>();

        View selectedView = null;
        int startingPosition = 0;
        int descriptionWidth = 0;
        float positionYOfDetailsView = 0;
        int heightOfSelectedView = 0;

        // We grab the views from the recycler view and copy them over
        int childCount = getViewBinding().classesRecyclerView.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View view = getViewBinding().classesRecyclerView.getChildAt(i);
            final int positionOfChild = getViewBinding().classesRecyclerView.getChildLayoutPosition(view);

            if (i == 0) {
                startingPosition = positionOfChild;
            }

            viewHeights.add(new Pair<>(view.getHeight(), view.getY()));

            final View copyView = mReservationClassSelectAdapter.getViewHolder(
                    positionOfChild,
                    getViewBinding().carSelectTransitionView,
                    true
            ).itemView;

            final ViewGroup.LayoutParams params = copyView.getLayoutParams();

            if (position == positionOfChild) {
                final View detailsView = view.findViewById(R.id.class_details_container);
                final View pointsView = view.findViewById(R.id.points_container);

                params.height = view.getHeight() - detailsView.getHeight() - pointsView.getHeight();
                positionYOfDetailsView = detailsView.getY() + view.getY() + pointsView.getY();

            } else {
                params.height = view.getHeight();
            }

            copyView.setLayoutParams(params);

            if (positionOfChild == position) {
                descriptionWidth = view.findViewById(R.id.class_description_container).getWidth();
                selectedView = copyView;
                heightOfSelectedView = view.getHeight();
            }
            copyView.setY(view.getY());
            getViewBinding().carSelectTransitionView.addView(copyView);
        }

        final int finalDescriptionWidth = descriptionWidth;
        final int finalStartingPosition = startingPosition;

        getViewBinding().carSelectTransitionView.setVisibility(View.VISIBLE);

        float appBarAdjustment = getViewBinding().appBarLayout.getHeight() + getViewBinding().appBarLayout.getY();
        if (appBarAdjustment > 0 && getViewBinding().appBarLayout.getVisibility() == View.VISIBLE) {
            getViewBinding().carSelectTransitionView.setTranslationY(appBarAdjustment);
        }

        getViewBinding().classesRecyclerView.setVisibility(View.GONE);
        getViewBinding().appBarLayout.animate()
                .setStartDelay(SLIDE_OFF_ANIMATION_DURATION)
                .translationYBy((-getViewBinding().appBarLayout.getHeight() - phoneScreenHeight) / 2)
                .setDuration(SLIDE_UP_ANIMATION_DURATION);

        if (getViewBinding().appBarLayout.getVisibility() == View.VISIBLE) {
            getViewBinding().carSelectTransitionView
                    .animate()
                    .setStartDelay(SLIDE_OFF_ANIMATION_DURATION)
                    .setDuration(SLIDE_UP_ANIMATION_DURATION)
                    .translationYBy(-appBarAdjustment);
        }

        final int finalHeightOfSelectedView = heightOfSelectedView;
        final int screenHeightFinal = phoneScreenHeight;

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                getViewBinding().bottomSpinnerText.setVisibility(!getViewModel().isUserLoggedIn() && !carClasses.isPrepayRateAvailable() ? View.VISIBLE : View.GONE);
                getViewBinding().bottomSpinnerArea.setVisibility(View.VISIBLE);
                ((AnimationDrawable) getViewBinding().bottomSpinner.getDrawable()).start();
                getViewBinding().bottomSpinnerArea.getLayoutParams().height = (screenHeightFinal - (int) getResources().getDimension(R.dimen.toolbar_height)) - finalHeightOfSelectedView;
                getViewBinding().bottomSpinner.requestLayout();
            }
        }, SLIDE_OFF_ANIMATION_DURATION + SLIDE_UP_ANIMATION_DURATION);

        final DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);

        final int screenHeight = DisplayUtils.getScreenHeight(getActivity());
        boolean gravitateUp = true;
        float animatePosition;

        childCount = getViewBinding().carSelectTransitionView.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View view = getViewBinding().carSelectTransitionView.getChildAt(i);

            View previouslySelected = view.findViewById(R.id.previously_selected_car_class);
            ((ViewGroup) view).removeView(previouslySelected);

            if (!view.equals(selectedView)) {
                //gravitateUp to animate either up or down depending on if we're before/after the selected view
                //childMultiple to determine what multiple of the child's height are we traversing in either direction to maintain a linear slide on screens with several car items

                animatePosition = (gravitateUp ? -1 : 1) * screenHeight;
                view.animate()
                        .translationYBy(animatePosition)
                        .setInterpolator(new AccelerateInterpolator())
                        .setDuration(SLIDE_OFF_ANIMATION_DURATION);
            } else {
                //if we're on selected item we're switching gravity and then setting delayed animation
                //for selected animation
                gravitateUp = false;
                View viewMoreDetails = view.findViewById(R.id.class_details_container);

                View priceContainer = view.findViewById(R.id.big_price_container);
                priceContainer.animate().alpha(0).setDuration(SLIDE_OFF_ANIMATION_DURATION);

                View pointsContainer = view.findViewById(R.id.points_container);

                animatePosition = screenHeight;
                view.animate()
                        .setStartDelay(SLIDE_OFF_ANIMATION_DURATION)
                        .translationY(0)
                        .setInterpolator(new AccelerateDecelerateInterpolator())
                        .setDuration(SLIDE_UP_ANIMATION_DURATION)
                        .setListener(new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animation) {
                            }

                            @Override
                            public void onAnimationEnd(Animator animation) {
                                //save data for reverse animation
                                ((ReservationFlowListener) getActivity()).setAnimationData(new AnimationDataHolder(
                                        viewHeights,
                                        position,
                                        finalDescriptionWidth,
                                        finalStartingPosition));

                                getViewModel().setChosenCar(carClasses);
                            }

                            @Override
                            public void onAnimationCancel(Animator animation) {

                            }

                            @Override
                            public void onAnimationRepeat(Animator animation) {

                            }
                        });

                ((ViewGroup) view).removeView(viewMoreDetails);
                getViewBinding().carSelectTransitionView.addView(viewMoreDetails);

                ((ViewGroup) view).removeView(pointsContainer);
                getViewBinding().carSelectTransitionView.addView(pointsContainer);

                viewMoreDetails.setY(positionYOfDetailsView);
                viewMoreDetails.animate().translationYBy(animatePosition)
                        .setDuration(SLIDE_OFF_ANIMATION_DURATION);

                pointsContainer.setY(positionYOfDetailsView);
                pointsContainer.animate().translationYBy(animatePosition)
                        .setDuration(SLIDE_OFF_ANIMATION_DURATION);
            }
        }
    }

    private void onRequestDialog(final EHICarClassDetails carClassDetails, final int position) {
        new AlertDialog.Builder(getActivity())
                .setMessage(getString(R.string.on_request_status_message_dialog_title))
                .setPositiveButton(getString(R.string.standard_ok_text), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (position != -1) {
                            detailsAnimation(carClassDetails, position);
                            EHIAnalyticsEvent.create()
                                    .screen(EHIAnalytics.Screen.SCREEN_CLASS.value, CarClassListFragment.SCREEN_NAME)
                                    .state(EHIAnalytics.State.STATE_LIST_CURRENCY.value)
                                    .action(EHIAnalytics.Motion.MOTION_TAP.value, EHIAnalytics.Action.ACTION_SELECT_CLASS.value)
                                    .addDictionary(EHIAnalyticsDictionaryUtils.reservation())
                                    .macroEvent(EHIAnalytics.MacroEvent.MACRO_CLASS_SELECTED.value)
                                    .tagScreen()
                                    .tagEvent()
                                    .tagMacroEvent();
                        } else {
                            getViewModel().addOrUpdateSelectedCarClass(carClassDetails);
                            getViewModel().selectCarClass(carClassDetails);
                        }
                    }
                })
                .setNegativeButton(getString(R.string.alert_cancel_title), null)
                .setCancelable(true)
                .create()
                .show();
    }

    private void linkOutForTrucksDialog(final String truckUrl) {
        new AlertDialog.Builder(getActivity())
                .setMessage(getString(R.string.alert_open_browser_text))
                .setPositiveButton(getString(R.string.standard_ok_text), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        IntentUtils.openUrlViaExternalApp(getActivity(), truckUrl);
                    }
                })
                .setNegativeButton(getString(R.string.alert_cancel_title), null)
                .setCancelable(true)
                .create()
                .show();
    }

    public void showTermsAndConditions(final boolean showAnimation, final EHICarClassDetails carClassDetails, final int position) {
        new AlertDialog.Builder(getActivity())
                .setTitle(R.string.reservation_policy_terms_and_conditions_title)
                .setMessage(Html.fromHtml(carClassDetails.getDescription()))
                .setPositiveButton(R.string.terms_and_conditions_accept_title, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (showAnimation) {
                            detailsAnimation(carClassDetails, position);
                        } else {
                            getViewModel().addOrUpdateSelectedCarClass(carClassDetails);
                            getViewModel().selectCarClass(carClassDetails);
                        }
                    }
                })
                .setNegativeButton(R.string.cancel_button_title_key, null)
                .setCancelable(true)
                .create()
                .show();
    }

    public void resetAnimation() {
        getViewModel().animationReset();
        getViewBinding().bottomSpinnerArea.setVisibility(View.GONE);
        getViewBinding().carSelectTransitionView.setVisibility(View.GONE);
        getViewBinding().carSelectTransitionView.removeAllViews();
        getViewBinding().classesRecyclerView.setVisibility(View.VISIBLE);
    }

    //Class used in order to track the animation positions of the options animation in order to reverse
    public static class AnimationDataHolder {

        //height, position y
        public ArrayList<Pair<Integer, Float>> viewHeightPositionY = new ArrayList<>();
        public int selectedViewPosition = -1;
        public int detailsViewWidth = -1;
        public int startPosition = -1;

        public AnimationDataHolder(ArrayList<Pair<Integer, Float>> viewHeightPositionY, int selectedViewPosition, int detailsViewWidth, int startPosition) {
            this.viewHeightPositionY = viewHeightPositionY;
            this.selectedViewPosition = selectedViewPosition;
            this.detailsViewWidth = detailsViewWidth;
            this.startPosition = startPosition;
        }

        public AnimationDataHolder() {

        }
    }

}