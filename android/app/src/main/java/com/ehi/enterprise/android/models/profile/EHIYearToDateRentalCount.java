package com.ehi.enterprise.android.models.profile;


import com.ehi.enterprise.android.models.EHIModel;
import com.google.gson.annotations.SerializedName;

class EHIYearToDateRentalCount extends EHIModel {

    @SerializedName("number_of_rentals")
    private int numberOfRentals;

    @SerializedName("number_of_rental_days")
    private int numberOfRentalDays;

    public int getNumberOfRentals() {
        return numberOfRentals;
    }

    public int getNumberOfRentalDays() {
        return numberOfRentalDays;
    }
}
