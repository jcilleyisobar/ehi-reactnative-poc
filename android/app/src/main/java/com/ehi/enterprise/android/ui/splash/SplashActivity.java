package com.ehi.enterprise.android.ui.splash;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;

import com.ehi.enterprise.android.BuildConfig;
import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.app.EHILocale;
import com.ehi.enterprise.android.app.Environment;
import com.ehi.enterprise.android.app.Settings;
import com.ehi.enterprise.android.app.SolrEnvironment;
import com.ehi.enterprise.android.databinding.SplashActivityBinding;
import com.ehi.enterprise.android.ui.dashboard.MainActivityHelper;
import com.ehi.enterprise.android.ui.fragment.DataBindingViewModelActivity;
import com.ehi.enterprise.android.ui.onboarding.OnboardingActivityHelper;
import com.ehi.enterprise.android.utils.LocaleUtils;
import com.ehi.enterprise.android.utils.PermissionUtils;
import com.ehi.enterprise.android.utils.locations.GeofenceManager;
import com.ehi.enterprise.android.utils.locations.LocationApiManager;
import com.ehi.enterprise.android.utils.manager.LocalDataManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.isobar.android.viewmodel.ViewModel;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import io.dwak.reactor.ReactorComputation;
import io.dwak.reactor.interfaces.ReactorComputationFunction;

@ViewModel(SplashActivityViewModel.class)
public class SplashActivity extends DataBindingViewModelActivity<SplashActivityViewModel, SplashActivityBinding> {

    public static final int LOCATION_PERMISSION_REQUEST_CODE = 10;
    private static final int REQUEST_CODE_RECOVER_PLAY_SERVICES = 1001;
    private Timer mStartTimer = new Timer();
    private AnimationDrawable mProgressAnimation;

    @Override
    protected void onNewIntent(final Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setDataBindingContentView(R.layout.ac_splash);
        initViews();

        final LocalDataManager localDataManager = LocalDataManager.getInstance();

        if (BuildConfig.FLAVOR.equalsIgnoreCase("dev") || BuildConfig.FLAVOR.equalsIgnoreCase("uat")) {
            setAppDefaults(localDataManager);
        }

        //This will let us know if this launch is an upgrade/new install
        localDataManager.setOldVersion(LocalDataManager.getInstance().getCurrentVersion());
        localDataManager.setCurrentVersion(BuildConfig.VERSION_CODE);
    }

    private void initViews() {
        getViewBinding().spinner.setImageResource(R.drawable.generic_spinner);
        mProgressAnimation = (AnimationDrawable) getViewBinding().spinner.getDrawable();
    }

    private void setAppDefaults(LocalDataManager localDataManager) {
        // set the env - important for the env chooser to persist
        final String selectedEnvName = localDataManager.getSelectedEnvironmentName();
        final Environment selectedEnv = Environment.fromString(selectedEnvName);
        if (!TextUtils.isEmpty(selectedEnv)) {
            Settings.setServicesEndpoint(selectedEnv);
        }

        final String selectSolrEnvironmentName = localDataManager.getSelectedSolrEnvironmentName();
        final SolrEnvironment selectedSolrEnv = SolrEnvironment.fromString(selectSolrEnvironmentName);
        if (!TextUtils.isEmpty(selectedSolrEnv)) {
            Settings.setServicesEndpoint(selectedEnv);
        }

        // set locale - locale chooser persist
        final String selectedLocaleName = localDataManager.getSelectedLocaleName();
        if (!TextUtils.isEmpty(selectedLocaleName)) {
            final EHILocale ehiLocale = EHILocale.valueOf(selectedLocaleName);
            if (ehiLocale != null) {
                LocaleUtils.updateAppLocale(this, ehiLocale.getLocale());
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (BuildConfig.FLAVOR.equalsIgnoreCase("dev")) {
            performPermissionsCheck();
        } else if (isPlayServicesAvailable()) {
            performPermissionsCheck();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mProgressAnimation != null) {
            mProgressAnimation.start();
        }
    }

    @Override
    public void onPause() {
        if (mProgressAnimation != null && mProgressAnimation.isRunning()) {
            mProgressAnimation.stop();
        }
        super.onPause();
    }

    private boolean isPlayServicesAvailable() {
        int status = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this);
        if (status != ConnectionResult.SUCCESS) {
            if (GoogleApiAvailability.getInstance().isUserResolvableError(status)) {
                showErrorDialog(status);
            } else {
                finish();
            }
            return false;
        }
        return true;
    }

    void showErrorDialog(int code) {
        GoogleApiAvailability.getInstance().getErrorDialog(this, code, REQUEST_CODE_RECOVER_PLAY_SERVICES).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_RECOVER_PLAY_SERVICES:
                if (resultCode == RESULT_CANCELED) {
                    finish();
                }
        }
    }

    @Override
    protected void initDependencies() {
        super.initDependencies();
        addReaction(new ReactorComputationFunction() {
            @Override
            public void react(ReactorComputation reactorComputation) {
                if (getViewModel().progress.getValue() != null
                        || getViewModel().progress.getValue()) {
                    getViewBinding().spinner.setVisibility(View.VISIBLE);
                } else {
                    getViewBinding().spinner.setVisibility(View.GONE);
                }
            }
        });


        addReaction(new ReactorComputationFunction() {
            @Override
            public void react(ReactorComputation reactorComputation) {
                if (getViewModel().isWeekendSpecialContractRequestDone()) {
                    openApp();
                }
            }
        });

    }

    private void performPermissionsCheck() {
        List<String> permissions = PermissionUtils.checkPermissions(this, Manifest.permission.ACCESS_FINE_LOCATION);
        proceedToApp(permissions.isEmpty());
    }

    private void proceedToApp(final boolean locationEnabled) {
        LocationApiManager.getInstance().initialize(getApplicationContext(), locationEnabled, null, null);
        GeofenceManager.getInstance().initialize(getApplicationContext(), locationEnabled);

        getViewModel().refreshPreferredRegionWeekendSpecialContract();
    }

    private void openApp() {
        if (mStartTimer == null) {
            // This is s result from a reaction. It could be called more than once
            // (happened - showed at Apteligent)
            return;
        }

        mStartTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (mStartTimer != null) {
                    mStartTimer.cancel();
                }
                mStartTimer = null;

                if (getViewModel().shouldShowOnboarding()) {
                    getViewModel().setOnboardingWasShown();
                    startActivity(new OnboardingActivityHelper.Builder().build(SplashActivity.this));
                } else {
                    startActivity(new MainActivityHelper.Builder().build(SplashActivity.this));
                }
                finish();

            }
        }, 2000);
    }

    @Override
    public void onRequestPermissionsResult(final int requestCode, @NonNull final String[] permissions, @NonNull final int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE:
                proceedToApp(grantResults[0] == PackageManager.PERMISSION_GRANTED);
                break;
        }
    }

}