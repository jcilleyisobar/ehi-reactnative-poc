package com.ehi.enterprise.android.ui.support;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.models.profile.EHIPhone;
import com.ehi.enterprise.android.ui.viewmodel.ManagersAccessViewModel;

import java.util.List;

import io.dwak.reactor.ReactorVar;
import io.dwak.reactor.unbinder.annotation.AutoUnbindAll;

@AutoUnbindAll
public class CustomerSupportViewModel extends ManagersAccessViewModel {

	final ReactorVar<Integer> title = new ReactorVar<>();

	@Override
	public void onAttachToView() {
		super.onAttachToView();
		title.setValue(R.string.customer_support_navigation_title);
	}

	public List<EHIPhone> getConfigSupportPhoneNumbers() {
		return getManagers().getSupportInfoManager().getSupportInfoForCurrentLocale().getSupportPhoneNumbers();
	}

	public String getSendUsAMessageUrl() {
		return getManagers().getSupportInfoManager().getSupportInfoForCurrentLocale().getSupportSendMessageUrl();
	}

	public String getSearchAnswersUrl() {
		return getManagers().getSupportInfoManager().getSupportInfoForCurrentLocale().getSupportAnswerUrl();
	}

}