package com.ehi.enterprise.android.ui.reservation;

import android.content.Context;
import android.util.AttributeSet;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.databinding.InvoiceAdditionalInfoViewBinding;
import com.ehi.enterprise.android.models.reservation.EHITripSummary;
import com.ehi.enterprise.android.models.reservation.EHIVehicleDetails;
import com.ehi.enterprise.android.ui.view.DataBindingViewModelView;
import com.ehi.enterprise.android.ui.viewmodel.ManagersAccessViewModel;
import com.ehi.enterprise.android.utils.EHITextUtils;
import com.isobar.android.viewmodel.ViewModel;

@ViewModel(ManagersAccessViewModel.class)
public class InvoiceAdditionalInfoView extends DataBindingViewModelView<ManagersAccessViewModel, InvoiceAdditionalInfoViewBinding> {

    public InvoiceAdditionalInfoView(Context context) {
        this(context, null, 0);
    }

    public InvoiceAdditionalInfoView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public InvoiceAdditionalInfoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (isInEditMode()) {
            addView(inflate(context, R.layout.v_invoice_additional_info, null));
            return;
        }
        createViewBinding(R.layout.v_invoice_additional_info);
    }

    public void populate(EHITripSummary invoice) {
        populateRenter(invoice);
        populateVehicle(invoice.getVehicleDetails());
        populateDistance(invoice.getVehicleDetails());
    }

    private void populateRenter(EHITripSummary invoice) {
        getViewBinding().nameValue.setText(invoice.getCustomerFirstName() + " " + invoice.getCustomerLastName());
        getViewBinding().memberValue.setText(invoice.getMembershipNumber());
        if (!EHITextUtils.isEmpty(invoice.getContractName())) {
            getViewBinding().contractView.setVisibility(VISIBLE);
            getViewBinding().contractValue.setText(invoice.getContractName());
        }
        getViewBinding().addressValue.setText(invoice.getAddress().getReadableAddress());
    }

    private void populateVehicle(EHIVehicleDetails vehicleDetails) {
        getViewBinding().drivenClassValue.setText(vehicleDetails.getVehicleClassDriven());
        getViewBinding().chargedClassValue.setText(vehicleDetails.getVehicleClassCharged());
        getViewBinding().modelValue.setText(String.format("%s %s", vehicleDetails.getMake(), vehicleDetails.getModel()).trim());
        getViewBinding().plateValue.setText(vehicleDetails.getLicensePlateNumber());
    }

    private void populateDistance(EHIVehicleDetails vehicleDetails) {
        String unit = vehicleDetails.getDistanceUnit().toLowerCase();
        getViewBinding().odometerStartValue.setText(String.format("%s %s", vehicleDetails.getStartingOdometer(), unit));
        getViewBinding().odometerEndValue.setText(String.format("%s %s", vehicleDetails.getEndingOdometer(), unit));
        getViewBinding().distanceValue.setText(String.format("%s %s", vehicleDetails.getDistanceTraveled(), unit));
    }
}
