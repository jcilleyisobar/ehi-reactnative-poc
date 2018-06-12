package com.ehi.enterprise.android.ui.reservation.history;

import android.app.Activity;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Environment;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.app.EHIStringToken;
import com.ehi.enterprise.android.databinding.TripSummaryBinding;
import com.ehi.enterprise.android.models.location.EHILocation;
import com.ehi.enterprise.android.models.reservation.EHITripSummary;
import com.ehi.enterprise.android.network.interfaces.IApiCallback;
import com.ehi.enterprise.android.network.requests.reservation.GetInvoiceRequest;
import com.ehi.enterprise.android.network.responses.ResponseWrapper;
import com.ehi.enterprise.android.network.responses.reservation.GetInvoiceResponse;
import com.ehi.enterprise.android.ui.viewmodel.ManagersAccessViewModel;
import com.ehi.enterprise.android.utils.BitmapUtils;
import com.ehi.enterprise.android.utils.DLog;
import com.isobar.android.tokenizedstring.TokenizedString;

import java.io.File;
import java.io.IOException;

import io.dwak.reactor.ReactorVar;
import io.dwak.reactor.unbinder.annotation.AutoUnbindAll;

@AutoUnbindAll
public class InvoiceViewModel extends ManagersAccessViewModel {

    private static final String TAG = "InvoiceViewModel";

    private String invoiceNumber;
    private ReactorVar<EHITripSummary> invoice = new ReactorVar<>();
    private ReactorVar<String> fileSaved = new ReactorVar<>();

    @Override
    public void onAttachToView() {
        super.onAttachToView();

        if (invoice.getValue() == null) {
            requestInvoice();
        }
    }

    private void requestInvoice() {
        showProgress(true);
        performRequest(new GetInvoiceRequest(invoiceNumber), new IApiCallback<GetInvoiceResponse>() {
            @Override
            public void handleResponse(ResponseWrapper<GetInvoiceResponse> response) {
                showProgress(false);
                if (response.isSuccess()) {
                    invoice.setValue(response.getData().getInvoice());
                } else {
                    errorResponse.setValue(response);
                }
            }
        });
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public EHITripSummary getInvoice() {
        return invoice.getValue();
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void saveReceipt(Activity context) {

        TripSummaryBinding binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.v_condensed_trip_summary, null, false);
        EHITripSummary invoice = getInvoice();
        // header
        binding.rentalAgreement.setText(new TokenizedString.Formatter<EHIStringToken>(context.getResources())
                .addTokenAndValue(EHIStringToken.RENTAL, invoice.getRentalAgreementNumber())
                .formatString(R.string.invoice_rental_number)
                .format());
        if (!TextUtils.isEmpty(invoice.getContractName())) {
            binding.contract.setVisibility(View.VISIBLE);
            binding.contract.setText(new TokenizedString.Formatter<EHIStringToken>(context.getResources())
                    .addTokenAndValue(EHIStringToken.CONTRACT_NAME, invoice.getContractName())
                    .formatString(R.string.invoice_rental_contract_name)
                    .format());
        }

        // trip summary
        final EHILocation pickupLocation = invoice.getPickupLocation();
        final EHILocation returnLocation = invoice.getReturnLocation();

        binding.pickupTime.setText(getFormattedDate(context, invoice.getPickupTime().getTime()));
        binding.returnTime.setText(getFormattedDate(context, invoice.getReturnTime().getTime()));

        binding.pickupNameView.setText(pickupLocation.getName());
        binding.pickupAddressView.setText(pickupLocation.getAddress().getReadableAddress());

        binding.returnNameView.setText(returnLocation.getName());
        binding.returnAddressView.setText(returnLocation.getAddress().getReadableAddress());

        binding.estimatedTotal.setText(invoice.getPriceSummary().getFormattedPriceView());

        saveView(binding.getRoot());
    }

    private void saveView(View view) {
        // Save in user's public pictures directory.
        String fileName = String.format("%s_%s_%s.png", getResources().getString(R.string.app_name), getResources().getString(R.string.invoice_title), invoiceNumber);
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), fileName);
        File outputDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        if (!outputDir.exists()){
            outputDir.mkdirs();
        }

        try {
            BitmapUtils.saveBitmapToPng(BitmapUtils.getBitmapFromView(view), file);
            fileSaved.setValue(file.getAbsolutePath());
        } catch (IOException e) {
            DLog.e(TAG, e);
        }
    }

    public String getFileSaved() {
        return fileSaved.getValue();
    }

    public void setFileSaved(String value) {
        fileSaved.setValue(value);
    }

    public String getFormattedDate(Context context, long date) {
        return DateUtils.formatDateTime(context, date,
                DateUtils.FORMAT_SHOW_TIME |
                        DateUtils.FORMAT_SHOW_DATE |
                        DateUtils.FORMAT_SHOW_YEAR |
                        DateUtils.FORMAT_ABBREV_MONTH);
    }

    public boolean shouldShowCurrencyWarning(EHILocation location) {
        return !getManagers().getLocalDataManager().getPreferredCountryCode()
                .equals(location.getAddress().getCountryCode());
    }
}
