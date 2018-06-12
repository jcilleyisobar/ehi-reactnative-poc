package com.ehi.enterprise.android.utils.manager;

import android.content.Context;
import android.support.annotation.NonNull;

import com.ehi.enterprise.android.models.profile.EHIPhone;
import com.ehi.enterprise.android.models.support.EHISupportInfo;
import com.ehi.enterprise.android.utils.EHIPhoneNumberUtils;

public class SupportInfoManager extends BaseDataManager {

    private static final String CONTACT_INFO_MANAGER_NAME = "CONTACT_INFO_MANAGER_NAME";

    private static SupportInfoManager sManager;

    protected SupportInfoManager() {
    }

    @Override
    protected String getSharedPreferencesName() {
        return CONTACT_INFO_MANAGER_NAME;
    }

    @NonNull
    public static SupportInfoManager getInstance() {
        if (sManager == null) {
            sManager = new SupportInfoManager();
        }
        return sManager;
    }

    @Override
    public void initialize(@NonNull Context context) {
        super.initialize(context);
        sManager = this;
    }

    public void saveSupportInfo(String countryCode, EHISupportInfo feed) {
        set(countryCode, feed);
    }

    public boolean hasSupportInfoForCountry(String countryCode) {
        return getSupportInfoForCountry(countryCode) != null;
    }

    public EHISupportInfo getSupportInfoForCountry(String countryCode) {
        return getEhiModel(countryCode, EHISupportInfo.class);
    }

    public EHISupportInfo getSupportInfoForCurrentLocale() {
        return getSupportInfoForCountry(LocalDataManager.getInstance().getPreferredCountryCode());
    }

    public String getContactUsPhoneNumberForCurrentLocale() {
        String countryCode = LocalDataManager.getInstance().getPreferredCountryCode();
        if (getSupportInfoForCountry(countryCode) != null) {
            String numberToCall = getSupportInfoForCountry(countryCode).getSupportPhoneNumber(EHIPhone.PhoneType.CONTACT_US);
            return EHIPhoneNumberUtils.formatNumberForMobileDialing(numberToCall, countryCode, true);
        }
        return "";
    }

    public String getDNRPhoneNumberForCurrentLocale() {
        String countryCode = LocalDataManager.getInstance().getPreferredCountryCode();
        String numberToCall = getSupportInfoForCountry(countryCode).getSupportPhoneNumber(EHIPhone.PhoneType.DNR);

        return EHIPhoneNumberUtils.formatNumberForMobileDialing(numberToCall, countryCode, true);
    }

}