package com.ehi.enterprise.android.models.profile;

import android.support.annotation.StringDef;
import android.text.TextUtils;

import com.ehi.enterprise.android.models.EHIModel;
import com.ehi.enterprise.android.utils.manager.LocalDataManager;
import com.google.gson.annotations.SerializedName;

import java.util.LinkedList;
import java.util.List;

public class EHIAddressProfile extends EHIModel {

    private static final int ADDRESS_LINES_COUNT = 2;

    @StringDef({TYPE_HOME})
    public @interface AddressType {
    }

    public static final String TYPE_HOME = "HOME";

    @SerializedName("street_addresses")
    private List<String> mStreetAddresses;

    @SerializedName("city")
    private String mCity;

    @SerializedName("country_subdivision_code")
    private String mCountrySubdivisionCode;

    @SerializedName("country_subdivision_name")
    private String mCountrySubdivisionName;

    @SerializedName("country_code")
    private String mCountryCode;

    @SerializedName("country_name")
    private String mCountryName;

    @SerializedName("postal")
    private String mPostal;

    @AddressType
    @SerializedName("address_type")
    private String mAddressType;

    public EHIAddressProfile() {
    }

    public EHIAddressProfile(String street, String city, String zip) {
        this.mStreetAddresses = new LinkedList<>();
        if (!TextUtils.isEmpty(street)) {
            this.mStreetAddresses.add(street);
        }
        this.mCity = city;
        this.mPostal = zip;
    }

    public EHIAddressProfile(List<String> streets, String city, String zip) {
        this.mStreetAddresses = new LinkedList<>(streets);
        this.mCity = city;
        this.mPostal = zip;
    }

    public List<String> getStreetAddresses() {
        return mStreetAddresses;
    }

    public String getCity() {
        return mCity;
    }

    public String getCountrySubdivisionCode() {
        return mCountrySubdivisionCode;
    }

    public String getCountrySubdivisionName() {
        return mCountrySubdivisionName;
    }

    public String getCountryCode() {
        return mCountryCode;
    }

    public String getCountryName() {
        return mCountryName;
    }

    public String getPostal() {
        return mPostal;
    }

    @AddressType
    public String getAddressType() {
        return mAddressType;
    }

    public String getReadableAddress() {
        boolean isEuropeanAddress = LocalDataManager.getInstance().isEuropeanAddress(mCountryCode);
        StringBuilder bld = new StringBuilder();
        if (mStreetAddresses != null) {
            for (String address : mStreetAddresses) {
                if (bld.length() > 0) {
                    bld.append(", ");
                }
                bld.append(address);
            }
        }
        bld.append("\n");
        if (isEuropeanAddress) {
            if (mPostal != null) {
                bld.append(mPostal);
                bld.append(" ");
            }
        }
        if (mCity != null) {
            bld.append(mCity);
        }
        if (mCountrySubdivisionCode != null) {
            bld.append(", ");
            bld.append(mCountrySubdivisionCode);
        }
        if (!isEuropeanAddress) {
            if (mPostal != null) {
                bld.append(" ");
                bld.append(mPostal);
            }
        }
        return bld.toString();
    }

    public String getAddressForCalendar() {
        boolean isEuropeanAddress = LocalDataManager.getInstance().isEuropeanAddress(mCountryCode);
        StringBuilder bld = new StringBuilder();
        if (mStreetAddresses != null) {
            for (String address : mStreetAddresses) {
                if (bld.length() > 0) {
                    bld.append(", ");
                }
                bld.append(address);
            }
        }
        if (isEuropeanAddress) {
            if (mPostal != null) {
                bld.append(mPostal);
                bld.append(" ");
            }
        }
        if (mCity != null) {
            bld.append(" ");
            bld.append(mCity);
        }
        if (mCountrySubdivisionCode != null) {
            bld.append(", ");
            bld.append(mCountrySubdivisionCode);
        }
        if (!isEuropeanAddress) {
            if (mPostal != null) {
                bld.append(" ");
                bld.append(mPostal);
            }
        }
        return bld.toString();

    }

    public void setStreetAddresses(List<String> streetAddresses) {
        mStreetAddresses = streetAddresses;
    }

    public void setCity(String city) {
        mCity = city;
    }

    public void setCountrySubdivisionCode(String countrySubdivisionCode) {
        mCountrySubdivisionCode = countrySubdivisionCode;
    }

    public void setCountrySubdivisionName(String countrySubdivisionName) {
        mCountrySubdivisionName = countrySubdivisionName;
    }

    public void setCountryCode(String countryCode) {
        mCountryCode = countryCode;
    }

    public void setCountryName(String countryName) {
        mCountryName = countryName;
    }

    public void setPostal(String postal) {
        mPostal = postal;
    }

    public void setAddressType(@AddressType String addressType) {
        mAddressType = addressType;
    }

    public void setStreetAddress(int position, String address) {
        if (mStreetAddresses == null) {
            mStreetAddresses = new LinkedList<>();
        }
        if (mStreetAddresses.size() < ADDRESS_LINES_COUNT) {
            for (int i = mStreetAddresses.size(); i < ADDRESS_LINES_COUNT; i++) {
                mStreetAddresses.add("");
            }
        }
        mStreetAddresses.set(position, address);
    }

    public EHIRegion getCountrySubdivisionRegion() {
        return new EHIRegion(mCountrySubdivisionName, mCountrySubdivisionCode);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EHIAddressProfile)) return false;

        EHIAddressProfile that = (EHIAddressProfile) o;

        if (mStreetAddresses != null ? !mStreetAddresses.equals(that.mStreetAddresses) : that.mStreetAddresses != null)
            return false;
        if (mCity != null ? !mCity.equals(that.mCity) : that.mCity != null) return false;
        if (mCountrySubdivisionCode != null ? !mCountrySubdivisionCode.equals(that.mCountrySubdivisionCode) : that.mCountrySubdivisionCode != null)
            return false;
        if (mCountrySubdivisionName != null ? !mCountrySubdivisionName.equals(that.mCountrySubdivisionName) : that.mCountrySubdivisionName != null)
            return false;
        if (mCountryCode != null ? !mCountryCode.equals(that.mCountryCode) : that.mCountryCode != null)
            return false;
        if (mCountryName != null ? !mCountryName.equals(that.mCountryName) : that.mCountryName != null)
            return false;
        return !(mPostal != null ? !mPostal.equals(that.mPostal) : that.mPostal != null) && !(mAddressType != null ? !mAddressType.equals(that.mAddressType) : that.mAddressType != null);

    }

    @Override
    public int hashCode() {
        int result = mStreetAddresses != null ? mStreetAddresses.hashCode() : 0;
        result = 31 * result + (mCity != null ? mCity.hashCode() : 0);
        result = 31 * result + (mCountrySubdivisionCode != null ? mCountrySubdivisionCode.hashCode() : 0);
        result = 31 * result + (mCountrySubdivisionName != null ? mCountrySubdivisionName.hashCode() : 0);
        result = 31 * result + (mCountryCode != null ? mCountryCode.hashCode() : 0);
        result = 31 * result + (mCountryName != null ? mCountryName.hashCode() : 0);
        result = 31 * result + (mPostal != null ? mPostal.hashCode() : 0);
        result = 31 * result + (mAddressType != null ? mAddressType.hashCode() : 0);
        return result;
    }
}
