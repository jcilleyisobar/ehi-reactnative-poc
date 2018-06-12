package com.ehi.enterprise.android.models.reservation;

import android.support.annotation.Nullable;

import com.ehi.enterprise.android.models.location.EHILocation;
import com.ehi.enterprise.android.models.location.EHIPolicy;
import com.ehi.enterprise.android.models.profile.EHIPaymentMethod;
import com.ehi.enterprise.android.network.responses.BaseResponse;
import com.ehi.enterprise.android.utils.EHITextUtils;
import com.ehi.enterprise.android.utils.ListUtils;
import com.ehi.enterprise.android.utils.payment.CreditCard;
import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.List;
import java.util.Locale;

public class EHIReservation extends BaseResponse {

    public final static String PENDING = "PR";
    public final static String OPEN = "OP";
    public final static String CONFIRMED = "CF";
    public final static String CHECKED_OUT = "CO";
    public final static String NO_SHOW = "NS";
    public final static String CANCELED = "CN";
    public final static String CLOSED = "CL";

    public final static String BOOKING_SYSTEM_ECARS = "ECARS";
    public final static String BOOKING_SYSTEM_ODYSSEY = "ODYSSEY";

    @SerializedName("res_session_id")
    private String mResSessionId;

    @SerializedName("pickup_location")
    private EHILocation mPickupLocation;

    @SerializedName("return_location")
    private EHILocation mReturnLocation;

    @SerializedName("pickup_time")
    private Date mPickupTime;

    @SerializedName("return_time")
    private Date mReturnTime;

    @SerializedName("after_hours_return")
    private boolean mAfterHoursReturn;

    @SerializedName("contract_has_additional_benefits")
    private boolean mContractHasAdditionalBenefits;

    @SerializedName("contract_details")
    private EHIContract mCorporateAccount;

    @SerializedName("business_leisure_generic_disclaimer")
    private String mBusinessLeisure;

    @SerializedName("renter_age")
    private Number mRenterAge;

    @SerializedName("car_classes")
    private List<EHICarClassDetails> mCarClasses;

    @SerializedName("car_classes_filters")
    private List<EHIAvailableCarFilters> mCarclassFilterList;

    @SerializedName("excluded_extras")
    private List mExcludedExtras;

    @SerializedName("additional_information")
    private List<EHIAdditionalInformation> mAdditionalInformation;

    @SerializedName("alternative_pickup_locations")
    private List<EHILocation> mAlternativePickupLocations;

    @SerializedName("alternative_return_locations")
    private List<EHILocation> mAlternativeReturnLocations;

    @SerializedName("car_class_details")
    private EHICarClassDetails mCarClassDetails;

    @SerializedName("upgrade_vehicle_possible")
    private boolean mUpgradeVechiclePossible;

    @SerializedName("upgrade_car_class_details")
    private List<EHICarClassDetails> mUpgradeCarClassDetails;

    @SerializedName("extras_no_longer_available_after_upgrades")
    private List mExtrasNoLongerAvailableAfterUpgrades;

    @SerializedName("policies")
    private List<EHIPolicy> mPolicies;

    @SerializedName("confirmation_number")
    private String mConfirmationNumber;

    @SerializedName("delivery_allowed")
    private boolean mDeliveryAllowed;

    @SerializedName("collection_allowed")
    private boolean mCollectionAllowed;

    @SerializedName("vehicle_logistics")
    private EHIVehicleLogistic mVehicleLogistic;

    @SerializedName("reservation_eligibility")
    private EHIReservationEligibility mReservationEligibility;

    @SerializedName("driver_info")
    private EHIDriverInfo mDriverInfo;

    @SerializedName("business_leisure_info_allowed")
    private boolean mBusinessLeisureInfoAllowed;

    @SerializedName("prefill_deep_link_url")
    private String mQuickRentalUrl;

    @SerializedName("prepay_selected")
    private boolean mPrePaySelected;

    @SerializedName("prepay_payment_processor")
    @CreditCard.PaymentProcessorType
    private String mPrePayPaymentProcessor;

    @SerializedName("selected_payment_method")
    private EHIPaymentMethod mSelectedPaymentMethod;

    @SerializedName("billing_account")
    private EHIBillingAccount mBillingAccount;

    @SerializedName("european_union_country")
    private boolean mEuropeanUnionCountry;

    @SerializedName("reservation_status")
    private String mReservationStatus;

    @SerializedName("rules_of_the_road_url")
    private String mRulesOfRoad;

    @SerializedName("reservation_booking_system")
    private String mReservationBookingSystem;

    @SerializedName("redemption_day_count")
    private int mRedemptionDayCount;

    @SerializedName("eplus_points_used")
    private int mEplusPointsUsed;

    @SerializedName("airline_info")
    private EHIAirlineInformation mEHIAirlineInformation;

    @SerializedName("key_facts_policies")
    private List<EHIKeyFactsPolicy> mEHIKeyFactsPolicies;

    @SerializedName("payments")
    private List<EHIPayment> mPayments;

