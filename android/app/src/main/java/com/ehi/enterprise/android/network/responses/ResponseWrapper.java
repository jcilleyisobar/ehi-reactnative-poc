package com.ehi.enterprise.android.network.responses;

import android.support.annotation.IntDef;

import com.ehi.enterprise.android.network.services.EHIServicesError;

import java.io.Serializable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class ResponseWrapper<T> implements Serializable {


    @Retention(RetentionPolicy.SOURCE)
    @IntDef({GENERIC_ERROR, NO_CONNECTIONS_AVAILABLE, JSON_PARSING_ERROR, OK, CREATED, MOVED, BAD_REQUEST, UNAUTHORIZED, FORBIDDEN, NOT_FOUND, INTERNAL_SERVER_ERROR, UNPROCESSABLE_ENTITY})
    public @interface ResponseStatus {
    }

    public static final int GENERIC_ERROR = -3;
    public static final int NO_CONNECTIONS_AVAILABLE = -2;
    public static final int JSON_PARSING_ERROR = -1;
    public static final int OK = 200;
    public static final int CREATED = 201;
    public static final int MOVED = 301;
    public static final int BAD_REQUEST = 400;
    public static final int UNAUTHORIZED = 401;
    public static final int FORBIDDEN = 403;
    public static final int NOT_FOUND = 404;
    public static final int CONFLICT = 409;
    public static final int INTERNAL_SERVER_ERROR = 500;
    public static final int UNPROCESSABLE_ENTITY = 422;

    private T mData;
    private int mStatus;
    private String mMessage;

    public ResponseWrapper() {
    }

    public ResponseWrapper(T data, int status, String message) {
        mData = data;
        mStatus = status;
        mMessage = message;
    }

    public T getData() {
        return mData;
    }

    public void setData(T mData) {
        this.mData = mData;
    }

    public
    @ResponseStatus
    int getStatus() {
        return mStatus;
    }

    public void setStatus(@ResponseStatus int mStatus) {
        this.mStatus = mStatus;
    }

    public boolean isSuccess() {
        return getStatus() == OK || getStatus() == CREATED;
    }

    public String getMessage() {
        if ((mData instanceof BaseResponse)) {
            return ((BaseResponse) mData).getErrorMessagesString();
        } else if (mMessage != null) {
            return mMessage;
        }
        return null;
    }

    public String getCodes() {
        if ((mData instanceof BaseResponse)) {
            return ((BaseResponse) mData).getServiceErrorCode();
        } else {
            return null;
        }
    }

    public void setMessage(String mMessage) {
        this.mMessage = mMessage;
    }

    public EHIServicesError.ErrorCode getErrorCode() {
        if (mData != null && mData instanceof BaseResponse) {
            return EHIServicesError.ErrorCode.createErrorCode(((BaseResponse) mData).getServiceErrorCode());
        }
        return null;
    }

    public EHIServicesError.DisplayAs getDisplayAs() {
        if (mData != null && mData instanceof BaseResponse) {
            return EHIServicesError.DisplayAs.createDisplayAs(((BaseResponse) mData).getServiceErrorDisplayAs());
        }
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ResponseWrapper)) {
            return false;
        }

        ResponseWrapper<?> that = (ResponseWrapper<?>) o;

        if (mStatus != that.mStatus) {
            return false;
        }
        return !(mData != null ? !mData.equals(that.mData) : that.mData != null) && !(mMessage != null ? !mMessage.equals(that.mMessage) : that.mMessage != null);

    }

    @Override
    public int hashCode() {
        int result = mData != null ? mData.hashCode() : 0;
        result = 31 * result + mStatus;
        result = 31 * result + (mMessage != null ? mMessage.hashCode() : 0);
        return result;
    }
}
