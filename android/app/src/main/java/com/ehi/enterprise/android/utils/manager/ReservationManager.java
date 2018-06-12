package com.ehi.enterprise.android.utils.manager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;

import com.ehi.enterprise.android.models.profile.EHIBasicProfile;
import com.ehi.enterprise.android.models.profile.EHIContactProfile;
import com.ehi.enterprise.android.models.profile.ProfileCollection;
import com.ehi.enterprise.android.models.reservation.EHIAirlineInformation;
import com.ehi.enterprise.android.models.reservation.EHIAvailableCarFilters;
import com.ehi.enterprise.android.models.reservation.EHICarClassDetails;
import com.ehi.enterprise.android.models.reservation.EHICharge;
import com.ehi.enterprise.android.models.reservation.EHIDriverInfo;
import com.ehi.enterprise.android.models.reservation.EHIReservation;
import com.ehi.enterprise.android.utils.BaseAppUtils;
import com.ehi.enterprise.android.utils.EHITextUtils;
import com.ehi.enterprise.android.utils.ListUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReservationManager extends BaseDataManager {

    public static final String RESERVATION = "RESERVATION";
    public static final String SELECTED_CAR_CLASS = "SELECTED_CAR_CLASS";
    public static final String SAVE_DRIVER_INFO = "SAVE_DRIVER_INFO";
    public static final String RESERVATION_FILTERS = "RESERVATION_FILTER_KEY";
    private static final String RESERVATION_FLOW_DRIVER_INFO_KEY = "RESERVATION_FLOW_DRIVER_INFO_KEY";
    private static final String EC_DATA = "EC_DATA";
    private static final String CAR_UPGRADE_KEY = "CAR_UPGRADE_KEY ";
    private static final String VEHICLE_UPGRADE_PROMPT = "VEHICLE_UPGRADE_PROMPT";
    private static final String PARES_KEY = "PARES_KEY";
    public static final String SELECTED_CAR_CLASS_CHARGES = "SELECTED_CAR_CLASS_CHARGES";
    public static final String EC_REFRESH_EVENT = "EC_REFRESH_EVENT";

    // we should not use this - we're keeping for reference
    public static final String CAR_CLASS_LIST = "CAR_CLASS_LIST";

    private static ReservationManager sReservationManagerInstance = new ReservationManager();
    private EHIDriverInfo mDriverInfo = null;
    private String mCurrentReservationId;
    private String mCurrentModifyReservationId;
    private boolean mSaveDriverInfo;
    private String mEmeraldClubAuthToken;
    private ProfileCollection mEmeraldClubProfile;
    private boolean mIsTripPurposePreRate;
    private List<EHIAvailableCarFilters> mFilters;
    private boolean mModify;
    private boolean mIsPromotionAvailable;
    private boolean mIsCorporateAvailable;
    private String mUpgradeAmount;
    private String mSessionSource;
    private boolean weekendSpecial;

    private EHIAirlineInformation mSelectedAirlineInformation;
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean isLoggedIn = intent.getBooleanExtra(LoginManager.LOGGED_IN, false);
            if (!isLoggedIn) {
                deleteDriverInfo();
            }
        }
    };

    @NonNull
    public static ReservationManager getInstance() {
        if (sReservationManagerInstance == null) {
            sReservationManagerInstance = new ReservationManager();
        }
        return sReservationManagerInstance;
    }

    @Override
    public void initialize(@NonNull Context context) {
        super.initialize(context);
        sReservationManagerInstance = this;
        LocalBroadcastManager.getInstance(mContext)
                .registerReceiver(mBroadcastReceiver,
                        new IntentFilter(LoginManager.LOGIN_EVENT));
    }

    protected ReservationManager() {
    }

    public List<EHIAvailableCarFilters> getFilters() {
        if (mFilters != null) {
            return mFilters;
        }
        TypeToken<List<EHIAvailableCarFilters>> typeToken = new TypeToken<List<EHIAvailableCarFilters>>() {
        };
        return getEhiModel(RESERVATION_FILTERS, typeToken.getType());
    }

    public void setFilters(List<EHIAvailableCarFilters> filters) {
        mFilters = filters;
        set(RESERVATION_FILTERS, filters);
    }

    public void addOrUpdateSelectedCarClass(@NonNull EHICarClassDetails carClassDetails) {
        set(SELECTED_CAR_CLASS, carClassDetails);
    }

    @Nullable
    public EHICarClassDetails getSelectedCarClass() {
        return getEhiModel(SELECTED_CAR_CLASS, EHICarClassDetails.class);
    }

    public void addOrUpdateReservation(@NonNull EHIReservation reservationObject) {
        set(reservationObject.getResSessionId(), reservationObject);
        mCurrentReservationId = reservationObject.getResSessionId();
    }

    public void setCarUpgradeSelection(@Nullable EHICarClassDetails details) {
        if (details == null) {
            remove(CAR_UPGRADE_KEY);
        } else {
            set(CAR_UPGRADE_KEY, details);
        }
    }

    public EHICarClassDetails getCarUpgradeSelection() {
        return getEhiModel(CAR_UPGRADE_KEY, EHICarClassDetails.class);
    }

    public String getUpgradeAmount() {
        return mUpgradeAmount;
    }

    public void setUpgradeAmount(String upgradeAmount) {
        mUpgradeAmount = upgradeAmount;
    }

    public void addOrUpdateModifyReservation(@NonNull EHIReservation reservationObject) {
        set("m" + reservationObject.getResSessionId(), reservationObject);
        mCurrentModifyReservationId = reservationObject.getResSessionId();
    }

    @Nullable
    public EHIReservation getReservation(String key) {
        TypeToken<EHIReservation> resResponseToken = new TypeToken<EHIReservation>() {
        };
        return getEhiModel(key, resResponseToken.getType());
    }

    public EHIReservation getModifyReservation(String key) {
        TypeToken<EHIReservation> resResponseToken = new TypeToken<EHIReservation>() {
        };
        return getEhiModel("m" + key, resResponseToken.getType());
    }

    public Map<String, EHIReservation> getReservations() {
        Gson gson = BaseAppUtils.getDefaultGson();

        TypeToken<EHIReservation> resResponseToken = new TypeToken<EHIReservation>() {
        };
        Map<String, String> reservations = (Map<String, String>) mSharedPreferences.getAll();
        Map<String, EHIReservation> resResponse = new HashMap<>();

        if (reservations != null) {
            for (String s : reservations.keySet()) {
                String encryptedJson = reservations.get(s);
                String decryptedJson = decrypt(encryptedJson);
                EHIReservation res = gson.fromJson(decryptedJson, resResponseToken.getType());
                resResponse.put(s, res);
            }
            return resResponse;
        } else {
            return null;
        }
    }

    public String getCurrentReservationId() {
        return mCurrentReservationId;
    }

    public String getCurrentModifyReservationId() {
        return mCurrentModifyReservationId;
    }

    @Nullable
    public EHIReservation getCurrentReservation() {
        return getReservation(getCurrentReservationId());
    }

    @Nullable
    public EHIReservation getCurrentModifyReservation() {
        return getModifyReservation(getCurrentModifyReservationId());
    }

    public void addOrUpdateDriverInfo(@NonNull EHIDriverInfo driverInfo) {
        addOrUpdateDriverInfo(driverInfo, true);
    }

    public void addOrUpdateDriverInfo(@NonNull EHIDriverInfo driverInfo, boolean saveInfo) {
        mSaveDriverInfo = saveInfo;
        set(SAVE_DRIVER_INFO, mSaveDriverInfo);
        if (mSaveDriverInfo) {
            mDriverInfo = driverInfo;
            set(RESERVATION_FLOW_DRIVER_INFO_KEY, driverInfo);
        } else {
            mDriverInfo = driverInfo;
        }
    }

    public void deleteDriverInfo() {
        mDriverInfo = null;
        remove(RESERVATION_FLOW_DRIVER_INFO_KEY);
    }

    @Nullable
    public EHIDriverInfo getDriverInfo() {
        if (mDriverInfo != null) {
            return mDriverInfo;
        }

        if (isLoggedIntoEmeraldClub() && getEmeraldClubProfile().getBasicProfile() != null && getEmeraldClubProfile().getContactProfile() != null) {
            final EHIBasicProfile basicProfile = getEmeraldClubProfile().getBasicProfile();
            final EHIContactProfile contactProfile = getEmeraldClubProfile().getContactProfile();
            return new EHIDriverInfo(contactProfile.getEmail(),
                    contactProfile.getMaskEmail(),
                    basicProfile.getFirstName(),
                    basicProfile.getLastName(),
                    contactProfile.getPhone(0).getPhoneNumber(),
                    false);
        }

        if (!mSaveDriverInfo) {
            mSaveDriverInfo = getBoolean(SAVE_DRIVER_INFO, false);
        }

        if (mSaveDriverInfo) {
            mDriverInfo = getEhiModel(RESERVATION_FLOW_DRIVER_INFO_KEY, EHIDriverInfo.class);
            return mDriverInfo;
        } else {
            return null;
        }
    }

    public boolean shouldSaveDriverInfo() {
        if (!mSaveDriverInfo) {
            mSaveDriverInfo = getBoolean(SAVE_DRIVER_INFO, false);
        }
        return mSaveDriverInfo;
    }

    public void setSelectedCarClassCharges(List<EHICharge> ehiCharges) {
        if (ListUtils.isEmpty(ehiCharges)) {
            remove(SELECTED_CAR_CLASS_CHARGES);
            return;
        }

        set(SELECTED_CAR_CLASS_CHARGES, ehiCharges);
    }

    public List<EHICharge> getSelectedCarClassCharges() {
        TypeToken<List<EHICharge>> listTypeToken = new TypeToken<List<EHICharge>>() {
        };
        return getEhiModel(SELECTED_CAR_CLASS_CHARGES, listTypeToken.getType());

    }

    @Override
    protected String getSharedPreferencesName() {
        return RESERVATION;
    }

    public boolean isLoggedIntoEmeraldClub() {
        return !TextUtils.isEmpty(mEmeraldClubAuthToken) || isEmeraldClubDataSaved();
    }

    public String getEmeraldClubAuthToken() {
        return mEmeraldClubAuthToken;
    }

    public void setEmeraldClubAuthToken(String token) {
        mEmeraldClubAuthToken = token;
    }

    public void setEmeraldClubProfile(ProfileCollection emeraldClubProfile) {
        mEmeraldClubProfile = emeraldClubProfile;
    }

    public ProfileCollection getEmeraldClubProfile() {
        return mEmeraldClubProfile;
    }

    public void removeEmeraldClubAccount() {
        mEmeraldClubAuthToken = null;
        mEmeraldClubProfile = null;
        remove(EC_DATA);
        deleteDriverInfo();
        Intent loginIntent = new Intent(EC_REFRESH_EVENT);
        LocalBroadcastManager.getInstance(getContext()).sendBroadcast(loginIntent);
    }

    public void saveEmeraldClubAuthData(String authData) {
        set(EC_DATA, authData);
    }

    @Nullable
    public String getEmeraldClubAuthData() {
        return getString(EC_DATA, null);
    }

    public boolean isEmeraldClubDataSaved() {
        return containsKey(EC_DATA) && !EHITextUtils.isEmpty(getString(EC_DATA, null));
    }

    public void setTripPurposePreRate(boolean isPreRate) {
        mIsTripPurposePreRate = isPreRate;
    }

    public boolean isTripPurposePreRate() {
        return mIsTripPurposePreRate;
    }

    public void deleteDriverInfoFromDisk() {
        remove(RESERVATION_FLOW_DRIVER_INFO_KEY);
    }

    public boolean isModify() {
        return mModify;
    }

    public void setModify(boolean modify) {
        mModify = modify;
    }

    public boolean getIsPromotionAvailableAllCarClasses() {
        return mIsPromotionAvailable;
    }

    public void setIsPromotionAvailableAllCarClasses(boolean isPromotionAvailable) {
        mIsPromotionAvailable = isPromotionAvailable;
    }

    public String getSessionSource() {
        return mSessionSource;
    }

    public void setSessionSource(String source) {
        mSessionSource = source;
    }

    public boolean vehicleUpgradePrompted() {
        return getBoolean(VEHICLE_UPGRADE_PROMPT, false);
    }

    public void vehicleUpgradeWasPrompted(boolean b) {
        set(VEHICLE_UPGRADE_PROMPT, b);
    }

    @Nullable
    public String getPARes() {
        return getString(PARES_KEY, null);
    }

    public void setPARes(String paresKey) {
        set(PARES_KEY, paresKey);
    }

    public void setWeekendSpecial(boolean value) {
        weekendSpecial = value;
    }

    public boolean isWeekendSpecial() {
        return weekendSpecial;
    }

    public void setIsCorporateAvailableAllCarClasses(boolean value) {
        mIsCorporateAvailable = value;
    }

    public boolean getIsCorporateAvailableAllCarClasses() {
        return mIsCorporateAvailable;
    }

    public EHIAirlineInformation getSelectedAirlineInformation() {
        return mSelectedAirlineInformation;
    }

    public void setSelectedAirlineInformation(EHIAirlineInformation selectedAirlineInformation) {
        mSelectedAirlineInformation = selectedAirlineInformation;
    }

    public void clearSelectedAirlineInformation() {
        mSelectedAirlineInformation = null;
    }
}