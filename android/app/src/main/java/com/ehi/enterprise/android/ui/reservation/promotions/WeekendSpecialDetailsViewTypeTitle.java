package com.ehi.enterprise.android.ui.reservation.promotions;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ehi.enterprise.android.R;

public class WeekendSpecialDetailsViewTypeTitle extends WeekendSpecialDetailsViewType {
    @Override
    public int getViewType() {
        return TITLE;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent) {
        return new PromotionDetailsTitleViewHolder(
                DataBindingUtil.inflate(
                        LayoutInflater.from(parent.getContext()),
                        R.layout.item_promotion_details_title,
                        parent,
                        false
                ).getRoot()
        );
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, PromotionDetailsDatum data) {
        if (!(holder instanceof PromotionDetailsTitleViewHolder)
                || !(data instanceof WeekendSpecialDetailsDatumTitle)) {
            return;
        }

        ((PromotionDetailsTitleViewHolder) holder).setTitle(
                ((WeekendSpecialDetailsDatumTitle) data).title
        );
    }

    public static class PromotionDetailsTitleViewHolder extends RecyclerView.ViewHolder {
        private TextView itemTitle;

        public PromotionDetailsTitleViewHolder(View itemView) {
            super(itemView);

            itemTitle = (TextView) itemView.findViewById(R.id.promotion_item_title);
        }

        public void setTitle(String title) {
            itemTitle.setText(title);
        }
    }

    public static class WeekendSpecialDetailsDatumTitle implements PromotionDetailsDatum {
        private String title;

        @Override
        public int getViewType() {
            return TITLE;
        }

        public WeekendSpecialDetailsDatumTitle(String title) {
            this.title = title;
        }

        public String getTitle() {
            return title;
        }
    }
}
