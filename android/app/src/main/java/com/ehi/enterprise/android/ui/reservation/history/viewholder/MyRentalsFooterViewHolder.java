package com.ehi.enterprise.android.ui.reservation.history.viewholder;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.databinding.MyRentalsFooterItemBinding;
import com.ehi.enterprise.android.ui.reservation.history.MyRentalsFooter;
import com.ehi.enterprise.android.ui.reservation.history.MyRentalsRecyclerAdapter;
import com.ehi.enterprise.android.ui.viewholder.DataBindingViewHolder;

public class MyRentalsFooterViewHolder extends DataBindingViewHolder<MyRentalsFooterItemBinding> {

    protected MyRentalsFooterViewHolder(final MyRentalsFooterItemBinding viewBinding) {
        super(viewBinding);
    }

    public static MyRentalsFooterViewHolder create(@NonNull Context context, @NonNull ViewGroup parent) {
        return new MyRentalsFooterViewHolder((MyRentalsFooterItemBinding) createViewBinding(
                context,
                R.layout.item_my_rentals_footer,
                parent
        ));
    }

    public static void bind(@NonNull MyRentalsFooterViewHolder viewHolder,
                            MyRentalsFooter footer,
                            boolean showBottomSpacer,
                            @NonNull final MyRentalsRecyclerAdapter.ReservationAdapterListener listener) {
        if (footer.footerPrompt != 0) {
            viewHolder.getViewBinding().prompt.setVisibility(View.VISIBLE);
            viewHolder.getViewBinding().prompt.setText(footer.footerPrompt);
        } else {
            viewHolder.getViewBinding().prompt.setVisibility(View.GONE);
        }

        if (footer.buttonText != 0) {
            viewHolder.getViewBinding().button.setText(footer.buttonText);
        }

        if (footer.onClickListener != null) {
            viewHolder.getViewBinding().button.setOnClickListener(footer.onClickListener);
        }

        if (footer.icon > 0) {
            viewHolder.getViewBinding().button.setCompoundDrawablesWithIntrinsicBounds(footer.icon, 0, 0, 0);
        } else {
            viewHolder.getViewBinding().button.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        }

        viewHolder.getViewBinding().bottomSpacer.setVisibility(
                showBottomSpacer ? View.VISIBLE : View.GONE
        );
    }
}
