package com.ehi.enterprise.android.models.reservation;

import android.support.annotation.Nullable;
import android.support.annotation.StringDef;
import android.support.annotation.VisibleForTesting;

import com.ehi.enterprise.android.models.location.EHIImage;
import com.ehi.enterprise.android.network.responses.BaseResponse;
import com.ehi.enterprise.android.utils.BaseAppUtils;
import com.ehi.enterprise.android.utils.DLog;
import com.ehi.enterprise.android.utils.ListUtils;
import com.google.gson.annotations.SerializedName;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class EHICarClassDetails extends BaseResponse {

    public static final String SOLD_OUT = "SOLD_OUT";
    public static final String ON_REQUEST = "ON_REQUEST";
    public static final String ON_REQUEST_AT_PROMOTIONAL_RATE = "ON_REQUEST_AT_PROMOTIONAL_RATE";
    public static final String ON_REQUEST_AT_CONTRACT_RATE = "ON_REQUEST_AT_CONTRACT_RATE";

    public static final String CAR_CODE_VAN = "500";

    @Retention(RetentionPolicy.SOURCE)
    @StringDef({
            CAR_CLASS_CODE_TRUCK,
            CAR_CLASS_CODE_VAN
    })
    public @interface CarClassCode {
    }

    public static final String CAR_CLASS_CODE_TRUCK = "PPAR";
    public static final String CAR_CLASS_CODE_VAN = "SKAR";

    @Retention(RetentionPolicy.SOURCE)
    @StringDef({
            RESTRICTED_AT_RETAIL_RATE,
            RESTRICTED_AT_PROMOTIONAL_RATE,
            RESTRICTED_AT_CONTRACT_RATE
    })
    public @interface Restriction {
    }

    public static final String RESTRICTED_AT_RETAIL_RATE = "RESTRICTED_AT_RETAIL_RATE";
    public static final String RESTRICTED_AT_PROMOTIONAL_RATE = "RESTRICTED_AT_PROMOTIONAL_RATE";
    public static final String RESTRICTED_AT_CONTRACT_RATE = "RESTRICTED_AT_CONTRACT_RATE";

    @Retention(RetentionPolicy.SOURCE)
    @StringDef({
            AVAILABLE_AT_CONTRACT_RATE,
            AVAILABLE_AT_PROMOTIONAL_RATE
    })
    public @interface Availability {
    }

    public static final String AVAILABLE_AT_CONTRACT_RATE = "AVAILABLE_AT_CONTRACT_RATE";
    public static final String AVAILABLE_AT_PROMOTIONAL_RATE = "AVAILABLE_AT_PROMOTIONAL_RATE";

    @Retention(RetentionPolicy.SOURCE)
    @StringDef({
            BUSINESS_LIMIT,
            DURATION_LIMIT,
            POINTS_BALANCE_LIMIT,
            REDEMPTION_NOT_ALLOWED
    })
    public @interface EPlusRestriction {
    }

    public static final String BUSINESS_LIMIT = "BUSINESS_LIMIT";
    public static final String DURATION_LIMIT = "DURATION_LIMIT";
    public static final String POINTS_BALANCE_LIMIT = "POINTS_BALANCE_LIMIT";
    public static final String REDEMPTION_NOT_ALLOWED = "REDEMPTION_NOT_ALLOWED";

    @SerializedName("code")
    private String mCode;

    @SerializedName("name")
    private String mName;

    @SerializedName("make_model_or_similar_text")
    private String mMakeModelOrSimilarText;

    @SerializedName("call_for_availability_phone_number")
    private String mCallForAvailabilityPhoneNumber;

    @SerializedName("category")
    private EHICategory mCategory;

    @SerializedName("previously_selected")
    private boolean mPreviouslySelected;

    @SerializedName("speciality_vehicle")
    private boolean mSpecialityVehicle;

    @SerializedName("images")
    private List<EHIImage> mImages;

    @SerializedName("redemption_points")
    private String mRedemptionPoints;

    @SerializedName("eplus_max_redemption_days")
    private Integer mMaxRedemptionDays;

    @SerializedName("eplus_max_redemption_days_reason")
    private String mEplusMaxDaysReason;

    @SerializedName("terms_and_conditions_required")
    private boolean mTermsAndConditionsRequired;

    @SerializedName("features")
    private List<EHIFeature> mFeatures;

    @SerializedName("features_shorts")
    private List<Object> mFeaturesShorts;

    @SerializedName("description")
    private String mDescription;

    @SerializedName("people_capacity")
    private int mPeopleCapacity;

    @SerializedName("luggage_capacity")
    private int mLuggageCapacity;

//	@SerializedName("price_summary")
//	private EHIPriceSummary mPriceSummary;

    @SerializedName("status")
    private String mStatus;

    @SerializedName("vehicle_rates")
    private List<EHIVehicleRate> mVehicleRates;

    @SerializedName("price_differences")
    private List<EHIPriceDifferences> mPriceDifferences;

    @SerializedName("charges")
    private List<EHICharge> mCharges;

    @SerializedName("filters")
    private List<EHICarFilter> mCarFilters;

    @SerializedName("mileage_info")
    private EHIMileageInfo mMileageInfo;

    @SerializedName("redemption_day_count")
    private int mRedemptionDayCount;

    @SerializedName("eplus_points_used")
    private int mEplusPointsUsed;

    @SerializedName("truck_url")
    private String mTruckUrl;

    public void setEplusPointsUsed(int eplusPointsUsed) {
        mEplusPointsUsed = eplusPointsUsed;
    }

    public void setRedemptionDayCount(int redemptionDayCount) {
        mRedemptionDayCount = redemptionDayCount;
    }

    public int getEplusPointsUsed() {
        return mEplusPointsUsed;
    }

    public int getRedemptionDayCount() {
        return mRedemptionDayCount;
    }

    public String getCode() {
        return mCode;
    }

    public String getName() {
        return mName;
    }

    public String getMakeModelOrSimilarText() {
        return mMakeModelOrSimilarText;
    }

    public String getCallForAvailabilityPhoneNumber() {
        return mCallForAvailabilityPhoneNumber;
    }

    public EHICategory getCategory() {
        return mCategory;
    }

    public boolean isPreviouslySelected() {
        return mPreviouslySelected;
    }

    public boolean isSpecialityVehicle() {
        return mSpecialityVehicle;
    }

    public List<EHIImage> getImages() {
        return mImages;
    }

    public boolean isTermsAndConditionsRequired() {
        return mTermsAndConditionsRequired;
    }

    public List<EHIFeature> getFeatures() {
        return mFeatures;
    }

    public List getFeaturesShorts() {
        return mFeaturesShorts;
    }

    public String getDescription() {
        return mDescription;
    }

    public Number getPeopleCapacity() {
        return mPeopleCapacity;
    }

    public Number getLuggageCapacity() {
        return mLuggageCapacity;
    }

    private List<EHIPriceDifferences> getPriceDifferences() {
        return mPriceDifferences;
    }

    public String getPrePayPriceDifference() {
        if (!ListUtils.isEmpty(mPriceDifferences)) {
            for (EHIPriceDifferences priceDifferences : mPriceDifferences) {
                if (EHIPriceDifferences.PREPAY.equals(priceDifferences.getDifferenceType())) {
                    if (priceDifferences.getDifferenceAmountView() != null) {
                        return priceDifferences.getDifferenceAmountView().getFormattedPrice(false).toString();
                    }
                    return priceDifferences.getDifferenceAmountPayment().getFormattedPrice(false).toString();
                }
            }
        }
        return null;
    }

    public EHIPriceDifferences getUpgradePriceDifference(boolean prepay) {
        if (!ListUtils.isEmpty(mPriceDifferences)) {
            for (EHIPriceDifferences priceDifferences : mPriceDifferences) {
                String type = priceDifferences.getDifferenceType();
                if (prepay) {
                    if (EHIPriceDifferences.UPGRADE_PREPAY.equals(type)) {
                        return priceDifferences;
                    }
                } else {
                    if (EHIPriceDifferences.UPGRADE_PAYLATER.equals(type)
                            || EHIPriceDifferences.UPGRADE.equals(type)) {
                        return priceDifferences;
                    }
                }
            }
        }
        return null;
    }

    public EHIPriceDifferences getPriceDifferenceWithType(String type) {
        if (!ListUtils.isEmpty(mPriceDifferences)) {
            for (EHIPriceDifferences priceDifferences : mPriceDifferences) {
                if (type.equals(priceDifferences.getDifferenceType())) {
                    return priceDifferences;
                }
            }
        }
        return null;
    }

    public CharSequence getUnpaidPositiveRefundAmountPriceDifference(boolean withStyle) {
        return getUnpaidRefundAmountPriceDifference(true, withStyle);
    }

    public CharSequence getUnpaidRefundAmountPriceDifference(boolean withStyle) {
        return getUnpaidRefundAmountPriceDifference(false, withStyle);
    }

    @Nullable
    private CharSequence getUnpaidRefundAmountPriceDifference(boolean onlyPositiveAmount, boolean withStyle) {
        final EHIPrice differenceAmountView = getDifferenceAmountView();
        if (isDifferenceAmountValid() && differenceAmountView != null) {
            if (onlyPositiveAmount) {
                return differenceAmountView.getPositiveFormattedPrice(withStyle);
            } else {
                return differenceAmountView.getFormattedPrice(withStyle);
            }
        }
        return null;
    }

    @Nullable
    public CharSequence getUnpaidRefundAmountPaymentPrice() {
        final EHIPrice differenceAmountPayment = getDifferenceAmountPayment();
        if (isDifferenceAmountValid() && differenceAmountPayment != null) {
            return differenceAmountPayment.getFormattedPrice(false);
        }
        return null;
    }

    private boolean isDifferenceAmountValid() {
        return getDifferenceAmount() != null;

    }

    public boolean isUnpaidRefundAmountPriceDifferenceNegative() {
        final String differenceAmount = getDifferenceAmount();
        return differenceAmount != null && Float.valueOf(differenceAmount) >= 0;
    }

    @Nullable
    public EHIPrice getDifferenceAmountView() {
        EHIPriceDifferences priceDifferences = getPriceDifferenceWithType(EHIPriceDifferences.UNPAID_REFUND_AMOUNT);
        if (priceDifferences != null
                && priceDifferences.getDifferenceAmountView() != null) {
            return priceDifferences.getDifferenceAmountView();
        }
        return null;
    }

    @Nullable
    public EHIPrice getDifferenceAmountPayment() {
        EHIPriceDifferences priceDifferences = getPriceDifferenceWithType(EHIPriceDifferences.UNPAID_REFUND_AMOUNT);
        if (priceDifferences != null
                && priceDifferences.getDifferenceAmountPayment() != null) {
            return priceDifferences.getDifferenceAmountPayment();
        }
        return null;
    }

    @Nullable
    private String getDifferenceAmount() {
        final EHIPrice differenceAmountView = getDifferenceAmountView();
        if (differenceAmountView != null) {
            return differenceAmountView.getAmmount();
        }
        return null;
    }

    @Nullable
    public EHIExtras getExtras(@EHIVehicleRate.ChargeType String payState) {
        return getExtrasPricePortion(payState);
    }

    public List<EHICarFilter> getCarFilters() {
        return mCarFilters;
    }

    @Nullable
    public EHIPriceSummary getPriceSummary() {
        if (isPrepayRateAvailable()) {
            return getPrepayPriceSummary();
        } else {
            return getPaylaterPriceSummary();
        }
    }

    @Nullable
    public EHIPriceSummary getPriceSummary(boolean prepay) {
        if (prepay) {
            return getPrepayPriceSummary();
        } else {
            return getPaylaterPriceSummary();
        }
    }

    @Nullable
    public EHIPriceSummary getPrepayPriceSummary() {
        if (!ListUtils.isEmpty(mVehicleRates)) {
            for (EHIVehicleRate rate : mVehicleRates) {
                if (rate.getChargeType().equalsIgnoreCase(EHIVehicleRate.PREPAY)) {
                    return rate.getPriceSummary();
                }
            }
        }
        return null;
    }

    @Nullable
    public EHIPriceSummary getPaylaterPriceSummary() {
        if (!ListUtils.isEmpty(mVehicleRates)) {
            for (EHIVehicleRate rate : mVehicleRates) {
                if (rate.getChargeType().equalsIgnoreCase(EHIVehicleRate.PAYLATER)) {
                    return rate.getPriceSummary();
                }
            }
        }
        return null;
    }

    @Nullable
    public EHIExtras getExtrasPricePortion(@EHIVehicleRate.ChargeType String payState) {
        if (!ListUtils.isEmpty(mVehicleRates)) {
            for (EHIVehicleRate rate : mVehicleRates) {
                if (rate.getChargeType().equalsIgnoreCase(payState)) {
                    return rate.getExtras();
                }
            }
        }
        return null;
    }

    public String getTransmissionType() {
        for (int i = 0; i < mCarFilters.size(); i++) {
            if (mCarFilters.get(i).getFilterName().equalsIgnoreCase(EHICarFilter.TRANSMISSION_NAME)) {
                return mCarFilters.get(i).getFilterCode();
            }
        }
        return null;
    }

    public String getPassengerCode() {
        for (int a = 0; a < mCarFilters.size(); a++) {
            if (mCarFilters.get(a).getFilterName().equalsIgnoreCase(EHICarFilter.PASSENGER_NAME)) {
                return mCarFilters.get(a).getFilterCode();
            }
        }
        return null;
    }

    public String getStatus() {
        return mStatus;
    }

    public void setStatus(String status) {
        mStatus = status;
    }

    public List<EHIVehicleRate> getVehicleRates() {
        return mVehicleRates;
    }

    public CharSequence getPaylaterVehiclePriceView() {
        if (!ListUtils.isEmpty(mVehicleRates)) {
            for (EHIVehicleRate vehicleRate : mVehicleRates) {
                if (EHIVehicleRate.PAYLATER.equals(vehicleRate.getChargeType())) {
                    return vehicleRate.getPriceSummary().getEstimatedTotalView().getFormattedPrice(true);
                }
            }
        }
        return null;
    }

    public boolean isPrepayRateAvailable() {
        return isRateAvailable(EHIVehicleRate.PREPAY);
    }

    public boolean isPayLaterRateAvailable() {
        return isRateAvailable(EHIVehicleRate.PAYLATER);
    }

    private boolean isRateAvailable(String vehicleRateType) {
        if (!ListUtils.isEmpty(mVehicleRates)) {
            for (EHIVehicleRate vehicleRate : mVehicleRates) {
                if (vehicleRateType.equals(vehicleRate.getChargeType())) {
                    return vehicleRate.getPriceSummary() != null
                            && vehicleRate.getPriceSummary().getEstimatedTotalView() != null;
                }
            }
        }
        return false;
    }

    public boolean isPrepayChargesAvailable() {
        return hasCharges(EHIVehicleRate.PREPAY);
    }

    public boolean isPayLaterChargesAvailable() {
        return hasCharges(EHIVehicleRate.PAYLATER);
    }

    private boolean hasCharges(String vehicleRateType) {
        if (!ListUtils.isEmpty(mCharges)) {
            for (EHICharge charge : mCharges) {
                if (vehicleRateType.equals(charge.getChargeType())
                        && charge.hasRates()) {
                    return true;
                }
            }
        }
        return false;
    }

    public List<EHICharge> getCharge() {
        return mCharges;
    }

    public void setCharges(List<EHICharge> charges) {
        mCharges = charges;
    }

    public EHICharge getCharge(String type) {
        if (!ListUtils.isEmpty(mCharges)) {
            for (EHICharge charge : mCharges) {
                if (type.equals(charge.getChargeType())) {
                    return charge;
                }
            }
        }
        return null;
    }

    public CharSequence getPrepayChargePriceView() {
        final EHICharge charge = getCharge(EHICharge.PREPAY);
        return charge == null ? null : charge.getPriceView().getFormattedPrice(true);
    }

    public CharSequence getPaylaterChargePriceView() {
        final EHICharge charge = getCharge(EHICharge.PAYLATER);
        return charge == null ? null : charge.getPriceView().getFormattedPrice(true);
    }

    public boolean showTotalCostAsterisks(boolean isPrepay) {
        final EHICharge ehiCharge = getCharge(isPrepay);
        final EHIPriceSummary priceSummary = getPriceSummary(isPrepay);
        if (ehiCharge != null) {
            return comparePriceCurrency(ehiCharge.getPriceView(), ehiCharge.getPricePayment());
        }
        return priceSummary != null && comparePriceCurrency(priceSummary.getEstimatedTotalView(), priceSummary.getEstimatedTotalPayment());
    }

    private boolean comparePriceCurrency(EHIPrice totalPriceView, EHIPrice paymentPriceView) {
        return totalPriceView != null
                && paymentPriceView != null
                && !totalPriceView.getCurrencyCode().equalsIgnoreCase(paymentPriceView.getCurrencyCode());
    }

    private EHICharge getCharge(boolean isPrepay) {
        if (isPrepay) {
            return EHICharge.getPrePayCharge(getCharge());
        } else {
            return EHICharge.getPayLaterCharge(getCharge());
        }
    }

    public EHIMileageInfo getMileageInfo() {
        return mMileageInfo;
    }

    public boolean isManualTransmission() {
        for (EHIFeature feature : mFeatures) {
            if (EHIFeature.TRANSMISSION_CODE_AUTOMATIC.equals(feature.getCode())) {
                return false;
            } else if (EHIFeature.TRANSMISSION_CODE_MANUAL.equals(feature.getCode())) {
                return true;
            }
        }

        return false;
    }

    public static String getTransmissionDescription(List<EHIFeature> features) {
        String transmission = "";
        for (EHIFeature feature : features) {
            if (EHIFeature.TRANSMISSION_CODE_AUTOMATIC.equals(feature.getCode())
                    || EHIFeature.TRANSMISSION_CODE_MANUAL.equals(feature.getCode())) {
                transmission = feature.getDescription();
                break;
            }
        }
        return transmission;
    }

    private String getTransmissionCode() {
        String transmissionCode = "";
        for (EHIFeature feature : mFeatures) {
            if (EHIFeature.TRANSMISSION_CODE_MANUAL.equals(feature.getCode())) {
                transmissionCode = feature.getCode();
                break;
            }
        }
        return transmissionCode;
    }

    public String getTruckUrl() {
        return mTruckUrl;
    }

    public EHICarClassDetails merge(EHICarClassDetails carClasses) {
        if (ListUtils.isEmpty(mCharges) &&
                !ListUtils.isEmpty(carClasses.getCharge())) {
            mCharges = new LinkedList<>(carClasses.getCharge());
        }
        if (mStatus == null && carClasses.getStatus() != null) {
            mStatus = new String(carClasses.getStatus());
        }

        mMaxRedemptionDays = carClasses.getMaxRedemptionDays();
        return this;
    }

    public boolean shouldShowCallForAvailability() {
        return RESTRICTED_AT_RETAIL_RATE.equals(getStatus())
                || RESTRICTED_AT_PROMOTIONAL_RATE.equals(getStatus())
                || RESTRICTED_AT_CONTRACT_RATE.equals(getStatus());
    }

    public boolean shouldShowNegotiatedRates() {
        return getStatus().equals(AVAILABLE_AT_CONTRACT_RATE)
                || getStatus().equals(AVAILABLE_AT_PROMOTIONAL_RATE);
    }

    public int getMaxRedemptionDays() {
        if (mMaxRedemptionDays == null) {
            return 0;
        }
        return mMaxRedemptionDays;
    }

    public float getRedemptionPoints() {
        return mRedemptionPoints == null
                ? 0f
                : Float.parseFloat(mRedemptionPoints);
    }

    public boolean isRedemptionAvailable() {
        return mMaxRedemptionDays != null
                && mEplusMaxDaysReason != null
                && !mEplusMaxDaysReason.equalsIgnoreCase(REDEMPTION_NOT_ALLOWED);
    }

    public boolean isTransmissionTypeManual() {
        return getTransmissionCode().equalsIgnoreCase(EHIFeature.TRANSMISSION_CODE_MANUAL);
    }

    public boolean hasFilterCode(final String code) {
        final int intCode = Integer.parseInt(code);
        return BaseAppUtils.contains(intCode, getCarFilters(), new BaseAppUtils.CompareTwo<Integer, EHICarFilter>() {
            @Override
            public boolean equals(Integer first, EHICarFilter second) {
                try {
                    return intCode <= 15 && second.getFilterCode() != null && !second.getFilterCode().equalsIgnoreCase("null") && Integer.parseInt(second.getFilterCode()) < 16 && Integer.parseInt(second.getFilterCode()) >= intCode || code.equalsIgnoreCase(second.getFilterCode());
                } catch (NumberFormatException exception) {
                    DLog.e("Parse int", exception);
                    return code.equalsIgnoreCase(second.getFilterCode());
                }
            }
        });
    }

    public static List<EHICarClassDetails> filter(EHIFilterValue value, List<EHICarClassDetails> cars) {
        List<EHICarClassDetails> list = new ArrayList<>(cars.size());
        for (int i = 0; i < cars.size(); i++) {

            if (cars.get(i).hasFilterCode(value.getCode())) {
                list.add(cars.get(i));
            }
        }

        return list;
    }

    public static Map<String, EHICarClassDetails> andOperator(Map<String, EHICarClassDetails> one, boolean oneIsValid, Map<String, EHICarClassDetails> two, boolean twoIsValid) {
        if (!oneIsValid || !twoIsValid) {
            return one.size() != 0 ? one : two;
        }

        HashMap<String, EHICarClassDetails> result = new HashMap<>(one.keySet().size());

        Iterator iterator = two.entrySet().iterator();
        EHICarClassDetails details;
        while (iterator.hasNext()) {
            details = ((Map.Entry<String, EHICarClassDetails>) iterator.next()).getValue();
            if (one.containsKey(details.getCode())) {
                result.put(details.getCode(), details);
            }
        }
        return result;
    }

    public static List<EHICarClassDetails> applyFilters(List<EHICarClassDetails> carClassDetails, List<EHICarClassDetails> resetCars, List<EHIAvailableCarFilters> filters) {

        List<EHICarClassDetails> temp;
        Map<String, EHICarClassDetails> passengerHashmap = new HashMap<>(carClassDetails.size());
        Map<String, EHICarClassDetails> transmissionHashmap = new HashMap<>(carClassDetails.size());
        Map<String, EHICarClassDetails> carHashmap = new HashMap<>(carClassDetails.size());

        int passengerFilterAdded = 0;
        int carFilterAdded = 0;
        int transmissionFilterAdded = 0;

        for (int i = 0; i < filters.size(); i++) {
            if (filters.get(i).activeFilterCount() > 0) {
                if (filters.get(i).isNotCarTypeFilter()) {
                    temp = filter(filters.get(i), resetCars);

                    if (filters.get(i).getFilterCode().equalsIgnoreCase(EHIAvailableCarFilters.CAR_PASSENGERS)) {
                        passengerFilterAdded = 1;
                        for (int j = 0; j < temp.size(); j++) {
                            passengerHashmap.put(temp.get(j).getCode(), temp.get(j));
                        }

                    } else {
                        transmissionFilterAdded = 1;
                        for (int j = 0; j < temp.size(); j++) {
                            transmissionHashmap.put(temp.get(j).getCode(), temp.get(j));
                        }
                    }

                } else { // change if more than one is added.
                    carFilterAdded = 1;
                    temp = filter(filters.get(i), resetCars);
                    for (int j = 0; temp != null && j < temp.size(); j++) {
                        carHashmap.put(temp.get(j).getCode(), temp.get(j));
                    }
                }
            }
        }
        if (passengerFilterAdded + transmissionFilterAdded + carFilterAdded == 0) {
            return resetCars;
        }

        List<EHICarClassDetails> result = new ArrayList<>(carHashmap.keySet().size());

        Map<String, EHICarClassDetails> addedFilters;
        Iterator<Map.Entry<String, EHICarClassDetails>> iterator;

        if (passengerFilterAdded + carFilterAdded + transmissionFilterAdded == 1) {

            if (passengerFilterAdded == 1) {
                addedFilters = passengerHashmap;
            } else if (transmissionFilterAdded == 1) {
                addedFilters = transmissionHashmap;
            } else {
                addedFilters = carHashmap;
            }
            iterator = addedFilters.entrySet().iterator();
            EHICarClassDetails details;
            while (iterator.hasNext()) {
                details = iterator.next().getValue();
                result.add(details);
            }
        } else {
            if (passengerFilterAdded + transmissionFilterAdded == 0) {
                addedFilters = carHashmap;
            } else {
                addedFilters = andOperator(passengerHashmap, passengerFilterAdded == 1, transmissionHashmap, transmissionFilterAdded == 1);
                addedFilters = andOperator(addedFilters, true, carHashmap, carFilterAdded == 1);
            }

            iterator = addedFilters.entrySet().iterator();
            EHICarClassDetails details;
            while (iterator.hasNext()) {
                details = iterator.next().getValue();
                result.add(details);
            }

        }
        return result;
    }

    public static List<EHICarClassDetails> filter(EHIAvailableCarFilters availableCarFilters, List<EHICarClassDetails> cars) {
        HashMap<String, EHICarClassDetails> result = new HashMap<>(cars.size());
        List<EHICarClassDetails> temp;
        for (int i = 0; i < availableCarFilters.getFilterValues().size(); i++) {
            if (availableCarFilters.getFilterValues().get(i).isActive()) {

                temp = filter(availableCarFilters.getFilterValues().get(i), cars);
                for (int j = 0; j < temp.size(); j++) {
                    result.put(temp.get(j).getCode(), temp.get(j));
                }
            }
        }

        return new ArrayList<>(result.values());
    }

    @VisibleForTesting
    public void setVehicleRates(final List<EHIVehicleRate> vehicleRates) {
        mVehicleRates = vehicleRates;
    }

    public boolean isSecretRate() {
        final EHIExtras extras = getExtras(EHIVehicleRate.PAYLATER);
        return (ListUtils.isEmpty(getCharge()) &&
                (extras == null || ListUtils.isEmpty(extras.getSelectedExtras())));
    }

    public boolean isSecretRateAfterCarSelected() {
        final EHIExtras extras = getExtras(EHIVehicleRate.PAYLATER);
        return getPaylaterPriceSummary() != null
                && !getPaylaterPriceSummary().hasCharges()
                && (extras == null || ListUtils.isEmpty(extras.getSelectedExtras()));
    }
}