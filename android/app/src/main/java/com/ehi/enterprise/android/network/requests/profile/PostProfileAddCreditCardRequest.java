package com.ehi.enterprise.android.network.requests.profile;

import com.ehi.enterprise.android.app.Settings;
import com.ehi.enterprise.android.network.headers.ApiHeaderBuilder;
import com.ehi.enterprise.android.network.request_params.profile.PostProfileAddCreditRequestParams;
import com.ehi.enterprise.android.network.requests.AbstractRequestProvider;
import com.ehi.enterprise.android.network.responses.profile.PaymentProfileResponse;
import com.ehi.enterprise.android.network.util.EHIUrlBuilder;
import com.ehi.enterprise.android.utils.payment.CreditCard;

import java.util.Map;

public class PostProfileAddCreditCardRequest extends AbstractRequestProvider<PaymentProfileResponse> {

    private final String mIndividualId;
    private final PostProfileAddCreditRequestParams mCreditCardBody;

    public PostProfileAddCreditCardRequest(String referenceId, CreditCard creditCard) {
        mIndividualId = creditCard.getIndividualId();
        mCreditCardBody = new PostProfileAddCreditRequestParams(referenceId, creditCard.getCard());
    }

    @Override
    public RequestType getRequestType() {
        return RequestType.POST;
    }

    @Override
    public String getRequestUrl() {
        EHIUrlBuilder bld = new EHIUrlBuilder()
                .appendSubPath(Settings.EHI_GBO_ENDPOINT_API)
                .appendSubPath("profiles")
                .appendSubPath(Settings.BRAND)
                .appendSubPath(Settings.CHANNEL)
                .appendSubPath("profile")
                .appendSubPath("payment")
                .addQueryParam("individualId", mIndividualId);
        return bld.build();
    }

    @Override
    public Map<String, String> getHeaders() {
        return ApiHeaderBuilder
                .gboDefaultHeaders()
                .build();
    }

    @Override
    protected HostType getHost() {
        return HostType.GBO_PROFILE;
    }

    @Override
    public Object getRequestBody() {
        return mCreditCardBody;
    }

    @Override
    public Class<PaymentProfileResponse> getResponseClass() {
        return PaymentProfileResponse.class;
    }

}
