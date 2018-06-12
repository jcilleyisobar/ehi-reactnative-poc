package com.ehi.enterprise.android.ui.reservation.view_controllers;

import android.animation.Animator;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Pair;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.app.EHIStringToken;
import com.ehi.enterprise.android.models.location.EHIImage;
import com.ehi.enterprise.android.models.reservation.EHICarClassDetails;
import com.ehi.enterprise.android.models.reservation.EHICharge;
import com.ehi.enterprise.android.models.reservation.EHIPriceSummary;
import com.ehi.enterprise.android.ui.reservation.CarClassExtrasFragment;
import com.ehi.enterprise.android.ui.reservation.CarClassListAdapter;
import com.ehi.enterprise.android.ui.reservation.interfaces.ReservationFlowListener;
import com.ehi.enterprise.android.ui.reservation.view_holders.CarClassSelectViewHolder;
import com.ehi.enterprise.android.utils.BaseAppUtils;
import com.ehi.enterprise.android.utils.EHITextUtils;
import com.ehi.enterprise.android.utils.ListUtils;
import com.ehi.enterprise.android.utils.image.EHIImageLoader;
import com.ehi.enterprise.android.utils.image.EHIImageUtils;
import com.ehi.enterprise.android.utils.manager.LocalDataManager;
import com.isobar.android.tokenizedstring.TokenizedString;

import java.util.List;

public class CarClassSelectViewController {

    private boolean mAnimatedOnce;
    private boolean mAnimating;

    public CarClassSelectViewController() {
        mAnimatedOnce = false;
        mAnimating = false;
    }

