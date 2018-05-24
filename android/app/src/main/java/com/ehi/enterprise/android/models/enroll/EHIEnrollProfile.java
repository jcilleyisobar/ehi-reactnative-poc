package com.ehi.enterprise.android.models.enroll;

import android.text.TextUtils;

import com.ehi.enterprise.android.models.EHIModel;
import com.ehi.enterprise.android.models.profile.EHIAddressProfile;
import com.ehi.enterprise.android.models.profile.EHILicenseProfile;
import com.ehi.enterprise.android.models.profile.EHIPhone;
import com.ehi.enterprise.android.models.profile.EHIPreference;
import com.ehi.enterprise.android.utils.DLog;
import com.google.gson.annotations.SerializedName;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class EHIEnrollProfile extends EHIModel {

    public static final String TAG = "EHIEnrollProfile";

    private static final SimpleDateFormat sDateFormatter = new SimpleDateFormat("yyyy-MM-dd");

    @SerializedName("first_name")
    private String mFirstName;

    @SerializedName("last_name")
    private String mLastName;

    @SerializedName("email")
    private String mEmail;

    @SerializedName("date_of_birth")
    private String mDayOfBirth;

    @SerializedName("password")
    private String mPassword;

    @SerializedName("individual_id")
    private String mIndividualId;

    @SerializedName("phones")
    private List<EHIPhone> mPhoneNumberList;

    @SerializedName("address")
    private EHIAddressProfile mEhiAddressProfile;

    @SerializedName("drivers_license")
    private EHILicenseProfile mEhiLicenseProfile;

    @SerializedName("terms_and_conditions")
    private EHITermsAndConditions mTermsAndConditionsAccept;

    @SerializedName("preference")
    private EHIPreference mPreference;

    public String getFirstName() {
        return mFirstName;
    }

    public void setFirstName(String firstName) {
        mFirstName = firstName;
    }

    public String getLastName() {
        return mLastName;
    }

    public void setLastName(String lastName) {
        mLastName = lastName;
    }

    public String getEmail() {
        return mEmail;
    }

    public void setEmail(String email) {
        mEmail = email;
    }

    public EHIEnrollProfile(){}

    protected EHIEnrollProfile(EHIEnrollProfile profile) {
        setFirstName(profile.getFirstName());
        setLastName(profile.getLastName());
        setEmail(profile.getEmail());
        setDayOfBirth(profile.getDayOfBirth());
        setPassword(profile.getPassword());
        setPhoneNumberList(profile.getPhoneNumberList());
        setEhiAddressProfile(profile.getEhiAddressProfile());
        setEhiLicenseProfile(profile.getEhiLicenseProfile());
        setTermsAndConditionsAccept(profile.getTermsAndConditionsAccept());
        setPreference(profile.getPreference());
        setIndividualId(profile.getIndividualId());
    }

    public Date getDayOfBirth() {
        if (mDayOfBirth != null) {
            try {
                return sDateFormatter.parse(mDayOfBirth);
            } catch (ParseException e) {
                DLog.w(TAG, e);
                return null;
            }
        }
        return null;
    }

    public void setDayOfBirth(Date dayOfBirth) {
        if (dayOfBirth != null) {
            mDayOfBirth = sDateFormatter.format(dayOfBirth);
        } else {
            mDayOfBirth = null;
        }
    }

    public String getPassword() {
        return mPassword;
    }

    public void setPassword(String password) {
        mPassword = password;
    }

    public List<EHIPhone> getPhoneNumberList() {
        return mPhoneNumberList;
    }

    public void setPhoneNumberList(List<EHIPhone> phoneNumberList) {
        mPhoneNumberList = phoneNumberList;
    }

    public EHIAddressProfile getEhiAddressProfile() {
        return mEhiAddressProfile;
    }

    public void setEhiAddressProfile(EHIAddressProfile ehiAddressProfile) {
        mEhiAddressProfile = ehiAddressProfile;
    }

    public EHILicenseProfile getEhiLicenseProfile() {
        return mEhiLicenseProfile;
    }

    public void setEhiLicenseProfile(EHILicenseProfile ehiLicenseProfile) {
        mEhiLicenseProfile = ehiLicenseProfile;
    }

    public EHITermsAndConditions getTermsAndConditionsAccept() {
        return mTermsAndConditionsAccept;
    }

    public void setTermsAndConditionsAccept(EHITermsAndConditions termsAndConditionsAccept) {
        mTermsAndConditionsAccept = termsAndConditionsAccept;
    }

    public EHIPreference getPreference() {
        return mPreference;
    }

    public void setPreference(EHIPreference preference) {
        mPreference = preference;
    }

    public boolean isDriverFound() {
        return !TextUtils.isEmpty(mIndividualId);
    }

    public void setIndividualId(String value) {
        mIndividualId = value;
    }

    public String getIndividualId() {
        return mIndividualId;
    }
}
