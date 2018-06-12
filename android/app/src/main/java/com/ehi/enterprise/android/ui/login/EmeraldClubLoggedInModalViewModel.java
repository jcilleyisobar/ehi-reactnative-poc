package com.ehi.enterprise.android.ui.login;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.ui.viewmodel.ManagersAccessViewModel;
import com.ehi.enterprise.android.utils.reactor_extensions.viewstate.ReactorTextViewState;

import io.dwak.reactor.ReactorVar;
import io.dwak.reactor.unbinder.annotation.AutoUnbindAll;

@AutoUnbindAll
public class EmeraldClubLoggedInModalViewModel extends ManagersAccessViewModel {
    //region Reactor Variables
    final ReactorVar<Integer> title = new ReactorVar<>();
    final ReactorTextViewState dialogText = new ReactorTextViewState();
    final ReactorTextViewState positiveButtonText = new ReactorTextViewState();
    final ReactorTextViewState negativeButtonText = new ReactorTextViewState();
    //endregion


    @Override
    public void onAttachToView() {
        super.onAttachToView();
        title.setValue(R.string.login_emerald_club_logged_in_title);
        dialogText.setText(R.string.login_emerald_club_logged_in_text);
        positiveButtonText.setText(R.string.login_emerald_club_logged_in_yes_button);
        negativeButtonText.setText(R.string.login_emerald_club_logged_in_no_button);
    }

    private boolean isYesClicked;

    public boolean isYesClicked() {
        return isYesClicked;
    }

    public void setIsYesPressed(boolean isYesPressed) {
        this.isYesClicked = isYesPressed;
    }

    public void yesButtonClicked() {
        isYesClicked = true;
        getManagers().getReservationManager().removeEmeraldClubAccount();
    }
}
