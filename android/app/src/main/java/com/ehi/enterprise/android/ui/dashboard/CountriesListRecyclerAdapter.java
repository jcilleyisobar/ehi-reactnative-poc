package com.ehi.enterprise.android.ui.dashboard;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import com.ehi.enterprise.android.models.profile.EHICountry;
import com.ehi.enterprise.android.ui.dashboard.view_holders.CountryViewHolder;

import java.util.ArrayList;
import java.util.List;

public class CountriesListRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable {

    private List<EHICountry> mCountries;
    private List<EHICountry> mFullCountriesList;
    private OnCountryClickListener mListener;
    private CountriesFilter mFilter = new CountriesFilter();

    public CountriesListRecyclerAdapter(List<EHICountry> countries) {
        mCountries = countries;
    }

    public void setOnCountryClickListener(OnCountryClickListener listener) {
        mListener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return CountryViewHolder.create(parent.getContext(), parent);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        CountryViewHolder viewHolder = (CountryViewHolder) holder;
        final EHICountry country = mCountries.get(position);

        CountryViewHolder.bind(viewHolder, country, mListener);
    }

    @Override
    public int getItemCount() {
        return mCountries.size();
    }

    public interface OnCountryClickListener {
        void onCountryClicked(EHICountry country);
    }

    @Override
    public Filter getFilter() {
        return mFilter;
    }

    private class CountriesFilter extends Filter {

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {

            mCountries = (List<EHICountry>) results.values;
            notifyDataSetChanged();
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            FilterResults results = new FilterResults();
            if (mFullCountriesList == null) {
                mFullCountriesList = new ArrayList<>(mCountries);
            }
            ArrayList<EHICountry> filteredList = new ArrayList<>();

            constraint = constraint.toString().toLowerCase();
            for (int i = 0; i < mFullCountriesList.size(); i++) {
                String dataNames = mFullCountriesList.get(i).getCountryName();
                if (dataNames.toLowerCase().startsWith(constraint.toString())) {
                    filteredList.add(mFullCountriesList.get(i));
                }
            }

            results.count = filteredList.size();
            results.values = filteredList;
            return results;
        }
    }


}
