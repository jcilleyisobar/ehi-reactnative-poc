package com.ehi.enterprise.android.ui.reservation.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.models.reservation.EHICarClassDetails;
import com.ehi.enterprise.android.models.reservation.EHIMileageInfo;
import com.ehi.enterprise.android.models.reservation.EHIPaymentLineItem;
import com.ehi.enterprise.android.models.reservation.EHIPriceSummary;
import com.ehi.enterprise.android.models.reservation.EHIReservation;
import com.ehi.enterprise.android.ui.viewmodel.ManagersAccessViewModel;
import com.isobar.android.viewmodel.ViewModel;

import java.util.LinkedList;
import java.util.List;

@ViewModel(ManagersAccessViewModel.class)
public class RentalCollapsibleView extends PriceSummaryBasedCollapsibleView<ManagersAccessViewModel> {

    private EHICarClassDetails ehiCarClassDetails;

    public RentalCollapsibleView(Context context) {
        super(context);
    }

    public RentalCollapsibleView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RentalCollapsibleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setReservation(EHIReservation ehiReservation, boolean isPrepay) {
        ehiCarClassDetails = ehiReservation.getCarClassDetails();
        super.setReservation(ehiReservation, isPrepay);
    }

    @Override
    public void setPriceSummary(EHIPriceSummary ehiPriceSummary) {
        setPriceSummary(ehiPriceSummary, R.string.price_section_title_rental);

        if (ehiPriceSummary != null && ehiPriceSummary.getEstimatedTotalVehicleView() != null) {
            if (ehiCarClassDetails.isSecretRateAfterCarSelected()) {
                getViewBinding().mainSubtitle.setText(getResources().getString(R.string.payment_line_item_included));
            } else {
                getViewBinding().mainSubtitle.setText(
                        ehiPriceSummary.getEstimatedTotalVehicleView().getFormattedPrice(false)
                );
            }
        }

        // I want to believe that we'll always have at least one item in the
        // payment line collection for rental
        final EHIMileageInfo ehiMileageInfo = ehiCarClassDetails.getMileageInfo();
        if (!ehiMileageInfo.isUnlimitedMileage()) {
            final CollapsibleItemView collapsibleItemView = new CollapsibleItemView(getContext());

            final String formattedInfo = "(" + ehiMileageInfo.getTotalFreeMiles()
                    + " " + ehiMileageInfo.getDistanceUnit()
                    + " - " + ehiMileageInfo.getExcessMileageRateView().getFormattedPrice(true)
                    + " / " + getResources().getString(R.string.price_section_mileage_additional)
                    + " " + ehiMileageInfo.getDistanceUnit()
                    + ")";

            collapsibleItemView.setInfo(
                    getResources().getString(R.string.reservation_line_item_mileage_title),
                    formattedInfo
            );

            collapsibleItemView.setValue(
                    getResources().getString(R.string.payment_line_item_included)
            );

            getViewBinding().childContainer.addView(collapsibleItemView);
        }

    }

    @NonNull
    @Override
    protected List<EHIPaymentLineItem> getLineItems(EHIPriceSummary priceSummary) {
        final List<EHIPaymentLineItem> validRentals = new LinkedList<>();
        if (priceSummary == null) {
            return validRentals;
        }

        final List<EHIPaymentLineItem> rentals = priceSummary.getRentalPaymentItems();
        if (rentals != null) {
            final Double zeroedQuantity = 0.0;
            for (final EHIPaymentLineItem rentalItem : rentals) {
                if (!zeroedQuantity.equals(rentalItem.getRateQuantity())) {
                    validRentals.add(rentalItem);
                }
            }
        }

        return validRentals;
    }

    @NonNull
    @Override
    protected CollapsibleItemView getCollapsibleItemView(EHIPaymentLineItem lineItem) {
        final CollapsibleItemView collapsibleItemView = new CollapsibleItemView(getContext());

        collapsibleItemView.setInfo(
                lineItem.getRentalAmountText(getResources()),
                lineItem.getRentalRateText(getResources())
        );

        collapsibleItemView.setValue(lineItem.getTotalAmountView().getFormattedPrice(false).toString());

        return collapsibleItemView;
    }
}
