package com.ehi.enterprise.android.utils.manager;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.ehi.enterprise.android.models.location.EHICityLocation;
import com.ehi.enterprise.android.models.location.solr.EHISolrLocation;
import com.ehi.enterprise.android.models.reservation.ReservationInformation;
import com.ehi.enterprise.android.utils.BaseAppUtils;
import com.ehi.enterprise.android.utils.DLog;
import com.ehi.enterprise.android.utils.TimeUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class LocationManager extends BaseDataManager {

    private static LocationManager sInstance;

    private static final String RECENT_TEMPORARY_RESERVATION_KEY = "RECENT_TEMPORARY_RESERVATION_KEY";
    private static final String RECENT_RESERVATION_KEY = "RECENT_RESERVATION_KEY";
    public static final String FAVORITES = "FAVORITES";
    public static final String RECENT_LOCATIONS = "RECENT_LOCATIONS";
    public static final String RECENT_CITY_LOCATIONS = "RECENT_CITY_LOCATIONS";
    public static final String ABANDONED_RESERVATIONS = "ABANDONED_RESERVATIONS";

    private static final int MAX_RECENTS_ALLOWED = 5;
    private static final int MAX_PAST_RESERVATIONS = 3;
    private static final int MAX_ABANDONED_RESERVATIONS = 3;

    @NonNull
    public static LocationManager getInstance() {
        if (sInstance == null) {
            sInstance = new LocationManager();
        }

        return sInstance;
    }

    @Override
    public void initialize(@NonNull Context context) {
        super.initialize(context);
        sInstance = this;
    }

    protected LocationManager() {
    }

    public void addFavoriteLocation(@NonNull EHISolrLocation solrLocation) {
        if (isRecentLocation(solrLocation)) {
            removeRecentLocation(solrLocation);
        }
        set(solrLocation.getPeopleSoftId(), solrLocation);
        //Remove recent rental from history if it is added to favorites
        String key = favoritesContains(solrLocation);
        if (key != null) {
            List<ReservationInformation> pastRentals = getRecentReservations();

            if (pastRentals != null) {
                for (int a = 0; a < pastRentals.size(); a++) {
                    if (pastRentals.get(a).getPickupLocation().getPeopleSoftId().equals(key)) {
                        pastRentals.remove(a);
                        break;
                    }
                }
                set(RECENT_RESERVATION_KEY, pastRentals);
            }

        }
    }

    public boolean isFavoriteLocation(@NonNull String locationId) {
        EHISolrLocation location = getEhiModel(locationId, EHISolrLocation.class);
        return location != null;
    }

    public void removeFavoriteLocation(@NonNull EHISolrLocation solrLocation) {
        remove(solrLocation.getPeopleSoftId());
    }

    @Nullable
    public Map<String, EHISolrLocation> getFavoriteLocations() {
        Gson gson = BaseAppUtils.getDefaultGson();
        TypeToken<EHISolrLocation> ehiSolrLocationTypeToken = new TypeToken<EHISolrLocation>() {
        };
        Map<String, String> allEntries = (Map<String, String>) mSharedPreferences.getAll();
        Map<String, EHISolrLocation> favoriteLocations = new HashMap<>();
        ArrayList<String> faultyFavorites = new ArrayList<>();
        if (allEntries != null) {
            for (String key : allEntries.keySet()) {
                if (!RECENT_CITY_LOCATIONS.equals(key)
                        && !RECENT_LOCATIONS.equals(key)
                        && !key.equals(ABANDONED_RESERVATIONS)
                        && !RECENT_RESERVATION_KEY.equals(key)
                        && !RECENT_TEMPORARY_RESERVATION_KEY.equals(key)) {
                    final String encryptedJson = allEntries.get(key);
                    final String decryptedJson = decrypt(encryptedJson);
                    try {
                        EHISolrLocation solrLocation = gson.fromJson(decryptedJson, ehiSolrLocationTypeToken.getType());
                        favoriteLocations.put(key, solrLocation);
                    } catch (IllegalStateException e) {
                        DLog.e(TAG, "", e);
                        faultyFavorites.add(key);
                    }
                }
            }

            for (String s : faultyFavorites) {
                allEntries.remove(s);
            }


            return Collections.unmodifiableMap(favoriteLocations);
        } else {
            return null;
        }
    }

    public void addRecentCitySearchLocation(@NonNull EHICityLocation cityLocation) {
        final TypeToken<Stack<EHICityLocation>> typeToken = new TypeToken<Stack<EHICityLocation>>() {
        };
        Stack<EHICityLocation> cityLocationStack = getEhiModel(RECENT_CITY_LOCATIONS, typeToken.getType());
        if (cityLocationStack == null) {
            cityLocationStack = new Stack<>();
        }
        cityLocationStack.add(cityLocation);

        set(RECENT_CITY_LOCATIONS, cityLocationStack);
    }

    @Nullable
    public EHICityLocation getMostRecentCitySearchLocation() {
        Stack<EHICityLocation> cityLocationStack = getEhiModel(RECENT_CITY_LOCATIONS, new TypeToken<Stack<EHICityLocation>>() {
        }.getType());
        if (cityLocationStack == null) {
            return null;
        } else {
            return cityLocationStack.peek();
        }
    }

    @Nullable
    public Stack<EHICityLocation> getRecentCitySearchLocations() {
        return getEhiModel(RECENT_CITY_LOCATIONS, new TypeToken<Stack<EHICityLocation>>() {
        }.getType());
    }

    public void clearRecentSearchCityLocations() {
        remove(RECENT_CITY_LOCATIONS);
    }

    public void removeRecentLocation(@NonNull EHISolrLocation solrLocation) {
        Stack<EHISolrLocation> recentLocations = getRecentLocations();
        if (recentLocations != null) {
            int indexToRemove = -1;
            for (int i = 0; i < recentLocations.size(); i++) {
                if (solrLocation.getPeopleSoftId().equals(recentLocations.get(i).getPeopleSoftId())) {
                    indexToRemove = i;
                }
            }
            if (indexToRemove >= 0) {
                recentLocations.remove(indexToRemove);
            }
        }

        set(RECENT_LOCATIONS, recentLocations);
    }

    public boolean isRecentLocation(@NonNull EHISolrLocation solrLocation) {
        return isRecentLocation(solrLocation.getPeopleSoftId());
    }

    public boolean isRecentLocation(String locationId) {
        Stack<EHISolrLocation> recentLocations = getRecentLocations();
        if (recentLocations != null && !recentLocations.isEmpty()) {
            for (EHISolrLocation recentLocation : recentLocations) {
                if (locationId.equals(recentLocation.getPeopleSoftId())) {
                    return true;
                }
            }
        }
        return false;
    }

    @Nullable
    public Stack<EHISolrLocation> getRecentLocations() {
        return getEhiModel(RECENT_LOCATIONS, new TypeToken<Stack<EHISolrLocation>>() {
        }.getType());
    }

    @Nullable
    public EHISolrLocation getMostRecentLocation() {
        Stack<EHISolrLocation> recentLocations = getRecentLocations();
        if (recentLocations != null && !recentLocations.isEmpty()) {
            return recentLocations.peek();
        } else {
            return null;
        }
    }

    public void addRecentLocation(@NonNull EHISolrLocation solrLocation) {
        if (isFavoriteLocation(solrLocation.getPeopleSoftId())) {
            return;
        }
        Stack<EHISolrLocation> recentLocations = getRecentLocations();
        if (recentLocations == null) {
            recentLocations = new Stack<>();
        }

        boolean recentExists = false;
        for (EHISolrLocation recentLocation : recentLocations) {
            if (solrLocation.getPeopleSoftId().equals(recentLocation.getPeopleSoftId())) {
                recentExists = true;
            }
        }

        if (!recentExists) {
            recentLocations.push(solrLocation);
        }

        if (recentLocations.size() > MAX_RECENTS_ALLOWED) {
            recentLocations.remove(recentLocations.firstElement());
        }


        set(RECENT_LOCATIONS, recentLocations);
    }

    public void clearRecentLocations() {
        remove(RECENT_LOCATIONS);
    }

    public void clearFavorites() {
        clear();
    }

    @Override
    protected String getSharedPreferencesName() {
        return FAVORITES;
    }

    public boolean hasSavedLocations() {
        Map<String, EHISolrLocation> favorites = getFavoriteLocations();
        List<ReservationInformation> abandoned = getAbandonedReservations();
        List<ReservationInformation> information = getRecentReservations();
        return (favorites != null
                && favorites.size() != 0) || (abandoned != null && abandoned.size() > 0)
                || information != null
                || getString(RECENT_TEMPORARY_RESERVATION_KEY, null) != null;
    }

    public void saveAbandonReservation(int indexToAdd, ReservationInformation abandonedReservation) {
        //Do not save if tracking disabled or abandoned reservation is same as recent reservation
        List<ReservationInformation> list = getAbandonedReservations();
        if (list == null) {
            list = new ArrayList<>();
        }

        int index = -1;
        for (int a = 0; a < list.size(); a++) {
            ReservationInformation information = list.get(a);
            if (information.equals(abandonedReservation)) {
                index = a;
                break;
            }
        }
        if (index != -1) {
            list.remove(index);
        } else if (list.size() >= MAX_ABANDONED_RESERVATIONS) {
            list.remove(MAX_ABANDONED_RESERVATIONS - 1);
        }
        list.add(indexToAdd, abandonedReservation);

        set(ABANDONED_RESERVATIONS, list);
    }

    public void saveAbandonReservation(ReservationInformation abandonedReservation) {
        saveAbandonReservation(0, abandonedReservation);
    }

    public int removeAbandonedReservation(ReservationInformation reservationInformation) {
        List<ReservationInformation> reservationInformationList = getAbandonedReservations();
        if (reservationInformationList != null) {
            for (ReservationInformation information : reservationInformationList) {
                if (information.equals(reservationInformation)) {
                    final int indexOf = reservationInformationList.indexOf(information);
                    reservationInformationList.remove(information);
                    set(ABANDONED_RESERVATIONS, reservationInformationList);
                    return indexOf;
                }
            }

        }

        return -1;
    }

    @Nullable
    public List<ReservationInformation> getAbandonedReservations() {
        List<ReservationInformation> list = getEhiModel(ABANDONED_RESERVATIONS, new TypeToken<List<ReservationInformation>>() {
        }.getType());
        if (list == null) {
            return null;
        }

        Calendar c;

        for (ReservationInformation i : list) {
            if (i.getPickupDate() == null) {
                continue;
            }
            c = TimeUtils.mergeDateTime(i.getPickupDate(), i.getPickupTime());
            if (Calendar.getInstance().after(c)) {
                i.setPickupDate(null);
                i.setPickupTime(null);
                i.setReturnDate(null);
                i.setReturnTime(null);
            }
        }

        return list;
    }

    public void clearRecentActivities() {
        remove(ABANDONED_RESERVATIONS);
        remove(RECENT_RESERVATION_KEY);
    }

    public List<ReservationInformation> commitAndGetAbandonedReservations() {
        commitAbandonedReservation();
        return getAbandonedReservations();
    }

    public void clearTrackingData() {
        remove(RECENT_CITY_LOCATIONS);
        remove(RECENT_LOCATIONS);
        remove(ABANDONED_RESERVATIONS);
        remove(RECENT_TEMPORARY_RESERVATION_KEY);
        remove(RECENT_RESERVATION_KEY);
    }

    public void setRecentReservation(ReservationInformation reservationInformation) {
        set(RECENT_TEMPORARY_RESERVATION_KEY, reservationInformation);
    }

    public void saveRecentReservation(ReservationInformation information) {
        final TypeToken<List<ReservationInformation>> typeToken = new TypeToken<List<ReservationInformation>>() {
        };
        List<ReservationInformation> list = getEhiModel(RECENT_RESERVATION_KEY, typeToken.getType());

        //Per requirements favorites cannot be added
        if (favoritesContains(information.getPickupLocation()) != null) {
            return;
        }

        //remove saved information
        ReservationInformation purgedInformation = new ReservationInformation();
        purgedInformation.setPickupLocation(information.getPickupLocation());
        purgedInformation.setReturnLocation(information.getReturnLocation());
        information = purgedInformation;

        if (list == null) {
            list = new ArrayList<>();
        }
        int index = -1;

        for (int a = 0; a < list.size(); a++) {
            if (list.get(a).equals(information)) {
                index = a;
                break;
            }
        }
        if (index != -1) {
            list.remove(index);
        } else if (list.size() >= MAX_PAST_RESERVATIONS) {
            list.remove(MAX_PAST_RESERVATIONS - 1);
        }
        list.add(0, information);
        set(RECENT_RESERVATION_KEY, list);
    }

    /**
     * Saves the temporary data into the abandoned reservations
     */
    public void commitAbandonedReservation() {
        ReservationInformation reservationInformation = getEhiModel(RECENT_TEMPORARY_RESERVATION_KEY, ReservationInformation.class);
        if (reservationInformation == null) {
            return;
        }
        saveAbandonReservation(reservationInformation);
        remove(RECENT_TEMPORARY_RESERVATION_KEY);
    }

    /**
     * Saves the temporary data into the recent reservations list
     */
    public void commitRecentReservation() {
        ReservationInformation reservationInformation = getEhiModel(RECENT_TEMPORARY_RESERVATION_KEY, ReservationInformation.class);
        if (reservationInformation == null) {
            return;
        }
        saveRecentReservation(reservationInformation);
        remove(RECENT_TEMPORARY_RESERVATION_KEY);
    }

    @Nullable
    private String favoritesContains(EHISolrLocation someLocation) {
        Map<String, EHISolrLocation> favorites = getFavoriteLocations();
        List<String> keys = new ArrayList<>(favorites.keySet());
        EHISolrLocation favLocation;
        for (int a = 0; a < keys.size(); a++) {
            favLocation = favorites.get(keys.get(a));
            if (favLocation.getPeopleSoftId().equals(someLocation.getPeopleSoftId())) {
                return keys.get(a);
            }
        }
        return null;
    }

    @Nullable
    public List<ReservationInformation> getRecentReservations() {
        return getEhiModel(RECENT_RESERVATION_KEY, new TypeToken<ArrayList<ReservationInformation>>() {
        }.getType());
    }

    public int removeRecentReservation(ReservationInformation reservationInformation) {
        List<ReservationInformation> reservationInformationList = getRecentReservations();

        if (reservationInformationList != null) {
            for (ReservationInformation information : reservationInformationList) {
                if (information.equals(reservationInformation)) {
                    final int indexOf = reservationInformationList.indexOf(information);
                    reservationInformationList.remove(information);
                    set(RECENT_RESERVATION_KEY, reservationInformationList);
                    return indexOf;
                }
            }
        }


        return -1;
    }
}