    @SerializedName("cancellation_details")
    private EHICancellation mCancellationDetails;

    @SerializedName("block_modify_pickup_location")
    private boolean mBlockModifyPickupLocation;

    @SerializedName("collect_new_payment_card_in_modify")
    private boolean mCollectNewPaymentCardInModify;

    public int getEplusPointsUsed() {
        return mEplusPointsUsed;
    }

    public int getRedemptionDayCount() {
        return mRedemptionDayCount;
    }

    public String getReservationStatus() {
        return mReservationStatus;
    }

    public boolean isBusinessLeisureInfoAllowed() {
        return mBusinessLeisureInfoAllowed;
    }

    public void setResSessionId(String resSessionId) {
        mResSessionId = resSessionId;
    }

    public String getResSessionId() {
        return mResSessionId;
    }

    public EHILocation getPickupLocation() {
        return mPickupLocation;
    }

    public EHILocation getReturnLocation() {
        return mReturnLocation;
    }

    public Date getPickupTime() {
        return mPickupTime;
    }

    public Date getReturnTime() {
        return mReturnTime;
    }

    public boolean isAfterHoursReturn() {
        return mAfterHoursReturn;
    }

    public String getBusinessLeisure() {
        return mBusinessLeisure;
    }

    public Number getRenterAge() {
        return mRenterAge;
    }

    public List<EHICarClassDetails> getCarClasses() {
        return mCarClasses;
    }

    public List getExcludedExtras() {
        return mExcludedExtras;
    }

    public List<EHIAdditionalInformation> getAdditionalInformation() {
        return mAdditionalInformation;
    }

    public List getAlternativePickupLocations() {
        return mAlternativePickupLocations;
    }

    public List getAlternativeReturnLocations() {
        return mAlternativeReturnLocations;
    }

    public boolean isUpgradeVechiclePossible() {
        return mUpgradeVechiclePossible;
    }

    public List<EHICarClassDetails> getUpgradeCarClassDetails() {
        return mUpgradeCarClassDetails;
    }

    public List getExtrasNoLongerAvailableAfterUpgrades() {
        return mExtrasNoLongerAvailableAfterUpgrades;
    }

    public EHIVehicleLogistic getVehicleLogistic() {
        return mVehicleLogistic;
    }

    public void setCorporateAccount(EHIContract corporateAccount) {
        mCorporateAccount = corporateAccount;
    }

    public EHIContract getCorporateAccount() {
        return mCorporateAccount;
    }

    public List<EHIPolicy> getPolicies() {
        return mPolicies;
    }

    public boolean isDeliveryAllowed() {
        return mDeliveryAllowed;
    }

    public boolean contractHasAdditionalBenefits() {
        if (mCorporateAccount != null) {
            return mCorporateAccount.contractHasAdditionalBenefits();
        }
        return false;
    }

    public boolean isCollectionAllowed() {
        return mCollectionAllowed;
    }

    public void setCarClassDetails(final EHICarClassDetails carClassDetails) {
        mCarClassDetails = carClassDetails;
    }

    public EHICarClassDetails getCarClassDetails() {
        return mCarClassDetails;
    }

    public String getConfirmationNumber() {
        return mConfirmationNumber;
    }

    public void setConfirmationNumber(String confirmationNumber) {
        mConfirmationNumber = confirmationNumber;
    }

    public EHIReservationEligibility getReservationEligibility() {
        return mReservationEligibility;
    }

    public void setReservationEligibility(EHIReservationEligibility reservationEligibility) {
        mReservationEligibility = reservationEligibility;
    }

    public EHIDriverInfo getDriverInfo() {
        return mDriverInfo;
    }

    public void setDriverInfo(EHIDriverInfo driverInfo) {
        mDriverInfo = driverInfo;
    }

    public void setPolicies(List<EHIPolicy> policies) {
        mPolicies = policies;
    }

    public boolean hasQuickRentalUrl() {
        return mQuickRentalUrl != null && !mQuickRentalUrl.isEmpty();
    }

    public EHIPaymentMethod getSelectedPaymentMethod() {
        return mSelectedPaymentMethod;
    }

    public void setSelectedPaymentMethod(EHIPaymentMethod selectedPaymentMethod) {
        mSelectedPaymentMethod = selectedPaymentMethod;
    }

    public void setUpgradeVechiclePossible(boolean upgradeVechiclePossible) {
        mUpgradeVechiclePossible = upgradeVechiclePossible;
    }

    public void setUpgradeCarClassDetails(List<EHICarClassDetails> upgradeCarClassDetails) {
        mUpgradeCarClassDetails = upgradeCarClassDetails;
    }

    public EHIBillingAccount getBillingAccount() {
        return mBillingAccount;
    }

    public void setBillingAccount(EHIBillingAccount billingAccount) {
        mBillingAccount = billingAccount;
    }

    public String getQuickRentalUrl() {
        return mQuickRentalUrl;
    }

    public void setQuickRentalUrl(String quickRentalUrl) {
        mQuickRentalUrl = quickRentalUrl;
    }

