package com.ehi.enterprise.android.ui.reservation.widget;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.databinding.CollapsibleItemViewBinding;
import com.ehi.enterprise.android.ui.view.DataBindingViewModelView;
import com.ehi.enterprise.android.ui.viewmodel.ManagersAccessViewModel;
import com.ehi.enterprise.android.utils.EHITextUtils;
import com.isobar.android.viewmodel.ViewModel;

@ViewModel(ManagersAccessViewModel.class)
public class CollapsibleItemView extends DataBindingViewModelView<ManagersAccessViewModel, CollapsibleItemViewBinding> {

    public CollapsibleItemView(Context context) {
        this(context, null);
    }

    public CollapsibleItemView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CollapsibleItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        if (isInEditMode()) {
            addView(inflate(context, R.layout.v_collapsible_item, null));
            return;
        }

        createViewBinding(R.layout.v_collapsible_item);
    }

    public void setTitle(String title) {
        setInfo(title, null);
    }

    public void setInfo(CharSequence title, CharSequence subtitle) {
        setInfo(title, subtitle, " ");
    }

    public void setInfo(CharSequence title, CharSequence subtitle, String separator) {
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();

        if (!EHITextUtils.isEmpty(title)) {
            spannableStringBuilder.append(title);
        }

        if (!EHITextUtils.isEmpty(subtitle)) {
            SpannableString spannableString = new SpannableString(subtitle);
            spannableString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.extra_subtitle)), 0, spannableString.toString().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannableString.setSpan(new AbsoluteSizeSpan(14, true), 0, spannableString.toString().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            if (spannableStringBuilder.length() > 0) {
                spannableStringBuilder.append(separator);
            }

            spannableStringBuilder.append(spannableString);
        }

        getViewBinding().itemTitle.setText(spannableStringBuilder);
    }

    public void setTitleColor(int color) {
        getViewBinding().itemTitle.setTextColor(color);
    }

    public void setValue(String value) {
        getViewBinding().itemValue.setText(value);
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        getViewBinding().getRoot().setOnClickListener(onClickListener);
    }
}
