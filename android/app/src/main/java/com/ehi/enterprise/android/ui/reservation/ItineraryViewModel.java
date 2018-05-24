package com.ehi.enterprise.android.ui.reservation;

import android.support.annotation.Nullable;
import android.view.View;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.app.EHIStringToken;
import com.ehi.enterprise.android.models.location.solr.EHIAgeOption;
import com.ehi.enterprise.android.models.location.solr.EHISolrLocation;
import com.ehi.enterprise.android.models.profile.EHIProfileResponse;
import com.ehi.enterprise.android.models.profile.ProfileCollection;
import com.ehi.enterprise.android.models.reservation.EHIAdditionalInformation;
import com.ehi.enterprise.android.models.reservation.EHICarClassDetails;
import com.ehi.enterprise.android.models.reservation.EHIContract;
import com.ehi.enterprise.android.models.reservation.EHIReservation;
import com.ehi.enterprise.android.models.reservation.ReservationInformation;
import com.ehi.enterprise.android.network.interfaces.IApiCallback;
import com.ehi.enterprise.android.network.requests.AbstractRequestProvider;
import com.ehi.enterprise.android.network.requests.location.solr.GetSolrAgeOptionsRequest;
import com.ehi.enterprise.android.network.requests.reservation.PostEmeraldClubLoginRequest;
import com.ehi.enterprise.android.network.requests.reservation.PostInitiateAuthRequest;
import com.ehi.enterprise.android.network.requests.reservation.PostInitiateUnAuthRequest;
import com.ehi.enterprise.android.network.requests.reservation.modify.PostDateAndLocationModifyRequest;
import com.ehi.enterprise.android.network.responses.ResponseWrapper;
import com.ehi.enterprise.android.network.responses.location.solr.GetSolrAgeOptionsResponse;
import com.ehi.enterprise.android.ui.location.SearchLocationsActivity;
import com.ehi.enterprise.android.ui.viewmodel.CountrySpecificViewModel;
import com.ehi.enterprise.android.utils.EHITextUtils;
import com.ehi.enterprise.android.utils.TimeUtils;
import com.ehi.enterprise.android.utils.reactor_extensions.viewstate.ReactorCompoundButtonState;
import com.ehi.enterprise.android.utils.reactor_extensions.viewstate.ReactorDateTimeSelectorViewState;
import com.ehi.enterprise.android.utils.reactor_extensions.viewstate.ReactorImageViewState;
import com.ehi.enterprise.android.utils.reactor_extensions.viewstate.ReactorPropertyChangedListener;
import com.ehi.enterprise.android.utils.reactor_extensions.viewstate.ReactorSpinnerViewState;
import com.ehi.enterprise.android.utils.reactor_extensions.viewstate.ReactorTextViewState;
import com.ehi.enterprise.android.utils.reactor_extensions.viewstate.ReactorViewState;
import com.isobar.android.tokenizedstring.TokenizedString;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.dwak.reactor.ReactorVar;
import io.dwak.reactor.unbinder.annotation.AutoUnbindAll;

@AutoUnbindAll
public class ItineraryViewModel extends CountrySpecificViewModel {

    //region ReactorVars
    final ReactorVar<Integer> titleResource = new ReactorVar<>();
    final ReactorVar<Integer> clientErrorDialogDialogTextRes = new ReactorVar<>();

    final ReactorVar<Integer> ecToast = new ReactorVar<>();

    final ReactorTextViewState pickupLocationHeader = new ReactorTextViewState();
    final ReactorTextViewState pickupLocationTextView = new ReactorTextViewState();
    final ReactorImageViewState pickupLockImage = new ReactorImageViewState();
    final ReactorImageViewState dropOffLockImage = new ReactorImageViewState();
    final ReactorViewState dropOffContainer = new ReactorViewState();

    final ReactorTextViewState returnLocationHeader = new ReactorTextViewState();
    final ReactorViewState addReturnLocationButton = new ReactorViewState();
    final ReactorViewState removeReturnLocation = new ReactorViewState();
    final ReactorTextViewState returnLocationTextView = new ReactorTextViewState();

    final ReactorDateTimeSelectorViewState selectPickupDateView = new ReactorDateTimeSelectorViewState();
    final ReactorDateTimeSelectorViewState selectReturnDateView = new ReactorDateTimeSelectorViewState();

    final ReactorViewState driversAgeHeader = new ReactorViewState();
    final ReactorSpinnerViewState driversAgeSpinner = new ReactorSpinnerViewState();

    final ReactorViewState contractFromProfileContainer = new ReactorViewState();
    final ReactorTextViewState contractFromProfileName = new ReactorTextViewState();
    final ReactorCompoundButtonState contractFromProfileSwitch = new ReactorCompoundButtonState();
    final ReactorPropertyChangedListener<Boolean> contractFromProfilChangeListener = new ReactorPropertyChangedListener<Boolean>() {
        @Override
        public void onPropertyChanged(final Boolean newValue) {
            if (newValue != mFromProfileSwitchChecked) {
                mFromProfileSwitchChecked = newValue;
                updateContractFromProfileViewState();
            }
        }
    };

    final ReactorViewState ecSignInButton = new ReactorViewState();
    final ReactorViewState ecEnabled = new ReactorViewState();

