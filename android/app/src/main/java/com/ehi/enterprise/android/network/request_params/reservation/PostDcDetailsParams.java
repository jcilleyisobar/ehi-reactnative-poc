package com.ehi.enterprise.android.network.request_params.reservation;

import com.ehi.enterprise.android.models.EHIModel;
import com.ehi.enterprise.android.models.reservation.EHIDCDetails;
import com.google.gson.annotations.SerializedName;

public class PostDcDetailsParams extends EHIModel {

	@SerializedName("delivery")
	private EHIDCDetails mDeliveryDetails;

	@SerializedName("collection")
	private EHIDCDetails mCollectionDetails;

	public PostDcDetailsParams(EHIDCDetails deliveryDetails, EHIDCDetails collectionDetails) {
		mDeliveryDetails = deliveryDetails;
		mCollectionDetails = collectionDetails;
	}
}
