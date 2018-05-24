package com.ehi.enterprise.android.ui.dashboard;

import com.ehi.enterprise.helpers.BaseViewModelTest;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Locale;


public class RegionChoiceViewModelTest extends BaseViewModelTest<RegionChoiceViewModel> {

    @Before
    public void setUp() throws Exception {
        Mockito.when(getMockedDelegate().getMockedLocalDataManager()
                                    .getMockedObject()
                                    .getPreferredCountryCode())
               .thenReturn(Locale.US.toString());
        super.setup();
    }

    @Test
    public void testGenerateRegionName() {
        Locale locale = new Locale(Locale.getDefault().getLanguage(), Locale.US.toString());
        Assert.assertEquals(locale.getDisplayCountry(), getViewModel().preferredRegionName.getRawValue());
    }

    @Override
    protected Class<RegionChoiceViewModel> getViewModelClass() {
        return RegionChoiceViewModel.class;
    }
}
