package com.ehi.enterprise.android.ui.dashboard;

import com.ehi.enterprise.android.app.Environment;
import com.ehi.enterprise.android.app.SolrEnvironment;
import com.ehi.enterprise.helpers.BaseViewModelTest;

import org.junit.Test;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

public class MainViewModelTest extends BaseViewModelTest<MainViewModel> {
    @Override
    protected Class<MainViewModel> getViewModelClass() {
        return MainViewModel.class;
    }

    @Test
    public void testShouldEraseDataAfterUpdatingEnvironment() {
        getViewModel().setEnvironment(Environment.RCQA);
        verify(getViewModel()).clearDataForRestart();
        verify(getViewModel()).logOut();
    }

    @Test
    public void testShouldNotEraseDataAfterUpdatingSolrEnvironment() {
        getViewModel().setSolrEnvironment(SolrEnvironment.INT1);
        verify(getViewModel(), never()).logOut();
        verify(getViewModel(), never()).clearDataForRestart();
    }
}
