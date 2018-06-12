package com.ehi.enterprise.android.ui.profile.widgets;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.FontRes;
import android.support.v4.content.res.ResourcesCompat;
import android.text.SpannableString;
import android.text.Spanned;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.app.EHIStringToken;
import com.ehi.enterprise.android.databinding.ItemProfilePhonePaymentViewBinding;
import com.ehi.enterprise.android.databinding.ProfilePaymentInfoBinding;
import com.ehi.enterprise.android.models.profile.EHIPaymentMethod;
import com.ehi.enterprise.android.models.profile.EHIPaymentProfile;
import com.ehi.enterprise.android.ui.view.DataBindingViewModelView;
import com.ehi.enterprise.android.ui.viewmodel.ManagersAccessViewModel;
import com.ehi.enterprise.android.utils.BaseAppUtils;
import com.ehi.enterprise.android.utils.CustomTypefaceSpan;
import com.ehi.enterprise.android.utils.ListUtils;
import com.isobar.android.tokenizedstring.TokenizedString;
import com.isobar.android.viewmodel.ViewModel;

import java.util.Collections;
import java.util.List;

import static com.ehi.enterprise.android.app.Settings.MAX_CREDIT_CARDS;

@ViewModel(ManagersAccessViewModel.class)
public class ProfilePaymentInfoView extends DataBindingViewModelView<ManagersAccessViewModel, ProfilePaymentInfoBinding> {

