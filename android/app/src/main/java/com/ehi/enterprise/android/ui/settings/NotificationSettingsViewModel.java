package com.ehi.enterprise.android.ui.settings;

import android.support.annotation.IdRes;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.models.notification.EHINotification;
import com.ehi.enterprise.android.ui.viewmodel.ManagersAccessViewModel;
import com.ehi.enterprise.android.utils.reactor_extensions.viewstate.ReactorPropertyChangedListener;
import com.ehi.enterprise.android.utils.reactor_extensions.viewstate.ReactorRadioGroupState;

import io.dwak.reactor.ReactorVar;
import io.dwak.reactor.unbinder.annotation.AutoUnbindAll;

@AutoUnbindAll
public class NotificationSettingsViewModel extends ManagersAccessViewModel{
    final ReactorRadioGroupState radioGroup = new ReactorRadioGroupState();
    final ReactorVar<Integer> title = new ReactorVar<>();
    final ReactorVar<Boolean> finish = new ReactorVar<>(false);
    private boolean mCanFinish = false;
    private Boolean mIsPickup;

    @Override
    public void onAttachToView() {
        super.onAttachToView();

        @IdRes int idToCheck = 0;
        switch (getNotificationTime()) {
            case OFF:
                idToCheck = R.id.do_not_notify;
                break;
            case THIRTY_MINUTES_BEFORE:
                idToCheck = R.id.thirty_minutes;
                break;
            case TWO_HOURS_BEFORE:
                idToCheck = R.id.two_hour;
                break;
            case TWENTY_FOUR_HOURS_BEFORE:
                idToCheck = R.id.one_day;
                break;
        }
        radioGroup.setCheckedId(idToCheck);

        radioGroup.setCheckedIdChangedListener(new ReactorPropertyChangedListener<Integer>() {
            @Override
            public void onPropertyChanged(final Integer newValue) {
                    switch (newValue) {
                        case R.id.do_not_notify:
                            setNotificationTime(EHINotification.NotificationTime.OFF);
                            break;
                        case R.id.thirty_minutes:
                            setNotificationTime(EHINotification.NotificationTime.THIRTY_MINUTES_BEFORE);
                            break;
                        case R.id.two_hour:
                            setNotificationTime(EHINotification.NotificationTime.TWO_HOURS_BEFORE);
                            break;
                        case R.id.one_day:
                            setNotificationTime(EHINotification.NotificationTime.TWENTY_FOUR_HOURS_BEFORE);
                            break;
                    }

            }
        });
    }

    public void setNotificationTime(EHINotification.NotificationTime notificationTime){
        if(mIsPickup){
            getManagers().getSettingsManager().setPickupNotificationTime(notificationTime);
        }
        else {
            getManagers().getSettingsManager().setReturnNotificationTime(notificationTime);
        }

        if(mCanFinish) {
            finish.setValue(true);
        }

        getManagers().getLocalDataManager().setShouldShowGeofenceNotificationIntro(false);
    }

    public void setIsPickup(final Boolean isPickup) {
        mIsPickup = isPickup;
        title.setValue(mIsPickup ? R.string.notification_setting_title_pickup : R.string.notification_setting_title_return);
    }

    public boolean isPickup() {
        return mIsPickup;
    }

    public EHINotification.NotificationTime getNotificationTime() {
        return mIsPickup ? getPickupNotificationTime() : getReturnNotificationTime();
    }

    public void setCanFinish(final boolean canFinish) {
        mCanFinish = canFinish;
    }
}
