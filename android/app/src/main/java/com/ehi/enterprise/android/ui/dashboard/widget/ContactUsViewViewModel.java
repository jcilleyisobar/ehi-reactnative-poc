package com.ehi.enterprise.android.ui.dashboard.widget;

import com.ehi.enterprise.android.ui.viewmodel.ManagersAccessViewModel;
import com.ehi.enterprise.android.utils.reactor_extensions.viewstate.ReactorTextViewState;

import io.dwak.reactor.unbinder.annotation.AutoUnbindAll;

@AutoUnbindAll
public class ContactUsViewViewModel extends ManagersAccessViewModel {

	final ReactorTextViewState viewTitle = new ReactorTextViewState();
	final ReactorTextViewState viewSubTitle = new ReactorTextViewState();

	public void setViewTitle(final String title) {
		viewTitle.setText(title);
	}

	public void setViewSubtitle(final String subtitle) {
		viewSubTitle.setText(subtitle);
	}

}