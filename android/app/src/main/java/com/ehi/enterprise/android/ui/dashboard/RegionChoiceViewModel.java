package com.ehi.enterprise.android.ui.dashboard;

import com.ehi.enterprise.android.ui.viewmodel.ManagersAccessViewModel;

import java.util.Locale;

import io.dwak.reactor.ReactorVar;
import io.dwak.reactor.unbinder.annotation.AutoUnbindAll;

@AutoUnbindAll
public class RegionChoiceViewModel extends ManagersAccessViewModel {

	final ReactorVar<String> preferredRegionName = new ReactorVar<>();

	@Override
	public void onAttachToView() {
		super.onAttachToView();
		preferredRegionName.setValue(generateRegionName());
	}

    private String generateRegionName() {
        return new Locale(Locale.getDefault().getLanguage(),
                          getManagers().getLocalDataManager().getPreferredCountryCode()).getDisplayCountry();

    }
}
