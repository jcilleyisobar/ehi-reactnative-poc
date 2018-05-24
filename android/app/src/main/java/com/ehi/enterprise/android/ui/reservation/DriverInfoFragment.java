package com.ehi.enterprise.android.ui.reservation;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.databinding.DriverInfoFragmentBinding;
import com.ehi.enterprise.android.models.location.EHILocation;
import com.ehi.enterprise.android.models.reservation.EHIDriverInfo;
import com.ehi.enterprise.android.models.reservation.EHIReservation;
import com.ehi.enterprise.android.ui.fragment.DataBindingViewModelFragment;
import com.ehi.enterprise.android.ui.login.LoginFragmentHelper;
import com.ehi.enterprise.android.ui.reservation.interfaces.ReservationFlowListener;
import com.ehi.enterprise.android.utils.BaseAppUtils;
import com.ehi.enterprise.android.utils.DialogUtils;
import com.ehi.enterprise.android.utils.FragmentUtils;
import com.ehi.enterprise.android.utils.ToastUtils;
import com.ehi.enterprise.android.utils.analytics.EHIAnalytics;
import com.ehi.enterprise.android.utils.analytics.EHIAnalyticsDictionaryUtils;
import com.ehi.enterprise.android.utils.analytics.EHIAnalyticsEvent;
import com.isobar.android.newinstancer.Extra;
import com.isobar.android.viewmodel.ViewModel;

import io.dwak.reactor.ReactorComputation;
import io.dwak.reactor.interfaces.ReactorComputationFunction;
import io.dwak.reactorbinding.activity.ReactorActivity;
import io.dwak.reactorbinding.view.ReactorView;
import io.dwak.reactorbinding.widget.ReactorCompoundButton;
import io.dwak.reactorbinding.widget.ReactorTextInputLayout;
import io.dwak.reactorbinding.widget.ReactorTextView;

@ViewModel(DriverInfoViewModel.class)
public class DriverInfoFragment extends DataBindingViewModelFragment<DriverInfoViewModel, DriverInfoFragmentBinding> {

    public static final String SCREEN_NAME = "DriverInfoFragment";
    public static final String TAG = "DriverInfoFragment";

    public static final int LOGIN_RESULT = 100;

    @Extra(value = EHIDriverInfo.class, required = false)
    public static final String DRIVER_INFO = "DRIVER_INFO";
    @Extra(boolean.class)
    public static String IS_EDITING = "IS_EDITING";
    @Extra(boolean.class)
    public static final String IS_MODIFY = "ehi.EXTRA_IS_MODIFY";

