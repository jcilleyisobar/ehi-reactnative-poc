package com.ehi.enterprise.android.ui.location;

import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.SparseArray;
import android.view.View;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.models.location.EHILatLng;
import com.ehi.enterprise.android.models.location.EHILocation;
import com.ehi.enterprise.android.models.location.solr.EHISolrLocation;
import com.ehi.enterprise.android.network.interfaces.IApiCallback;
import com.ehi.enterprise.android.network.requests.location.solr.GetSolrLocationsByCoordRequest;
import com.ehi.enterprise.android.network.responses.ResponseWrapper;
import com.ehi.enterprise.android.network.responses.location.solr.GetSolrLocationsResponse;
import com.ehi.enterprise.android.ui.viewmodel.ManagersAccessViewModel;
import com.ehi.enterprise.android.utils.DistanceUtils;
import com.ehi.enterprise.android.utils.ListUtils;
import com.ehi.enterprise.android.utils.filters.EHIFilter;
import com.ehi.enterprise.android.utils.reactor_extensions.viewstate.ReactorViewState;
import com.google.android.m4b.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import io.dwak.reactor.ReactorVar;
import io.dwak.reactor.unbinder.annotation.AutoUnbindAll;

@AutoUnbindAll
public class LocationsOnMapViewModel extends ManagersAccessViewModel {

    ReactorVar<List<EHISolrLocation>> mSolrLocations = new ReactorVar<>();
    ReactorVar<ResponseWrapper> mErrorWrapper = new ReactorVar<>();
    ReactorVar<String> mLocationName = new ReactorVar<>();
    ReactorVar<EHILatLng> mLatLong = new ReactorVar<>();
    ReactorVar<Date> mPickupDate = new ReactorVar<>();
    ReactorVar<Date> mReturnDate = new ReactorVar<>();
    ReactorVar<Date> mPickupTime = new ReactorVar<>();
    ReactorVar<Date> mReturnTime = new ReactorVar<>();
    ReactorViewState locationFilterView = new ReactorViewState();
    ReactorViewState filterTipView = new ReactorViewState();
    ReactorVar<Boolean> mShowNoFilteredLocationsDialog = new ReactorVar<>(false);
    ReactorVar<Boolean> mIsLoading = new ReactorVar<>(false);

    private SparseArray<EHIFilter> mFilters = new SparseArray<>();

    private long mSearchRadius;

    private long mScreenAreaRadius = 0;
    private boolean mNalmo = false;

    private @SearchLocationsActivity.Flow int mFlow;

    private boolean mSearchingNearby = false;
    private long mMapSetRadius;
    private boolean mIsModify;
    private boolean mIsFromLDT;

    private boolean mSkipOnSearchOnResume = false;

    @Override
    public void onApiServiceConnected() {
        super.onApiServiceConnected();
    }

    @Override
    public void onAttachToView() {
        super.onAttachToView();
        setLocationFilterViewVisibility();
    }

    @Nullable
    public List<EHISolrLocation> getSolrLocations() {
        if (mSolrLocations.getValue() != null) {
            return mSolrLocations.getValue();
        }
        return null;
    }

    public boolean containsNalmo() {
        return mNalmo && getFilters().size() == 0;
    }

    public void setSolrLocations(@Nullable List<EHISolrLocation> solrLocations) {

        final Location current = getManagers().getLocationApiManager().getLastCurrentLocation();
        if (solrLocations != null) {
            for (EHISolrLocation location : solrLocations) {
                if (mSearchingNearby
                        && current != null) {
                    location.setDistanceToUserLocation(DistanceUtils.getFormattedDistanceToLocation(current,
                            location.getLocation(),
                            getResources().getString(R.string.metrics_miles),
                            getResources().getString(R.string.metrics_kilometers)));
                }
            }
            if (current != null && mSearchingNearby) {
                Collections.sort(solrLocations, new Comparator<EHISolrLocation>() {
                    @Override
                    public int compare(EHISolrLocation lhs, EHISolrLocation rhs) {
                        if (lhs.getCalculatedDistance() == null) {
                            return 1;
                        }
                        if (rhs.getCalculatedDistance() == null) {
                            return -1;
                        }
                        return Float.valueOf(lhs.getLocation().distanceTo(current))
                                .compareTo(rhs.getLocation().distanceTo(current));
                    }
                });
            }
            mSolrLocations.setValue(solrLocations);
        } else {
            mSolrLocations.setValue(new ArrayList<EHISolrLocation>());
        }
    }

