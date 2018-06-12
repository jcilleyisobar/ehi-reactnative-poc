package com.ehi.enterprise.android.ui.profile.interfaces;


import com.ehi.enterprise.android.models.profile.EHIPaymentMethod;

public abstract class EditPaymentListener {

    public abstract void onEdit(EHIPaymentMethod method);

    public abstract void onDelete(EHIPaymentMethod method);
}
