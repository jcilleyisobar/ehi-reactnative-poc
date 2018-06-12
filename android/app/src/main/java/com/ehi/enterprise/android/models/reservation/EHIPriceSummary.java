package com.ehi.enterprise.android.models.reservation;

import android.support.annotation.Nullable;

import com.ehi.enterprise.android.models.EHIModel;
import com.google.gson.annotations.SerializedName;

import java.text.NumberFormat;
import java.util.Currency;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class EHIPriceSummary extends EHIModel {

    public static final String US_CURRENCY = "USD";
    public  static final String CA_CURRENCY = "CAD";

    @SerializedName("estimated_total_view")
    private EHIPrice mEstimatedTotalView;

    @SerializedName("estimated_total_payment")
    private EHIPrice mEstimatedTotalPayment;

    @SerializedName("estimated_total_taxes_and_fees_view")
    private EHIPrice mEstimatedTaxesFeesView;

    @SerializedName("estimated_total_taxes_and_fees_payment")
    private EHIPrice mEstimatedTaxesFeesPayment;

    @SerializedName("estimated_total_extras_and_coverages_view")
    private EHIPrice mEstimatedTotalExtrasAndCoveragesView;

    @SerializedName("estimated_total_extras_and_coverages_payment")
    private EHIPrice mEstimatedTotalExtrasAndCoveragesPayment;

    @SerializedName("estimated_total_savings_view")
    private EHIPrice mEstimatedTotalSavingsView;

    @SerializedName("estimated_total_savings_payment")
    private EHIPrice mEstimatedTotalSavingsPayment;

    @SerializedName("estimated_total_vehicle_view")
    private EHIPrice mEstimatedTotalVehicleView;

    @SerializedName("estimated_total_vehicle_payment")
    private EHIPrice mEstimatedTotalVehiclePayment;

    @SerializedName("payment_line_items")
    private List<EHIPaymentLineItem> mPaymentLineItems;

    @SerializedName("total_charged")
    private String mTotalCharged;

    @SerializedName("amount_due")
    private String mAmountDue;

    @SerializedName("currency_code")
    private String mCurrencyCode;

    @SerializedName("currency_symbol")
    private String mCurrencySymbol;

    public EHIPrice getEstimatedTotalView() {
        return mEstimatedTotalView;
    }

    public EHIPrice getEstimatedTotalPayment() {
        return mEstimatedTotalPayment;
    }

    public EHIPrice getEstimatedTaxesFeesView() {
        return mEstimatedTaxesFeesView;
    }

    public EHIPrice getEstimatedTotalExtrasAndCoveragesView() {
        return mEstimatedTotalExtrasAndCoveragesView;
    }

    public EHIPrice getEstimatedTotalSavingsView() {
        return mEstimatedTotalSavingsView;
    }

    public EHIPrice getEstimatedTotalVehicleView() {
        return mEstimatedTotalVehicleView;
    }

    public List<EHIPaymentLineItem> getAllPaymentLineItems() {
        return mPaymentLineItems;
    }

    @Nullable
    public List<EHIPaymentLineItem> getRentalPaymentItems() {
        if (mPaymentLineItems == null
                || mPaymentLineItems.size() == 0) {
            return null;
        }
        List<EHIPaymentLineItem> items = new LinkedList<>();
        for (EHIPaymentLineItem i : mPaymentLineItems) {
            if (EHIPaymentLineItem.VEHICLE_RATE.equalsIgnoreCase(i.getCategory())) {
                items.add(i);
            }
        }
        return items;
    }

    public List<EHIPaymentLineItem> getEquipmentLineItems() {
        if (mPaymentLineItems == null
                || mPaymentLineItems.size() == 0) {
            return null;
        }
        List<EHIPaymentLineItem> equipment = new LinkedList<>();
        for (EHIPaymentLineItem i : mPaymentLineItems) {
            if (EHIPaymentLineItem.EQUIPMENT.equalsIgnoreCase(i.getCategory())) {
                equipment.add(i);

            }
        }

        return equipment;
    }

    public List<EHIPaymentLineItem> getInsuranceLineItems() {
        if (mPaymentLineItems == null
                || mPaymentLineItems.size() == 0) {
            return null;
        }
        List<EHIPaymentLineItem> insurance = new LinkedList<>();
        for (EHIPaymentLineItem i : mPaymentLineItems) {
            if (EHIPaymentLineItem.COVERAGE.equalsIgnoreCase(i.getCategory())) {
                insurance.add(i);

            }
        }
        return insurance;
    }

    public List<EHIPaymentLineItem> getFeesLineItems() {
        if (mPaymentLineItems == null
                || mPaymentLineItems.size() == 0) {
            return null;
        }
        List<EHIPaymentLineItem> fees = new LinkedList<>();
        for (EHIPaymentLineItem i : mPaymentLineItems) {
            if (EHIPaymentLineItem.FEE.equalsIgnoreCase(i.getCategory())) {
                fees.add(i);

            }
        }

        return fees;
    }

    public List<EHIPaymentLineItem> getSavingsLineItems() {
        if (mPaymentLineItems == null
                || mPaymentLineItems.size() == 0) {
            return null;
        }
        List<EHIPaymentLineItem> savings = new LinkedList<>();
        for (EHIPaymentLineItem i : mPaymentLineItems) {
            if (EHIPaymentLineItem.SAVINGS.equalsIgnoreCase(i.getCategory())) {
                savings.add(i);

            }
        }

        return savings;
    }

    @Nullable
    public EHIPaymentLineItem getRedemptionLineItem() {
        if (mPaymentLineItems == null
                || mPaymentLineItems.size() == 0) {
            return null;
        }
        for (EHIPaymentLineItem i : mPaymentLineItems) {
            if (EHIPaymentLineItem.EPLUS_REDEMPTION_SAVINGS.equalsIgnoreCase(i.getCategory())) {
                return i;
            }
        }

        return null;
    }

    public boolean hasCharges() {
        if (mPaymentLineItems == null
                || mPaymentLineItems.size() == 0) {
            return false;
        }
        for (EHIPaymentLineItem i : mPaymentLineItems) {
            if (EHIPaymentLineItem.CHARGED.equalsIgnoreCase(i.getStatus())) {
                return true;
            }
        }
        return false;
    }

    public Map<String, EHIPaymentLineItem> getExtrasMap() {
        Map<String, EHIPaymentLineItem> extras = new HashMap<>();
        if (mPaymentLineItems != null
                && mPaymentLineItems.size() >= 0) {
            for (EHIPaymentLineItem item : mPaymentLineItems) {
                if (item.getCode() != null) {
                    extras.put(item.getCode(), item);
                }
            }
        }

        return extras;
    }

    public String getTotalCharged() {
        return mTotalCharged;
    }

    public String getAmountDue() {
        return mAmountDue;
    }

    public String getCurrencyCode() {
        return mCurrencyCode;
    }

    public String getCurrencySymbol() {
        return mCurrencySymbol;
    }

    public String getFormattedPriceView() {
        if (mCurrencyCode == null && mTotalCharged != null) {
            return mTotalCharged;
        } else if (mTotalCharged == null) {
            return null;
        } else {
            Currency currency = Currency.getInstance(mCurrencyCode);
            NumberFormat format = NumberFormat.getCurrencyInstance();
            format.setCurrency(currency);
            return format.format(Double.parseDouble(mTotalCharged));
        }
    }

    public boolean isDifferentPaymentCurrency() {
        return getEstimatedTotalView() != null
                && getEstimatedTotalPayment() != null
                && !getEstimatedTotalView().getCurrencyCode().equalsIgnoreCase(
                getEstimatedTotalPayment().getCurrencyCode());
    }

    public boolean isTravelingBetweenUSAndCanada() {
        if (getEstimatedTotalView() != null && getEstimatedTotalPayment() != null) {
            final String destination = getEstimatedTotalView().getCurrencyCode();
            final String current = getEstimatedTotalPayment().getCurrencyCode();
            return current.equals(US_CURRENCY) && destination.equals(CA_CURRENCY)
                    || current.equals(CA_CURRENCY) && destination.equals(US_CURRENCY);
        }
        return false;
    }

    public boolean isAllTaxesAndFeesIncluded() {
        List<EHIPaymentLineItem> fees = getFeesLineItems();
        boolean allIncluded = true;
        if (fees.size() > 0) {
            for (EHIPaymentLineItem item : fees) {
                if (!EHIPaymentLineItem.STATUS_INCLUDED.equalsIgnoreCase(item.getStatus())) {
                    allIncluded = false;
                    break;
                }
            }
        } else {
            allIncluded = false;
        }
        return allIncluded;
    }

    @Override
    public String toString() {
        return "EHIPriceSummary{" +
                "mEstimatedTotalView=" + mEstimatedTotalView +
                ", mEstimatedTotalPayment=" + mEstimatedTotalPayment +
                ", mPaymentLineItems=" + mPaymentLineItems +
                "}";
    }
}
