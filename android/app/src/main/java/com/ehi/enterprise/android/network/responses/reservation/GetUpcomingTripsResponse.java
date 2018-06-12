package com.ehi.enterprise.android.network.responses.reservation;

import com.ehi.enterprise.android.models.reservation.EHITripSummary;
import com.ehi.enterprise.android.network.responses.BaseResponse;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class GetUpcomingTripsResponse extends BaseResponse {
    @SerializedName("pre_write_tickets")
    private List<EHITripSummary> mPreWriteTickets;

    @SerializedName("upcoming_reservations")
    private List<EHITripSummary> mUpcomingReservations;

    @SerializedName("more_records_available")
    private boolean mMoreRecordsAvailable;

    private static Comparator<EHITripSummary> mTripSummaryComparator = new Comparator<EHITripSummary>() {
        @Override
        public int compare(EHITripSummary lhs, EHITripSummary rhs) {
            return lhs.getPickupTime().before(rhs.getPickupTime()) ? -1 : 1;
        }
    };

    public boolean isMoreRecordsAvailable() {
        return mMoreRecordsAvailable;
    }

    public List<EHITripSummary> getPreWriteTickets() {
        return mPreWriteTickets;
    }

    public List<EHITripSummary> getUpcomingReservations() {
        return mUpcomingReservations;
    }

    public List<EHITripSummary> getFullSortedTripList(){
        List<EHITripSummary> fullList = new ArrayList<>();
        fullList.addAll(mPreWriteTickets);
        fullList.addAll(mUpcomingReservations);

        Collections.sort(fullList, mTripSummaryComparator);

        return fullList;
    }
}
