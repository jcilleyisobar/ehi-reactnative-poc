package com.ehi.enterprise.android.network.util;

import android.support.annotation.NonNull;
import android.support.v4.util.Pair;

import com.ehi.enterprise.android.network.requests.reservation.ISO8601DateTypeAdapter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class EHIUrlBuilder {
	private static final String TAG = "EHIUrlBuilder";

	public static final String UTF_8 = "UTF-8";
	public static final String QUERY_SEPERATOR = "?";
	StringBuilder mStringBuilder;

	private List<Pair<String, String>> mValuePairList;
	public static final String NAME_VALUE_SEPERATOR = "=";
	private static final String PARAMETER_SEPARATOR = "&";


    public EHIUrlBuilder() {
		mStringBuilder = new StringBuilder();
		mValuePairList = new ArrayList<>();
	}

	public EHIUrlBuilder appendSubPath(@NonNull String subpath) {
        mStringBuilder.append(subpath.replace("+", "%20"));
		if (subpath.length() > 0
				&& subpath.charAt(subpath.length()-1) != '/') {
			mStringBuilder.append("/");
		}
		return this;
	}

	public EHIUrlBuilder appendLastSubPath(@NonNull String subpath) {
		mStringBuilder.append(subpath.replace("+", "%20"));
		return this;
	}

	public EHIUrlBuilder appendSubPath(double subpath) {
        mStringBuilder.append(String.valueOf(subpath));
		mStringBuilder.append("/");
		return this;
	}

	public EHIUrlBuilder addQueryParam(@NonNull String paramName, @NonNull Date date){
		return addQueryParam(paramName, ISO8601DateTypeAdapter.fromDateObject(date));
	}
	public EHIUrlBuilder addQueryParam(@NonNull String paramName, @NonNull String paramValue) {
        mValuePairList.add(new Pair<>(paramName, paramValue));
		return this;
	}

	public EHIUrlBuilder addQueryParam(@NonNull String paramName, double paramValue) {
        mValuePairList.add(new Pair<>(paramName, String.valueOf(paramValue)));
		return this;
	}

	public EHIUrlBuilder addQueryParam(@NonNull String paramName, long paramValue) {
        mValuePairList.add(new Pair<>(paramName, String.valueOf(paramValue)));
		return this;
	}

	public EHIUrlBuilder addQueryParam(@NonNull String paramName, boolean paramValue) {
        mValuePairList.add(new Pair<>(paramName, String.valueOf(paramValue)));
		return this;
	}

	public String build() {
		String requestUrl = mStringBuilder.toString();
		if (requestUrl.charAt(requestUrl.length() - 1) == '/') {
			requestUrl = requestUrl.substring(0, requestUrl.length() - 1);
			mStringBuilder = new StringBuilder(requestUrl);
		}
		if (mValuePairList.size() > 0) {
            StringBuilder builder = new StringBuilder();
            for (Pair<String, String> pair : mValuePairList) {
                if (builder.length() > 0) {
                    builder.append(PARAMETER_SEPARATOR);
                }
                else {
                    builder.append(QUERY_SEPERATOR);
                }
                String encodedName = pair.first;
                String encodedValue = pair.second != null ? pair.second : "";
                builder.append(encodedName);
                builder.append(NAME_VALUE_SEPERATOR);
                builder.append(encodedValue);
            }
			mStringBuilder.append(builder);
		}
		return mStringBuilder.toString();
	}


	@Override
	public String toString() {
		return build();
	}

}
