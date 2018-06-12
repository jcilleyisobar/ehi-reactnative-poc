package com.ehi.enterprise.android.ui.reservation.modify;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.MenuItem;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.databinding.ToolbarActivityBinding;
import com.ehi.enterprise.android.models.reservation.EHIAirlineDetails;
import com.ehi.enterprise.android.models.reservation.EHICarClassDetails;
import com.ehi.enterprise.android.models.reservation.EHIDriverInfo;
import com.ehi.enterprise.android.ui.fragment.DataBindingViewModelActivity;
import com.ehi.enterprise.android.ui.reservation.CarClassListFragment;
import com.ehi.enterprise.android.ui.reservation.ChooseYourRateFragment;
import com.ehi.enterprise.android.ui.reservation.ChooseYourRateFragmentHelper;
import com.ehi.enterprise.android.ui.reservation.DeliveryAndCollectionFragmentHelper;
import com.ehi.enterprise.android.ui.reservation.ReviewFragmentHelper;
import com.ehi.enterprise.android.ui.reservation.interfaces.BackButtonBlockListener;
import com.ehi.enterprise.android.ui.reservation.interfaces.ReservationFlowListener;
import com.ehi.enterprise.android.ui.reservation.redemption.RedemptionFragment;
import com.ehi.enterprise.android.ui.reservation.redemption.RedemptionFragmentHelper;
import com.ehi.enterprise.android.utils.FragmentUtils;
import com.isobar.android.newinstancer.Extra;
import com.isobar.android.viewmodel.ViewModel;

import java.util.List;

@ViewModel(ModifyReviewActivityViewModel.class)
public class ModifyReviewActivity
        extends DataBindingViewModelActivity<ModifyReviewActivityViewModel, ToolbarActivityBinding>
        implements ReservationFlowListener, BackButtonBlockListener {

    private static final int MODIFY_ACTIVITY_RESULT = 1098;

    @Extra(PayState.class)
    public static final String PAY_STATE = "PAY_STATE";
    private PayState mPayState;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setDataBindingContentView(R.layout.ac_toolbar_activity);
        setPayState(new ModifyReviewActivityHelper.Extractor(this).payState());
        commitFragment();

        getViewBinding().toolbarInclude.toolbar.setTitle("");
        setSupportActionBar(getViewBinding().toolbarInclude.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void commitFragment() {
        new FragmentUtils.Transaction(getSupportFragmentManager(), FragmentUtils.ADD)
                .fragment(new ReviewFragmentHelper.Builder()
                        .extraIsModify(true)
                        .extraPayState(mPayState)
                        .extraPrePayOriginalAmount(getViewModel().getPrePayOriginalAmount())
                        .build())
                .into(R.id.ac_single_fragment_container)
                .commit();
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
    public void setTitle(CharSequence title) {
        getViewBinding().toolbarInclude.title.setText(title);
    }

    @Override
    public void setTitle(int titleId) {
        getViewBinding().toolbarInclude.title.setText(titleId);
    }

    @Override
    public void showItinerary(boolean edit, boolean clearLocation) {
        startActivityForResult(new ModifyItineraryActivityHelper.Builder()
                .screenId(ModifyItineraryActivity.SCREEN_ID_LOCATION_DATE_TIME)
                .build(ModifyReviewActivity.this), MODIFY_ACTIVITY_RESULT);
    }

    @Override
    public void showAvailableCarClasses(boolean edit) {
        startActivityForResult(new ModifyItineraryActivityHelper.Builder()
                .screenId(ModifyItineraryActivity.SCREEN_ID_CAR_CLASSES)
                .build(ModifyReviewActivity.this), MODIFY_ACTIVITY_RESULT);
    }

    @Override
    public void showCarDetails(@NonNull EHICarClassDetails carClassDetails) {
        //shouldn't be called on this activty
    }

    @Override
    public void showCarExtras(@NonNull EHICarClassDetails carClassDetails, boolean edit, final PayState payState, final boolean fromChooseYourRate) {
        mPayState = payState;
        startActivity(new ModifyItineraryActivityHelper.Builder()
                .screenId(ModifyItineraryActivity.SCREEN_ID_EXTRAS)
                .build(ModifyReviewActivity.this));
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
    public void carListAnimationInProgress(boolean inProgress, AnimatingViewCallback callback) {

    }

    @Override
    public void showDriverInfo(String price, EHIDriverInfo driverInfo, boolean edit) {
        //nothing here?
    }

    @Override
    public void showMultiTerminal(List<EHIAirlineDetails> ehiAirlineDetails, boolean edit) {
        //nothing here?
    }

    @Override
    public void showReview() {
        //nothing here?
    }

    @Override
    public void showDeliveryAndCollection() {
        new FragmentUtils.Transaction(getSupportFragmentManager(), FragmentUtils.REPLACE)
                .fragment(new DeliveryAndCollectionFragmentHelper.Builder().isModify(true).build())
                .into(R.id.ac_single_fragment_container)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public CarClassListFragment.AnimationDataHolder getAnimationData() {
        return null;
    }

    @Override
    public void setAnimationData(CarClassListFragment.AnimationDataHolder data) {
        //nothing
    }

    @Override
    public void showRedemption(EHICarClassDetails ehiCarClassDetails, final boolean fromChooseYourRate) {
        RedemptionFragment fragment = new RedemptionFragmentHelper.Builder()
                .carClass(ehiCarClassDetails)
                .isModify(true)
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
    public void blockBackPressed(boolean block) {
        getViewModel().setBackButtonBlocked(block);
    }

    @Override
    public void onBackPressed() {
        if (getViewModel().isBackButtonBlocked()) {
            return;
        }

        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == MODIFY_ACTIVITY_RESULT) {
            PayState payState = (PayState) data.getSerializableExtra(PAY_STATE);
            if (payState != null) {
                mPayState = payState;
            }
        }
    }
}
