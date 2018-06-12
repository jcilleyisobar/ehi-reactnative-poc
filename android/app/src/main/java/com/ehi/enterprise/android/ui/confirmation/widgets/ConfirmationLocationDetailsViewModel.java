package com.ehi.enterprise.android.ui.confirmation.widgets;

import android.view.View;

import com.ehi.enterprise.android.models.location.EHILocation;
import com.ehi.enterprise.android.ui.viewmodel.ManagersAccessViewModel;
import com.ehi.enterprise.android.utils.reactor_extensions.viewstate.ReactorImageViewState;
import com.ehi.enterprise.android.utils.reactor_extensions.viewstate.ReactorTextViewState;

import io.dwak.reactor.unbinder.annotation.AutoUnbindAll;

@AutoUnbindAll
public class ConfirmationLocationDetailsViewModel extends ManagersAccessViewModel {

    private EHILocation mLocation;

    public final ReactorImageViewState iconImageView = new ReactorImageViewState();
    public final ReactorTextViewState nameTextView = new ReactorTextViewState();
    public final ReactorTextViewState addressTextView = new ReactorTextViewState();
    public final ReactorTextViewState phoneTextView = new ReactorTextViewState();


    public void setLocation(EHILocation location) {
        mLocation = location;

        int drawableId = location.getGreenLocationCellIconDrawable();
        if (drawableId > 0) {
            iconImageView.setImageResource(drawableId);
            iconImageView.setVisibility(View.VISIBLE);
        }
        else {
            iconImageView.setVisibility(View.GONE);
        }

        nameTextView.setText(location.getName());

        if (location.getAddress() != null) {
            addressTextView.setText(location.getAddress().getReadableAddress());
        }

        if (location.getPhoneNumbers() != null
                && location.getPhoneNumbers().size() > 0) {
            phoneTextView.setText(location.getFormattedPhoneNumber(true));
        }
    }

    public EHILocation getLocation() {
        return mLocation;
    }
}
