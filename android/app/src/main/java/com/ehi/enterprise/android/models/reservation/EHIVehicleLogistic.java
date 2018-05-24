package com.ehi.enterprise.android.models.reservation;

import com.ehi.enterprise.android.models.EHIModel;
import com.google.gson.annotations.SerializedName;

public class EHIVehicleLogistic extends EHIModel {

	@SerializedName("delivery_info")
	private EHIDCDetails mDeliveryInfo;

	@SerializedName("collection_info")
	private EHIDCDetails mCollectionInfo;

	public EHIDCDetails getDeliveryInfo() {
		return mDeliveryInfo;
	}

	public EHIDCDetails getCollectionInfo() {
		return mCollectionInfo;
	}

	public boolean isSameAsDelivery() {
        return mDeliveryInfo != null && mCollectionInfo != null && mDeliveryInfo.equals(mCollectionInfo);

    }
}
