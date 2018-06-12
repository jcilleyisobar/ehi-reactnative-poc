package com.ehi.enterprise.android.ui.location;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.ehi.enterprise.android.models.location.EHICityLocation;
import com.ehi.enterprise.android.models.location.solr.EHISolrLocation;
import com.ehi.enterprise.android.network.interfaces.IApiCallback;
import com.ehi.enterprise.android.network.requests.location.solr.GetSolrLocationsByQueryRequest;
import com.ehi.enterprise.android.network.responses.ResponseWrapper;
import com.ehi.enterprise.android.network.responses.location.solr.EHIPostalCodeLocation;
import com.ehi.enterprise.android.network.responses.location.solr.GetSolrLocationsByQueryResponse;
import com.ehi.enterprise.android.ui.viewmodel.ManagersAccessViewModel;

import java.util.List;
import java.util.Map;
import java.util.Stack;

import io.dwak.reactor.ReactorDependency;
import io.dwak.reactor.ReactorVar;
import io.dwak.reactor.unbinder.annotation.AutoUnbindAll;

@AutoUnbindAll
public class SearchLocationsFragmentViewModel extends ManagersAccessViewModel {

	ReactorVar<List<EHICityLocation>> mSearchResult = new ReactorVar<>();
	ReactorVar<List<EHISolrLocation>> mAirportLocations = new ReactorVar<>();
	ReactorVar<List<EHISolrLocation>> mBranchLocations = new ReactorVar<>();
	ReactorVar<List<EHICityLocation>> mCityLocations = new ReactorVar<>();
	ReactorVar<List<EHIPostalCodeLocation>> mPostalCodeLocations = new ReactorVar<>();
	ReactorVar<ResponseWrapper> mErrorResponseWrapper = new ReactorVar<>();
	ReactorVar<Boolean> mShowNoResults = new ReactorVar<>(false);
	private ReactorDependency mFavoritesDependency = new ReactorDependency();

	private ReactorDependency mRecentLocations = new ReactorDependency();

	private @SearchLocationsActivity.Flow int mFlow = 0;

	public void searchForSolrLocation(@NonNull String query) {
		boolean isDropOff = mFlow == SearchLocationsActivity.FLOW_DROP_OFF_LOCATION_ONE_WAY;
		performRequest(new GetSolrLocationsByQueryRequest(query, isDropOff), new IApiCallback<GetSolrLocationsByQueryResponse>() {
			@Override
			public void handleResponse(ResponseWrapper<GetSolrLocationsByQueryResponse> response) {
				if (response.isSuccess()) {
					boolean hasAirportLocations;
					boolean hasBranchLocations;
					boolean hasCityLocations;
					boolean hasPostalCodeLocations;

					setAirportLocations(response.getData().getAirportLocationsList());
					hasAirportLocations = response.getData().getAirportLocationsList() != null
							&& !response.getData().getAirportLocationsList().isEmpty();

					setBranchLocations(response.getData().getBranchLocationsList());
					hasBranchLocations = response.getData().getBranchLocationsList() != null
							&& !response.getData().getBranchLocationsList().isEmpty();

					setCityLocations(response.getData().getCityLocations());
					hasCityLocations = response.getData().getCityLocations() != null
							&& !response.getData().getCityLocations().isEmpty();

					setPostalCodeLocations(response.getData().getPostalCodeLocations());
					hasPostalCodeLocations = response.getData().getPostalCodeLocations() != null
							&& !response.getData().getPostalCodeLocations().isEmpty();

					setShowNoResults(!(hasAirportLocations || hasBranchLocations || hasCityLocations || hasPostalCodeLocations));
				}
				else {
					setErrorResponseWrapper(response);
				}
			}
		});
	}

	@Nullable
	public List<EHICityLocation> getCityLocations() {
		if (mCityLocations != null) {
			return mCityLocations.getValue();
		}
		return null;
	}

	private void setCityLocations(@Nullable List<EHICityLocation> cityLocations) {
		mCityLocations.setValue(cityLocations);
	}

	@Nullable
	public List<EHIPostalCodeLocation> getPostalCodeLocations() {
		if (mPostalCodeLocations != null) {
			return mPostalCodeLocations.getValue();
		}

		return null;
	}

	public void setPostalCodeLocations(@Nullable List<EHIPostalCodeLocation> postalCodeLocations) {
		mPostalCodeLocations.setValue(postalCodeLocations);
	}

	@Nullable
	public List<EHISolrLocation> getAirportLocations() {
		if (mAirportLocations != null) {
			return mAirportLocations.getValue();
		}
		return null;
	}

	private void setAirportLocations(@Nullable List<EHISolrLocation> airportLocations) {
		mAirportLocations.setValue(airportLocations);
	}

	@Nullable
	public List<EHISolrLocation> getBranchLocations() {
		if (mBranchLocations != null) {
			return mBranchLocations.getValue();
		}
		return null;
	}

	private void setBranchLocations(@Nullable List<EHISolrLocation> branchLocations) {
		mBranchLocations.setValue(branchLocations);
	}

	@Nullable
	public Map<String, EHISolrLocation> getFavoriteLocations() {
		mFavoritesDependency.depend();
		return getManagers().getLocationManager().getFavoriteLocations();
	}

	@Nullable
	public Stack<EHISolrLocation> getRecentLocations() {
		mRecentLocations.depend();
		return getManagers().getLocationManager().getRecentLocations();
	}

	public void clearRecentLocations() {
		getManagers().getLocationManager().clearRecentLocations();
		mRecentLocations.changed();
	}

	@NonNull
	public Boolean getShowNoResults() {
		return mShowNoResults.getValue();
	}

	private void setShowNoResults(boolean showNoResults) {
		mShowNoResults.setValue(showNoResults);
	}

	public ResponseWrapper getErrorResponseWrapper() {
		return mErrorResponseWrapper.getValue();
	}

	public void setErrorResponseWrapper(ResponseWrapper errorResponseWrapper) {
		mErrorResponseWrapper.setValue(errorResponseWrapper);
	}

	public void clearQuery() {
		setShowNoResults(false);
		setErrorResponseWrapper(null);
	}

	public int getFlow() {
		return mFlow;
	}

	public void setFlow(int flow) {
		mFlow = flow;
	}

	public void addRecentLocation(EHISolrLocation ehiSolrLocation) {
		if (getManagers().getSettingsManager().isSearchHistoryEnabled()) {
			getManagers().getLocationManager().addRecentLocation(ehiSolrLocation);
		}
	}

	public void addFavoriteLocation(EHISolrLocation ehiSolrLocation) {
		getManagers().getLocationManager().addFavoriteLocation(ehiSolrLocation);
	}

	public void removeRecentLocation(EHISolrLocation locationToRemove) {
		getManagers().getLocationManager().removeRecentLocation(locationToRemove);
	}

	public void removeFavoriteLocation(EHISolrLocation locationToRemove) {
		getManagers().getLocationManager().removeFavoriteLocation(locationToRemove);
	}

	public boolean isFavoriteLocation(EHISolrLocation location) {
		return getManagers().getLocationManager().isFavoriteLocation(location.getPeopleSoftId());
	}

}