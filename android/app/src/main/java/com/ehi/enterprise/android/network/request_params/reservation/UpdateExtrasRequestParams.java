package com.ehi.enterprise.android.network.request_params.reservation;

import com.ehi.enterprise.android.models.EHIModel;
import com.ehi.enterprise.android.models.reservation.EHIExtraItem;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class UpdateExtrasRequestParams {

	@SerializedName("extras")
	private List<ExtaPair> mExtrasPair = new ArrayList<>();

	public UpdateExtrasRequestParams(List<EHIExtraItem> extras) {
		for (EHIExtraItem item : extras) {
			mExtrasPair.add(new ExtaPair(item.getCode(), item.getSelectedQuantity()));
		}
	}

	private static final class ExtaPair extends EHIModel {
		@SerializedName("code")
		private String mCode;

		@SerializedName("quantity")
		private int mQuantity;

		private ExtaPair(String code, int quantity) {
			mCode = code;
			mQuantity = quantity;
		}
	}

}