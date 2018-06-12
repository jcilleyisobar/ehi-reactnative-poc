package com.ehi.enterprise.android.ui.reservation.widget;

import android.content.Context;
import android.util.AttributeSet;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.app.EHIStringToken;
import com.ehi.enterprise.android.databinding.PriceSummaryViewBinding;
import com.ehi.enterprise.android.models.reservation.EHIMileageInfo;
import com.ehi.enterprise.android.models.reservation.EHIReservation;
import com.ehi.enterprise.android.ui.reservation.interfaces.OnExtraActionListener;
import com.ehi.enterprise.android.ui.view.DataBindingViewModelView;
import com.isobar.android.tokenizedstring.TokenizedString;
import com.isobar.android.viewmodel.ViewModel;

import io.dwak.reactor.ReactorComputation;
import io.dwak.reactor.interfaces.ReactorComputationFunction;
import io.dwak.reactorbinding.view.ReactorView;
import io.dwak.reactorbinding.widget.ReactorTextView;

@ViewModel(PriceSummaryViewModel.class)
public class PriceSummaryView extends DataBindingViewModelView<PriceSummaryViewModel, PriceSummaryViewBinding> {

    private static final String TAG = "PriceSummaryView";

    private OnExtraActionListener mOnExtraActionClickListener;
    private PriceSummaryListener mPriceSummaryListener;

    //region constructors
    public PriceSummaryView(Context context) {
        this(context, null, 0);
    }

    public PriceSummaryView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PriceSummaryView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        if (isInEditMode()) {
            addView(inflate(context, R.layout.v_price_summary, null));
            return;
        }

