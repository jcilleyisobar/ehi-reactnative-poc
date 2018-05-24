package com.ehi.enterprise.android.ui.reservation.promotions;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

import com.ehi.enterprise.android.ui.reservation.view_holders.EmptyViewHolder;

import java.util.ArrayList;
import java.util.List;

public class WeekendSpecialDetailsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    @NonNull
    private SparseArray<WeekendSpecialDetailsViewType> promotionDetailsViewTypes = new SparseArray<>();

    @NonNull
    private List<WeekendSpecialDetailsViewType.PromotionDetailsDatum> promotionDetailsData = new ArrayList<>();

    public void addViewType(WeekendSpecialDetailsViewType viewType) {
        promotionDetailsViewTypes.put(viewType.getViewType(), viewType);
    }

    public void addItem(WeekendSpecialDetailsViewType.PromotionDetailsDatum item) {
        promotionDetailsData.add(item);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        WeekendSpecialDetailsViewType promotionDetailsViewType = promotionDetailsViewTypes.get(viewType);

        if (promotionDetailsViewType == null) {
            return new EmptyViewHolder(new View(parent.getContext()));
        }

        return promotionDetailsViewType.onCreateViewHolder(parent);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        WeekendSpecialDetailsViewType.PromotionDetailsDatum data = promotionDetailsData.get(position);
        int viewType = getItemViewType(position);

        WeekendSpecialDetailsViewType promotionDetailsViewType = promotionDetailsViewTypes.get(viewType);

        if (promotionDetailsViewType == null) {
            return;
        }

        promotionDetailsViewType.onBindViewHolder(holder, data);
    }

    @Override
    public int getItemCount() {
        return promotionDetailsData.size();
    }

    @Override
    public int getItemViewType(int position) {
        return promotionDetailsData.get(position) != null ?
                promotionDetailsData.get(position).getViewType() : -1;
    }

}
