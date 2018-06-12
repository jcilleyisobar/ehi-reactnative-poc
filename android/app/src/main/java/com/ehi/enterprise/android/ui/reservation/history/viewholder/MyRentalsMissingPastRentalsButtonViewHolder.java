package com.ehi.enterprise.android.ui.reservation.history.viewholder;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.databinding.MissingPastRentalsItemBinding;
import com.ehi.enterprise.android.ui.reservation.history.MyRentalsRecyclerAdapter;
import com.ehi.enterprise.android.ui.viewholder.DataBindingViewHolder;

public class MyRentalsMissingPastRentalsButtonViewHolder extends DataBindingViewHolder<MissingPastRentalsItemBinding> {
    protected MyRentalsMissingPastRentalsButtonViewHolder(final MissingPastRentalsItemBinding viewBinding) {
        super(viewBinding);
    }

    public static MyRentalsMissingPastRentalsButtonViewHolder create(Context context, ViewGroup viewGroup) {
        return new MyRentalsMissingPastRentalsButtonViewHolder((MissingPastRentalsItemBinding) createViewBinding(
                context,
                R.layout.item_missing_past_rentals,
                viewGroup
        ));
    }

    public static void bind(MyRentalsMissingPastRentalsButtonViewHolder holder,
                            final MyRentalsRecyclerAdapter.ReservationAdapterListener listener) {
        holder.getViewBinding().missingPastRentalsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                listener.onMissingRentalsClicked();
            }
        });
    }
}