    //region onClickListener
    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view == getViewBinding().signUpEmailContainer) {
                if (getViewBinding().signUpEmailCheckBox.isChecked()) {
                    EHIAnalyticsEvent.create()
                            .screen(EHIAnalytics.Screen.SCREEN_RESERVATION.value, DriverInfoFragment.SCREEN_NAME)
                            .state(EHIAnalytics.State.STATE_DRIVER_INFO.value)
                            .action(EHIAnalytics.Motion.MOTION_TAP.value, EHIAnalytics.Action.ACTION_EMAIL_OPT_OUT.value)
                            .addDictionary(EHIAnalyticsDictionaryUtils.driverInfo(getViewBinding().saveInformationCheckBox.isChecked()))
                            .tagScreen()
                            .tagEvent();
                } else {
                    EHIAnalyticsEvent.create()
                            .screen(EHIAnalytics.Screen.SCREEN_RESERVATION.value, DriverInfoFragment.SCREEN_NAME)
                            .state(EHIAnalytics.State.STATE_DRIVER_INFO.value)
                            .action(EHIAnalytics.Motion.MOTION_TAP.value, EHIAnalytics.Action.ACTION_EMAIL_OPT_IN.value)
                            .addDictionary(EHIAnalyticsDictionaryUtils.driverInfo(getViewBinding().saveInformationCheckBox.isChecked()))
                            .tagScreen()
                            .tagEvent();
                }
                getViewModel().signUpEmailClicked();
            } else if (view == getViewBinding().saveInformationContainer) {
                getViewModel().saveInformationClicked();
            } else if (view == getViewBinding().continueButton) {
                EHIAnalyticsEvent.create()
                        .screen(EHIAnalytics.Screen.SCREEN_RESERVATION.value, DriverInfoFragment.SCREEN_NAME)
                        .state(EHIAnalytics.State.STATE_DRIVER_INFO.value)
                        .action(EHIAnalytics.Motion.MOTION_TAP.value, EHIAnalytics.Action.ACTION_DONE.value)
                        .addDictionary(EHIAnalyticsDictionaryUtils.driverInfo(getViewBinding().saveInformationCheckBox.isChecked()))
                        .tagScreen()
                        .tagEvent();

                getViewModel().continueButtonClicked();
                if (!getViewModel().isModify()) {
                    exit();
                }
            } else if (view == getViewBinding().signInButton) {
                showModalForResult(getActivity(), new LoginFragmentHelper.Builder().hideEnroll(true).build(), LOGIN_RESULT);
            } else {
                BaseAppUtils.hideKeyboard(getActivity());
            }
        }
    };

    private View.OnClickListener mOnDisabledClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View view) {
            getViewModel().highlightInvalidFields();
            ToastUtils.showLongToast(getContext(), R.string.required_fields_error);
        }
    };
    //endregion

    //region lifecycle
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            DriverInfoFragmentHelper.Extractor extractor = new DriverInfoFragmentHelper.Extractor(this);
            getViewModel().setIsModify(extractor.isModify());
            getViewModel().setIsEditing(extractor.isEditing());

            if (extractor.driverInfo() != null) {
                getViewModel().setDriverInfo(extractor.driverInfo());
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        createViewBinding(inflater, R.layout.fr_reservation_driver_info, container);
        initViews();
        return getViewBinding().getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        EHIAnalyticsEvent.create()
                .screen(EHIAnalytics.Screen.SCREEN_RESERVATION.value, DriverInfoFragment.SCREEN_NAME)
                .state(EHIAnalytics.State.STATE_DRIVER_INFO.value)
                .addDictionary(EHIAnalyticsDictionaryUtils.reservation())
                .tagScreen()
                .tagEvent();
    }

    //endregion

    private void initViews() {
        getViewBinding().signUpEmailContainer.setOnClickListener(mOnClickListener);
        getViewBinding().saveInformationContainer.setOnClickListener(mOnClickListener);
        getViewBinding().scrollViewClickInterceptor.setOnClickListener(mOnClickListener);
        getViewBinding().signInButton.setOnClickListener(mOnClickListener);

        getViewBinding().continueButton.setOnClickListener(mOnClickListener);
        getViewBinding().continueButton.setOnDisabledClickListener(mOnDisabledClickListener);
    }

    @Override
    protected void initDependencies() {
        super.initDependencies();
        bind(FragmentUtils.progress(getViewModel().progress, getActivity()));
        bind(DialogUtils.errorDialog(getViewModel().errorResponse, getActivity()));
        bind(ReactorActivity.titleRes(getViewModel().titleRes, getActivity()));

        bind(ReactorView.visibility(getViewModel().signinLayout.visibility(), getViewBinding().signInContainer));

        bind(ReactorTextView.text(getViewModel().authFullName.text(), getViewBinding().authName));
        bind(ReactorView.visibility(getViewModel().authFullName.visibility(), getViewBinding().authName));

        bind(ReactorTextView.bindText(getViewModel().firstName.text(), getViewBinding().firstName));
        bind(ReactorView.backgroundRes(getViewModel().firstName.backgroundResource(), getViewBinding().firstName));
        bind(ReactorView.visibility(getViewModel().firstNameLayout.visibility(), getViewBinding().firstNameLayout));

        bind(ReactorTextView.bindText(getViewModel().lastName.text(), getViewBinding().lastName));
        bind(ReactorView.backgroundRes(getViewModel().lastName.backgroundResource(), getViewBinding().lastName));
        bind(ReactorView.visibility(getViewModel().lastNameLayout.visibility(), getViewBinding().lastNameLayout));

        bind(ReactorTextView.bindText(getViewModel().phoneNumber.text(), getViewBinding().phoneNumber));
        bind(ReactorView.backgroundRes(getViewModel().phoneNumber.backgroundResource(), getViewBinding().phoneNumber));
        bind(ReactorTextView.bindText(getViewModel().emailAddress.text(), getViewBinding().email));
        bind(ReactorView.backgroundRes(getViewModel().emailAddress.backgroundResource(), getViewBinding().email));

        bind(ReactorCompoundButton.bindChecked(getViewModel().saveInformationCheckBox.checked(), getViewBinding().saveInformationCheckBox));
        bind(ReactorView.visibility(getViewModel().saveInformationContainer.visibility(), getViewBinding().saveInformationContainer));
        bind(ReactorCompoundButton.bindChecked(getViewModel().signUpEmailCheckBox.checked(), getViewBinding().signUpEmailCheckBox));
        bind(ReactorView.visibility(getViewModel().signUpEmailContainer.visibility(), getViewBinding().signUpEmailContainer));

        bind(ReactorView.visibility(getViewModel().germanDoubleOptInText.visibility(), getViewBinding().germanDoubleOptInText));
        bind(ReactorView.visibility(getViewModel().driverInfoUkIdentityText.visibility(), getViewBinding().driverInfoUkIdentityText));

        bind(ReactorTextInputLayout.error(getViewModel().firstNameError, getViewBinding().firstNameLayout));
        bind(ReactorTextInputLayout.error(getViewModel().lastNameError, getViewBinding().lastNameLayout));
        bind(ReactorTextInputLayout.error(getViewModel().phoneNumberError, getViewBinding().phoneNumberLayout));
        bind(ReactorTextInputLayout.error(getViewModel().emailError, getViewBinding().emailLayout));

        bind(ReactorView.enabled(getViewModel().continueButton.enabled(), getViewBinding().continueButton));
        bind(ReactorTextView.text(getViewModel().continueButton.text(), getViewBinding().continueButton));

        addReaction("MODIFY_DRIVER_INFO", new ReactorComputationFunction() {
            @Override
            public void react(ReactorComputation reactorComputation) {
                EHIReservation successResponse = getViewModel().getSuccessModifyDriverResponse();
                if (successResponse != null) {
                    exit();
                    getViewModel().clearSuccessModifyDriverResponse();
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == LOGIN_RESULT) {
                getViewModel().fillFormWithLoggedUserData();
                if (getViewModel().isEPUserLoggedIn()) {
                    ToastUtils.showToast(getActivity(), R.string.reservation_driver_info_toast);
                }
            }
        }
    }

    public void exit() {
        if (getViewModel().isEditing()) {
            if (!getViewModel().isModify()) {
                // ok result will trigger logic to show locally saved data not the one from reservation
                // so we should not put it in modify
                getActivity().setResult(Activity.RESULT_OK);
            }
            getActivity().finish();
        } else {
            ((ItineraryActivity) getActivity()).setIsLoginAfterStart(getViewModel().isIsLoginAfterStart());
            EHILocation pickup = getViewModel().getReservationObject().getPickupLocation();
            if (pickup.isMultiTerminal()) {
                ((ReservationFlowListener) getActivity()).showMultiTerminal(pickup.getEHIAirlineDetails(), false);
            } else {
                ((ReservationFlowListener) getActivity()).showReview();
            }
        }
    }
}