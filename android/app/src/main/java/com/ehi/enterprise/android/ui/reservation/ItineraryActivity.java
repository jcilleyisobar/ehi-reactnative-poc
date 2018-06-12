package com.ehi.enterprise.android.ui.reservation;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.MenuItem;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.databinding.ToolbarActivityBinding;
import com.ehi.enterprise.android.models.location.solr.EHISolrLocation;
import com.ehi.enterprise.android.models.reservation.EHIAirlineDetails;
import com.ehi.enterprise.android.models.reservation.EHICarClassDetails;
import com.ehi.enterprise.android.models.reservation.EHIDriverInfo;
import com.ehi.enterprise.android.models.reservation.ReservationInformation;
import com.ehi.enterprise.android.ui.fragment.DataBindingViewModelActivity;
import com.ehi.enterprise.android.ui.location.SearchLocationsActivity;
import com.ehi.enterprise.android.ui.reservation.interfaces.BackButtonBlockListener;
import com.ehi.enterprise.android.ui.reservation.interfaces.ReservationFlowListener;
import com.ehi.enterprise.android.ui.reservation.redemption.RedemptionFragment;
import com.ehi.enterprise.android.ui.reservation.redemption.RedemptionFragmentHelper;
import com.ehi.enterprise.android.utils.DLog;
import com.ehi.enterprise.android.utils.DialogUtils;
import com.ehi.enterprise.android.utils.EHIBundle;
import com.ehi.enterprise.android.utils.FragmentUtils;
import com.ehi.enterprise.android.utils.IntentUtils;
import com.ehi.enterprise.android.utils.analytics.EHIAnalytics;
import com.ehi.enterprise.android.utils.analytics.EHIAnalyticsDictionaryUtils;
import com.ehi.enterprise.android.utils.analytics.EHIAnalyticsEvent;
import com.ehi.enterprise.android.utils.exceptions.NoArgumentsFoundException;
import com.isobar.android.newinstancer.Extra;
import com.isobar.android.viewmodel.ViewModel;

import java.util.Date;
import java.util.List;

