package com.ehi.enterprise.android.ui.reservation.interfaces;

import com.ehi.enterprise.android.models.location.EHILocation;
import com.ehi.enterprise.android.models.reservation.EHIExtraItem;
import com.ehi.enterprise.android.models.reservation.EHIExtras;
import com.ehi.enterprise.android.models.reservation.EHIKeyFactsPolicy;

import java.util.List;

public interface IKeyFactsActionDelegate {
    void showKeyFacts(final EHILocation pickupLocation, List<EHIKeyFactsPolicy> policies, final EHIExtras carClassDetailsExtras);

    void onKeyFactsPolicyClicked(EHIKeyFactsPolicy policy);

    void onKeyFactsExclusionsClicked(List<EHIKeyFactsPolicy> exclusions);

    void onExtraItemClicked(EHIExtraItem ehiExtraItem);
}
