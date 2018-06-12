package com.ehi.enterprise.android.network.request_params.profile;

import com.ehi.enterprise.android.BuildConfig;
import com.ehi.enterprise.android.models.profile.EHICreditCard;
import com.ehi.enterprise.android.models.profile.payment.pangui.EHIPaymentMedia;
import com.ehi.enterprise.android.models.profile.payment.pangui.EHISecurityCredential;
import com.google.gson.annotations.SerializedName;

import java.util.UUID;


public class PostPanguiAddCreditRequestBodyParams {

    @SerializedName("SecurityCredential")
    private EHISecurityCredential mSecurityCredential;

    @SerializedName("PaymentMedia")
    private EHIPaymentMedia mPaymentMedia;

    @SerializedName("SourceSystemCode")
    private int mSourceSystemCode;

    @SerializedName("Request")
    private PanguiRequest mRequest;


    public PostPanguiAddCreditRequestBodyParams(String token, int sourceSystemCode, String callingApplicationName, EHICreditCard creditCard, String holderName) {
        mSecurityCredential = new EHISecurityCredential(token);
        mSourceSystemCode = sourceSystemCode;
        mRequest = new PanguiRequest(callingApplicationName, callingApplicationName);
        mPaymentMedia = new EHIPaymentMedia(creditCard, holderName);
    }

    private static final class PanguiRequest {

        @SerializedName("CallerIdentity")
        private final String mCallerIdentity = "eApp_Android";

        @SerializedName("CallingProcess")
        private String mCallingProcess;

        @SerializedName("CallingApplicationName")
        private String mCallingApplicationName;

        @SerializedName("CallingInterfaceVersion")
        private final String mCallingInterfaceVersion = "2.7.0";

        @SerializedName("CallingApplicationVersion")
        private final String mCallingApplicationVersion = BuildConfig.VERSION_NAME;

        @SerializedName("CallingHostOrWeblogicInstance")
        private final String mCallingHostOrWeblogicInstance = "eApp_Mobile_App_Android";

        @SerializedName("RequestId")
        private final String mRequestId = UUID.randomUUID().toString();

        public PanguiRequest(String callingProcess, String callingApplicationName) {
            mCallingProcess = callingProcess;
            mCallingApplicationName = callingApplicationName;
        }
    }
}
