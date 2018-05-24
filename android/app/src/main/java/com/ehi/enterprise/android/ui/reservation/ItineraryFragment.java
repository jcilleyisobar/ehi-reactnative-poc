package com.ehi.enterprise.android.ui.reservation;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.databinding.ItineraryFragmentBinding;
import com.ehi.enterprise.android.models.location.solr.EHISolrLocation;
import com.ehi.enterprise.android.models.reservation.EHIAdditionalInformation;
import com.ehi.enterprise.android.models.reservation.EHIContract;
import com.ehi.enterprise.android.models.reservation.EHIReservation;
import com.ehi.enterprise.android.models.reservation.ReservationInformation;
import com.ehi.enterprise.android.ui.fragment.DataBindingViewModelFragment;
import com.ehi.enterprise.android.ui.location.SearchLocationsActivity;
import com.ehi.enterprise.android.ui.location.SearchLocationsActivityHelper;
import com.ehi.enterprise.android.ui.login.ForceChangePasswordFragment;
import com.ehi.enterprise.android.ui.login.ForceChangePasswordFragmentHelper;
import com.ehi.enterprise.android.ui.login.LoginFragmentHelper;
import com.ehi.enterprise.android.ui.reservation.interfaces.ReservationFlowListener;
import com.ehi.enterprise.android.ui.reservation.widget.DateTimeSelectorView;
import com.ehi.enterprise.android.ui.widget.EHISpinnerView;
import com.ehi.enterprise.android.utils.BaseAppUtils;
import com.ehi.enterprise.android.utils.DialogUtils;
import com.ehi.enterprise.android.utils.EHIBundle;
import com.ehi.enterprise.android.utils.FragmentUtils;
import com.ehi.enterprise.android.utils.ToastUtils;
import com.ehi.enterprise.android.utils.analytics.EHIAnalytics;
import com.ehi.enterprise.android.utils.analytics.EHIAnalyticsDictionaryUtils;
import com.ehi.enterprise.android.utils.analytics.EHIAnalyticsEvent;
import com.google.gson.reflect.TypeToken;
import com.isobar.android.newinstancer.Extra;
import com.isobar.android.viewmodel.ViewModel;

import java.util.Date;
import java.util.List;

import io.dwak.reactor.ReactorComputation;
import io.dwak.reactor.interfaces.ReactorComputationFunction;
import io.dwak.reactorbinding.activity.ReactorActivity;
import io.dwak.reactorbinding.view.ReactorView;
import io.dwak.reactorbinding.widget.ReactorCompoundButton;
import io.dwak.reactorbinding.widget.ReactorTextView;

@ViewModel(ItineraryViewModel.class)
public class ItineraryFragment extends DataBindingViewModelFragment<ItineraryViewModel, ItineraryFragmentBinding> {

    public static final String SCREEN_NAME = "ItineraryFragment";
    public static final String TAG = ItineraryFragment.class.getSimpleName();
    public static final int EMERALD_CLUB_SIGN_IN_REQUEST_CODE = 1231;
    public static final int SIGN_IN_REQUEST_CODE = 1232;
    private static final int TRIP_PURPOSE_RESULT = 987;
    private static final int PIN_RESULT = 1001;
    private static final int PRE_RATE_ADDITIONAL_FIELD_RESULT = 988;

    //region extras keys
    @Extra(value = EHISolrLocation.class, required = false)
    public static final String EXTRA_PICKUP_LOCATION = "ehi.EXTRA_PICKUP_LOCATION";
    @Extra(value = ReservationInformation.class, required = false)
    public static final String EXTRA_ABANDONED_HOLDER = "ABANDONED_RESERVATION_HOLDER";
    @Extra(boolean.class)
    public static final String IS_MODIFY = "ehi.EXTRA_IS_MODIFY";
    @Extra(value = Date.class, required = false)
    public static final String EXTRA_PICKUP_DATE = "ehi.EXTRA_PICKUP_DATE";
    @Extra(value = Date.class, required = false)
    public static final String EXTRA_RETURN_DATE = "ehi.EXTRA_RETURN_DATE";
    @Extra(value = Date.class, required = false)
    public static final String EXTRA_PICKUP_TIME = "ehi.EXTRA_PICKUP_TIME";
    @Extra(value = Date.class, required = false)
    public static final String EXTRA_RETURN_TIME = "ehi.EXTRA_RETURN_TIME";
    //endregion

