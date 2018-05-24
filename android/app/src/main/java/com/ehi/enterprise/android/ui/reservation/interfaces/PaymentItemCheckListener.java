package com.ehi.enterprise.android.ui.reservation.interfaces;


import com.ehi.enterprise.android.models.profile.EHIPaymentMethod;
import com.ehi.enterprise.android.ui.reservation.view_holders.PaymentItemCheckViewHolder;

public abstract class PaymentItemCheckListener {

    public abstract void onEdit(EHIPaymentMethod method);

    public abstract void onCheck(PaymentItemCheckViewHolder viewHolder, EHIPaymentMethod method, int position);

    public abstract void onClick(EHIPaymentMethod method);
}
