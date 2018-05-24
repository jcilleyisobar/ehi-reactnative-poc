package com.ehi.enterprise.android.ui.reservation;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.ehi.enterprise.android.models.profile.EHIPaymentMethod;
import com.ehi.enterprise.android.ui.reservation.interfaces.PaymentItemCheckListener;
import com.ehi.enterprise.android.ui.reservation.view_holders.PaymentItemCheckViewHolder;
import com.ehi.enterprise.android.utils.analytics.EHIAnalytics;
import com.ehi.enterprise.android.utils.analytics.EHIAnalyticsDictionaryUtils;
import com.ehi.enterprise.android.utils.analytics.EHIAnalyticsEvent;

import java.util.List;

import static com.ehi.enterprise.android.ui.reservation.SelectCreditCardFragment.SCREEN_NAME;

public class SelectPaymentAdapter extends RecyclerView.Adapter<PaymentItemCheckViewHolder> {

    private List<EHIPaymentMethod> mCardPaymentMethods;
    private final boolean mShouldAutomaticallySelectCard;
    private PaymentItemCheckListener mOnCheckListener;
    private final CompoundButton.OnCheckedChangeListener mOnAutomaticallySelectCardCheckedListener;
    private PaymentItemCheckViewHolder mLastOptionChecked;
    private int mLastCheckedPosition = 0;

    public SelectPaymentAdapter(List<EHIPaymentMethod> cardPaymentMethods,
                                boolean shouldAutomaticallySelectCard,
                                PaymentItemCheckListener onCheckListener,
                                CompoundButton.OnCheckedChangeListener onAutomaticallySelectCardCheckedListener) {
        mCardPaymentMethods = cardPaymentMethods;
        mShouldAutomaticallySelectCard = shouldAutomaticallySelectCard;
        setOnCheckListener(onCheckListener);
        mOnAutomaticallySelectCardCheckedListener = onAutomaticallySelectCardCheckedListener;
    }

    private void setOnCheckListener(final PaymentItemCheckListener listener) {
        mOnCheckListener = new PaymentItemCheckListener() {
            @Override
            public void onEdit(EHIPaymentMethod method) {
                listener.onEdit(method);
            }

            @Override
            public void onCheck(PaymentItemCheckViewHolder viewHolder, EHIPaymentMethod method, int position) {
                if (mLastOptionChecked != null) {
                    mLastOptionChecked.getViewBinding().checkbox.setChecked(false);
                    mLastOptionChecked.getViewBinding().automaticallySelectCheckbox.setVisibility(View.GONE);
                }
                mLastOptionChecked = viewHolder;
                mLastCheckedPosition = position;
                if (method.isPreferred()) {
                    viewHolder.getViewBinding().automaticallySelectCheckbox.setVisibility(View.VISIBLE);
                }
                listener.onCheck(viewHolder, method, position);
            }

            @Override
            public void onClick(EHIPaymentMethod method) {
                if (method.isPreferred()) {
                    EHIAnalyticsEvent.create()
                            .screen(EHIAnalytics.Screen.SCREEN_SELECT_CREDIT_CARD.value, SCREEN_NAME)
                            .state(EHIAnalytics.State.STATE_CREDIT_CARD_DETAILS.value)
                            .action(EHIAnalytics.Motion.MOTION_TAP.value, EHIAnalytics.Action.ACTION_CHECK_PREFERRED_CARD.value)
                            .addDictionary(EHIAnalyticsDictionaryUtils.reservation())
                            .tagScreen()
                            .tagEvent();
                }
            }
        };
    }

    public void setCardPaymentMethods(List<EHIPaymentMethod> cardPaymentMethods) {
        mCardPaymentMethods = cardPaymentMethods;
        notifyDataSetChanged();
    }

    @Override
    public PaymentItemCheckViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return PaymentItemCheckViewHolder.create(parent);
    }

    @Override
    public void onBindViewHolder(PaymentItemCheckViewHolder holder, int position) {
        PaymentItemCheckViewHolder.bind(holder,
                mCardPaymentMethods.get(position),
                position,
                position == mLastCheckedPosition,
                mOnCheckListener,
                mOnAutomaticallySelectCardCheckedListener,
                mShouldAutomaticallySelectCard);
    }

    @Override
    public int getItemCount() {
        return mCardPaymentMethods.size();
    }
}