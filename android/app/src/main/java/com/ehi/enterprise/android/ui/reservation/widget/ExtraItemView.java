package com.ehi.enterprise.android.ui.reservation.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.CompoundButton;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.app.EHIStringToken;
import com.ehi.enterprise.android.databinding.ExtraItemViewBinding;
import com.ehi.enterprise.android.models.reservation.EHIExtraItem;
import com.ehi.enterprise.android.models.reservation.EHIPaymentLineItem;
import com.ehi.enterprise.android.ui.reservation.CarClassExtrasFragment;
import com.ehi.enterprise.android.ui.reservation.interfaces.OnExtraActionListener;
import com.ehi.enterprise.android.ui.view.DataBindingViewModelView;
import com.ehi.enterprise.android.ui.viewmodel.ManagersAccessViewModel;
import com.ehi.enterprise.android.utils.DisplayUtils;
import com.ehi.enterprise.android.utils.analytics.EHIAnalytics;
import com.ehi.enterprise.android.utils.analytics.EHIAnalyticsDictionaryUtils;
import com.ehi.enterprise.android.utils.analytics.EHIAnalyticsEvent;
import com.isobar.android.tokenizedstring.TokenizedString;
import com.isobar.android.viewmodel.ViewModel;

@ViewModel(ManagersAccessViewModel.class)
public class ExtraItemView extends DataBindingViewModelView<ManagersAccessViewModel, ExtraItemViewBinding> {

    private static final String TAG = ExtraItemView.class.getSimpleName();

    private static final long ANIMATION_DURATION = 300;

    private int mMaxCount = 1;
    private int mCurrentCount = 1;
    private int mDescriptionHeight = 0;

    private boolean mDescriptionVisible = false;

    private EHIExtraItem mExtraItem;

    private OnExtraActionListener mListener;

