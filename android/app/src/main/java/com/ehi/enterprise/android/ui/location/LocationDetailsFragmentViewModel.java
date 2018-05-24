package com.ehi.enterprise.android.ui.location;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Pair;
import android.view.View;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.app.EHIStringToken;
import com.ehi.enterprise.android.models.location.EHILocation;
import com.ehi.enterprise.android.models.location.solr.EHISolrLocation;
import com.ehi.enterprise.android.network.interfaces.IApiCallback;
import com.ehi.enterprise.android.network.requests.location.GetLocationByIdRequest;
import com.ehi.enterprise.android.network.requests.location.solr.GetSolrHoursByLocationIdRequest;
import com.ehi.enterprise.android.network.responses.ResponseWrapper;
import com.ehi.enterprise.android.network.responses.location.GetLocationDetailsResponse;
import com.ehi.enterprise.android.network.responses.location.solr.GetSolrHoursResponse;
import com.ehi.enterprise.android.ui.location.widgets.LocationDetailsConflictMessageViewState;
import com.ehi.enterprise.android.ui.viewmodel.ManagersAccessViewModel;
import com.isobar.android.tokenizedstring.TokenizedString;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import io.dwak.reactor.ReactorVar;
import io.dwak.reactor.unbinder.annotation.AutoUnbindAll;

@AutoUnbindAll
public class LocationDetailsFragmentViewModel extends ManagersAccessViewModel {

    private static final String TAG = LocationDetailsFragmentViewModel.class.getSimpleName();
    private static final SimpleDateFormat mDateFormat = new SimpleDateFormat("MMM, dd", Locale.getDefault());
    private static final SimpleDateFormat mTimeformat = new SimpleDateFormat("KK:mm aaa", Locale.getDefault());

    ReactorVar<EHILocation> mSelectedLocation = new ReactorVar<>();
    ReactorVar<ResponseWrapper> mErrorWrapper = new ReactorVar<>();

    private Date mPickupDate;
    private Date mReturnDate;

    LocationDetailsConflictMessageViewState conflictMessageView = new LocationDetailsConflictMessageViewState();

    private EHISolrLocation mSolrLocation;

    public void setPickupDate(Date pickupDate) {
        this.mPickupDate = pickupDate;
    }

    public void setDropoffDate(Date returnDate) {
        this.mReturnDate = returnDate;
    }

    @Override
    public void onAttachToView() {
        super.onAttachToView();
        setupConflictMessage();
    }

    public boolean shouldShowConflictMessage() {
        final boolean areFilterDatesAvailable = mPickupDate != null || mReturnDate != null;
        final boolean isLocationInvalid = mSolrLocation.isInvalidForDropoff() || mSolrLocation.isInvalidForPickup();
        return isLocationInvalid && areFilterDatesAvailable;
    }

    public void setupConflictMessage() {
        final String title = getTitle();
        if (shouldShowConflictMessage()) {
            conflictMessageView.setVisibility(View.VISIBLE);
            if (title != null && !title.isEmpty()) {
                conflictMessageView.title().setValue(title);
            }
            setSubtitle();
            setExtraSubtitle();
        } else {
            conflictMessageView.setVisibility(View.GONE);
        }
    }

    public String getTitle() {
        String formattedTitle = "";
        if (mSolrLocation.isInvalidForPickup() && mSolrLocation.isInvalidForDropoff()) {
            final StringBuilder closedOnText = new StringBuilder();
            closedOnText.append(getResources().getString(R.string.locations_map_closed_pickup))
                    .append(" & ")
                    .append(getResources().getString(R.string.locations_map_closed_return));
            formattedTitle = new TokenizedString.Formatter<EHIStringToken>(getResources())
                    .formatString(R.string.locations_map_closed_your_pickup)
                    .addTokenAndValue(EHIStringToken.CLOSED_ON, closedOnText)
                    .format().toString();
        } else if (mSolrLocation.isInvalidForDropoff()) {
            formattedTitle = new TokenizedString.Formatter<EHIStringToken>(getResources())
                    .formatString(R.string.locations_map_closed_your_pickup)
                    .addTokenAndValue(EHIStringToken.CLOSED_ON, getResources().getString(R.string.locations_map_closed_return))
                    .format().toString();
        } else if (mSolrLocation.isInvalidForPickup()) {
            formattedTitle = new TokenizedString.Formatter<EHIStringToken>(getResources())
                    .formatString(R.string.locations_map_closed_your_pickup)
                    .addTokenAndValue(EHIStringToken.CLOSED_ON, getResources().getString(R.string.locations_map_closed_pickup))
                    .format().toString();
        }
        return formattedTitle;
    }

