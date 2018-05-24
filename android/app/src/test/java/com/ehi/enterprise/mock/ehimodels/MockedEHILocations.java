package com.ehi.enterprise.mock.ehimodels;

import com.ehi.enterprise.android.models.location.solr.EHISolrLocation;
import com.ehi.enterprise.helpers.MockableObject;

public class MockedEHILocations {
    public static MockableObject<EHISolrLocation> newInstance(){


        return new MockableObject<EHISolrLocation>(EHISolrLocation.class);
    }
}
