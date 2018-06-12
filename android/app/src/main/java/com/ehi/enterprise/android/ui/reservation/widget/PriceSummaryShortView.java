package com.ehi.enterprise.android.ui.reservation.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.LinearLayout;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.app.EHIStringToken;
import com.ehi.enterprise.android.databinding.ClassDetailsPaymentItemViewBinding;
import com.ehi.enterprise.android.models.reservation.EHICarClassDetails;
import com.ehi.enterprise.android.models.reservation.EHIExtraItem;
import com.ehi.enterprise.android.models.reservation.EHIPaymentLineItem;
import com.ehi.enterprise.android.models.reservation.EHIPriceSummary;
import com.ehi.enterprise.android.models.reservation.EHIVehicleRate;
import com.ehi.enterprise.android.utils.DisplayUtils;
import com.isobar.android.tokenizedstring.TokenizedString;

import io.dwak.reactor.ReactorComputation;
import io.dwak.reactor.ReactorVar;
import io.dwak.reactor.interfaces.ReactorComputationFunction;

public class PriceSummaryShortView extends LinearLayout {

    private static final String TAG = PriceSummaryShortView.class.getSimpleName();

    private OnShortPriceSummaryEventsListener mListener;
    private int mViewHeight = 0;
    private boolean mExpanded = true;

    public PriceSummaryShortView(Context context) {
        this(context, null, 0);
    }

