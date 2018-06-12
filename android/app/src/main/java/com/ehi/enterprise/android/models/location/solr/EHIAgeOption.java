package com.ehi.enterprise.android.models.location.solr;

import com.ehi.enterprise.android.models.EHIModel;
import com.ehi.enterprise.android.network.responses.location.solr.GetSolrAgeOptionsResponse;
import com.google.gson.annotations.SerializedName;

public class EHIAgeOption extends EHIModel{

    public static String age_or_older = "#{age_or_older}";
    public static String age_to = "#{age_to}";
    public static String age_or_younger = "#{age_or_younger}";

    @SerializedName("value")
    private int mValue;

    @SerializedName("label")
    private String mLabel;

    @SerializedName("selected")
    private boolean mSelected;

    public EHIAgeOption(int value, String label) {
        this.mValue = value;
        this.mLabel = label;
    }

    public EHIAgeOption() {
    }

    public EHIAgeOption(GetSolrAgeOptionsResponse getSolrAgeOptionsResponse) {
        mValue = getSolrAgeOptionsResponse.getValue();
        mLabel = getSolrAgeOptionsResponse.getLabel();
        mSelected = getSolrAgeOptionsResponse.isSelected();
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EHIAgeOption)) return false;

        EHIAgeOption that = (EHIAgeOption) o;

        if (mValue != that.mValue) return false;
        if (mSelected != that.mSelected) return false;
        return !(mLabel != null ? !mLabel.equals(that.mLabel) : that.mLabel != null);

    }

    @Override
    public int hashCode() {
        int result = mValue;
        result = 31 * result + (mLabel != null ? mLabel.hashCode() : 0);
        result = 31 * result + (mSelected ? 1 : 0);
        return result;
    }
}
