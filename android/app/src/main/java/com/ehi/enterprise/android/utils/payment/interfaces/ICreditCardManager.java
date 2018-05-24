package com.ehi.enterprise.android.utils.payment.interfaces;

import com.ehi.enterprise.android.network.interfaces.ITypedRequestProcessorService;
import com.ehi.enterprise.android.utils.payment.CreditCard;

public interface ICreditCardManager {
    void setRequestProcessorService(ITypedRequestProcessorService requestProcessorService);

    void uploadCreditCardToService(CreditCard creditCard, IOnSaveCreditCardCallback onSaveCreditCardCallback);
}
