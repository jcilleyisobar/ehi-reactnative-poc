package com.ehi.enterprise.android.ui.reservation.widget;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.models.location.EHILocation;
import com.ehi.enterprise.android.utils.reactor_extensions.viewstate.ReactorViewState;
import com.ehi.enterprise.helpers.BaseViewModelTest;

import junit.framework.Assert;

import org.junit.Test;
import org.mockito.Mockito;

public class ReviewLocationsViewModelTest extends BaseViewModelTest<ReviewLocationsViewModel> {

    @Test
    public void testSetLocations() throws Exception {
        EHILocation mockPickupLocation = Mockito.spy(EHILocation.class);
        EHILocation mockReturnLocation = Mockito.spy(EHILocation.class);

        String pickupLocationId = "PICKUP";
        String returnLocationId = "RETURN";

        String pickupLocationName = "PICKUP NAME";
        String returnLocationName = "RETURN NAME";

        Mockito.when(mockPickupLocation.getId()).thenReturn(pickupLocationId);
        Mockito.when(mockReturnLocation.getId()).thenReturn(returnLocationId);
        getMockedDelegate().getMockedLocationManager().addAnswer("isFavoriteLocation", true);
        getMockedDelegate().getMockedLocationManager().addAnswer("isFavoriteLocation", true);
        Mockito.doReturn(-1).when(mockPickupLocation).getGrayLocationCellIconDrawable();
        Mockito.doReturn(-1).when(mockReturnLocation).getGrayLocationCellIconDrawable();

        getViewModel().setLocations(mockPickupLocation,
                                    mockReturnLocation, false);
        Assert.assertEquals(ReactorViewState.GONE, getViewModel().pickupLocationIcon.visibility().getRawValue().intValue());

        Mockito.doReturn(R.drawable.icon_airport_gray).when(mockPickupLocation).getGrayLocationCellIconDrawable();
        getViewModel().setLocations(mockPickupLocation,
                                    mockReturnLocation, false);
        Assert.assertEquals(R.drawable.icon_airport_gray, getViewModel().pickupLocationIcon.imageResource().getRawValue().intValue());
        Assert.assertEquals(ReactorViewState.VISIBLE,
                            getViewModel().pickupLocationIcon.visibility().getRawValue().intValue());

        Mockito.when(mockPickupLocation.getName()).thenReturn(pickupLocationName);
        getViewModel().setLocations(mockPickupLocation, mockReturnLocation, false);
        Assert.assertEquals(pickupLocationName, getViewModel().pickupLocationName.text().getRawValue());

        pickupLocationId = "ID";
        returnLocationId= "ID";
        Mockito.when(mockPickupLocation.getId()).thenReturn(pickupLocationId);
        Mockito.when(mockReturnLocation.getId()).thenReturn(returnLocationId);
        getViewModel().setLocations(mockPickupLocation, mockReturnLocation, false);
        Assert.assertEquals(ReactorViewState.GONE, getViewModel().viewSeparator.visibility().getRawValue().intValue());
        Assert.assertEquals(ReactorViewState.GONE, getViewModel().returnLocationContainer.visibility().getRawValue().intValue());
        Assert.assertEquals(R.string.reservation_review_location_section_title, getViewModel().locationsTitle.textRes().getRawValue().intValue());

        pickupLocationId = "IDPICKUP";
        returnLocationId= "IDRETURN";

        Mockito.when(mockPickupLocation.getId()).thenReturn(pickupLocationId);
        Mockito.when(mockReturnLocation.getId()).thenReturn(returnLocationId);
        Mockito.when(mockReturnLocation.getName()).thenReturn(returnLocationName);
        getViewModel().setLocations(mockPickupLocation, mockReturnLocation, false);
        Assert.assertEquals(ReactorViewState.VISIBLE, getViewModel().viewSeparator.visibility().getRawValue().intValue());
        Assert.assertEquals(ReactorViewState.VISIBLE, getViewModel().returnLocationContainer.visibility().getRawValue().intValue());
        Assert.assertEquals(returnLocationName, getViewModel().returnLocationName.text().getRawValue());
        Assert.assertEquals(R.string.reservation_confirmation_location_section_one_way_title,
                            getViewModel().locationsTitle.textRes().getRawValue().intValue());

    }

    @Override
    protected Class<ReviewLocationsViewModel> getViewModelClass() {
        return ReviewLocationsViewModel.class;
    }
}