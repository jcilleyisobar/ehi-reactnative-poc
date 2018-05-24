package com.ehi.enterprise.android.ui.reservation.history.viewholder;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.databinding.MyRentalsLoadMoreButtonItemBinding;
import com.ehi.enterprise.android.ui.reservation.history.MyRentalsRecyclerAdapter;
import com.ehi.enterprise.android.ui.viewholder.DataBindingViewHolder;

public class MyRentalsLoadMoreButtonViewHolder extends DataBindingViewHolder<MyRentalsLoadMoreButtonItemBinding> {


    protected MyRentalsLoadMoreButtonViewHolder(final MyRentalsLoadMoreButtonItemBinding viewBinding) {
        super(viewBinding);
    }

    public static MyRentalsLoadMoreButtonViewHolder create(Context context, ViewGroup parent) {
        return new MyRentalsLoadMoreButtonViewHolder((MyRentalsLoadMoreButtonItemBinding) createViewBinding(
                context,
                R.layout.item_load_more_button,
                parent
        ));
    }

    public static void bind(MyRentalsLoadMoreButtonViewHolder viewHolder, final MyRentalsRecyclerAdapter.ReservationAdapterListener listener) {
        viewHolder.getViewBinding().loadMoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onLoadMoreClicked();
            }
        });
    }
}
