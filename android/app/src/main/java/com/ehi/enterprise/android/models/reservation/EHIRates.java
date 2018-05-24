package com.ehi.enterprise.android.models.reservation;

import com.ehi.enterprise.android.models.EHIModel;
import com.google.gson.annotations.SerializedName;

public class EHIRates extends EHIModel {

	@SerializedName("unit_rate_type")
	private String mUnitRateType;

	@SerializedName("retail_unit_amount")
	private float mRetailUnitAmount;

	@SerializedName("unit_rate_type_quantity")
	private float mUnitRateTypeQuantity;

	public String getUnitRateType() {
		return mUnitRateType;
	}

	public float getRetailUnitAmount() {
		return mRetailUnitAmount;
	}

	public float getUnitRateTypeQuantity() {
		return mUnitRateTypeQuantity;
	}

}
