package com.ehi.enterprise.android.utils;

import android.text.TextUtils;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

public class EHIPhoneNumberUtils {

    public static final String TAG = "EHIPhoneNumberUtils";

    public static String formatNumberForMobileDialing(String phoneNumber, String countryCode, boolean withFormatting){
        String formattedPhoneNumber = "";
        PhoneNumberUtil phoneUtils = PhoneNumberUtil.getInstance();

        try {
            Phonenumber.PhoneNumber number = phoneUtils.parse(phoneNumber, countryCode);
            formattedPhoneNumber = phoneUtils.formatNumberForMobileDialing(number, countryCode, withFormatting);
        } catch (NullPointerException | NumberParseException e) {
            DLog.e(TAG, e);
        }

        if (TextUtils.isEmpty(formattedPhoneNumber) && phoneNumber != null) {
            formattedPhoneNumber = phoneNumber;
        }

        return formattedPhoneNumber;
    }

}
