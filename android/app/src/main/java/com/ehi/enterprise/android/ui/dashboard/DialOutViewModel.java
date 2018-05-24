package com.ehi.enterprise.android.ui.dashboard;

import android.view.View;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.models.profile.EHIPhone;
import com.ehi.enterprise.android.ui.viewmodel.ManagersAccessViewModel;
import com.ehi.enterprise.android.utils.reactor_extensions.viewstate.ReactorTextViewState;
import com.ehi.enterprise.android.utils.reactor_extensions.viewstate.ReactorViewState;

import io.dwak.reactor.ReactorVar;
import io.dwak.reactor.unbinder.annotation.AutoUnbindAll;

@AutoUnbindAll
public class DialOutViewModel extends ManagersAccessViewModel {

	final ReactorVar<String> title = new ReactorVar<>();
	final ReactorViewState roadSideAssistanceView = new ReactorViewState();
	final ReactorTextViewState supportTextView = new ReactorTextViewState();

	private boolean hasCurrentRental;

	@Override
	public void onAttachToView() {
		super.onAttachToView();
		if(hasCurrentRental) {
			setTitle(getResources().getString(R.string.call_support_modal_title));
			roadSideAssistanceView.setVisibility(View.VISIBLE);
			supportTextView.setVisibility(View.GONE);
		}
		else {
			setTitle(getResources().getString(R.string.dashboard_call_support_title));
			supportTextView.setVisibility(View.VISIBLE);
		}
	}

	public void setHasCurrentRental(boolean hasCurrentRental) {
		this.hasCurrentRental = hasCurrentRental;
	}

	public void setTitle(String title) {
		this.title.setValue(title);
	}

	public boolean hasCurrentRental() {
		return hasCurrentRental;
	}

	public String getSupportPhoneNumber(EHIPhone.PhoneType type) {
		return getManagers().getSupportInfoManager().getSupportInfoForCurrentLocale().getSupportPhoneNumber(type);
	}

}