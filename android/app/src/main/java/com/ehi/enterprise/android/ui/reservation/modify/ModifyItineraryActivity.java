package com.ehi.enterprise.android.ui.reservation.modify;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import com.ehi.enterprise.android.ui.reservation.CarClassDetailFragment;
import com.ehi.enterprise.android.ui.reservation.CarClassDetailFragmentHelper;
import com.ehi.enterprise.android.ui.reservation.CarClassExtrasFragment;
import com.ehi.enterprise.android.ui.reservation.CarClassExtrasFragmentHelper;
import com.ehi.enterprise.android.ui.reservation.CarClassListFragment;
import com.ehi.enterprise.android.ui.reservation.CarClassListFragmentHelper;
import com.ehi.enterprise.android.ui.reservation.ChooseYourRateFragment;
import com.ehi.enterprise.android.ui.reservation.ChooseYourRateFragmentHelper;
import com.ehi.enterprise.android.ui.reservation.DriverInfoFragment;
import com.ehi.enterprise.android.ui.reservation.DriverInfoFragmentHelper;
import com.ehi.enterprise.android.ui.reservation.FlightDetailsFragment;
import com.ehi.enterprise.android.ui.reservation.FlightDetailsFragmentHelper;
import com.ehi.enterprise.android.ui.reservation.ItineraryActivity;
import com.ehi.enterprise.android.ui.reservation.ItineraryFragment;
import com.ehi.enterprise.android.ui.reservation.ItineraryFragmentHelper;
import com.ehi.enterprise.android.ui.reservation.interfaces.ReservationFlowListener;
import com.ehi.enterprise.android.ui.reservation.redemption.RedemptionFragment;
import com.ehi.enterprise.android.ui.reservation.redemption.RedemptionFragmentHelper;
import com.ehi.enterprise.android.utils.EHIBundle;
import com.ehi.enterprise.android.utils.FragmentUtils;
import com.isobar.android.newinstancer.Extra;
import com.isobar.android.viewmodel.ViewModel;

import java.util.Date;
import java.util.List;

