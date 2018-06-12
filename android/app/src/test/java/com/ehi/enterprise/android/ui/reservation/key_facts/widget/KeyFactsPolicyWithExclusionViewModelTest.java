package com.ehi.enterprise.android.ui.reservation.key_facts.widget;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.models.reservation.EHIExtraItem;
import com.ehi.enterprise.android.models.reservation.EHIKeyFactsPolicy;
import com.ehi.enterprise.android.models.reservation.EHIPrice;
import com.ehi.enterprise.helpers.BaseViewModelTest;
import com.ehi.enterprise.helpers.MockableObject;

import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class KeyFactsPolicyWithExclusionViewModelTest extends BaseViewModelTest<KeyFactsPolicyWithExclusionViewModel> {

    private MockableObject.TestAnswer mStringAnswer = new MockableObject.TestAnswer() {
        @Override
        public Object provideAnswer(final InvocationOnMock invocation) {
            final Object[] arguments = invocation.getArguments();
            int stringResId = (int) arguments[0];

            switch (stringResId) {
                case R.string.reservation_line_item_rental_rate_title:
                    return "#{price} / #{unit}";
                case R.string.reservation_rate_hourly_unit:
                    return "Hour";
                case R.string.reservation_rate_daily_unit:
                    return "Day";
                case R.string.reservation_rate_weekly_unit:
                    return "Week";
                case R.string.reservation_rate_rental_unit:
                    return "Rental";
                case R.string.reservation_rate_gallon_unit:
                    return "Gallon";
            }

            return null;
        }
    };

    @Test
    public void testSetPolicyWithExclusions() throws Exception {
        final String policyDescription = "Policy";
        final EHIKeyFactsPolicy policy = mock(EHIKeyFactsPolicy.class);
        final EHIKeyFactsPolicy exclusion = mock(EHIKeyFactsPolicy.class);
        final List<EHIKeyFactsPolicy> exclusions = new ArrayList<>();
        exclusions.add(exclusion);

        when(policy.getPolicyExclusions()).thenReturn(exclusions);
        when(policy.getDescription()).thenReturn(policyDescription);

        getViewModel().setPolicy(policy);

        assertEquals(policy, getViewModel().getPolicy());
        assertEquals(exclusions, getViewModel().getExclusions());
        assertVisible(getViewModel().exclusion);
        assertEquals(R.string.key_facts_protections_exclusions, getViewModel().exclusion.textRes().getRawValue().intValue());
        assertEquals(policyDescription, getViewModel().policyName.text().getRawValue());
    }

    @Test
    public void testSetPolicyWithoutExclusions() throws Exception {
        final String policyDescription = "Policy";
        EHIKeyFactsPolicy policy = mock(EHIKeyFactsPolicy.class);
        when(policy.getDescription()).thenReturn(policyDescription);
        when(policy.getPolicyExclusions()).thenReturn(null);

        getViewModel().setPolicy(policy);

        assertEquals(policy, getViewModel().getPolicy());
        assertNull(getViewModel().getExclusions());
        assertGone(getViewModel().exclusion);
        assertEquals(policyDescription, getViewModel().policyName.text().getRawValue());
    }

    @Test
    public void testSetExtraItemWithPriceHourly() throws Exception {
        EHIExtraItem item = getExtraItemForRate(EHIExtraItem.HOURLY);
        testExtraItemForRate(item, "Hour");
    }

    @Test
    public void testSetExtraItemWithPriceDaily() throws Exception {
        EHIExtraItem item = getExtraItemForRate(EHIExtraItem.DAILY);
        testExtraItemForRate(item, "Day");
    }

    @Test
    public void testSetExtraItemWithPriceWeekly() throws Exception {
        EHIExtraItem item = getExtraItemForRate(EHIExtraItem.WEEKLY);
        testExtraItemForRate(item, "Week");
    }

    @Test
    public void testSetExtraItemWithPriceRental() throws Exception {
        EHIExtraItem item = getExtraItemForRate(EHIExtraItem.RENTAL);
        testExtraItemForRate(item, "Rental");
    }

    @Test
    public void testSetExtraItemWithPriceGallon() throws Exception {
        EHIExtraItem item = getExtraItemForRate(EHIExtraItem.GALLON);
        testExtraItemForRate(item, "Gallon");
    }

    private EHIExtraItem getExtraItemForRate(@EHIExtraItem.RateType String rate){
        final String extraName = "Name";
        final EHIExtraItem item = mock(EHIExtraItem.class);
        final EHIPrice price = new EHIPrice("USD", "$", 10.0);
        when(item.getName()).thenReturn(extraName);
        when(item.getRateAmountView()).thenReturn(price);
        when(item.getRateType()).thenReturn(rate);

        return item;
    }

    private void testExtraItemForRate(EHIExtraItem item, String expectedRate){
        getMockedContext().getMockedResources().addAnswer(mStringAnswer);
        getViewModel().setExtraItem(item);

        assertGone(getViewModel().exclusion);
        assertEquals("Name", getViewModel().policyName.text().getRawValue());
        assertVisible(getViewModel().policyPrice);
        assertEquals("$10.00 / " + expectedRate, getViewModel().policyPrice.textCharSequence().getRawValue());
    }


    @Override
    protected Class<KeyFactsPolicyWithExclusionViewModel> getViewModelClass() {
        return KeyFactsPolicyWithExclusionViewModel.class;
    }
}