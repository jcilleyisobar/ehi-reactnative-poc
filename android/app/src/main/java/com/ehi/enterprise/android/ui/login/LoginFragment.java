package com.ehi.enterprise.android.ui.login;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.util.Pair;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.databinding.LoginFragmentBinding;
import com.ehi.enterprise.android.ui.enroll.EnrollActivity;
import com.ehi.enterprise.android.ui.enroll.EnrollActivityHelper;
import com.ehi.enterprise.android.ui.fragment.DataBindingViewModelFragment;
import com.ehi.enterprise.android.ui.navigation.NavigationDrawerViewModel;
import com.ehi.enterprise.android.ui.reservation.EmeraldClubSignInFragment;
import com.ehi.enterprise.android.ui.reservation.EmeraldClubSignInFragmentHelper;
import com.ehi.enterprise.android.ui.reservation.ReservationDNRDialogFragmentHelper;
import com.ehi.enterprise.android.utils.DLog;
import com.ehi.enterprise.android.utils.DialogUtils;
import com.ehi.enterprise.android.utils.FragmentUtils;
import com.ehi.enterprise.android.utils.IntentUtils;
import com.ehi.enterprise.android.utils.ToastUtils;
import com.ehi.enterprise.android.utils.analytics.EHIAnalytics;
import com.ehi.enterprise.android.utils.analytics.EHIAnalyticsDictionaryUtils;
import com.ehi.enterprise.android.utils.analytics.EHIAnalyticsEvent;
import com.ehi.enterprise.android.utils.manager.LoginManager;
import com.ehi.enterprise.android.utils.manager.ReservationManager;
import com.isobar.android.newinstancer.Extra;
import com.isobar.android.viewmodel.ViewModel;

import io.dwak.reactor.ReactorComputation;
import io.dwak.reactor.interfaces.ReactorComputationFunction;
import io.dwak.reactorbinding.activity.ReactorActivity;
import io.dwak.reactorbinding.view.ReactorView;
import io.dwak.reactorbinding.widget.ReactorTextView;

@ViewModel(LoginViewModel.class)
public class LoginFragment extends DataBindingViewModelFragment<LoginViewModel, LoginFragmentBinding> {

    public static final String SCREEN_NAME = "LoginFragment";
    public static final int EC_LOGGED_IN_REQUEST_CODE = 1000;
    public static final int FORGOT_PASSWORD_REQUEST_CODE = 1001;
    private static final int EMERALD_CLUB_SIGN_IN_REQUEST_CODE = 1002;
    private static final int DNR_REQUEST_CODE = 1003;
    private static final int ENROLL_REQUEST_CODE = 1004;

    private int currentDrawerItemId = NavigationDrawerViewModel.RESET_MENU;

