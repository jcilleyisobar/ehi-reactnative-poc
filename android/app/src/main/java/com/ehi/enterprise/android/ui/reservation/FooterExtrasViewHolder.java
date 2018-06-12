package com.ehi.enterprise.android.ui.reservation;

import android.support.annotation.NonNull;
import android.view.ViewGroup;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.databinding.ClassExtrasSectionFooterBinding;
import com.ehi.enterprise.android.ui.adapter.view_holders.AnimatingDataBindingViewHolder;

public class FooterExtrasViewHolder extends AnimatingDataBindingViewHolder<ClassExtrasSectionFooterBinding> {

    public FooterExtrasViewHolder(ClassExtrasSectionFooterBinding viewBinding) {
        super(viewBinding);
    }

    public static FooterExtrasViewHolder create(@NonNull ViewGroup parent) {
        return new FooterExtrasViewHolder((ClassExtrasSectionFooterBinding) createViewBinding(parent.getContext(), R.layout.v_class_extras_section_footer, parent));
    }

}