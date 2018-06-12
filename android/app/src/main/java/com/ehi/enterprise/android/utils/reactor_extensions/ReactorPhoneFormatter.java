package com.ehi.enterprise.android.utils.reactor_extensions;

import android.support.v4.util.Pair;

import com.ehi.enterprise.android.utils.manager.LocalDataManager;
import com.google.i18n.phonenumbers.AsYouTypeFormatter;
import com.google.i18n.phonenumbers.PhoneNumberUtil;

import io.dwak.reactor.ReactorVar;

public class ReactorPhoneFormatter {
    private AsYouTypeFormatter mAsYouTypeFormatter;
    private boolean mIsFormatting;

    public ReactorPhoneFormatter() {
        mAsYouTypeFormatter = PhoneNumberUtil.getInstance().getAsYouTypeFormatter(LocalDataManager.getInstance().getPreferredCountryCode());
    }

    public Pair<String, String> format(ReactorVar<String> unformatted) {
        Pair<String, String> formattedPair = null;
        if (!mIsFormatting) {
            mIsFormatting = true;
            try {
                mAsYouTypeFormatter.clear();
                String formatted = "";
                StringBuilder digitsOnly = new StringBuilder();
                for (int i = 0; i < unformatted.getRawValue().length(); i++) {
                    if (!Character.isDigit(unformatted.getRawValue().charAt(i))) {
                        continue;
                    }
                    digitsOnly.append(unformatted.getRawValue().charAt(i));
                    formatted = mAsYouTypeFormatter.inputDigit(unformatted.getRawValue().charAt(i));
                }
                formattedPair = new Pair<>(formatted, digitsOnly.toString());
                unformatted.setValue(formatted);
            } catch (Exception ignored) {
            }
            mIsFormatting = false;
        }

        return formattedPair;
    }
}
