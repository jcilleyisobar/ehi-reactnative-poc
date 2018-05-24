package com.ehi.enterprise.android.ui.reservation.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.models.reservation.EHIExtraItem;
import com.ehi.enterprise.android.models.reservation.EHIPaymentLineItem;
import com.ehi.enterprise.android.models.reservation.EHIPriceSummary;
import com.ehi.enterprise.android.models.reservation.EHIReservation;
import com.ehi.enterprise.android.ui.reservation.interfaces.OnExtraActionListener;
import com.ehi.enterprise.android.ui.viewmodel.ManagersAccessViewModel;
import com.isobar.android.viewmodel.ViewModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ViewModel(ManagersAccessViewModel.class)
public class ExtrasCollapsibleView extends PriceSummaryBasedCollapsibleView<ManagersAccessViewModel> {

    private EHIReservation mEhiReservation;
    private Map<String, EHIExtraItem> extrasMap;

    private OnExtraActionListener onExtraActionClickListener;

    public ExtrasCollapsibleView(Context context) {
        super(context);
    }

    public ExtrasCollapsibleView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ExtrasCollapsibleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setReservation(EHIReservation ehiReservation, boolean isPrepay) {
        mEhiReservation = ehiReservation;
        extrasMap = null;
        super.setReservation(ehiReservation, isPrepay);
    }

    @Override
    public void setPriceSummary(EHIPriceSummary ehiPriceSummary) {
        // we'll be doing our own thing
        getViewBinding().mainTitle.setText(R.string.price_section_title_extras);

        if (ehiPriceSummary.getEstimatedTotalExtrasAndCoveragesView() != null) {
            if (mEhiReservation.getCarClassDetails().isSecretRateAfterCarSelected()) {
                getViewBinding().mainSubtitle.setText(getResources().getString(R.string.payment_line_item_included));
            } else {
                getViewBinding().mainSubtitle.setText(
                        ehiPriceSummary.getEstimatedTotalExtrasAndCoveragesView().getFormattedPrice(false)
                );
            }
        }

        final List<EHIPaymentLineItem> equipmentLineItems = getRatedLineItems(ehiPriceSummary.getEquipmentLineItems());

        final List<EHIPaymentLineItem> insuranceLineItems = getRatedLineItems(ehiPriceSummary.getInsuranceLineItems());

        boolean hasNoChildren = equipmentLineItems.size() == 0 && insuranceLineItems.size() == 0;

        getViewBinding().childContainer.removeAllViews();

        if (hasNoChildren) {
            getViewBinding().getRoot().setVisibility(GONE);
            return;
        }

        getViewBinding().getRoot().setVisibility(VISIBLE);

        for (final EHIPaymentLineItem lineItem : equipmentLineItems) {
            getViewBinding().childContainer.addView(getCollapsibleItemView(lineItem));
        }

        for (final EHIPaymentLineItem lineItem : insuranceLineItems) {
            getViewBinding().childContainer.addView(getCollapsibleItemView(lineItem));
        }
    }

    public void setOnExtraActionClickListener(OnExtraActionListener listener) {
        onExtraActionClickListener = listener;
    }

    @NonNull
    @Override
    protected List<EHIPaymentLineItem> getLineItems(EHIPriceSummary priceSummary) {
        return new ArrayList<>();
    }

    @NonNull
    @Override
    protected CollapsibleItemView getCollapsibleItemView(EHIPaymentLineItem lineItem) {
        final EHIExtraItem extraItem = getExtrasMap().get(lineItem.getCode());

        final CollapsibleItemView collapsibleItemView = new CollapsibleItemView(getContext());

        collapsibleItemView.setTitleColor(getContext().getResources().getColor(R.color.ehi_primary));

        String title = null;
        final StringBuilder subtitleStringBuilder = new StringBuilder();

        if (extraItem != null) {
            title = extraItem.getName();

            if (extraItem.getSelectedQuantity() != null
                    && extraItem.getSelectedQuantity() > 1) {
                subtitleStringBuilder.append("(x")
                        .append(extraItem.getSelectedQuantity())
                        .append(") ");
            }
        } else {
            title = lineItem.getDescription(getContext().getResources());
        }

        if (EHIPaymentLineItem.STATUS_INCLUDED.equalsIgnoreCase(lineItem.getStatus())) {
            collapsibleItemView.setValue(getContext().getString(R.string.payment_line_item_included));
        } else if (EHIPaymentLineItem.STATUS_WAIVED.equalsIgnoreCase(lineItem.getStatus())) {
            collapsibleItemView.setValue("-");
        } else {
            subtitleStringBuilder.append(lineItem.getRentalRateText(getResources()));
            collapsibleItemView.setValue(lineItem.getTotalAmountView().getFormattedPrice(false).toString());
        }

        collapsibleItemView.setInfo(title, subtitleStringBuilder.toString());

        collapsibleItemView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onExtraActionClickListener != null && extraItem != null) {
                    onExtraActionClickListener.onClick(extraItem);
                }
            }
        });

        return collapsibleItemView;
    }

    private List<EHIPaymentLineItem> getRatedLineItems(List<EHIPaymentLineItem> allItems) {
        final List<EHIPaymentLineItem> lineItems = new ArrayList<>();

        final Double zeroedQuantity = 0.0;
        if (allItems != null) {
            for (final EHIPaymentLineItem lineItem : allItems) {
                if (!zeroedQuantity.equals(lineItem.getRateQuantity())) {
                    lineItems.add(lineItem);
                }
            }
        }

        return lineItems;
    }

    private Map<String, EHIExtraItem> getExtrasMap() {
        if (extrasMap == null) {
            if (mEhiReservation.getExtras() == null) {
                extrasMap = new HashMap<>();
            } else {
                extrasMap = mEhiReservation.getExtras().getExtrasMap();
            }
        }

        return extrasMap;
    }

}