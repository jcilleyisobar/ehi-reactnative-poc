package com.ehi.enterprise.android.ui.notification;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.models.notification.EHINotification;
import com.ehi.enterprise.android.ui.viewmodel.ManagersAccessViewModel;

import io.dwak.reactor.ReactorVar;
import io.dwak.reactor.unbinder.annotation.AutoUnbindAll;

@AutoUnbindAll
public class NotificationPromptViewModel extends ManagersAccessViewModel{
    //region ReactorVars
    final ReactorVar<Integer> title = new ReactorVar<>();
    //endregion

    @Override
    public void onAttachToView() {
        super.onAttachToView();
        title.setValue(R.string.notification_prompt_title);
    }

    public void notifyMeClicked() {
        getManagers().getSettingsManager().setPickupNotificationTime(EHINotification.NotificationTime.TWO_HOURS_BEFORE);
        getManagers().getSettingsManager().setReturnNotificationTime(EHINotification.NotificationTime.TWO_HOURS_BEFORE);
    }

    public void notNowClicked() {
        getManagers().getSettingsManager().setPickupNotificationTime(EHINotification.NotificationTime.OFF);
        getManagers().getSettingsManager().setReturnNotificationTime(EHINotification.NotificationTime.OFF);
    }
}
