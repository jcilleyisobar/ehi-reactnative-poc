package com.ehi.enterprise.android.network.services;

public class EHIServicesError {
    public enum ErrorCode {

        CROS_INVALID_AUTH_TOKEN("CROS_INVALID_AUTH_TOKEN"),
        INVALID_API_KEY("INVALID_API_KEY"),
        NO_VALID_PRODUCTS("PRICING_16401"),
        CROS_BUSINESS_LEISURE_CONTRACT_NOT_ON_PROFILE("CROS_BUSINESS_LEISURE_CONTRACT_NOT_ON_PROFILE"),
        CROS_CONTRACT_NOT_ON_PROFILE("CROS_CONTRACT_NOT_ON_PROFILE"),
        CROS_LOGIN_SYSTEM_ERROR("CROS_LOGIN_SYSTEM_ERROR"),
        CROS_TRAVEL_PURPOSE_NOT_SPECIFIED("CROS_TRAVEL_PURPOSE_NOT_SPECIFIED"),
        CROS_LOGIN_TERMS_AND_CONDITIONS_ACCEPT_VERSION_MISMATCH("CROS_LOGIN_TERMS_AND_CONDITIONS_ACCEPT_VERSION_MISMATCH"),
        CROS_REDEMPTION_RES_LOOKUP_LOGIN_REQUIRED("CROS_REDEMPTION_RES_LOOKUP_LOGIN_REQUIRED"),
        CROS_LOGIN_WEAK_PASSWORD_ERROR("CROS_LOGIN_WEAK_PASSWORD_ERROR"),
        CROS_CONTRACT_PIN_REQUIRED("CROS_CONTRACT_PIN_REQUIRED"),
        CROS_CONTRACT_PIN_INVALID("CROS_CONTRACT_PIN_INVALID"),
        CROS_RES_PRE_RATE_ADDITIONAL_FIELD_REQUIRED("CROS_RES_PRE_RATE_ADDITIONAL_FIELD_REQUIRED"),
        CROS_RES_INVALID_ADDITIONAL_FIELD("CROS_RES_INVALID_ADDITIONAL_FIELD");

        private String mCodeValue;

        ErrorCode(String codeValue) {
            mCodeValue = codeValue;
        }

        public String getCodeValue() {
            return mCodeValue;
        }

        public static ErrorCode createErrorCode(String codeValue) {
            for (ErrorCode errorCode : ErrorCode.values()) {
                if (codeValue.contains(errorCode.getCodeValue())) {
                    return errorCode;
                }
            }
            return null;
        }

    }

    public enum DisplayAs {
        SILENT("silent"),
        ALERT("alert"),
        CALL_US("callus"),
        CALL_US_CONTINUE("callus_cont");

        private String mMessage;

        DisplayAs(String message) {
            mMessage = message;
        }

        public String getMessage() {
            return mMessage;
        }

        public static DisplayAs createDisplayAs(String displayAsString) {
            for (DisplayAs displayAs : DisplayAs.values()) {
                if (displayAsString.contains(displayAs.getMessage())) {
                    return displayAs;
                }
            }

            return null;
        }
    }
}
