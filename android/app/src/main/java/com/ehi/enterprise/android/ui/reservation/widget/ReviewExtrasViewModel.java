package com.ehi.enterprise.android.ui.reservation.widget;

import com.ehi.enterprise.android.models.reservation.EHIExtraItem;
import com.ehi.enterprise.android.models.reservation.EHIExtras;
import com.ehi.enterprise.android.ui.viewmodel.ManagersAccessViewModel;
import com.ehi.enterprise.android.utils.reactor_extensions.viewstate.ReactorViewState;

import java.util.List;

import io.dwak.reactor.ReactorVar;
import io.dwak.reactor.unbinder.annotation.AutoUnbindAll;

@AutoUnbindAll
public class ReviewExtrasViewModel extends ManagersAccessViewModel {

    //region reactive vars/states
    final ReactorViewState includedArea = new ReactorViewState();
    final ReactorViewState includedExtrasContainer = new ReactorViewState();
    final ReactorViewState includedExtrasTitle = new ReactorViewState();
    final ReactorViewState mandatoryArea = new ReactorViewState();
    final ReactorViewState mandatoryExtrasContainer = new ReactorViewState();
    final ReactorViewState mandatoryExtrasTitle = new ReactorViewState();
    final ReactorViewState addedArea = new ReactorViewState();
    final ReactorViewState addedExtrasContainer = new ReactorViewState();
    final ReactorViewState addedExtrasTitle = new ReactorViewState();
    final ReactorViewState noExtrasAddedView = new ReactorViewState();
    final ReactorViewState noExtrasAddedViewExtraArrow = new ReactorViewState();

    final ReactorVar<List<EHIExtraItem>> includedExtras = new ReactorVar<>();
    final ReactorVar<List<EHIExtraItem>> mandatoryExtras = new ReactorVar<>();
    final ReactorVar<List<EHIExtraItem>> addedExtras = new ReactorVar<>();
    //endregion

    private boolean mHideGreenArrow = false;

    public void setExtras(EHIExtras extras, boolean isReadOnly) {
        if (extras == null) {
            includedArea.setVisibility(ReactorViewState.GONE);
            mandatoryArea.setVisibility(ReactorViewState.GONE);
            addedArea.setVisibility(ReactorViewState.VISIBLE);
            noExtrasAddedView.setVisibility(ReactorViewState.VISIBLE);
            return;
        }

        List<EHIExtraItem> included = extras.getIncludedExtras();
        if (included.size() > 0) {
            includedArea.setVisibility(ReactorViewState.VISIBLE);
        } else {
            includedArea.setVisibility(ReactorViewState.GONE);
        }
        includedExtras.setValue(included);

        List<EHIExtraItem> mandatory = extras.getMandatoryExtras();
        if (mandatory.size() > 0) {
            mandatoryArea.setVisibility(ReactorViewState.VISIBLE);
        } else {
            mandatoryArea.setVisibility(ReactorViewState.GONE);
        }
        mandatoryExtras.setValue(mandatory);

        List<EHIExtraItem> added = extras.getSelectedExtras();
        if (added.size() > 0) {
            addedArea.setVisibility(ReactorViewState.VISIBLE);
            noExtrasAddedView.setVisibility(ReactorViewState.GONE);
        } else if (isReadOnly) {
            addedArea.setVisibility(ReactorViewState.GONE);
        } else {
            addedArea.setVisibility(ReactorViewState.VISIBLE);
            noExtrasAddedView.setVisibility(ReactorViewState.VISIBLE);
        }
        addedExtras.setValue(added);
    }

    public List<EHIExtraItem> getIncludedExtras() {
        return includedExtras.getValue();
    }

    public List<EHIExtraItem> getMandatoryExtras() {
        return mandatoryExtras.getValue();
    }

    public List<EHIExtraItem> getAddedExtras() {
        return addedExtras.getValue();
    }

    public void hideGreenArrow() {
        mHideGreenArrow = true;
        noExtrasAddedViewExtraArrow.setVisibility(ReactorViewState.GONE);
    }

    public void showGreenArrow() {
        mHideGreenArrow = false;
        noExtrasAddedViewExtraArrow.setVisibility(ReactorViewState.VISIBLE);
    }

    public boolean getHideGreenArrow() {
        return mHideGreenArrow;
    }
}
