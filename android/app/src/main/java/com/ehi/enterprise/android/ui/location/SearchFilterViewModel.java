package com.ehi.enterprise.android.ui.location;

import android.content.res.Resources;
import android.util.SparseArray;

import com.ehi.enterprise.android.models.location.solr.EHISolrLocation;
import com.ehi.enterprise.android.network.interfaces.IApiCallback;
import com.ehi.enterprise.android.network.requests.location.solr.GetSolrLocationsByCoordRequest;
import com.ehi.enterprise.android.network.responses.ResponseWrapper;
import com.ehi.enterprise.android.network.responses.location.solr.GetSolrLocationsResponse;
import com.ehi.enterprise.android.ui.viewmodel.ManagersAccessViewModel;
import com.ehi.enterprise.android.utils.filters.EHIFilter;
import com.ehi.enterprise.android.utils.filters.EHIFilterList;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.dwak.reactor.ReactorVar;
import io.dwak.reactor.unbinder.annotation.AutoUnbindAll;

@AutoUnbindAll
public class SearchFilterViewModel extends ManagersAccessViewModel {

    private ReactorVar<SparseArray<EHIFilter>> mActiveFilters = new ReactorVar<>(new SparseArray<EHIFilter>());
    private ReactorVar<ResponseWrapper> mErrorWrapper = new ReactorVar<>();
    private Date mPickupDate;
    private Date mReturnDate;

    private ReactorVar<Boolean> mDateUpdate = new ReactorVar<>(false);

    private Date mPickupTime;
    private Date mReturnTime;

    public void addFilter(int key, EHIFilter filter) {
        if (mActiveFilters.getValue().indexOfKey(key) >= 0) {
            return;
        }
        mActiveFilters.getValue().put(key, filter);
        mActiveFilters.getDependency().changed();
    }

    public void setFilters(ArrayList<Integer> filterTypes, Resources resource) {
        setFilters(EHIFilterList.translateToSparse(filterTypes, resource));
    }

    public void setFilters(SparseArray<EHIFilter> filters) {
        mActiveFilters.setValue(filters);
    }

    public void removeFilter(int key) {
        mActiveFilters.getValue().remove(key);
        mActiveFilters.getDependency().changed();
    }

    private void clearDatesAndTimes() {
        resetPickupDate();
        resetPickupTime();
        resetReturnDate();
        resetReturnTime();
        mDateUpdate.setValue(true);
    }

    public boolean isFilter(int key) {
        return mActiveFilters.getValue().indexOfKey(key) >= 0;
    }

    public ArrayList<Integer> getFilterTypes() {
        return EHIFilterList.fetchActiveFilterTypes(mActiveFilters.getValue());
    }

    public boolean applyFilters() {
        return true;
    }

    public SparseArray<EHIFilter> getFilters() {
        return mActiveFilters.getValue();
    }

    public ResponseWrapper getErrorWrapper() {
        return mErrorWrapper.getValue();
    }

    public void setErrorWrapper(ResponseWrapper errorWrapper) {
        mErrorWrapper.setValue(errorWrapper);
    }

    public void clearFilters() {
        mActiveFilters.getValue().clear();
        mActiveFilters.getDependency().changed();
        clearDatesAndTimes();
    }

    public void setPickupDate(Date pickupDate) {
        this.mPickupDate = pickupDate;
        mDateUpdate.setValue(true);
    }

    public void setReturnDate(Date returnDate) {
        this.mReturnDate = returnDate;
        mDateUpdate.setValue(true);
    }

    public Date getPickupDate() {
        return mPickupDate;
    }

    public Date getReturnDate() {
        return mReturnDate;
    }

    public boolean isDateUpdated() {
        return mDateUpdate.getValue();
    }

    public void resetPickupDate() {
        mPickupDate = null;
    }


    public void resetReturnDate() {
        mReturnDate = null;
    }

    public void resetPickupTime() {
        mPickupTime = null;
    }

    public void resetReturnTime() {
        mReturnTime = null;
    }

    public void setPickupTime(Date pickupTime) {
        this.mPickupTime = pickupTime;
        mDateUpdate.setValue(true);
    }

    public void setReturnTime(Date returnTime) {
        this.mReturnTime = returnTime;
        mDateUpdate.setValue(true);
    }

    public Date getPickupTime() {
        return mPickupTime;
    }

    public Date getReturnTime() {
        return mReturnTime;
    }
}