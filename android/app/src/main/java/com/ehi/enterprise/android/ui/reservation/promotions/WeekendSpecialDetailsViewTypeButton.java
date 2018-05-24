package com.ehi.enterprise.android.ui.reservation.promotions;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ehi.enterprise.android.R;

public class WeekendSpecialDetailsViewTypeButton extends WeekendSpecialDetailsViewType {
    @Override
    public int getViewType() {
        return BUTTON;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent) {
        return new WeekendSpecialDetailsViewTypeButton.PromotionDetailsButtonViewHolder(
                DataBindingUtil.inflate(
                        LayoutInflater.from(parent.getContext()),
                        R.layout.item_promotion_details_button,
                        parent,
                        false
                ).getRoot()
        );
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, PromotionDetailsDatum data) {
        if (!(holder instanceof PromotionDetailsButtonViewHolder)
                || !(data instanceof WeekendSpecialDetailsDatumButton)) {
            return;
        }

        PromotionDetailsButtonViewHolder typedHolder = (PromotionDetailsButtonViewHolder) holder;
        WeekendSpecialDetailsDatumButton typedData = (WeekendSpecialDetailsDatumButton) data;

        typedHolder.setButtonText(typedData.buttonText);
        typedHolder.setButtonOnClickListener(typedData.listener);
    }

    public static class PromotionDetailsButtonViewHolder extends RecyclerView.ViewHolder {
        private TextView button;

        public PromotionDetailsButtonViewHolder(View itemView) {
            super(itemView);

            button = (TextView) itemView.findViewById(R.id.promotion_item_button);
        }

        public void setButtonText(String buttonText) {
            button.setText(buttonText);
        }

        public void setButtonOnClickListener(View.OnClickListener listener) {
            button.setOnClickListener(listener);
        }
    }

    public static class WeekendSpecialDetailsDatumButton implements PromotionDetailsDatum {
        private String buttonText;
        private View.OnClickListener listener;

        @Override
        public int getViewType() {
            return BUTTON;
        }

        public WeekendSpecialDetailsDatumButton(String buttonText, View.OnClickListener listener) {
            this.buttonText = buttonText;
            this.listener = listener;
        }

        public String getButtonText() {
            return buttonText;
        }

        public View.OnClickListener getListener() {
            return listener;
        }
    }
}
