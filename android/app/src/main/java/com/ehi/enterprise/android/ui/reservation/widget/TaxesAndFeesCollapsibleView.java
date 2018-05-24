package com.ehi.enterprise.android.ui.reservation.widget;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.databinding.PaymentLearnMoreItemViewBinding;
import com.ehi.enterprise.android.models.reservation.EHICarClassDetails;
import com.ehi.enterprise.android.models.reservation.EHIPaymentLineItem;
import com.ehi.enterprise.android.models.reservation.EHIPriceSummary;
import com.ehi.enterprise.android.models.reservation.EHIReservation;
import com.ehi.enterprise.android.ui.viewmodel.ManagersAccessViewModel;
import com.isobar.android.viewmodel.ViewModel;

import java.util.List;

@ViewModel(ManagersAccessViewModel.class)
public class TaxesAndFeesCollapsibleView extends PriceSummaryBasedCollapsibleView<ManagersAccessViewModel> {

    private PriceSummaryView.PriceSummaryListener mPriceSummaryListener;
    private EHICarClassDetails mCarClassDetails;

    public TaxesAndFeesCollapsibleView(Context context) {
        super(context);
    }

    public TaxesAndFeesCollapsibleView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TaxesAndFeesCollapsibleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setReservation(EHIReservation ehiReservation, boolean isPrepay) {
        mCarClassDetails = ehiReservation.getCarClassDetails();
        super.setReservation(ehiReservation, isPrepay);
    }

    @Override
    public void setPriceSummary(EHIPriceSummary ehiPriceSummary) {
        setPriceSummary(ehiPriceSummary, R.string.price_section_title_taxes_fees);

        if (ehiPriceSummary.getEstimatedTaxesFeesView() != null) {
            if (mCarClassDetails.isSecretRateAfterCarSelected()) {
                getViewBinding().mainSubtitle.setText(getResources().getString(R.string.payment_line_item_included));
            } else {
                getViewBinding().mainSubtitle.setText(
                        ehiPriceSummary.getEstimatedTaxesFeesView().getFormattedPrice(false)
                );
            }
        }

        final PaymentLearnMoreItemViewBinding learnMoreHolder = DataBindingUtil.inflate(
                LayoutInflater.from(getContext()),
                R.layout.item_payment_learn_more,
                getViewBinding().childContainer,
                false
        );

        learnMoreHolder.getRoot().setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPriceSummaryListener != null) {
                    mPriceSummaryListener.onLearnMoreClicked();
                }
            }
        });

        getViewBinding().childContainer.addView(learnMoreHolder.getRoot());
    }

    public void setOnPriceSummaryListener(PriceSummaryView.PriceSummaryListener listener) {
        mPriceSummaryListener = listener;
    }

    @NonNull
    @Override
    protected List<EHIPaymentLineItem> getLineItems(EHIPriceSummary priceSummary) {
        return priceSummary.getFeesLineItems();
    }

    @NonNull
    @Override
    protected CollapsibleItemView getCollapsibleItemView(EHIPaymentLineItem lineItem) {
        final CollapsibleItemView collapsibleItemView = new CollapsibleItemView(getContext());

        collapsibleItemView.setTitle(lineItem.getDescription(getResources()));

        if (EHIPaymentLineItem.STATUS_INCLUDED.equalsIgnoreCase(lineItem.getStatus())
                || Double.valueOf(0.0d).equals(lineItem.getTotalAmountView().getDoubleAmmount())) {
            collapsibleItemView.setValue(getContext().getString(R.string.payment_line_item_included));
        } else {
            collapsibleItemView.setValue(lineItem.getTotalAmountView().getFormattedPrice(false).toString());
        }

        return collapsibleItemView;
    }
}
