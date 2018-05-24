package com.ehi.enterprise.android.ui.location;

import com.ehi.enterprise.android.models.location.EHIWayfindingStep;
import com.ehi.enterprise.android.ui.viewmodel.ManagersAccessViewModel;

import java.util.List;

public class DirectionsFromTerminalViewModel extends ManagersAccessViewModel{
    private List<EHIWayfindingStep> mWayfindingSteps;

    public void setWayfindingSteps(final List<EHIWayfindingStep> wayfindingSteps) {
        mWayfindingSteps = wayfindingSteps;
    }

    public List<EHIWayfindingStep> getWayfindingSteps() {
        return mWayfindingSteps;
    }
}