        createViewBinding(R.layout.v_price_summary);
    }
    //endregion

    @Override
    protected void initDependencies() {
        super.initDependencies();
        bind(ReactorView.visibility(getViewModel().mileageArea.visibility(), getViewBinding().mileageArea));

        bind(ReactorView.visibility(getViewModel().estimatedTotalView.visibility(), getViewBinding().estimatedTotal));
        bind(ReactorView.visibility(getViewModel().totalPaidAmountView.visibility(), getViewBinding().totalPaidAmount));

        bind(ReactorView.visibility(getViewModel().estimatedTotalModifyView.visibility(), getViewBinding().estimatedTotalModify));
        bind(ReactorView.visibility(getViewModel().totalPaidAmountModifyView.visibility(), getViewBinding().totalPaidAmountModify));

        bind(ReactorTextView.textRes(getViewModel().estimatedTotalLabelText.textRes(), getViewBinding().estimatedTotalTextView));
        bind(ReactorTextView.text(getViewModel().estimatedTotalText.textCharSequence(), getViewBinding().estimatedTotal));
        bind(ReactorTextView.text(getViewModel().estimatedTotalTextModify.text(), getViewBinding().estimatedTotalModify));

        bind(ReactorView.visibility(getViewModel().totalPaidView.visibility(), getViewBinding().totalPaidView));
        bind(ReactorTextView.textRes(getViewModel().totalPaidAmountLabelText.textRes(), getViewBinding().totalPaidAmountTextView));
        bind(ReactorTextView.textRes(getViewModel().totalPaidAmountDescLabelText.textRes(), getViewBinding().totalPaidAmountDescTextView));
        bind(ReactorTextView.text(getViewModel().totalPaidAmount.textCharSequence(), getViewBinding().totalPaidAmount));
        bind(ReactorTextView.text(getViewModel().totalPaidAmountModify.textCharSequence(), getViewBinding().totalPaidAmountModify));

        bind(ReactorView.visibility(getViewModel().totalRefundOweView.visibility(), getViewBinding().totalRefundOweView));
        bind(ReactorTextView.textRes(getViewModel().totalRefundOweAmountLabelText.textRes(), getViewBinding().totalRefundOweAmountTextView));
        bind(ReactorTextView.textRes(getViewModel().totalRefundOweAmountDescLabelText.textRes(), getViewBinding().totalRefundOweAmountDescTextView));
        bind(ReactorTextView.text(getViewModel().totalRefundOweAmount.textCharSequence(), getViewBinding().totalRefundOweAmount));
        bind(ReactorTextView.text(getViewModel().actualAmountText.textCharSequence(), getViewBinding().actualAmountTextView));
        bind(ReactorView.visibility(getViewModel().actualAmountText.visibility(), getViewBinding().actualAmountTextView));
        bind(ReactorView.visibility(getViewModel().travelingUsCanadaClarificationText.visibility(), getViewBinding().travelingUsCanadaClarification));
        bind(ReactorTextView.text(getViewModel().travelingUsCanadaClarificationText.textCharSequence(), getViewBinding().travelingUsCanadaClarification));

        bind(ReactorView.visibility(getViewModel().conversionArea.visibility(), getViewBinding().conversionArea));
        bind(ReactorTextView.text(getViewModel().conversionText.textCharSequence(), getViewBinding().conversionText));
        bind(ReactorTextView.text(getViewModel().conversionTotal.text(), getViewBinding().conversionTotal));
        bind(ReactorView.visibility(getViewModel().promoRateArea.visibility(), getViewBinding().customPromotionalRateArea));
        bind(ReactorView.visibility(getViewModel().promoRateTriangle.visibility(), getViewBinding().customPromotionalRateTriangle));
        bind(ReactorTextView.textRes(getViewModel().promoRateText.textRes(), getViewBinding().customPromotionalRateText));

        addReaction("RENTAL_ITEMS", new ReactorComputationFunction() {
            @Override
            public void react(ReactorComputation reactorComputation) {
                if (getViewModel().getReservation() != null) {
                    if (getViewModel().isPrepay() && getViewModel().getReservation().getCarClassDetails().getPrepayPriceSummary() == null
                            || !getViewModel().isPrepay() && getViewModel().getReservation().getCarClassDetails().getPaylaterPriceSummary() == null) {
                        return;
                    }
                    getViewBinding().rentalView.setReservation(getViewModel().getReservation(), getViewModel().isPrepay());
                    getViewBinding().rentalView.hideContent();

                    getViewBinding().miscellaneousView.setReservation(getViewModel().getReservation(), getViewModel().isPrepay());
                    getViewBinding().miscellaneousView.hideContent();

                    getViewBinding().extrasView.setReservation(getViewModel().getReservation(), getViewModel().isPrepay());
                    getViewBinding().extrasView.setOnExtraActionClickListener(mOnExtraActionClickListener);
                    getViewBinding().extrasView.hideContent();

                    getViewBinding().taxesAndFeesView.setReservation(getViewModel().getReservation(), getViewModel().isPrepay());
                    getViewBinding().taxesAndFeesView.setOnPriceSummaryListener(mPriceSummaryListener);
                    getViewBinding().taxesAndFeesView.hideContent();
                }
            }
        });

        addReaction("MILEAGE_ITEM", new ReactorComputationFunction() {
            @Override
            public void react(ReactorComputation reactorComputation) {
                EHIMileageInfo info = getViewModel().mileageInfo.getValue();
                if (info != null) {
                    if (info.isUnlimitedMileage()) {
                        getViewBinding().mileageTitle.setText(R.string.price_section_mileage_unlimited);
                        getViewBinding().mileageSubtitle.setVisibility(GONE);
                    } else {
                        CharSequence title = new TokenizedString.Formatter<EHIStringToken>(getResources())
                                .formatString(R.string.price_section_mileage_included)
                                .addTokenAndValue(EHIStringToken.MILES, info.getTotalFreeMiles() + " " + info.getDistanceUnit())
                                .format();
                        getViewBinding().mileageTitle.setText(title);

                        CharSequence subtitle = new TokenizedString.Formatter<EHIStringToken>(getResources())
                                .formatString(R.string.price_section_mileage_price)
                                .addTokenAndValue(EHIStringToken.PRICE, info.getExcessMileageRateView().getFormattedPrice(false).toString())
                                .addTokenAndValue(EHIStringToken.UNIT, info.getDistanceUnit())
                                .format();

                        getViewBinding().mileageSubtitle.setText(subtitle);
                        getViewBinding().mileageSubtitle.setVisibility(VISIBLE);
                    }
                }
            }
        });
    }

    public void setIsPrepay(boolean isPrepay) {
        getViewModel().setIsPrepay(isPrepay);
    }

    public void setIsModify(boolean isModify) {
        getViewModel().setIsModify(isModify);
    }

    public void setRedemptionInfo(int redeemingDays, int pointsRateADay) {
        getViewBinding().miscellaneousView.setRedemptionInfo(redeemingDays, pointsRateADay);
    }

    public void setReservation(EHIReservation ehiReservation) {
        getViewModel().setReservation(ehiReservation);
    }

    public void setOnExtraActionClickListener(OnExtraActionListener listener) {
        mOnExtraActionClickListener = listener;
    }

    public void setPriceSummaryListener(PriceSummaryListener priceSummaryListener) {
        mPriceSummaryListener = priceSummaryListener;
    }

    public interface PriceSummaryListener {
        void onLearnMoreClicked();
    }

    public ReviewPrepayOrSaveView getPrepayOrSaveView() {
        return getViewBinding().prepayOrSaveView;
    }

}