    public void setSubtitle() {
        if (mSolrLocation.isInvalidForPickup()) {
            conflictMessageView.subtitle().setValue(new Pair<>(mPickupDate, mSolrLocation.getPickupValidity()));
        } else if (mSolrLocation.isInvalidForDropoff()) {
            conflictMessageView.subtitle().setValue(new Pair<>(mReturnDate, mSolrLocation.getDropoffValidity()));
        }
    }

    public void setExtraSubtitle() {
        if (mSolrLocation.isInvalidForPickup() && mSolrLocation.isInvalidForDropoff()) {
            conflictMessageView.extraSubtitle().setValue(new Pair<>(mReturnDate, mSolrLocation.getDropoffValidity()));
        }
    }

    @Nullable
    public EHILocation getSelectedLocation() {
        return mSelectedLocation.getValue();
    }

    /**
     * Will use solr location with favorite manager
     */
    public void setSolrLocation(@NonNull EHISolrLocation solrLocation) {
        mSolrLocation = solrLocation;

    }

    public void loadSelectedLocation(@NonNull EHILocation selectedLocation) {
        setSelectedLocation(selectedLocation);
        requestLocationDetails();
    }

    public void setSelectedLocation(@NonNull EHILocation selectedLocation) {
        mSelectedLocation.setValue(selectedLocation);
    }

    public void setEHILocationToFavorite(@NonNull EHILocation selectedLocation) {
        mSolrLocation = EHISolrLocation.fromLocation(selectedLocation);
    }

    @Nullable
    public ResponseWrapper getErrorWrapper() {
        return mErrorWrapper.getValue();
    }

    public void setErrorWrapper(ResponseWrapper errorWrapper) {
        mErrorWrapper.setValue(errorWrapper);
    }

    private void requestLocationDetails() {
        performRequest(new GetLocationByIdRequest(mSelectedLocation.getValue().getId()), new IApiCallback<GetLocationDetailsResponse>() {
            @Override
            public void handleResponse(ResponseWrapper<GetLocationDetailsResponse> response) {
                if (response.isSuccess()) {
                    if (response.getData() != null) {
                        EHILocation location = response.getData().getLocation();
                        mSelectedLocation.setValue(location);
                        getWorkingHours();
                    }
                } else {
                    setErrorWrapper(response);
                }
            }
        });
    }

    private void getWorkingHours() {
        Calendar days = Calendar.getInstance();
        days.add(Calendar.DAY_OF_MONTH, 8);
        performRequest(new GetSolrHoursByLocationIdRequest(mSelectedLocation.getRawValue().getId(), new Date(), days.getTime()),
                new IApiCallback<GetSolrHoursResponse>() {
                    @Override
                    public void handleResponse(ResponseWrapper<GetSolrHoursResponse> response) {
                        if (response.isSuccess()) {
                            if (response.getData() != null) {
                                EHILocation location = getSelectedLocation();
                                location.setWorkingHours(response.getData().getDaysInfo());
                                mSelectedLocation.setValue(location);
                            }
                        } else {
                            setErrorWrapper(response);
                        }
                    }
                });
    }

    /**
     * Favorite/unfavorite location depending on current its state
     */
    public void updateFavoriteState() {
        EHILocation location = mSelectedLocation.getValue();
        if (location.isFavorite()) {
            getManagers().getLocationManager().removeFavoriteLocation(mSolrLocation);
        } else {
            getManagers().getLocationManager().addFavoriteLocation(mSolrLocation);
        }
        mSelectedLocation.setValue(location);
    }

    public EHISolrLocation getSolrLocation() {
        return mSolrLocation;
    }

    public boolean shouldShowAfterHoursDropoff() {
        return mSolrLocation.isDropoffAfterHours();
    }
}
