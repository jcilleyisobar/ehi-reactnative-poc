package com.ehi.enterprise.android.ui.reservation.history.viewholder;

import android.content.Context;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.View;
import android.view.ViewGroup;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.app.EHIStringToken;
import com.ehi.enterprise.android.databinding.MyRentalsPastTripItemBinding;
import com.ehi.enterprise.android.models.reservation.EHIPriceSummary;
import com.ehi.enterprise.android.models.reservation.EHITripSummary;
import com.ehi.enterprise.android.models.reservation.EHIVehicleDetails;
import com.ehi.enterprise.android.ui.reservation.history.MyRentalsRecyclerAdapter;
import com.ehi.enterprise.android.ui.viewholder.DataBindingViewHolder;
import com.isobar.android.tokenizedstring.TokenizedString;

public class MyRentalsPastTripViewHolder extends DataBindingViewHolder<MyRentalsPastTripItemBinding> {

    protected MyRentalsPastTripViewHolder(final MyRentalsPastTripItemBinding viewBinding) {
        super(viewBinding);
    }

    public static MyRentalsPastTripViewHolder create(Context context, ViewGroup parent) {
        return new MyRentalsPastTripViewHolder((MyRentalsPastTripItemBinding) createViewBinding(
                context,
                R.layout.item_my_rentals_past_trip,
                parent
        ));
    }

    public static void bind(Context context,
                            MyRentalsPastTripViewHolder viewHolder,
                            final EHITripSummary tripSummary,
                            final MyRentalsRecyclerAdapter.ReservationAdapterListener listener) {
        viewHolder.getViewBinding().rentalAgreementNumber.setText(tripSummary.getInvoiceNumber());
        viewHolder.getViewBinding().rentalPickupLocation.setText(tripSummary.getPickupLocation().getName());

        EHIVehicleDetails vehicleDetails = tripSummary.getVehicleDetails();
        if (vehicleDetails != null) {
            StringBuilder stringBuilder = new StringBuilder();

            if (!TextUtils.isEmpty(vehicleDetails.getColor())) {
                stringBuilder.append(vehicleDetails.getColor());
            }

            if (!TextUtils.isEmpty(vehicleDetails.getMake())) {
                stringBuilder.append(" ");
                stringBuilder.append(vehicleDetails.getMake());
            }

            if (!TextUtils.isEmpty(vehicleDetails.getModel())) {
                stringBuilder.append(" ");
                stringBuilder.append(vehicleDetails.getModel());
            }

            viewHolder.getViewBinding().rentalVehicle.setText(stringBuilder.toString());
            viewHolder.getViewBinding().vehicleArea.setVisibility(View.VISIBLE);
        } else {
            viewHolder.getViewBinding().vehicleArea.setVisibility(View.GONE);
        }

        EHIPriceSummary priceSummary = tripSummary.getPriceSummary();
        if (priceSummary != null && priceSummary.getCurrencyCode() != null) {
            SpannableStringBuilder spanBuilder = new SpannableStringBuilder(
                    priceSummary.getFormattedPriceView() + " " + priceSummary.getCurrencyCode()
            );

            spanBuilder.setSpan(new RelativeSizeSpan(0.8f),
                    priceSummary.getFormattedPriceView().length(),
                    priceSummary.getFormattedPriceView().length() + priceSummary.getCurrencyCode().length() + 1,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            spanBuilder.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.ehi_gray)),
                    priceSummary.getFormattedPriceView().length(),
                    priceSummary.getFormattedPriceView().length() + priceSummary.getCurrencyCode().length() + 1,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            viewHolder.getViewBinding().totalValue.setText(spanBuilder);
            viewHolder.getViewBinding().totalArea.setVisibility(View.VISIBLE);
            viewHolder.getViewBinding().paymentSeparator.setVisibility(View.VISIBLE);
        } else {
            viewHolder.getViewBinding().totalArea.setVisibility(View.GONE);
            viewHolder.getViewBinding().paymentSeparator.setVisibility(View.GONE);
        }

        // Later on we will use this
        viewHolder.getViewBinding().paymentMethod.setVisibility(View.GONE);
        viewHolder.getViewBinding().paymentInfo.setVisibility(View.GONE);

        String formattedFromDate = DateUtils.formatDateTime(context, tripSummary.getPickupTime().getTime(), DateUtils.FORMAT_SHOW_DATE |
                DateUtils.FORMAT_ABBREV_MONTH);

        String formattedToDate = DateUtils.formatDateTime(context, tripSummary.getReturnTime().getTime(), DateUtils.FORMAT_SHOW_DATE |
                DateUtils.FORMAT_SHOW_YEAR |
                DateUtils.FORMAT_ABBREV_MONTH);

        CharSequence formattedDateTime = new TokenizedString.Formatter<EHIStringToken>(context.getResources())
                .addTokenAndValue(EHIStringToken.DAY, formattedFromDate)
                .addTokenAndValue(EHIStringToken.DATE, formattedToDate)
                .formatString(R.string.past_rental_display_time)
                .format();

        viewHolder.getViewBinding().headerText.setText(formattedDateTime);

        viewHolder.getViewBinding().receiptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onTripClicked(tripSummary);
            }
        });
    }
}
