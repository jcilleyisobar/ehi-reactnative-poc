package com.ehi.enterprise.android.ui.reservation.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.util.AttributeSet;

import com.ehi.enterprise.android.models.reservation.EHICarClassDetails;
import com.ehi.enterprise.android.models.reservation.EHIPaymentLineItem;
import com.ehi.enterprise.android.models.reservation.EHIPriceSummary;
import com.ehi.enterprise.android.models.reservation.EHIReservation;
import com.ehi.enterprise.android.ui.viewmodel.ManagersAccessViewModel;

import java.util.List;

public abstract class PriceSummaryBasedCollapsibleView<T extends ManagersAccessViewModel> extends CollapsibleView<T> {

    public PriceSummaryBasedCollapsibleView(Context context) {
        super(context);
    }

    public PriceSummaryBasedCollapsibleView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PriceSummaryBasedCollapsibleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public abstract void setPriceSummary(EHIPriceSummary ehiPriceSummary);

    public void setReservation(EHIReservation ehiReservation, boolean isPrepay) {
        setPriceSummary(getEhiPriceSummary(ehiReservation.getCarClassDetails(), isPrepay));
    }

    public void setPriceSummary(EHIPriceSummary ehiPriceSummary, @StringRes int stringResId) {
        getViewBinding().mainTitle.setText(stringResId);

        final List<EHIPaymentLineItem> lineItems = getLineItems(ehiPriceSummary);

        getViewBinding().childContainer.removeAllViews();

        if (lineItems == null || lineItems.size() == 0) {
            getViewBinding().getRoot().setVisibility(GONE);
            return;
        }

        getViewBinding().getRoot().setVisibility(VISIBLE);

        for (final EHIPaymentLineItem lineItem : lineItems) {
            getViewBinding().childContainer.addView(getCollapsibleItemView(lineItem));
        }
    }

    protected abstract List<EHIPaymentLineItem> getLineItems(EHIPriceSummary priceSummary);

    @NonNull
    protected abstract CollapsibleItemView getCollapsibleItemView(EHIPaymentLineItem lineItem);

    protected EHIPriceSummary getEhiPriceSummary(EHICarClassDetails ehiCarClassDetails, boolean isPrepay) {
        if (isPrepay) {
            return ehiCarClassDetails.getPrepayPriceSummary();
        }

        return ehiCarClassDetails.getPaylaterPriceSummary();
    }
}
