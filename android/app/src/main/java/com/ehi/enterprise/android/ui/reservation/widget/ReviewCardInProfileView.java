package com.ehi.enterprise.android.ui.reservation.widget;

import android.content.Context;
import android.support.annotation.FontRes;
import android.support.annotation.Nullable;
import android.support.v4.content.res.ResourcesCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CompoundButton;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.app.EHIStringToken;
import com.ehi.enterprise.android.databinding.CreditCardInProfileViewBinding;
import com.ehi.enterprise.android.models.profile.EHIPaymentMethod;
import com.ehi.enterprise.android.ui.view.DataBindingViewModelView;
import com.ehi.enterprise.android.ui.viewmodel.ManagersAccessViewModel;
import com.ehi.enterprise.android.utils.BaseAppUtils;
import com.ehi.enterprise.android.utils.CustomTypefaceSpan;
import com.isobar.android.tokenizedstring.TokenizedString;
import com.isobar.android.viewmodel.ViewModel;

@ViewModel(ManagersAccessViewModel.class)
public class ReviewCardInProfileView extends DataBindingViewModelView<ManagersAccessViewModel, CreditCardInProfileViewBinding> {

    private OnClickListener mOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v == getViewBinding().haveReadConditions || v == getViewBinding().haveReadConditionsView) {
                if (mReviewCardPolicyListener != null) {
                    mReviewCardPolicyListener.onPrepaymentPolicyClick();
                }
            } else if (v == getViewBinding().getRoot()) {
                if (mCreditCardViewClickListener != null) {
                    mCreditCardViewClickListener.editCreditCard();
                }
            }
        }
    };

    @Nullable
    private ReviewCardNoInfoView.ReviewPrepayAddPaymentListener mReviewCardPolicyListener;
    private ReviewCardNoInfoView.CreditCardViewClickListener mCreditCardViewClickListener;

    public ReviewCardInProfileView(Context context) {
        this(context, null, 0);
    }

    public ReviewCardInProfileView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ReviewCardInProfileView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (isInEditMode()) {
            addView(inflate(context, R.layout.v_review_card_in_profile, null));
        } else {
            createViewBinding(R.layout.v_review_card_in_profile);
        }
    }

    public void populateView() {
        getViewBinding().getRoot().setOnClickListener(mOnClickListener);
        getViewBinding().haveReadConditions.setOnClickListener(mOnClickListener);
        getViewBinding().haveReadConditionsView.setOnClickListener(mOnClickListener);
    }

    public void buildView(EHIPaymentMethod method) {
        getViewBinding().itemReviewCreditCardCompanyView.setText(method.getAliasOrType(getResources()));
        getViewBinding().itemReviewCreditCardMaskView.setText(method.getMaskedCreditCardNumber());

        @FontRes int pathToTypeface = R.font.source_sans_light;
        int expMessageResId = R.string.profile_payment_options_expires_text;
        if (method.isExpired()) {
            getViewBinding().itemReviewCreditCardExpView.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.icon_alert_03, 0, 0, 0
            );
            getViewBinding().itemReviewCreditCardExpView.setCompoundDrawablePadding((int) getViewBinding().getRoot().getResources().getDimension(R.dimen.margin_xsmall));

            pathToTypeface = R.font.source_sans_bold;
            expMessageResId = R.string.profile_payment_options_expired_text;
        } else {
            getViewBinding().itemReviewCreditCardExpView.setCompoundDrawablePadding(0);
            getViewBinding().itemReviewCreditCardExpView.setCompoundDrawablesWithIntrinsicBounds(
                    0, 0, 0, 0
            );
        }

        final CharSequence expirationMessage = new TokenizedString.Formatter<EHIStringToken>(getResources())
                .formatString(expMessageResId)
                .addTokenAndValue(EHIStringToken.DATE, method.getExpirationDateAsLocalizedString())
                .format();

        SpannableString spannableString = new SpannableString(expirationMessage);
        spannableString.setSpan(new CustomTypefaceSpan("sans-serif",
                ResourcesCompat.getFont(getContext(), pathToTypeface)),
                0, expirationMessage.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        getViewBinding().itemReviewCreditCardExpView.setText(spannableString);

        getViewBinding().itemReviewCreditCardMaskView.setCompoundDrawablesWithIntrinsicBounds(BaseAppUtils.getCardIconByType(method.getCardType()), 0, 0, 0);
        getViewBinding().itemReviewCreditCardMaskView.setCompoundDrawablePadding((int) getViewBinding().getRoot().getResources().getDimension(R.dimen.padding_xxsmall));

        SpannableString textToShow = new SpannableString(getResources().getString(R.string.terms_and_conditions_prepay_title));
        textToShow.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.ehi_primary)), 0, textToShow.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        getViewBinding().haveReadConditions.setText(new TokenizedString.Formatter<EHIStringToken>(getResources())
                .formatString(R.string.review_prepay_policies_read)
                .addTokenAndValue(EHIStringToken.POLICIES, textToShow)
                .format());
    }

    public void setReviewCardPolicyListener(@Nullable ReviewCardNoInfoView.ReviewPrepayAddPaymentListener reviewCardPolicyListener) {
        this.mReviewCardPolicyListener = reviewCardPolicyListener;
    }

    public void setCreditCardViewClickListener(ReviewCardNoInfoView.CreditCardViewClickListener listener) {
        mCreditCardViewClickListener = listener;
    }

    public void setTermsAndConditionsCheckListener(CompoundButton.OnCheckedChangeListener listener) {
        getViewBinding().haveReadConditionsCheckBox.setOnCheckedChangeListener(listener);
        getViewBinding().prepayTermsAndConditionsView.setVisibility(VISIBLE);
    }

    public void check(boolean value) {
        getViewBinding().haveReadConditionsCheckBox.setChecked(value);
    }

    public void setTermsAndConditionsClickListener(OnClickListener listener) {
        getViewBinding().haveReadConditions.setOnClickListener(listener);
    }

    public void showTermsCheckBox(Boolean showCheckbox) {
        getViewBinding().prepayTermsAndConditionsView.setVisibility(showCheckbox ? VISIBLE : GONE);
        getViewBinding().haveReadConditionsView.setVisibility(showCheckbox ? GONE : VISIBLE);
    }

    public void toggleVisibility() {
        setVisibility(getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
    }
}