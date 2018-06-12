package com.ehi.enterprise.android.network.request_params.profile;

import com.ehi.enterprise.android.models.profile.EHICreditCard;
import com.google.gson.annotations.SerializedName;

public class PostAddCreditRequestParams {

    @SerializedName("card")
    private EHICreditCard mCard;

    public PostAddCreditRequestParams(EHICreditCard card) {
        mCard = card;
    }
}