    final ReactorTextViewState addCidButton = new ReactorTextViewState();
    final ReactorViewState cidInputHeader = new ReactorViewState();
    final ReactorViewState cidInputArea = new ReactorViewState();
    final ReactorViewState cidContainer = new ReactorViewState();
    final ReactorTextViewState cidEditText = new ReactorTextViewState();
    final ReactorPropertyChangedListener<String> cidTextChangeListener = new ReactorPropertyChangedListener<String>() {
        @Override
        public void onPropertyChanged(String newValue) {
            mManuallyEnteredCid = newValue;
        }
    };

    final ReactorViewState clearCidButton = new ReactorViewState();
    final ReactorVar<Boolean> applyWeekendSpecial = new ReactorVar<>(false);

    final ReactorTextViewState continueButton = new ReactorTextViewState();

    final ReactorVar<String> mCID = new ReactorVar<>();

    final ReactorVar<EHIReservation> mInitiateResult = new ReactorVar<>();
    final ReactorVar<Boolean> mRequiresAuthentication = new ReactorVar<>(false);
    final ReactorVar<Boolean> mRequiresTravelPurpose = new ReactorVar<>(false);
    final ReactorVar<Boolean> mRequiresPreRateAdditionalField = new ReactorVar<>(false);
    final ReactorVar<Boolean> mCodeNotOnProfile = new ReactorVar<>(false);
    private ReactorVar<Boolean> mRequiresPin = new ReactorVar<>(false);
    private ReactorVar<Boolean> mWrongPin = new ReactorVar<>(false);
    private ReactorVar<Boolean> mInvalidAuthToken = new ReactorVar<>(false);

    final ReactorVar<Boolean> determinateLoader = new ReactorVar<>(false);
    //endregion

    private EHISolrLocation mPickUpLocation = null;
    private EHISolrLocation mReturnLocation = null;

    private Date mPickupDate = null;
    private Date mReturnDate = null;
    private Date mPickupTime = null;
    private Date mReturnTime = null;

    private List<EHIAgeOption> mAgeOptions;
    private int mAge = 0;
    private int mRentersAgeIndex;

    private EHIContract mCorpAccountFromProfile;
    private boolean mFromProfileSwitchChecked = true;
    private String mManuallyEnteredCid = "";
    private boolean mWeekenSpecialApplied = false;

    private boolean mECWasLogedIn = false;

    private boolean mCidInputFieldShowed = false;

    private String mTripPurpose;
    private boolean mEdit = false;
    private boolean mIsModify = false;

    private String mAuthPin;
    private List<EHIAdditionalInformation> mEhiAdditionalInformationList;
    private String mPreRateErrorMessage;

    //region lifecycle
    @Override
    public void onAttachToView() {
        super.onAttachToView();
        updateTitle();
        populateCorpAccountFromProfile();
        updateLocationHeadersText();
        updateContinueButtonState();
        updateDateTimeViewsState();
        renewEmeraldClubAuthData();

        cidEditText.setTextChangedListener(cidTextChangeListener);
        contractFromProfileSwitch.setCheckedChangedListener(contractFromProfilChangeListener);
    }

    @Override
    public void onDetachFromView() {
        super.onDetachFromView();
        saveFlowForAbandon();
    }
    //endregion


    //region locations
    public EHISolrLocation getPickUpLocation() {
        return mPickUpLocation;
    }

    public void setPickUpLocation(EHISolrLocation pickUpLocation) {
        if (pickUpLocation == null) {
            return;
        }
        mPickUpLocation = pickUpLocation;
        if (pickUpLocation.getAgeOptions() != null) {
            setAgeOptions(pickUpLocation.getAgeOptions());
        } else {
            requestAgeOptions();
        }
        pickupLocationTextView.setText(pickUpLocation.getTranslatedLocationName());
        if (pickUpLocation.getGrayLocationCellIconDrawable() > 0) {
            pickupLocationTextView.setDrawableLeft(pickUpLocation.getGrayLocationCellIconDrawable());
            pickupLocationTextView.setCompoundDrawablePaddingInDp(8);
        } else {
            pickupLocationTextView.setDrawableLeft(0);
        }

        pickupLockImage.setVisibility(shouldBlockLocationChange() ? ReactorViewState.VISIBLE : ReactorViewState.GONE);

        //one way support

        if (!mPickUpLocation.isOneWaySupported()
                && mReturnLocation != null
                && !mReturnLocation.getPeopleSoftId().trim().equalsIgnoreCase(mPickUpLocation.getPeopleSoftId().trim())) {
            //reseting return location since new pickup location not support one way
            setReturnLocation(null);
            showOneWayNotSupportedError();
        }
    }

    public void showOneWayNotSupportedError() {
        clientErrorDialogDialogTextRes.setValue(R.string.alert_one_way_reservation_text);
    }

    public EHISolrLocation getReturnLocation() {
        return mReturnLocation;
    }

