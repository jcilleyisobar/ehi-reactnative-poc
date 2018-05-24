package com.ehi.enterprise.android.models.reservation;

import android.support.annotation.StringDef;

import com.ehi.enterprise.android.models.EHIModel;
import com.google.gson.annotations.SerializedName;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class EHIVehicleRate extends EHIModel {

	@Retention(RetentionPolicy.SOURCE)
	@StringDef({PREPAY, PAYLATER, CONTRACT, PROMOTION, RETAIL})
	public @interface ChargeType {
	}

	public static final String PREPAY = "PREPAY";
	public static final String PAYLATER = "PAYLATER";
	public static final String CONTRACT = "CONTRACT";
	public static final String PROMOTION = "PROMOTION";
	public static final String RETAIL = "RETAIL";

	@SerializedName("charge_type")
	private @ChargeType String mChargeType;

	@SerializedName("extras")
	private EHIExtras mExtras;

	@SerializedName("price_summary")
	private EHIPriceSummary mPriceSummary;

	public String getChargeType() {
		return mChargeType;
	}

	public EHIExtras getExtras() {
		return mExtras;
	}

	public EHIPriceSummary getPriceSummary() {
		return mPriceSummary;
	}
}
