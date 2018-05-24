package com.ehi.enterprise.android.models.reservation;

import android.support.annotation.Nullable;

import com.ehi.enterprise.android.BuildConfig;
import com.ehi.enterprise.android.models.EHIModel;
import com.ehi.enterprise.android.models.location.EHILocation;
import com.ehi.enterprise.android.models.profile.EHIAddressProfile;
import com.ehi.enterprise.android.utils.EHITextUtils;
import com.google.gson.annotations.SerializedName;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class EHITripSummary extends EHIModel {
    @SerializedName("res_session_id")
    private String mResSessionId;
    @SerializedName("confirmation_number")
    private String mConfirmationNumber;
    @SerializedName("invoice_number")
    private String mInvoiceNumber;
    @SerializedName("ticket_number")
    private String mTicketNumber;
    @SerializedName("customer_first_name")
    private String mCustomerFirstName;
    @SerializedName("customer_last_name")
    private String mCustomerLastName;
    @SerializedName("pickup_time")
    private Date mPickupTime;
    @SerializedName("return_time")
    private Date mReturnTime;
    @SerializedName("pickup_location")
    private EHILocation mPickupLocation;
    @SerializedName("return_location")
    private EHILocation mReturnLocation;
    @SerializedName("vehicle_details")
    private EHIVehicleDetails mVehicleDetails;
    @SerializedName("price_summary")
    private EHIPriceSummary mPriceSummary;
    @SerializedName("rate_my_ride_url")
    private String mRateMyRideUrl;
    @SerializedName("membership_number")
    private String mMembershipNumber;
    @SerializedName("rental_agreement_number")
    private String mRentalAgreementNumber;
    @SerializedName("contract_name")
    private String mContractName;
    @SerializedName("vat_number")
    private String mVatNumber;
    @SerializedName("payment_details")
    private List<EHIPaymentDetail> mPaymentDetails;
    @SerializedName("customer_address")
    private EHIAddressProfile mAddress;

    public void setPickupLocation(final EHILocation pickupLocation) {
        mPickupLocation = pickupLocation;
    }

    public void setReturnLocation(final EHILocation returnLocation) {
        mReturnLocation = returnLocation;
    }

    public void setVehicleDetails(EHIVehicleDetails vehicleDetails) {
        mVehicleDetails = vehicleDetails;
    }

    public String getConfirmationNumber() {
        return mConfirmationNumber;
    }

    public String getInvoiceNumber() {
        return mInvoiceNumber;
    }

    public String getTicketNumber() {
        return mTicketNumber;
    }

    public String getCustomerFirstName() {
        return mCustomerFirstName;
    }

    public String getCustomerLastName() {
        return mCustomerLastName;
    }

    public Date getPickupTime() {
        return mPickupTime;
    }

    public Date getReturnTime() {
        return mReturnTime;
    }

    public EHILocation getPickupLocation() {
        return mPickupLocation;
    }

    public EHILocation getReturnLocation() {
        return mReturnLocation;
    }

    public EHIVehicleDetails getVehicleDetails() {
        return mVehicleDetails;
    }

    public EHIPriceSummary getPriceSummary() {
        return mPriceSummary;
    }

    public String getResSessionId() {
        return mResSessionId;
    }

    public void setResSessionId(String resSessionId) {
        mResSessionId = resSessionId;
    }

    public int daysTillRentalStart() {
        if (mPickupTime == null) {
            return 0;
        }
        Calendar now = Calendar.getInstance();

        Calendar begin = Calendar.getInstance();
        begin.setTime(mPickupTime);

        return (int) ((begin.getTimeInMillis() - now.getTimeInMillis()) / 24 * 60 * 60 * 1000);
    }

    public String getRateMyRideUrl() {
        return mRateMyRideUrl;
    }

    @Override
    public String toString() {
        return "EHITripSummary{" +
                "mResSessionId='" + mResSessionId + '\'' +
                ", mConfirmationNumber='" + mConfirmationNumber + '\'' +
                ", mInvoiceNumber='" + mInvoiceNumber + '\'' +
                ", mTicketNumber='" + mTicketNumber + '\'' +
                ", mCustomerFirstName='" + mCustomerFirstName + '\'' +
                ", mCustomerLastName='" + mCustomerLastName + '\'' +
                ", mPickupTime=" + mPickupTime +
                ", mReturnTime=" + mReturnTime +
                ", mPickupLocation=" + mPickupLocation +
                ", mReturnLocation=" + mReturnLocation +
                ", mVehicleDetails=" + mVehicleDetails +
                ", mPriceSummary=" + mPriceSummary +
                ", mRateMyRideUrl='" + mRateMyRideUrl + '\'' +
                '}';
    }

    public static EHITripSummary getMockTripSummary(boolean isCurrent, @Nullable String confirmationNumber) {
        if (!(BuildConfig.FLAVOR.equalsIgnoreCase("dev") || BuildConfig.FLAVOR.equalsIgnoreCase("uat"))) {
            throw new IllegalStateException("This can't be used outside of dev or uat!");
        }

        EHITripSummary ehiTripSummary = new EHITripSummary();
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, 30);
        calendar.add(Calendar.SECOND, 5);
        if (isCurrent) {
            ehiTripSummary.mReturnTime = calendar.getTime();
        } else {
            ehiTripSummary.mPickupTime = calendar.getTime();
        }

        if (EHITextUtils.isEmpty(confirmationNumber)) {
            if (isCurrent) {
                ehiTripSummary.mTicketNumber = "1120397";
            } else {
                ehiTripSummary.mConfirmationNumber = "1120397";
            }
        } else {
            if (isCurrent) {
                ehiTripSummary.mTicketNumber = confirmationNumber;
            } else {
                ehiTripSummary.mConfirmationNumber = confirmationNumber;
            }
        }
        return ehiTripSummary;
    }

    public String getMembershipNumber() {
        return mMembershipNumber;
    }

    public String getRentalAgreementNumber() {
        return mRentalAgreementNumber;
    }

    public String getContractName() {
        return mContractName;
    }

    public String getVatNumber() {
        return mVatNumber;
    }

    public List<EHIPaymentDetail> getPaymentDetails() {
        return mPaymentDetails;
    }

    public EHIAddressProfile getAddress() {
        return mAddress;
    }
}