    public void setReturnLocation(EHISolrLocation returnLocation) {
        mReturnLocation = returnLocation;
        if (mReturnLocation == null) {
            removeReturnLocation.setVisibility(View.GONE);
            addReturnLocationButton.setVisibility(View.VISIBLE);
            returnLocationTextView.setVisibility(View.GONE);
        } else {
            if (shouldBlockLocationChange()) {
                dropOffLockImage.setVisibility(ReactorViewState.VISIBLE);
                dropOffContainer.setBackgroundResource(R.drawable.date_button_touch_overlay);
                removeReturnLocation.setVisibility(View.GONE);
            } else {
                dropOffLockImage.setVisibility(ReactorViewState.GONE);
                dropOffContainer.setBackground(null);
                removeReturnLocation.setVisibility(View.VISIBLE);
            }

            addReturnLocationButton.setVisibility(View.GONE);
            returnLocationTextView.setVisibility(View.VISIBLE);
            returnLocationTextView.setText(mReturnLocation.getTranslatedLocationName());
            if (mReturnLocation.getGrayLocationCellIconDrawable() > 0) {
                returnLocationTextView.setDrawableLeft(mReturnLocation.getGrayLocationCellIconDrawable());
                returnLocationTextView.setCompoundDrawablePaddingInDp(8);
            } else {
                returnLocationTextView.setDrawableLeft(0);
            }
        }
        updateLocationHeadersText();
    }

    private void updateLocationHeadersText() {
        if (getReturnLocation() == null) {
            pickupLocationHeader.setText(R.string.reservation_location_selection_pickup_header_fallback_title);
            returnLocationHeader.setVisibility(View.GONE);
        } else {
            pickupLocationHeader.setText(R.string.reservation_location_selection_pickup_header_title);
            returnLocationHeader.setVisibility(View.VISIBLE);
        }
    }

    //endregion

    //region date/time
    public void setPickupDate(Date pickUpDateTime) {
        mPickupDate = pickUpDateTime;
        updateContinueButtonState();
        updateDateTimeViewsState();
    }

    public void setPickupTime(Date pickupTime) {
        mPickupTime = pickupTime;
        updateContinueButtonState();
        updateDateTimeViewsState();
    }

    public void setDropoffDate(Date dropOffDateTime) {
        mReturnDate = dropOffDateTime;
        updateContinueButtonState();
        updateDateTimeViewsState();
    }

    public void setDropoffTime(Date returnTime) {
        mReturnTime = returnTime;
        updateContinueButtonState();
        updateDateTimeViewsState();
    }

    public Date getPickupDate() {
        return mPickupDate;
    }

    public Date getPickupTime() {
        return mPickupTime;
    }

    public Date getReturnDate() {
        return mReturnDate;
    }

    public Date getReturnTime() {
        return mReturnTime;
    }

    public Date getPickupDateTime() {
        if (getPickupDate() == null || getPickupTime() == null) {
            return null;
        }
        return TimeUtils.mergeDateTime(getPickupDate(), getPickupTime()).getTime();
    }

    public Date getReturnDateTime() {
        if (getReturnDate() == null || getReturnTime() == null) {
            return null;
        }
        return TimeUtils.mergeDateTime(getReturnDate(), getReturnTime()).getTime();
    }

    public void setPickupDateTime(Date pickupDateTime) {
        if (pickupDateTime == null) {
            setPickupTime(null);
            setPickupDate(null);
            return;
        }
        Date[] pickupDateAndTime = TimeUtils.splitDateAndTime(pickupDateTime);
        setPickupDate(pickupDateAndTime[0]);
        setPickupTime(pickupDateAndTime[1]);
    }

    public void setReturnDateTime(Date returnDateTime) {
        if (returnDateTime == null) {
            setDropoffDate(null);
            setDropoffTime(null);
            return;
        }
        Date[] returnDateAndTime = TimeUtils.splitDateAndTime(returnDateTime);
        setDropoffDate(returnDateAndTime[0]);
        setDropoffTime(returnDateAndTime[1]);
    }

    private void updateDateTimeViewsState() {
        selectPickupDateView.setSelectedDate(getPickupDate());
        selectPickupDateView.setSelectedTime(getPickupTime());
        selectReturnDateView.setSelectedDate(getReturnDate());
        selectReturnDateView.setSelectedTime(getReturnTime());

        if (getPickupDate() == null
                && getReturnDate() == null) {
            selectReturnDateView.setPickerBackgroundDrawableRes(R.drawable.time_selector_gray_button_touch_overlay);
            selectReturnDateView.setPickerIconTextAlpha(0.5f);
            selectPickupDateView.setTimeEnabled(false);
            selectReturnDateView.setTimeEnabled(false);
        } else {
            selectReturnDateView.setPickerBackgroundDrawableRes(R.drawable.green_button_touch_overlay);
            selectReturnDateView.setPickerIconTextAlpha(1.0f);

            if (getPickupDate() != null
                    && getReturnDate() == null) {
                selectPickupDateView.setTimeEnabled(false);
                selectReturnDateView.setTimeEnabled(false);
            } else if (getPickupDate() != null
                    && getReturnDate() != null
                    && getPickupTime() == null
                    && getReturnTime() == null) {
                selectPickupDateView.setTimeEnabled(true);
                selectReturnDateView.setTimeEnabled(false);
            } else {
                selectPickupDateView.setTimeEnabled(true);
                selectReturnDateView.setTimeEnabled(true);
            }
        }
    }

