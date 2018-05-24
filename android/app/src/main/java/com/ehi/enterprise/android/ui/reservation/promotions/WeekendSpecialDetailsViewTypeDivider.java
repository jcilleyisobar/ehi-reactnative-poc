package com.ehi.enterprise.android.ui.reservation.promotions;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ehi.enterprise.android.R;

public class WeekendSpecialDetailsViewTypeDivider extends WeekendSpecialDetailsViewType {
    @Override
    public int getViewType() {
        return DIVIDER;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent) {
        return new PromotionDetailsDividerViewHolder(
                DataBindingUtil.inflate(
                        LayoutInflater.from(parent.getContext()),
                        R.layout.item_promotion_details_divider,
                        parent,
                        false
                ).getRoot()
        );
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, PromotionDetailsDatum data) {
        // nothing to do here
    }

    public static class PromotionDetailsDividerViewHolder extends RecyclerView.ViewHolder {
        public PromotionDetailsDividerViewHolder(View itemView) {
            super(itemView);
        }
    }

    public static class WeekendSpecialDetailsDatumDivider implements PromotionDetailsDatum {
        @Override
        public int getViewType() {
            return DIVIDER;
        }
    }
}
