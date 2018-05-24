package com.ehi.enterprise.android.ui.profile;


import android.support.v4.content.res.ResourcesCompat;
import android.text.SpannableString;
import android.text.Spanned;
import android.view.View;
import android.view.ViewGroup;
import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.app.EHIStringToken;
import com.ehi.enterprise.android.databinding.ItemProfilePaymentViewBinding;
import com.ehi.enterprise.android.models.profile.EHIPaymentMethod;
import com.ehi.enterprise.android.ui.profile.interfaces.EditPaymentListener;
import com.ehi.enterprise.android.ui.viewholder.DataBindingViewHolder;
import com.ehi.enterprise.android.utils.BaseAppUtils;
import com.ehi.enterprise.android.utils.CustomTypefaceSpan;
import com.isobar.android.tokenizedstring.TokenizedString;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class PaymentItemViewHolder extends DataBindingViewHolder<ItemProfilePaymentViewBinding> {

    private EditPaymentListener listener;

    public static PaymentItemViewHolder create(ViewGroup parent, EHIPaymentMethod method) {
        return new PaymentItemViewHolder(
                (ItemProfilePaymentViewBinding) createViewBinding(
                        parent.getContext(),
                        R.layout.item_profile_payment,
                        parent), method);
    }

    protected PaymentItemViewHolder(ItemProfilePaymentViewBinding viewBinding, final EHIPaymentMethod method) {
        super(viewBinding);

        getViewBinding().itemProfilePaymentView.setText(method.getMaskedCreditCardNumber());

        if (method.getPaymentType().equals(EHIPaymentMethod.TYPE_CREDIT_CARD)) {
            if (method.isExpired()) {
                getViewBinding().itemProfilePhonePaymentExpView.setVisibility(VISIBLE);
                getViewBinding().itemProfilePhonePaymentExpView.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.icon_alert_03, 0, 0, 0
                );

                CharSequence expirationMessage = new TokenizedString.Formatter<EHIStringToken>(viewBinding.getRoot().getResources())
                        .formatString(R.string.profile_payment_options_expired_text)
                        .addTokenAndValue(EHIStringToken.DATE, method.getExpirationDateAsLocalizedString())
                        .format();

                SpannableString spannableString = new SpannableString(expirationMessage);
                spannableString.setSpan(new CustomTypefaceSpan("sans-serif", ResourcesCompat.getFont(itemView.getContext(), R.font.source_sans_bold)),
                        0, expirationMessage.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                getViewBinding().itemProfilePhonePaymentExpView.setText(spannableString);
            }

            getViewBinding().itemProfilePaymentView.setCompoundDrawablesWithIntrinsicBounds(BaseAppUtils.getCardIconByType(method.getCardType()), 0, 0, 0);
            getViewBinding().itemProfilePaymentView.setCompoundDrawablePadding((int) getViewBinding().getRoot().getResources().getDimension(R.dimen.padding_xxsmall));
        } else {
            getViewBinding().itemProfilePhonePaymentExpView.setVisibility(GONE);
        }

        getViewBinding().preferredMark.setVisibility(method.isPreferred() ? VISIBLE : GONE);
        getViewBinding().itemProfilePaymentEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null) {
                    listener.onEdit(method);
                }
            }
        });

        getViewBinding().itemProfilePaymentDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null) {
                    listener.onDelete(method);
                }
            }
        });
    }

    public void setListener(EditPaymentListener listener) {
        this.listener = listener;
    }
}
