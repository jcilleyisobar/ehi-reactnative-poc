package com.ehi.enterprise.android.network.requests.profile;

import android.net.Uri;

import com.ehi.enterprise.android.app.Settings;
import com.ehi.enterprise.android.models.profile.EHICreditCard;
import com.ehi.enterprise.android.network.request_params.profile.PostAddCreditRequestParams;
import com.ehi.enterprise.android.network.requests.AbstractRequestProvider;
import com.ehi.enterprise.android.network.responses.BaseResponse;

import java.util.Map;

public class PostAddCreditCardRequest extends AbstractRequestProvider<BaseResponse>{

    private final PostAddCreditRequestParams mCreditCardBody;
    private final String mCardSubmissionUrl;

    public PostAddCreditCardRequest(EHICreditCard creditCard, String cardSubmissionUrl, String submissionKey) {
        mCardSubmissionUrl = cardSubmissionUrl.replace("{cardSubmissionKey}", submissionKey);
        final Uri uri = Uri.parse(cardSubmissionUrl);
        Settings.ENVIRONMENT.setPaymentEnvironmentHost(uri.getHost());
        mCreditCardBody = new PostAddCreditRequestParams(creditCard);
    }

    @Override
    public RequestType getRequestType() {
        return RequestType.POST;
    }

    @Override
    public String getRequestUrl() {
        return mCardSubmissionUrl;
    }

    @Override
    public Map<String, String> getHeaders() {
        return null;
    }

    @Override
    public Object getRequestBody() {
        return mCreditCardBody;
    }

    @Override
    public boolean isExpectingBody() {
        return false;
    }

    @Override
    public Class<BaseResponse> getResponseClass() {
        return BaseResponse.class;
    }

    @Override
    public String getEndpointUrl() {
        return mCardSubmissionUrl;
    }
}
