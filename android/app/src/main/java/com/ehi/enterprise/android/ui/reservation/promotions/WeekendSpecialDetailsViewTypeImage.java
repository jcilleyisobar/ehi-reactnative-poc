package com.ehi.enterprise.android.ui.reservation.promotions;

import android.databinding.DataBindingUtil;
import android.support.annotation.DrawableRes;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.ehi.enterprise.android.R;

public class WeekendSpecialDetailsViewTypeImage extends WeekendSpecialDetailsViewType {
    @Override
    public int getViewType() {
        return IMAGE;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent) {
        return new PromotionDetailsImageViewHolder(
                DataBindingUtil.inflate(
                        LayoutInflater.from(parent.getContext()),
                        R.layout.item_promotion_details_image,
                        parent,
                        false
                ).getRoot()
        );
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, PromotionDetailsDatum data) {
        if (!(holder instanceof PromotionDetailsImageViewHolder) || !(data instanceof WeekendSpecialDetailsDatumImage)) {
            // unknown
            return;
        }

        ((PromotionDetailsImageViewHolder) holder).setImageResId(
                ((WeekendSpecialDetailsDatumImage) data).imageResId
        );
    }

    public static class PromotionDetailsImageViewHolder extends RecyclerView.ViewHolder {
        private ImageView itemImage;

        public PromotionDetailsImageViewHolder(View itemView) {
            super(itemView);

            itemImage = (ImageView) itemView.findViewById(R.id.promotion_item_image);
        }

        public void setImageResId(@DrawableRes int imageResId) {
            itemImage.setImageResource(imageResId);
        }
    }

    public static class WeekendSpecialDetailsDatumImage implements PromotionDetailsDatum {

        @DrawableRes
        private int imageResId;

        @Override
        public int getViewType() {
            return IMAGE;
        }

        public WeekendSpecialDetailsDatumImage(@DrawableRes int imageResId) {
            this.imageResId = imageResId;
        }

        @DrawableRes
        public int getImageResId() {
            return imageResId;
        }
    }
}
