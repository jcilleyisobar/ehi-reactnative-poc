package com.ehi.enterprise.android.ui.reservation.view_holders;


import android.support.annotation.FontRes;
import android.support.v4.content.res.ResourcesCompat;
import android.text.SpannableString;
import android.text.Spanned;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.app.EHIStringToken;
import com.ehi.enterprise.android.databinding.ItemCheckPaymentViewBinding;
import com.ehi.enterprise.android.models.profile.EHIPaymentMethod;
import com.ehi.enterprise.android.ui.reservation.interfaces.PaymentItemCheckListener;
import com.ehi.enterprise.android.ui.viewholder.DataBindingViewHolder;
import com.ehi.enterprise.android.utils.BaseAppUtils;
import com.ehi.enterprise.android.utils.CustomTypefaceSpan;
import com.isobar.android.tokenizedstring.TokenizedString;

public class PaymentItemCheckViewHolder extends DataBindingViewHolder<ItemCheckPaymentViewBinding> {

    protected PaymentItemCheckViewHolder(ItemCheckPaymentViewBinding viewBinding) {
        super(viewBinding);
    }

    public static PaymentItemCheckViewHolder create(ViewGroup parent) {
        return new PaymentItemCheckViewHolder(
                (ItemCheckPaymentViewBinding) createViewBinding(
                        parent.getContext(),
                        R.layout.item_check_payment,
                        parent));
    }


    public static void bind(final PaymentItemCheckViewHolder holder,
                            final EHIPaymentMethod method,
                            final int position,
                            boolean checked,
                            final PaymentItemCheckListener listener,
                            CompoundButton.OnCheckedChangeListener onAutomaticallySelectCardCheckListener,
                            boolean shouldAutomaticallySelectCard) {

        String cardName = method.getAliasOrType(holder.itemView.getResources());
        holder.getViewBinding().cardName.setText(cardName);
        holder.getViewBinding().maskedNumber.setText(method.getMaskedNumber());

        int expirationResourceId = R.string.profile_payment_options_expires_text;
        @FontRes int pathToTypeface = R.font.source_sans_light;

        if (method.isExpired()) {
            holder.getViewBinding().expirationView.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.icon_alert_03, 0, 0, 0
            );
            expirationResourceId = R.string.profile_payment_options_expired_text;
            pathToTypeface = R.font.source_sans_bold;
            holder.getViewBinding().expirationView.setCompoundDrawablePadding((int) holder.getViewBinding().getRoot().getResources().getDimension(R.dimen.margin_xsmall));
        } else {
            holder.getViewBinding().expirationView.setCompoundDrawablePadding(0);
        }

        CharSequence expirationMessage = new TokenizedString.Formatter<EHIStringToken>(holder.itemView.getResources())
                .formatString(expirationResourceId)
                .addTokenAndValue(EHIStringToken.DATE, method.getExpirationDateAsLocalizedString())
                .format();
        SpannableString spannableString = new SpannableString(expirationMessage);
        spannableString.setSpan(new CustomTypefaceSpan("sans-serif",
                        ResourcesCompat.getFont(holder.itemView.getContext(), pathToTypeface)),
                0, expirationMessage.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        holder.getViewBinding().expirationView.setText(spannableString);

        holder.getViewBinding().maskedNumber.setCompoundDrawablesWithIntrinsicBounds(BaseAppUtils.getCardIconByType(method.getCardType()), 0, 0, 0);
        holder.getViewBinding().maskedNumber.setCompoundDrawablePadding((int) holder.itemView.getResources().getDimension(R.dimen.padding_xxsmall));

        holder.getViewBinding().checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if (checked && listener != null) {
                    listener.onCheck(holder, method, position);
                }
            }
        });

        holder.getViewBinding().checkbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onClick(method);
                }
            }
        });

        if (method.isPreferred()) {
            holder.getViewBinding().preferredMark.setVisibility(View.VISIBLE);
            holder.getViewBinding().checkbox.setChecked(true);
            holder.getViewBinding().automaticallySelectCheckbox.setVisibility(View.VISIBLE);
            holder.getViewBinding().automaticallySelectCheckbox.setChecked(shouldAutomaticallySelectCard);
            holder.getViewBinding().automaticallySelectCheckbox.setOnCheckedChangeListener(onAutomaticallySelectCardCheckListener);
        }


        if (checked) {
            holder.getViewBinding().checkbox.setChecked(true);
        }

        holder.getViewBinding().paymentEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null) {
                    listener.onEdit(method);
                }
            }
        });
    }


}
