package com.ehi.enterprise.android.ui.reservation;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.databinding.FlightDetailsFragmentBinding;
import com.ehi.enterprise.android.models.reservation.EHIAirlineDetails;
import com.ehi.enterprise.android.ui.fragment.DataBindingViewModelFragment;
import com.ehi.enterprise.android.ui.reservation.interfaces.ReservationFlowListener;
import com.ehi.enterprise.android.utils.EHIBundle;
import com.ehi.enterprise.android.utils.ToastUtils;
import com.ehi.enterprise.android.utils.analytics.EHIAnalytics;
import com.ehi.enterprise.android.utils.analytics.EHIAnalyticsDictionaryUtils;
import com.ehi.enterprise.android.utils.analytics.EHIAnalyticsEvent;
import com.isobar.android.newinstancer.Extra;
import com.isobar.android.viewmodel.ViewModel;

import java.util.List;

import io.dwak.reactorbinding.view.ReactorView;
import io.dwak.reactorbinding.widget.ReactorTextInputLayout;
import io.dwak.reactorbinding.widget.ReactorTextView;

@ViewModel(FlightDetailViewModel.class)
public class FlightDetailsFragment extends DataBindingViewModelFragment<FlightDetailViewModel, FlightDetailsFragmentBinding> {

    public static final String SCREEN_NAME = "FlightDetailsFragment";
    public static final String TAG = "FlightDetailsFragment";

    @Extra(value = List.class, type = EHIAirlineDetails.class)
    public static final String FLIGHT_DETAILS = "EXTRA_FLIGHT_DETAILS";
    @Extra(value = String.class, required = false)
    public static final String FLIGHT_NUMBER = "EXTRA_FLIGHT_NUMBER";
    @Extra(value = EHIAirlineDetails.class, required = false)
    public static final String SELECTED_AIRLINE = "EXTRA_SELECTED_AIRLINE";
    @Extra(value = boolean.class)
    public static final String MULTI_TERMINAL = "EXTRA_MULTI_TERMINAL";
    @Extra(boolean.class)
    public static final String IS_MODIFY = "ehi.EXTRA_IS_MODIFY";
    @Extra(boolean.class)
    public static String IS_EDITING = "IS_EDITING";

    public static final String KEY_FLIGHT_NUMBER = "KEY_FLIGHT_NUMBER";
    public static final String KEY_AIRLINE_DETAILS = "KEY_AIRLINE_DETAILS";

    public static final int REQUEST_CODE_FLIGHT_DETAILS = 1001;
    private static final int REQUEST_CODE_SEARCH_AIRLINES = 1002;

    //region onClickListener
    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view == getViewBinding().submitButton) {
                saveAndClose();

                EHIAnalyticsEvent.create()
                        .screen(EHIAnalytics.Screen.SCREEN_RESERVATION.value, FlightDetailsFragment.SCREEN_NAME)
                        .state(EHIAnalytics.State.STATE_FLIGHT_INFO.value)
                        .action(EHIAnalytics.Motion.MOTION_TAP.value, EHIAnalytics.Action.ACTION_FLIGHT_INFO_SAVED.value)
                        .addDictionary(EHIAnalyticsDictionaryUtils.review())
                        .tagScreen()
                        .tagEvent();
            } else if (view == getViewBinding().noFlightButton) {
                getViewModel().setFlightNumber(null);
                getViewModel().setSelectedAirline(getViewModel().getWalkInDetails());
                saveAndClose();

                EHIAnalyticsEvent.create()
                        .screen(EHIAnalytics.Screen.SCREEN_RESERVATION.value, FlightDetailsFragment.SCREEN_NAME)
                        .state(EHIAnalytics.State.STATE_FLIGHT_INFO.value)
                        .action(EHIAnalytics.Motion.MOTION_TAP.value, EHIAnalytics.Action.ACTION_NO_FLIGHT_INFO.value)
                        .addDictionary(EHIAnalyticsDictionaryUtils.review())
                        .tagScreen()
                        .tagEvent();
            } else if (view == getViewBinding().airlineDescription) {
                showModalWithSearchHeaderForResult(getActivity(),
                        new SearchAirlinesFragmentHelper.Builder().flightDetails(getViewModel().getAirlines()).build(),
                        REQUEST_CODE_SEARCH_AIRLINES);
            }
        }
    };

    private View.OnClickListener mOnSubmitDisabledClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            getViewModel().highlightInvalidFields();
            ToastUtils.showToast(getContext(), R.string.flight_details_no_flight_toast);
        }
    };
    //endregion

    private void saveAndClose() {
        getViewModel().saveCurrentlySelectedDetails();

        if (getViewModel().isEditing()) {
            getActivity().setResult(Activity.RESULT_OK);
            getActivity().finish();
        } else {
            ((ReservationFlowListener) getActivity()).showReview();
        }
    }

    //region lifecycle
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            final FlightDetailsFragmentHelper.Extractor extractor = new FlightDetailsFragmentHelper.Extractor(this);
            getViewModel().setIsModify(extractor.isModify());
            getViewModel().setIsEditing(extractor.isEditing());

            getViewModel().setAirlines(extractor.flightDetails());

            if (extractor.selectedAirline() != null) {
                getViewModel().setSelectedAirline(extractor.selectedAirline());
                getViewModel().setFlightNumber(extractor.flightNumber());
            }
            getViewModel().setIsMultiTerminal(extractor.multiTerminal());
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstance) {
        super.onActivityCreated(savedInstance);
        getActivity().setTitle(R.string.flight_details_screen_title);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        createViewBinding(inflater, R.layout.fr_flight_details, container);
        initView();
        return getViewBinding().getRoot();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_SEARCH_AIRLINES && resultCode == Activity.RESULT_OK) {
            EHIAirlineDetails details = EHIBundle.fromBundle(data.getExtras()).getEHIModel(FlightDetailsFragment.KEY_AIRLINE_DETAILS, EHIAirlineDetails.class);
            getViewModel().setSelectedAirline(details);
        }
    }
    //endregion

    private void initView() {
        getViewBinding().airlineDescription.setOnClickListener(mOnClickListener);
        getViewBinding().submitButton.setOnClickListener(mOnClickListener);
        getViewBinding().submitButton.setOnDisabledClickListener(mOnSubmitDisabledClickListener);
        getViewBinding().noFlightButton.setOnClickListener(mOnClickListener);

        if (getViewModel().isMultiTerminal()) {
            getViewBinding().flightNumberOptionalTag.setVisibility(View.VISIBLE);
            getViewBinding().flightDetailsTitle.setText(getString(R.string.flight_details_help_description_multi_terminal));
        }

        getViewModel().syncToReservation();
    }

    @Override
    protected void initDependencies() {
        super.initDependencies();
        bind(ReactorTextView.text(getViewModel().airlineDescription.text(), getViewBinding().airlineDescription));
        bind(ReactorTextView.textRes(getViewModel().airlineDescription.textRes(), getViewBinding().airlineDescription));
        bind(ReactorTextView.bindText(getViewModel().flightNumber, getViewBinding().flightNumber));
        bind(ReactorView.enabled(getViewModel().submitButton.enabled(), getViewBinding().submitButton));
        bind(ReactorTextInputLayout.error(getViewModel().airlineDescriptionError, getViewBinding().airlineDescriptionLayout));
    }
}