    public void resetFilters() {
        mFilters = new SparseArray<>();
        if (mLatLong.getDependency() != null) {
            mLatLong.getDependency().changed();
        }
        resetDates();
    }

    private void resetDates() {
        mPickupDate.setValue(null);
        mReturnDate.setValue(null);
        mPickupTime.setValue(null);
        mReturnTime.setValue(null);
    }

    public boolean isDropOff() {
        return mFlow == SearchLocationsActivity.FLOW_DROP_OFF_LOCATION_ONE_WAY;
    }

    public ResponseWrapper getErrorResponseWrapper() {
        return mErrorWrapper.getValue();
    }

    public void setErrorWrapper(ResponseWrapper errorWrapper) {
        mErrorWrapper.setValue(errorWrapper);
    }

    /**
     * Utilize method wrappers to decouple android dependencies without effecting consumers of the VM
     *
     * @param latLng
     */
    public void searchSolrLocationsForCoordinates(@NonNull LatLng latLng) {
        searchSolrLocationsForCoordinates(new EHILatLng(latLng));
    }

    public void searchSolrLocationsForCoordinates(@NonNull EHILatLng latLng) {
        if (mSkipOnSearchOnResume) {
            mSkipOnSearchOnResume = false;
            return;
        }

        mIsLoading.setValue(true);

        performRequest(new GetSolrLocationsByCoordRequest(latLng.getLatitude(), latLng.getLongitude(), mScreenAreaRadius, isDropOff(),
                getFilters(), getPickupDateForFilter(), getPickupTimeForFilter(), getDropoffDateForFilter(), getDropoffTimeForFilter()), new IApiCallback<GetSolrLocationsResponse>() {
            @Override
            public void handleResponse(ResponseWrapper<GetSolrLocationsResponse> response) {
                if (response.isSuccess()) {
                    mNalmo = response.getData().getBrands() != null &&
                            (response.getData().getBrands().contains(EHILocation.BRAND_ALAMO) ||
                                    response.getData().getBrands().contains(EHILocation.BRAND_NATIONAL));
                    mSearchRadius = response.getData().getRadiusInMeters();
                    if (areFiltersApplied() && ListUtils.isEmpty(response.getData().getSolrLocationList())) {
                        mShowNoFilteredLocationsDialog.setRawValue(true);
                    }
                    setSolrLocations(response.getData().getSolrLocationList());
                    setShowNoFilteredLocationsDialog(mShowNoFilteredLocationsDialog.getRawValue());
                } else {
                    mErrorWrapper.setValue(response);
                }
                mIsLoading.setValue(false);
            }
        });
    }

    public void setFlow(@SearchLocationsActivity.Flow int flow) {
        mFlow = flow;
    }

    public void setSearchingNearby(boolean searchingNearby) {
        mSearchingNearby = searchingNearby;
    }

    public boolean isSearchingNearby() {
        return mSearchingNearby;
    }

    public void setLocationName(String name) {
        mLocationName.setValue(name);
    }

    public String getLocationName() {
        return mLocationName.getValue();
    }

    public SparseArray<EHIFilter> getFilters() {
        return mFilters;
    }

    public void setFilters(SparseArray<EHIFilter> filters) {
        mFilters = filters;
    }


    public void setLatLong(LatLng center) {
        mLatLong.setValue(new EHILatLng(center));
    }

    public LatLng getLatLong() {
        return mLatLong.getValue() == null ? null : mLatLong.getValue().getLatLng();
    }

    public long getSearchRadius() {
        return mSearchRadius;
    }

    public void setScreenAreaRadius(long screenAreaRadius) {
        mMapSetRadius = screenAreaRadius;
        mScreenAreaRadius = screenAreaRadius;
    }

    public long getMapSetRadius() {
        return mMapSetRadius;
    }

    public void addRecentLocation(EHISolrLocation location) {
        if (getManagers().getSettingsManager().isSearchHistoryEnabled()) {
            getManagers().getLocationManager().addRecentLocation(location);
        }
    }

