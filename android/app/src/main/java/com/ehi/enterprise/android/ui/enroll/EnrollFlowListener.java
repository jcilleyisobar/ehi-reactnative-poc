package com.ehi.enterprise.android.ui.enroll;

import java.util.ArrayList;

public interface EnrollFlowListener {

    void goToStepOne();

    void goToStepTwo();

    void goToStepTwoWithDriverFound();

    void goToAddressStep(boolean isEmeraldClub);

    void goToStepThree();

    void goToFullFormStep(ArrayList<String> errorMessageList);

    void goToConfirmationStep(String loyaltyNumber, String password);
}
