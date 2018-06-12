package com.ehi.enterprise.android.models.profile;

import com.ehi.enterprise.android.models.EHIModel;
import com.google.gson.annotations.SerializedName;

public class EHIEmailPreference extends EHIModel {

    @SerializedName("rental_receipts")
    private boolean mRentalReceipts;

    @SerializedName("special_offers")
    private Boolean mSpecialOffers;

    @SerializedName("partner_offers")
    private boolean mPartnerOffers;

    public void setRentalReceipts(boolean rentalReceipts) {
        mRentalReceipts = rentalReceipts;
    }

    public void setSpecialOffers(boolean isSelectedByDefault, boolean selected) {
        // logic necessary to keep 3 states:
        // opt-in: option is checked
        // opt-out: option was checked before and user unchecks
        // opt-null: unchecked and user didn't interact with the checkbox
        Boolean opt = null;
        if (isSelectedByDefault) {
            opt = selected;
        } else if (selected) {
            opt = true;
        }
        mSpecialOffers = opt;
    }

    public void setPartnerOffers(boolean partnerOffers) {
        mPartnerOffers = partnerOffers;
    }

    public boolean isRentalReceipts() {
        return mRentalReceipts;
    }

    public boolean isSpecialOffers() {
        return mSpecialOffers == null ? false : mSpecialOffers;
    }

    public boolean isPartnerOffers() {
        return mPartnerOffers;
    }
}
