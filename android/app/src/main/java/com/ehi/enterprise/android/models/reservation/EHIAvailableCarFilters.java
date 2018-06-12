package com.ehi.enterprise.android.models.reservation;

import android.content.res.Resources;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.models.EHIModel;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class EHIAvailableCarFilters extends EHIModel {

    public static final String CAR_CLASS = "CLASS";
    public static final String CAR_PASSENGERS = "PASSENGERS";
    public static final String CAR_TRANSMISSION = "TRANSMISSION";
    public static final String CAR_FUEL = "FUEL";

    @SerializedName("filter_description")
    private String mFilterDescription;

    @SerializedName("filter_code")
    private String mFilterCode;

    @SerializedName("filter_values")
    private List<EHIFilterValue> mFilterValues;

    public String getFilterDescription() {
        return mFilterDescription;
    }

    public void setFilterDescription(String filterDescription) {
        mFilterDescription = filterDescription;
    }

    public String getFilterCode() {
        return mFilterCode;
    }

    public void setFilterCode(String filterCode) {
        mFilterCode = filterCode;
    }

    public List<EHIFilterValue> getFilterValues() {
        return mFilterValues;
    }

    public void setFilterValues(List<EHIFilterValue> filterValues) {
        mFilterValues = filterValues;
    }

    public void removeFilter(EHIFilterValue value) {
        for (int i = 0; i < mFilterValues.size(); i++) {
            if (value.getCode().equals(mFilterValues.get(i).getCode())) {
                mFilterValues.get(i).setActive(false);

            }
        }
    }

    public void addFilter(EHIFilterValue value) {
        if (!getFilterCode().equals(CAR_CLASS)) {
            clearFilters();
        }

        for (int i = 0; i < mFilterValues.size(); i++) {
            if (value.getCode().equals(mFilterValues.get(i).getCode())) {
                mFilterValues.get(i).setActive(true);
            }
        }
    }

    public void clearFilters() {
        for (int i = 0; i < mFilterValues.size(); i++) {
            mFilterValues.get(i).setActive(false);
        }
    }

    public int activeFilterCount() {
        return getActiveFilters().size();
    }

    public List<EHIFilterValue> getActiveFilters() {
        List<EHIFilterValue> values = new ArrayList<>(mFilterValues.size());

        for (int i = 0; i < mFilterValues.size(); i++) {
            if (mFilterValues.get(i).isActive()) {
                values.add(mFilterValues.get(i));
            }
        }
        return values;
    }


    public boolean isNotCarTypeFilter() {
        return !getFilterCode().equalsIgnoreCase(CAR_CLASS);
    }

    public String getDefaultOption(Resources resources) {
        if (getFilterCode().equalsIgnoreCase(CAR_PASSENGERS)) {
            return resources.getString(R.string.class_select_filter_passenger_capacity_all);
        } else if (getFilterCode().equalsIgnoreCase(CAR_TRANSMISSION)) {
            return resources.getString(R.string.class_select_filter_transmission_all);
        }
        return "";
    }

    public boolean sortable() {
        return getFilterCode().equalsIgnoreCase(CAR_PASSENGERS);
    }


    public boolean isValid() {
        return !getFilterCode().equalsIgnoreCase(CAR_FUEL);
    }


    public static String getFilterText(List<EHIAvailableCarFilters> availableCarFilters) {
        StringBuilder builder = new StringBuilder();
        List<EHIFilterValue> values = EHIAvailableCarFilters.getActiveFilters(availableCarFilters);

        for (int i = 0; i < values.size(); i++) {
            builder.append(values.get(i).getDescription());
            if (i + 1 != values.size()) {
                builder.append(", ");
            }
        }
        return builder.toString();
    }

    public static List<EHIFilterValue> getActiveFilters(List<EHIAvailableCarFilters> availableCarFilters) {
        List<EHIFilterValue> values = new ArrayList<>(2);

        for (int i = 0; i < availableCarFilters.size(); i++) {
            values.addAll(availableCarFilters.get(i).getActiveFilters());
        }
        return values;
    }


}
