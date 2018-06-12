package com.ehi.enterprise.android.ui.reservation;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.databinding.ClassSelectSectionFooterBinding;
import com.ehi.enterprise.android.ui.adapter.view_holders.AnimatingDataBindingViewHolder;

public class FooterViewHolder extends AnimatingDataBindingViewHolder<ClassSelectSectionFooterBinding> {

    public FooterViewHolder(ClassSelectSectionFooterBinding viewBinding) {
        super(viewBinding);
    }

    public static FooterViewHolder create(@NonNull Context context, @NonNull ViewGroup parent) {
        return new FooterViewHolder((ClassSelectSectionFooterBinding) createViewBinding(context, R.layout.v_class_select_section_footer, parent));
    }

    public static void bind(@NonNull FooterViewHolder viewHolder, final CarClassListAdapter.CarClassListAdapterListener mListener, boolean showAsterisks) {
        if (!showAsterisks) {
            viewHolder.getViewBinding().totalCostAsterisksText.setVisibility(View.VISIBLE);
        }

        viewHolder.getViewBinding().rentalTermsConditions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onRentalTermsConditionsClicked();
            }
        });

    }

}