    public PriceSummaryShortView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PriceSummaryShortView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOrientation(LinearLayout.VERTICAL);
    }

    public void setOnShortPriceSummaryEventsListener(OnShortPriceSummaryEventsListener listener) {
        mListener = listener;
    }

    public static ReactorComputationFunction carDetails(final ReactorVar<EHICarClassDetails> details, final ReactorVar<String> payState, final PriceSummaryShortView target) {
        return new ReactorComputationFunction() {
            @Override
            public void react(ReactorComputation reactorComputation) {
                target.setCarClassDetails(details.getValue(), payState.getValue());
                if (!target.isMeasured()) {
                    target.measureAndHideView();
                }
            }
        };
    }

    public void setCarClassDetails(EHICarClassDetails carDetails, String payState) {
        this.removeAllViews();
        final EHIPriceSummary priceSummary = carDetails.getPriceSummary(EHIVehicleRate.PREPAY.equals(payState));
        if (priceSummary == null) {
            return;
        }

        LayoutInflater layoutInflater = LayoutInflater.from(getContext());

        for (final EHIPaymentLineItem ehiPaymentLineItems : priceSummary.getAllPaymentLineItems()) {

            ClassDetailsPaymentItemViewBinding binding = DataBindingUtil.inflate(layoutInflater, R.layout.item_class_detail_payment_item, this, false);

            if (EHIPaymentLineItem.SAVINGS.equals(ehiPaymentLineItems.getCategory())) {
                binding.lineItemText.setText(ehiPaymentLineItems.getDescription(getResources()));
                binding.totalPrice.setText(ehiPaymentLineItems.getTotalAmountView().getFormattedPrice(false));
                this.addView(binding.getRoot());
            }
            if (Double.valueOf(0.0).equals(ehiPaymentLineItems.getRateQuantity())) {
                continue;
            }
            if (EHIPaymentLineItem.VEHICLE_RATE.equals(ehiPaymentLineItems.getCategory())) {

                SpannableStringBuilder bld = new SpannableStringBuilder(ehiPaymentLineItems.getRentalAmountText(getResources()));

                SpannableString rate = new SpannableString(ehiPaymentLineItems.getRentalRateText(getResources()));
                rate.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.extra_subtitle)), 0, ehiPaymentLineItems.getRentalRateText(getResources()).length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                rate.setSpan(new AbsoluteSizeSpan(14, true), 0, ehiPaymentLineItems.getRentalRateText(getResources()).length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                bld.append(" ");
                bld.append(rate);

                binding.lineItemText.setText(bld);
                binding.totalPrice.setText(ehiPaymentLineItems.getTotalAmountView().getFormattedPrice(false));

                this.addView(binding.getRoot());
            }
        }

        //taxes and fees
        {
            ClassDetailsPaymentItemViewBinding binding = DataBindingUtil.inflate(layoutInflater, R.layout.item_class_detail_payment_item, this, false);

            binding.lineItemText.setText(getResources().getString(R.string.class_details_taxes_fees_summary_title));
            binding.lineItemText.setTextColor(getResources().getColor(R.color.ehi_primary));
            binding.lineItemIcon.setVisibility(View.VISIBLE);

            if (priceSummary.isAllTaxesAndFeesIncluded()) {
                binding.totalPrice.setText(R.string.payment_line_item_included);
            } else {
                binding.totalPrice.setText(priceSummary.getEstimatedTaxesFeesView().getFormattedPrice(false));
            }

            binding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        mListener.onTaxesAndFeesClicked(priceSummary);
                    }
                }
            });

            this.addView(binding.getRoot());
        }

        //extras
        for (final EHIExtraItem extraItem : carDetails.getExtras(payState).getIncludedExtras()) {
            ClassDetailsPaymentItemViewBinding binding = DataBindingUtil.inflate(layoutInflater, R.layout.item_class_detail_payment_item, this, false);

            if (extraItem.getName() == null) {
                continue;
            }
            SpannableStringBuilder bld = new SpannableStringBuilder(extraItem.getName());

            if (extraItem.getSelectedQuantity() != null
                    && extraItem.getSelectedQuantity() > 1) {
                SpannableString count = new SpannableString(" (x" + extraItem.getSelectedQuantity() + ")");
                count.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.extra_subtitle)), 0, count.toString().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                count.setSpan(new AbsoluteSizeSpan(14, true), 0, count.toString().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                bld.append(" ");
                bld.append(count);
            }

            binding.lineItemText.setText(bld);

            binding.lineItemText.setTextColor(getResources().getColor(R.color.ehi_primary));
            binding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mListener != null) {
                        mListener.onExtrasClicked(extraItem);
                    }
                }
            });
            binding.totalPrice.setText(getResources().getString(R.string.payment_line_item_included));

            this.addView(binding.getRoot());
        }

        for (final EHIExtraItem extraItem : carDetails.getExtras(payState).getMandatoryExtras()) {
            for (EHIPaymentLineItem lineItem : priceSummary.getAllPaymentLineItems()) {
                if (lineItem.getCode() != null
                        && lineItem.getCode().equalsIgnoreCase(extraItem.getCode())) {

                    ClassDetailsPaymentItemViewBinding binding = DataBindingUtil.inflate(layoutInflater, R.layout.item_class_detail_payment_item, this, false);

                    binding.lineItemText.setTextColor(getResources().getColor(R.color.ehi_primary));
                    SpannableStringBuilder bld = new SpannableStringBuilder(extraItem.getName());

                    if (extraItem.getSelectedQuantity() != null
                            && extraItem.getSelectedQuantity() > 1) {
                        SpannableString count = new SpannableString(" (x" + extraItem.getSelectedQuantity() + ")");
                        count.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.extra_subtitle)), 0, count.toString().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        count.setSpan(new AbsoluteSizeSpan(14, true), 0, count.toString().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        bld.append(" ");
                        bld.append(count);
                    }

                    SpannableString rate = new SpannableString(getResources().getString(R.string.payment_line_item_mandatory));
                    rate.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.extra_subtitle)), 0, getResources().getString(R.string.payment_line_item_mandatory).length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    rate.setSpan(new AbsoluteSizeSpan(14, true), 0, getResources().getString(R.string.payment_line_item_mandatory).length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    bld.append(" ");
                    bld.append(rate);

                    binding.lineItemText.setText(bld);

                    binding.getRoot().setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (mListener != null) {
                                mListener.onExtrasClicked(extraItem);
                            }
                        }
                    });
                    binding.totalPrice.setText(lineItem.getTotalAmountView().getFormattedPrice(false));

                    this.addView(binding.getRoot());
                }
            }
        }

        for (final EHIExtraItem extraItem : carDetails.getExtras(payState).getSelectedExtras()) {
            for (EHIPaymentLineItem lineItem : priceSummary.getAllPaymentLineItems()) {
                if (lineItem.getCode() != null
                        && lineItem.getCode().equalsIgnoreCase(extraItem.getCode())) {

                    ClassDetailsPaymentItemViewBinding binding = DataBindingUtil.inflate(layoutInflater, R.layout.item_class_detail_payment_item, this, false);

                    binding.lineItemText.setTextColor(getResources().getColor(R.color.ehi_primary));

                    SpannableStringBuilder bld = new SpannableStringBuilder(extraItem.getName());

                    if (extraItem.getSelectedQuantity() != null
                            && extraItem.getSelectedQuantity() > 1) {
                        SpannableString count = new SpannableString(" (x" + extraItem.getSelectedQuantity() + ")");
                        count.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.extra_subtitle)), 0, count.toString().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        count.setSpan(new AbsoluteSizeSpan(14, true), 0, count.toString().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        bld.append(" ");
                        bld.append(count);
                    }

                    binding.lineItemText.setText(bld);

                    binding.getRoot().setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (mListener != null) {
                                mListener.onExtrasClicked(extraItem);
                            }
                        }
                    });
                    binding.totalPrice.setText(lineItem.getTotalAmountView().getFormattedPrice(false));
                    this.addView(binding.getRoot());
                }
            }
        }


        //mileage
        if (carDetails.getMileageInfo().isUnlimitedMileage()) {
            ClassDetailsPaymentItemViewBinding binding = DataBindingUtil.inflate(layoutInflater, R.layout.item_class_detail_payment_item, this, false);
            binding.lineItemText.setText(R.string.car_class_mileage_unlimited);
            binding.totalPrice.setText(R.string.payment_line_item_included);
            this.addView(binding.getRoot());
        } else {
            ClassDetailsPaymentItemViewBinding binding = DataBindingUtil.inflate(layoutInflater, R.layout.item_class_detail_payment_item, this, false);
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(String.valueOf(carDetails.getMileageInfo().getTotalFreeMiles()));
            spannableStringBuilder.append(" ");
            spannableStringBuilder.append(carDetails.getMileageInfo().getDistanceUnit());
            CharSequence subtitle = new TokenizedString.Formatter<EHIStringToken>(getResources())
                    .formatString(R.string.car_mileage_rate)
                    .addTokenAndValue(EHIStringToken.MILES, String.valueOf(carDetails.getMileageInfo().getTotalFreeMiles()))
                    .format();
            String subtitleString = subtitle.toString();
            subtitleString += String.format(" %s/%s",
                    carDetails.getMileageInfo().getExcessMileageRateView().getFormattedPrice(false),
                    carDetails.getMileageInfo().getDistanceUnit());

            SpannableString rate = new SpannableString(subtitleString);
            rate.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.extra_subtitle)), 0, subtitleString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            rate.setSpan(new AbsoluteSizeSpan(14, true), 0, subtitleString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannableStringBuilder.append(" ");
            spannableStringBuilder.append(rate);
            binding.lineItemText.setText(spannableStringBuilder);
            binding.totalPrice.setText(R.string.payment_line_item_included);
            this.addView(binding.getRoot());
        }
    }

    public void measureAndHideView() {
        measure(MeasureSpec.makeMeasureSpec(DisplayUtils.getScreenWidth(getContext()), MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(DisplayUtils.getScreenHeight(getContext()), MeasureSpec.AT_MOST));
        mViewHeight = getMeasuredHeight();
        post(new Runnable() {
            @Override
            public void run() {
                ViewGroup.LayoutParams params = getLayoutParams();
                params.height = 0;
                mExpanded = false;
                setLayoutParams(params);
                requestLayout();
            }
        });
    }

    public boolean isMeasured() {
        return mViewHeight != 0;
    }

    public void setBreakdownVisible(boolean visible) {
        int beginValue;
        int endValue;
        if (visible) {
            beginValue = 0;
            endValue = mViewHeight;
        } else {
            beginValue = getLayoutParams().height;
            endValue = 0;
        }

        final ViewGroup.LayoutParams descriptionViewParam = getLayoutParams();
        ValueAnimator valueAnimator = ValueAnimator.ofInt(beginValue, endValue);
        valueAnimator.setDuration(300);
        valueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                descriptionViewParam.height = (Integer) valueAnimator.getAnimatedValue();
                requestLayout();
            }
        });
        valueAnimator.start();

        mExpanded = visible;
    }

    public boolean isExpanded() {
        return mExpanded;
    }

    public static ReactorComputationFunction carClassDetails(final ReactorVar<EHICarClassDetails> source, final ReactorVar<String> payState, final PriceSummaryShortView target) {
        return new ReactorComputationFunction() {
            @Override
            public void react(final ReactorComputation reactorComputation) {
                target.setCarClassDetails(source.getValue(), payState.getValue());
            }
        };
    }

    public interface OnShortPriceSummaryEventsListener {

        void onExtrasClicked(EHIExtraItem item);

        void onTaxesAndFeesClicked(EHIPriceSummary priceSummary);
    }
}
