package com.ehi.enterprise.android.models.location.solr;

import android.graphics.Color;
import android.location.Location;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.models.EHIModel;
import com.ehi.enterprise.android.models.location.EHILocation;
import com.ehi.enterprise.android.utils.manager.LocalDataManager;
import com.ehi.enterprise.android.utils.manager.LocationManager;
import com.google.android.m4b.maps.model.LatLng;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class EHISolrLocation extends EHIModel {

    private static final String ATTR_ONE_WAY = "ONE_WAYS";
    private static final String ATTR_MOTORCYCLES = "MOTORCYCLES";
    private static final String ATTR_EXOTICS = "EXOTICS";

    public enum LocationType {
        AIRPORT("airport"),
        CITY("city"),
        RAIL("rail"),
        PORT("port_of_call");

        private final String mValue;

        LocationType(String value) {
            this.mValue = value;
        }

        public String getValue() {
            return mValue;
        }

        @NonNull
        public static LocationType fromValue(String value) {
            if (value == null) {
                return CITY;
            }
            if (value.equalsIgnoreCase("airport")) {
                return AIRPORT;
            } else if (value.equalsIgnoreCase("port_of_call")) {
                return PORT;
            } else if (value.equalsIgnoreCase("rail")) {
                return RAIL;
            } else {
                return CITY;
            }

        }
    }

    @SerializedName("addressLines")
    private List<String> mAddressLines;
    @SerializedName("_version_")
    private long mVersion;
    @SerializedName("afterHoursDropoff")
    private boolean mAfterHoursDropoff;
    @SerializedName("afterHoursPickup")
    private boolean mAfterHoursPickup;
    @SerializedName("airportCode")
    private String mAirportCode;
    @SerializedName("brand")
    private String mBrand;
    @SerializedName("city")
    private String mCity;
    @SerializedName("cityId")
    private String mCityId;//???
    @SerializedName("countryCode")
    private String mCountryCode;
    @SerializedName("currencyCode")
    private String mCurrencyCode;
    @SerializedName("defaultLocationName")
    private String mDefaultLocationName;
    @SerializedName("locationNameTranslation")
    private String mLocationNameTranslation;
    @SerializedName("internationalPhoneNumberPrefix")
    private String mInternationalPhoneNumberPrefix;//???
    @SerializedName("latitude")
    private Double mLatitude;
    @SerializedName("locationType")
    private String mLocationType;
    @SerializedName("longitude")
    private Double mLongitude;
    @SerializedName("peopleSoftId")
    private String mPeopleSoftId;
    @SerializedName("phoneNumber")
    private String mPhoneNumber;
    @SerializedName("postalCode")
    private String mPostalCode;
    @SerializedName("state")
    private String mState;
    @SerializedName("stationId")
    private String mStationId;//???
    @SerializedName("is24HourLocation")
    private boolean mOpen247;
    @SerializedName("openSundays")
    private boolean mOpenSundays;
    @SerializedName("calculatedDistance")
    private Double mCalculatedDistance;
    @SerializedName("attributes")
    private List<String> mAttributes;
    @SerializedName("bookingUrl")
    private String mBookingUrl;
    @SerializedName("ageOptions")
    private List<EHIAgeOption> mAgeOptions;
    @SerializedName("pickupValidity")
    private EHISolrLocationValidity mPickupValidity;
    @SerializedName("dropoffValidity")
    private EHISolrLocationValidity mDropoffValidity;

    private String mDistanceToUserLocation; // already formatted, with mi/km at the end.

    public String getReadableAddress() {
        StringBuilder bld = new StringBuilder();
        boolean isEuropeanAddress = LocalDataManager.getInstance().isEuropeanAddress(mCountryCode);
        if (mAddressLines != null) {
            for (String address : mAddressLines) {
                bld.append(address);
                bld.append(", ");
            }
        }
        if (isEuropeanAddress) {
            if (mPostalCode != null) {
                bld.append(mPostalCode);
                bld.append(" ");
            }
        }
        if (mCity != null) {
            bld.append(mCity);
        }
        if (mState != null) {
            bld.append(", ");
            bld.append(mState);
        }
        if (!isEuropeanAddress) {
            bld.append(" ");
            bld.append(mPostalCode);
        }
        return bld.toString().trim();
    }

    public List<String> getAddressLines() {
        return mAddressLines;
    }

    public boolean isAirport() {
        return LocationType.fromValue(mLocationType).equals(LocationType.AIRPORT);
    }

    public long getVersion() {
        return mVersion;
    }

    public boolean isAfterHoursDropoff() {
        return mAfterHoursDropoff;
    }

    public boolean isAfterHoursPickup() {
        return mAfterHoursPickup;
    }

    public String getAirportCode() {
        return mAirportCode;
    }

    public String getBrand() {
        return mBrand;
    }

    public void setBrand(@EHILocation.LocationBrand String brand) {
        mBrand = brand;
    }

    public String getCity() {
        return mCity;
    }

    public String getCityId() {
        return mCityId;
    }

    public String getCountryCode() {
        return mCountryCode;
    }

    public String getCurrencyCode() {
        return mCurrencyCode;
    }

    public String getDefaultLocationName() {
        return mDefaultLocationName;
    }

    public String getTranslatedLocationName() {
        if (mLocationNameTranslation == null || mLocationNameTranslation.trim().length() == 0) {
            return getReadableAddress();
        }
        return mLocationNameTranslation;
    }

    public Spannable getNameWithAirportCode() {
        SpannableStringBuilder bld = new SpannableStringBuilder();
        bld.append(getTranslatedLocationName());

        if (mAirportCode != null && !mAirportCode.isEmpty()) {
            bld.append(" ");
            SpannableString textToShow = new SpannableString(mAirportCode);
            textToShow.setSpan(new RelativeSizeSpan(0.75f), 0, mAirportCode.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            textToShow.setSpan(new ForegroundColorSpan(Color.BLACK), 0, mAirportCode.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            bld.append(textToShow);
        }

        return bld;
    }

    public String getInternationalPhoneNumberPrefix() {
        return mInternationalPhoneNumberPrefix;
    }

    public Double getLatitude() {
        return mLatitude;
    }

    public void setLongitude(Double longitude) {
        mLongitude = longitude;
    }

    public void setLatitude(Double latitude) {
        mLatitude = latitude;
    }

    public LocationType getLocationType() {
        return LocationType.fromValue(mLocationType);
    }

    public void setLocationType(LocationType type) {
        mLocationType = type.getValue();
    }

    @Nullable
    public Location getLocation() {
        if (mLatitude == null || mLongitude == null) {
            return null;
        }

        Location location = new Location("");
        location.setLatitude(mLatitude);
        location.setLongitude(mLongitude);

        return location;
    }

    @Nullable
    public LatLng getLatLng() {
        if (mLatitude == null || mLongitude == null) {
            return null;
        }

        return new LatLng(mLatitude, mLongitude);
    }

    public Double getLongitude() {
        return mLongitude;
    }

    public String getPeopleSoftId() {
        return mPeopleSoftId;
    }

    public String getPhoneNumber() {
        return mPhoneNumber;
    }

    public String getPostalCode() {
        return mPostalCode;
    }

    public String getState() {
        return mState;
    }

    public String getStationId() {
        return mStationId;
    }

    public boolean isFavorite() {
        return LocationManager.getInstance().isFavoriteLocation(getPeopleSoftId());
    }

    public void setDefaultLocationName(String defaultLocationName) {
        mDefaultLocationName = defaultLocationName;
    }

    public void setLocationNameTranslation(String locationNameTranslation) {
        mLocationNameTranslation = locationNameTranslation;
    }

    public void setAddressLines(List<String> addressLines) {
        mAddressLines = addressLines;
    }

    public void setCity(String city) {
        mCity = city;
    }

    public void setState(String state) {
        mState = state;
    }

    public void setPostalCode(String postalCode) {
        mPostalCode = postalCode;
    }

    @DrawableRes
    public int getMapPinDrawable(boolean selected) {
        final boolean isClosed = isLocationInvalid();
        if (isFavorite()) {
            return selected ? R.drawable.map_pin_fav_selected : R.drawable.map_pin_fav;
        }
        if (isClosed) {
            return getClosedLocationIcon(selected);
        } else {
            return getOpenLocationIcon(selected);
        }
    }

    private int getClosedLocationIcon(boolean selected) {
        if (isExoticLocation()) {
            return selected ? R.drawable.map_pin_exotics_selected_closed : R.drawable.map_pin_exotics_closed;
        }

        if (getBrand() != null) {
            switch (getBrand()) {
                case EHILocation.BRAND_ALAMO:
                    return selected ? R.drawable.map_pin_alamo_selected : R.drawable.map_pin_alamo;
                case EHILocation.BRAND_NATIONAL:
                    return selected ? R.drawable.map_pin_national_selected : R.drawable.map_pin_national;
            }
        }

        if (isMotorcycleLocation()) {
            return selected ? R.drawable.map_pin_motorcycles_selected : R.drawable.map_pin_motorcycles;
        }

        if (getLocationType() == LocationType.AIRPORT) {
            return selected ? R.drawable.map_pin_airports_selected_closed : R.drawable.map_pin_airports_closed;
        } else if (getLocationType() == LocationType.PORT) {
            return selected ? R.drawable.map_pin_port_selected_closed : R.drawable.map_pin_port_closed;
        } else if (getLocationType() == LocationType.RAIL) {
            return selected ? R.drawable.map_pin_rail_selected_closed : R.drawable.map_pin_rail_closed;
        } else if (getLocationType() == LocationType.CITY) {
            return selected ? R.drawable.map_pin_standard_selected_closed : R.drawable.map_pin_standard_closed;
        }

        return selected ? R.drawable.map_pin_standard_selected_closed : R.drawable.map_pin_standard_closed;
    }

    private int getOpenLocationIcon(boolean active) {
        if (isExoticLocation()) {
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

        if (isMotorcycleLocation()) {
            return active ? R.drawable.map_pin_motorcycles_selected : R.drawable.map_pin_motorcycles;
        }

        if (getLocationType() == LocationType.AIRPORT) {
            return active ? R.drawable.map_pin_airports_selected : R.drawable.map_pin_airports;
        } else if (getLocationType() == LocationType.PORT) {
            return active ? R.drawable.map_pin_port_selected : R.drawable.map_pin_port;
        } else if (getLocationType() == LocationType.RAIL) {
            return active ? R.drawable.map_pin_rail_selected : R.drawable.map_pin_rail;
        } else if (getLocationType() == LocationType.CITY) {
            return active ? R.drawable.map_pin_standard_selected : R.drawable.map_pin_standard;
        }

        return active ? R.drawable.map_pin_standard_selected : R.drawable.map_pin_standard;
    }

    @DrawableRes
    public int getGreenLocationCellIconDrawable(boolean markFavorite) {
        if (isFavorite()
                && markFavorite) {
            return R.drawable.icon_favorites_03;
        }

        if (isExoticLocation()) {
            return R.drawable.icon_exotics_gray;
        }

        if (getBrand() != null) {
            switch (getBrand()) {
                case EHILocation.BRAND_ALAMO:
                case EHILocation.BRAND_NATIONAL:
                    return getMapPinDrawable(false);
            }
        }

        if (getLocationType() == LocationType.AIRPORT) {
            return R.drawable.icon_airport_green;
        } else if (getLocationType() == LocationType.PORT) {
            return R.drawable.icon_port_01;
        } else if (getLocationType() == LocationType.RAIL) {
            return R.drawable.icon_rail_01;
        } else if (getLocationType() == LocationType.CITY) {
            return -1;
        }

        return -1;
    }

    @DrawableRes
    public int getGrayLocationCellIconDrawable() {
        if (isFavorite()) {
            return R.drawable.icon_favorites_01;
        }

        if (isExoticLocation()) {
            return R.drawable.icon_exotics_gray;
        }

        if (getBrand() != null) {
            switch (getBrand()) {
                case EHILocation.BRAND_ALAMO:
                case EHILocation.BRAND_NATIONAL:
                    return getMapPinDrawable(false);
            }
        }

        if (getLocationType() == LocationType.AIRPORT) {
            return R.drawable.icon_airport_gray;
        } else if (getLocationType() == LocationType.PORT) {
            return R.drawable.icon_port_02;
        } else if (getLocationType() == LocationType.RAIL) {
            return R.drawable.icon_rail_02;
        } else if (getLocationType() == LocationType.CITY) {
            return -1;
        }

        return -1;
    }

    public List<EHIAgeOption> getAgeOptions() {
        return mAgeOptions;
    }

    public void setAgeOptions(List<EHIAgeOption> ageOptions) {
        mAgeOptions = ageOptions;
    }

    public static boolean compareToLocation(EHISolrLocation location1, EHISolrLocation location2) {
        if (location1 == null || location2 == null) {
            return location1 == location2;
        }
        return location1.getPeopleSoftId().equalsIgnoreCase(location2.getPeopleSoftId());
    }

    public void setPeopleSoftId(String peopleSoftId) {
        mPeopleSoftId = peopleSoftId;
    }

    public boolean isOpen247() {
        return mOpen247;
    }

    public void setOpen247(boolean open247) {
        mOpen247 = open247;
    }

    public boolean isNalmo() {
        return getBrand().contains(EHILocation.BRAND_ALAMO) || getBrand().contains(EHILocation.BRAND_NATIONAL);
    }

    public boolean isOpenSundays() {
        return mOpenSundays;
    }

    public void setOpenSundays(boolean openSundays) {
        mOpenSundays = openSundays;
    }

    public String getDistanceToUserLocation() {
        return mDistanceToUserLocation;
    }

    public void setDistanceToUserLocation(String distanceToUserLocation) {
        mDistanceToUserLocation = distanceToUserLocation;
    }

    public Double getCalculatedDistance() {
        return mCalculatedDistance;
    }

    public void setCalculatedDistance(Double calculatedDistance) {
        mCalculatedDistance = calculatedDistance;
    }

    public boolean isOneWaySupported() {
        return isLocation(ATTR_ONE_WAY);
    }

    public boolean isMotorcycleLocation() {
        return isLocation(ATTR_MOTORCYCLES);
    }

    public boolean isExoticLocation() {
        return isLocation(ATTR_EXOTICS);
    }

    public String getBookingUrl() {
        return mBookingUrl;
    }

    public static EHISolrLocation fromLocation(EHILocation location) {
        EHISolrLocation solrLocation = new EHISolrLocation();
        solrLocation.setPeopleSoftId(location.getId());
        solrLocation.setDefaultLocationName(location.getRawNameValue());
        solrLocation.setLocationNameTranslation(location.getRawNameValue());
        solrLocation.setAddressLines(location.getAddress().getStreetAddresses());
        solrLocation.setCity(location.getAddress().getCity());
        solrLocation.setState(location.getAddress().getCountrySubdivisionName());
        solrLocation.setPostalCode(location.getAddress().getPostal());

        //Default to Enterprise is no brand is present. Should always be Enterprise brand though at the modify stage
        String brand = location.getBrand() == null ? EHILocation.BRAND_ENTERPRISE : location.getBrand();
        solrLocation.setBrand(brand);

        if (location.isTrainStation()) {
            solrLocation.setLocationType(LocationType.RAIL);
        } else if (location.isAirport()) {
            solrLocation.setLocationType(LocationType.AIRPORT);
        } else if (location.isPort()) {
            solrLocation.setLocationType(LocationType.PORT);
        } else {
            solrLocation.setLocationType(LocationType.CITY);
        }

        return solrLocation;
    }

    private boolean isLocation(String attribute) {
        if (mAttributes == null || mAttributes.size() == 0) {
            return false;
        }

        for (String attr : mAttributes) {
            if (attr.trim().equalsIgnoreCase(attribute)) {
                return true;
            }
        }

        return false;
    }

    public EHISolrLocationValidity getPickupValidity() {
        return mPickupValidity;
    }

    public List<EHISolrTimeSpan> getStandardPickupOpenCloseHours() {
        if (mPickupValidity == null) {
            return null;
        }
        return mPickupValidity.getStandardOpenCloseHours();
    }

    public List<EHISolrTimeSpan> getStandardDropoffOpenCloseHours() {
        if (mDropoffValidity == null) {
            return null;
        }
        return mDropoffValidity.getStandardOpenCloseHours();
    }

    public EHISolrLocationValidity getDropoffValidity() {
        return mDropoffValidity;
    }

    public boolean isLocationInvalid() {
        return (mPickupValidity != null
                && mPickupValidity.isLocationInvalid())
                || (mDropoffValidity != null
                && mDropoffValidity.isLocationInvalid());
    }

    public boolean isDropoffAfterHours() {
        return (mDropoffValidity != null
                && mDropoffValidity.isAfterHours());
    }

    public boolean isInvalidForDropoff() {
        return mDropoffValidity != null && mDropoffValidity.isLocationInvalid();
    }

    public boolean isAllDayClosedForPickup() {
        return mPickupValidity != null && mPickupValidity.isAllDayClosed();
    }

    public boolean isAllDayClosedForDropoff() {
        return mDropoffValidity != null && mDropoffValidity.isAllDayClosed();
    }

    public boolean isInvalidAtTimeForPickup() {
        return mPickupValidity != null && mPickupValidity.isInvalidAtTime();
    }

    public boolean isInvalidAtTimeForDropoff() {
        return mDropoffValidity != null && mDropoffValidity.isInvalidAtTime();
    }

    public boolean isClosedForPickupAndDropoff() {
        return isInvalidForPickup() && isInvalidForDropoff();
    }

    public boolean isInvalidForPickup() {
        return mPickupValidity != null && mPickupValidity.isLocationInvalid();
    }

    public String getLocationDetailsTitle() {
        if (getTranslatedLocationName() != null) {
            if (mAirportCode != null && !mAirportCode.isEmpty()) {
                return getNameWithAirportCode().toString();
            } else {
                return getTranslatedLocationName();
            }
        } else {
            return mCity;
        }
    }
}
