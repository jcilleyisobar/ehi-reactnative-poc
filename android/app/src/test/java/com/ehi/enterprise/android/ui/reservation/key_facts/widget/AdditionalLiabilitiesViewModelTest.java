package com.ehi.enterprise.android.ui.reservation.key_facts.widget;

import com.ehi.enterprise.helpers.BaseViewModelTest;

import org.junit.Test;

public class AdditionalLiabilitiesViewModelTest extends BaseViewModelTest<AdditionalLiabilitiesViewModel> {

    @Test
    public void testOnAttachToView() throws Exception {
        assertGone(getViewModel().content.visibility().getRawValue());
    }

    @Test
    public void testCellTitleClicked() throws Exception {
        getViewModel().cellTitleClicked();
        assertVisible(getViewModel().content.visibility().getRawValue());

        getViewModel().cellTitleClicked();
        assertGone(getViewModel().content.visibility().getRawValue());
    }

    @Override
    protected Class<AdditionalLiabilitiesViewModel> getViewModelClass() {
        return AdditionalLiabilitiesViewModel.class;
    }
}