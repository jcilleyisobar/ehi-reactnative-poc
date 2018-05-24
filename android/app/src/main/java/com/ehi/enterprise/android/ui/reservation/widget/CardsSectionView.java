package com.ehi.enterprise.android.ui.reservation.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CompoundButton;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.databinding.CardsSectionBinding;
import com.ehi.enterprise.android.models.profile.EHIPaymentMethod;
import com.ehi.enterprise.android.ui.view.DataBindingViewModelView;
import com.ehi.enterprise.android.ui.viewmodel.ManagersAccessViewModel;
import com.isobar.android.viewmodel.ViewModel;

@ViewModel(ManagersAccessViewModel.class)
public class CardsSectionView extends DataBindingViewModelView<ManagersAccessViewModel, CardsSectionBinding> {

    public CardsSectionView(Context context) {
        this(context, null, 0);
    }

    public CardsSectionView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CardsSectionView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (isInEditMode()) {
            addView(inflate(context, R.layout.v_review_cards_section, null));
        } else {
            createViewBinding(R.layout.v_review_cards_section);
            initViews();
        }
    }

    private void initViews() {
        getViewBinding().cardInProfileView.populateView();
        getViewBinding().cardNoInfoView.populateView();
    }

    @Override
    protected void initDependencies() {
        super.initDependencies();
    }

    public void setPaymentMethod(EHIPaymentMethod method) {
        getViewBinding().cardNoInfoView.setVisibility(View.GONE);
        getViewBinding().cardInProfileView.setVisibility(View.VISIBLE);
        getViewBinding().cardInProfileView.buildView(method);
    }

    public void togglePrepayAddPaymentVisibility() {
        getViewBinding().cardNoInfoView.toggleVisibility();
        getViewBinding().cardInProfileView.toggleVisibility();
    }

    public void setAddPrepayViewListeners(ReviewCardNoInfoView.CreditCardViewClickListener creditCardViewClickListener,
                                          ReviewCardNoInfoView.ReviewPrepayAddPaymentListener reviewPrepayAddPaymentListener) {
        getViewBinding().cardNoInfoView.setAddPrepayListener(creditCardViewClickListener);
        getViewBinding().cardNoInfoView.setReviewPrepayAddPaymentListener(reviewPrepayAddPaymentListener);
    }

    public void setPaymentModifyUnavailableListener(ReviewPaymentModifyUnavailableView.PaymentModifyUnavailableListener listener) {
        getViewBinding().paymentModifyUnavailableView.setPaymentModifyUnavailableListener(listener);
    }

    public void setCreditCardAdded(boolean cardAdded) {
        getViewBinding().cardInProfileView.setVisibility(View.GONE);
        getViewBinding().cardNoInfoView.setCreditCardAddedButtonVisible(cardAdded);
        getViewBinding().cardNoInfoView.setAddPaymentButtonVisible(!cardAdded);
        getViewBinding().cardNoInfoView.setCreditCardAdded(cardAdded);
    }

    public void showPrepayAddPaymentView() {
        getViewBinding().cardNoInfoView.setVisibility(VISIBLE, true);
    }

    public void hidePrepayAddPaymentView() {
        getViewBinding().cardNoInfoView.setVisibility(GONE, true);
    }

    public void setReviewCardPolicyListener(ReviewCardNoInfoView.ReviewPrepayAddPaymentListener reviewCardPolicyListener) {
        getViewBinding().cardInProfileView.setReviewCardPolicyListener(reviewCardPolicyListener);
    }

    public void setReviewCardListener(ReviewCardNoInfoView.CreditCardViewClickListener creditCardViewClickListener) {
        getViewBinding().cardInProfileView.setCreditCardViewClickListener(creditCardViewClickListener);
    }

    public void setReviewCardInProfileViewChecked(boolean checked) {
        getViewBinding().cardInProfileView.check(checked);
    }

    public void setReviewCardInProfileViewTermsCheckListener(CompoundButton.OnCheckedChangeListener listener) {
        getViewBinding().cardInProfileView.setTermsAndConditionsCheckListener(listener);
    }

    public void setReviewCardInProfileViewTermsClickListener(OnClickListener listener) {
        getViewBinding().cardInProfileView.setTermsAndConditionsClickListener(listener);
    }

    public void showReviewCardInProfileViewTermsCheckBox(boolean value) {
        getViewBinding().cardInProfileView.showTermsCheckBox(value);
    }

    public void showPaymentUnavailableView() {
        getViewBinding().paymentModifyUnavailableView.setVisibility(View.VISIBLE);
    }
}
