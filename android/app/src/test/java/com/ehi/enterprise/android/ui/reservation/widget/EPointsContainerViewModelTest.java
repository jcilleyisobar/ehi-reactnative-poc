package com.ehi.enterprise.android.ui.reservation.widget;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.models.reservation.EHICarClassDetails;
import com.ehi.enterprise.android.utils.reactor_extensions.viewstate.ReactorViewState;
import com.ehi.enterprise.helpers.BaseViewModelTest;
import com.ehi.enterprise.helpers.MockableObject;

import junit.framework.Assert;

import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;

public class EPointsContainerViewModelTest extends BaseViewModelTest<EPointsContainerViewModel> {

    @Test
    public void testSetExpanded() throws Exception {
        getViewModel().setExpanded(false);
        assertGone(getViewModel().root.visibility().getRawValue());

        getViewModel().setExpanded(true);
        assertVisible(getViewModel().root.visibility().getRawValue());
    }

    @Test
    public void testSetKeepHidden() throws Exception {
        getViewModel().root.setVisibility(ReactorViewState.VISIBLE); //need to let the lazy initializer run
        getViewModel().setKeepHidden(false);
        assertVisible(getViewModel().root.visibility().getRawValue());

        getViewModel().setKeepHidden(true);
        assertGone(getViewModel().root.visibility().getRawValue());
    }

    @Test
    public void testSetRootVisibilityKeepHidden() throws Exception {
        getViewModel().setKeepHidden(true);

        getViewModel().setRootVisibility(ReactorViewState.VISIBLE);
        assertGone(getViewModel().root.visibility().getRawValue());

        getViewModel().setRootVisibility(ReactorViewState.GONE);
        assertGone(getViewModel().root.visibility().getRawValue());
    }

    @Test
    public void testSetRootVisibility() throws Exception {
        getViewModel().setKeepHidden(false);

        getViewModel().setRootVisibility(ReactorViewState.VISIBLE);
        assertVisible(getViewModel().root.visibility().getRawValue());

        getViewModel().setRootVisibility(ReactorViewState.GONE);
        assertGone(getViewModel().root.visibility().getRawValue());
    }

    @Test
    public void testSetCarClassDetailsShowPointsTwoWayReservationNotEnoughPoints() throws Exception {
        final String expected = "Not enough points for a free day";
        getMockedContext().getMockedResources().addAnswer("getString", expected);
        getViewModel().onAttachToView();

        EHICarClassDetails ehiCarClassDetails = Mockito.spy(EHICarClassDetails.class);
        Mockito.when(ehiCarClassDetails.getRedemptionPoints()).thenReturn(4120.00f);
        Mockito.when(ehiCarClassDetails.getMaxRedemptionDays()).thenReturn(0);
        getViewModel().setRootVisibility(ReactorViewState.VISIBLE);

        getViewModel().setCarClassDetails(ehiCarClassDetails);

        Assert.assertFalse(getViewModel().isKeepHidden());
        assertVisible(getViewModel().root.visibility().getRawValue());

        Assert.assertEquals("4,120", getViewModel().pointsPerDay.text().getRawValue());

        assertVisible(getViewModel().title.visibility().getRawValue());
        Assert.assertEquals(expected, getViewModel().title.text().getRawValue());
        assertGone(getViewModel().subtitle.visibility().getRawValue());
    }

    @Test
    public void testSetCarClassDetailsShowPointsTwoWayReservationEnoughPointsOneDay() throws Exception {
        final String freeDaysTemplateString = "#{number_of_days} free day";
        final String freeDaysString = "1 free day";
        final String titleString = "Enough points for";
        getMockedContext().getMockedResources().addAnswer(new MockableObject.TestAnswer() {
            @Override
            public Object provideAnswer(final InvocationOnMock invocation) {
                if (invocation.getMethod().getName().equals("getString")) {
                    if (((int) invocation.getArguments()[0]) == R.string.redemption_free_day_subtitle) {
                        return freeDaysTemplateString;
                    } else if (((int) invocation.getArguments()[0]) == R.string.redemption_free_days_title) {
                        return titleString;
                    }
                }
                return null;
            }
        });
        getViewModel().onAttachToView();

        EHICarClassDetails ehiCarClassDetails = Mockito.spy(EHICarClassDetails.class);
        Mockito.when(ehiCarClassDetails.getRedemptionPoints()).thenReturn(4120.00f);
        Mockito.when(ehiCarClassDetails.getMaxRedemptionDays()).thenReturn(1);
        getViewModel().setRootVisibility(ReactorViewState.VISIBLE);

        getViewModel().setCarClassDetails(ehiCarClassDetails);

        Assert.assertEquals(titleString, getViewModel().title.text().getRawValue());

        assertVisible(getViewModel().title.visibility().getRawValue());

        Assert.assertEquals(freeDaysString, getViewModel().subtitle.textCharSequence().getRawValue());
        assertVisible(getViewModel().subtitle.visibility().getRawValue());
    }

    @Test
    public void testSetCarClassDetailsShowPointsTwoWayReservationEnoughPointsMultiDay() throws Exception {
        final String freeDaysTemplateString = "#{number_of_days} free days";
        final String freeDaysString = "10 free days";
        final String titleString = "Enough points for";
        getMockedContext().getMockedResources().addAnswer(new MockableObject.TestAnswer() {
            @Override
            public Object provideAnswer(final InvocationOnMock invocation) {
                if (invocation.getMethod().getName().equals("getString")) {
                    if (((int) invocation.getArguments()[0]) == R.string.redemption_free_days_subtitle) {
                        return freeDaysTemplateString;
                    } else if (((int) invocation.getArguments()[0]) == R.string.redemption_free_days_title) {
                        return titleString;
                    }
                }
                return null;
            }
        });
        getViewModel().onAttachToView();

        EHICarClassDetails ehiCarClassDetails = Mockito.spy(EHICarClassDetails.class);
        Mockito.when(ehiCarClassDetails.getRedemptionPoints()).thenReturn(4120.00f);
        Mockito.when(ehiCarClassDetails.getMaxRedemptionDays()).thenReturn(10);
        getViewModel().setRootVisibility(ReactorViewState.VISIBLE);

        getViewModel().setCarClassDetails(ehiCarClassDetails);

        Assert.assertEquals(titleString, getViewModel().title.text().getRawValue());

        assertVisible(getViewModel().title.visibility().getRawValue());

        Assert.assertEquals(freeDaysString, getViewModel().subtitle.textCharSequence().getRawValue());
        assertVisible(getViewModel().subtitle.visibility().getRawValue());
    }

    @Override
    protected Class<EPointsContainerViewModel> getViewModelClass() {
        return EPointsContainerViewModel.class;
    }
}