    private void updateContinueButtonState() {
        if (getPickupDate() != null
                && getPickupTime() != null
                && getReturnTime() != null
                && getReturnDate() != null) {
            continueButton.setEnabled(true);
        } else {
            continueButton.setEnabled(false);
        }

        if (mIsModify) {
            continueButton.setText(R.string.reservation_itinerary_in_modify_action_button);
        } else {
            continueButton.setText(R.string.reservation_itinerary_action_button);
        }

    }

    //endregion

    //region age

    public int getRenterAge() {
        return mAge;
    }

    public void setRenterAge(int age) {
        mAge = age;
        final List<EHIAgeOption> ehiAgeOptions = getAgeOptions();

        if (ehiAgeOptions != null && ehiAgeOptions.size() > 0) {
            final int size = ehiAgeOptions.size();
            mRentersAgeIndex = size - 1;
            final int renterAge = getRenterAge();

            for (int i = 0; i < size; i++) {
                if ((renterAge == 0 && ehiAgeOptions.get(i).isSelected())
                        || (ehiAgeOptions.get(i).getValue() == renterAge)) {
                    mRentersAgeIndex = i;
                }
            }
        }

        updateAgeSpinnerViewState();
    }

    private void requestAgeOptions() {
        showProgress(true);
        performRequest(new GetSolrAgeOptionsRequest(getPickUpLocation()), new IApiCallback<GetSolrAgeOptionsResponse[]>() {
            @Override
            public void handleResponse(ResponseWrapper<GetSolrAgeOptionsResponse[]> response) {
                if (response.isSuccess()) {
                    List<EHIAgeOption> ageOptions = new ArrayList<>(response.getData().length);
                    for (int i = 0; i < response.getData().length; i++) {
                        ageOptions.add(new EHIAgeOption(response.getData()[i]));
                    }
                    setAgeOptions(ageOptions);
                    showProgress(false);
                } else {
                    showProgress(false);
                    setError(response);
                }

            }
        });
    }

    public void setAgeOptions(List<EHIAgeOption> options) {
        mAgeOptions = options;
        if (mAgeOptions != null && mAgeOptions.size() > 0) {
            setRenterAge(mAgeOptions.get(mAgeOptions.size() - 1).getValue());
        }
        updateAgeSpinnerViewState();
    }

    private void updateAgeSpinnerViewState() {
        if (isUserLoggedIn()
                || mECWasLogedIn
                || isModify()) {
            driversAgeHeader.setVisibility(View.GONE);
            driversAgeSpinner.setVisibility(View.GONE);
        } else {
            driversAgeHeader.setVisibility(View.VISIBLE);
            driversAgeSpinner.setVisibility(View.VISIBLE);
            if (getAgeOptions() != null) {
                driversAgeSpinner.populateView(getDisplayAgeOptions(),
                        getRentersAgeIndex(),
                        getResources().getString(R.string.reservation_age_field_title));
            }
        }
    }

    public List<EHIAgeOption> getAgeOptions() {
        return mAgeOptions;
    }

    public List<CharSequence> getDisplayAgeOptions() {
        List<CharSequence> options = new ArrayList<>(getAgeOptions().size());
        for (int i = 0; i < getAgeOptions().size(); i++) {
            options.add(new TokenizedString.Formatter<EHIStringToken>(getResources())
                    .formatString(getAgeOptions().get(i).getLabel())
                    .addTokenAndValue(EHIStringToken.AGE_TO, getResources().getString(R.string.age_to_label))
                    .addTokenAndValue(EHIStringToken.AGE_OR_YOUNGER, getResources().getString(R.string.age_or_younger_label))
                    .addTokenAndValue(EHIStringToken.AGE_OR_OLDER, getResources().getString(R.string.age_or_older_label))
                    .format());
        }
        return options;
    }

    public int getRentersAgeIndex() {
        return mRentersAgeIndex;
    }

    //endregion

    //region contract from profile

    private void populateCorpAccountFromProfile() {
        if (!isModify()) {
            ProfileCollection ecProfile = getManagers().getReservationManager().getEmeraldClubProfile();
            if (mECWasLogedIn // if emerald club, will populate from emerald club
                    && getManagers().getReservationManager().isLoggedIntoEmeraldClub()
                    && ecProfile != null
                    && ecProfile.getProfile() != null
                    && ecProfile.getProfile().getCorporateAccount() != null) {
                mCorpAccountFromProfile = ecProfile.getProfile().getCorporateAccount();
                updateContractFromProfileViewState();
                return;
            }

            ProfileCollection mainProfile = getManagers().getLoginManager().getProfileCollection();
            if (isUserLoggedIn()
                    && mainProfile.getProfile() != null
                    && mainProfile.getProfile().getCorporateAccount() != null) {
                mCorpAccountFromProfile = mainProfile.getProfile().getCorporateAccount();
                updateContractFromProfileViewState();
                return;
            }

            updateContractFromProfileViewState();
        }
    }

