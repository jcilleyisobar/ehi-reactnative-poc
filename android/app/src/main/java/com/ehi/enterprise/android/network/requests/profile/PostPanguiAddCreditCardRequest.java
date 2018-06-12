package com.ehi.enterprise.android.network.requests.profile;

import com.ehi.enterprise.android.models.profile.EHICreditCard;
import com.ehi.enterprise.android.network.request_params.profile.PostPanguiAddCreditRequestParams;
import com.ehi.enterprise.android.network.requests.AbstractRequestProvider;
import com.ehi.enterprise.android.network.responses.profile.PostPanguiAddCreditCardResponse;

import java.util.Map;

public class PostPanguiAddCreditCardRequest extends AbstractRequestProvider<PostPanguiAddCreditCardResponse> {

    private final PostPanguiAddCreditRequestParams body;
    private final String mCardSubmissionUrl;

    public PostPanguiAddCreditCardRequest(String submissionUrl, String token, int sourceSystemCode, String callingApplicationName, EHICreditCard creditCard, String holderName) {
        body = new PostPanguiAddCreditRequestParams(token, sourceSystemCode, callingApplicationName, creditCard, holderName);
        mCardSubmissionUrl = submissionUrl;
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
        return body;
    }

    @Override
    public Class<PostPanguiAddCreditCardResponse> getResponseClass() {
        return PostPanguiAddCreditCardResponse.class;
    }

    @Override
    public String getEndpointUrl() {
        return mCardSubmissionUrl;
    }
}
