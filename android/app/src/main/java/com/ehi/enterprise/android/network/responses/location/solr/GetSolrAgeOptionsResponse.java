package com.ehi.enterprise.android.network.responses.location.solr;

import com.ehi.enterprise.android.network.responses.BaseResponse;
import com.google.gson.annotations.SerializedName;

public class GetSolrAgeOptionsResponse extends BaseResponse {
    @SerializedName("value")
    private int mValue;

    @SerializedName("label")
    private String mLabel;

    @SerializedName("selected")
    private boolean mSelected;

    public GetSolrAgeOptionsResponse() {
    }

    public GetSolrAgeOptionsResponse(int value, String label) {
        mValue = value;
        mLabel = label;
    }

    public int getValue() {
        return mValue;
    }

    public void setValue(int value) {
        mValue = value;
    }

    public String getLabel() {
        return mLabel;
    }

    public void setLabel(String label) {
        mLabel = label;
    }

    public boolean isSelected() {
        return mSelected;
    }

    public void setSelected(boolean selected) {
        mSelected = selected;
    }
}