    public void fillCellWithData(final Context context,
                                 final CarClassSelectViewHolder holder,
                                 @Nullable final EHICarClassDetails carClass,
                                 final CarClassListAdapter.CarClassListAdapterListener listener,
                                 final int position,
                                 final boolean inList,
                                 final boolean showPoints,
                                 final boolean preventAnimations,
                                 final boolean animatedOnce,
                                 final boolean showPreviouslySelected,
                                 final boolean showPrice,
                                 final ReservationFlowListener.PayState payState,
                                 final boolean fromChooseYourRate,
                                 final List<EHICharge> ehiCharges,
                                 final boolean showPointsAnimation) {
        holder.getViewBinding().executePendingBindings();
        if (carClass == null) {
            return;
        }

        //noinspection PointlessBooleanExpression
        if (mAnimatedOnce == false) {
            setAnimatedOnce(animatedOnce);
        }

        if (listener != null) {
            holder.getViewBinding().classDetailsContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!holder.isAnimating()) {
                        listener.onMoreDetailsClicked(carClass);
                    }
                }
            });
            holder.getViewBinding().classCarImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!holder.isAnimating()) {
                        listener.onClassImageClicked(carClass, position);
                    }
                }
            });
            holder.getViewBinding().priceContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!holder.isAnimating()) {
                        listener.onTotalCostClicked(carClass, position);
                    }
                }
            });
        }

        if (!TextUtils.isEmpty(carClass.getName())) {
            holder.getViewBinding().classTitle.setText(carClass.getName());
        }

        if (carClass.isPreviouslySelected() && showPreviouslySelected) {
            holder.getViewBinding().previouslySelectedCarClass.setVisibility(View.VISIBLE);
        } else {
            holder.getViewBinding().previouslySelectedCarClass.setVisibility(View.GONE);
        }

        if (!inList && carClass.isSecretRateAfterCarSelected()) {
            holder.getViewBinding().negotiatedRateExtrasText.setText(R.string.car_class_cell_negotiated_rate_title);
            holder.getViewBinding().netRateExtrasText.setVisibility(View.VISIBLE);
        } else if (EHICarClassDetails.AVAILABLE_AT_CONTRACT_RATE.equalsIgnoreCase(carClass.getStatus())) {
            holder.getViewBinding().negotiatedRateContainer.setVisibility(View.VISIBLE);
            holder.getViewBinding().negotiatedRateText.setText(R.string.car_class_cell_negotiated_rate_title);
            holder.getViewBinding().negotiatedRateExtrasText.setText(R.string.car_class_cell_negotiated_rate_title);
        } else if (EHICarClassDetails.AVAILABLE_AT_PROMOTIONAL_RATE.equalsIgnoreCase(carClass.getStatus())) {
            holder.getViewBinding().negotiatedRateContainer.setVisibility(View.VISIBLE);
            holder.getViewBinding().negotiatedRateText.setText(R.string.car_class_cell_promotional_rate_title);
            holder.getViewBinding().negotiatedRateExtrasText.setText(R.string.car_class_cell_promotional_rate_title);
        } else {
            holder.getViewBinding().negotiatedRateContainer.setVisibility(View.GONE);
            holder.getViewBinding().negotiatedRateExtrasText.setVisibility(View.GONE);
        }

        if (carClass.getStatus() == null || !carClass.shouldShowCallForAvailability()) {

            if (inList && carClass.isSecretRate()) {
                holder.getViewBinding().priceContainer.setVisibility(View.GONE);
                holder.getViewBinding().noPriceAvailable.setVisibility(View.VISIBLE);
            } else {
                holder.getViewBinding().priceUnavailableContainer.setVisibility(View.GONE);
                holder.getViewBinding().priceContainer.setVisibility(View.VISIBLE);
                holder.getViewBinding().classPrice.setVisibility(View.VISIBLE);
            }

            holder.getViewBinding().chevron.setVisibility(View.VISIBLE);
            holder.getViewBinding().totalCost.setText(context.getString(R.string.reservation_price_total_subtitle));

            CharSequence classPriceText = "";
            if (payState != null && !inList) {
                classPriceText = getPriceText(carClass, ehiCharges, payState);
            } else if (inList) {
                classPriceText = getPriceText(carClass, payState);
            }

            holder.getViewBinding().classPrice.setText(classPriceText);

            if (carClass.showTotalCostAsterisks(payState == ReservationFlowListener.PayState.PREPAY)) {
                holder.getViewBinding().totalCostAsterisks.setVisibility(View.VISIBLE);
            } else {
                holder.getViewBinding().totalCostAsterisks.setVisibility(View.GONE);
            }
            LocalDataManager.getInstance().setShowClassTotalCostAsterisks(carClass.showTotalCostAsterisks(payState == ReservationFlowListener.PayState.PREPAY));

            holder.getViewBinding().pointsContainer.preventAnimation(preventAnimations);

            if (!holder.getViewBinding().pointsContainer.isInstantiated()) {
                holder.getViewBinding().pointsContainer.setCarClassDetail(carClass, showPoints);
            }
            holder.getViewBinding().pointsContainer.setText(carClass);

            if (showPointsAnimation) {
                holder.getViewBinding().pointsContainer.animateShowPoints(showPoints);
            } else {
                holder.getViewBinding().pointsContainer.setVisibility(
                        showPoints ? View.VISIBLE : View.GONE
                );
            }

            if (!TextUtils.isEmpty(carClass.getTruckUrl())) {
                holder.getViewBinding().priceUnavailableText.setText(R.string.reservation_price_leave_the_app);
            } else {
                holder.getViewBinding().priceUnavailableText.setText(R.string.reservation_price_call_for_availability);
            }
        } else if (carClass.shouldShowCallForAvailability()) {
            if (!holder.getViewBinding().pointsContainer.isInstantiated()) {
                holder.getViewBinding().pointsContainer.setCarClassDetail(carClass, showPoints);
            }
            holder.getViewBinding().priceUnavailableContainer.setVisibility(View.VISIBLE);
            holder.getViewBinding().priceUnavailableText.setText(R.string.reservation_price_call_for_availability);
        }

        final List<EHIImage> carClassImages = carClass.getImages();
        if (!ListUtils.isEmpty(carClassImages) && carClassImages.get(EHIImageUtils.IMAGE_TYPE_SIDE_PROFILE).getPath() != null) {
            final Pair<Integer, Integer> defaultCarImageMeasureSpec = BaseAppUtils.getDefaultCarImageMeasureSpec(context);
            holder.getViewBinding().classCarImage.measure(defaultCarImageMeasureSpec.first, defaultCarImageMeasureSpec.second);
            EHIImageLoader.with(context)
                    .loadTypeFromList(EHIImageUtils.IMAGE_TYPE_SIDE_PROFILE, carClassImages)
                    .into(holder.getViewBinding().classCarImage);
        }

        if (carClass.getFeatures() != null) {
            holder.getViewBinding().classTransmissionType.setText(EHICarClassDetails.getTransmissionDescription(carClass.getFeatures()));
            holder.getViewBinding().carTransmissionHidden.setText(EHICarClassDetails.getTransmissionDescription(carClass.getFeatures()));
            if (carClass.isTransmissionTypeManual()) {
                holder.getViewBinding().manualTransmissionIcon.setVisibility(View.VISIBLE);
            } else {
                holder.getViewBinding().manualTransmissionIcon.setVisibility(View.GONE);
            }
        }

        if (carClass.getMakeModelOrSimilarText() != null && holder.getViewBinding().classDescription != null) {
            holder.getViewBinding().classDescription.setText(new TokenizedString.Formatter<EHIStringToken>(context.getResources())
                    .formatString(R.string.reservation_car_class_make_model_title)
                    .addTokenAndValue(EHIStringToken.MAKE_MODEL, carClass.getMakeModelOrSimilarText().trim())
                    .format() + "*");
        }

        if (!inList && !mAnimating) {
            if (mAnimatedOnce) {
                holder.getViewBinding().totalCostAsterisks.setAlpha(0);
                holder.getViewBinding().totalCost.setAlpha(0);
                ((View) holder.getViewBinding().chevron).setAlpha(0);
                holder.getViewBinding().bigPriceContainer.setAlpha(1);
                holder.getViewBinding().priceContainer.setBackgroundColor(ContextCompat.getColor(context, R.color.white));
                holder.getViewBinding().classPrice.setTextColor(ContextCompat.getColor(context, R.color.ehi_black));
                if (!TextUtils.isEmpty(holder.getViewBinding().negotiatedRateExtrasText.getText())){
                    holder.getViewBinding().negotiatedRateExtrasText.setVisibility(View.VISIBLE);
                } else {
                    holder.getViewBinding().negotiatedRateExtrasText.setVisibility(View.GONE);
                }

                holder.getViewBinding().negotiatedRateContainer.setVisibility(View.GONE);
            } else {
                mAnimating = true;
                holder.getViewBinding().totalCostAsterisks
                        .animate()
                        .setDuration(CarClassExtrasFragment.SLIDE_DOWN_ANIMATION)
                        .setListener(new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animator) {
                                mAnimating = true;
                            }

                            @Override
                            public void onAnimationEnd(Animator animator) {
                                mAnimating = false;
                            }

                            @Override
                            public void onAnimationCancel(Animator animator) {

                            }

                            @Override
                            public void onAnimationRepeat(Animator animator) {

                            }
                        })
                        .alpha(0);
                holder.getViewBinding().totalCost
                        .animate()
                        .setDuration(CarClassExtrasFragment.SLIDE_DOWN_ANIMATION)
                        .alpha(0);
                holder.getViewBinding().chevron
                        .animate()
                        .setDuration(CarClassExtrasFragment.SLIDE_DOWN_ANIMATION)
                        .alpha(0);
                holder.getViewBinding().priceContainer.setBackgroundColor(ContextCompat.getColor(context, R.color.white));
                holder.getViewBinding().classPrice.setTextColor(ContextCompat.getColor(context, R.color.ehi_black));
                holder.getViewBinding().bigPriceContainer
                        .animate()
                        .setDuration(CarClassExtrasFragment.SLIDE_DOWN_ANIMATION)
                        .alpha(1);
