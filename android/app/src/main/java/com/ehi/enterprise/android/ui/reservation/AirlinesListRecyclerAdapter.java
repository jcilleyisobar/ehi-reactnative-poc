package com.ehi.enterprise.android.ui.reservation;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import com.ehi.enterprise.android.models.reservation.EHIAirlineDetails;
import com.ehi.enterprise.android.ui.reservation.view_holders.AirlinesViewHolder;

import java.util.ArrayList;
import java.util.List;

public class AirlinesListRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable {

    private List<EHIAirlineDetails> mAirlines;
    private EHIAirlineDetails mOther;
    private List<EHIAirlineDetails> mAllAirlines;
    private OnAirlineClickListener mListener;
    private AirlinesFilter mFilter = new AirlinesFilter();

    public AirlinesListRecyclerAdapter(List<EHIAirlineDetails> value, EHIAirlineDetails other) {
        mAllAirlines = new ArrayList<>(value);
        mAirlines = value;
        mAirlines.add(other);
        mOther = other;
    }

    public void setOnAirlineClickListener(OnAirlineClickListener listener) {
        mListener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return AirlinesViewHolder.create(parent.getContext(), parent);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        AirlinesViewHolder viewHolder = (AirlinesViewHolder) holder;
        final EHIAirlineDetails airline = mAirlines.get(position);
        AirlinesViewHolder.bind(viewHolder, airline, mListener);
    }

    @Override
    public int getItemCount() {
        return mAirlines.size();
    }

    public interface OnAirlineClickListener {
        void onAirlineClicked(EHIAirlineDetails airline);
    }

    @Override
    public Filter getFilter() {
        return mFilter;
    }

    private class AirlinesFilter extends Filter {

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {

            mAirlines = (List<EHIAirlineDetails>) results.values;
            notifyDataSetChanged();
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            final FilterResults results = new FilterResults();
            final ArrayList<EHIAirlineDetails> filteredList = new ArrayList<>();
            final String constraintString = constraint.toString().toLowerCase();

            for (int i = 0, size = mAllAirlines.size(); i < size; i++) {
                final EHIAirlineDetails ehiAirlineDetails = mAllAirlines.get(i);
                final String dataNames = ehiAirlineDetails.getDescription();
                if (dataNames.toLowerCase().contains(constraintString)) {
                    filteredList.add(ehiAirlineDetails);
                }
            }

            filteredList.add(mOther);

            results.count = filteredList.size();
            results.values = filteredList;
            return results;
        }
    }

}