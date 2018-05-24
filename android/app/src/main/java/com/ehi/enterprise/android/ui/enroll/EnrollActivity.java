package com.ehi.enterprise.android.ui.enroll;

import android.os.Bundle;
import android.view.MenuItem;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.databinding.ToolbarActivityBinding;
import com.ehi.enterprise.android.models.geofence.EHIGeofence;
import com.ehi.enterprise.android.ui.fragment.DataBindingViewModelActivity;
import com.ehi.enterprise.android.ui.navigation.NavigationDrawerViewModel;
import com.ehi.enterprise.android.utils.FragmentUtils;
import com.isobar.android.newinstancer.Extra;
import com.isobar.android.viewmodel.ViewModel;

import java.util.ArrayList;

@ViewModel(EnrollActivityViewModel.class)
public class EnrollActivity extends DataBindingViewModelActivity<EnrollActivityViewModel, ToolbarActivityBinding> implements EnrollFlowListener {

    public static final String TOTAL_STEPS = "3";

    @Extra(value = Integer.class, required = false)
    public static final String CURRENT_DRAWER_ITEM = "CURRENT_DRAWER";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setDataBindingContentView(R.layout.ac_toolbar_activity);
        getViewModel().startEnrollProfile();
        initViews();
        goToStepOne();
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (isFinishing()) {
            getViewModel().clearEnrollProfile();
        }
    }

    private void initViews() {
        getViewBinding().toolbarInclude.toolbar.setTitle("");
        getViewBinding().toolbarInclude.title.setText(R.string.enroll_window_title);
        setSupportActionBar(getViewBinding().toolbarInclude.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void goToStepOne() {
        final EnrollStepOneFragment fragment = new EnrollStepOneFragmentHelper.Builder().build();
        new FragmentUtils.Transaction(getSupportFragmentManager(), FragmentUtils.REPLACE)
                .fragment(fragment)
                .into(R.id.ac_single_fragment_container)
                .commit();
    }

    @Override
    public void goToStepTwo() {
        goToStepTwo(false);
    }

    @Override
    public void goToStepTwoWithDriverFound() {
        goToStepTwo(true);
    }

    @Override
    public void goToAddressStep(boolean isEmeraldClub) {
        final EnrollAddressFragment fragment =
                new EnrollAddressFragmentHelper.Builder()
                        .emeraldClub(isEmeraldClub)
                        .build();
        new FragmentUtils.Transaction(getSupportFragmentManager(), FragmentUtils.REPLACE)
                .fragment(fragment)
                .into(R.id.ac_single_fragment_container)
                .addToBackStack(EnrollStepTwoFragment.TAG)
                .commit();
    }

    @Override
    public void goToStepThree() {
        final EnrollStepThreeFragment fragment = new EnrollStepThreeFragmentHelper.Builder().build();
        new FragmentUtils.Transaction(getSupportFragmentManager(), FragmentUtils.REPLACE)
                .fragment(fragment)
                .into(R.id.ac_single_fragment_container)
                .addToBackStack(EnrollStepThreeFragment.TAG)
                .commit();
    }

    @Override
    public void goToFullFormStep(ArrayList<String> errorMessageList) {
        // hide back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        FragmentUtils.clearBackStackInclusive(this, EnrollStepTwoFragment.TAG);

        final EnrollFullFormFragment fragment = new EnrollFullFormFragmentHelper.Builder()
                .errorMessageList(errorMessageList)
                .build();

        new FragmentUtils.Transaction(getSupportFragmentManager(), FragmentUtils.REPLACE)
                .fragment(fragment)
                .into(R.id.ac_single_fragment_container)
                .commit();
    }

    @Override
    public void goToConfirmationStep(String loyaltyNumber, String password) {
        // hide back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        FragmentUtils.clearBackStackInclusive(this, EnrollStepTwoFragment.TAG);

        final EnrollConfirmationFragment fragment = new EnrollConfirmationFragmentHelper.Builder()
                .memberNumber(loyaltyNumber)
                .password(password)
                .currentDrawer(getIntent().getIntExtra(CURRENT_DRAWER_ITEM, NavigationDrawerViewModel.RESET_MENU))
                .build();
        new FragmentUtils.Transaction(getSupportFragmentManager(), FragmentUtils.REPLACE)
                .fragment(fragment)
                .into(R.id.ac_single_fragment_container)
                .commit();
    }

    private void goToStepTwo(boolean driverFound) {
        final EnrollStepTwoFragment fragment = new EnrollStepTwoFragmentHelper.Builder()
                .driverFound(driverFound)
                .build();
        new FragmentUtils.Transaction(getSupportFragmentManager(), FragmentUtils.REPLACE)
                .fragment(fragment)
                .into(R.id.ac_single_fragment_container)
                .addToBackStack(EnrollStepTwoFragment.TAG)
                .commit();
    }
}