    void updateContractFromProfileViewState() {
        if (mCorpAccountFromProfile == null || isWeekendSpecialApplied()) {
            contractFromProfileName.setText("");
            contractFromProfileContainer.setVisibility(View.GONE);
        } else {
            contractFromProfileName.setText(mCorpAccountFromProfile.getContractOrBillingName());
            contractFromProfileContainer.setVisibility(View.VISIBLE);
        }
        contractFromProfileSwitch.setChecked(mFromProfileSwitchChecked);
        if (isModify()) {
            contractFromProfileSwitch.setEnabled(false);
            contractFromProfileContainer.setAlpha(0.5f);
        } else {
            contractFromProfileSwitch.setEnabled(true);
            contractFromProfileContainer.setAlpha(1f);
        }
        updateCidViewState();
    }

    //endregion

    //region CID

    private void updateCidViewState() {
        if (mIsModify) {
            //is modify
            if (!EHITextUtils.isEmpty(mManuallyEnteredCid)) {
                //if have contract number and it's not from profile show it
                cidContainer.setVisibility(View.VISIBLE);
                cidInputHeader.setVisibility(View.GONE);
                cidInputArea.setVisibility(View.VISIBLE);
                cidEditText.setEnabled(false);
                cidEditText.setText(mManuallyEnteredCid);
                cidEditText.setVisibility(View.VISIBLE);
                clearCidButton.setVisibility(View.GONE);
                addCidButton.setVisibility(View.GONE);
            } else {
                // disable it
                cidContainer.setVisibility(View.VISIBLE);
                cidInputArea.setVisibility(View.GONE);
                cidInputHeader.setVisibility(View.GONE);
                cidEditText.setVisibility(View.GONE);
                clearCidButton.setVisibility(View.GONE);
                addCidButton.setVisibility(View.VISIBLE);
                addCidButton.setEnabled(false);
                addCidButton.setTextColor(getResources().getColor(R.color.disabled_text));
                addCidButton.setDrawableLeft(R.drawable.icon_add_disable);
            }
        } else {
            //if main reservation flow
            if (((mCorpAccountFromProfile != null && mFromProfileSwitchChecked))
                    && !isWeekendSpecialApplied()) {
                //if cid from profile agreedToTermsAndConditions or EC is turned on
                cidContainer.setVisibility(View.GONE);
            } else {
                ///in all other cases
                cidContainer.setVisibility(View.VISIBLE);

                if (isWeekendSpecialApplied()) {
                    return;
                }

                //if CID was entered manually then showing it
                if (!EHITextUtils.isEmpty(mManuallyEnteredCid)) {
                    mCidInputFieldShowed = true;
                }
                if (mCidInputFieldShowed) {
                    //show input field
                    cidInputArea.setVisibility(View.VISIBLE);
                    cidInputHeader.setVisibility(View.VISIBLE);
                    cidEditText.setVisibility(View.VISIBLE);
                    if (mManuallyEnteredCid != null) {
                        cidEditText.setText(mManuallyEnteredCid);
                    } else {
                        cidEditText.setText("");
                    }
                    clearCidButton.setVisibility(View.VISIBLE);
                    addCidButton.setVisibility(View.GONE);
                } else {
                    //show add cid button
                    cidInputArea.setVisibility(View.GONE);
                    cidInputHeader.setVisibility(View.GONE);
                    cidEditText.setVisibility(View.GONE);
                    clearCidButton.setVisibility(View.GONE);
                    addCidButton.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    public void clearCidButtonClicked() {
        if (!EHITextUtils.isEmpty(mManuallyEnteredCid)) {
            mCidInputFieldShowed = true;
            cidEditText.setText("");
        } else {
            mCidInputFieldShowed = false;
        }
        clearCidRelatedData(true);
        updateCidViewState();
    }

    private void clearCidRelatedData(boolean unsetTripPurposePreRate) {
        setEhiAdditionalInformationList(null);
        setAuthPin(null);
        setTripPurpose(null);

        if (unsetTripPurposePreRate) {
            setTripPurposePreRate(false);
        }

        mRequiresPreRateAdditionalField.setRawValue(false);
        mRequiresPin.setRawValue(false);
        mWrongPin.setRawValue(false);
        mRequiresTravelPurpose.setRawValue(false);
    }

    public void addCidButtonClicked() {
        mCidInputFieldShowed = true;
        updateCidViewState();
    }
    //endregion

    //region emerald club

    private void renewEmeraldClubAuthData() {
        setECWasLogedIn(getManagers().getReservationManager().getEmeraldClubAuthToken() != null);
        if (!mECWasLogedIn && isEmeraldClubDataSaved()) {
            showProgress(true);
            performRequest(new PostEmeraldClubLoginRequest(getManagers().getReservationManager().getEmeraldClubAuthData()),
                    new IApiCallback<EHIProfileResponse>() {
                        @Override
                        public void handleResponse(final ResponseWrapper<EHIProfileResponse> response) {
                            showProgress(false);
                            if (response.isSuccess()) {
                                getManagers().getReservationManager().setEmeraldClubProfile(response.getData());
                                getManagers().getReservationManager().setEmeraldClubAuthToken(response.getData().getAuthToken());
                                getManagers().getReservationManager().saveEmeraldClubAuthData(response.getData().getEncryptedAuthData());

                                mECWasLogedIn = true;
                                ecToast.setValue(R.string.reservation_emerald_sign_in_confirmation_toast_message);
                                populateCorpAccountFromProfile();
                                updateEmeraldClubViewsState();
                            } else {
                                setError(response);
                            }
                        }
                    });
        } else {
            updateEmeraldClubViewsState();
        }
    }

    public void setECWasLogedIn(boolean ECWasLogedIn) {
        mECWasLogedIn = ECWasLogedIn;
        populateCorpAccountFromProfile();
        updateEmeraldClubViewsState();
    }

    private void updateEmeraldClubViewsState() {
        if (mECWasLogedIn) {
            ecEnabled.setVisibility(View.VISIBLE);
            ecSignInButton.setVisibility(View.GONE);
        } else {
            ecEnabled.setVisibility(View.GONE);
            if (isUserLoggedIn()
                    || isModify()) {
                ecSignInButton.setVisibility(View.GONE);
            } else {
                ecSignInButton.setVisibility(View.VISIBLE);
            }
        }
        updateAgeSpinnerViewState();
        updateContractFromProfileViewState();
        updateCidViewState();
    }

    public void removeEmeraldClubAccount() {
        getManagers().getReservationManager().removeEmeraldClubAccount();
        mECWasLogedIn = false;
        renewEmeraldClubAuthData();
        mCorpAccountFromProfile = null;
        populateCorpAccountFromProfile();
    }

    //endregion

    //region general things
    private void updateTitle() {
        if (mIsModify) {
            titleResource.setValue(R.string.reservation_navigation_in_modify_title_key);
        } else {
            titleResource.setValue(R.string.reservation_navigation_title_key);
        }
    }

    public void saveFlowForAbandon() {
        if (mReturnDate == null
                && mReturnTime == null
                && mPickupDate == null
                && mPickupTime == null
                && mReturnLocation == null) {
            return; //don't bother saving if it's just a location search
        }

        if (!getManagers().getSettingsManager().isSearchHistoryEnabled()) {
            return;
        }
        ReservationInformation resInfo = new ReservationInformation();
        resInfo.setPickupLocation(mPickUpLocation);
        resInfo.setReturnLocation(mReturnLocation);
        resInfo.setPickupDate(getPickupDate());
        resInfo.setPickupTime(getPickupTime());
        resInfo.setReturnDate(getReturnDate());
        resInfo.setReturnTime(getReturnTime());
        resInfo.setRenterAge(getRenterAge());
        if (mCorpAccountFromProfile != null) { // have account from profile
            resInfo.setCorpAccount(mCorpAccountFromProfile);
        } else if (!EHITextUtils.isEmpty(mManuallyEnteredCid)) { // or CID was entered
            EHIContract acc = new EHIContract();
            acc.setContractNumber(mManuallyEnteredCid);
            acc.setContractName(mManuallyEnteredCid);
            resInfo.setCorpAccount(acc);
        }

        getManagers().getLocationManager().setRecentReservation(resInfo);
    }

    public void clearReturnLocation() {
        setEdit(true);
        setReturnLocation(null);
    }

    public void populateFromReservationInformation(ReservationInformation reservationInformation) {
        if (getPickUpLocation() == null && reservationInformation.getPickupLocation() != null) {
            setPickUpLocation(reservationInformation.getPickupLocation());
        }
        if (getReturnLocation() == null && reservationInformation.getReturnLocation() != null) {
            setReturnLocation(reservationInformation.getReturnLocation());
        }
        if (getPickupDate() == null && reservationInformation.getPickupDate() != null) {
            setPickupDate(reservationInformation.getPickupDate());
            setPickupTime(reservationInformation.getPickupTime());
        }
        if (getReturnDate() == null && reservationInformation.getReturnDate() != null) {
            setDropoffTime(reservationInformation.getReturnTime());
            setDropoffDate(reservationInformation.getReturnDate());
        }
        if (reservationInformation.getCorpAccount() != null
                && reservationInformation.getCorpAccount().getContractNumber() != null) {
            if (reservationInformation.getCorpAccount().getContractNumber().equalsIgnoreCase(
                    reservationInformation.getCorpAccount().getContractOrBillingName())) {
                //since name are == contract_number let decide it's manually entered CID
                mManuallyEnteredCid = reservationInformation.getCorpAccount().getContractNumber();
            } else {
                //different name and CID - let say it's cor account from profile
                mCorpAccountFromProfile = reservationInformation.getCorpAccount();
            }
        }

        if (reservationInformation.getRenterAge() > 0) {
            setRenterAge(reservationInformation.getRenterAge());
        }
    }

    public void setTripPurpose(String tripPurpose) {
        mTripPurpose = tripPurpose;
    }

    public void setTripPurposePreRate(boolean isPreRate) {
        getManagers().getReservationManager().setTripPurposePreRate(isPreRate);
    }

    public boolean isEdit() {
        return mEdit;
    }

    public void setEdit(final boolean edit) {
        mEdit = edit;
    }

    public void removeReturnLocationClicked() {
        setReturnLocation(null);
    }
    //endregion

    //region network part


    public void initiateReservation() {

        determinateLoader.setValue(true);
        final AbstractRequestProvider<EHIReservation> requestProvider;
        getManagers().getReservationManager().clearSelectedAirlineInformation();
        if (mIsModify) {
            requestProvider = new PostDateAndLocationModifyRequest(getManagers().getReservationManager().getCurrentModifyReservation().getResSessionId(),
                    getPickupLocationIdForInitiate(),
                    getReturnLocationIdForInitiate(),
                    getPickupDateTime(),
                    getReturnDateTime());
        } else {
            if (isUserLoggedIn()) {
                requestProvider = new PostInitiateAuthRequest(
                        getPickupLocationIdForInitiate(),
                        getReturnLocationIdForInitiate(),
                        getPickupDateTime(),
                        getReturnDateTime(),
                        getCidForInitiate(),
                        getAuthPin(),
                        getUserCountry(),
                        mTripPurpose,
                        getEhiAdditionalInformation());
            } else if (getManagers().getReservationManager().isLoggedIntoEmeraldClub()) {
                requestProvider = new PostInitiateAuthRequest(
                        getPickupLocationIdForInitiate(),
                        getReturnLocationIdForInitiate(),
                        getPickupDateTime(),
                        getReturnDateTime(),
                        getCidForInitiate(),
                        getAuthPin(),
                        getUserCountry(),
                        mTripPurpose,
                        getEhiAdditionalInformation());
            } else {
                //pre pay rates cid
                requestProvider = new PostInitiateUnAuthRequest(
                        getPickupLocationIdForInitiate(),
                        getReturnLocationIdForInitiate(),
                        getPickupDateTime(),
                        getReturnDateTime(),
                        getRenterAge(),
                        getCidForInitiate(),
                        getAuthPin(),
                        getUserCountry(),
                        mTripPurpose,
                        getEhiAdditionalInformation());
            }
        }

        performRequest(requestProvider, new IApiCallback<EHIReservation>() {
            @Override
            public void handleResponse(ResponseWrapper<EHIReservation> response) {
                if (response.isSuccess()) {
                    clearCidRelatedData(false);
                    mInitiateResult.setValue(response.getData());
                    if (isModify()) {
                        getManagers().getReservationManager().addOrUpdateModifyReservation(response.getData());
                    } else {
                        getManagers().getReservationManager().addOrUpdateReservation(response.getData());
                    }
                    getManagers().getReservationManager().setFilters(response.getData().getCarClassFilterList());
                    determinateLoader.setValue(false);
                    mWeekenSpecialApplied = false;
                } else {
                    if (response.getErrorCode() != null) {
                        switch (response.getErrorCode()) {
                            case CROS_BUSINESS_LEISURE_CONTRACT_NOT_ON_PROFILE:
                            case CROS_CONTRACT_NOT_ON_PROFILE:
                                mCodeNotOnProfile.setValue(true);
                                break;
                            case CROS_LOGIN_SYSTEM_ERROR:
                                mRequiresAuthentication.setValue(true);
                                break;
                            case CROS_TRAVEL_PURPOSE_NOT_SPECIFIED:
                                mRequiresTravelPurpose.setValue(true);
                                break;
                            case CROS_CONTRACT_PIN_REQUIRED:
                                mRequiresPin.setValue(true);
                                break;
                            case CROS_CONTRACT_PIN_INVALID:
                                mWrongPin.setValue(true);
                                mPreRateErrorMessage = response.getMessage();
                                break;
                            case CROS_RES_PRE_RATE_ADDITIONAL_FIELD_REQUIRED:
                                mRequiresPreRateAdditionalField.setValue(true);
                                break;
                            case CROS_RES_INVALID_ADDITIONAL_FIELD:
                                mRequiresPreRateAdditionalField.setValue(true);
                                mPreRateErrorMessage = response.getMessage();
                                break;
                            case CROS_INVALID_AUTH_TOKEN:
                                getManagers().getReservationManager().removeEmeraldClubAccount();
                                mInvalidAuthToken.setValue(true);
                                break;
                            default:
                                setError(response);
                        }
                    } else {
                        setError(response);
                    }
                    determinateLoader.setValue(false);
                }
            }
        });
    }

    public String getPickupLocationIdForInitiate() {
        return getPickUpLocation().getPeopleSoftId();
    }

    public String getReturnLocationIdForInitiate() {
        return getReturnLocation() != null ? getReturnLocation().getPeopleSoftId()
                : getPickUpLocation().getPeopleSoftId();
    }

    public String getCidForInitiate() {

        // weekend special has preference
        if (mWeekenSpecialApplied && getWeekendSpecialContract() != null) {
            return getWeekendSpecialContract().getContractNumber();
        }

        //checking corp account attached to profile
        if (mCorpAccountFromProfile != null && mFromProfileSwitchChecked) {
            return mCorpAccountFromProfile.getContractNumber();
        }
        //manually entered cid
        if (!EHITextUtils.isEmpty(mManuallyEnteredCid)) {
            return mManuallyEnteredCid;
        }

        return null;
    }

    public String getUserCountry() {
        return getManagers().getLocalDataManager().getPreferredCountryCode();
    }

    @Nullable
    public EHIReservation getInitiateResponse() {
        return mInitiateResult.getValue();
    }

    public void resetInitiateResponse() {
        mInitiateResult.setValue(null);
    }

    public Boolean requiresAuthentication() {
        return mRequiresAuthentication.getValue();
    }

    public void setRequiresAuthentication(boolean requiresAuthentication) {
        mRequiresAuthentication.setValue(requiresAuthentication);
    }

    public void setRequiresTravelPurpose(boolean requiresTravelPurpose) {
        mRequiresTravelPurpose.setValue(requiresTravelPurpose);
    }

    public boolean getRequiresTravelPurpose() {
        return mRequiresTravelPurpose.getValue();
    }

    public void setRequiresPreRateAdditionalField(boolean requiresPreRateAdditionalField) {
        mRequiresPreRateAdditionalField.setValue(requiresPreRateAdditionalField);
    }

    public boolean getRequiresPreRateAdditionalField() {
        return mRequiresPreRateAdditionalField.getValue();
    }

    public boolean getCodeNotOnProfile() {
        return mCodeNotOnProfile.getValue();
    }

    public void setCodeNotOnProfile(boolean codeNotOnProfile) {
        mCodeNotOnProfile.setValue(codeNotOnProfile);
    }

    //endregion

    public boolean isLocationSoldOut(List<EHICarClassDetails> carClassList) {
        int totalSoldOut = 0;
        for (EHICarClassDetails carDetails : carClassList) {
            if (carDetails.getStatus().equals(EHICarClassDetails.SOLD_OUT)) {
                totalSoldOut++;
            }
        }
        return totalSoldOut == carClassList.size();
    }

    public void setIsModify(boolean isModify) {
        mIsModify = isModify;
    }

    public boolean isModify() {
        return mIsModify;
    }

    public void setWeekendSpecialApplied(boolean value) {
        mWeekenSpecialApplied = value;
    }

    public boolean isWeekendSpecialApplied() {
        return mWeekenSpecialApplied;
    }

    public Boolean getRequiresPin() {
        return mRequiresPin.getValue();
    }

    public void setRequiresPin(Boolean value) {
        mRequiresPin.setValue(value);
    }

    public Boolean getWrongPin() {
        return mWrongPin.getValue();
    }

    public void setWrongPin(Boolean value) {
        mWrongPin.setValue(value);
    }

    public void setAuthPin(String pin) {
        mAuthPin = pin;
    }

    public String getAuthPin() {
        return mAuthPin;
    }

    public String getPreRateErrorMessage() {
        return mPreRateErrorMessage;
    }

    public void clearPreRateErrorMessage() {
        mPreRateErrorMessage = null;
    }

    //for tests only

    public String getManuallyEnteredCid() {
        return mManuallyEnteredCid;
    }

    public EHIContract getCorpAccountFromProfile() {
        return mCorpAccountFromProfile;
    }

    public List<EHIAdditionalInformation> getEhiAdditionalInformationList() {
        return mEhiAdditionalInformationList;
    }

    public void setEhiAdditionalInformationList(List<EHIAdditionalInformation> ehiAdditionalInformationList) {
        this.mEhiAdditionalInformationList = ehiAdditionalInformationList;
    }

    private List<EHIAdditionalInformation> getEhiAdditionalInformation() {
        return mEhiAdditionalInformationList;
    }

    public Boolean getInvalidAuthToken() {
        return mInvalidAuthToken.getValue();
    }

    public boolean shouldBlockLocationChange() {
        if (getManagers().getReservationManager().getCurrentModifyReservation() != null) {
            return isModify() && getManagers().getReservationManager().getCurrentModifyReservation().shouldBlockModifyPickupLocation();
        }
        return false;
    }

    public boolean shouldTrackModifyChangeLocation() {
        return isModify();
    }

    public @SearchLocationsActivity.Flow int getFlow(boolean isPickupSelected) {
        if (isPickupSelected) {
            if (getReturnLocation() == null) {
                return SearchLocationsActivity.FLOW_PICKUP_LOCATION_ROUND_TRIP;
            }
            return SearchLocationsActivity.FLOW_PICKUP_LOCATION_ONE_WAY;
        } else {
            return SearchLocationsActivity.FLOW_DROP_OFF_LOCATION_ONE_WAY;
        }
    }

    public void updateDatesFromFlow(@SearchLocationsActivity.Flow int flow, Date pickupDate, Date dropoffDate, Date pickupTime, Date dropoffTime) {
        if (flow == SearchLocationsActivity.FLOW_PICKUP_LOCATION_ROUND_TRIP || flow == SearchLocationsActivity.FLOW_PICKUP_LOCATION_ONE_WAY) {
            setPickupDate(pickupDate);
            setPickupTime(pickupTime);
        }
        if (flow == SearchLocationsActivity.FLOW_PICKUP_LOCATION_ROUND_TRIP || flow == SearchLocationsActivity.FLOW_DROP_OFF_LOCATION_ONE_WAY) {
            setDropoffDate(dropoffDate);
            setDropoffTime(dropoffTime);
        }

        clearDropoffDatesIfBeforePickup();
    }

    private void clearDropoffDatesIfBeforePickup() {
        if (getPickupDate() != null && getReturnDate() != null) {
            final boolean isDropoffDateBeforePickup = getReturnDate().before(getPickupDate());
            final boolean isDropoffTimeBeforePickup = getReturnDate().equals(getPickupDate()) &&
                    getReturnTime() != null && getPickupTime() != null && getReturnTime().before(getPickupTime());
            if (isDropoffDateBeforePickup || isDropoffTimeBeforePickup) {
                setDropoffDate(null);
                setDropoffTime(null);
            }
        }
    }
}