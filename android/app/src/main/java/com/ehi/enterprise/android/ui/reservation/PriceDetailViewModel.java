package com.ehi.enterprise.android.ui.reservation;

import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;

import com.ehi.enterprise.android.models.reservation.EHIPaymentLineItem;
import com.ehi.enterprise.android.network.interfaces.IApiCallback;
import com.ehi.enterprise.android.network.requests.reservation.GetMoreTaxesInformationRequest;
import com.ehi.enterprise.android.network.responses.ResponseWrapper;
import com.ehi.enterprise.android.network.responses.reservation.GetMoreTaxesInformationResponse;
import com.ehi.enterprise.android.ui.viewmodel.ManagersAccessViewModel;

import java.util.List;

import io.dwak.reactor.ReactorVar;
import io.dwak.reactor.unbinder.annotation.AutoUnbindAll;

@AutoUnbindAll
public class PriceDetailViewModel extends ManagersAccessViewModel {
	private List<EHIPaymentLineItem> mPaymentLineItems;
    ReactorVar<String> mTaxesAndFeesMoreInformation = new ReactorVar<>();
    ReactorVar<ResponseWrapper> mErrorResponse = new ReactorVar<>();

    public void setPaymentLineItems(List<EHIPaymentLineItem> paymentLineItems) {
		mPaymentLineItems = paymentLineItems;
	}

	public List<EHIPaymentLineItem> getPaymentLineItems() {
		return mPaymentLineItems;
	}

    void requestLearnMore(){
        performRequest(new GetMoreTaxesInformationRequest(), new IApiCallback<GetMoreTaxesInformationResponse>(){
            @Override
            public void handleResponse(ResponseWrapper<GetMoreTaxesInformationResponse> response) {
                if(response.isSuccess()){
                    setTaxesAndFeesMoreInformation(response.getData().getContent());
                }
                else {
                    setErrorResponse(response);
                }
            }
        });
    }

    public void setTaxesAndFeesMoreInformation(String taxesAndFeesMoreInformation) {
        mTaxesAndFeesMoreInformation.setValue(taxesAndFeesMoreInformation);
    }

    public Spanned getTaxesAndFeesMoreInformtion(){
        if(TextUtils.isEmpty(mTaxesAndFeesMoreInformation.getValue())){
            return null;
        }
        return Html.fromHtml(mTaxesAndFeesMoreInformation.getValue());
    }

    public ResponseWrapper getErrorResponse() {
        return mErrorResponse.getValue();
    }

    public void setErrorResponse(ResponseWrapper errorResponse) {
        mErrorResponse.setValue(errorResponse);
    }
}
