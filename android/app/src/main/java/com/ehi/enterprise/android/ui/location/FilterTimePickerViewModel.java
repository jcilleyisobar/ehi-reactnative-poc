package com.ehi.enterprise.android.ui.location;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.ui.viewmodel.ManagersAccessViewModel;

import java.util.Date;

import io.dwak.reactor.ReactorVar;
import io.dwak.reactor.unbinder.annotation.AutoUnbindAll;

@AutoUnbindAll
public class FilterTimePickerViewModel extends ManagersAccessViewModel {

    private final ReactorVar<Date> timeDate = new ReactorVar<>();
    public ReactorVar<String> titleText = new ReactorVar<>();
    public ReactorVar<String> headerText = new ReactorVar<>();
    public ReactorVar<String> subHeaderText = new ReactorVar<>();
    private boolean mIsPickup;

    @Override
    public void onAttachToView() {
        super.onAttachToView();
        if (mIsPickup) {
            titleText.setValue(getResources().getString(R.string.time_select_pickup_title));
            headerText.setValue(getResources().getString(R.string.locations_map_closed_pickup));
            subHeaderText.setValue(getResources().getString(R.string.time_select_pickup_section_title));
        } else {
            titleText.setValue(getResources().getString(R.string.time_select_return_title));
            headerText.setValue(getResources().getString(R.string.locations_map_closed_return));
            subHeaderText.setValue(getResources().getString(R.string.time_select_return_section_title));
        }
    }

    public void selectTime(Date time) {
        timeDate.setValue(time);
    }

    public Date getTime() {
        return timeDate.getValue();
    }

    public void setIsPickup(Boolean isPickup) {
        this.mIsPickup = isPickup;
    }
}
