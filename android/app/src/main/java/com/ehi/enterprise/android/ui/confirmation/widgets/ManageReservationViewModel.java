package com.ehi.enterprise.android.ui.confirmation.widgets;

import android.view.View;

import com.ehi.enterprise.android.ui.viewmodel.ManagersAccessViewModel;
import com.ehi.enterprise.android.utils.reactor_extensions.viewstate.ReactorViewState;

import io.dwak.reactor.ReactorVar;

public class ManageReservationViewModel extends ManagersAccessViewModel {

    public ReactorViewState buttonsContainer = new ReactorViewState();
    public ReactorVar<Boolean> rotateArrow = new ReactorVar<>(false);
    public ReactorVar<Boolean> expandButtonsContainer = new ReactorVar<>(false);
    public ReactorVar<Boolean> collapseButtonsContainer = new ReactorVar<>(false);

    private int arrowInitialPosition = 0;
    private int arrowFinalPosition = 0;

    public int getArrowInitialPosition() {
        return arrowInitialPosition;
    }

    public int getArrowFinalPosition() {
        return arrowFinalPosition;
    }

    @Override
    public void onAttachToView() {
        super.onAttachToView();
        buttonsContainer.setVisibility(View.GONE);
    }

    public void toggleContainer() {
        if (isContainerVisible()) {
            collapseContainer();
        } else {
            expandContainer();
        }
        rotateArrow.setValue(true);
    }

    public boolean isContainerVisible() {
        return buttonsContainer.visibility().getRawValue() != null
                && buttonsContainer.visibility().getRawValue() == View.VISIBLE;
    }

    public void collapseContainer() {
        arrowInitialPosition = 90;
        arrowFinalPosition = 0;
        collapseButtonsContainer.setValue(true);
    }

    public void expandContainer() {
        arrowInitialPosition = 0;
        arrowFinalPosition = 90;
        expandButtonsContainer.setValue(true);
    }

    public void resetInitialState() {
        if (isContainerVisible()) {
            collapseContainer();
            rotateArrow.setValue(true);
        }
    }
}
