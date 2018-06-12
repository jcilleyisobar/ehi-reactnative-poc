package com.ehi.enterprise.android.ui.reservation;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.models.reservation.EHIDriverInfo;
import com.ehi.enterprise.android.models.reservation.EHIReservation;
import com.ehi.enterprise.helpers.BaseViewModelTest;

import junit.framework.Assert;

import org.junit.Test;
import org.mockito.Mockito;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

public class DriverInfoViewModelTest extends BaseViewModelTest<DriverInfoViewModel> {

    @Test
    public void testOnAttachToViewNoDriverInfoLoggedOut() throws Exception {
        final String continueButtonText = "CONTINUE TO REVIEW";
        getMockedContext().getMockedResources().addAnswer("getString", continueButtonText);

        EHIReservation ehiReservation = Mockito.mock(EHIReservation.class);
        when(ehiReservation.shouldShowIdentityCheckWithExternalVendorMessage()).thenReturn(false);

        doReturn(ehiReservation).when(getMockedDelegate().getReservationManager()).getCurrentReservation();

        getViewModel().onAttachToView();

        Assert.assertEquals(R.string.reservation_driver_info_navigation_title, getViewModel().titleRes.getRawValue().intValue());
        Assert.assertEquals(continueButtonText, getViewModel().continueButton.text().getRawValue());

        getMockedDelegate().getMockedReservationManager().addAnswer("getLocalDriverInfo", null);
        getMockedDelegate().getMockedSettingsManager().addAnswer("isAutoSaveEnabled", false);

        Assert.assertFalse(getViewModel().signUpEmailCheckBox.checked().getRawValue());
        Assert.assertFalse(getViewModel().saveInformationCheckBox.checked().getRawValue());
        Assert.assertFalse(getViewModel().continueButton.enabled().getRawValue());

        getMockedDelegate().getMockedLoginManager().addAnswer("isUserLoggedIn", false);
        getMockedDelegate().getMockedReservationManager().addAnswer("getEmeraldClubAuthToken", null);
        assertGone(getViewModel().authFullName.visibility().getRawValue());
        assertVisible(getViewModel().firstNameLayout.visibility().getRawValue());
        assertVisible(getViewModel().lastNameLayout.visibility().getRawValue());
        assertVisible(getViewModel().saveInformationContainer.visibility().getRawValue());
        assertVisible(getViewModel().signUpEmailContainer.visibility().getRawValue());

        Assert.assertFalse(getViewModel().continueButton.enabled().getRawValue());
    }

    @Test
    public void testOnAttachToViewSavedDriverInfoLoggedOut() throws Exception {
        final String email = "email@domain.com";
        final String maskedEmail = "e***@domain.com";
        final String first = "first";
        final String last = "last";
        final String phoneNumber = "(312) 312-3123";
        final boolean requestEmailPromotions = false;
        EHIDriverInfo ehiDriverInfo = new EHIDriverInfo(email, maskedEmail, first, last, phoneNumber, requestEmailPromotions);
        doReturn(ehiDriverInfo).when(getMockedDelegate().getReservationManager()).getDriverInfo();

        EHIReservation ehiReservation = Mockito.mock(EHIReservation.class);
        when(ehiReservation.shouldShowIdentityCheckWithExternalVendorMessage()).thenReturn(false);

        doReturn(ehiReservation).when(getMockedDelegate().getReservationManager()).getCurrentReservation();

        getViewModel().onAttachToView();

        Assert.assertEquals(first, getViewModel().firstName.text().getRawValue());
        Assert.assertEquals(last, getViewModel().lastName.text().getRawValue());
        Assert.assertEquals(phoneNumber, getViewModel().phoneNumber.text().getRawValue());
        Assert.assertEquals(maskedEmail, getViewModel().emailAddress.text().getRawValue());
        Assert.assertEquals(requestEmailPromotions, getViewModel().signUpEmailCheckBox.checked().getRawValue().booleanValue());
        Assert.assertTrue(getViewModel().saveInformationCheckBox.checked().getRawValue());

    }

    @Test
    public void testOnAttachToViewEditingDriverInfoLoggedOut() throws Exception {
        final String email = "email@domain.com";
        final String maskedEmail = "e***@domain.com";
        final String first = "first";
        final String last = "last";
        final String phoneNumber = "(312) 312-3123";
        final boolean requestEmailPromotions = false;
        EHIDriverInfo ehiDriverInfo = new EHIDriverInfo(email, maskedEmail, first, last, phoneNumber, requestEmailPromotions);
        getViewModel().setDriverInfo(ehiDriverInfo);

        EHIReservation ehiReservation = Mockito.mock(EHIReservation.class);
        when(ehiReservation.shouldShowIdentityCheckWithExternalVendorMessage()).thenReturn(false);

        doReturn(ehiReservation).when(getMockedDelegate().getReservationManager()).getCurrentReservation();

        getViewModel().onAttachToView();

        Assert.assertEquals(first, getViewModel().firstName.text().getRawValue());
        Assert.assertEquals(last, getViewModel().lastName.text().getRawValue());
        Assert.assertEquals(phoneNumber, getViewModel().phoneNumber.text().getRawValue());
        Assert.assertEquals(maskedEmail, getViewModel().emailAddress.text().getRawValue());
        Assert.assertEquals(requestEmailPromotions, getViewModel().signUpEmailCheckBox.checked().getRawValue().booleanValue());
        Assert.assertFalse(getViewModel().saveInformationCheckBox.checked().getRawValue());
    }

    @Test
    public void testInputDriverInfo() throws Exception {
        Assert.assertFalse(getViewModel().continueButton.enabled().getRawValue());

        final String firstName = "first";
        final String lastName = "last";
        final String phoneNumber = "(312) 312-3123";
        getViewModel().firstName.setText(firstName);
        Assert.assertFalse(getViewModel().continueButton.enabled().getRawValue());

        getViewModel().lastName.setText(lastName);
        Assert.assertFalse(getViewModel().continueButton.enabled().getRawValue());

        getViewModel().phoneNumber.setText(phoneNumber);
        Assert.assertFalse(getViewModel().continueButton.enabled().getRawValue());

        getViewModel().emailAddress.setText("email");
        Assert.assertFalse(getViewModel().continueButton.enabled().getRawValue());
        Assert.assertEquals(R.drawable.edit_text_red_border, getViewModel().emailAddress.backgroundResource().getRawValue().intValue());

        getViewModel().emailAddress.setText("email@email.com");
        Assert.assertTrue(getViewModel().continueButton.enabled().getRawValue());
        Assert.assertEquals(R.drawable.edit_text_transparent_dark_border, getViewModel().emailAddress.backgroundResource().getRawValue().intValue());
    }

    @Test
    public void loggedUserShouldNotSeeLoginView() {
        doReturn(true).when(getMockedDelegate().getLoginManager()).isLoggedIn();
        getViewModel().onAttachToView();
        assertGone(getViewModel().signinLayout);
    }

    @Test
    public void guestUserShouldSeeLoginView() {
        getViewModel().onAttachToView();
        assertVisible(getViewModel().signinLayout);
    }

    @Test
    public void guestUserOnModifyShouldNotSeeLoginView() {
        getViewModel().setIsModify(true);
        getViewModel().onAttachToView();
        assertGone(getViewModel().signinLayout);
    }

    @Override
    protected Class<DriverInfoViewModel> getViewModelClass() {
        return DriverInfoViewModel.class;
    }
}