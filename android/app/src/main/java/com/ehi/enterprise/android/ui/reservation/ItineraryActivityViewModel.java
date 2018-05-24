package com.ehi.enterprise.android.ui.reservation;

import com.ehi.enterprise.android.ui.reservation.interfaces.ReservationFlowListener;

import io.dwak.reactor.unbinder.annotation.AutoUnbindAll;

@AutoUnbindAll
public class ItineraryActivityViewModel extends ReservationFlowControlViewModel {

	private boolean mBackButtonBlocked;
	private boolean mAnimationInProgress;
	private ReservationFlowListener.AnimatingViewCallback mAnimationCallback;

	public boolean shouldSaveDriverInfo() {
		return getManagers().getReservationManager().shouldSaveDriverInfo();
	}

	public void deleteDriverInfo() {
		getManagers().getReservationManager().deleteDriverInfo();
	}

	public void removeEmeraldClubAccount() {
		getManagers().getReservationManager().removeEmeraldClubAccount();
	}

	public void setBackButtonBlocked(final boolean backButtonBlocked) {
		mBackButtonBlocked = backButtonBlocked;
	}

	public boolean isBackButtonBlocked() {
		if(isCarListAnimationInProgress()){
			mAnimationCallback.backPressed();
			return true;
		}
		return mBackButtonBlocked;
	}

	public void resetModifyState() {
		getManagers().getReservationManager().setModify(false);
	}

	public void setAnimationInProgress(boolean inProgress) {
		mAnimationInProgress = inProgress;
	}

	public boolean isCarListAnimationInProgress() {
		return mAnimationInProgress;
	}

	public void setAnimatingCallback(ReservationFlowListener.AnimatingViewCallback callback) {
		mAnimationCallback = callback;
	}
}