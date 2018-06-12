package com.ehi.enterprise.android.network.responses.profile;

import com.ehi.enterprise.android.models.profile.EHICreditCard;
import com.ehi.enterprise.android.network.responses.BaseResponse;
import com.google.gson.annotations.SerializedName;

public class PostAddCreditCardResponse extends BaseResponse {
    @SerializedName("card")
    private EHICreditCard mEHICreditCard;

    public EHICreditCard getEHICreditCard() {
        return mEHICreditCard;
    }
}
