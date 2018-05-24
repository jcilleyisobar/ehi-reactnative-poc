package com.ehi.enterprise.android.utils.filters;

import com.ehi.enterprise.android.models.reservation.EHIAvailableCarFilters;
import com.ehi.enterprise.android.models.reservation.EHICarFilter;
import com.ehi.enterprise.android.models.reservation.EHIFilterValue;

import java.util.ArrayList;
import java.util.List;

public class EHIFilterImpl implements EHIFilter<EHICarFilter> {

    private final EHIFilterValue mFilter;

    public EHIFilterImpl(EHIFilterValue filter) {
        mFilter = filter;
    }

    @Override
    public Boolean applyFilter(EHICarFilter car) {
        return mFilter.getCode().equalsIgnoreCase(car.getFilterCode());
    }

    @Override
    public String getTitle() {
        return mFilter.getDescription();
    }

    @Override
    public int getID() {
        return Integer.parseInt(mFilter.getCode());
    }

    public static List<EHIFilterImpl> transformToFilter(EHIAvailableCarFilters filters){

        List<EHIFilterImpl> filterImpl = new ArrayList<>(filters.getFilterValues().size());
        EHIFilterValue value;
        for(int i =0; i < filters.getFilterValues().size(); i++){
            value = filters.getFilterValues().get(i);
            filterImpl.add(new EHIFilterImpl(value));
        }
        return filterImpl;
    }
}
