package com.ehi.enterprise.android.ui.navigation;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.ui.login.LoginViewModel;
import com.ehi.enterprise.android.utils.manager.LoginManager;
import com.ehi.enterprise.helpers.BaseViewModelTest;
import com.ehi.enterprise.helpers.MockableObject;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.invocation.InvocationOnMock;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


@RunWith(JUnit4.class)
public class NavDrawerViewModelTest extends BaseViewModelTest<NavigationDrawerViewModel> {
    @Override
    protected Class<NavigationDrawerViewModel> getViewModelClass() {
        return NavigationDrawerViewModel.class;
    }

    @Test
    public void testCorrectDrawerItems() {

        final List<Integer> drawerItemStrings = new ArrayList<>(2);

        getMockedContext().getMockedResources().addCallback(new MockableObject.MockableObjectCallback() {
            @Override
            public void callback(InvocationOnMock invocationOnMock) {
                drawerItemStrings.add((int) invocationOnMock.getArguments()[0]);
            }
        });

        LoginAnswer loginAnswer = new LoginAnswer();
        getMockedDelegate().getMockedLoginManager().addAnswer(loginAnswer);

        getViewModel().populateDrawerItems();

        Assert.assertTrue(drawerItemStrings.contains(R.string.login_title));
        Assert.assertFalse(drawerItemStrings.contains(R.string.menu_profile));
        Assert.assertFalse(drawerItemStrings.contains(R.string.menu_sign_out));

        drawerItemStrings.clear();
        loginAnswer.LoggedIn = true;

        getViewModel().populateDrawerItems();

        Assert.assertFalse(drawerItemStrings.contains(R.string.login_title));
        Assert.assertTrue(drawerItemStrings.contains(R.string.menu_profile));
        Assert.assertTrue(drawerItemStrings.contains(R.string.menu_sign_out));
    }

    @Test
    public void testLocaleEnUs() throws Exception {
        testShareFeedbackForLocale(Locale.US, true);
    }

    @Test
    public void testLocaleEsUs() throws Exception {
        testShareFeedbackForLocale(new Locale("es", "US"), false);
    }

    @Test
    public void testLocaleEnCa() throws Exception {
        testShareFeedbackForLocale(Locale.CANADA, true);
    }

    @Test
    public void testLocaleFrCa() throws Exception {
        testShareFeedbackForLocale(Locale.CANADA_FRENCH, false);
    }

    private void testShareFeedbackForLocale(final Locale locale, final boolean expectedAssert) {
        Locale.setDefault(locale);

        getMockedDelegate().getMockedLocalDataManager().addAnswer("getPreferredCountryCode", locale.getCountry());


        final List<Integer> drawerItemStrings = new ArrayList<>(2);
        getMockedContext().getMockedResources().addCallback(new MockableObject.MockableObjectCallback() {
            @Override
            public void callback(InvocationOnMock invocationOnMock) {
                drawerItemStrings.add((int) invocationOnMock.getArguments()[0]);
            }
        });

        getViewModel().populateDrawerItems();
        Assert.assertEquals(expectedAssert, drawerItemStrings.contains(R.string.menu_send_feedback));
        Locale.setDefault(Locale.US);
    }

    @Test
    public void testDrawerSelection() {
        getViewModel().populateDrawerItems();

        getViewModel().setCurrentItem(2);
        Assert.assertTrue(getViewModel().getCurrentItemPosition() == 2);
    }

    class LoginAnswer implements MockableObject.TestAnswer {
        public boolean LoggedIn = false;

        @Override
        public Object provideAnswer(InvocationOnMock invocation) {
            return LoggedIn;
        }
    }
}
