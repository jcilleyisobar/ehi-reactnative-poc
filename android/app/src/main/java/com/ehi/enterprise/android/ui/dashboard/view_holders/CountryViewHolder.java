package com.ehi.enterprise.android.ui.dashboard.view_holders;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.databinding.CountryItemBinding;
import com.ehi.enterprise.android.models.profile.EHICountry;
import com.ehi.enterprise.android.ui.dashboard.CountriesListRecyclerAdapter;
import com.ehi.enterprise.android.ui.viewholder.DataBindingViewHolder;

public final class CountryViewHolder extends DataBindingViewHolder<CountryItemBinding> {

    public CountryViewHolder(CountryItemBinding viewBinding) {
        super(viewBinding);
    }

    public static void bind(CountryViewHolder holder, final EHICountry country, final CountriesListRecyclerAdapter.OnCountryClickListener listener) {
        holder.getViewBinding().countryNameTextView.setText(country.getCountryName());
        holder.getViewBinding().countryNameTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null) {
                    listener.onCountryClicked(country);
                }
            }
        });
    }

    public static CountryViewHolder create(Context context, ViewGroup parent){
        return new CountryViewHolder((CountryItemBinding) createViewBinding(context, R.layout.item_country, parent));
    }
}