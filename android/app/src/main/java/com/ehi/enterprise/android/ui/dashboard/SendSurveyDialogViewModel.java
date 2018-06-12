package com.ehi.enterprise.android.ui.dashboard;

import com.ehi.enterprise.android.ui.viewmodel.CountrySpecificViewModel;
import com.ehi.enterprise.android.ui.viewmodel.ManagersAccessViewModel;
import com.ehi.enterprise.android.utils.EHIPatterns;
import com.ehi.enterprise.android.utils.EHITextUtils;
import com.ehi.enterprise.android.utils.reactor_extensions.ReactorPhoneFormatter;
import com.ehi.enterprise.android.utils.reactor_extensions.viewstate.ReactorPropertyChangedListener;
import com.ehi.enterprise.android.utils.reactor_extensions.viewstate.ReactorTextViewState;
import com.ehi.enterprise.android.utils.reactor_extensions.viewstate.ReactorViewState;

import io.dwak.reactor.unbinder.annotation.AutoUnbindAll;

@AutoUnbindAll
public class SendSurveyDialogViewModel extends CountrySpecificViewModel {

    final ReactorTextViewState contactInfo = new ReactorTextViewState();
    final ReactorViewState sendButton = new ReactorViewState();
    private ReactorPhoneFormatter mPhoneNumberFormatter;

    private final ReactorPropertyChangedListener<String> mFormChangedCallback = new ReactorPropertyChangedListener<String>() {
        @Override
        public void onPropertyChanged(String newValue) {
            String text = contactInfo.text().getRawValue();
            if (text == null) {
                text = "";
            }

            if (EHIPatterns.ONLY_PHONE_NUMBERS.matcher(text).matches()) {
                mPhoneNumberFormatter.format(contactInfo.text());
            }

            sendButton.setEnabled(
                    !EHITextUtils.isEmpty(text)
                            && (EHIPatterns.EMAIL_ADDRESS.matcher(text).matches()
                            || EHIPatterns.FORMATTED_PHONE_NUMBERS.matcher(text).matches())
            );
        }
    };

    @Override
    public void onAttachToView() {
        super.onAttachToView();
        String currentValue = contactInfo.text().getRawValue();
        if (currentValue == null) {
            currentValue = "";
        }
        mFormChangedCallback.onPropertyChanged(currentValue);
        mPhoneNumberFormatter = new ReactorPhoneFormatter();
        contactInfo.setTextChangedListener(mFormChangedCallback);
    }

    public void onSendSurveyClicked() {
        showProgress(true);
        getManagers().getForeseeSurveyManager().sendSurvey(contactInfo.text().getRawValue());
    }

    public void stopProgress() {
        showProgress(false);
    }

    public void showProgress() {
        showProgress(true);
    }
}
