package com.ehi.enterprise.android.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.databinding.MyProfileFragmentBinding;
import com.ehi.enterprise.android.models.profile.EHICountry;
import com.ehi.enterprise.android.models.profile.ProfileCollection;
import com.ehi.enterprise.android.ui.activity.ModalActivityHelper;
import com.ehi.enterprise.android.ui.dashboard.MainActivity;
import com.ehi.enterprise.android.ui.fragment.DataBindingViewModelFragment;
import com.ehi.enterprise.android.ui.login.ChangePasswordFragmentHelper;
import com.ehi.enterprise.android.ui.login.VerifyLoginFragmentHelper;
import com.ehi.enterprise.android.ui.profile.interfaces.ISignOutDelegate;
import com.ehi.enterprise.android.ui.profile.widgets.ProfilePaymentInfoView;
import com.ehi.enterprise.android.ui.reservation.AddCreditCardFragmentHelper;
import com.ehi.enterprise.android.utils.analytics.EHIAnalytics;
import com.ehi.enterprise.android.utils.analytics.EHIAnalyticsDictionaryUtils;
import com.ehi.enterprise.android.utils.analytics.EHIAnalyticsEvent;
import com.ehi.enterprise.android.utils.analytics.IRootMenuScreen;
import com.ehi.enterprise.android.utils.exceptions.NotImplementedException;
import com.isobar.android.newinstancer.NoExtras;
import com.isobar.android.viewmodel.ViewModel;

import java.util.List;

import io.dwak.reactor.ReactorComputation;
import io.dwak.reactor.interfaces.ReactorComputationFunction;