    @Extra(value = String.class, required = false)
    static final String MESSAGE = "MESSAGE";
    @Extra(value = String.class, required = false)
    static final String MEMBER_NUMBER = "MEMBER_NUMBER";
    @Extra(value = Integer.class)
    static final String MENU_POSITION = "MENU_POSITION";
    @Extra(value = Boolean.class, required = false)
    static final String HIDE_ENROLL = "HIDE_ENROLL";


    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view == getViewBinding().loginContainerForgotEmailButton) {
                showModalDialogForResult(getActivity(), ForgotUserNameFragment.newInstance(), ForgotUserNameFragment.REQUEST_CODE);
            } else if (view == getViewBinding().loginScreenForgotPasswordButton) {
                showModalForResult(getActivity(), new ForgotPasswordFragmentHelper.Builder().build(), FORGOT_PASSWORD_REQUEST_CODE);
            } else if (view == getViewBinding().loginScreenSigninButton) {
                getViewModel().setRememberLogin(getViewBinding().loginScreenKeepMeSignedInContainer.isChecked());
                if (getViewModel().isLoggedIntoEmeraldClub()) {
                    showModalDialogForResult(getActivity(), new EmeraldClubLoggedInModalFragmentHelper.Builder().build(), EC_LOGGED_IN_REQUEST_CODE);
                } else {
                    getViewModel().attemptLogin(currentDrawerItemId);
                }
            } else if (view == getViewBinding().joinEnterpriseLoginButton) {
                EHIAnalyticsEvent.create()
                        .screen(EHIAnalytics.Screen.SCREEN_SIGN_IN.value, LoginFragment.SCREEN_NAME)
                        .state(EHIAnalytics.State.STATE_HOME.value)
                        .action(EHIAnalytics.Motion.MOTION_TAP.value, EHIAnalytics.Action.ACTION_JOIN_PLUS.value)
                        .addDictionary(EHIAnalyticsDictionaryUtils.signIn())
                        .tagScreen()
                        .tagEvent();
                Intent intent = new EnrollActivityHelper.Builder()
                        .currentDrawerItem(currentDrawerItemId).build(getContext());

                startActivityForResult(intent, ENROLL_REQUEST_CODE);
            } else if (view == getViewBinding().loginScreenShowPasswordContainer) {
                if (getViewBinding().loginEditPassword.getInputType() == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
                    getViewBinding().loginEditPassword.setInputType(InputType.TYPE_CLASS_TEXT |
                            InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    getViewBinding().loginEditPassword.setTypeface(getViewBinding().loginEditUserName.getTypeface());
                    getViewBinding().loginScreenShowPasswordContainer.setImageDrawable(getResources().getDrawable(R.drawable.icon_show_02));
                } else {
                    getViewBinding().loginEditPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    getViewBinding().loginEditPassword.setTypeface(getViewBinding().loginEditUserName.getTypeface());
                    getViewBinding().loginScreenShowPasswordContainer.setImageDrawable(getResources().getDrawable(R.drawable.icon_show));
                }
                getViewBinding().loginEditPassword.setSelection(getViewBinding().loginEditPassword.getText().toString().length());
            } else if (view == getViewBinding().frLoginJoinEplus) {
                EHIAnalyticsEvent.create()
                        .screen(EHIAnalytics.Screen.SCREEN_SIGN_IN.value, LoginFragment.SCREEN_NAME)
                        .state(EHIAnalytics.State.STATE_HOME.value)
                        .action(EHIAnalytics.Motion.MOTION_TAP.value, EHIAnalytics.Action.ACTION_JOIN_NOW.value)
                        .addDictionary(EHIAnalyticsDictionaryUtils.signIn())
                        .tagScreen()
                        .tagEvent();
                Intent intent = new Intent(getActivity(), EnrollActivity.class);
                startActivity(intent);
            } else if (view == getViewBinding().loginEnrolledAtBranch) {
                new AlertDialog.Builder(getActivity())
                        .setTitle(getString(R.string.signin_partial_enrollment_recovery_modal_title))
                        .setMessage(getString(R.string.signin_partial_enrollment_recovery_modal_details_text))
                        .setPositiveButton(R.string.signin_partial_enrollment_action_button_title, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                EHIAnalyticsEvent.create()
                                        .screen(EHIAnalytics.Screen.SCREEN_SIGN_IN.value, LoginFragment.SCREEN_NAME)
                                        .state(EHIAnalytics.State.STATE_HOME.value)
                                        .action(EHIAnalytics.Motion.MOTION_TAP.value, EHIAnalytics.Action.ACTION_COMPLETE_ENROLLMENT.value)
                                        .addDictionary(EHIAnalyticsDictionaryUtils.signIn())
                                        .tagScreen()
                                        .tagEvent();
                                IntentUtils.openUrlViaExternalApp(getActivity(), getViewModel().getActivationLink());
                            }
                        })
                        .setNegativeButton(R.string.alert_cancel_title, null)
                        .show();
            } else if (view == getViewBinding().loginEmeraldClubMembersLogin) {
                showModalForResult(getActivity(), new EmeraldClubSignInFragmentHelper.Builder().build(), EMERALD_CLUB_SIGN_IN_REQUEST_CODE);
            } else if (view == getViewBinding().loginEmeraldClubMembersLogout) {
                DialogUtils.showOkCancelDialog(getActivity(), getString((R.string.menu_emerald_club_sign_out)), getString(R.string.signout_emerald_club_confirmation_text), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getViewModel().logoutEC();
                    }
                });
            } else if (view == getViewBinding().loginScreenKeepMeSignedInContainer) {
                if (getViewBinding().loginScreenKeepMeSignedInContainer.isChecked()) {
                    EHIAnalyticsEvent.create()
                            .screen(EHIAnalytics.Screen.SCREEN_SIGN_IN.value, EmeraldClubSignInFragment.SCREEN_NAME)
                            .state(EHIAnalytics.State.STATE_HOME.value)
                            .action(EHIAnalytics.Motion.MOTION_TAP.value, EHIAnalytics.Action.ACTION_REMEMBER_ON.value)
                            .addDictionary(EHIAnalyticsDictionaryUtils.signIn())
                            .tagScreen()
                            .tagEvent();
                } else {
                    EHIAnalyticsEvent.create()
                            .screen(EHIAnalytics.Screen.SCREEN_SIGN_IN.value, EmeraldClubSignInFragment.SCREEN_NAME)
                            .state(EHIAnalytics.State.STATE_HOME.value)
                            .action(EHIAnalytics.Motion.MOTION_TAP.value, EHIAnalytics.Action.ACTION_REMEMBER_OFF.value)
                            .addDictionary(EHIAnalyticsDictionaryUtils.signIn())
                            .tagScreen()
                            .tagEvent();
                }

            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            currentDrawerItemId = getArguments().getInt(MENU_POSITION, NavigationDrawerViewModel.RESET_MENU);
            LoginFragmentHelper.Extractor extractor = new LoginFragmentHelper.Extractor(this);
            if (!TextUtils.isEmpty(extractor.message())) {
                getViewModel().setLoginMessage(extractor.message());
            }
            if (!TextUtils.isEmpty(extractor.memberNumber())) {
                getViewModel().setUsername(extractor.memberNumber());
            }

            if (extractor.hideEnroll() != null){
                getViewModel().setHideEnrollButton(extractor.hideEnroll());
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case ForceChangePasswordFragment.REQUEST_CODE:
                if (resultCode == Activity.RESULT_OK) {
                    showModalForResult(getActivity(), new ChangePasswordFragmentHelper.Builder()
                            .changePasswordUserName(getViewModel().username.getValue())
                            .changePasswordIsEcFlow(false)
                            .changePasswordOldPassword(getViewModel().password.getValue())
                            .changePasswordRememberMe(getViewModel().getRememberLogin())
                            .build(), ChangePasswordFragment.REQUEST_CODE);
                }
                break;
            case ForgotUserNameFragment.REQUEST_CODE:
                if (resultCode == Activity.RESULT_CANCELED) {
                    DialogUtils.showDialogWithTitleAndText(getActivity(), getString(R.string.location_services_generic_error), "");
                }
                break;
            case ChangePasswordFragment.REQUEST_CODE:
                getActivity().setResult(Activity.RESULT_OK);
                getActivity().finish();
                break;
            case EC_LOGGED_IN_REQUEST_CODE:
                if (resultCode == Activity.RESULT_OK) {
                    getViewModel().attemptLogin(currentDrawerItemId);
                }
                break;
            case FORGOT_PASSWORD_REQUEST_CODE:
                if (resultCode == Activity.RESULT_OK) {
                    ToastUtils.showLongToast(getActivity(), R.string.forgot_password_email_sent_message);
                }
                break;

            case EMERALD_CLUB_SIGN_IN_REQUEST_CODE:
                if (resultCode == Activity.RESULT_OK) {
                    getViewModel().updateEmeraldClubState();
                    Intent loginIntent = new Intent(ReservationManager.EC_REFRESH_EVENT);
                    loginIntent.putExtra(LoginManager.CURRENT_DRAWER_ITEM, currentDrawerItemId);
                    LocalBroadcastManager.getInstance(getContext()).sendBroadcast(loginIntent);

                    getActivity().finish();
                }
                break;
            case DNR_REQUEST_CODE:
                if (resultCode == Activity.RESULT_OK) {
                    proceedToApp();
                }
                break;
            case ENROLL_REQUEST_CODE:
                if (resultCode == Activity.RESULT_OK) {
                    proceedToApp();
                }
                break;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        createViewBinding(inflater, R.layout.fr_login, container);
        initViews();
        return getViewBinding().getRoot();
    }

    private void initViews() {
        getViewBinding().loginScreenSigninButton.setOnClickListener(mOnClickListener);
        getViewBinding().loginScreenShowPasswordContainer.setOnClickListener(mOnClickListener);
        getViewBinding().loginContainerForgotEmailButton.setOnClickListener(mOnClickListener);
        getViewBinding().loginScreenForgotPasswordButton.setOnClickListener(mOnClickListener);
        getViewBinding().loginEnrolledAtBranch.setOnClickListener(mOnClickListener);
        getViewBinding().joinEnterpriseLoginButton.setOnClickListener(mOnClickListener);
        getViewBinding().loginEmeraldClubMembersLogin.setOnClickListener(mOnClickListener);
        getViewBinding().loginEmeraldClubMembersLogout.setOnClickListener(mOnClickListener);
        getViewBinding().loginScreenKeepMeSignedInContainer.setOnClickListener(mOnClickListener);
        getViewBinding().loginScreenKeepMeSignedInContainer.setChecked(getViewModel().needCheckRememberMeByDefault());

        if (getViewModel().getDevLogins() != null && !getViewModel().getDevLogins().isEmpty()) {
            getViewBinding().devLoginContainer.setVisibility(View.VISIBLE);
            for (final Pair<String, String> usernamePasswordPair : getViewModel().getDevLogins()) {
                final TextView loginButton = (TextView) DataBindingUtil.inflate(
                        LayoutInflater.from(getActivity()), R.layout.v_dev_login_button, getViewBinding().devLoginContainer, false).getRoot();
                loginButton.setText(usernamePasswordPair.first);
                loginButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getViewBinding().loginEditUserName.setText(usernamePasswordPair.first);
                        getViewBinding().loginEditPassword.setText(usernamePasswordPair.second);
                        DLog.d("Logging in with : " + usernamePasswordPair.first + " " + usernamePasswordPair.second);
                    }
                });
                getViewBinding().devLoginContainer.addView(loginButton);
            }
        } else {
            getViewBinding().devLoginContainer.setVisibility(View.GONE);
        }

        if (getViewModel().isUserNameSet()) {
            getViewBinding().loginEditUserName.setVisibility(View.GONE);
            getViewBinding().loginUserNameTextView.setVisibility(View.GONE);
            getViewBinding().emeraldClubAndEnrollView.setVisibility(View.GONE);
            getViewBinding().loginContainerForgotEmailButton.setVisibility(View.GONE);
        }

        if (getViewModel().shouldHideEnrollButton()){
            getViewBinding().enrollView.setVisibility(View.GONE);
        }

    }


    @Override
    protected void initDependencies() {
        super.initDependencies();
        bind(ReactorActivity.titleRes(getViewModel().title, getActivity()));
        bind(FragmentUtils.progress(getViewModel().progress, getActivity()));
        bind(DialogUtils.errorDialog(getViewModel().errorResponse, getActivity()));

        bind(ReactorTextView.bindText(getViewModel().username, getViewBinding().loginEditUserName));
        bind(ReactorTextView.bindText(getViewModel().password, getViewBinding().loginEditPassword));

        bind(ReactorTextView.text(getViewModel().loginMessage, getViewBinding().loginMessage));
        bind(ReactorView.visible(getViewModel().loginMessageVisibility, getViewBinding().loginMessage));

        bind(ReactorView.enabled(getViewModel().isValidForm, getViewBinding().loginScreenSigninButton));

        addReaction("FORCE_RESET_PASSWORD_REACTION", new ReactorComputationFunction() {
            @Override
            public void react(ReactorComputation reactorComputation) {
                getViewModel().isChangePasswordRequired();
                if (!reactorComputation.isFirstRun()) {
                    //todo when api is revealed for change password
                    showModalDialogForResult(getActivity(), new ForceChangePasswordFragmentHelper.Builder().build(), ForceChangePasswordFragment.REQUEST_CODE);
                }
            }
        });

        addReaction("TERMS_CONDITIONS_MISMATCH", new ReactorComputationFunction() {
            @Override
            public void react(ReactorComputation reactorComputation) {
                if (getViewModel().isTermsConditionsMismatch()) {
                    if (TextUtils.isEmpty(getViewModel().getTermsAndConditionsString())) {
                        getViewModel().getTermsConditions();
                    } else {
                        showTermsConditionsDialog(getViewModel().getTermsAndConditionsString(), getViewModel().getTermsConditionsVersion());
                    }
                }
            }
        });

        addReaction("LOGIN_SUCCESS_REACTION", new ReactorComputationFunction() {
            @Override
            public void react(ReactorComputation reactorComputation) {
                if (getViewModel().isUserLoggedIn()) {
                    getViewModel().getCountries();
                }
            }
        });

        addReaction("GET_COUNTRIES_REACTION", new ReactorComputationFunction() {
            @Override
            public void react(ReactorComputation reactorComputation) {
                if (getViewModel().isGetCountriesDone()) {
                    getViewModel().updatePreferredRegionFromProfile();
                }
            }
        });

        addReaction("UPDATED_PREFERRED_REGION", new ReactorComputationFunction() {
            @Override
            public void react(ReactorComputation reactorComputation) {
                if (getViewModel().isWeekendSpecialContractRequestDone()) {
                    if (getViewModel().needToShowDNRDialog()) {
                        showModalDialogForResult(getActivity(), new ReservationDNRDialogFragmentHelper.Builder().build(), DNR_REQUEST_CODE);
                    } else {
                        proceedToApp();
                    }
                }
            }
        });

        addReaction("EC_LOGGED_IN", new ReactorComputationFunction() {
            @Override
            public void react(ReactorComputation reactorComputation) {
                if (getViewModel().isEcLoggedIn()) {
                    getViewBinding().loginEmeraldClubMembersLogin.setVisibility(View.GONE);
                    getViewBinding().loginEmeraldClubMembersLogout.setVisibility(View.VISIBLE);
                } else {
                    getViewBinding().loginEmeraldClubMembersLogin.setVisibility(View.VISIBLE);
                    getViewBinding().loginEmeraldClubMembersLogout.setVisibility(View.GONE);
                }
            }
        });
    }

    private void proceedToApp() {
        EHIAnalyticsEvent.create()
                .screen(EHIAnalytics.Screen.SCREEN_SIGN_IN.value, LoginFragment.SCREEN_NAME)
                .state(EHIAnalytics.State.STATE_SUCCESSFUL.value)
                .addCustomDimensions(EHIAnalyticsDictionaryUtils.customDimensions())
                .addDictionary(EHIAnalyticsDictionaryUtils.signIn())
                .tagScreen()
                .tagEvent();
        getActivity().setResult(Activity.RESULT_OK);
        getActivity().finish();
    }

    private void showTermsConditionsDialog(String termsConditionsString, final String versionNumber) {
        new AlertDialog.Builder(getActivity())
                .setTitle(R.string.terms_and_conditions_updated_title)
                .setMessage(Html.fromHtml(termsConditionsString))
                .setPositiveButton(R.string.terms_and_conditions_accept_title, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        getViewModel().setTermsConditionsVersionNumber(versionNumber);
                        getViewModel().attemptLogin();
                    }
                })
                .setNegativeButton(R.string.cancel_button_title_key, null)
                .setCancelable(true)
                .create()
                .show();
    }


    @Override
    public void onResume() {
        super.onResume();
        if (getViewModel().isUserLoggedIn()) {
            getActivity().finish();
        }

        EHIAnalyticsEvent.create()
                .screen(EHIAnalytics.Screen.SCREEN_SIGN_IN.value, LoginFragment.SCREEN_NAME)
                .state(EHIAnalytics.State.STATE_HOME.value)
                .addDictionary(EHIAnalyticsDictionaryUtils.signIn())
                .tagScreen()
                .tagEvent();
    }

}