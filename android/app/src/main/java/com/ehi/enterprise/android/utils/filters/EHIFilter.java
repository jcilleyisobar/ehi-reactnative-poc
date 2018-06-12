package com.ehi.enterprise.android.utils.filters;

public interface EHIFilter<FilterType> {
	<T> T applyFilter(FilterType filter);

	String getTitle();

    int getID();

}