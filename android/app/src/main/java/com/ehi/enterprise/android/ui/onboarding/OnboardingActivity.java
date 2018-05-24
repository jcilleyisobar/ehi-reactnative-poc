package com.ehi.enterprise.android.ui.onboarding;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.databinding.OnboardingActivityBinding;
import com.ehi.enterprise.android.ui.dashboard.MainActivityHelper;
import com.ehi.enterprise.android.ui.enroll.EnrollActivity;
import com.ehi.enterprise.android.ui.fragment.DataBindingViewModelActivity;
import com.ehi.enterprise.android.ui.login.LoginFragmentHelper;
import com.ehi.enterprise.android.ui.viewmodel.ManagersAccessViewModel;
import com.ehi.enterprise.android.utils.analytics.EHIAnalytics;
import com.ehi.enterprise.android.utils.analytics.EHIAnalyticsDictionaryUtils;
import com.ehi.enterprise.android.utils.analytics.EHIAnalyticsEvent;
import com.isobar.android.newinstancer.NoExtras;
import com.isobar.android.viewmodel.ViewModel;

import java.util.Locale;


@NoExtras
@ViewModel(ManagersAccessViewModel.class)
public class OnboardingActivity extends DataBindingViewModelActivity<ManagersAccessViewModel, OnboardingActivityBinding> {

    private static final String SCREEN_NAME = "OnboardingActivity";

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view == getViewBinding().signIn) {
                EHIAnalyticsEvent.create()
                        .screen(EHIAnalytics.Screen.SCREEN_WELCOME.value, SCREEN_NAME)
                        .state(EHIAnalytics.State.STATE_HOME.value)
                        .addDictionary(EHIAnalyticsDictionaryUtils.termsAndConditions(Locale.getDefault().getCountry()))                        .action(EHIAnalytics.Motion.MOTION_TAP.value, EHIAnalytics.Action.ACTION_SIGN_IN.value)
                        .tagScreen()
                        .tagEvent();
                goToMainActivity();
                showModal(new LoginFragmentHelper.Builder().build());
                finish();
            } else if (view == getViewBinding().join) {
                EHIAnalyticsEvent.create()
                        .screen(EHIAnalytics.Screen.SCREEN_WELCOME.value, SCREEN_NAME)
                        .state(EHIAnalytics.State.STATE_HOME.value)
                        .action(EHIAnalytics.Motion.MOTION_TAP.value, EHIAnalytics.Action.ACTION_JOIN_NOW.value)
                        .addDictionary(EHIAnalyticsDictionaryUtils.termsAndConditions(Locale.getDefault().getCountry()))                        .tagScreen()
                        .tagEvent();
                goToMainActivity();
                startActivity(new Intent(OnboardingActivity.this, EnrollActivity.class));
                finish();
            } else if (view == getViewBinding().continueAsAGuest) {
                EHIAnalyticsEvent.create()
                        .screen(EHIAnalytics.Screen.SCREEN_WELCOME.value, SCREEN_NAME)
                        .state(EHIAnalytics.State.STATE_HOME.value)
                        .addDictionary(EHIAnalyticsDictionaryUtils.termsAndConditions(Locale.getDefault().getCountry()))                        .action(EHIAnalytics.Motion.MOTION_TAP.value, EHIAnalytics.Action.ACTION_SKIP_CONTINUE.value)
                        .tagScreen()
                        .tagEvent();

                goToMainActivity();
                finish();
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setDataBindingContentView(R.layout.ac_onboarding);
        initViews();
    }

    @Override
    public void onResume() {
        super.onResume();
        checkShouldRedirectUser();
    }

    private void initViews() {
        getViewBinding().signIn.setOnClickListener(mOnClickListener);
        getViewBinding().join.setOnClickListener(mOnClickListener);
        getViewBinding().continueAsAGuest.setOnClickListener(mOnClickListener);
    }

    private void checkShouldRedirectUser() {
        if (getViewModel().isUserLoggedIn()) {
            goToMainActivity();
            finish();
            return;
        }

        EHIAnalyticsEvent.create()
                .screen(EHIAnalytics.Screen.SCREEN_WELCOME.value, SCREEN_NAME)
                .state(EHIAnalytics.State.STATE_HOME.value)
                .tagScreen();
    }

    private void goToMainActivity() {
        Intent mainActivityIntent = new MainActivityHelper.Builder().build(OnboardingActivity.this);
        mainActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(mainActivityIntent);
    }
}
