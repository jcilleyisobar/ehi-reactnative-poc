package com.ehi.enterprise.android.ui.reservation.history.viewholder;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.util.Pair;
import android.view.View;
import android.view.ViewGroup;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.databinding.MyRentalsInfoMessageItemBinding;
import com.ehi.enterprise.android.ui.viewholder.DataBindingViewHolder;

public class MyRentalsInfoMessageViewHolder extends DataBindingViewHolder<MyRentalsInfoMessageItemBinding> {

    protected MyRentalsInfoMessageViewHolder(final MyRentalsInfoMessageItemBinding viewBinding) {
        super(viewBinding);
    }

    public static MyRentalsInfoMessageViewHolder create(@NonNull Context context, @NonNull ViewGroup parent) {
        return new MyRentalsInfoMessageViewHolder((MyRentalsInfoMessageItemBinding) createViewBinding(
                context,
                R.layout.item_my_rentals_info_message,
                parent
        ));
    }

    public static void bind(@NonNull MyRentalsInfoMessageViewHolder viewHolder, @NonNull Pair<String, Integer> dataPair) {
        viewHolder.getViewBinding().message.setText(dataPair.first);
        if (dataPair.second != 0) {
            viewHolder.getViewBinding().icon.setImageResource(dataPair.second);
        }
        else {
            viewHolder.getViewBinding().icon.setVisibility(View.GONE);
        }
    }
}
