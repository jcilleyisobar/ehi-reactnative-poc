package com.ehi.enterprise.android.ui.location.interfaces;

import com.ehi.enterprise.android.models.location.EHICityLocation;
import com.ehi.enterprise.android.models.location.solr.EHISolrLocation;
import com.ehi.enterprise.android.network.responses.location.solr.EHIPostalCodeLocation;

public interface OnSolrLocationInfoClickListener {

    void onShowLocationDetails(EHISolrLocation location);

    void onSelectLocation(EHISolrLocation location);

    void onShowCityLocation(EHICityLocation cityLocation);

    void onShowPostalLocation(EHIPostalCodeLocation postalCodeLocation);
}