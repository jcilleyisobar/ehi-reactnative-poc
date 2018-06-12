package com.ehi.enterprise.android.ui.reservation.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.models.reservation.EHIPaymentLineItem;
import com.ehi.enterprise.android.models.reservation.EHIPriceSummary;
import com.ehi.enterprise.android.ui.viewmodel.ManagersAccessViewModel;
import com.isobar.android.viewmodel.ViewModel;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

@ViewModel(ManagersAccessViewModel.class)
public class MiscellaneousCollapsibleView extends PriceSummaryBasedCollapsibleView<ManagersAccessViewModel> {

    private int redeemingDays;
    private int pointsRateADay;

    public MiscellaneousCollapsibleView(Context context) {
        super(context);
    }

    public MiscellaneousCollapsibleView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MiscellaneousCollapsibleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setPriceSummary(EHIPriceSummary ehiPriceSummary) {
        setPriceSummary(ehiPriceSummary, R.string.price_section_title_adjustments);

        if (ehiPriceSummary.getEstimatedTotalSavingsView() != null) {
            getViewBinding().mainSubtitle.setText("-" +
                    ehiPriceSummary.getEstimatedTotalSavingsView().getFormattedPrice(false)
            );
        }
    }

    public void setRedemptionInfo(int redeemingDays, int pointsRateADay) {
        this.redeemingDays = redeemingDays;
        this.pointsRateADay = pointsRateADay;
    }

    @Override
    @NonNull
    protected List<EHIPaymentLineItem> getLineItems(EHIPriceSummary priceSummary) {
        final List<EHIPaymentLineItem> lineItems = new ArrayList<>();

        if (redeemingDays > 0) {
            final EHIPaymentLineItem redemptionLineItem = priceSummary.getRedemptionLineItem();
            if (redemptionLineItem != null) {
                lineItems.add(redemptionLineItem);
            }
        }

        lineItems.addAll(priceSummary.getSavingsLineItems());

        return lineItems;

    }

    @NonNull
    @Override
    protected CollapsibleItemView getCollapsibleItemView(EHIPaymentLineItem lineItem) {
        final CollapsibleItemView collapsibleItemView = new CollapsibleItemView(getContext());
        if (lineItem.getCategory().equals(EHIPaymentLineItem.EPLUS_REDEMPTION_SAVINGS)) {
            final StringBuilder subtitle = new StringBuilder();
            subtitle.append(redeemingDays);
            subtitle.append(" ");
            subtitle.append(getResources().getString(R.string.reservation_rate_daily_unit_plural).toLowerCase());
            subtitle.append(": ");
            subtitle.append(NumberFormat.getInstance().format(pointsRateADay));
            subtitle.append(" ");
            subtitle.append(getResources().getString(R.string.redemption_points_per_day).toLowerCase());

            collapsibleItemView.setInfo(
                    getResources().getString(R.string.redemption_line_item_title),
                    subtitle,
                    "\n");
        } else {
            collapsibleItemView.setTitle(lineItem.getDescription(getResources()));
        }
        collapsibleItemView.setValue("-" + lineItem.getTotalAmountView().getFormattedPrice(false).toString());

        return collapsibleItemView;
    }

}
