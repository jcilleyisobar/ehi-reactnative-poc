package com.ehi.enterprise.android.ui.location;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.ehi.enterprise.android.models.location.EHIWayfindingStep;
import com.ehi.enterprise.android.ui.location.view_holders.WayfindingViewHolder;

import java.util.ArrayList;
import java.util.List;

public class WayfindingAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<EHIWayfindingStep> mWayfindingSteps = new ArrayList<>();

    public WayfindingAdapter(List<EHIWayfindingStep> objects) {
        mWayfindingSteps.clear();
        if (objects != null) {
            mWayfindingSteps.addAll(objects);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return WayfindingViewHolder.create(parent.getContext(), parent);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        WayfindingViewHolder.bind((WayfindingViewHolder) holder,
                mWayfindingSteps.get(position));
    }

    @Override
    public int getItemCount() {
        return mWayfindingSteps.size();
    }

}