@NoExtras
@ViewModel(MyProfileViewModel.class)
public class MyProfileFragment extends DataBindingViewModelFragment<MyProfileViewModel, MyProfileFragmentBinding>
                implements IRootMenuScreen{

    public static final String SCREEN_NAME = "MyProfileFragment";
    private boolean mWasBlocked = false;

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view == getViewBinding().editMemberDetails) {
                if (getViewModel().needToShowDNRDialog()) {
                    showModalDialog(getActivity(), new ProfileDNRDialogFragmentHelper.Builder().build());
                } else {
                    startActivity(new MemberInfoActivityHelper.Builder().build(getActivity()));
                }
            } else if (view == getViewBinding().editDriverLicenseDetails) {
                if (getViewModel().needToShowDNRDialog()) {
                    showModalDialog(getActivity(), new ProfileDNRDialogFragmentHelper.Builder().build());
                } else {
                    startActivity(new LicenseInfoActivityHelper.Builder().build(getActivity()));
                }
            } else if (view == getViewBinding().editPaymentOptions) {
                EHIAnalyticsEvent.create()
                        .screen(EHIAnalytics.Screen.SCREEN_MY_PROFILE.value, MyProfileFragment.SCREEN_NAME)
                        .state(EHIAnalytics.State.STATE_HOME.value)
                        .action(EHIAnalytics.Motion.MOTION_TAP.value, EHIAnalytics.Action.ACTION_EDIT_PAYMENT_OPTIONS.value)
                        .addDictionary(EHIAnalyticsDictionaryUtils.signIn())
                        .tagScreen()
                        .tagEvent();

                showModal(getActivity(), new PaymentsListFragmentHelper.Builder().build());
            } else if (view == getViewBinding().frMyProfileChangePassword) {
                EHIAnalyticsEvent.create()
                        .screen(EHIAnalytics.Screen.SCREEN_MY_PROFILE.value, MyProfileFragment.SCREEN_NAME)
                        .state(EHIAnalytics.State.STATE_HOME.value)
                        .action(EHIAnalytics.Motion.MOTION_TAP.value, EHIAnalytics.Action.ACTION_CHANGE_PASSWORD.value)
                        .addDictionary(EHIAnalyticsDictionaryUtils.signIn())
                        .tagScreen()
                        .tagEvent();

                showModal(getActivity(), new ChangePasswordFragmentHelper.Builder().build());
            }
        }
    };

    private ProfilePaymentInfoView.ProfilePaymentClickListener profilePaymentClickListener = new ProfilePaymentInfoView.ProfilePaymentClickListener() {
        @Override
        public void onAddCreditCardClick() {
            EHIAnalyticsEvent.create()
                    .screen(EHIAnalytics.Screen.SCREEN_MY_PROFILE.value, MyProfileFragment.SCREEN_NAME)
                    .state(EHIAnalytics.State.STATE_HOME.value)
                    .action(EHIAnalytics.Motion.MOTION_TAP.value, EHIAnalytics.Action.ACTION_ADD_CREDIT_CARD.value)
                    .addDictionary(EHIAnalyticsDictionaryUtils.signIn())
                    .tagScreen()
                    .tagEvent();

            showModal(getActivity(), new AddCreditCardFragmentHelper.Builder().extraFromProfile(true).build());
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setShouldHideCallMenuItem(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        createViewBinding(inflater, R.layout.fr_my_profile, container);
        initViews();
        return getViewBinding().getRoot();
    }

    private void initViews() {
        getViewBinding().editMemberDetails.setOnClickListener(mOnClickListener);
        getViewBinding().editDriverLicenseDetails.setOnClickListener(mOnClickListener);
        getViewBinding().editPaymentOptions.setOnClickListener(mOnClickListener);
        getViewBinding().frMyProfileChangePassword.setOnClickListener(mOnClickListener);

        getViewBinding().frMyProfilePaymentInfo.setProfilePaymentClickListener(profilePaymentClickListener);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (!(getActivity() instanceof ISignOutDelegate)) {
            throw new NotImplementedException();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        getActivity().setTitle(R.string.profile_navigation_title);
        if (getViewModel().getProfileFromManager() != null) {
            getViewModel().setUserProfile(getViewModel().getProfileFromManager());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        trackScreenChange();
        if (getViewModel().isNeedToRelogin() && !mWasBlocked) {
            Intent intent = new ModalActivityHelper.Builder()
                    .fragmentClass(new VerifyLoginFragmentHelper.Builder().build().getClass())
                    .build(getActivity());

            getActivity().startActivityForResult(intent, MainActivity.AUTHENTICATE_PROFILE_CODE);
            getActivity().overridePendingTransition(R.anim.modal_slide_in, R.anim.modal_stay);
            mWasBlocked = true;
        } else {
            mWasBlocked = false;
        }
    }

    @Override
    public void trackScreenChange() {
        EHIAnalyticsEvent.create()
                .screen(EHIAnalytics.Screen.SCREEN_MY_PROFILE.value, MyProfileFragment.SCREEN_NAME)
                .state(EHIAnalytics.State.STATE_HOME.value)
                .addDictionary(EHIAnalyticsDictionaryUtils.signIn())
                .tagScreen()
                .tagEvent();
    }

    @Override
    protected void initDependencies() {
        super.initDependencies();
        addReaction("PROFILE_REACTION", new ReactorComputationFunction() {
            @Override
            public void react(ReactorComputation reactorComputation) {
                ProfileCollection profile = getViewModel().getUserProfileCollection();
                if (profile != null) {
                    getViewBinding().frMyProfileMemberInfo.setProfile(profile);
                    getViewBinding().frMyProfileLicenseInfo.setLicenseProfile(profile.getLicenseProfile());

                    if (profile.getPaymentProfile() != null
                            && profile.getPaymentProfile().getAllPaymentMethods() != null) {

                        getViewBinding().editPaymentOptions.setVisibility(
                                getViewModel().shouldShowEditPaymentOptions()? View.VISIBLE : View.GONE
                        );

                        getViewBinding().frMyProfilePaymentHeader.setVisibility(View.VISIBLE);
                        getViewBinding().frMyProfilePaymentInfo.setVisibility(View.VISIBLE);
                        getViewBinding().frMyProfilePaymentInfo.setPaymentProfile(
                                profile.getPaymentProfile(), getViewModel().shouldShowPaymentManagementActions()
                        );
                    } else {
                        getViewBinding().frMyProfilePaymentInfo.setVisibility(View.GONE);
                        getViewBinding().frMyProfilePaymentHeader.setVisibility(View.GONE);
                    }

                    final List<EHICountry> countries = getViewModel().getCountries();
                    if (countries != null && countries.size() > 0) {
                        final String licenceProfileCountryCode = getViewModel().getLicenceProfileCountryCode();
                        EHICountry licenseProfileCountry = null;
                        for (final EHICountry country : countries) {
                            if (country.getCountryCode().equalsIgnoreCase(licenceProfileCountryCode)) {
                                licenseProfileCountry = country;
                                break;
                            }
                        }
                        if (licenseProfileCountry != null) {
                            getViewBinding().frMyProfileLicenseInfo.showIssueDate(licenseProfileCountry.shouldShowIssueDate());
                            getViewBinding().frMyProfileLicenseInfo.showExpiationDate(licenseProfileCountry.shouldShowExpiryDateOnProfile());
                        }
                    }
                }
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_my_profile, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_sign_out) {
            onSignOut();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void onSignOut() {
        ((ISignOutDelegate) getActivity()).onSignOut();
    }


}