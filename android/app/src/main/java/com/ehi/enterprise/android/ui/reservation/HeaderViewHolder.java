package com.ehi.enterprise.android.ui.reservation;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.view.View;
import android.view.ViewGroup;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.app.EHIStringToken;
import com.ehi.enterprise.android.databinding.ClassSelectSectionHeaderBinding;
import com.ehi.enterprise.android.models.reservation.EHIAvailableCarFilters;
import com.ehi.enterprise.android.ui.adapter.view_holders.AnimatingDataBindingViewHolder;
import com.ehi.enterprise.android.utils.CustomTypefaceSpan;
import com.ehi.enterprise.android.utils.EHITextUtils;
import com.isobar.android.tokenizedstring.TokenizedString;

import java.util.List;

public class HeaderViewHolder extends AnimatingDataBindingViewHolder<ClassSelectSectionHeaderBinding> {

    private CarClassListAdapter.CarClassListAdapterListener mCallBackListener;

    private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (getViewBinding().clearButton == view) {
                mCallBackListener.onFilterClearButtonClicked();
                getViewBinding().clearContainer.setVisibility(View.GONE);
            }
        }
    };

    public HeaderViewHolder(ClassSelectSectionHeaderBinding viewBinding) {
        super(viewBinding);
    }

    public static RecyclerView.ViewHolder create(@NonNull Context context, @NonNull ViewGroup parent) {
        return new HeaderViewHolder(
                (ClassSelectSectionHeaderBinding) createViewBinding(context, R.layout.v_class_select_section_header, parent));
    }

    public static void bind(@NonNull HeaderViewHolder viewHolder,
                            CarClassListAdapter.CarClassListAdapterListener listener,
                            List<EHIAvailableCarFilters> filters,
                            String cidName,
                            String contractType,
                            boolean isAvailableAtContract,
                            boolean isAvailableAtPromo,
                            View.OnClickListener termsClick,
                            String currency) {

        int filterSize = EHIAvailableCarFilters.getActiveFilters(filters).size();
        if (filterSize != 0) {
            viewHolder.getViewBinding().clearFilterDivider.setVisibility(View.VISIBLE);
            viewHolder.getViewBinding().greenFilterDivider.setVisibility(View.VISIBLE);
            viewHolder.getViewBinding().clearContainer.setVisibility(View.VISIBLE);
            SpannableStringBuilder builder = new SpannableStringBuilder();
            String filterText = viewHolder.itemView.getResources().getString(R.string.location_filter_banner_title_prefix) + " ";
            builder.append(filterText)
                    .append(EHIAvailableCarFilters.getFilterText(filters));

            Typeface tf = ResourcesCompat.getFont(viewHolder.itemView.getContext(), R.font.source_sans_bold);

            final CustomTypefaceSpan bss = new CustomTypefaceSpan("", tf);

            builder.setSpan(bss, 0, filterText.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
            viewHolder.getViewBinding().filterGeneratedText.setText(builder);
            viewHolder.itemView.setVisibility(View.VISIBLE);
        } else {
            viewHolder.getViewBinding().clearFilterDivider.setVisibility(View.GONE);
            viewHolder.getViewBinding().greenFilterDivider.setVisibility(View.GONE);
            viewHolder.getViewBinding().clearContainer.setVisibility(View.GONE);
        }

        viewHolder.mCallBackListener = listener;
        viewHolder.getViewBinding().clearButton.setOnClickListener(viewHolder.mOnClickListener);

        viewHolder.getViewBinding().contractBanner.setup(cidName, contractType, isAvailableAtContract, isAvailableAtPromo, termsClick, true);

        setUpCurrencyWarning(viewHolder, currency);

        viewHolder.itemView.invalidate();
    }

    private static void setUpCurrencyWarning(@NonNull HeaderViewHolder viewHolder, String currency) {
        if (!EHITextUtils.isEmpty(currency)) {
            final SpannableString spannableString = new SpannableString(currency);
            final Typeface tf = ResourcesCompat.getFont(viewHolder.itemView.getContext(), R.font.source_sans_bold);

            final CustomTypefaceSpan bss = new CustomTypefaceSpan("", tf);
            spannableString.setSpan(bss, 0, currency.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
            viewHolder.getViewBinding().currencyBanner.setMessage(new TokenizedString.Formatter<EHIStringToken>(viewHolder.itemView.getResources())
                    .addTokenAndValue(EHIStringToken.CURRENCY_CODE, spannableString)
                    .formatString(R.string.car_class_currency_code_differs_title)
                    .format());
            viewHolder.getViewBinding().currencyBanner.setVisibility(View.VISIBLE);
            viewHolder.getViewBinding().currencyBanner.setIcon(R.drawable.ico_info);
        }
    }

}