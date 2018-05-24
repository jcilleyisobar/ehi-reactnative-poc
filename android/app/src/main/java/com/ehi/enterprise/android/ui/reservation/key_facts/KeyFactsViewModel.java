package com.ehi.enterprise.android.ui.reservation.key_facts;

import android.view.View;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.models.location.EHILocation;
import com.ehi.enterprise.android.models.profile.EHICountry;
import com.ehi.enterprise.android.models.reservation.EHIExtras;
import com.ehi.enterprise.android.models.reservation.EHIKeyFactsPolicy;
import com.ehi.enterprise.android.models.reservation.EHIReservation;
import com.ehi.enterprise.android.ui.viewmodel.ManagersAccessViewModel;
import com.ehi.enterprise.android.utils.EHITextUtils;
import com.ehi.enterprise.android.utils.ListUtils;
import com.ehi.enterprise.android.utils.reactor_extensions.viewstate.ReactorTextViewState;
import com.ehi.enterprise.android.utils.reactor_extensions.viewstate.ReactorViewState;

import java.util.ArrayList;
import java.util.List;

import io.dwak.reactor.ReactorVar;
import io.dwak.reactor.unbinder.annotation.AutoUnbindAll;

@AutoUnbindAll
public class KeyFactsViewModel extends ManagersAccessViewModel {
    private static final String RULES_OF_THE_ROAD_URL = "http://ec.europa.eu/transport/road_safety/index_en.htm";
    private EHILocation mPickupLocation;

    //region ReactorVar
    final ReactorVar<List<EHIKeyFactsPolicy>> mMinimumRequirementsPolicies = new ReactorVar<>();
    final ReactorVar<List<EHIKeyFactsPolicy>> mProtectionProducts = new ReactorVar<>();
    final ReactorVar<List<EHIKeyFactsPolicy>> mAdditionalPolicies = new ReactorVar<>();
    final ReactorVar<List<EHIKeyFactsPolicy>> mQuestionsPolicies = new ReactorVar<>();
    final ReactorVar<EHICountry> mDisputeCountry = new ReactorVar<>();
    final ReactorVar<Integer> title = new ReactorVar<>();
    final ReactorViewState disputeCell = new ReactorViewState();
    final ReactorTextViewState viewRoadRules = new ReactorTextViewState();
    final ReactorVar<EHIExtras> mCarClassDetailsExtras = new ReactorVar<>();
    final ReactorVar<EHIExtras> mVehicleRatesExtras = new ReactorVar<>();
    //endregion

    @Override
    public void onAttachToView() {
        super.onAttachToView();
        title.setValue(R.string.reservation_about_your_rental_section_title);
        if (mDisputeCountry.getRawValue() == null) {
            getDisputeInfoFromCountry();
        }
        viewRoadRules.setText(getResources().getString(R.string.key_facts_rules_of_the_road_view) + " >");
    }

    private void getDisputeInfoFromCountry() {
        List<EHICountry> countries = getManagers().getLocalDataManager().getCountriesList();
        String disputeCountry = mPickupLocation.getAddress().getCountryCode();
        if (!ListUtils.isEmpty(countries) && !EHITextUtils.isEmpty(disputeCountry)) {
            for (EHICountry c : countries) {
                if (c.getCountryCode().equalsIgnoreCase(disputeCountry)) {
                    if (!EHITextUtils.isEmpty(c.getKeyFactsDisputeEmail()) && !EHITextUtils.isEmpty(c.getKeyFactsDisputePhone())) {
                        mDisputeCountry.setValue(c);
                    }
                    disputeCell.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    public void setKeyFactsPolicies(final List<EHIKeyFactsPolicy> keyFactsPolicies) {
        final List<EHIKeyFactsPolicy> minimumRequirementsPolicies = new ArrayList<>();
        final List<EHIKeyFactsPolicy> protectionProducts = new ArrayList<>();
        final List<EHIKeyFactsPolicy> additionalPolicies = new ArrayList<>();
        final List<EHIKeyFactsPolicy> questionsPolicies = new ArrayList<>();

        for (EHIKeyFactsPolicy policy : keyFactsPolicies) {
            if (EHIKeyFactsPolicy.MINIMUM_REQUIREMENTS.equals(policy.getSection())) {
                minimumRequirementsPolicies.add(policy);
            } else if (EHIKeyFactsPolicy.PROTECTIONS.equals(policy.getSection())) {
                protectionProducts.add(policy);
            } else if (EHIKeyFactsPolicy.ADDITIONAL.equals(policy.getSection())) {
                additionalPolicies.add(policy);
            } else if (EHIKeyFactsPolicy.QUESTIONS.equals(policy.getSection())) {
                questionsPolicies.add(policy);
            }
        }

        mMinimumRequirementsPolicies.setValue(minimumRequirementsPolicies);
        mProtectionProducts.setValue(protectionProducts);
        mAdditionalPolicies.setValue(additionalPolicies);
        mQuestionsPolicies.setValue(questionsPolicies);
    }

    public List<EHIKeyFactsPolicy> getMinimumRequirementsPolicies() {
        return mMinimumRequirementsPolicies.getValue();
    }

    public List<EHIKeyFactsPolicy> getProtectionProducts() {
        return mProtectionProducts.getValue();
    }

    public List<EHIKeyFactsPolicy> getAdditionalPolicies() {
        return mAdditionalPolicies.getValue();
    }

    public List<EHIKeyFactsPolicy> getQuestionsPolicies() {
        return mQuestionsPolicies.getValue();
    }

    public EHICountry getDisputeInfo() {
        return mDisputeCountry.getValue();
    }

    public EHIExtras getCarClassDetailsExtras() {
        return mCarClassDetailsExtras.getValue();
    }

    public void setCarClassDetailsExtras(final EHIExtras extras) {
        mCarClassDetailsExtras.setValue(extras);
    }

    public void setVehicleRatesExtras(final EHIExtras vehicleRatesExtras) {
        mVehicleRatesExtras.setValue(vehicleRatesExtras);
    }

    public void setPickupLocation(final EHILocation pickupLocation) {
        mPickupLocation = pickupLocation;
    }

    public String getRulesOfTheRoadUrl(){
        final EHIReservation reservation = getEhiReservation(false);
        if (!reservation.getRulesOfRoad().isEmpty()){
            return  reservation.getRulesOfRoad();
        } else {
            return RULES_OF_THE_ROAD_URL;
        }
    }

}
