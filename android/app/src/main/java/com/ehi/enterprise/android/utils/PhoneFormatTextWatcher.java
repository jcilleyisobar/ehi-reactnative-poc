package com.ehi.enterprise.android.utils;

import android.text.Editable;
import android.text.TextWatcher;

import com.ehi.enterprise.android.utils.manager.LocalDataManager;
import com.google.i18n.phonenumbers.AsYouTypeFormatter;
import com.google.i18n.phonenumbers.PhoneNumberUtil;

public abstract class PhoneFormatTextWatcher implements TextWatcher {

	private static final String TAG = PhoneFormatTextWatcher.class.getSimpleName();

	private boolean isFormatting = false;

	private PhoneNumberUtil mNumberUtils;
	private AsYouTypeFormatter mNumberFormatter;

	public PhoneFormatTextWatcher() {
		mNumberUtils = PhoneNumberUtil.getInstance();
		mNumberFormatter = mNumberUtils.getAsYouTypeFormatter(LocalDataManager.getInstance().getPreferredCountryCode());
	}

	@Override
	public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

	}

	@Override
	public void afterTextChanged(Editable editable) {

	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		if (!isFormatting) {
			isFormatting = true;
			try {
				mNumberFormatter.clear();
				String formatted = "";
				StringBuilder digitsOnly = new StringBuilder();
				for (int i = 0; i < s.length(); i++) {
					if (!Character.isDigit(s.charAt(i))) {
						continue;
					}
					digitsOnly.append(s.charAt(i));
					formatted = mNumberFormatter.inputDigit(s.charAt(i));
				}
				onPhoneNumberFormatted(formatted, digitsOnly.toString());
			} catch (Exception e) {
				DLog.e(TAG, e);
			}
			isFormatting = false;
		}
	}

	public abstract void onPhoneNumberFormatted(String formattedNumber, String digitOnlyNumber);

}
