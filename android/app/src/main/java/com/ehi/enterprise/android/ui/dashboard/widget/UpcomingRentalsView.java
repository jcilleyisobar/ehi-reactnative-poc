package com.ehi.enterprise.android.ui.dashboard.widget;

import android.content.Context;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.view.View;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.databinding.DashboardUpcomingRentalBinding;
import com.ehi.enterprise.android.models.location.EHILocation;
import com.ehi.enterprise.android.models.location.EHIWayfindingStep;
import com.ehi.enterprise.android.models.reservation.EHITripSummary;
import com.ehi.enterprise.android.ui.view.DataBindingViewModelView;
import com.ehi.enterprise.android.utils.DisplayUtils;
import com.ehi.enterprise.android.utils.image.EHIImageLoader;
import com.isobar.android.viewmodel.ViewModel;

import java.util.List;

import io.dwak.reactor.Reactor;
import io.dwak.reactor.ReactorComputation;
import io.dwak.reactor.interfaces.ReactorComputationFunction;
import io.dwak.reactorbinding.view.ReactorView;
import io.dwak.reactorbinding.widget.ReactorImageView;
import io.dwak.reactorbinding.widget.ReactorTextView;

@ViewModel(UpcomingRentalsViewModel.class)
public class UpcomingRentalsView extends DataBindingViewModelView<UpcomingRentalsViewModel, DashboardUpcomingRentalBinding> {

    private UpcomingRentalsListener mUpcomingRentalsListener;

    //region onClickListener
    private OnClickListener mOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v == getViewBinding().viewDetailsButton || v == getViewBinding().upcomingRentalHeaderContainer) {
                mUpcomingRentalsListener.onViewDetailsClicked(getViewModel().getTripSummary());
            } else if (v == getViewBinding().getDirectionsButton) {
                mUpcomingRentalsListener.onGetDirectionsClicked(getViewModel().getTripSummary());
            } else if (v == getViewBinding().rentalPickupLocation) {
                if (mUpcomingRentalsListener != null) {
                    mUpcomingRentalsListener.onLocationNameClicked(getViewModel().getTripSummary().getPickupLocation());
                }
            } else if (v == getViewBinding().upcomingRentalDirectionFromTerminal) {
                if (mUpcomingRentalsListener != null) {
                    mUpcomingRentalsListener.onDirectionsFromTerminalClicked(getViewModel().getWayfindings());
                }
            }
        }
    };
    //endregion

    //region constructors
    public UpcomingRentalsView(Context context) {
        this(context, null);
    }

    public UpcomingRentalsView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public UpcomingRentalsView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        createViewBinding(R.layout.v_dashboard_upcoming_rental);
        initViews();
    }
    //endregion


    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        getViewBinding().upcomingRentalHeaderContainer.measure(MeasureSpec.makeMeasureSpec(DisplayUtils.getScreenWidth(getContext()) / 2, MeasureSpec.AT_MOST),
                MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
        getViewBinding().vehicleImage.setMaxHeight(getViewBinding().upcomingRentalHeaderContainer.getMeasuredHeight());
    }

    private void initViews() {
        getViewBinding().rentalPickupLocation.setOnClickListener(mOnClickListener);
        getViewBinding().upcomingRentalHeaderContainer.setOnClickListener(mOnClickListener);
        getViewBinding().upcomingRentalDirectionFromTerminal.setOnClickListener(mOnClickListener);
        getViewBinding().getDirectionsButton.setOnClickListener(mOnClickListener);
        getViewBinding().viewDetailsButton.setOnClickListener(mOnClickListener);
    }

    @Override
    protected void initDependencies() {
        super.initDependencies();

        bind(ReactorTextView.text(getViewModel().seeYouMessage.textCharSequence(), getViewBinding().statusMessage));
        bind(ReactorTextView.text(getViewModel().confirmationNumber.text(), getViewBinding().confirmationNumber));
        bind(EHIImageLoader.imageByType(getViewModel().vehicleImageUrls, getViewBinding().vehicleImage, getViewModel().vehicleImageType));
        bind(ReactorImageView.imageResource(getViewModel().locationPin.imageResource(), getViewBinding().locationPin));
        bind(ReactorTextView.text(getViewModel().pickupDateTime.textCharSequence(), getViewBinding().rentalPickupDateTime));
        bind(ReactorView.visibility(getViewModel().locationPin.visibility(), getViewBinding().locationPin));
        bind(ReactorView.visibility(getViewModel().upcomingRentalsDirectionsFromTerminal.visibility(), getViewBinding().upcomingRentalDirectionFromTerminal));
        addReaction(new ReactorComputationFunction() {
            @Override
            public void react(final ReactorComputation reactorComputation) {
                if (getViewModel().getRentalPickupLocationName() != null) {
                    SpannableStringBuilder builder = new SpannableStringBuilder(getViewModel().getRentalPickupLocationName());
                    if (getViewModel().getRentalPickupAirportCode() != null) {
                        int length = builder.length();
                        builder.append(" ")
                                .append(getViewModel().getRentalPickupAirportCode())
                                .setSpan(new ForegroundColorSpan(getResources().getColor(R.color.ehi_black)),
                                        length,
                                        builder.length(),
                                        Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                    }

                    getViewBinding().rentalPickupLocation.setText(builder);
                }
            }
        });
    }

    //region setters
    public void setTrip(EHITripSummary tripSummary) {
        getViewModel().setTripSummary(tripSummary);
    }

    public void setWayfindings(List<EHIWayfindingStep> wayfindings) {
        getViewModel().setWayfindings(wayfindings);
    }

    // endregion

    public void setUpcomingRentalsListener(UpcomingRentalsListener upcomingRentalsListener) {
        mUpcomingRentalsListener = upcomingRentalsListener;
    }

    public interface UpcomingRentalsListener {
        void onGetDirectionsClicked(EHITripSummary ehiTripSummary);

        void onViewDetailsClicked(EHITripSummary ehiTripSummary);

        void onLocationNameClicked(EHILocation location);

        void onDirectionsFromTerminalClicked(List<EHIWayfindingStep> wayfindings);
    }

}
