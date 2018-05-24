package com.ehi.enterprise.android.ui.reservation.widget;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.content.res.ResourcesCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.util.AttributeSet;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.databinding.FlightDetailsViewBinding;
import com.ehi.enterprise.android.models.reservation.EHIAirlineDetails;
import com.ehi.enterprise.android.ui.view.DataBindingViewModelView;
import com.ehi.enterprise.android.utils.CustomTypefaceSpan;
import com.isobar.android.viewmodel.ViewModel;

import io.dwak.reactorbinding.view.ReactorView;
import io.dwak.reactorbinding.widget.ReactorTextView;

@ViewModel(FlightDetailsViewViewModel.class)
public class FlightDetailsView extends DataBindingViewModelView<FlightDetailsViewViewModel, FlightDetailsViewBinding> {

    //region constructors
    public FlightDetailsView(Context context) {
        this(context, null, 0);
    }

    public FlightDetailsView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FlightDetailsView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        if (isInEditMode()) {
            addView(inflate(context, R.layout.v_flight_details, null));
            return;
        }

        createViewBinding(R.layout.v_flight_details);
        setAddFlightDetailsTitle();
    }
    //endregion

    @Override
    protected void initDependencies() {
        super.initDependencies();
        bind(ReactorTextView.text(getViewModel().currentFlightDetails.text(), getViewBinding().currentFlightAirline));
        bind(ReactorTextView.text(getViewModel().currentFlightNumber.text(), getViewBinding().currentFlightNumber));
        bind(ReactorView.visible(getViewModel().currentFlightNumber.visible(), getViewBinding().currentFlightNumber));
        bind(ReactorTextView.textRes(getViewModel().currentFlightDetails.textRes(), getViewBinding().currentFlightAirline));
        bind(ReactorView.visible(getViewModel().currentFlightDetailsContainer.visible(), getViewBinding().currentFlightDetailsContainer));
        bind(ReactorView.visible(getViewModel().addFlightDetailsContainer.visible(), getViewBinding().addFlightContainer));
        bind(ReactorView.visible(getViewModel().rootView.visible(), getViewBinding().getRoot()));
        bind(ReactorView.visibility(getViewModel().greenArrow.visibility(), getViewBinding().chevron));
    }

    public void setCurrentFlightDetails(final EHIAirlineDetails value, final String flightNumber, boolean isMultiTerminal) {
        getViewModel().setCurrentFlightDetails(value, flightNumber, isMultiTerminal);
    }

    public void setExternalClickListener(OnClickListener externalClickListener) {
        getViewBinding().getRoot().setOnClickListener(externalClickListener);
    }

    private void setAddFlightDetailsTitle() {
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();

        Typeface typeface = ResourcesCompat.getFont(getContext(), R.font.source_sans_bold);
        SpannableString spannableString = new SpannableString(getResources().getText(R.string.reservation_flight_details_add_button_title));
        spannableString.setSpan(
                new CustomTypefaceSpan("", typeface), 0, spannableString.toString().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        );

        spannableStringBuilder.append(spannableString);

        spannableStringBuilder.append(" ");

        spannableStringBuilder.append(getResources().getText(R.string.additional_info_header_optional));

        getViewBinding().addFlightTitle.setText(spannableStringBuilder);
    }

    public void hideGreenArrow() {
        getViewModel().hideGreenArrow();
    }

    public void showGreenArrow() {
        getViewModel().showGreenArrow();
    }

    public void setVisibilityForContent() {
        getViewModel().setVisibilityForContent();
    }

}
