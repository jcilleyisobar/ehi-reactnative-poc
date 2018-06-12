package com.ehi.enterprise.android.ui.confirmation.widgets;

import com.ehi.enterprise.android.models.location.EHIImage;
import com.ehi.enterprise.android.models.reservation.EHICarClassDetails;
import com.ehi.enterprise.android.ui.viewmodel.ManagersAccessViewModel;
import com.ehi.enterprise.android.utils.reactor_extensions.viewstate.ReactorTextViewState;

import java.util.List;

import io.dwak.reactor.ReactorVar;
import io.dwak.reactor.unbinder.annotation.AutoUnbindAll;

@AutoUnbindAll
public class ConfirmationHeaderViewModel extends ManagersAccessViewModel {

    public final ReactorTextViewState confirmationNumberText = new ReactorTextViewState();
    private final  ReactorVar<List<EHIImage>> mImagesList = new ReactorVar<>();

    public void setConfirmationNumber(String number) {
        confirmationNumberText.setText("#" + number);
    }

    public List<EHIImage> getImagesList() {
        return mImagesList.getValue();
    }

    public void setCarClassDetails(EHICarClassDetails details) {
        if (details != null) {
            mImagesList.setValue(details.getImages());
        }
    }
}
