package com.ehi.enterprise.android.ui.reservation.widget;

import android.support.annotation.NonNull;
import android.view.View;

import com.ehi.enterprise.android.models.profile.EHIPaymentMethod;
import com.ehi.enterprise.android.models.profile.EHIPaymentProfile;
import com.ehi.enterprise.android.models.profile.EHIProfile;
import com.ehi.enterprise.android.models.profile.EHIProfileResponse;
import com.ehi.enterprise.android.models.reservation.EHIContract;
import com.ehi.enterprise.android.models.reservation.EHIReservation;
import com.ehi.enterprise.android.network.request_params.reservation.CommitRequestParams;
import com.ehi.enterprise.helpers.BaseViewModelTest;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

/**
 * See: https://docs.google.com/drawings/d/1ReDUrHMNahaE6jVwHpVQ8OHE70y6qLRL-5gcwtj5VZo/edit for logic around testing this viewmodel
 */
public class BillingAccountViewModelTest extends BaseViewModelTest<BillingAccountViewModel> {

    private static final String TEST_ALIAS = "test_alias";
    private EHIProfileResponse mProfile;
    private EHIReservation mReservationObject;
    private EHIPaymentProfile mPaymentProfile;
    private int mInvokeBillingCalls = 0;

    @Override
    public void setup() {
        super.setup();
        mInvokeBillingCalls = 0;
        mProfile = spy(new EHIProfileResponse());
        mReservationObject = spy(new EHIReservation());
        mPaymentProfile = spy(new EHIPaymentProfile());
        when(mProfile.getPaymentProfile()).thenReturn(mPaymentProfile);
    }


    @Test
    public void testNoBenefitsNoAttachedAccount() {
        BillingAccountView.IBillingCallBack testCallback = new BillingAccountView.IBillingCallBack() {
            @Override
            public void onBillingMethodChanged(String billingNumber, @CommitRequestParams.BillingTypes String billingType, List<String> paymentIds, String method) {
                assertNull(billingNumber);
                assertNull(billingType);
                assertNull(paymentIds);
            }
        };
        when(mReservationObject.getCorporateAccount()).thenReturn(new EHIContract());

        getViewModel().setup(mProfile, mReservationObject, testCallback);
        assertGone(getViewModel().rootViewVisible.getRawValue());
        assertFalse(getViewModel().isBillingAccountViewChecked());
    }

    @Test
    public void testUnauthNoContract() {
        BillingAccountView.IBillingCallBack testCallback = new BillingAccountView.IBillingCallBack() {
            @Override
            public void onBillingMethodChanged(String billingNumber, @CommitRequestParams.BillingTypes String billingType, List<String> paymentIds, String method) {
                assertNull(billingNumber);
                assertNull(billingType);
                assertNull(paymentIds);
            }
        };
        getViewModel().setup(null, mReservationObject, testCallback);
        assertGone(getViewModel().rootViewVisible.getRawValue());
        assertFalse(getViewModel().isBillingAccountViewChecked());
    }

    @Test
    public void testNoBenefitsAttachedAccount() {
        BillingAccountView.IBillingCallBack testCallback = new BillingAccountView.IBillingCallBack() {
            @Override
            public void onBillingMethodChanged(String billingNumber, @CommitRequestParams.BillingTypes String billingType, List<String> paymentIds, String method) {
                assertNull(billingNumber);
                assertTrue(billingType == CommitRequestParams.EXISTING);
                assertNull(paymentIds);
            }
        };
        EHIContract corporateAccount = spy(new EHIContract());
        when(corporateAccount.getBillingAccount()).thenReturn("test account");
        when(mReservationObject.getCorporateAccount()).thenReturn(corporateAccount);

        getViewModel().setup(mProfile, mReservationObject, testCallback);
        assertGone(getViewModel().rootViewVisible.getRawValue());
        assertTrue(getViewModel().isBillingAccountViewChecked());
    }

    @Test
    public void testSelectingBillingAndRedrawingShouldKeepState() {
        getViewModel().setup(mProfile, mReservationObject, null);
        assertFalse(getViewModel().isBillingAccountViewChecked());

        getViewModel().setBillingCheckRowChecked(true);
        getViewModel().newBillingCode.setValue("123");
        getViewModel().setLastCheckboxState(BillingAccountViewModel.BILLING_CHECKED);

        // redraw
        getViewModel().setup(mProfile, mReservationObject, null);

        assertTrue(getViewModel().isBillingAccountViewChecked());
    }