@ViewModel(ItineraryActivityViewModel.class)
public class ItineraryActivity extends DataBindingViewModelActivity<ItineraryActivityViewModel, ToolbarActivityBinding>
        implements ReservationFlowListener, BackButtonBlockListener {

    public static final String TAG = "ItineraryActivity";
    public static final String SCREEN_NAME = "ItineraryActivity";
    public static final int DATE_RANGE_REQUEST_CODE = 1;

    //region extras
    @Extra(value = Integer.class, required = false)
    public static final String EXTRA_FLOW = "ehi.EXTRA_FLOW";
    @Extra(value = EHISolrLocation.class, required = false)
    public static final String EXTRA_PICKUP_LOCATION = "ehi.EXTRA_PICKUP_LOCATION";
    @Extra(value = EHISolrLocation.class, required = false)
    public static final String EXTRA_RETURN_LOCATION = "ehi.EXTRA_RETURN_LOCATION";
    @Extra(value = Date.class, required = false)
    public static final String EXTRA_PICKUP_DATE = "ehi.EXTRA_PICKUP_DATE";
    @Extra(value = Date.class, required = false)
    public static final String EXTRA_DROPOFF_DATE = "ehi.EXTRA_DROPOFF_DATE";
    @Extra(value = Date.class, required = false)
    public static final String EXTRA_PICKUP_TIME = "ehi.EXTRA_PICKUP_TIME";
    @Extra(value = Date.class, required = false)
    public static final String EXTRA_DROPOFF_TIME = "ehi.EXTRA_DROPOFF_TIME";
    @Extra(value = ReservationInformation.class, required = false)
    public static final String ABANDONED_RESERVTION_INFORMATION = "ehi.EXTRA_HOLDER_ABANDONED";
    //endregion

    public static final String ITINERARY_TAG = "ITINERARY_FRAGMENT_TAG";
    public static final String SHOWING_MODAL = "SHOWING_MODAL";
    public static final String PAY_STATE = "PAY_STATE";

    private CarClassListFragment.AnimationDataHolder mAnimationDataHolder = new CarClassListFragment.AnimationDataHolder();
    private Menu mMenu;

    private boolean mShowingModal;
    private PayState mPayState = PayState.PAY_LATER;
    private boolean mIsLoginAfterStart;

    //region lifecycle

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        updateReservationState(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setDataBindingContentView(R.layout.ac_toolbar_activity);
        initViews();

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(SHOWING_MODAL)) {
                mShowingModal = false; //reset this flag
            }

            if (savedInstanceState.containsKey(PAY_STATE)) {
                mPayState = (PayState) savedInstanceState.getSerializable(PAY_STATE);
            }
        } else if (getIntent().getExtras() != null) {
            final ItineraryActivityHelper.Extractor extractor = new ItineraryActivityHelper.Extractor(ItineraryActivity.this);
            if (extractor.abandonedReservtionInformation() != null) {
                commitFragments(extractor.abandonedReservtionInformation());
            } else if (extractor.extraPickupLocation() != null) {
                commitFragments(extractor);
            }
        } else {
            DLog.e(TAG, new NoArgumentsFoundException());
            finish();
            return;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(SHOWING_MODAL, mShowingModal);
        outState.putSerializable(PAY_STATE, mPayState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_cancel, menu);
        mMenu = menu;
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_cancel:
                EHIAnalyticsEvent.create()
                        .screen(EHIAnalytics.Screen.SCREEN_RESERVATION.value, ItineraryActivity.SCREEN_NAME)
                        .state(EHIAnalytics.State.STATE_ABANDON.value)
                        .addDictionary(EHIAnalyticsDictionaryUtils.reservation())
                        .tagScreen()
                        .tagEvent();
                showCancelDialog();
                return true;
        }
        return super.onOptionsItemSelected(item);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!getViewModel().shouldSaveDriverInfo()) {
            getViewModel().deleteDriverInfo();
        }
        getViewModel().resetModifyState();
    }

    @Override
    public void onBackPressed() {
        if (getViewModel().isBackButtonBlocked()) {
            return;
        }

        Fragment confirmationAnimationFragment = getSupportFragmentManager().findFragmentByTag(ReviewFragment.ANIMATION_TAG);
        if (confirmationAnimationFragment != null && confirmationAnimationFragment.isVisible()) {
            super.onBackPressed();
            new FragmentUtils.Transaction(getSupportFragmentManager(), FragmentUtils.REMOVE)
                    .fragment(confirmationAnimationFragment)
                    .commit();
            overridePendingTransition(R.anim.modal_stay, R.anim.modal_slide_out);
            return;
        }
        if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
            EHIAnalyticsEvent.create()
                    .screen(EHIAnalytics.Screen.SCREEN_RESERVATION.value, ItineraryActivity.SCREEN_NAME)
                    .state(EHIAnalytics.State.STATE_ABANDON.value)
                    .addDictionary(EHIAnalyticsDictionaryUtils.reservation())
                    .tagScreen()
                    .tagEvent();
            showCancelDialog();
        } else {
            super.onBackPressed();
        }
    }

    //endregion

    private void showCancelDialog(){
        DialogUtils.showDiscardReservationDialog(this, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                EHIAnalyticsEvent.create()
                        .screen(EHIAnalytics.Screen.SCREEN_RESERVATION.value, ItineraryActivity.SCREEN_NAME)
                        .state(EHIAnalytics.State.STATE_ABANDON.value)
                        .action(EHIAnalytics.Motion.MOTION_TAP.value, EHIAnalytics.Action.ACTION_YES.value)
                        .addDictionary(EHIAnalyticsDictionaryUtils.reservation())
                        .tagScreen()
                        .tagEvent();

                // clean any promotions applied
                getViewModel().getManagers().getReservationManager().setWeekendSpecial(false);

                overridePendingTransition(R.anim.modal_stay, R.anim.modal_slide_out);
                IntentUtils.goToHomeScreen(ItineraryActivity.this);
            }
        }, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
    }

    private void commitFragments(ItineraryActivityHelper.Extractor extractor) {
        resetToolbar();
        new FragmentUtils.Transaction(getSupportFragmentManager(), FragmentUtils.REPLACE)
                .fragment(new ItineraryFragmentHelper.Builder()
                                .extraPickupLocation(extractor.extraPickupLocation())
                                .extraPickupDate(extractor.extraPickupDate())
                                .extraPickupTime(extractor.extraPickupTime())
                                .extraReturnDate(extractor.extraDropoffDate())
                                .extraReturnTime(extractor.extraDropoffTime())
                                .isModify(false)
                                .build(),
                        ITINERARY_TAG)
                .into(R.id.ac_single_fragment_container)
                .commit();
    }

    private void commitFragments(ReservationInformation reservation) {
        new FragmentUtils.Transaction(getSupportFragmentManager(), FragmentUtils.ADD)
                .fragment(new ItineraryFragmentHelper.Builder()
                                .isModify(false)
                                .extraAbandonedHolder(reservation)
                                .build(),
                        ITINERARY_TAG)
                .into(R.id.ac_single_fragment_container)
                .commit();
    }

    private void initViews() {
        getViewBinding().toolbarInclude.toolbar.inflateMenu(R.menu.menu_cancel);
        getViewBinding().toolbarInclude.toolbar.setTitle("");
        setSupportActionBar(getViewBinding().toolbarInclude.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void resetToolbar() {
        if (mMenu == null) {
            return;
        }
        MenuItem item = mMenu.findItem(R.id.action_filter);
        if (item != null) {
            item.setVisible(false);
        }
        item = mMenu.findItem(R.id.action_cancel);
        if (item != null) {
            item.setVisible(true);
        }
    }

    private ItineraryFragment getLDTFragment() {
        return (ItineraryFragment) getSupportFragmentManager().findFragmentByTag(ITINERARY_TAG);
    }

    @Override
    public void setTitle(CharSequence title) {
        getViewBinding().toolbarInclude.title.setText(title);
    }

    @Override
    public void setTitle(int titleId) {
        getViewBinding().toolbarInclude.title.setText(titleId);
    }

    @Override
    public void blockBackPressed(boolean blocked) {
        getViewModel().setBackButtonBlocked(blocked);
    }

    @Override
    protected void initDependencies() {
        super.initDependencies();
        bind(DialogUtils.errorDialog(getViewModel().errorResponse, ItineraryActivity.this));
        bind(FragmentUtils.progress(getViewModel().progress, ItineraryActivity.this));
    }

    private void updateReservationState(Intent intent) {
        if (intent.getExtras().containsKey(EXTRA_PICKUP_LOCATION)) {
            EHISolrLocation location = EHIBundle.fromBundle(intent.getExtras()).getEHIModel(EXTRA_PICKUP_LOCATION, EHISolrLocation.class);
            getLDTFragment().setPickupLocation(location);
        } else if (intent.getExtras().containsKey(EXTRA_RETURN_LOCATION)) {
            EHISolrLocation location = EHIBundle.fromBundle(intent.getExtras()).getEHIModel(EXTRA_RETURN_LOCATION, EHISolrLocation.class);
            getLDTFragment().setReturnLocation(location);
        }
        Date pickupDate = (Date) intent.getSerializableExtra(EXTRA_PICKUP_DATE);
        Date dropoffDate = (Date) intent.getSerializableExtra(EXTRA_DROPOFF_DATE);
        Date pickupTime = (Date) intent.getSerializableExtra(EXTRA_PICKUP_TIME);
        Date dropoffTime = (Date) intent.getSerializableExtra(EXTRA_DROPOFF_TIME);
        @SearchLocationsActivity.Flow int flow = intent.getIntExtra(EXTRA_FLOW, SearchLocationsActivity.FLOW_PICKUP_LOCATION_ROUND_TRIP);
        getLDTFragment().updateDatesFromFlow(flow, pickupDate, dropoffDate, pickupTime, dropoffTime);
    }

    @Override
    public void showItinerary(boolean edit, boolean clearLocation) {
        if (clearLocation) {
            getLDTFragment().clearReturnLocation();
        } else {
            getLDTFragment().setEdited();
        }
        if (edit) {
            mPayState = PayState.PAY_LATER; //paylater is the default paystate when initializing a brand new reservation
        }
        FragmentUtils.clearBackStackInclusive(ItineraryActivity.this);
    }

    @Override
    public void showAvailableCarClasses(boolean edit) {
        if (edit) {
            FragmentUtils.clearBackStack(ItineraryActivity.this, CarClassListFragment.TAG);
            return;
        }
        resetToolbar();
        new FragmentUtils.Transaction(getSupportFragmentManager(), FragmentUtils.REPLACE)
                .fragment(new CarClassListFragmentHelper.Builder()
                        .isModify(false)
                        .build())
                .into(R.id.ac_single_fragment_container)
                .addToBackStack(CarClassListFragment.TAG)
                .commit();
    }

    @Override
    public void showCarDetails(@NonNull EHICarClassDetails carClassDetails) {
        resetToolbar();
        new FragmentUtils.Transaction(getSupportFragmentManager(), FragmentUtils.REPLACE)
                .fragment(new CarClassDetailFragmentHelper.Builder()
                        .carClassDetails(carClassDetails)
                        .isModify(false)
                        .build())
                .into(R.id.ac_single_fragment_container)
                .addToBackStack(CarClassDetailFragment.TAG)
                .commit();
    }

    @Override
    public void showCarExtras(@NonNull EHICarClassDetails carClassDetails, boolean edit, final PayState payState, final boolean fromChooseYourRate) {
        mPayState = payState;
        if (edit) {
            FragmentUtils.clearBackStack(ItineraryActivity.this, CarClassExtrasFragment.TAG);
            return;
        }
        resetToolbar();
        new FragmentUtils.Transaction(getSupportFragmentManager(), FragmentUtils.REPLACE)
                .fragment(new CarClassExtrasFragmentHelper.Builder()
                        .fromChooseYourRate(fromChooseYourRate)
                        .isModify(false)
                        .build())
                .into(R.id.ac_single_fragment_container)
                .addToBackStack(CarClassExtrasFragment.TAG)
                .commit();
    }

    @Override
    public void showChooseYourRateScreen(@NonNull final EHICarClassDetails carClassDetails, final boolean edit) {
        if (edit) {
            FragmentUtils.clearBackStack(ItineraryActivity.this, CarClassExtrasFragment.TAG);
            return;
        }
        resetToolbar();
        new FragmentUtils.Transaction(getSupportFragmentManager(), FragmentUtils.REPLACE)
                .fragment(new ChooseYourRateFragmentHelper.Builder()
                        .isModify(false)
                        .build())
                .into(R.id.ac_single_fragment_container)
                .addToBackStack(ChooseYourRateFragment.TAG)
                .commit();

    }

    @Override
    public void carListAnimationInProgress(boolean inProgress, AnimatingViewCallback callback) {
        getViewModel().setAnimationInProgress(inProgress);
        getViewModel().setAnimatingCallback(callback);
    }

    @Override
    public void showDriverInfo(String price, EHIDriverInfo driverInfo, boolean edit) {
        resetToolbar();
        DriverInfoFragment fragment = new DriverInfoFragmentHelper.Builder()
                .driverInfo(driverInfo)
                .isEditing(edit)
                .isModify(false)
                .build();
        new FragmentUtils.Transaction(getSupportFragmentManager(), FragmentUtils.REPLACE)
                .fragment(fragment)
                .into(R.id.ac_single_fragment_container)
                .addToBackStack(DriverInfoFragment.TAG)
                .commit();
    }

    @Override
    public void showMultiTerminal(List<EHIAirlineDetails> ehiAirlineDetails, boolean edit) {
        resetToolbar();
        FlightDetailsFragment fragment = new FlightDetailsFragmentHelper.Builder()
                .flightDetails(ehiAirlineDetails)
                .multiTerminal(true)
                .isEditing(edit)
                .isModify(false)
                .build();
        new FragmentUtils.Transaction(getSupportFragmentManager(), FragmentUtils.REPLACE)
                .fragment(fragment)
                .into(R.id.ac_single_fragment_container)
                .addToBackStack(FlightDetailsFragment.TAG)
                .commit();
    }

    @Override
    public void showDeliveryAndCollection() {
        resetToolbar();
        new FragmentUtils.Transaction(getSupportFragmentManager(), FragmentUtils.REPLACE)
                .fragment(new DeliveryAndCollectionFragmentHelper.Builder().isModify(false).build())
                .into(R.id.ac_single_fragment_container)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void showReview() {
        resetToolbar();
        ReviewFragment fragment = new ReviewFragmentHelper.Builder()
                .extraIsModify(false)
                .extraPayState(mPayState)
                .extraLoginAfterStart(mIsLoginAfterStart)
                .build();
        new FragmentUtils.Transaction(getSupportFragmentManager(), FragmentUtils.REPLACE)
                .fragment(fragment)
                .into(R.id.ac_single_fragment_container)
                .addToBackStack(ReviewFragment.TAG)
                .commit();
    }

    @Override
    public CarClassListFragment.AnimationDataHolder getAnimationData() {
        return mAnimationDataHolder;
    }

    @Override
    public void setAnimationData(CarClassListFragment.AnimationDataHolder data) {
        mAnimationDataHolder = data;
    }

    @Override
    public void showRedemption(EHICarClassDetails ehiCarClassDetails, final boolean fromChooseYourRate) {
        RedemptionFragment fragment = new RedemptionFragmentHelper.Builder()
                .fromChooseYourRate(fromChooseYourRate)
                .carClass(ehiCarClassDetails)
                .build();
        new FragmentUtils.Transaction(getSupportFragmentManager(), FragmentUtils.REPLACE)
                .fragment(fragment)
                .into(R.id.ac_single_fragment_container)
                .addToBackStack(RedemptionFragment.TAG)
                .commit();
    }

    @Override
    public void showingModal(boolean showingModal) {
        mShowingModal = showingModal;
    }

    @Override
    public PayState getPayState() {
        return mPayState;
    }

    @Override
    public void setPayState(final PayState payState) {
        mPayState = payState;
    }

    @Override
    public boolean needShowRateScreen(@NonNull EHICarClassDetails carClassDetails, String contractType) {
        return getViewModel().needShowPaymentScreen(carClassDetails, mPayState, contractType, false);
    }

    public void setIsLoginAfterStart(boolean value) {
        mIsLoginAfterStart = value;
    }
}