    private ProfilePaymentClickListener listener;

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view == getViewBinding().addCreditCard) {
                if (listener != null) {
                    listener.onAddCreditCardClick();
                }
            }
        }
    };

    public ProfilePaymentInfoView(Context context) {
        this(context, null, 0);
    }

    public ProfilePaymentInfoView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ProfilePaymentInfoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        if (isInEditMode()) {
            addView(inflate(context, R.layout.v_profile_payment_info, null));
            return;
        }

        createViewBinding(R.layout.v_profile_payment_info);
    }

    @Override
    protected void initDependencies() {
        super.initDependencies();

        getViewBinding().addCreditCard.setOnClickListener(mOnClickListener);
    }

    public void setProfilePaymentClickListener(ProfilePaymentClickListener listener) {
        this.listener = listener;
    }

    public void setPaymentProfile(EHIPaymentProfile paymentProfile, boolean mayAddCreditCard) {
        getViewBinding().billingCodeContainer.removeAllViewsInLayout();
        getViewBinding().creditCardContainer.removeAllViewsInLayout();
        LayoutInflater inflater = LayoutInflater.from(getContext());

        if (ListUtils.isEmpty(paymentProfile.getBillingPaymentMethods())) {
            getViewBinding().billingCodeLayout.setVisibility(GONE);
        } else {
            getViewBinding().billingCodeLayout.setVisibility(VISIBLE);
            fillSection(inflater, paymentProfile.getBillingPaymentMethods(),
                    getViewBinding().billingCodeContainer,
                    getViewBinding().billingCodeLayout);
        }

        getViewBinding().creditCardHeader.setVisibility(VISIBLE);
        if (ListUtils.isEmpty(paymentProfile.getBillingPaymentMethods())
                && ListUtils.isEmpty(paymentProfile.getCardPaymentMethods())) {
            // show no payment method message
            getViewBinding().creditCardHeader.setVisibility(GONE);
            getViewBinding().creditCardContainer.setVisibility(GONE);
            getViewBinding().warningView.setVisibility(VISIBLE);
            getViewBinding().warningViewTitle.setVisibility(GONE);
            getViewBinding().warningViewText.setText(R.string.profile_payment_options_no_payment_text);
        } else if (ListUtils.isEmpty(paymentProfile.getCardPaymentMethods())) {
            // show no credit card message
            getViewBinding().creditCardContainer.setVisibility(GONE);
            getViewBinding().warningView.setVisibility(VISIBLE);
            getViewBinding().warningViewTitle.setVisibility(GONE);
            getViewBinding().warningViewText.setText(R.string.profile_payment_options_no_credit_card_text);
        } else {
            getViewBinding().creditCardContainer.setVisibility(VISIBLE);
            getViewBinding().warningView.setVisibility(GONE);
            fillSection(inflater, paymentProfile.getCardPaymentMethods(),
                    getViewBinding().creditCardContainer,
                    getViewBinding().creditCardLayout);
        }

        if (mayAddCreditCard && paymentProfile.getCardPaymentMethods().size() == MAX_CREDIT_CARDS) {
            // max message
            getViewBinding().warningView.setVisibility(VISIBLE);
            getViewBinding().warningViewTitle.setVisibility(VISIBLE);
            getViewBinding().warningViewText.setText(
                    new TokenizedString.Formatter<EHIStringToken>(getResources())
                            .formatString(R.string.profile_payment_options_max_credit_card_text)
                            .addTokenAndValue(EHIStringToken.COUNT, String.valueOf(MAX_CREDIT_CARDS))
                            .format()
            );

            // max credit card qtd - may not add more
            mayAddCreditCard = false;
        } else if (getViewBinding().creditCardContainer.getVisibility() == VISIBLE) {
            getViewBinding().warningView.setVisibility(GONE);
        }

        getViewBinding().addCreditCard.setVisibility(
                mayAddCreditCard ? VISIBLE : GONE
        );
    }

    private void fillSection(LayoutInflater inflater, List<EHIPaymentMethod> methods, LinearLayout container, LinearLayout layout) {
        if (methods.size() > 0) {

            Collections.sort(methods);

            for (int i = 0; i < methods.size(); i++) {
                ItemProfilePhonePaymentViewBinding binding = DataBindingUtil.inflate(
                        inflater,
                        R.layout.item_profile_phone_payment,
                        container,
                        false
                );
                EHIPaymentMethod method = methods.get(i);

                binding.itemProfilePhonePaymentView.setText(method.getMaskedCreditCardNumber());

                if (method.getPaymentType().equals(EHIPaymentMethod.TYPE_CREDIT_CARD)) {
                    binding.itemProfilePhonePaymentExpView.setVisibility(VISIBLE);

                    @FontRes int pathToTypeface = R.font.source_sans_light;
                    int expMessageResId = R.string.profile_payment_options_expires_text;
                    if (method.isExpired()) {
                        binding.itemProfilePhonePaymentExpView.setCompoundDrawablesWithIntrinsicBounds(
                                R.drawable.icon_alert_03, 0, 0, 0
                        );

                        pathToTypeface =  R.font.source_sans_bold;
                        expMessageResId = R.string.profile_payment_options_expired_text;
                    }

                    CharSequence expirationMessage = new TokenizedString.Formatter<EHIStringToken>(getResources())
                            .formatString(expMessageResId)
                            .addTokenAndValue(EHIStringToken.DATE, method.getExpirationDateAsLocalizedString())
                            .format();

                    SpannableString spannableString = new SpannableString(expirationMessage);
                    spannableString.setSpan(new CustomTypefaceSpan("sans-serif",
                            ResourcesCompat.getFont(getContext(), pathToTypeface)),
                            0, expirationMessage.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                    binding.itemProfilePhonePaymentExpView.setText(spannableString);

                    binding.itemProfilePhonePaymentView.setCompoundDrawablesWithIntrinsicBounds(BaseAppUtils.getCardIconByType(method.getCardType()), 0, 0, 0);
                    binding.itemProfilePhonePaymentView.setCompoundDrawablePadding((int)getViewBinding().getRoot().getResources().getDimension(R.dimen.padding_xxsmall));
                } else {
                    binding.itemProfilePhonePaymentExpView.setVisibility(GONE);
                }

                binding.preferredMark.setVisibility(
                        method.isPreferred() ? VISIBLE : GONE
                );

                container.addView(binding.getRoot());
            }
        }
    }

    public interface ProfilePaymentClickListener {
        void onAddCreditCardClick();
    }

}