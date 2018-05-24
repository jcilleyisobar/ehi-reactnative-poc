package com.ehi.enterprise.android.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.databinding.DataBindingUtil;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.databinding.SubtitleButtonViewBinding;
import com.ehi.enterprise.android.utils.DisplayUtils;
import com.ehi.enterprise.android.utils.EHITextUtils;

import io.dwak.reactor.ReactorComputation;
import io.dwak.reactor.ReactorVar;
import io.dwak.reactor.interfaces.ReactorComputationFunction;
import io.dwak.reactorbinding.Preconditions;

public class RateButton extends FrameLayout {
    private SubtitleButtonViewBinding mBinding;
    private CharSequence mPrimaryText;
    private CharSequence mSecondaryText;
    private CharSequence mPrice;
    private CharSequence mPriceSubtitle;
    private CharSequence mSavingPrice;

    private OnClickListener mOnDisabledClickListener;

    public RateButton(final Context context) {
        this(context, null);
    }

    public RateButton(final Context context, final AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RateButton(final Context context, final AttributeSet attrs, final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (!isInEditMode()) {
            mBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.v_rate_button, this, true);
            loadTextFromAttributes(context, attrs);
            setPrimaryText(mPrimaryText);
            setSecondaryText(mSecondaryText);
        }
    }

    private void loadTextFromAttributes(final Context context, final AttributeSet attributeSet) {
        final TypedArray array = context.getTheme().obtainStyledAttributes(
                attributeSet,
                R.styleable.RateButton,
                0,
                0
        );

        try {
            mPrimaryText = array.getString(R.styleable.RateButton_primaryText);
            mSecondaryText = array.getString(R.styleable.RateButton_secondaryText);
        } finally {
            array.recycle();
        }
    }

    public void setPrimaryText(final CharSequence text) {
        mPrimaryText = text;
        mBinding.title.setText(mPrimaryText);
    }

    public void setSecondaryText(final CharSequence text) {
        mSecondaryText = text;
        mBinding.subtitle.setText(mSecondaryText);
        if (!EHITextUtils.isEmpty(mSecondaryText)) {
            mBinding.subtitle.setVisibility(View.VISIBLE);
        } else {
            mBinding.subtitle.setVisibility(View.GONE);
        }
    }

    public void setPrice(final CharSequence price) {
        mPrice = price;
        mBinding.price.setText(mPrice);
        if (!EHITextUtils.isEmpty(mPrice)) {
            mBinding.price.setVisibility(View.VISIBLE);
        } else {
            mBinding.price.setVisibility(View.GONE);
        }
    }

    public void setPriceSubtitle(final CharSequence priceSubtitle) {
        mBinding.totalCost.setText(priceSubtitle);
    }

    public void setSavingPrice(CharSequence savingPrice) {
        mSavingPrice = savingPrice;
        mBinding.savingPrice.setVisibility(View.VISIBLE);
        mBinding.savingPrice.setText(mSavingPrice);
    }

    public void setSavingPriceVisibility(Boolean visibility) {
        mBinding.savingPrice.setVisibility(visibility ? View.VISIBLE : View.GONE);
    }

    public void showArrow(final boolean show) {
        mBinding.arrow.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    public void showWarningIcon(final boolean show) {
        if (show) {
            mBinding.subtitle.setCompoundDrawablePadding((int) DisplayUtils.dipToPixels(getContext(), 6f));
            mBinding.subtitle.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.icon_alert_03,
                    0,
                    0,
                    0
            );
        } else {
            mBinding.subtitle.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        }
    }

    public static ReactorComputationFunction primaryText(final ReactorVar<? extends CharSequence> source,
                                                         final RateButton target) {
        return new ReactorComputationFunction() {
            @Override
            public void react(final ReactorComputation reactorComputation) {
                if (Preconditions.checkNotNull(source, source.getValue(), target)) {
                    target.setPrimaryText(source.getValue());
                }
            }
        };
    }

    public static ReactorComputationFunction secondaryText(final ReactorVar<? extends CharSequence> source,
                                                           final RateButton target) {
        return new ReactorComputationFunction() {
            @Override
            public void react(final ReactorComputation reactorComputation) {
                if (Preconditions.checkNotNull(source, source.getValue(), target)) {
                    target.setSecondaryText(source.getValue());
                }
            }
        };
    }

    public static ReactorComputationFunction price(final ReactorVar<? extends CharSequence> source,
                                                   final RateButton target) {
        return new ReactorComputationFunction() {
            @Override
            public void react(final ReactorComputation reactorComputation) {
                if (Preconditions.checkNotNull(source, source.getValue(), target)) {
                    target.setPrice(source.getValue());
                }
            }
        };
    }

    public static ReactorComputationFunction priceSubtitle(final ReactorVar<? extends CharSequence> source,
                                                           final RateButton target) {
        return new ReactorComputationFunction() {
            @Override
            public void react(final ReactorComputation reactorComputation) {
                if (Preconditions.checkNotNull(source, source.getValue(), target)) {
                    target.setPriceSubtitle(source.getValue());
                }
            }
        };
    }

    public static ReactorComputationFunction savingPrice(final ReactorVar<? extends CharSequence> source,
                                                         final RateButton target) {
        return new ReactorComputationFunction() {
            @Override
            public void react(final ReactorComputation reactorComputation) {
                if (Preconditions.checkNotNull(source, source.getValue(), target)) {
                    target.setSavingPrice(source.getValue());
                }
            }
        };
    }

    public static ReactorComputationFunction savingPriceVisibility(final ReactorVar<Boolean> source,
                                                                   final RateButton target) {
        return new ReactorComputationFunction() {
            @Override
            public void react(final ReactorComputation reactorComputation) {
                if (Preconditions.checkNotNull(source, source.getValue(), target)) {
                    target.setSavingPriceVisibility(source.getValue());
                }
            }
        };
    }

    public static ReactorComputationFunction arrow(final ReactorVar<Boolean> source,
                                                   final RateButton target) {
        return new ReactorComputationFunction() {
            @Override
            public void react(final ReactorComputation reactorComputation) {
                if (Preconditions.checkNotNull(source, source.getValue(), target)) {
                    target.showArrow(source.getValue());
                }
            }
        };
    }

    public static ReactorComputationFunction warningIcon(final ReactorVar<Boolean> source,
                                                         final RateButton target) {
        return new ReactorComputationFunction() {
            @Override
            public void react(final ReactorComputation reactorComputation) {
                if (Preconditions.checkNotNull(source, source.getValue(), target)) {
                    target.showWarningIcon(source.getValue());
                }
            }
        };
    }

    /**
     * Set the listener to be invoked when user clicks the disabled button
     *
     * @param disabledClickListener
     */
    public void setOnDisabledClickListener(OnClickListener disabledClickListener) {
        this.mOnDisabledClickListener = disabledClickListener;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isEnabled() && mOnDisabledClickListener != null) {
            final int action = event.getActionMasked();
            if (action == MotionEvent.ACTION_DOWN) {
                mOnDisabledClickListener.onClick(this);
                return true;
            }
        } else {
            return super.onTouchEvent(event);
        }
        return false;
    }

}
