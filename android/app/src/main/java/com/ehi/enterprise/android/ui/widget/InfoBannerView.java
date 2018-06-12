package com.ehi.enterprise.android.ui.widget;

import android.content.Context;
import android.support.v4.content.res.ResourcesCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.databinding.InfoBannerViewBinding;
import com.ehi.enterprise.android.models.reservation.EHICharge;
import com.ehi.enterprise.android.models.reservation.EHIContract;
import com.ehi.enterprise.android.ui.view.DataBindingViewModelView;
import com.ehi.enterprise.android.ui.viewmodel.ManagersAccessViewModel;
import com.ehi.enterprise.android.utils.CustomTypefaceSpan;
import com.isobar.android.viewmodel.ViewModel;

@ViewModel(ManagersAccessViewModel.class)
public class InfoBannerView extends DataBindingViewModelView<ManagersAccessViewModel, InfoBannerViewBinding> {

    public InfoBannerView(Context context) {
        this(context, null, 0);
    }

    public InfoBannerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public InfoBannerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (isInEditMode()) {
            addView(inflate(context, R.layout.v_info_banner, null));
            return;
        }
        createViewBinding(R.layout.v_info_banner);
    }

    public void setup(String accountName,
                      String corporateContractType,
                      boolean isAvailableAtContract,
                      boolean isAvailableAtPromo,
                      OnClickListener termsClick,
                      boolean showSubtitle) {

        if (!TextUtils.isEmpty(accountName)) {

            setVisibility(View.VISIBLE);

            if (corporateContractType.equalsIgnoreCase(EHIContract.CONTRACT_TYPE_CORPORATE)
                    && isAvailableAtContract) {
                getViewBinding().contractDescription.setText(getDescription(R.string.class_select_discount_contract_prefix, accountName));

                if (showSubtitle) {
                    getViewBinding().contractSubtitle.setVisibility(VISIBLE);
                    getViewBinding().contractSubtitle.setText(R.string.class_select_discount_subtitle);
                }

            } else if (corporateContractType.equalsIgnoreCase(EHICharge.PROMOTION)
                    && isAvailableAtPromo) {

                getViewBinding().contractDescription.setText(getDescription(R.string.class_select_discount_coupon_prefix, accountName));

                if (termsClick != null) {
                    getViewBinding().terms.setVisibility(View.VISIBLE);
                    getViewBinding().terms.setOnClickListener(termsClick);
                }
            } else if (TextUtils.isEmpty(getViewBinding().contractDescription.getText().toString())) {
                getViewBinding().contractDescription.setText(R.string.class_select_discount_unavailable_prefix);
                if (showSubtitle) {
                    getViewBinding().contractSubtitle.setVisibility(VISIBLE);
                    getViewBinding().contractSubtitle.setText(R.string.class_select_discount_unavailable_subtitle);
                }
                getViewBinding().checkMark.setImageResource(R.drawable.icon_alert_02);
            }
        } else {
            setVisibility(View.GONE);
        }
    }

    private SpannableStringBuilder getDescription(int resId, CharSequence accountName) {
        SpannableString accountNameText = new SpannableString(accountName);
        accountNameText.setSpan(
                new CustomTypefaceSpan("", ResourcesCompat.getFont(getContext(), R.font.source_sans_bold)),
                0,
                accountNameText.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        );
        SpannableStringBuilder bld = new SpannableStringBuilder();
        bld.append(getResources().getString(resId))
                .append(" ")
                .append(accountNameText);

        return bld;
    }
}