    // region onClickListener
    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view == getViewBinding().pickupLocationContainer) {
                if (getViewModel().shouldBlockLocationChange()) {
                    showModalDialog(getActivity(), new BlockModifyLocationDialogFragmentHelper.Builder().build(), false, -1);
                } else {
                    startActivity(new SearchLocationsActivityHelper.Builder()
                            .extraFlow(getViewModel().getFlow(true))
                            .extraShowStartReservation(false)
                            .extraFromLdt(true)
                            .isModify(getViewModel().isModify())
                            .extraPickupDate(getViewModel().getPickupDate())
                            .extraPickupTime(getViewModel().getPickupTime())
                            .extraDropoffDate(getViewModel().getReturnDate())
                            .extraDropoffTime(getViewModel().getReturnTime())
                            .build(getActivity()));
                }
            } else if (view == getViewBinding().addReturnLocationButton ||
                    view == getViewBinding().returnLocationTextView) {
                if (getViewModel().shouldBlockLocationChange()) {
                    showModalDialog(getActivity(), new BlockModifyLocationDialogFragmentHelper.Builder().build(), false, -1);
                } else {
                    EHIAnalyticsEvent.create()
                            .screen(EHIAnalytics.Screen.SCREEN_RESERVATION.value, ItineraryFragment.SCREEN_NAME)
                            .state(EHIAnalytics.State.STATE_WIDGET.value)
                            .action(EHIAnalytics.Motion.MOTION_TAP.value, EHIAnalytics.Action.ACTION_RETURN_DIFF_LOCATION.value)
                            .addDictionary(EHIAnalyticsDictionaryUtils.dateTime(getViewModel().getPickUpLocation(),
                                    getViewModel().getReturnLocation(),
                                    getViewModel().getPickupDateTime(),
                                    getViewModel().getReturnDateTime()))
                            .tagScreen()
                            .tagEvent();

                    if (getViewModel().getPickUpLocation().isOneWaySupported() || getViewModel().isModify()) {
                        //will not check one way support for modify since we don't have one way info at that time
                        startActivity(new SearchLocationsActivityHelper.Builder()
                                .extraFlow(getViewModel().getFlow(false))
                                .extraShowStartReservation(false)
                                .extraFromLdt(true)
                                .isModify(getViewModel().isModify())
                                .extraPickupDate(getViewModel().getPickupDate())
                                .extraPickupTime(getViewModel().getPickupTime())
                                .extraDropoffDate(getViewModel().getReturnDate())
                                .extraDropoffTime(getViewModel().getReturnTime())
                                .build(getActivity()));
                    } else {
                        getViewModel().showOneWayNotSupportedError();
                    }
                }
            } else if (view == getViewBinding().dropoffLockImage) {
                showModalDialog(getActivity(), new BlockModifyLocationDialogFragmentHelper.Builder().build(), false, -1);
            } else if (view == getViewBinding().selectPickupDateView) {
                Fragment datePicker;
                if (getViewModel().getReturnLocation() == null) {
                    datePicker = new DatePickerFragmentHelper.Builder().extraPickupLocation(getViewModel().getPickUpLocation())
                            .extraReturnLocation(getViewModel().getPickUpLocation())
                            .build();
                } else {
                    datePicker = new DatePickerFragmentHelper.Builder().extraPickupLocation(getViewModel().getPickUpLocation())
                            .extraReturnLocation(getViewModel().getReturnLocation())
                            .build();
                }
                showModalForResult(getActivity(), datePicker, ItineraryActivity.DATE_RANGE_REQUEST_CODE);
            } else if (view == getViewBinding().selectReturnDateView) {
                if (getViewModel().getPickupDate() == null && getViewModel().getPickupTime() == null) {
                    return;
                }

                DatePickerFragmentHelper.Builder datePickerBuilder = new DatePickerFragmentHelper.Builder()
                        .extraPickupLocation(getViewModel().getPickUpLocation())
                        .extraPickupDate(getViewModel().getPickupDate())
                        .extraReturnDate(getViewModel().getReturnDate())
                        .extraPickupTime(getViewModel().getPickupTime());

                if (getViewModel().getReturnLocation() == null) {
                    datePickerBuilder.extraReturnLocation(getViewModel().getPickUpLocation());
                } else {
                    datePickerBuilder.extraReturnLocation(getViewModel().getReturnLocation());
                }

                if (getViewModel().getPickupDate() != null) {
                    datePickerBuilder.extraDateSelectState(DatePickerViewModel.DATE_SELECT_STATE_RETURN_DATE);
                }

                showModalForResult(getActivity(), datePickerBuilder.build(), ItineraryActivity.DATE_RANGE_REQUEST_CODE);
            } else if (view == getViewBinding().removeReturnLocation) {
                EHIAnalyticsEvent.create()
                        .screen(EHIAnalytics.Screen.SCREEN_RESERVATION.value, ItineraryFragment.SCREEN_NAME)
                        .state(EHIAnalytics.State.STATE_WIDGET.value)
                        .action(EHIAnalytics.Motion.MOTION_TAP.value, EHIAnalytics.Action.ACTION_DELETE_RETURN_LOCATION.value)
                        .addDictionary(EHIAnalyticsDictionaryUtils.dateTime(getViewModel().getPickUpLocation(),
                                getViewModel().getReturnLocation(),
                                getViewModel().getPickupDateTime(),
                                getViewModel().getReturnDateTime()))
                        .tagScreen()
                        .tagEvent();

                if (getViewModel().shouldBlockLocationChange()) {
                    showModalDialog(getActivity(), new BlockModifyLocationDialogFragmentHelper.Builder().build(), false, -1);
                } else {
                    getViewModel().removeReturnLocationClicked();
                }
            } else if (view == getViewBinding().addCidButton) {
                EHIAnalyticsEvent.create()
                        .screen(EHIAnalytics.Screen.SCREEN_RESERVATION.value, ItineraryFragment.SCREEN_NAME)
                        .state(EHIAnalytics.State.STATE_WIDGET.value)
                        .action(EHIAnalytics.Motion.MOTION_TAP.value, EHIAnalytics.Action.ACTION_EXPAND_CID.value)
                        .addDictionary(EHIAnalyticsDictionaryUtils.dateTime(getViewModel().getPickUpLocation(),
                                getViewModel().getReturnLocation(),
                                getViewModel().getPickupDateTime(),
                                getViewModel().getReturnDateTime()))
                        .tagScreen()
                        .tagEvent();
                getViewModel().addCidButtonClicked();
            } else if (view == getViewBinding().clearCidButton) {
                getViewModel().clearCidButtonClicked();
                getViewBinding().cidEditText.requestFocus();
                BaseAppUtils.showKeyboardForView(getViewBinding().cidEditText);
            } else if (view == getViewBinding().cidInputArea) {
                BaseAppUtils.hideKeyboard(getActivity());
            } else if (view == getViewBinding().buttonContinue) {
                EHIAnalyticsEvent.create()
                        .screen(EHIAnalytics.Screen.SCREEN_RESERVATION.value, ItineraryFragment.SCREEN_NAME)
                        .state(EHIAnalytics.State.STATE_WIDGET.value)
                        .action(EHIAnalytics.Motion.MOTION_TAP.value, EHIAnalytics.Action.ACTION_VIEW_CONTINUE.value)
                        .macroEvent(EHIAnalytics.MacroEvent.MACRO_DATE_TIME_SELECTED.value)
                        .addDictionary(EHIAnalyticsDictionaryUtils.dateTime(getViewModel().getPickUpLocation(),
                                getViewModel().getReturnLocation(),
                                getViewModel().getPickupDateTime(),
                                getViewModel().getReturnDateTime()))
                        .tagScreen()
                        .tagEvent()
                        .tagMacroEvent();

                getViewModel().initiateReservation();
            } else if (view == getViewBinding().ecSignInButton) {
                if (!getViewModel().isUserLoggedIn()) {
                    showModalForResult(getActivity(), new EmeraldClubSignInFragmentHelper.Builder().build(), EMERALD_CLUB_SIGN_IN_REQUEST_CODE);
                }
            }
        }
    };
    // endregion

    // region date/time callbacks
    private DateTimeSelectorView.InteractionListener mPickupDateTimeChooserListener = new DateTimeSelectorView.InteractionListener() {
        @Override
        public void onSelectDateClicked() {
            Fragment datePicker;
            if (getViewModel().getReturnLocation() == null) {
                datePicker = new DatePickerFragmentHelper.Builder().extraPickupLocation(getViewModel().getPickUpLocation())
                        .extraReturnLocation(getViewModel().getPickUpLocation())
                        .extraPickupDate(getViewModel().getPickupDate())
                        .extraReturnDate(getViewModel().getReturnDate())
                        .extraPickupTime(getViewModel().getPickupTime())
                        .extraReturnTime(getViewModel().getReturnTime())
                        .extraDateSelectState(DatePickerViewModel.DATE_SELECT_STATE_PICKUP_DATE)
                        .extraEdit(true)
                        .build();
            } else {
                datePicker = new DatePickerFragmentHelper.Builder().extraPickupLocation(getViewModel().getPickUpLocation())
                        .extraReturnLocation(getViewModel().getReturnLocation())
                        .extraPickupDate(getViewModel().getPickupDate())
                        .extraReturnDate(getViewModel().getReturnDate())
                        .extraPickupTime(getViewModel().getPickupTime())
                        .extraReturnTime(getViewModel().getReturnTime())
                        .extraDateSelectState(DatePickerViewModel.DATE_SELECT_STATE_PICKUP_DATE)
                        .extraEdit(true)
                        .build();
            }
            showModalForResult(getActivity(), datePicker, ItineraryActivity.DATE_RANGE_REQUEST_CODE);
        }

        @Override
        public void onSelectTimeClicked() {
            Fragment datePicker;
            if (getViewModel().getReturnLocation() == null) {
                datePicker = new DatePickerFragmentHelper.Builder().extraPickupLocation(getViewModel().getPickUpLocation())
                        .extraReturnLocation(getViewModel().getPickUpLocation())
                        .extraPickupDate(getViewModel().getPickupDate())
                        .extraReturnDate(getViewModel().getReturnDate())
                        .extraPickupTime(getViewModel().getPickupTime())
                        .extraReturnTime(getViewModel().getReturnTime())
                        .extraDateSelectState(DatePickerViewModel.DATE_SELECT_STATE_PICKUP_TIME)
                        .extraEdit(true)
                        .build();
            } else {
                datePicker = new DatePickerFragmentHelper.Builder().extraPickupLocation(getViewModel().getPickUpLocation())
                        .extraReturnLocation(getViewModel().getReturnLocation())
                        .extraPickupDate(getViewModel().getPickupDate())
                        .extraReturnDate(getViewModel().getReturnDate())
                        .extraPickupTime(getViewModel().getPickupTime())
                        .extraReturnTime(getViewModel().getReturnTime())
                        .extraDateSelectState(DatePickerViewModel.DATE_SELECT_STATE_PICKUP_TIME)
                        .extraEdit(true)
                        .build();
            }
            showModalForResult(getActivity(), datePicker, ItineraryActivity.DATE_RANGE_REQUEST_CODE);
        }
    };

    private DateTimeSelectorView.InteractionListener mReturnDateTimeChooserListener = new DateTimeSelectorView.InteractionListener() {
        @Override
        public void onSelectDateClicked() {
            Fragment datePicker;
            if (getViewModel().getReturnLocation() == null) {
                datePicker = new DatePickerFragmentHelper.Builder().extraPickupLocation(getViewModel().getPickUpLocation())
                        .extraReturnLocation(getViewModel().getPickUpLocation())
                        .extraPickupDate(getViewModel().getPickupDate())
                        .extraReturnDate(getViewModel().getReturnDate())
                        .extraPickupTime(getViewModel().getPickupTime())
                        .extraReturnTime(getViewModel().getReturnTime())
                        .extraDateSelectState(DatePickerViewModel.DATE_SELECT_STATE_RETURN_DATE)
                        .extraEdit(true)
                        .build();
            } else {
                datePicker = new DatePickerFragmentHelper.Builder().extraPickupLocation(getViewModel().getPickUpLocation())
                        .extraReturnLocation(getViewModel().getReturnLocation())
                        .extraPickupDate(getViewModel().getPickupDate())
                        .extraReturnDate(getViewModel().getReturnDate())
                        .extraPickupTime(getViewModel().getPickupTime())
                        .extraReturnTime(getViewModel().getReturnTime())
                        .extraDateSelectState(DatePickerViewModel.DATE_SELECT_STATE_RETURN_DATE)
                        .extraEdit(true)
                        .build();
            }
            showModalForResult(getActivity(), datePicker, ItineraryActivity.DATE_RANGE_REQUEST_CODE);
        }

        @Override
        public void onSelectTimeClicked() {
            Fragment datePicker;
            if (getViewModel().getReturnLocation() == null) {
                datePicker = new DatePickerFragmentHelper.Builder()
                        .extraPickupLocation(getViewModel().getPickUpLocation())
                        .extraReturnLocation(getViewModel().getPickUpLocation())
                        .extraPickupDate(getViewModel().getPickupDate())
                        .extraReturnDate(getViewModel().getReturnDate())
                        .extraPickupTime(getViewModel().getPickupTime())
                        .extraReturnTime(getViewModel().getReturnTime())
                        .extraDateSelectState(DatePickerViewModel.DATE_SELECT_STATE_RETURN_TIME)
                        .extraEdit(true)
                        .build();
            } else {
                datePicker = new DatePickerFragmentHelper.Builder()
                        .extraPickupLocation(getViewModel().getPickUpLocation())
                        .extraReturnLocation(getViewModel().getReturnLocation())
                        .extraPickupDate(getViewModel().getPickupDate())
                        .extraReturnDate(getViewModel().getReturnDate())
                        .extraPickupTime(getViewModel().getPickupTime())
                        .extraReturnTime(getViewModel().getReturnTime())
                        .extraDateSelectState(DatePickerViewModel.DATE_SELECT_STATE_RETURN_TIME)
                        .extraEdit(true)
                        .build();
            }
            showModalForResult(getActivity(), datePicker, ItineraryActivity.DATE_RANGE_REQUEST_CODE);
        }
    };
    // endregion

    //region renter age dialog click listner
    private DialogInterface.OnClickListener mDialogClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            getViewModel().setRenterAge(getViewModel().getAgeOptions().get(i).getValue());
        }
    };
    //endregion

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final ItineraryFragmentHelper.Extractor extractor = new ItineraryFragmentHelper.Extractor(this);
        getViewModel().setPickupTime(extractor.extraPickupTime());
        getViewModel().setPickupDate(extractor.extraPickupDate());
        getViewModel().setDropoffTime(extractor.extraReturnTime());
        getViewModel().setDropoffDate(extractor.extraReturnDate());
    }

    //region lifecycle
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        createViewBinding(inflater, R.layout.fr_itinerary, container);
        initViews();
        return getViewBinding().getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initDependencies();
        if (!getViewModel().isEdit()) {
            final ItineraryFragmentHelper.Extractor extractor = new ItineraryFragmentHelper.Extractor(this);
            getViewModel().setIsModify(extractor.isModify());
            if (extractor.extraAbandonedHolder() != null) {
                populateFromAbadonedReservation(extractor.extraAbandonedHolder());
            } else if (getViewModel().getPickUpLocation() == null) {
                getViewModel().setPickUpLocation(extractor.extraPickupLocation());
            }
            getViewModel().applyWeekendSpecial.setValue(getViewModel().getManagers().getReservationManager().isWeekendSpecial());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        EHIAnalyticsEvent.create()
                .screen(EHIAnalytics.Screen.SCREEN_RESERVATION.value, ItineraryFragment.SCREEN_NAME)
                .state(EHIAnalytics.State.STATE_WIDGET.value)
                .addDictionary(EHIAnalyticsDictionaryUtils.dateTime(getViewModel().getPickUpLocation(),
                        getViewModel().getReturnLocation(),
                        getViewModel().getPickupDateTime(),
                        getViewModel().getReturnDateTime()))
                .tagScreen()
                .tagEvent();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case ItineraryActivity.DATE_RANGE_REQUEST_CODE:
                    getViewModel().setPickupDate((Date) data.getSerializableExtra(DatePickerFragment.EXTRA_PICKUP_DATE));
                    getViewModel().setDropoffDate((Date) data.getSerializableExtra(DatePickerFragment.EXTRA_RETURN_DATE));
                    getViewModel().setPickupTime((Date) data.getSerializableExtra(DatePickerFragment.EXTRA_PICKUP_TIME));
                    getViewModel().setDropoffTime((Date) data.getSerializableExtra(DatePickerFragment.EXTRA_RETURN_TIME));
                    break;
                case ForceChangePasswordFragment.REQUEST_CODE:

                    break;
                case ItineraryFragment.EMERALD_CLUB_SIGN_IN_REQUEST_CODE:
                    if (data != null && data.getBooleanExtra(EmeraldClubSignInFragment.PASSWORD_RESET_REQUIRED, false)) {
                        showModalForResult(getActivity(), new ForceChangePasswordFragmentHelper.Builder().build(), ForceChangePasswordFragment.REQUEST_CODE);
                    } else {
                        getViewModel().setECWasLogedIn(true);
                    }
                    break;
                case ItineraryFragment.SIGN_IN_REQUEST_CODE:
                    if (getViewModel().isUserLoggedIn()) {
                        getViewModel().initiateReservation();
                    }
                    break;
                case TRIP_PURPOSE_RESULT:
                    getViewModel().setTripPurpose(data.getExtras().getString(TripPurposeFragment.TRIP_TYPE));
                    getViewModel().setTripPurposePreRate(true);
                    getViewModel().initiateReservation();
                    break;
                case PIN_RESULT:
                    getViewModel().setAuthPin(data.getExtras().getString(PreRatePinFragment.PIN));
                    getViewModel().initiateReservation();
                    break;
                case PRE_RATE_ADDITIONAL_FIELD_RESULT:
                    setPreRateAdditionalField(data);
                    break;
            }
        }
    }

    //endregion

    private void initViews() {
        getViewBinding().pickupLocationContainer.setOnClickListener(mOnClickListener);
        getViewBinding().returnLocationTextView.setOnClickListener(mOnClickListener);
        getViewBinding().addReturnLocationButton.setOnClickListener(mOnClickListener);
        getViewBinding().dropoffLockImage.setOnClickListener(mOnClickListener);
        getViewBinding().removeReturnLocation.setOnClickListener(mOnClickListener);
        getViewBinding().addCidButton.setOnClickListener(mOnClickListener);
        getViewBinding().clearCidButton.setOnClickListener(mOnClickListener);
        getViewBinding().buttonContinue.setOnClickListener(mOnClickListener);
        getViewBinding().cidInputArea.setOnClickListener(mOnClickListener);
        getViewBinding().ecSignInButton.setOnClickListener(mOnClickListener);

        getViewBinding().selectPickupDateView.setOnClickListener(mOnClickListener);
        getViewBinding().selectPickupDateView.setTimeCalloutText(R.string.reservation_scheduler_pickup_time_callout);
        getViewBinding().selectPickupDateView.setInteractionListener(mPickupDateTimeChooserListener);

        getViewBinding().selectReturnDateView.setOnClickListener(mOnClickListener);
        getViewBinding().selectReturnDateView.setTimeCalloutText(R.string.reservation_scheduler_return_time_callout);

        getViewBinding().selectReturnDateView.setInteractionListener(mReturnDateTimeChooserListener);

        getViewBinding().driverAgeSpinner.setCallback(mDialogClickListener);

        getViewBinding().cidEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (event != null
                        && v.getText().toString().trim().length() == 0
                        && ((event.getKeyCode() == KeyEvent.KEYCODE_ENTER) || (actionId == EditorInfo.IME_ACTION_DONE))) {
                    getViewModel().clearCidButtonClicked();
                } else {
                    getViewBinding().cidEditText.clearFocus();
                    getViewBinding().parentView.requestFocus();
                }

                BaseAppUtils.hideKeyboard(getActivity());
                return false;
            }
        });

        getViewBinding().ecAdded.setOnRemoveListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(getActivity())
                        .setTitle(R.string.alert_remove_emerald_club_title)
                        .setMessage(R.string.alert_remove_emerald_club_message)
                        .setPositiveButton(R.string.standard_button_yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(final DialogInterface dialog, final int which) {
                                getViewModel().removeEmeraldClubAccount();
                            }
                        })
                        .setNegativeButton(R.string.standard_button_no, null)
                        .show();
            }
        });
    }

    @Override
    protected void initDependencies() {
        super.initDependencies();
        bind(FragmentUtils.progress(getViewModel().progress, getActivity()));
        bind(FragmentUtils.progressDefinite(getViewModel().determinateLoader, getActivity()));
        bind(DialogUtils.errorDialog(getViewModel().errorResponse, getActivity()));
        bind(ReactorActivity.titleRes(getViewModel().titleResource, getActivity()));
        bind(ToastUtils.toastRes(getViewModel().ecToast, getActivity()));
        bind(DialogUtils.bindMessage(getViewModel().clientErrorDialogDialogTextRes, getActivity()));

        //location
        bind(ReactorTextView.textRes(getViewModel().pickupLocationHeader.textRes(), getViewBinding().pickupLocationHeader));
        bind(ReactorTextView.text(getViewModel().pickupLocationTextView.text(), getViewBinding().pickupLocationTextView));
        bind(ReactorTextView.drawableRight(getViewModel().pickupLocationTextView.drawableRight(), getViewBinding().pickupLocationTextView));
        bind(ReactorTextView.drawableLeft(getViewModel().pickupLocationTextView.drawableLeft(), getViewBinding().pickupLocationTextView));
        bind(ReactorTextView.compoundDrawablePaddingInDp(getViewModel().pickupLocationTextView.compoundDrawablePaddingInDp(), getViewBinding().pickupLocationTextView));

        bind(ReactorView.visibility(getViewModel().returnLocationHeader.visibility(), getViewBinding().returnLocationHeader));
        bind(ReactorView.visibility(getViewModel().addReturnLocationButton.visibility(), getViewBinding().addReturnLocationButton));
        bind(ReactorView.visibility(getViewModel().removeReturnLocation.visibility(), getViewBinding().removeReturnLocation));
        bind(ReactorTextView.text(getViewModel().returnLocationTextView.text(), getViewBinding().returnLocationTextView));
        bind(ReactorView.visibility(getViewModel().returnLocationTextView.visibility(), getViewBinding().returnLocationTextView));
        bind(ReactorTextView.drawableLeft(getViewModel().returnLocationTextView.drawableLeft(), getViewBinding().returnLocationTextView));
        bind(ReactorTextView.compoundDrawablePaddingInDp(getViewModel().returnLocationTextView.compoundDrawablePaddingInDp(), getViewBinding().returnLocationTextView));

        bind(ReactorView.visibility(getViewModel().pickupLockImage.visibility(), getViewBinding().pickupLockImage));

        bind(ReactorView.visibility(getViewModel().dropOffLockImage.visibility(), getViewBinding().dropoffLockImage));
        bind(ReactorView.backgroundRes(getViewModel().dropOffContainer.backgroundResource(), getViewBinding().dropoffContainer));

        //date
        bind(DateTimeSelectorView.bind(getViewModel().selectPickupDateView, getViewBinding().selectPickupDateView));
        bind(DateTimeSelectorView.bind(getViewModel().selectReturnDateView, getViewBinding().selectReturnDateView));

        //age
        bind(ReactorView.visibility(getViewModel().driversAgeHeader.visibility(), getViewBinding().driversAgeHeader));
        bind(ReactorView.visibility(getViewModel().driversAgeSpinner.visibility(), getViewBinding().driverAgeSpinner));
        bind(ReactorView.enabled(getViewModel().driversAgeSpinner.enabled(), getViewBinding().driverAgeSpinner));
        bind(EHISpinnerView.bindPopulateMethodBox(getViewModel().driversAgeSpinner, getViewBinding().driverAgeSpinner));

        //contract from profile
        bind(ReactorView.visibility(getViewModel().contractFromProfileContainer.visibility(), getViewBinding().contractFromProfileContainer));
        bind(ReactorView.alpha(getViewModel().contractFromProfileContainer.alpha(), getViewBinding().contractFromProfileContainer));
        bind(ReactorTextView.text(getViewModel().contractFromProfileName.text(), getViewBinding().contractFromProfileName));
        bind(ReactorCompoundButton.bindChecked(getViewModel().contractFromProfileSwitch.checked(), getViewBinding().contractFromProfileSwitch));
        bind(ReactorView.enabled(getViewModel().contractFromProfileSwitch.enabled(), getViewBinding().contractFromProfileSwitch));

        bind(ReactorView.visibility(getViewModel().ecSignInButton.visibility(), getViewBinding().ecSignInButton));
        bind(ReactorView.visibility(getViewModel().ecEnabled.visibility(), getViewBinding().ecAdded));

        //cid
        bind(ReactorTextView.bindText(getViewModel().cidEditText.text(), getViewBinding().cidEditText));
        bind(ReactorView.enabled(getViewModel().cidEditText.enabled(), getViewBinding().cidEditText));
        bind(ReactorView.enabled(getViewModel().addCidButton.enabled(), getViewBinding().addCidButton));
        bind(ReactorView.visibility(getViewModel().addCidButton.visibility(), getViewBinding().addCidButton));
        bind(ReactorTextView.textColor(getViewModel().addCidButton.textColor(), getViewBinding().addCidButton));
        bind(ReactorTextView.drawableLeft(getViewModel().addCidButton.drawableLeft(), getViewBinding().addCidButton));
        bind(ReactorView.visibility(getViewModel().cidInputHeader.visibility(), getViewBinding().cidInputHeader));
        bind(ReactorView.visibility(getViewModel().cidInputArea.visibility(), getViewBinding().cidInputArea));
        bind(ReactorView.visibility(getViewModel().cidContainer.visibility(), getViewBinding().cidContainer));
        bind(ReactorView.visibility(getViewModel().clearCidButton.visibility(), getViewBinding().clearCidButton));

        bind(ReactorTextView.textRes(getViewModel().continueButton.textRes(), getViewBinding().buttonContinue));
        bind(ReactorView.enabled(getViewModel().continueButton.enabled(), getViewBinding().buttonContinue));

        addReaction(new ReactorComputationFunction() {
            @Override
            public void react(final ReactorComputation reactorComputation) {
                if (getViewModel().applyWeekendSpecial.getValue() && !getViewModel().isWeekendSpecialApplied()) {
                    EHIContract contract = getViewModel().getWeekendSpecialContract();
                    String promoTitle = contract == null ? "" : contract.getContractName();
                    getViewBinding().itemApplied.setTitle(getString(R.string.reservation_promotion_applied) + ":\n" + promoTitle);
                    getViewBinding().itemApplied.setOnRemoveListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            getViewModel().addCidButton.setVisibility(View.VISIBLE);
                            getViewBinding().itemApplied.setVisibility(View.GONE);
                            getViewModel().setWeekendSpecialApplied(false);
                            getViewModel().applyWeekendSpecial.setValue(false);
                            getViewModel().getManagers().getReservationManager().setWeekendSpecial(false);
                            getViewModel().updateContractFromProfileViewState();
                        }
                    });
                    getViewBinding().itemApplied.setVisibility(View.VISIBLE);
                    getViewModel().setWeekendSpecialApplied(true);
                    getViewModel().addCidButton.setVisibility(View.GONE);
                    getViewModel().updateContractFromProfileViewState();
                }
            }
        });


        addReaction("INITIATE_REACTION", new ReactorComputationFunction() {
            @Override
            public void react(ReactorComputation reactorComputation) {
                final EHIReservation initiateResponse = getViewModel().getInitiateResponse();
                if (initiateResponse != null) {
                    if (!getViewModel().isLocationSoldOut(initiateResponse.getCarClasses())
                            || initiateResponse.getCarClasses() == null              //second and third condition is for modify flow
                            || initiateResponse.getCarClasses().size() == 0) {        // will fetch available for modify car car classes on next page
                        ((ReservationFlowListener) getActivity()).showAvailableCarClasses(false);
                    } else {
                        showSoldOutDialog();
                    }
                    getViewModel().resetInitiateResponse();
                }
            }
        });

        addReaction("AUTHENTICATION_REQUIRED_REACTION", new ReactorComputationFunction() {
            @Override
            public void react(ReactorComputation reactorComputation) {
                if (getViewModel().requiresAuthentication()) {
                    EHIAnalyticsEvent.create()
                            .screen(EHIAnalytics.Screen.SCREEN_CORP_RES.value, ItineraryActivity.SCREEN_NAME)
                            .state(EHIAnalytics.State.STATE_LOGIN_MODAL.value)
                            .addDictionary(EHIAnalyticsDictionaryUtils.reservation())
                            .tagScreen()
                            .tagEvent();
                    getViewModel().setRequiresAuthentication(false);
                    showModalForResult(
                            getActivity(),
                            new LoginFragmentHelper.Builder().message(getResources().getString(R.string.sign_in_corp_flow_header_login_error)).build(),
                            SIGN_IN_REQUEST_CODE
                    );
                }
            }
        });

        addReaction("TRIP_PURPOSE_REQUIRED_REACTION", new ReactorComputationFunction() {
            @Override
            public void react(ReactorComputation reactorComputation) {
                if (getViewModel().getRequiresTravelPurpose()) {
                    EHIAnalyticsEvent.create()
                            .screen(EHIAnalytics.Screen.SCREEN_CORP_RES.value, ItineraryActivity.SCREEN_NAME)
                            .state(EHIAnalytics.State.STATE_PURPOSE_MODAL.value)
                            .addDictionary(EHIAnalyticsDictionaryUtils.reservation())
                            .tagScreen()
                            .tagEvent();
                    getViewModel().setRequiresTravelPurpose(false);
                    showModalDialogForResult(
                            getActivity(),
                            new TripPurposeFragmentHelper.Builder().build(),
                            TRIP_PURPOSE_RESULT
                    );
                }
            }
        });

        addReaction("PRE_RATE_ADDITIONAL_FIELD_REQUIRED_REACTION", new ReactorComputationFunction() {
            @Override
            public void react(ReactorComputation reactorComputation) {
                if (getViewModel().getRequiresPreRateAdditionalField()) {
                    AdditionalInfoFragmentHelper.Builder builder = new AdditionalInfoFragmentHelper.Builder();

                    builder.extraPreRate(true);
                    builder.extraContractNumber(getViewModel().getCidForInitiate());

                    if (getViewModel().getEhiAdditionalInformationList() != null) {
                        builder.extraAdditionalInfo(getViewModel().getEhiAdditionalInformationList());
                    }

                    if (getViewModel().getPreRateErrorMessage() != null) {
                        builder.extraErrorMessage(getViewModel().getPreRateErrorMessage());
                        getViewModel().clearPreRateErrorMessage();
                    }

                    AdditionalInfoFragment fragment = builder.build();

                    getViewModel().setRequiresPreRateAdditionalField(false);
                    showModalForResult(
                            getActivity(),
                            fragment,
                            PRE_RATE_ADDITIONAL_FIELD_RESULT
                    );
                }
            }
        });

        addReaction(new ReactorComputationFunction() {
            @Override
            public void react(ReactorComputation reactorComputation) {
                if (getViewModel().getRequiresPin()) {
                    getViewModel().setRequiresPin(false);
                    showModalForResult(getActivity(), new PreRatePinFragmentHelper.Builder().build(), PIN_RESULT);
                }
            }
        });

        addReaction(new ReactorComputationFunction() {
            @Override
            public void react(ReactorComputation reactorComputation) {
                if (getViewModel().getWrongPin()) {
                    getViewModel().setWrongPin(false);
                    showModalForResult(getActivity(),
                            new PreRatePinFragmentHelper.Builder()
                                    .extraErrorMessage(getViewModel().getPreRateErrorMessage())
                                    .extraPin(getViewModel().getAuthPin())
                                    .build(),
                            PIN_RESULT);
                }
            }
        });

        addReaction("BUSINESS_LEISURE_NOT_ON_PROFILE", new ReactorComputationFunction() {
            @Override
            public void react(ReactorComputation reactorComputation) {
                if (getViewModel().getCodeNotOnProfile()) {
                    EHIAnalyticsEvent.create()
                            .screen(EHIAnalytics.Screen.SCREEN_CORP_RES.value, ItineraryActivity.SCREEN_NAME)
                            .state(EHIAnalytics.State.STATE_PROFILE_ERROR_MODAL.value)
                            .addDictionary(EHIAnalyticsDictionaryUtils.reservation())
                            .tagScreen()
                            .tagEvent();
                    getViewModel().setCodeNotOnProfile(false);
                    showModalDialog(getActivity(), new CodeNotAttachedFragmentHelper.Builder().build());
                }
            }
        });

        addReaction(new ReactorComputationFunction() {
            @Override
            public void react(ReactorComputation reactorComputation) {
                if (getViewModel().getInvalidAuthToken()) {
                    getActivity().finish();
                }
            }
        });
    }

    private void populateFromAbadonedReservation(ReservationInformation reservationInformation) {
        getViewModel().populateFromReservationInformation(reservationInformation);
    }

    public void setPickupLocation(EHISolrLocation pickupLocation) {
        getViewModel().setPickUpLocation(pickupLocation);
    }

    public void setReturnLocation(EHISolrLocation dropoffLocation) {
        getViewModel().setReturnLocation(dropoffLocation);
    }

    public void clearReturnLocation() {
        getViewModel().clearReturnLocation();
    }

    public void setEdited() {
        getViewModel().setEdit(true);
    }

    private void showSoldOutDialog() {
        new AlertDialog.Builder(getActivity())
                .setTitle(R.string.info_modal_empty_reservation_title)
                .setMessage(getString(R.string.info_modal_empty_reservation_details))
                .setNegativeButton(getString(R.string.standard_ok_text), null)
                .setCancelable(false)
                .create()
                .show();
    }

    private void setPreRateAdditionalField(Intent data) {
        EHIBundle ehiBundle = new EHIBundle(
                data.getBundleExtra(AdditionalInfoFragment.EXTRA_DATA)
        );

        getViewModel().setEhiAdditionalInformationList(
                ehiBundle.<List<EHIAdditionalInformation>>getEHIModel(
                        AdditionalInfoFragment.EXTRA_ADDITIONAL_INFO,
                        new TypeToken<List<EHIAdditionalInformation>>() {
                        }.getType()
                )
        );

        getViewModel().initiateReservation();
    }

    public void updateDatesFromFlow(@SearchLocationsActivity.Flow int flow, Date pickupDate, Date dropoffDate, Date pickupTime, Date dropoffTime) {
        getViewModel().updateDatesFromFlow(flow, pickupDate, dropoffDate, pickupTime, dropoffTime);
    }
}
