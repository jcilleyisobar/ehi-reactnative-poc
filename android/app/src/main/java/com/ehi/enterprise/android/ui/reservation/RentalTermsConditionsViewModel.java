package com.ehi.enterprise.android.ui.reservation;

import com.ehi.enterprise.android.models.profile.ProfileCollection;
import com.ehi.enterprise.android.models.reservation.EHIReservation;
import com.ehi.enterprise.android.models.terms_conditions.EHIRentalTermsAndConditions;
import com.ehi.enterprise.android.network.interfaces.IApiCallback;
import com.ehi.enterprise.android.network.requests.terms_conditions.GetRentalTermsAndConditionsRequest;
import com.ehi.enterprise.android.network.responses.ResponseWrapper;
import com.ehi.enterprise.android.network.responses.terms_conditions.GetRentalTermsAndConditionsResponse;

import java.util.ArrayList;
import java.util.Locale;

import io.dwak.reactor.ReactorVar;
import io.dwak.reactor.unbinder.annotation.AutoUnbindAll;

@AutoUnbindAll
public class RentalTermsConditionsViewModel extends ReservationViewModel{

    final ReactorVar<GetRentalTermsAndConditionsResponse> mRentalTermsAndConditions = new ReactorVar<>();
    final ReactorVar<ArrayList> mAvailableLocalesReactorVar = new ReactorVar<>();
    final ReactorVar<String> mSelectedLocale = new ReactorVar<>();
    final ReactorVar<String> mTermsConditionsText = new ReactorVar<>();
    final ArrayList<String> mAvailableLocales = new ArrayList<>();
    private String mInjectedResSessionId = null;

    @Override
    public void onAttachToView() {
        super.onAttachToView();
        if (mRentalTermsAndConditions.getRawValue() == null) {
            requestRentalTermsAndConditions();
        }
    }

    public void requestRentalTermsAndConditions() {
        showProgress(true);
        EHIReservation ongoingReservation = getOngoingReservation();
        if (mInjectedResSessionId != null
                || (ongoingReservation != null
                    && ongoingReservation.getResSessionId() != null)
                ) {

            String sessionId = mInjectedResSessionId != null
                    ? mInjectedResSessionId
                    : ongoingReservation.getResSessionId();

            performRequest(new GetRentalTermsAndConditionsRequest(sessionId), new IApiCallback<GetRentalTermsAndConditionsResponse>() {
                @Override
                public void handleResponse(ResponseWrapper<GetRentalTermsAndConditionsResponse> response) {
                    showProgress(false);
                    if (response.isSuccess()) {
                        if (response.getData() != null
                                && response.getData().getEHIRentalTermsAndConditions() != null
                                && response.getData().getEHIRentalTermsAndConditions().size() > 0) {
                            mRentalTermsAndConditions.setValue(response.getData());
                            String defaultLocale = Locale.getDefault().toString();
                            for (EHIRentalTermsAndConditions rentalTermsAndConditions : response.getData().getEHIRentalTermsAndConditions()) {
                                if (rentalTermsAndConditions.getLocale().equals(defaultLocale)) {
                                    mAvailableLocales.add(0, rentalTermsAndConditions.getLocaleLabel());
                                } else {
                                    mAvailableLocales.add(rentalTermsAndConditions.getLocaleLabel());
                                }
                            }
                            mAvailableLocalesReactorVar.setValue(mAvailableLocales);
                            setSelectedLocalLabel(mAvailableLocales.get(0));
                        }
                    } else {
                        setError(response);
                    }
                }
            });
        }
    }

    public GetRentalTermsAndConditionsResponse getTermsAndConditionsResponse() {
        return mRentalTermsAndConditions.getValue();
    }

    public void setSelectedLocalLabel(String selectedLocale) {
        mSelectedLocale.setValue(selectedLocale);
    }

    public String getSelectedLocaleLabel() {
        return mSelectedLocale.getValue();
    }

    public ArrayList<String> getAvailableLocales() {
        return mAvailableLocalesReactorVar.getValue();
    }

    public String getTermsAndConditionsByLocale(String locale) {
        for (EHIRentalTermsAndConditions rentalTermsAndConditions : mRentalTermsAndConditions.getValue().getEHIRentalTermsAndConditions()) {
            if (locale.equalsIgnoreCase(rentalTermsAndConditions.getLocaleLabel())) {
                return rentalTermsAndConditions.getRentalTermsAndConditionsText();
            }
        }
        return null;
    }

    public String getCountryCode() {
        final ProfileCollection profile = getProfile();
        if (profile != null && profile.getLicenseProfile() != null) {
            return profile.getLicenseProfile().getCountryCode();
        }
        return null;
    }

    private ProfileCollection getProfile() {
        return getManagers().getLoginManager().getProfileCollection();
    }

    public String getInjectedResSessionId() {
        return mInjectedResSessionId;
    }

    public void setInjectedResSessionId(String injectedResSessionId) {
        mInjectedResSessionId = injectedResSessionId;
    }
}