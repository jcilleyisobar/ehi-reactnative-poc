package com.ehi.enterprise.android.ui.location.interfaces;

import com.google.android.m4b.maps.model.LatLng;

public interface ISearchByLocationDelegate {

	void searchByLocation(LatLng latLong, int radius);
}