    private OnClickListener mOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v == getViewBinding().compoundRowView) {
                getViewBinding().checkBox.setOnCheckedChangeListener(null);
                mOnCheckedChangeListener.onCheckedChanged(getViewBinding().checkBox,
                        getViewBinding().compoundRowView.isChecked());
                getViewBinding().checkBox.setOnCheckedChangeListener(mOnCheckedChangeListener);
            }
            if (v == getViewBinding().arrowImageView) {
                if (!mDescriptionVisible) {
                    EHIAnalyticsEvent.create()
                            .screen(EHIAnalytics.Screen.SCREEN_EXTRAS.value, CarClassExtrasFragment.SCREEN_NAME)
                            .state(EHIAnalytics.State.STATE_SUMMARY.value)
                            .action(EHIAnalytics.Motion.MOTION_TAP.value, EHIAnalytics.Action.ACTION_EXPAND.value)
                            .addDictionary(EHIAnalyticsDictionaryUtils.reservation())
                            .tagScreen()
                            .tagEvent();
                }
                setDescriptionVisibility(!mDescriptionVisible);
            } else if (v == getViewBinding().moreInfoButton) {
                if (mListener != null) {
                    EHIAnalyticsEvent.create()
                            .screen(EHIAnalytics.Screen.SCREEN_EXTRAS.value, CarClassExtrasFragment.SCREEN_NAME)
                            .state(EHIAnalytics.State.STATE_SUMMARY.value)
                            .action(EHIAnalytics.Motion.MOTION_TAP.value, EHIAnalytics.Action.ACTION_SHOW_MORE.value)
                            .addDictionary(EHIAnalyticsDictionaryUtils.reservation())
                            .tagScreen()
                            .tagEvent();
                    mListener.onClick(mExtraItem);
                }
            } else if (v == getViewBinding().minusButton) {
                mCurrentCount--;
                if (mCurrentCount == 0) {
                    mCurrentCount = 1;
                } else {
                    if (mListener != null) {
                        mListener.onChangeExtraCount(mExtraItem, mCurrentCount);
                    }
                }
                updateButtonsState();
                getViewBinding().counterText.setText(mCurrentCount + "");

            } else if (v == getViewBinding().plusButton) {
                mCurrentCount++;
                if (mCurrentCount > mMaxCount) {
                    mCurrentCount = mMaxCount;
                } else {
                    if (mListener != null) {
                        mListener.onChangeExtraCount(mExtraItem, mCurrentCount);
                    }
                }
                updateButtonsState();
                getViewBinding().counterText.setText(mCurrentCount + "");
            }
        }
    };


    private CompoundButton.OnCheckedChangeListener mOnCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (mMaxCount == 1) {
                if (isChecked) {
                    mCurrentCount = 1;
                } else {
                    mCurrentCount = 0;
                }
                updateButtonsState();
                if (mListener != null) {
                    mListener.onChangeExtraCount(mExtraItem, mCurrentCount);
                }
            } else {
                if (mCurrentCount < 1) {
                    mCurrentCount = 1;
                }
                updateButtonsState();
                setCountAreaVisibility(isChecked);
                if (isChecked) {
                    if (mListener != null) {
                        mListener.onChangeExtraCount(mExtraItem, mCurrentCount);
                    }
                } else {
                    if (mListener != null) {
                        mListener.onChangeExtraCount(mExtraItem, 0);
                    }
                }
            }
        }
    };


    public ExtraItemView(Context context) {
        this(context, null, 0);
    }

    public ExtraItemView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ExtraItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        if (isInEditMode()) {
            addView(inflate(context, R.layout.v_extra_item, null));
            return;
        }

        createViewBinding(R.layout.v_extra_item);
        initViews();
    }

    private void initViews() {
        getViewBinding().compoundRowView.setOnClickListener(mOnClickListener);

        getViewBinding().arrowImageView.setRotation(90);
        getViewBinding().arrowImageView.setOnClickListener(mOnClickListener);

        getViewBinding().counterArea.post(new Runnable() {
            @Override
            public void run() {
                ViewGroup.LayoutParams params = getViewBinding().counterArea.getLayoutParams();
                params.height = 0;
                getViewBinding().counterArea.requestLayout();
            }
        });

        getViewBinding().minusButton.setOnClickListener(mOnClickListener);
        getViewBinding().plusButton.setOnClickListener(mOnClickListener);

        setDescription(getViewBinding().descriptionText.getText().toString());

        getViewBinding().moreInfoButton.setOnClickListener(mOnClickListener);
    }

    public void setExtraItem(EHIExtraItem extraItem) {
        mMaxCount = extraItem.getMaxQuantity();
        mCurrentCount = extraItem.getSelectedQuantity();
        updateButtonsState();
        if (mCurrentCount == 0) {
            getViewBinding().counterText.setText("1");
        } else {
            getViewBinding().counterText.setText(mCurrentCount + "");
        }

//        getViewBinding().checkBox.setOnCheckedChangeListener(null);
        getViewBinding().checkBox.setChecked(extraItem.getSelectedQuantity() >= 1);
//        getViewBinding().checkBox.setOnCheckedChangeListener(mOnCheckedChangeListener);

        if (mMaxCount > 1 && mCurrentCount > 0) {
            getViewBinding().counterArea.post(new Runnable() {
                @Override
                public void run() {
                    ViewGroup.LayoutParams params = getViewBinding().counterArea.getLayoutParams();
                    params.height = (int) DisplayUtils.dipToPixels(getContext(), 55);
                    getViewBinding().counterArea.requestLayout();
                }
            });
        } else {
            getViewBinding().counterArea.post(new Runnable() {
                @Override
                public void run() {
                    ViewGroup.LayoutParams params = getViewBinding().counterArea.getLayoutParams();
                    params.height = 0;
                    getViewBinding().counterArea.requestLayout();
                }
            });
        }
        getViewBinding().completeAmount.setText(extraItem.getTotalAmountView().getFormattedPrice(false));

        if (mExtraItem != null
                && extraItem.getCode().equals(mExtraItem.getCode())) {
            return;
        } else {
            mExtraItem = extraItem;
        }

        getViewBinding().extraTitle.setText(mExtraItem.getName());
        fillSubtitleForItem(mExtraItem);

        ViewGroup.LayoutParams params = getViewBinding().descriptionContainer.getLayoutParams();
        params.height = 0;
        getViewBinding().descriptionContainer.requestLayout();

        setDescription(mExtraItem.getDescription());
    }

    private void fillSubtitleForItem(EHIExtraItem item) {
        StringBuilder subtitle = new StringBuilder();
        if (item.getRateType() != null) {
            TokenizedString.Formatter formatter = new TokenizedString.Formatter<EHIStringToken>(getResources())
                    .formatString(R.string.reservation_line_item_rental_rate_title)
                    .addTokenAndValue(EHIStringToken.PRICE, String.valueOf(mExtraItem.getRateAmountView().getFormattedPrice(false)));
            if (item.getRateType().equalsIgnoreCase(EHIPaymentLineItem.HOURLY)) {
                subtitle.append(formatter.addTokenAndValue(EHIStringToken.UNIT, getResources().getString(R.string.reservation_rate_hourly_unit)).format());
            } else if (item.getRateType().equalsIgnoreCase(EHIPaymentLineItem.DAILY)) {
                subtitle.append(formatter.addTokenAndValue(EHIStringToken.UNIT, getResources().getString(R.string.reservation_rate_daily_unit)).format());
            } else if (item.getRateType().equalsIgnoreCase(EHIPaymentLineItem.WEEKLY)) {
                subtitle.append(formatter.addTokenAndValue(EHIStringToken.UNIT, getResources().getString(R.string.reservation_rate_weekly_unit)).format());
            } else if (item.getRateType().equalsIgnoreCase(EHIPaymentLineItem.MONTHLY)) {
                subtitle.append(formatter.addTokenAndValue(EHIStringToken.UNIT, getResources().getString(R.string.reservation_rate_monthly_unit)).format());
            } else if (item.getRateType().equalsIgnoreCase(EHIPaymentLineItem.MILES)) {
                subtitle.append(formatter.addTokenAndValue(EHIStringToken.UNIT, getResources().getString(R.string.reservation_rate_mile_unit)).format());
            } else if (item.getRateType().equalsIgnoreCase(EHIPaymentLineItem.RENTAL)) {
                subtitle.append(formatter.addTokenAndValue(EHIStringToken.UNIT, getResources().getString(R.string.reservation_rate_rental_unit)).format());
            }
        }
        if (item.getMaxAmountView() != null
                && !Double.valueOf(0.0d).equals(item.getMaxAmountView().getDoubleAmmount())) {
            subtitle.append(new TokenizedString.Formatter(getResources())
                    .formatString(R.string.extra_max)
                    .addTokenAndValue(EHIStringToken.PRICE,
                            String.valueOf(item.getMaxAmountView().getFormattedPrice(false)))
                    .format());
        }

        if (subtitle.length() == 0 && item.getStatus().equals(EHIExtraItem.WAIVED)) {
            subtitle.append(getContext().getString(R.string.car_extras_waived_extra_pricing_info));
        }

        getViewBinding().extraSubtitle.setVisibility(subtitle.length() != 0 ? View.VISIBLE : View.GONE);
        getViewBinding().extraSubtitle.setText(subtitle);
    }

    public void setOnExtraActionListener(OnExtraActionListener listener) {
        mListener = listener;
    }

    private void setDescription(String description) {
        getViewBinding().descriptionText.setText(description);
        getViewBinding().descriptionContainer.measure(MeasureSpec.makeMeasureSpec(DisplayUtils.getScreenWidth(getContext()), MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(DisplayUtils.getScreenHeight(getContext()), MeasureSpec.AT_MOST));
        mDescriptionHeight = getViewBinding().descriptionContainer.getMeasuredHeight();
        getViewBinding().descriptionContainer.post(new Runnable() {
            @Override
            public void run() {
                ViewGroup.LayoutParams params = getViewBinding().descriptionContainer.getLayoutParams();
                params.height = 0;
                mDescriptionVisible = false;
                getViewBinding().descriptionContainer.setLayoutParams(params);
                getViewBinding().descriptionContainer.requestLayout();
            }
        });
    }


    private void setCountAreaVisibility(boolean visible) {
        int beginValue;
        int endValue;
        if (visible) {
            beginValue = 0;
            endValue = (int) DisplayUtils.dipToPixels(getContext(), 55);
        } else {
            beginValue = getViewBinding().counterArea.getLayoutParams().height;
            endValue = 0;
        }

        final ViewGroup.LayoutParams counterViewParam = getViewBinding().counterArea.getLayoutParams();

        ValueAnimator valueAnimator = ValueAnimator.ofInt(beginValue, endValue);
        valueAnimator.setDuration(ANIMATION_DURATION);
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                counterViewParam.height = (Integer) valueAnimator.getAnimatedValue();
                getViewBinding().counterArea.requestLayout();
            }
        });
        valueAnimator.start();
    }

    private void setDescriptionVisibility(boolean visible) {
        int beginValue;
        int endValue;
        int rotation;
        if (visible) {
            beginValue = 0;
            endValue = mDescriptionHeight;
            rotation = -90;
        } else {
            beginValue = getViewBinding().descriptionContainer.getLayoutParams().height;
            endValue = 0;
            rotation = 90;
        }

        final ViewGroup.LayoutParams descriptionViewParam = getViewBinding().descriptionContainer.getLayoutParams();
        ValueAnimator valueAnimator = ValueAnimator.ofInt(beginValue, endValue);
        valueAnimator.setDuration(ANIMATION_DURATION);
        valueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                descriptionViewParam.height = (Integer) valueAnimator.getAnimatedValue();
                getViewBinding().descriptionContainer.requestLayout();
            }
        });
        valueAnimator.start();

        getViewBinding().arrowImageView
                .animate()
                .setDuration(ANIMATION_DURATION)
                .setInterpolator(new LinearInterpolator())
                .rotation(rotation);

        mDescriptionVisible = visible;
    }

    private void updateButtonsState() {
        if (mCurrentCount <= 1) {
            getViewBinding().minusButton.setBackgroundResource(R.drawable.dark_gray_button_touch_overlay);
        } else {
            getViewBinding().minusButton.setBackgroundResource(R.drawable.st_ehi_primary_secondary);
        }

        if (mCurrentCount >= mMaxCount) {
            getViewBinding().plusButton.setBackgroundResource(R.drawable.dark_gray_button_touch_overlay);
        } else {
            getViewBinding().plusButton.setBackgroundResource(R.drawable.st_ehi_primary_secondary);
        }
    }
}