    @Test
    public void testSelectingPayAtPickupShouldHideBillingDropDown() {

        getViewModel().setBillingCheckRowChecked(false);
        getViewModel().newBillingCode.setValue("123");

        getViewModel().setBillingCheckRowChecked(false);
        assertFalse(getViewModel().isBillingAccountViewChecked());
    }

    @Test
    public void testBenefitsCustomerNoBilling() {
        BillingAccountView.IBillingCallBack testCallback = new BillingAccountView.IBillingCallBack() {
            @Override
            public void onBillingMethodChanged(String billingNumber, @CommitRequestParams.BillingTypes String billingType, List<String> paymentIds, String method) {
                assertNull(billingNumber);
                assertNull(billingType);
                assertNull(paymentIds);
            }
        };
        EHIContract corporateAccount = spy(new EHIContract());
        when(mReservationObject.contractHasAdditionalBenefits()).thenReturn(true);
        when(corporateAccount.isContractAcceptsBilling()).thenReturn(false);

        getViewModel().setup(mProfile, mReservationObject, testCallback);
        assertFalse(getViewModel().isBillingAccountViewChecked());
        assertGone(getViewModel().rootViewVisible.getRawValue());
    }

    /**
     * In this test despite a credit card being preferred, the attached billing account will start off being agreedToTermsAndConditions
     */
    @Test
    public void testBenefitsCustomerWithBilling_CreditCardPrefered() {
        TestBillingCallback testCallback = new TestBillingCallback() {
            @Override
            public void onBillingMethodChanged(String billingNumber, @CommitRequestParams.BillingTypes String billingType, List<String> paymentIds, String method) {

                if (mInvokeBillingCalls == 0) {
                    assertTrue(billingNumber == null || billingNumber.length() == 0);
                    assertTrue(billingType.contains(CommitRequestParams.EXISTING));
                    assertNull(paymentIds);
                }
                else if (mInvokeBillingCalls > 0) {
                    //hide default impl of TestBillingCallback until after first half of test is complete

                    super.onBillingMethodChanged(billingNumber, billingType, paymentIds, method);
                }
                mInvokeBillingCalls++;
            }
        };

        mockReservationWithBillingAccount();
        List<EHIPaymentMethod> paymentMethods = getPaymentMethods(EHIPaymentMethod.TYPE_CREDIT_CARD, true);
        when(mPaymentProfile.getCardPaymentMethods()).thenReturn(paymentMethods);

        getViewModel().setup(mProfile, mReservationObject, testCallback);
        assertTrue(getViewModel().isBillingAccountViewChecked());
        assertTrue(getViewModel().paymentSpinnerSelection.getRawValue().second.toString().contains(TEST_ALIAS));
        assertTrue(getViewModel().paymentSpinnerSelection.getRawValue().first == 1);
        assertTrue(mInvokeBillingCalls == 1);

        assertVisible(getViewModel().rootViewVisible.getRawValue());
        assertGone(getViewModel().billingSpinnerVisibility.getRawValue());
        assertGone(getViewModel().payAtCounterSpinnerVisible.getRawValue());
        assertVisible(getViewModel().maskedBillingAccountText.visibility().getRawValue());
        //endregion

        testCallback.setBillingNumber(null);
        testCallback.setBillingType(null);
        testCallback.setPaymentIds(Collections.singletonList(TEST_ALIAS));

        when(paymentMethods.get(1).getPaymentReferenceId()).thenReturn(TEST_ALIAS);

        //testing selecting payment option
        getViewModel().setBillingCheckRowChecked(false);

        testCallback.setBillingType(CommitRequestParams.EXISTING);
        testCallback.setPaymentIds(null);
        getViewModel().setBillingCheckRowChecked(true);
    }

