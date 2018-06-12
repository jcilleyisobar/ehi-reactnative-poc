package com.ehi.enterprise.android.ui.support.interfaces;

import com.ehi.enterprise.android.models.profile.EHIPhone;

public interface OnSupportItemClickListener {

	void onCallSupportNumber(EHIPhone phoneNumber);
	void onMessageLinkOut(String url);
	void onSearchLinkOut(String url);

}