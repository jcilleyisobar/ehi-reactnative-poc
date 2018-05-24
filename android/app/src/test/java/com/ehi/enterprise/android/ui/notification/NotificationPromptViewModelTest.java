package com.ehi.enterprise.android.ui.notification;

import com.ehi.enterprise.android.models.notification.EHINotification;
import com.ehi.enterprise.helpers.BaseViewModelTest;

import org.junit.Test;

import static org.mockito.Mockito.verify;

public class NotificationPromptViewModelTest extends BaseViewModelTest<NotificationPromptViewModel> {

    @Test
    public void testNotifyMeClicked() throws Exception {
        getViewModel().notifyMeClicked();

        verify(getMockedDelegate().getMockedSettingsManager().getMockedObject()).setPickupNotificationTime(EHINotification.NotificationTime.TWO_HOURS_BEFORE);
        verify(getMockedDelegate().getMockedSettingsManager().getMockedObject()).setReturnNotificationTime(EHINotification.NotificationTime.TWO_HOURS_BEFORE);
    }

    @Test
    public void testNotNowClicked() throws Exception {
        getViewModel().notNowClicked();

        verify(getMockedDelegate().getMockedSettingsManager().getMockedObject()).setPickupNotificationTime(EHINotification.NotificationTime.OFF);
        verify(getMockedDelegate().getMockedSettingsManager().getMockedObject()).setReturnNotificationTime(EHINotification.NotificationTime.OFF);
    }

    @Override
    protected Class<NotificationPromptViewModel> getViewModelClass() {
        return NotificationPromptViewModel.class;
    }
}