package com.ehi.enterprise.android.ui.reservation.promotions;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ehi.enterprise.android.R;

public class WeekendSpecialDetailsViewTypeBullet extends WeekendSpecialDetailsViewType {
    @Override
    public int getViewType() {
        return BULLET_ITEM;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent) {
        return new PromotionDetailsBulletViewHolder(
                DataBindingUtil.inflate(
                        LayoutInflater.from(parent.getContext()),
                        R.layout.item_promotion_details_bullet,
                        parent,
                        false
                ).getRoot()
        );
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, PromotionDetailsDatum data) {
        if (!(holder instanceof PromotionDetailsBulletViewHolder)
                || !(data instanceof WeekendSpecialDetailsDatumBullet)) {
            return;
        }

        ((PromotionDetailsBulletViewHolder) holder).setBulletText(
                ((WeekendSpecialDetailsDatumBullet) data).bulletText
        );
    }

    public static class PromotionDetailsBulletViewHolder extends RecyclerView.ViewHolder {
        private TextView bulletTextView;

        public PromotionDetailsBulletViewHolder(View itemView) {
            super(itemView);

            bulletTextView = (TextView) itemView.findViewById(R.id.promotion_item_bullet);
        }

        public void setBulletText(String bulletText) {
            bulletTextView.setText(bulletText);
        }
    }

    public static class WeekendSpecialDetailsDatumBullet implements PromotionDetailsDatum {
        private String bulletText;

        @Override
        public int getViewType() {
            return BULLET_ITEM;
        }

        public WeekendSpecialDetailsDatumBullet(String bulletText) {
            this.bulletText = bulletText;
        }

        public String getBulletText() {
            return bulletText;
        }
    }
}
