package com.ehi.enterprise.android.ui.reservation.history.viewholder;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.databinding.MyRentalsEmptyCellItemBinding;
import com.ehi.enterprise.android.ui.reservation.history.MyRentalsRecyclerAdapter;
import com.ehi.enterprise.android.ui.viewholder.DataBindingViewHolder;

public class MyRentalsEmptyListCellViewHolder extends DataBindingViewHolder<MyRentalsEmptyCellItemBinding> {

    protected MyRentalsEmptyListCellViewHolder(final MyRentalsEmptyCellItemBinding viewBinding) {
        super(viewBinding);
    }

    public static MyRentalsEmptyListCellViewHolder create(@NonNull Context context, @NonNull ViewGroup parent) {
        return new MyRentalsEmptyListCellViewHolder((MyRentalsEmptyCellItemBinding) createViewBinding(
                context,
                R.layout.item_my_rentals_empty_cell,
                parent
        ));
    }

    public static void bind(MyRentalsEmptyListCellViewHolder holder, EmptyListCellData data, final MyRentalsRecyclerAdapter.ReservationAdapterListener listener) {
        holder.getViewBinding().emptyCellText.setText(data.message);

        if (data.showLookUp) {
            holder.getViewBinding().lookUpRentalButton.setVisibility(View.VISIBLE);
            holder.getViewBinding().lookUpRentalButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onLookUpRentalClicked();
                }
            });
        } else {
            holder.getViewBinding().lookUpRentalButton.setVisibility(View.GONE);
        }
    }

    public static class EmptyListCellData {
        public String message;
        public boolean showLookUp;

        public EmptyListCellData(String message, boolean showLookUp) {
            this.message = message;
            this.showLookUp = showLookUp;
        }
    }
}
