package com.ehi.enterprise.android.ui.reservation.view_holders;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.databinding.AirlineItemBinding;
import com.ehi.enterprise.android.models.reservation.EHIAirlineDetails;
import com.ehi.enterprise.android.ui.reservation.AirlinesListRecyclerAdapter;
import com.ehi.enterprise.android.ui.viewholder.DataBindingViewHolder;

public final class AirlinesViewHolder extends DataBindingViewHolder<AirlineItemBinding> {
    public AirlinesViewHolder(AirlineItemBinding viewBinding) {
        super(viewBinding);
    }

    public static void bind(AirlinesViewHolder holder, final EHIAirlineDetails airline, final AirlinesListRecyclerAdapter.OnAirlineClickListener listener) {
        holder.getViewBinding().nameTextView.setText(airline.getDescription());
        holder.getViewBinding().nameTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null) {
                    listener.onAirlineClicked(airline);
                }
            }
        });
    }

    public static AirlinesViewHolder create(Context context, ViewGroup parent){
        return new AirlinesViewHolder((AirlineItemBinding) createViewBinding(context, R.layout.item_airline, parent));
    }
}
