package com.ehi.enterprise.android.ui.reservation.history;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.app.EHIStringToken;
import com.ehi.enterprise.android.databinding.LookupRentalFragmentBinding;
import com.ehi.enterprise.android.models.reservation.EHIReservation;
import com.ehi.enterprise.android.network.services.EHIServicesError;
import com.ehi.enterprise.android.ui.confirmation.ConfirmationActivityHelper;
import com.ehi.enterprise.android.ui.fragment.DataBindingViewModelFragment;
import com.ehi.enterprise.android.ui.login.LoginFragmentHelper;
import com.ehi.enterprise.android.ui.util.AfterTextChangedWatcher;
import com.ehi.enterprise.android.utils.DLog;
import com.ehi.enterprise.android.utils.DialogUtils;
import com.ehi.enterprise.android.utils.FragmentUtils;
import com.ehi.enterprise.android.utils.IntentUtils;
import com.ehi.enterprise.android.utils.analytics.EHIAnalytics;
import com.ehi.enterprise.android.utils.analytics.EHIAnalyticsDictionaryUtils;
import com.ehi.enterprise.android.utils.analytics.EHIAnalyticsEvent;
import com.isobar.android.newinstancer.NoExtras;
import com.isobar.android.tokenizedstring.TokenizedString;
import com.isobar.android.viewmodel.ViewModel;

import io.dwak.reactor.ReactorComputation;
import io.dwak.reactor.interfaces.ReactorComputationFunction;

@NoExtras
@ViewModel(LookupRentalViewModel.class)
public class LookupRentalFragment extends DataBindingViewModelFragment<LookupRentalViewModel, LookupRentalFragmentBinding> {

    public static final String SCREEN_NAME = "LookupRentalFragment";

    private EHIReservation retrievedReservation;

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v == getViewBinding().contactUsButton) {
                String phone = getViewModel().getSupportPhoneNumber();
                if (phone == null) {
                    //In case config feed is messed up
                    DialogUtils.showDialogWithTitleAndText(getActivity(), getString(R.string.locations_unvailable_error_title), getString(R.string.alert_service_error_title));
                } else {
                    IntentUtils.callNumber(getActivity(), phone);
                }
            } else if (v == getViewBinding().findRentalButton) {
                EHIAnalyticsEvent.create()
                        .screen(EHIAnalytics.Screen.SCREEN_MY_RENTALS.value, LookupRentalFragment.SCREEN_NAME)
                        .state(EHIAnalytics.State.STATE_LOOKUP_RENTAL.value)
                        .action(EHIAnalytics.Motion.MOTION_TAP.value, EHIAnalytics.Action.ACTION_FIND_RENTAL.value)
                        .addDictionary(EHIAnalyticsDictionaryUtils.signIn())
                        .tagScreen()
                        .tagEvent();
                FragmentUtils.addProgressFragment(getActivity());
                getViewModel().findRental();
            } else if (v == getViewBinding().forgotConfirmationNumber) {
                showModalDialog(getActivity(), new ForgotConfirmationFragmentHelper.Builder().build());
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        createViewBinding(inflater, R.layout.fr_look_up_rental, container);
        getActivity().setTitle(getResources().getString(R.string.rentals_lookup_navigation_title));
        initView();
        return getViewBinding().getRoot();
    }

    private void initView() {
        getViewBinding().confirmationNumberInput.addTextChangedListener(new AfterTextChangedWatcher(new AfterTextChangedWatcher.AfterTextChangedListener() {
            @Override
            public void afterTextChanged(String s) {
                getViewModel().setConfirmationNumber(s);
            }
        }));

        getViewBinding().firstNameInput.addTextChangedListener(new AfterTextChangedWatcher(new AfterTextChangedWatcher.AfterTextChangedListener() {
            @Override
            public void afterTextChanged(String s) {
                getViewModel().setFirstName(s);
            }
        }));

        getViewBinding().lastNameInput.addTextChangedListener(new AfterTextChangedWatcher(new AfterTextChangedWatcher.AfterTextChangedListener() {
            @Override
            public void afterTextChanged(String s) {
                getViewModel().setLastName(s);
            }
        }));
        getViewBinding().findRentalButton.setOnClickListener(mOnClickListener);
        getViewBinding().contactUsButton.setOnClickListener(mOnClickListener);
        getViewBinding().forgotConfirmationNumber.setOnClickListener(mOnClickListener);
    }

    @Override
    protected void initDependencies() {
        super.initDependencies();
        addReaction("RESERVATION_REACTION", new ReactorComputationFunction() {
            @Override
            public void react(ReactorComputation reactorComputation) {
                if (getViewModel().getRetrievedReservation() != null) {

                    FragmentUtils.removeProgressFragment(getActivity());
                    retrievedReservation = getViewModel().getRetrievedReservation();

                    if (retrievedReservation.getReservationStatus().equalsIgnoreCase(EHIReservation.CANCELED)) {
                        //reservation is canceled
                        getViewBinding().errorContainer.setVisibility(View.VISIBLE);
                        getViewBinding().errorAlertText.setText(new TokenizedString.Formatter<EHIStringToken>(getResources())
                                .formatString(R.string.rentals_canceled_lookup_text)
                                .addTokenAndValue(EHIStringToken.NUMBER, retrievedReservation.getConfirmationNumber())
                                .format());
                    } else {
                        Intent intent = new ConfirmationActivityHelper.Builder().extraReservation(retrievedReservation)
                                .isModify(false)
                                .exitGoesHome(false)
                                .build(getActivity());
                        startActivity(intent);
                    }
                    getViewModel().clearRetrievedReservation();
                }
            }
        });

        addReaction("ERROR_REACTION", new ReactorComputationFunction() {
            @Override
            public void react(ReactorComputation reactorComputation) {
                if (getViewModel().getErrorResponse() != null) {
                    FragmentUtils.removeProgressFragment(getActivity());
                    DLog.i(SCREEN_NAME, "RETURN CODE: " + getViewModel().getErrorResponse().getErrorCode());
                    if (EHIServicesError.ErrorCode.CROS_REDEMPTION_RES_LOOKUP_LOGIN_REQUIRED.equals(getViewModel().getErrorResponse().getErrorCode())) {
                        showModal(getActivity(), new LoginFragmentHelper.Builder().build());
                        getViewModel().setErrorResponse(null);
                    } else {
                        getViewBinding().errorContainer.setVisibility(View.VISIBLE);
                    }
                }
            }
        });

        addReaction("VALIDATION_REACTION", new ReactorComputationFunction() {
            @Override
            public void react(ReactorComputation reactorComputation) {
                getViewBinding().findRentalButton.setEnabled(getViewModel().isButtonEnabled());
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        getViewBinding().firstNameInput.setText(getViewModel().getFirstName());
        getViewBinding().lastNameInput.setText(getViewModel().getLastName());
    }

}