package com.ehi.enterprise.android.ui.reservation.history.viewholder;

import android.content.Context;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.View;
import android.view.ViewGroup;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.app.EHIStringToken;
import com.ehi.enterprise.android.databinding.MyRentalsUpcomingTripItemBinding;
import com.ehi.enterprise.android.models.reservation.EHITripSummary;
import com.ehi.enterprise.android.ui.reservation.history.MyRentalsRecyclerAdapter;
import com.ehi.enterprise.android.ui.reservation.history.MyRentalsRecyclerItem;
import com.ehi.enterprise.android.ui.viewholder.DataBindingViewHolder;
import com.isobar.android.tokenizedstring.TokenizedString;

public class MyRentalsTripViewHolder extends DataBindingViewHolder<MyRentalsUpcomingTripItemBinding> {

    protected MyRentalsTripViewHolder(final MyRentalsUpcomingTripItemBinding viewBinding) {
        super(viewBinding);
    }

    public static MyRentalsTripViewHolder create(Context context, ViewGroup parent) {
        return new MyRentalsTripViewHolder((MyRentalsUpcomingTripItemBinding) createViewBinding(
                context,
                R.layout.item_my_rentals_upcoming_trip,
                parent
        ));
    }

    public static void bind(Context context,
                            MyRentalsTripViewHolder viewHolder,
                            final EHITripSummary tripSummary,
                            final int viewType,
                            final boolean isCurrent,
                            final MyRentalsRecyclerAdapter.ReservationAdapterListener listener) {
        viewHolder.getViewBinding().rentalConfirmation.setText(
                "#" + (viewType == MyRentalsRecyclerItem.UPCOMING_TRIP ? tripSummary.getConfirmationNumber() : tripSummary.getTicketNumber())
        );
        viewHolder.getViewBinding().rentalPickupLocation.setText(tripSummary.getPickupLocation().getName());

        if (!TextUtils.isEmpty(tripSummary.getReturnLocation().getName())
                && !tripSummary.getReturnLocation().getId().equals(tripSummary.getPickupLocation().getId())) {
            viewHolder.getViewBinding().rentalReturnLocation.setText(tripSummary.getReturnLocation().getName());
            viewHolder.getViewBinding().rentalReturnLocation.setVisibility(View.VISIBLE);
        }
        else {
            viewHolder.getViewBinding().rentalReturnLocation.setVisibility(View.GONE);
        }


        if (isCurrent) {
            viewHolder.getViewBinding().dateHeader.headerText.setText(context.getString(R.string.current_rentals_cell_header));
            viewHolder.getViewBinding().chevron.setVisibility(View.GONE);
            viewHolder.itemView.setOnClickListener(null);
        }
        else {
            String formattedDate = DateUtils.formatDateTime(
                    context, tripSummary.getPickupTime().getTime(),
                    DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR | DateUtils.FORMAT_ABBREV_MONTH
            );

            String formattedTime = DateUtils.formatDateTime(context, tripSummary.getPickupTime().getTime(), DateUtils.FORMAT_SHOW_TIME);

            CharSequence formattedDateTime = new TokenizedString.Formatter<EHIStringToken>(context.getResources())
                    .addTokenAndValue(EHIStringToken.DATE, formattedDate)
                    .addTokenAndValue(EHIStringToken.TIME, formattedTime)
                    .formatString(R.string.user_rental_display_time)
                    .format();

            viewHolder.getViewBinding().dateHeader.headerText.setText(formattedDateTime);
            viewHolder.getViewBinding().chevron.setVisibility(View.VISIBLE);
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onUpcomingRentalClicked(tripSummary);
                }
            });
        }
    }
}