    public EHIAirlineInformation getAirlineInformation() {
        return mEHIAirlineInformation;
    }

    public void setAirlineInformation(EHIAirlineInformation EHIAirlineInformation) {
        mEHIAirlineInformation = EHIAirlineInformation;
    }

    public boolean isPrepaySelected() {
        return mPrePaySelected;
    }

    public boolean reservationBillingIsCorpBilling() {
        return getBillingAccount().getBillingAccountNumber().equalsIgnoreCase(getCorporateAccount().getBillingAccount());
    }

    public List<EHIAvailableCarFilters> getCarClassFilterList() {
        return mCarclassFilterList;
    }

    public void setCarclassFilterList(List<EHIAvailableCarFilters> carclassFilterList) {
        mCarclassFilterList = carclassFilterList;
    }

    public List<EHIPayment> getPayments() {
        return mPayments;
    }

    public void setPayments(List<EHIPayment> payments) {
        this.mPayments = payments;
    }

    public boolean isEuropeanUnionCountry() {
        return mEuropeanUnionCountry;
    }

    public String getRulesOfRoad() {
        return (mRulesOfRoad != null) ? mRulesOfRoad :  "";
    }

    public String getReservationBookingSystem() {
        return mReservationBookingSystem;
    }

    public List<EHIKeyFactsPolicy> getEHIKeyFactsPolicies() {
        return mEHIKeyFactsPolicies;
    }

    public EHICancellation getCancellationDetails() {
        return mCancellationDetails;
    }

    public boolean isOneWayRental() {
        return getReturnLocation() == null
                || (getPickupLocation() != null
                && !getPickupLocation().getId().equalsIgnoreCase(getReturnLocation().getId()));
    }

    public EHICarClassDetails getShortDetailsForSelectedClass() {
        for (EHICarClassDetails details : mCarClasses) {
            if (details.getCode().equalsIgnoreCase(mCarClassDetails.getCode())) {
                return details;
            }
        }
        return null;
    }

    public boolean isCancellable() {
        return getReservationEligibility() != null
                && getReservationEligibility().isCancelReservation();
    }

    public boolean isModifiable() {
        return getReservationEligibility() != null
                && getReservationEligibility().isModifyReservation();
    }

    public boolean doesLocationSupportRedemption() {
        boolean supportRedemption = false;
        if (mCarClasses != null
                && mCarClasses.size() > 0) {
            for (EHICarClassDetails detail : mCarClasses) {
                if (detail.isRedemptionAvailable()) {
                    supportRedemption = true;
                    break;
                }
            }
        }
        return supportRedemption;
    }

    public boolean doesSelectedCarClassSupportRedemption() {
        return mCarClassDetails != null
                && getCarClassDetails().isRedemptionAvailable();
    }


    //countries specific logic

    public boolean shouldMoveVansToEndOfList() {
        return !getPickupLocation().getAddress().getCountryCode().equals(Locale.US.getCountry())
                && !getPickupLocation().getAddress().getCountryCode().equals(Locale.CANADA.getCountry());
    }

    public boolean shouldShowIdentityCheckWithExternalVendorMessage() {
        return getPickupLocation().getAddress().getCountryCode().equals(Locale.UK.getCountry());
    }

    public boolean isPaymentProvidedPopulated() {
        return !EHITextUtils.isEmpty(mPrePayPaymentProcessor)
                && (mPrePayPaymentProcessor.equals(CreditCard.PANGUI)
                || mPrePayPaymentProcessor.equals(CreditCard.FARE_OFFICE));
    }

    public boolean doesCarClassListHasPrepayRates() {
        for (EHICarClassDetails carClass : mCarClasses) {
            if (carClass.isPrepayRateAvailable()) {
                return true;
            }
        }
        return false;
    }

    public boolean doesCarClassListHasPrepayCharges() {
        if (mCarClasses != null) {
            for (EHICarClassDetails carClass: mCarClasses) {
                if (carClass.isPrepayChargesAvailable()) {
                    return true;
                }
            }
        }
        return false;
    }

    @Nullable
    public EHIExtras getExtras() {
        return mCarClassDetails.getExtras(getPayState());
    }

    @EHIVehicleRate.ChargeType
    public String getPayState() {
        if (isPrepaySelected()) {
            return EHIVehicleRate.PREPAY;
        } else {
            return EHIVehicleRate.PAYLATER;
        }
    }

    public CharSequence getPreviousReservationTotal() {
        if (!ListUtils.isEmpty(mPayments)) {
            return mPayments.get(0).getAmount().getFormattedPrice(false);
        }
        return null;
    }

    public boolean shouldCollectNewPaymentCardInModify() {
        return mCollectNewPaymentCardInModify;
    }

    public boolean shouldBlockModifyPickupLocation() {
        return mBlockModifyPickupLocation;
    }

    public boolean hasCorporateContract() {
        return mCorporateAccount != null && mCorporateAccount.isCorporateContract();
    }
}