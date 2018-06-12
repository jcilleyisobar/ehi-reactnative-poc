package com.ehi.enterprise.android.models.reservation;

import com.ehi.enterprise.android.models.EHIModel;
import com.ehi.enterprise.android.models.location.EHIImage;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class EHIVehicleDetails extends EHIModel {
    @SerializedName("code")
    private String mCode;
    @SerializedName("make")
    private String mMake;
    @SerializedName("model")
    private String mModel;
    @SerializedName("color")
    private String mColor;
    @SerializedName("distance_unit")
    private String mDistanceUnit;
    @SerializedName("vin_number")
    private String mVinNumber;
    @SerializedName("license_plate")
    private String mLicensePlateNumber;
    @SerializedName("license_state")
    private String mLicenseState;
    @SerializedName("name")
    private String mName;
    @SerializedName("starting_odometer")
    private String mStartingOdometer;
    @SerializedName("ending_odometer")
    private String mEndingOdometer;
    @SerializedName("distance_traveled")
    private String mDistanceTraveled;
    @SerializedName("images")
    private List<EHIImage> mImages;
    @SerializedName("vehicle_class_driven")
    private String mVehicleClassDriven;
    @SerializedName("vehicle_class_charged")
    private String mVehicleClassCharged;

    public String getCode() {
        return mCode;
    }

    public String getMake() {
        return mMake;
    }

    public String getModel() {
        return mModel;
    }

    public String getColor() {
        return mColor;
    }

    public String getDistanceUnit() {
        return mDistanceUnit;
    }

    public String getVinNumber() {
        return mVinNumber;
    }

    public String getLicensePlateNumber() {
        return mLicensePlateNumber;
    }

    public String getLicenseState() {
        return mLicenseState;
    }

    public String getName() {
        return mName;
    }

    public String getStartingOdometer() {
        return mStartingOdometer;
    }

    public String getEndingOdometer() {
        return mEndingOdometer;
    }

    public String getDistanceTraveled() {
        return mDistanceTraveled;
    }

    public List<EHIImage> getImages() {
        return mImages;
    }

    public void setMake(String make) {
        mMake = make;
    }

    public void setModel(String model) {
        mModel = model;
    }

    public void setImages(List<EHIImage> images) {
        mImages = images;
    }

    public void setLicensePlateNumber(String licensePlateNumber) {
        mLicensePlateNumber = licensePlateNumber;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getVehicleClassDriven() {
        return mVehicleClassDriven;
    }

    public String getVehicleClassCharged() {
        return mVehicleClassCharged;
    }
}
