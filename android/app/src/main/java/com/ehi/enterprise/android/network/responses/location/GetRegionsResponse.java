package com.ehi.enterprise.android.network.responses.location;

import android.support.annotation.Nullable;

import com.ehi.enterprise.android.models.profile.EHIRegion;
import com.ehi.enterprise.android.network.responses.BaseResponse;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class GetRegionsResponse extends BaseResponse {

	@SerializedName("regions")
	private Map<String, EHIRegion> mRegionsMap;

    private List<EHIRegion> mRegions;

    public List<EHIRegion> getRegions() {
        if (mRegions == null) {
            return Collections.emptyList();
        }
        return mRegions;
    }

    private void setRegions(List<EHIRegion> regions) {
        mRegions = regions;
    }

    public void toOrderedList() {
        if (mRegionsMap != null) {
            final List<EHIRegion> list = new ArrayList<>(mRegionsMap.values());
            Collections.sort(list);
            setRegions(list);
        }
    }
}
