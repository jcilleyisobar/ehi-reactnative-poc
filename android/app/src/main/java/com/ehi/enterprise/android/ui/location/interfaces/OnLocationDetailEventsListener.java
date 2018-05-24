package com.ehi.enterprise.android.ui.location.interfaces;

import com.ehi.enterprise.android.models.location.EHILocation;

public interface OnLocationDetailEventsListener {

    void onFavoriteStateChanged();

    void onCallLocation(String phoneNumber);

    void onShowDirection();

    void onShowDirectionFromTerminal();

    void onShowLocationDetails(EHILocation location);

    void onShowAfterHoursDialog();
}
