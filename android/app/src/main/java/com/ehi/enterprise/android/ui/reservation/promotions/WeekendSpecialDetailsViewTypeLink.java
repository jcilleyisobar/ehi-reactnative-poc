package com.ehi.enterprise.android.ui.reservation.promotions;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ehi.enterprise.android.R;

public class WeekendSpecialDetailsViewTypeLink extends WeekendSpecialDetailsViewType {
    @Override
    public int getViewType() {
        return CLICKABLE_ITEM;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent) {
        return new PromotionDetailsLinkViewHolder(
                DataBindingUtil.inflate(
                        LayoutInflater.from(parent.getContext()),
                        R.layout.item_promotion_details_link,
                        parent,
                        false
                ).getRoot()
        );
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, PromotionDetailsDatum data) {
        if (!(holder instanceof PromotionDetailsLinkViewHolder)
                || !(data instanceof WeekendSpecialDetailsDatumLink)) {
            return;
        }

        PromotionDetailsLinkViewHolder typedHolder = (PromotionDetailsLinkViewHolder) holder;
        WeekendSpecialDetailsDatumLink typedData = (WeekendSpecialDetailsDatumLink) data;

        typedHolder.setLinkText(typedData.linkText);
        typedHolder.setOnClickListener(typedData.listener);
    }

    public static class PromotionDetailsLinkViewHolder extends RecyclerView.ViewHolder {
        private TextView linkTextView;

        public PromotionDetailsLinkViewHolder(View itemView) {
            super(itemView);

            linkTextView = (TextView) itemView.findViewById(R.id.promotion_item_link);
        }

        public void setLinkText(String linkText) {
            linkTextView.setText(linkText);
        }

        public void setOnClickListener(View.OnClickListener listener) {
            itemView.setOnClickListener(listener);
        }
    }

    public static class WeekendSpecialDetailsDatumLink implements PromotionDetailsDatum {
        private String linkText;
        private View.OnClickListener listener;

        @Override
        public int getViewType() {
            return CLICKABLE_ITEM;
        }

        public WeekendSpecialDetailsDatumLink(String linkText, View.OnClickListener listener) {
            this.linkText = linkText;
            this.listener = listener;
        }

        public String getLinkText() {
            return linkText;
        }

        public View.OnClickListener getListener() {
            return listener;
        }
    }
}
