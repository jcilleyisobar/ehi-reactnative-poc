package com.ehi.enterprise.android.ui.location;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.ehi.enterprise.android.models.location.EHIPolicy;
import com.ehi.enterprise.android.ui.location.view_holders.PolicyViewHolder;

import java.util.List;

public class PoliciesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<EHIPolicy> mPoliciesList;
    private AdapterView.OnItemClickListener mOnItemClicklListener;

    public PoliciesAdapter(List<EHIPolicy> policiesList) {
        mPoliciesList = policiesList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return PolicyViewHolder.create(parent.getContext(), parent);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        PolicyViewHolder.bind((PolicyViewHolder) holder,
                mPoliciesList.get(position),
                new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mOnItemClicklListener != null) {
                    mOnItemClicklListener.onItemClick(null, null, position, position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mPoliciesList.size();
    }

    public void setOnItemClickListener(AdapterView.OnItemClickListener listener) {
        mOnItemClicklListener = listener;
    }
}