    /**
     * Unlike the test above, the payment section will be agreedToTermsAndConditions due to the credit card being preferred, this is because there are billing options but there's no billing account
     */
    @Test
    public void testBenefitsCustomerWithNoBilling_CreditCardPreferred() {
        TestBillingCallback testCallback = new TestBillingCallback() {
            @Override
            public void onBillingMethodChanged(String billingNumber, @CommitRequestParams.BillingTypes String billingType, List<String> paymentIds, String method) {
                super.onBillingMethodChanged(billingNumber, billingType, paymentIds, method);
            }
        };
        //region setup
        String accountNumber = "testAccountNumber";
        mockReservationWithContractNumber(accountNumber);
        mockProfileWithCorporateAccountWithContractNumber(accountNumber);

        List<EHIPaymentMethod> creditMethods = getPaymentMethods(EHIPaymentMethod.TYPE_CREDIT_CARD, true);
        List<EHIPaymentMethod> billingMethods = getPaymentMethods(EHIPaymentMethod.TYPE_BILLING_CODE, false);

        when(mPaymentProfile.getCardPaymentMethods()).thenReturn(creditMethods);
        when(mPaymentProfile.getBillingPaymentMethods()).thenReturn(billingMethods);


        testCallback.setPaymentIds(new ArrayList<>(Collections.<String>singleton(null)));

        getViewModel().setup(mProfile, mReservationObject, testCallback);
        assertFalse(getViewModel().isBillingAccountViewChecked());

        assertVisible(getViewModel().rootViewVisible.getRawValue());
        assertGone(getViewModel().billingSpinnerVisibility.getRawValue());
        assertVisible(getViewModel().payAtCounterSpinnerVisible.getRawValue());
        assertGone(getViewModel().maskedBillingAccountText.visibility().getRawValue());
        //endregion

        //region toggling-testing
        String accountName = "name";
        int accountPosition = 0;
        when(creditMethods.get(0).getPaymentReferenceId()).thenReturn(accountNumber);
        testCallback.setPaymentIds(new ArrayList<>(Collections.singleton(accountNumber)));
        getViewModel().setCurrentPaymentSelection(accountPosition, accountName);

        //in the case where a billing account is not preferred the default is the first option
        testCallback.setPaymentIds(new ArrayList<>(Collections.<String>singleton(null)));
        getViewModel().setBillingCheckRowChecked(true);

        accountNumber = "billingNumber";
        testCallback.setPaymentIds(new ArrayList<>(Collections.singleton(accountNumber)));
        when(billingMethods.get(0).getPaymentReferenceId()).thenReturn(accountNumber);
        getViewModel().setCurrentBillingSelection(0, accountName);

        //Select adding a new account
        testCallback.setBillingNumber("");
        testCallback.setBillingType(CommitRequestParams.CUSTOM);
        testCallback.setPaymentIds(null);
        getViewModel().setCurrentBillingSelection(2, "");
        assertVisible(getViewModel().billingCodeEditTextVisibility.getRawValue());
        assertTrue(getViewModel().isBillingAccountViewChecked());


        testCallback.setBillingNumber(accountNumber);
        getViewModel().newBillingCode.setValue(accountNumber);

        testCallback.setPaymentIds(new ArrayList<>(Collections.singleton(accountNumber)));
        testCallback.setBillingNumber(null);
        testCallback.setBillingType(null);
        getViewModel().setCurrentBillingSelection(0, "");

        //endregion
    }

    /**
     *
     */
    @Test
    public void testBenefitsCustomerWithNoBilling_NoBillingNoPreferred() {
        TestBillingCallback testCallback = new TestBillingCallback();
        //region setup
        String accountNumber = "testAccountNumber";
        mockReservationWithContractNumber(accountNumber);
        mockProfileWithCorporateAccountWithContractNumber(accountNumber);

        List<EHIPaymentMethod> paymentMethods = getPaymentMethods(EHIPaymentMethod.TYPE_CREDIT_CARD, false);
        when(mPaymentProfile.getCardPaymentMethods()).thenReturn(paymentMethods);

        testCallback.setPaymentIds(new ArrayList<>(Collections.<String>singleton(null)));

        getViewModel().setup(mProfile, mReservationObject, testCallback);
        assertFalse(getViewModel().isBillingAccountViewChecked());

        assertVisible(getViewModel().rootViewVisible.getRawValue());
        assertGone(getViewModel().billingSpinnerVisibility.getRawValue());
        assertGone(getViewModel().billingCodeEditTextVisibility.getRawValue());
        assertVisible(getViewModel().payAtCounterSpinnerVisible.getRawValue());
        assertGone(getViewModel().maskedBillingAccountText.visibility().getRawValue());

        testCallback.setPaymentIds(null);
        testCallback.setBillingType(CommitRequestParams.CUSTOM);
        testCallback.setBillingNumber(accountNumber);

        getViewModel().billingCodeEditTextVisibility.setValue(View.VISIBLE);
        getViewModel().newBillingCode.setValue(accountNumber);


        //Testing to see if 'typing' toggles billing view to agreedToTermsAndConditions
        assertTrue(getViewModel().isBillingAccountViewChecked());

        //endregion

    }

