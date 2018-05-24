package com.ehi.enterprise.android.ui.enroll;

import java.util.List;

public interface FormContract {

    interface FormListener {
        void isValid(boolean validation);
    }

    interface FormView {
        boolean isValid();

        void highlightInvalidFields();

        List<String> getErrorMessageList();

        void startHighlightInvalidFieldsOnFormChange();

        void stopHighlightInvalidFieldsOnFormChange();
    }
}
