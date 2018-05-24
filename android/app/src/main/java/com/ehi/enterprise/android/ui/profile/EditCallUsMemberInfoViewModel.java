package com.ehi.enterprise.android.ui.profile;

import com.ehi.enterprise.android.ui.viewmodel.ManagersAccessViewModel;
import com.ehi.enterprise.android.utils.EHITextUtils;

public class EditCallUsMemberInfoViewModel extends ManagersAccessViewModel {

    private String description;

    public String getPhoneNumber() {

        return EHITextUtils.isEmpty(getValidPhoneNumber().getPhoneNumber())
               ? getSupportPhoneNumber()
               : getValidPhoneNumber().getPhoneNumber();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