    @Test
    public void testWhenSavedStateIsBilling_BillingCheckboxShouldBeSelected() throws Exception {
        getViewModel().setLastCheckboxState(BillingAccountViewModel.BILLING_CHECKED);
        mockReservationWithBillingAccount();
        getViewModel().setup(mProfile, mReservationObject, null);
        assertTrue(getViewModel().isBillingAccountViewChecked());
    }

    @Test
    public void testWhenSavedStateIsPayAtPickup_PayAtPickupShouldBeChecked() throws Exception {
        getViewModel().setLastCheckboxState(BillingAccountViewModel.PAY_AT_PICKUP_CHECKED);
        mockReservationWithBillingAccount();
        getViewModel().setup(mProfile, mReservationObject, null);
        assertFalse(getViewModel().isBillingAccountViewChecked());
    }

    @NonNull
    private void mockReservationWithContractNumber(String accountNumber) {
        EHIContract corporateAccount = spy(new EHIContract());
        when(mReservationObject.contractHasAdditionalBenefits()).thenReturn(true);
        when(mReservationObject.getCorporateAccount()).thenReturn(corporateAccount);
        when(corporateAccount.isContractAcceptsBilling()).thenReturn(true);
        when(corporateAccount.getContractNumber()).thenReturn(accountNumber);
    }

    private void mockReservationWithBillingAccount() {
        EHIContract corporateAccount = spy(new EHIContract());
        when(mReservationObject.contractHasAdditionalBenefits()).thenReturn(true);
        when(mReservationObject.getCorporateAccount()).thenReturn(corporateAccount);
        when(corporateAccount.isContractAcceptsBilling()).thenReturn(true);
        when(corporateAccount.getBillingAccount()).thenReturn("test account");
    }

    private void mockProfileWithCorporateAccountWithContractNumber(String accountNumber) {
        EHIProfile profile = spy(new EHIProfile());
        when(mProfile.getProfile()).thenReturn(profile);
        EHIContract userCorporateAccount = mock(EHIContract.class);
        when(userCorporateAccount.getContractNumber()).thenReturn(accountNumber);
        when(profile.getCorporateAccount()).thenReturn(userCorporateAccount);
    }


    /**
     * Utility method to create a list of size 2 with element at index 1 being prefered based on preferred param
     *
     * @param paymentType
     * @param preferred
     * @return
     */
    private List<EHIPaymentMethod> getPaymentMethods(@EHIPaymentMethod.PaymentType String paymentType, boolean preferred) {
        List<EHIPaymentMethod> paymentMethods = new ArrayList<>(2);

        EHIPaymentMethod method = spy(new EHIPaymentMethod());
        when(method.getPaymentType()).thenReturn(paymentType);
        doReturn(TEST_ALIAS).when(method).getAlias();
        paymentMethods.add(method);

        method = spy(new EHIPaymentMethod());
        when(method.getPaymentType()).thenReturn(paymentType);
        when(method.isPreferred()).thenReturn(preferred);
        doReturn(TEST_ALIAS).when(method).getAlias();
        paymentMethods.add(method);

        return paymentMethods;
    }


    @Override
    protected Class<BillingAccountViewModel> getViewModelClass() {
        return BillingAccountViewModel.class;
    }

    class TestBillingCallback implements BillingAccountView.IBillingCallBack {

        private String mBillingNumber;
        private String mBillingType;
        private List<String> mPaymentIds;
        private boolean mDisable = false;

        public void setBillingNumber(String billingNumber) {
            mBillingNumber = billingNumber;
        }

        public void setBillingType(String billingType) {
            mBillingType = billingType;
        }

        public void setPaymentIds(List<String> paymentIds) {
            mPaymentIds = paymentIds;
        }


        @Override
        public void onBillingMethodChanged(String billingNumber, @CommitRequestParams.BillingTypes String billingType, List<String> paymentIds, String method) {
            if (billingNumber == null) {
                assertNull(mBillingNumber);
            }
            else {
                assertTrue(billingNumber.contains(mBillingNumber));
            }
            if (billingType == null) {
                assertNull(mBillingType);
            }
            else {
                assertTrue(mBillingType.equals(billingType));
            }
            if (paymentIds == null) {
                assertNull(mPaymentIds);
            }
            else {
                String pId = paymentIds.get(0);
                if (pId == null) {
                    assertNull(mPaymentIds.get(0));
                }
                else {
                    assertTrue(pId.contains(mPaymentIds.get(0)));
                }
            }
        }
    }
}
