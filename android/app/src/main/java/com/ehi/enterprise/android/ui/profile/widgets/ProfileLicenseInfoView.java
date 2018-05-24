package com.ehi.enterprise.android.ui.profile.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.databinding.LicenseProfileInfoBinding;
import com.ehi.enterprise.android.models.profile.EHILicenseProfile;
import com.ehi.enterprise.android.ui.view.DataBindingViewModelView;
import com.ehi.enterprise.android.ui.viewmodel.ManagersAccessViewModel;
import com.isobar.android.viewmodel.ViewModel;

@ViewModel(ManagersAccessViewModel.class)
public class ProfileLicenseInfoView extends DataBindingViewModelView<ManagersAccessViewModel, LicenseProfileInfoBinding> {

    private static final String TAG = ProfileLicenseInfoView.class.getSimpleName();

    private EHILicenseProfile mProfile;

    public ProfileLicenseInfoView(Context context) {
        this(context, null, 0);
    }

    public ProfileLicenseInfoView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ProfileLicenseInfoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        if (isInEditMode()) {
            addView(inflate(context, R.layout.v_profile_license_info, null));
            return;
        }

        createViewBinding(R.layout.v_profile_license_info);
    }

    public void setLicenseProfile(EHILicenseProfile licenseProfile) {
        mProfile = licenseProfile;
        getViewBinding().licenseNumber.setText(licenseProfile.getLicenseNumber());
    }

    public void showIssueDate(boolean show) {
        if (show && mProfile.getLicenseIssue() != null) {
            getViewBinding().issueDateText.setText(mProfile.getLicenseIssue());
            getViewBinding().issueDateGroup.setVisibility(View.VISIBLE);
        } else {
            getViewBinding().issueDateText.setText("");
            getViewBinding().issueDateGroup.setVisibility(View.GONE);
        }
    }

    public void showExpiationDate(boolean show) {
        if (show && mProfile.getLicenseExpiry() != null) {
            getViewBinding().expirationDateTextView.setText(mProfile.getLicenseExpiry());
            getViewBinding().expirationDateGroup.setVisibility(View.VISIBLE);
        } else {
            getViewBinding().expirationDateTextView.setText("");
            getViewBinding().expirationDateGroup.setVisibility(View.GONE);
        }
    }

}