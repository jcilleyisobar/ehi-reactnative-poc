package com.ehi.enterprise.android.models;

import com.ehi.enterprise.android.models.location.solr.EHISolrLocationValidity;

import org.junit.Test;

import static junit.framework.Assert.assertTrue;

public class EHISolrLocationTest {
    @Test
    public void testIsInvalidLocationAllDay() {
        EHISolrLocationValidity solrLocation = new EHISolrLocationValidity();
        solrLocation.setValidityType(EHISolrLocationValidity.INVALID_ALL_DAY);
        assertTrue(solrLocation.isLocationInvalid());
    }

    @Test
    public void testIsInvalidLocationAtTime() {
        EHISolrLocationValidity solrLocation = new EHISolrLocationValidity();
        solrLocation.setValidityType(EHISolrLocationValidity.INVALID_AT_THAT_TIME);
        assertTrue(solrLocation.isLocationInvalid());
    }
}
