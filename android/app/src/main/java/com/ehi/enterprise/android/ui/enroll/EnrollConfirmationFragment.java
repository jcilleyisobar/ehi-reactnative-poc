package com.ehi.enterprise.android.ui.enroll;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.app.EHIStringToken;
import com.ehi.enterprise.android.databinding.EnrollConfirmationFragmentBinding;
import com.ehi.enterprise.android.ui.fragment.DataBindingViewModelFragment;
import com.ehi.enterprise.android.ui.rewards.RewardsLearnMoreFragmentHelper;
import com.ehi.enterprise.android.utils.FragmentUtils;
import com.ehi.enterprise.android.utils.analytics.EHIAnalytics;
import com.ehi.enterprise.android.utils.analytics.EHIAnalyticsDictionaryUtils;
import com.ehi.enterprise.android.utils.analytics.EHIAnalyticsEvent;
import com.isobar.android.newinstancer.Extra;
import com.isobar.android.tokenizedstring.TokenizedString;
import com.isobar.android.viewmodel.ViewModel;

@ViewModel(EnrollConfirmationViewModel.class)
public class EnrollConfirmationFragment extends DataBindingViewModelFragment<EnrollConfirmationViewModel, EnrollConfirmationFragmentBinding> {

    private static final String TAG = "EnrollConfirmationFragment";

    @Extra(String.class)
    public static final String MEMBER_NUMBER = "EXTRA_MEMBER_NUMBER";
    @Extra(String.class)
    public static final String PASSWORD = "EXTRA_PASSWORD";
    @Extra(Integer.class)
    public static final String CURRENT_DRAWER = "CURRENT_DRAWER";

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View view) {
            if (view == getViewBinding().viewEplusBenefits) {
                final Fragment fragment = new RewardsLearnMoreFragmentHelper.Builder()
                        .loggedIn(true)
                        .build();
                showModal(getActivity(), fragment);
                trackClick(EHIAnalytics.Action.ACTION_LEARN_MORE.value);
            } else if (view == getViewBinding().continueButton) {
                trackClick(EHIAnalytics.Action.ACTION_CONTINUE.value);
                getActivity().setResult(Activity.RESULT_OK);
                getActivity().finish();
            }
        }
    };

    private void trackClick(String action) {
        EHIAnalyticsEvent.create()
                .screen(EHIAnalytics.Screen.SCREEN_ENROLL_CONFIRMATION.value, TAG)
                .state(getViewModel().getState())
                .addDictionary(EHIAnalyticsDictionaryUtils.enroll(
                        getViewModel().getState(),
                        getViewModel().getSelectedCountryCode()))
                .action(EHIAnalytics.Motion.MOTION_TAP.value, action)
                .tagScreen()
                .tagEvent();
    }

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);

        EnrollConfirmationFragmentHelper.Extractor extractor = new EnrollConfirmationFragmentHelper.Extractor(this);
        getViewModel().setMemberNumber(extractor.memberNumber());
        getViewModel().setPassword(extractor.password());
        getViewModel().setCurrentDrawer(extractor.currentDrawer());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        createViewBinding(inflater, R.layout.fr_enroll_confirmation, container);
        initViews();
        return getViewBinding().getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        EHIAnalyticsEvent.create()
                .screen(EHIAnalytics.Screen.SCREEN_ENROLL_CONFIRMATION.value, TAG)
                .state(getViewModel().getState())
                .macroEvent(EHIAnalytics.MacroEvent.MACRO_ENROLLMENT_COMPLETE.value)
                .addDictionary(EHIAnalyticsDictionaryUtils.enroll(
                        getViewModel().getState(),
                        getViewModel().getSelectedCountryCode()))
                .tagMacroEvent()
                .tagScreen()
                .tagEvent();
    }

    @Override
    protected void initDependencies() {
        super.initDependencies();
        bind(FragmentUtils.progress(getViewModel().progress, getActivity()));
    }

    private void initViews() {

        getViewBinding().memberNumber.setText(
                new TokenizedString.Formatter<EHIStringToken>(getResources())
                        .addTokenAndValue(EHIStringToken.NUMBER, getViewModel().getMemberNumber())
                        .formatString(R.string.enroll_confirmation_member_number)
                        .format());

        getViewBinding().bulletOne.setText(
                getString(R.string.text_with_bullet_prefix, getString(R.string.enroll_confirmation_bullet_one)));

        getViewBinding().bulletTwo.setText(
                getString(R.string.text_with_bullet_prefix, getString(R.string.enroll_confirmation_bullet_two)));

        getViewBinding().bulletThree.setText(
                getString(R.string.text_with_bullet_prefix, getString(R.string.enroll_confirmation_bullet_three)));

        getViewBinding().bulletFour.setText(
                getString(R.string.text_with_bullet_prefix, getString(R.string.enroll_confirmation_bullet_four)));

        getViewBinding().viewEplusBenefits.setOnClickListener(mOnClickListener);

        getViewBinding().continueButton.setOnClickListener(mOnClickListener);
    }
}
