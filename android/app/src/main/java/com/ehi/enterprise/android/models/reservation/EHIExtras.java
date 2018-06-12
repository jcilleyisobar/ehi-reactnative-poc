package com.ehi.enterprise.android.models.reservation;

import com.ehi.enterprise.android.models.EHIModel;
import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class EHIExtras extends EHIModel {

    @SerializedName("equipment")
    private List<EHIExtraItem> mEquipment;

    @SerializedName("insurance")
    private List<EHIExtraItem> mInsurance;

    @SerializedName("fuel")
    private List<EHIExtraItem> mFuel;

    public List<EHIExtraItem> getEquipment() {
        return mEquipment;
    }

    public List<EHIExtraItem> getInsurance() {
        return mInsurance;
    }

    public List<EHIExtraItem> getFuel() {
        return mFuel;
    }

    public List<EHIExtraItem> getOptionalAndWaivedEquipment() {
        List<EHIExtraItem> eq = new LinkedList<>();
        if (mEquipment != null) {
            for (EHIExtraItem item : mEquipment) {
                if (item.getStatus() != null
                        && (item.getStatus().equals(EHIExtraItem.OPTIONAL)
                        || item.getStatus().equals(EHIExtraItem.WAIVED))) {
                    eq.add(item);
                }
            }
        }
        return eq;
    }

    public List<EHIExtraItem> getIncludedEquipment() {
        List<EHIExtraItem> eq = new LinkedList<>();
        if (mEquipment != null) {
            for (EHIExtraItem item : mEquipment) {
                if (item.getStatus() != null
                        && item.getStatus().equals(EHIExtraItem.INCLUDED)) {
                    eq.add(item);
                }
            }
        }
        return eq;
    }

    public List<EHIExtraItem> getOptionalAndWaivedInsurance() {
        List<EHIExtraItem> ins = new LinkedList<>();
        if (mInsurance != null) {
            for (EHIExtraItem item : mInsurance) {
                if (item.getStatus() != null
                        && (item.getStatus().equals(EHIExtraItem.OPTIONAL)
                        || item.getStatus().equals(EHIExtraItem.WAIVED))) {
                    ins.add(item);
                }
            }
        }
        return ins;
    }

    public List<EHIExtraItem> getIncludedInsurance() {
        List<EHIExtraItem> ins = new LinkedList<>();
        if (mInsurance != null) {
            for (EHIExtraItem item : mInsurance) {
                if (item.getStatus() != null
                        && item.getStatus().equals(EHIExtraItem.INCLUDED)) {
                    ins.add(item);
                }
            }
        }
        return ins;
    }

    public List<EHIExtraItem> getOptionalAndWaivedFuel() {
        List<EHIExtraItem> fuel = new LinkedList<>();
        if (mFuel != null) {
            for (EHIExtraItem item : mFuel) {
                if (item.getStatus() != null
                        && (item.getStatus().equals(EHIExtraItem.OPTIONAL)
                        || item.getStatus().equals(EHIExtraItem.WAIVED))) {
                    fuel.add(item);
                }
            }
        }
        return fuel;
    }

    public List<EHIExtraItem> getIncludedExtras() {
        List<EHIExtraItem> included = new LinkedList<>();
        if (mEquipment != null) {
            for (EHIExtraItem item : mEquipment) {
                if (item.getStatus() != null
                        && item.getStatus().equals(EHIExtraItem.INCLUDED)) {
                    included.add(item);
                }
            }
        }
        if (mInsurance != null) {
            for (EHIExtraItem item : mInsurance) {
                if (item.getStatus() != null
                        && item.getStatus().equals(EHIExtraItem.INCLUDED)) {
                    included.add(item);
                }
            }
        }

        if (mFuel != null) {
            for (EHIExtraItem item : mFuel) {
                if (item.getStatus() != null
                        && item.getStatus().equals(EHIExtraItem.INCLUDED)) {
                    included.add(item);
                }
            }
        }
        return included;
    }

    public List<EHIExtraItem> getMandatoryExtras() {
        List<EHIExtraItem> included = new LinkedList<>();
        if (mEquipment != null) {
            for (EHIExtraItem item : mEquipment) {
                if (item.getStatus() != null
                        && item.getStatus().equals(EHIExtraItem.MANDATORY)) {
                    included.add(item);
                }
            }
        }
        if (mInsurance != null) {
            for (EHIExtraItem item : mInsurance) {
                if (item.getStatus() != null
                        && item.getStatus().equals(EHIExtraItem.MANDATORY)) {
                    included.add(item);
                }
            }
        }

        if (mFuel != null) {
            for (EHIExtraItem item : mFuel) {
                if (item.getStatus() != null
                        && item.getStatus().equals(EHIExtraItem.MANDATORY)) {
                    included.add(item);
                }
            }
        }
        return included;
    }

    public List<EHIExtraItem> getAllExtras() {
        List<EHIExtraItem> all = new LinkedList<>();
        if (mEquipment != null) {
            all.addAll(mEquipment);
        }
        if (mInsurance != null) {
            all.addAll(mInsurance);
        }
        if (mFuel != null) {
            all.addAll(mFuel);
        }
        return all;
    }

    public List<EHIExtraItem> getSelectedExtras() {
        List<EHIExtraItem> selected = new LinkedList<>();
        if (mEquipment != null) {
            for (EHIExtraItem item : mEquipment) {
                if (item.getStatus() != null
                        && item.getSelectedQuantity() > 0
                        && (item.getStatus().equals(EHIExtraItem.OPTIONAL)
                        || item.getStatus().equals(EHIExtraItem.WAIVED))) {
                    selected.add(item);
                }
            }
        }
        if (mInsurance != null) {
            for (EHIExtraItem item : mInsurance) {
                if (item.getStatus() != null
                        && item.getSelectedQuantity() > 0
                        && (item.getStatus().equals(EHIExtraItem.OPTIONAL)
                        || item.getStatus().equals(EHIExtraItem.WAIVED))) {
                    selected.add(item);
                }
            }
        }
        if (mFuel != null) {
            for (EHIExtraItem item : mFuel) {
                if (item.getStatus() != null
                        && item.getSelectedQuantity() > 0
                        && (item.getStatus().equals(EHIExtraItem.OPTIONAL)
                        || item.getStatus().equals(EHIExtraItem.WAIVED))) {
                    selected.add(item);
                }
            }
        }
        return selected;
    }

    public Map<String, EHIExtraItem> getExtrasMap() {
        Map<String, EHIExtraItem> all = new HashMap<>();
        if (mEquipment != null) {
            for (EHIExtraItem item : mEquipment) {
                all.put(item.getCode(), item);
            }
        }
        if (mInsurance != null) {
            for (EHIExtraItem item : mInsurance) {
                all.put(item.getCode(), item);
            }
        }
        if (mFuel != null) {
            for (EHIExtraItem item : mFuel) {
                all.put(item.getCode(), item);
            }
        }
        return all;
    }

}