package com.ehi.enterprise.android.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.databinding.SectionHeaderItemBinding;
import com.ehi.enterprise.android.ui.viewholder.DataBindingViewHolder;

public class SectionHeaderViewHolder extends DataBindingViewHolder<SectionHeaderItemBinding> {

    public SectionHeaderViewHolder(SectionHeaderItemBinding viewBinding) {
        super(viewBinding);
    }

    public static SectionHeaderViewHolder create(@NonNull Context context, @NonNull ViewGroup parent) {
        return new SectionHeaderViewHolder((SectionHeaderItemBinding) createViewBinding(
                context,
                R.layout.item_section_header,
                parent));
    }

    public static void bind(@NonNull SectionHeaderViewHolder viewHolder, @NonNull SectionHeader sectionHeader) {
        viewHolder.getViewBinding().headerSecondaryText.setText(sectionHeader.getSecondaryTitle());
        viewHolder.getViewBinding().headerText.setText(sectionHeader.getTitle());
        viewHolder.getViewBinding().headerButton.setVisibility(sectionHeader.shouldUseButton() ? View.VISIBLE : View.INVISIBLE);
        if (sectionHeader.shouldUseButton()) {
            viewHolder.getViewBinding().headerButton.setText(sectionHeader.getButtonText());
            viewHolder.getViewBinding().headerButton.setOnClickListener(sectionHeader.getOnClickListener());
        }
        if (sectionHeader.shouldShowTriangle()) {
            viewHolder.getViewBinding().sectionHeaderTriangle.setVisibility(View.VISIBLE);
            viewHolder.getViewBinding().sectionHeaderTriangleLine.setVisibility(View.VISIBLE);
        }
        else {
            viewHolder.getViewBinding().sectionHeaderTriangle.setVisibility(View.GONE);
            viewHolder.getViewBinding().sectionHeaderTriangleLine.setVisibility(View.GONE);
        }

    }
}