//                final AnimatorSet animatorSet = new AnimatorSet();
//                animatorSet.setInterpolator(new LinearOutSlowInInterpolator());
//                animatorSet.setDuration(CarClassExtrasFragment.SLIDE_DOWN_ANIMATION);
//                final AnimatorSet.Builder play = animatorSet.play(animateTextColor(holder.getViewBinding().classPrice));
//                if (!fromChooseYourRate) {
//                    play.with(animateBackgroundColor(holder.getViewBinding().priceContainer));
//                }
//                else {
//                    holder.getViewBinding().priceContainer.setBackgroundColor(ContextCompat.getColor(context, R.color.white));
//                }
//                animatorSet.start();
                setAnimatedOnce(true);
            }
        }

        if (!showPrice) {
            holder.getViewBinding().bigPriceContainer.setVisibility(View.INVISIBLE);
        }
    }

    private CharSequence getPriceText(EHICarClassDetails carClass, ReservationFlowListener.PayState preferredPayState) {
        if (preferredPayState == ReservationFlowListener.PayState.PAY_LATER
                || preferredPayState == ReservationFlowListener.PayState.REDEMPTION) {
            CharSequence text = carClass.getPaylaterChargePriceView();

            if (EHITextUtils.isEmpty(text)) {
                return carClass.getPrepayChargePriceView();
            }
            return text;
        } else {
            CharSequence text = carClass.getPrepayChargePriceView();

            if (EHITextUtils.isEmpty(text)) {
                return carClass.getPaylaterChargePriceView();
            }
            return text;
        }
    }

    private CharSequence getPriceText(EHICarClassDetails carClass, List<EHICharge> ehiCharges, ReservationFlowListener.PayState preferredPayState) {
        if (preferredPayState == ReservationFlowListener.PayState.PAY_LATER
                || preferredPayState == ReservationFlowListener.PayState.REDEMPTION) {
            CharSequence text = getPayLaterClassPriceText(carClass, ehiCharges);

            if (EHITextUtils.isEmpty(text)) {
                return getPrePayClassPriceText(carClass, ehiCharges);
            }
            return text;
        } else {
            CharSequence text = getPrePayClassPriceText(carClass, ehiCharges);

            if (EHITextUtils.isEmpty(text)) {
                return getPayLaterClassPriceText(carClass, ehiCharges);
            }
            return text;
        }
    }

    private CharSequence getPayLaterClassPriceText(EHICarClassDetails carClass, List<EHICharge> ehiCharges) {
        CharSequence payLaterPrice = "";
        if (!EHITextUtils.isEmpty(carClass.getPaylaterChargePriceView())) {
            payLaterPrice = carClass.getPaylaterChargePriceView();
        } else if (EHICharge.getPayLaterCharge(ehiCharges) != null) {
            final EHICharge ehiCharge = EHICharge.getPayLaterCharge(ehiCharges);
            payLaterPrice = ehiCharge.getPriceView().getFormattedPrice(true);
        } else {
            final EHIPriceSummary payLaterPriceSummary = carClass.getPaylaterPriceSummary();
            if (payLaterPriceSummary != null) {
                payLaterPrice = payLaterPriceSummary.getEstimatedTotalView().getFormattedPrice(true);
            }
        }
        return payLaterPrice;
    }

    private CharSequence getPrePayClassPriceText(EHICarClassDetails carClass, List<EHICharge> ehiCharges) {
        CharSequence prepayPrice = "";
        if (!EHITextUtils.isEmpty(carClass.getPrepayChargePriceView())) {
            prepayPrice = carClass.getPrepayChargePriceView();
        } else if (EHICharge.getPrePayCharge(ehiCharges) != null) {
            final EHICharge ehiCharge = EHICharge.getPrePayCharge(ehiCharges);
            prepayPrice = ehiCharge.getPriceView().getFormattedPrice(true);
        } else {
            final EHIPriceSummary prepayPriceSummary = carClass.getPrepayPriceSummary();
            if (prepayPriceSummary != null) {
                prepayPrice = prepayPriceSummary.getEstimatedTotalView().getFormattedPrice(true);
            }
        }
        return prepayPrice;
    }

    private void setAnimatedOnce(final boolean animatedOnce) {
        mAnimatedOnce = animatedOnce;
    }

    private ValueAnimator animateBackgroundColor(final View view) {
        Integer colorFrom = ContextCompat.getColor(view.getContext(), android.R.color.transparent);
        Integer colorTo = ContextCompat.getColor(view.getContext(), android.R.color.transparent);
        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                view.setBackgroundColor((Integer) animator.getAnimatedValue());
            }
        });
        return colorAnimation;
    }

    private ValueAnimator animateTextColor(final TextView view) {
        Integer colorFrom = ContextCompat.getColor(view.getContext(), android.R.color.transparent);
        Integer colorTo = ContextCompat.getColor(view.getContext(), R.color.ehi_black);
        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                view.setTextColor((Integer) animator.getAnimatedValue());
            }
        });
        return colorAnimation;
    }

    public static class Builder {
        private final CarClassSelectViewController mController;
        private CarClassSelectViewHolder mCarClassSelectViewHolder;
        private EHICarClassDetails mEhiCarClassDetails;
        private CarClassListAdapter.CarClassListAdapterListener mCarClassListAdapterListener;
        private int mPosition = 0;
        private boolean mInList = false;
        private boolean mShowPoints = false;
        private boolean mPreventAnimations = true;
        private boolean mAnimatedOnce = false;
        private boolean mShowPreviouslySelected = false;
        private boolean mShowPrice = true;
        private ReservationFlowListener.PayState mPayState;
        private boolean mFromChooseYourRate = false;
        private List<EHICharge> mEehiCharges;
        private boolean mShowPointsAnimation = false;

        public Builder() {
            mController = new CarClassSelectViewController();
        }

        public Builder forHolder(final CarClassSelectViewHolder carClassSelectViewHolder) {
            mCarClassSelectViewHolder = carClassSelectViewHolder;
            return this;
        }

        public Builder carClassDetails(final EHICarClassDetails ehiCarClassDetails) {
            mEhiCarClassDetails = ehiCarClassDetails;
            return this;
        }

        public Builder listener(final CarClassListAdapter.CarClassListAdapterListener carClassListAdapterListener) {
            mCarClassListAdapterListener = carClassListAdapterListener;
            return this;
        }

        public Builder position(final int position) {
            mPosition = position;
            return this;
        }

        public Builder inList(final boolean inList) {
            mInList = inList;
            return this;
        }

        public Builder showPoints(final boolean showPoints) {
            mShowPoints = showPoints;
            return this;
        }

        public Builder preventAnimations(final boolean preventAnimations) {
            mPreventAnimations = preventAnimations;
            return this;
        }

        public Builder animatedOnce(final boolean animatedOnce) {
            mAnimatedOnce = animatedOnce;
            return this;
        }

        public Builder showPreviouslySelected(final boolean showPreviouslySelected) {
            mShowPreviouslySelected = showPreviouslySelected;
            return this;
        }

        public Builder showPrice(final boolean showPrice) {
            mShowPrice = showPrice;
            return this;
        }

        public Builder payState(final ReservationFlowListener.PayState payState) {
            mPayState = payState;
            return this;
        }

        public Builder fromChooseYourRate(final boolean fromChooseYourRate) {
            mFromChooseYourRate = fromChooseYourRate;
            return this;
        }

        public Builder addEHICharges(final List<EHICharge> ehiCharges) {
            mEehiCharges = ehiCharges;
            return this;
        }

        public Builder showPointsAnimation(final boolean showPointsAnimation) {
            mShowPointsAnimation = showPointsAnimation;
            return this;
        }

        public void build(final Context context) {
            mController.fillCellWithData(
                    context,
                    mCarClassSelectViewHolder,
                    mEhiCarClassDetails,
                    mCarClassListAdapterListener,
                    mPosition,
                    mInList,
                    mShowPoints,
                    mPreventAnimations,
                    mAnimatedOnce,
                    mShowPreviouslySelected,
                    mShowPrice,
                    mPayState,
                    mFromChooseYourRate,
                    mEehiCharges,
                    mShowPointsAnimation
            );
        }

    }


}