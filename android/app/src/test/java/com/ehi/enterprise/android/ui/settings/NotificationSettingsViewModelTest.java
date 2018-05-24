package com.ehi.enterprise.android.ui.settings;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.models.notification.EHINotification;
import com.ehi.enterprise.helpers.BaseViewModelTest;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

public class NotificationSettingsViewModelTest extends BaseViewModelTest<NotificationSettingsViewModel> {

    @Test
    public void testDoNotNotifyPickup() throws Exception {
        testNotification(EHINotification.NotificationTime.OFF, true, R.id.do_not_notify);
    }

    @Test
    public void testDoNotNotifyReturn() throws Exception {
        testNotification(EHINotification.NotificationTime.OFF, false, R.id.do_not_notify);
    }

    @Test
    public void testThirtyMinutesPickup() throws Exception {
        testNotification(EHINotification.NotificationTime.THIRTY_MINUTES_BEFORE, true, R.id.thirty_minutes);
    }

    @Test
    public void testThirtyMinutesReturn() throws Exception {
        testNotification(EHINotification.NotificationTime.THIRTY_MINUTES_BEFORE, false, R.id.thirty_minutes);
    }

    @Test
    public void testTwoHourPickup() throws Exception {
        testNotification(EHINotification.NotificationTime.TWO_HOURS_BEFORE, true, R.id.two_hour);
    }

    @Test
    public void testTwoHourReturn() throws Exception {
        testNotification(EHINotification.NotificationTime.TWO_HOURS_BEFORE, false, R.id.two_hour);
    }

    @Test
    public void testOneDayPickup() throws Exception {
        testNotification(EHINotification.NotificationTime.TWENTY_FOUR_HOURS_BEFORE, true, R.id.one_day);
    }

    @Test
    public void testOneDayReturn() throws Exception {
        testNotification(EHINotification.NotificationTime.TWENTY_FOUR_HOURS_BEFORE, false, R.id.one_day);
    }

    @Test
    public void testSetDoNotNotifyPickup() throws Exception {
        testSetNotification(true, R.id.do_not_notify, EHINotification.NotificationTime.OFF);
    }

    @Test
    public void testSetDoNotNotifyReturn() throws Exception {
        testSetNotification(false, R.id.do_not_notify, EHINotification.NotificationTime.OFF);
    }

    @Test
    public void testSetThirtyMinutesPickup() throws Exception {
        testSetNotification(true, R.id.thirty_minutes, EHINotification.NotificationTime.THIRTY_MINUTES_BEFORE);
    }

    @Test
    public void testSetThirtyMinutesReturn() throws Exception {
        testSetNotification(false, R.id.thirty_minutes, EHINotification.NotificationTime.THIRTY_MINUTES_BEFORE);
    }

    @Test
    public void testSetTwoHoursPickup() throws Exception {
        testSetNotification(true, R.id.two_hour, EHINotification.NotificationTime.TWO_HOURS_BEFORE);
    }

    @Test
    public void testSetTwoHoursReturn() throws Exception {
        testSetNotification(false, R.id.two_hour, EHINotification.NotificationTime.TWO_HOURS_BEFORE);
    }

    @Test
    public void testSetOneDayPickup() throws Exception {
        testSetNotification(true, R.id.one_day, EHINotification.NotificationTime.TWENTY_FOUR_HOURS_BEFORE);
    }

    @Test
    public void testSetOneDayReturn() throws Exception {
        testSetNotification(false, R.id.one_day, EHINotification.NotificationTime.TWENTY_FOUR_HOURS_BEFORE);
    }

    private void testSetNotification(boolean isPickup, int checkedId, EHINotification.NotificationTime expectedNotificationTime ){
        if(isPickup){
            doReturn(EHINotification.NotificationTime.OFF).when(getMockedDelegate().getMockedSettingsManager().getMockedObject()).getPickupNotificationTime();
        }
        else {
            doReturn(EHINotification.NotificationTime.OFF).when(getMockedDelegate().getMockedSettingsManager().getMockedObject()).getReturnNotificationTime();
        }

        getViewModel().setIsPickup(isPickup);
        getViewModel().onAttachToView();
        getViewModel().setCanFinish(true);
        getViewModel().radioGroup.setCheckedId(checkedId);

        if(isPickup){
            verify(getMockedDelegate().getMockedSettingsManager().getMockedObject()).setPickupNotificationTime(expectedNotificationTime);
        }
        else {
            verify(getMockedDelegate().getMockedSettingsManager().getMockedObject()).setReturnNotificationTime(expectedNotificationTime);
        }

        assertTrue(getViewModel().finish.getRawValue().booleanValue());
    }

    private void testNotification(EHINotification.NotificationTime notificationTime, boolean isPickup, int expectedId){
        if(isPickup) {
            doReturn(notificationTime)
                    .when(getMockedDelegate().getMockedSettingsManager().getMockedObject()).getPickupNotificationTime();
        }
        else {
            doReturn(notificationTime)
                    .when(getMockedDelegate().getMockedSettingsManager().getMockedObject()).getReturnNotificationTime();
        }
        getViewModel().setIsPickup(isPickup);
        getViewModel().onAttachToView();

        assertEquals(isPickup ? R.string.notification_setting_title_pickup : R.string.notification_setting_title_return, getViewModel().title.getRawValue().intValue());
        assertEquals(expectedId, getViewModel().radioGroup.checkedId().getRawValue().intValue());

    }

    @Override
    protected Class<NotificationSettingsViewModel> getViewModelClass() {
        return NotificationSettingsViewModel.class;
    }
}