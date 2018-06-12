package com.ehi.enterprise.android.models.reservation;

import com.ehi.enterprise.android.models.EHIModel;
import com.google.gson.annotations.SerializedName;

public class EHIAirlineDetails extends EHIModel {

    public static final String WALK_IN_CODE = "WU";
    public static final String OTHER_CODE = "XX";

    @SerializedName("code")
    private String mCode;

    @SerializedName("description")
    private String mDescription;

    public String getCode() {
        return mCode;
    }

    public String getDescription() {
        return mDescription;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final EHIAirlineDetails that = (EHIAirlineDetails) o;

        if (mCode != null ? !mCode.equals(that.mCode) : that.mCode != null) return false;
        return !(mDescription != null ? !mDescription.equals(that.mDescription) : that.mDescription != null);

    }

    @Override
    public int hashCode() {
        int result = mCode != null ? mCode.hashCode() : 0;
        result = 31 * result + (mDescription != null ? mDescription.hashCode() : 0);
        return result;
    }
}
