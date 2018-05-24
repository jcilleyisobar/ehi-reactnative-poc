package com.ehi.enterprise.android.models.reservation;

import com.ehi.enterprise.android.models.EHIModel;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class EHIReservationEligibility extends EHIModel {

	@SerializedName("modify_reservation")
	private boolean mModifyReservation;

	@SerializedName("cancel_reservation")
	private boolean mCancelReservation;

	@SerializedName("view_base_reservation")
	private boolean mViewBaseReservation;

	@SerializedName("view_full_reservation")
	private boolean mViewFullReservation;

	@SerializedName("blocked_reasons")
	private List mBlockedReasons;

	public boolean isModifyReservation() {
		return mModifyReservation;
	}

	public boolean isCancelReservation() {
		return mCancelReservation;
	}

	public boolean isViewBaseReservation() {
		return mViewBaseReservation;
	}

	public boolean isViewFullReservation() {
		return mViewFullReservation;
	}

	public List getBlockedReasons() {
		return mBlockedReasons;
	}

}