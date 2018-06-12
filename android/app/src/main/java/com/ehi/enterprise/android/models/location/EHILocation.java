package com.ehi.enterprise.android.models.location;

import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.annotation.StringDef;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.models.EHIModel;
import com.ehi.enterprise.android.models.location.solr.EHISolrLocation;
import com.ehi.enterprise.android.models.location.solr.EHISolrTimeItem;
import com.ehi.enterprise.android.models.location.solr.EHISolrWorkingDayInfo;
import com.ehi.enterprise.android.models.profile.EHIAddressProfile;
import com.ehi.enterprise.android.models.profile.EHIPhone;
import com.ehi.enterprise.android.models.reservation.EHIAirlineDetails;
import com.ehi.enterprise.android.models.reservation.EHICarClassDetails;
import com.ehi.enterprise.android.utils.BaseAppUtils;
import com.ehi.enterprise.android.utils.EHIPhoneNumberUtils;
import com.ehi.enterprise.android.utils.manager.LocationManager;
import com.google.gson.annotations.SerializedName;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class EHILocation extends EHIModel {

    public static final String TAG = "EHILocation";

    @Retention(RetentionPolicy.SOURCE)
    @StringDef({BRAND_ALAMO, BRAND_NATIONAL, BRAND_ENTERPRISE})
    public @interface LocationBrand {
    }

    public static final String BRAND_ALAMO = "ALAMO";
    public static final String BRAND_NATIONAL = "NATIONAL";
    public static final String BRAND_ENTERPRISE = "ENTERPRISE";

    @Retention(RetentionPolicy.SOURCE)
    @StringDef({INDICATOR_TRAIN, INDICATOR_PORT, INDICATOR_MOTORCYCLE})
    public @interface LocationIndicators {
    }

    public static final String INDICATOR_TRAIN = "TRN";
    public static final String INDICATOR_PORT = "POC";
    public static final String INDICATOR_MOTORCYCLE = "MTR";
    public static final String INDICATOR_EXOTIC = "EXC";

    public static EHILocation fromSolrLocation(EHISolrLocation solr) {
        EHILocation ehiLocation = new EHILocation();

        if (solr != null) {
            ehiLocation.setAfterHoursReturn(solr.isAfterHoursDropoff());
            ehiLocation.setAfterHoursPickup(solr.isAfterHoursPickup());
            ehiLocation.setAirportCode(solr.getAirportCode());
            ehiLocation.setLocationType(solr.getLocationType().getValue());
            ehiLocation.setBrand(solr.getBrand());
            ehiLocation.setCurrencyCode(solr.getCurrencyCode());
            ehiLocation.setName(solr.getTranslatedLocationName());
            ehiLocation.setId(solr.getPeopleSoftId());

//		EHIAddressProfile address = new EHIAddressProfile();
//		address.setStreetAddresses(solr.getAddressLines());
//		address.setCity(solr.getCity());
//		address.setCountryCode(solr.getCountryCode());
//		address.setCountrySubdivisionCode(solr.getState());
//		address.setPostal(solr.getPostalCode());
//		l.setAddress(address);

            EHILatLng latLng = new EHILatLng();
            if (solr.getLatitude() != null && solr.getLongitude() != null) {
                latLng.setLatitude(solr.getLatitude());
                latLng.setLongitude(solr.getLongitude());
            }
            ehiLocation.setGpsCoordinates(latLng);

//		EHIPhone number = new EHIPhone();
//		number.setPhoneType(EHIPhone.PhoneType.OFFICE.getValue());
//		number.setPhoneNumber(solr.getPhone());
//		l.setPhoneNumbers(Arrays.asList(number));
        }

        return ehiLocation;
    }

    @EHILocation.LocationBrand
    @SerializedName("brand")
    private String mBrand;

    @SerializedName("id")
    private String mId;

    @SerializedName("location_type")
    private String mLocationType;

    @SerializedName("business_types")
    private List<EHIBusinessType> mBusinessTypes;

    @SerializedName("indicators")
    private List<EHIIndicator> mIndicators;

    @SerializedName("name")
    private String mName;

    @SerializedName("description")
    private String mDescription;

    @SerializedName("airport_code")
    private String mAirportCode;

    @SerializedName("gps")
    private EHILatLng mGpsCoordinates;

    @SerializedName("drive_distance")
    private EHIDriveDistance mDriveDistance;

    @SerializedName("hours")
    private List<EHIWorkingHours> mWorkingHours;

    @SerializedName("time_zone_id")
    private String mTimeZoneId;

    @SerializedName("offered_car_classes")
    private List<EHICarClassDetails> mOfferedCarClasses;

    @SerializedName("filter_tags")
    private List<String> mFilterTags;

    @SerializedName("booking_urls")
    private List<EHIBookingUrl> mBookingUrls;

    @SerializedName("after_hours_pickup")
    private boolean mAfterHoursPickup;

    @SerializedName("after_hours_return")
    private boolean mAfterHoursReturn;

    @SerializedName("policies")
    private List<EHIPolicy> mPolicies;

    @SerializedName("wayfindings")
    private List<EHIWayfindingStep> mWayfindings;

    @SerializedName("address")
    private EHIAddressProfile mAddress;

    @SerializedName("phones")
    private List<EHIPhone> mPhoneNumbers;

    @SerializedName("currency_code")
    private String mCurrencyCode;

    @SerializedName("airline_details")
    private List<EHIAirlineDetails> mEHIAirlineDetails;

    @SerializedName("multi_terminal")
    private boolean mMultiTerminal;

    @EHILocation.LocationBrand
    public String getBrand() {
        return mBrand;
    }

    public void setBrand(String brand) {
        mBrand = brand;
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public boolean isNalmo() {
        final String brand = getBrand();
        return brand != null
                &&
                (brand.contains(EHILocation.BRAND_ALAMO)
                        || brand.contains(EHILocation.BRAND_NATIONAL));
    }

    public void setLocationType(String locationType) {
        mLocationType = locationType;
    }

    public List<EHIBusinessType> getBusinessTypes() {
        return mBusinessTypes;
    }

    public void setBusinessTypes(List<EHIBusinessType> businessTypes) {
        mBusinessTypes = businessTypes;
    }

    public List<EHIAirlineDetails> getEHIAirlineDetails() {
        return mEHIAirlineDetails;
    }

    public String getRawNameValue() {
        return mName;
    }

    public String getName() {
        if (mName != null && mName.trim().length() > 0) {
            return mName;
        }

        if (getAddress() != null) {
            return getAddress().getReadableAddress();
        }

        return null;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    public String getAirportCode() {
        return mAirportCode;
    }

    public void setAirportCode(String airportCode) {
        mAirportCode = airportCode;
    }

    public EHILatLng getGpsCoordinates() {
        return mGpsCoordinates;
    }

    public void setGpsCoordinates(EHILatLng gpsCoordinates) {
        mGpsCoordinates = gpsCoordinates;
    }

    public EHIDriveDistance getDriveDistance() {
        return mDriveDistance;
    }

    public void setDriveDistance(EHIDriveDistance driveDistance) {
        mDriveDistance = driveDistance;
    }

    public List<EHIWorkingHours> getWorkingHours() {
        return mWorkingHours;
    }

    public void setWorkingHours(Map<String, EHISolrWorkingDayInfo> daysInfo) {
        mWorkingHours.clear();
        List<EHIWorkingDayInfo> list = new ArrayList<>();
        for (String key : daysInfo.keySet()) {
            final EHISolrTimeItem solrItem = daysInfo.get(key).getStandardTime();
            final EHIWorkingDayInfo ehiItem = new EHIWorkingDayInfo(solrItem, key);
            list.add(ehiItem);
        }
        mWorkingHours.add(new EHIWorkingHours(list));
    }

    public String getTimeZoneId() {
        return mTimeZoneId;
    }

    public void setTimeZoneId(String timeZoneId) {
        mTimeZoneId = timeZoneId;
    }

    public List<EHICarClassDetails> getOfferedCarClasses() {
        return mOfferedCarClasses;
    }

    public void setOfferedCarClasses(List<EHICarClassDetails> offeredCarClasses) {
        mOfferedCarClasses = offeredCarClasses;
    }

    public List<String> getFilterTags() {
        return mFilterTags;
    }

    public String getLocationType() {
        return mLocationType;
    }

    public void setFilterTags(List<String> filterTags) {
        mFilterTags = filterTags;
    }

    public List<EHIBookingUrl> getBookingUrls() {
        return mBookingUrls;
    }

    public void setBookingUrls(List<EHIBookingUrl> bookingUrls) {
        mBookingUrls = bookingUrls;
    }

    public boolean isAfterHoursPickup() {
        return mAfterHoursPickup;
    }

    public void setAfterHoursPickup(boolean afterHoursPickup) {
        mAfterHoursPickup = afterHoursPickup;
    }

    public boolean isAfterHoursReturn() {
        return mAfterHoursReturn;
    }

    public void setAfterHoursReturn(boolean afterHoursReturn) {
        mAfterHoursReturn = afterHoursReturn;
    }

    public List<EHIPolicy> getPolicies() {
        return mPolicies;
    }

    public void setPolicies(List<EHIPolicy> policies) {
        mPolicies = policies;
    }

    public List<EHIWayfindingStep> getWayfindings() {
        return mWayfindings;
    }

    public void setWayfindings(List<EHIWayfindingStep> wayfindings) {
        mWayfindings = wayfindings;
    }

    public EHIAddressProfile getAddress() {
        return mAddress;
    }

    public void setAddress(EHIAddressProfile address) {
        mAddress = address;
    }

    public List<EHIPhone> getPhoneNumbers() {
        return mPhoneNumbers;
    }

    public void setPhoneNumbers(List<EHIPhone> phoneNumbers) {
        mPhoneNumbers = phoneNumbers;
    }

    public String getCurrencyCode() {
        return mCurrencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        mCurrencyCode = currencyCode;
    }

    public boolean isFavorite() {
        return LocationManager.getInstance().isFavoriteLocation(getId());
    }

    public boolean haveGpsCoordinates() {
        return getGpsCoordinates() != null && getGpsCoordinates().getLocation() != null;
    }

    public boolean isMultiTerminal() {
        return mMultiTerminal;
    }

    @DrawableRes
    public int getMapPinDrawable(boolean active) {
        if (isFavorite()) {
            return active ? R.drawable.map_pin_fav_selected : R.drawable.map_pin_fav;
        }

        if (isExotic()) {
            return active ? R.drawable.map_pin_exotics_selected : R.drawable.map_pin_exotics;
        }

        if (getBrand() != null) {
            switch (getBrand()) {
                case EHILocation.BRAND_ALAMO:
                    return active ? R.drawable.map_pin_alamo_selected : R.drawable.map_pin_alamo;
                case EHILocation.BRAND_NATIONAL:
                    return active ? R.drawable.map_pin_national_selected : R.drawable.map_pin_national;
            }
        }

        if (isMotorcycle()) {
            return active ? R.drawable.map_pin_motorcycles_selected : R.drawable.map_pin_motorcycles;
        }

        if (isAirport()) {
            return active ? R.drawable.map_pin_airports_selected : R.drawable.map_pin_airports;
        } else if (isPort()) {
            return active ? R.drawable.map_pin_port_selected : R.drawable.map_pin_port;
        } else if (isTrainStation()) {
            return active ? R.drawable.map_pin_rail_selected : R.drawable.map_pin_rail;
        } else {
            return active ? R.drawable.map_pin_standard_selected : R.drawable.map_pin_standard;
        }
    }

    @DrawableRes
    public int getGreenLocationCellIconDrawable() {
        if (isFavorite()) {
            return R.drawable.icon_favorites_03;
        }

        if (isExotic()) {
            return R.drawable.icon_exotics_gray;
        }

        if (getBrand() != null) {
            switch (getBrand()) {
                case EHILocation.BRAND_ALAMO:
                case EHILocation.BRAND_NATIONAL:
                    return getMapPinDrawable(false);
            }
        }

        if (isAirport()) {
            return R.drawable.icon_airport_green;
        } else if (isPort()) {
            return R.drawable.icon_port_01;
        } else if (isTrainStation()) {
            return R.drawable.icon_rail_01;
        } else {
            return -1;
        }
    }

    @DrawableRes
    public int getGrayLocationCellIconDrawable() {
        if (isFavorite()) {
            return R.drawable.icon_favorites_01;
        }

        if (isExotic()) {
            return R.drawable.icon_exotics_gray;
        }

        if (getBrand() != null) {
            switch (getBrand()) {
                case EHILocation.BRAND_ALAMO:
                case EHILocation.BRAND_NATIONAL:
                    return getMapPinDrawable(false);
            }
        }

        if (isAirport()) {
            return R.drawable.icon_airport_gray;
        } else if (isPort()) {
            return R.drawable.icon_port_02;
        } else if (isTrainStation()) {
            return R.drawable.icon_rail_02;
        } else {
            return -1;
        }
    }

    public boolean isAirport() {
        return EHISolrLocation.LocationType.AIRPORT.getValue().equalsIgnoreCase(getLocationType());
    }

    public boolean isPort() {
        return isIndicator(INDICATOR_PORT);
    }

    public boolean isTrainStation() {
        return isIndicator(INDICATOR_TRAIN);
    }

    public boolean isMotorcycle() {
        return isIndicator(INDICATOR_MOTORCYCLE);
    }

    public boolean isExotic() {
        return isIndicator(INDICATOR_EXOTIC);
    }

    public String getFormattedPhoneNumber(boolean withFormatting) {
        return EHIPhoneNumberUtils.formatNumberForMobileDialing(getPrimaryPhoneNumber(), getAddress().getCountryCode(), withFormatting);
    }

    /**
     * Returns office phone number, or any other number if there is no office one.
     */
    public String getPrimaryPhoneNumber() {
        for (EHIPhone number : mPhoneNumbers) {
            if (number.getPhoneType() == EHIPhone.PhoneType.OFFICE && number.isDefaultIndicator()) {
                return number.getPhoneNumber();
            }
        }
//		//Have no OFFICE number, so will show the first one from the list
        if (mPhoneNumbers.size() > 0) {
            return getPhoneNumbers().get(0).getPhoneNumber();
        }

        return "";
    }

    public List<EHIWorkingDayInfo> getWeekAfterDate(Date currentDate) {
        final List<EHIWorkingHours> workingHoursList = getWorkingHours();
        if (workingHoursList == null || workingHoursList.size() == 0) {
            return new ArrayList<>();
        }

        List<EHIWorkingDayInfo> days = new ArrayList<>();
        for (EHIWorkingHours week : workingHoursList) {
            if (EHIWorkingHours.TYPE_STANDARD.equals(week.getType())) {
                days.addAll(week.getWorkingDays());
            }
        }

        for (int i = 0, size = days.size(); i < size; i++) {
            final Date day = days.get(i).getDateObject();
            if (day != null && day.after(currentDate)) {
                int sublistBegin = i == 0 ? 0 : i - 1;
                int sublistEnd = sublistBegin + 7 < days.size() - 1 ? sublistBegin + 7 : days.size() - 1;

                return days.subList(sublistBegin, sublistEnd);
            }
        }

        //fallback - will show first week server sends to us
        return workingHoursList.get(0).getWorkingDays();
    }

    @Nullable
    public String getOfficeNumber() {
        final List<EHIPhone> phoneNumbers = getPhoneNumbers();
        for (int i = 0, size = phoneNumbers.size(); i < size; i++) {
            final EHIPhone ehiPhone = phoneNumbers.get(i);
            if (ehiPhone.getPhoneType().equals(EHIPhone.PhoneType.OFFICE)) {
                return ehiPhone.getPhoneNumber();
            }
        }
        return null;
    }

    @Nullable
    public EHIPolicy getAfterHoursPolicy() {
        int index = -1;

        if (mPolicies != null) {
            index = BaseAppUtils.indexOf(EHIPolicy.AfterHours, mPolicies, new BaseAppUtils.CompareTwo<String, EHIPolicy>() {
                @Override
                public boolean equals(String first, EHIPolicy second) {
                    return second.isPolicy(first);
                }
            });
        }

        if (index != -1) {
            return mPolicies.get(index);
        }

        return null;
    }

    private boolean isIndicator(String indicator) {
        if (mIndicators != null) {
            for (EHIIndicator ehiIndicator : mIndicators) {
                if (ehiIndicator.getCode().equalsIgnoreCase(indicator)) {
                    return true;
                }
            }
        }
        return false;
    }
}