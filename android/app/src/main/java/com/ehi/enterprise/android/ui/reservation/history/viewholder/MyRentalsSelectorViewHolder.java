package com.ehi.enterprise.android.ui.reservation.history.viewholder;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.databinding.MyRentalsSelectorItemBinding;
import com.ehi.enterprise.android.ui.reservation.history.MyRentalsRecyclerAdapter;
import com.ehi.enterprise.android.ui.reservation.history.MyRentalsViewModel;
import com.ehi.enterprise.android.ui.viewholder.DataBindingViewHolder;

public class MyRentalsSelectorViewHolder extends DataBindingViewHolder<MyRentalsSelectorItemBinding> {

    protected MyRentalsSelectorViewHolder(final MyRentalsSelectorItemBinding viewBinding) {
        super(viewBinding);
    }

    public static MyRentalsSelectorViewHolder create(@NonNull Context context, @NonNull ViewGroup parent) {
        return new MyRentalsSelectorViewHolder((MyRentalsSelectorItemBinding) createViewBinding(context,
                R.layout.item_my_rentals_selector,
                parent));
    }

    public static void bind(@NonNull MyRentalsSelectorViewHolder viewHolder, @NonNull final MyRentalsRecyclerAdapter.ReservationAdapterListener listener) {
        viewHolder.getViewBinding().selectorGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.past_rental_selector:
                        listener.onSelectorChanged(MyRentalsViewModel.PAST);
                        break;
                    case R.id.upcoming_rentals_selector:
                        listener.onSelectorChanged(MyRentalsViewModel.UPCOMING);
                        break;
                }
            }
        });
    }
}