    public String getValidAlamoReservationUrl() {
        return getManagers().getSupportInfoManager().getSupportInfoForCurrentLocale().getValidAlamoReservationUrl();
    }

    public String getValidNationalReservationUrl() {
        return getManagers().getSupportInfoManager().getSupportInfoForCurrentLocale().getValidNationalReservationUrl();
    }

    public boolean isModify() {
        return mIsModify;
    }

    public void setIsModify(boolean isModify) {
        mIsModify = isModify;
    }

    public void showProgress(boolean show) {
        mIsLoading.setValue(show);
    }

    public void setPickupDate(Date pickupDate) {
        this.mPickupDate.setValue(pickupDate);
        if (pickupDate == null) {
            this.mPickupTime.setValue(null);
        }
    }

    public void setDropoffDate(Date returnDate) {
        this.mReturnDate.setValue(returnDate);
        if (returnDate == null) {
            this.mReturnTime.setValue(null);
        }
    }

    public Date getPickupDate() {
        return mPickupDate.getValue();
    }

    public Date getDropoffDate() {
        return mReturnDate.getValue();
    }

    public void setPickupTime(Date pickupTime) {
        if (mPickupDate.getRawValue() != null) {
            mPickupTime.setValue(pickupTime);
        }
    }

    public void setDropoffTime(Date returnTime) {
        if (mReturnDate.getRawValue() != null) {
            mReturnTime.setValue(returnTime);
        }
    }

    public Date getPickupTime() {
        return mPickupTime.getValue();
    }

    public Date getDropoffTime() {
        return mReturnTime.getValue();
    }

    protected Date getPickupDateForFilter() {
        if (mFlow != SearchLocationsActivity.FLOW_DROP_OFF_LOCATION_ONE_WAY) {
            return getPickupDate();
        }
        return null;
    }

    protected Date getPickupTimeForFilter() {
        if (mFlow != SearchLocationsActivity.FLOW_DROP_OFF_LOCATION_ONE_WAY) {
            return getPickupTime();
        }
        return null;
    }

    protected Date getDropoffDateForFilter() {
        if (mFlow != SearchLocationsActivity.FLOW_PICKUP_LOCATION_ONE_WAY) {
            return getDropoffDate();
        }
        return null;
    }

    protected Date getDropoffTimeForFilter() {
        if (mFlow != SearchLocationsActivity.FLOW_PICKUP_LOCATION_ONE_WAY) {
            return getDropoffTime();
        }
        return null;
    }

    public boolean areFiltersApplied() {
        return getPickupTime() != null ||
                getDropoffTime() != null ||
                getPickupDate() != null ||
                getDropoffDate() != null ||
                (getFilters() != null && getFilters().size() > 0);
    }

    public void setLocationFilterViewVisibility() {
        if (areFiltersApplied()) {
            locationFilterView.setVisibility(View.VISIBLE);
        } else {
            locationFilterView.setVisibility(View.GONE);
        }
    }

    public void setFilterTipViewVisibility() {
        if (getManagers().getLocalDataManager().isFirstTimeOnMapScreen() ||
                (mIsFromLDT && getPickupDate() == null && getDropoffDate() == null)) {
            filterTipView.setVisibility(View.VISIBLE);
        } else {
            filterTipView.setVisibility(View.GONE);
        }
    }

    public Boolean needShowNoFilteredLocationsDialog() {
        return mShowNoFilteredLocationsDialog.getValue();
    }

    public boolean willShowNoFilteredLocationsDialog() {
        return mShowNoFilteredLocationsDialog.getRawValue();
    }

    public void setShowNoFilteredLocationsDialog(boolean showNoFilteredLocationsDialog) {
        mShowNoFilteredLocationsDialog.setValue(showNoFilteredLocationsDialog);
    }

    public void setSkipOneSearch(boolean skip) {
        mSkipOnSearchOnResume = skip;
    }

    public boolean isLoading() {
        return mIsLoading.getValue();
    }

    public void closeFilterTip() {
        filterTipView.setVisibility(View.GONE);
    }

    public void setIsFromLDT(boolean value) {
        mIsFromLDT = value;
    }
}
