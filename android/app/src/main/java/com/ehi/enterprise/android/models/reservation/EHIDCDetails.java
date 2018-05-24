package com.ehi.enterprise.android.models.reservation;

import com.ehi.enterprise.android.models.EHIModel;
import com.ehi.enterprise.android.models.profile.EHIAddressProfile;
import com.ehi.enterprise.android.models.profile.EHIPhone;
import com.ehi.enterprise.android.utils.EHIPhoneNumberUtils;
import com.ehi.enterprise.android.utils.manager.LocalDataManager;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class EHIDCDetails extends EHIModel {

    public static final String TAG = "EHIDCDetails";

    @SerializedName("address")
    private EHIAddressProfile mAddress;

    @SerializedName("phone")
    private EHIPhone mPhone;

    @SerializedName("comments")
    private String mComment;

    public EHIDCDetails() {
    }

    public EHIDCDetails(EHIAddressProfile address, EHIPhone phone, String comment) {
        mAddress = address;
        mPhone = phone;
        mComment = comment;
    }

    public EHIDCDetails(String street, String city, String zip, String phone, String comment) {
        this.mAddress = new EHIAddressProfile(street, city, zip);
        this.mAddress.setAddressType(EHIAddressProfile.TYPE_HOME);
        this.mPhone = new EHIPhone(phone, EHIPhone.PhoneType.HOME.getValue());
        this.mComment = comment;
    }

    public EHIDCDetails(List<String> streets, String city, String zip, String phone, String comment) {
        this.mAddress = new EHIAddressProfile(streets, city, zip);
        this.mAddress.setAddressType(EHIAddressProfile.TYPE_HOME);
        this.mPhone = new EHIPhone(phone, EHIPhone.PhoneType.HOME.getValue());
        this.mComment = comment;
    }

    public EHIAddressProfile getAddress() {
        return mAddress;
    }

    public EHIPhone getPhone() {
        return mPhone;
    }

    public String getComment() {
        return mComment;
    }

    public String getFormattedPhoneNumber(boolean withFormatting) {
        if (mPhone != null
                && mPhone.getPhoneNumber() != null) {
            return EHIPhoneNumberUtils.formatNumberForMobileDialing(mPhone.getPhoneNumber(),
                    LocalDataManager.getInstance().getPreferredCountryCode(),
                    withFormatting);
        } else {
            return "";
        }

    }

    //TODO this method was modified we're not checking comment
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EHIDCDetails)) return false;

        EHIDCDetails that = (EHIDCDetails) o;

        if (mAddress != null ? !mAddress.equals(that.mAddress) : that.mAddress != null)
            return false;
        return !(mPhone != null ? !mPhone.equals(that.mPhone) : that.mPhone != null);

    }

    @Override
    public int hashCode() {
        int result = mAddress != null ? mAddress.hashCode() : 0;
        result = 31 * result + (mPhone != null ? mPhone.hashCode() : 0);
        result = 31 * result + (mComment != null ? mComment.hashCode() : 0);
        return result;
    }
}