@ViewModel(ModifyItineraryViewModel.class)
public class ModifyItineraryActivity
        extends DataBindingViewModelActivity<ModifyItineraryViewModel, ToolbarActivityBinding>
        implements ReservationFlowListener {

    public static final int SCREEN_ID_LOCATION_DATE_TIME = 1;
    public static final int SCREEN_ID_CAR_CLASSES = 2;
    public static final int SCREEN_ID_EXTRAS = 3;

    @Extra(value = Integer.class, required = false)
    public static final String EXTRA_FLOW = "ehi.EXTRA_FLOW";
    @Extra(value = EHISolrLocation.class, required = false)
    public static final String EXTRA_PICKUP_LOCATION = "ehi.EXTRA_PICKUP_LOCATION";
    @Extra(value = EHISolrLocation.class, required = false)
    public static final String EXTRA_RETURN_LOCATION = "ehi.EXTRA_RETURN_LOCATION";
    @Extra(value = Date.class, required = false)
    public static final String EXTRA_PICKUP_DATE = "ehi.EXTRA_PICKUP_DATE";
    @Extra(value = Date.class, required = false)
    public static final String EXTRA_RETURN_DATE = "ehi.EXTRA_RETURN_DATE";
    @Extra(value = Date.class, required = false)
    public static final String EXTRA_PICKUP_TIME = "ehi.EXTRA_PICKUP_TIME";
    @Extra(value = Date.class, required = false)
    public static final String EXTRA_RETURN_TIME = "ehi.EXTRA_RETURN_TIME";
    @Extra(value = Integer.class, required = false)
    public static final String SCREEN_ID = "ehi.SCREEN_ID";

    public static final String PAY_STATE = "PAY_STATE";

    private PayState mPayState;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setDataBindingContentView(R.layout.ac_toolbar_activity);

        if (savedInstanceState != null && savedInstanceState.containsKey(PAY_STATE)) {
            mPayState = (PayState) savedInstanceState.getSerializable(PAY_STATE);
        }

        getViewBinding().toolbarInclude.toolbar.setTitle("");
        setSupportActionBar(getViewBinding().toolbarInclude.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mPayState = getViewModel().getPayState();

        final ModifyItineraryActivityHelper.Extractor extractor = new ModifyItineraryActivityHelper.Extractor(this);
        Integer screenId = extractor.screenId();
        if (screenId == null) {
            screenId = SCREEN_ID_LOCATION_DATE_TIME;
        }
        switch (screenId) {
            case SCREEN_ID_LOCATION_DATE_TIME:
                showItinerary(extractor);
                break;
            case SCREEN_ID_CAR_CLASSES:
                showAvailableCarClasses(false);
                break;
            case SCREEN_ID_EXTRAS:
                showCarExtras(
                        getViewModel().getCurrentModifyReservation().getCarClassDetails(),
                        false,
                        mPayState,
                        false
                );
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(PAY_STATE, mPayState);
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
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() == 1) {
            finish();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        updateModifyState(intent);
    }

    private void updateModifyState(Intent intent) {
        if (intent.getExtras().containsKey(ItineraryActivity.EXTRA_PICKUP_LOCATION)) {
            EHISolrLocation location = EHIBundle.fromBundle(intent.getExtras()).getEHIModel(ItineraryActivity.EXTRA_PICKUP_LOCATION, EHISolrLocation.class);
            getLDTFragment().setPickupLocation(location);
        } else if (intent.getExtras().containsKey(ItineraryActivity.EXTRA_RETURN_LOCATION)) {
            EHISolrLocation location = EHIBundle.fromBundle(intent.getExtras()).getEHIModel(ItineraryActivity.EXTRA_RETURN_LOCATION, EHISolrLocation.class);
            getLDTFragment().setReturnLocation(location);
        }
        Date pickupDate = (Date) intent.getSerializableExtra(EXTRA_PICKUP_DATE);
        Date dropoffDate = (Date) intent.getSerializableExtra(EXTRA_RETURN_DATE);
        Date pickupTime = (Date) intent.getSerializableExtra(EXTRA_PICKUP_TIME);
        Date dropoffTime = (Date) intent.getSerializableExtra(EXTRA_RETURN_TIME);
        @SearchLocationsActivity.Flow int flow = intent.getIntExtra(EXTRA_FLOW, SearchLocationsActivity.FLOW_PICKUP_LOCATION_ROUND_TRIP);
        getLDTFragment().updateDatesFromFlow(flow, pickupDate, dropoffDate, pickupTime, dropoffTime);
    }

    private ItineraryFragment getLDTFragment() {
        return (ItineraryFragment) getSupportFragmentManager().findFragmentByTag(ItineraryActivity.ITINERARY_TAG);
    }

    //region reservationFlow

    @Override
    public void showItinerary(boolean edit, boolean clearLocation) {
        new FragmentUtils.Transaction(getSupportFragmentManager(), FragmentUtils.REPLACE)
                .fragment(new ItineraryFragmentHelper.Builder()
                        .extraAbandonedHolder(ReservationInformation.fromReservationObject(getViewModel().getCurrentModifyReservation()))
                        .isModify(true)
                        .build(), ItineraryActivity.ITINERARY_TAG)
                .into(R.id.ac_single_fragment_container)
                .addToBackStack(ItineraryFragment.TAG)
                .commit();
    }

    private void showItinerary(ModifyItineraryActivityHelper.Extractor extractor) {
        new FragmentUtils.Transaction(getSupportFragmentManager(), FragmentUtils.REPLACE)
                .fragment(new ItineraryFragmentHelper.Builder()
                        .extraAbandonedHolder(ReservationInformation.fromReservationObject(getViewModel().getCurrentModifyReservation()))
                        .isModify(true)
                        .extraPickupDate(extractor.extraPickupDate())
                        .extraPickupTime(extractor.extraPickupTime())
                        .extraReturnDate(extractor.extraReturnDate())
                        .extraReturnTime(extractor.extraReturnTime())
                        .build(), ItineraryActivity.ITINERARY_TAG)
                .into(R.id.ac_single_fragment_container)
                .addToBackStack(ItineraryFragment.TAG)
                .commit();
    }

    @Override
    public void showAvailableCarClasses(boolean edit) {
        new FragmentUtils.Transaction(getSupportFragmentManager(), FragmentUtils.REPLACE)
                .fragment(new CarClassListFragmentHelper.Builder().isModify(true).build())
                .into(R.id.ac_single_fragment_container)
                .addToBackStack(CarClassListFragment.TAG)
                .commit();
    }

    @Override
    public void showCarDetails(@NonNull EHICarClassDetails carClassDetails) {
        new FragmentUtils.Transaction(getSupportFragmentManager(), FragmentUtils.REPLACE)
                .fragment(new CarClassDetailFragmentHelper.Builder()
                        .carClassDetails(carClassDetails)
                        .isModify(true)
                        .build())
                .into(R.id.ac_single_fragment_container)
                .addToBackStack(CarClassDetailFragment.TAG)
                .commit();
    }

    @Override
    public void showCarExtras(@NonNull EHICarClassDetails carClassDetails, boolean edit, final PayState payState, final boolean fromChooseYourRate) {
        mPayState = payState;
        new FragmentUtils.Transaction(getSupportFragmentManager(), FragmentUtils.REPLACE)
                .fragment(new CarClassExtrasFragmentHelper.Builder()
                        .isModify(true)
                        .build())
                .into(R.id.ac_single_fragment_container)
                .addToBackStack(CarClassExtrasFragment.TAG)
                .commit();
    }

    @Override
    public void showChooseYourRateScreen(@NonNull final EHICarClassDetails carClassDetails, final boolean edit) {
        new FragmentUtils.Transaction(getSupportFragmentManager(), FragmentUtils.REPLACE)
                .fragment(new ChooseYourRateFragmentHelper.Builder()
                        .isModify(true)
                        .build())
                .into(R.id.ac_single_fragment_container)
                .addToBackStack(ChooseYourRateFragment.TAG)
                .commit();
    }

    @Override
    public void showDriverInfo(String price, EHIDriverInfo driverInfo, boolean edit) {
        new FragmentUtils.Transaction(getSupportFragmentManager(), FragmentUtils.REPLACE)
                .fragment(new DriverInfoFragmentHelper.Builder()
                        .driverInfo(driverInfo)
                        .isEditing(edit)
                        .isModify(true)
                        .build())
                .into(R.id.ac_single_fragment_container)
                .addToBackStack(DriverInfoFragment.TAG)
                .commit();
    }

    @Override
    public void showMultiTerminal(List<EHIAirlineDetails> ehiAirlineDetails, boolean edit) {
        FlightDetailsFragment fragment = new FlightDetailsFragmentHelper.Builder()
                .flightDetails(ehiAirlineDetails)
                .multiTerminal(true)
                .isEditing(edit)
                .isModify(true)
                .build();
        new FragmentUtils.Transaction(getSupportFragmentManager(), FragmentUtils.REPLACE)
                .fragment(fragment)
                .into(R.id.ac_single_fragment_container)
                .addToBackStack(FlightDetailsFragment.TAG)
                .commit();
    }

    @Override
    public void showReview() {
        Intent data = new Intent();
        data.putExtra(PAY_STATE, mPayState);

        setResult(RESULT_OK, data);
        finish();
    }

    @Override
    public void showDeliveryAndCollection() {
        //shouldn't be called in this activity
    }

    @Override
    public CarClassListFragment.AnimationDataHolder getAnimationData() {
        return null;
    }

    @Override
    public void setAnimationData(CarClassListFragment.AnimationDataHolder data) {
        //shouldn't be called in this activity
    }

    @Override
    public void showRedemption(EHICarClassDetails ehiCarClassDetails, final boolean fromChooseYourRate) {
        RedemptionFragment fragment = new RedemptionFragmentHelper.Builder()
                .carClass(ehiCarClassDetails)
                .isModify(true)
                .fromChooseYourRate(fromChooseYourRate)
                .build();
        new FragmentUtils.Transaction(getSupportFragmentManager(), FragmentUtils.REPLACE)
                .fragment(fragment)
                .into(R.id.ac_single_fragment_container)
                .addToBackStack(RedemptionFragment.TAG)
                .commit();
    }

    @Override
    public void showingModal(boolean showingModal) {

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
        return getViewModel().needShowPaymentScreen(carClassDetails, mPayState, contractType, true);
    }

    @Override
    public void carListAnimationInProgress(boolean inProgress, AnimatingViewCallback callback) {

    }

    //endregion
}


