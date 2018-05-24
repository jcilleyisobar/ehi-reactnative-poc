package com.ehi.enterprise.android.ui.enroll.widget;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.databinding.EnrollStepOneViewBinding;
import com.ehi.enterprise.android.models.enroll.EHIEnrollProfile;
import com.ehi.enterprise.android.models.profile.EHICountry;
import com.ehi.enterprise.android.models.profile.EHIRegion;
import com.ehi.enterprise.android.models.profile.EHIRenterSearchCriteria;
import com.ehi.enterprise.android.ui.enroll.CountryContract;
import com.ehi.enterprise.android.ui.enroll.FormContract;
import com.ehi.enterprise.android.ui.view.DataBindingViewModelView;
import com.ehi.enterprise.android.utils.BaseAppUtils;
import com.ehi.enterprise.android.utils.DialogUtils;
import com.isobar.android.viewmodel.ViewModel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import io.dwak.reactor.Reactor;
import io.dwak.reactor.ReactorComputation;
import io.dwak.reactor.ReactorVar;
import io.dwak.reactor.interfaces.ReactorComputationFunction;
import io.dwak.reactorbinding.view.ReactorView;
import io.dwak.reactorbinding.widget.ReactorTextInputLayout;
import io.dwak.reactorbinding.widget.ReactorTextView;

@ViewModel(EnrollStepOneViewModel.class)
public class EnrollStepOneView extends DataBindingViewModelView<EnrollStepOneViewModel, EnrollStepOneViewBinding>
        implements FormContract.FormView, CountryContract.CountryView {

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View view) {
            if (view == getViewBinding().country) {
                countryListener.onCountryClick();
            } else if (view == getViewBinding().subdivision) {
                countryListener.onRegionClick();
            } else if (view == getViewBinding().issueDate) {
                showIssueDatePickerDialog();
            } else if (view == getViewBinding().expirationDate) {
                showExpirationDatePickerDialog();
            } else if (view == getViewBinding().birthDate) {
                showBirthDatePickerDialog();
            }
        }
    };

    private CountryContract.CountryListener countryListener;
    private FormContract.FormListener formListener;

    public EnrollStepOneView(Context context) {
        this(context, null);
    }

    public EnrollStepOneView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EnrollStepOneView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        if (isInEditMode()) {
            addView(inflate(context, R.layout.v_enroll_step_one, null));
            return;
        }

        createViewBinding(R.layout.v_enroll_step_one);
        initViews();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        getViewModel().setDateFormatter((SimpleDateFormat) DateFormat.getDateFormat(getContext()));
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        getViewModel().setDateFormatter(null);
    }

    private void initViews() {
        getViewModel().setDateFormatter((SimpleDateFormat) DateFormat.getDateFormat(getContext()));

        getViewBinding().country.setOnClickListener(mOnClickListener);
        getViewBinding().subdivision.setOnClickListener(mOnClickListener);
        getViewBinding().issueDate.setOnClickListener(mOnClickListener);
        getViewBinding().expirationDate.setOnClickListener(mOnClickListener);
        getViewBinding().birthDate.setOnClickListener(mOnClickListener);

        getViewBinding().firstName.requestFocus();
        getViewBinding().lastName.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    BaseAppUtils.hideKeyboard((FragmentActivity)getContext());
                    getViewBinding().stepOneFormLayout.requestFocus();
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    protected void initDependencies() {
        super.initDependencies();

        bind(ReactorTextView.bindText(getViewModel().firstName, getViewBinding().firstName));
        bind(ReactorTextView.bindText(getViewModel().lastName, getViewBinding().lastName));
        bind(ReactorTextView.bindText(getViewModel().licenseNumber, getViewBinding().driverLicense));
        bind(ReactorTextView.bindText(getViewModel().issueDateText, getViewBinding().issueDate));
        bind(ReactorTextView.bindText(getViewModel().expiryDateText, getViewBinding().expirationDate));
        bind(ReactorTextView.bindText(getViewModel().birthDateText, getViewBinding().birthDate));
        bind(ReactorTextView.text(getViewModel().country, getViewBinding().country));
        bind(ReactorTextView.text(getViewModel().subdivision, getViewBinding().subdivision));
        bind(ReactorView.visibility(getViewModel().subdivisionArea.visibility(), getViewBinding().subdivisionArea));
        bind(ReactorView.visibility(getViewModel().issueDateArea.visibility(), getViewBinding().issueDateLayout));
        bind(ReactorView.visibility(getViewModel().issueDateTitle.visibility(), getViewBinding().issueDateTitle));
        bind(ReactorView.visibility(getViewModel().expiryDateArea.visibility(), getViewBinding().expirationDateLayout));
        bind(ReactorView.visibility(getViewModel().expiryDateTitle.visibility(), getViewBinding().expirationDateTitle));

        bind(ReactorView.visibility(getViewModel().enrollStepTitleArea.visibility(), getViewBinding().enrollStepTitleArea));
        bind(ReactorTextView.text(getViewModel().enrollStepTitle, getViewBinding().enrollStepTitle));

        bind(ReactorTextInputLayout.error(getViewModel().firstNameError, getViewBinding().firstNameLayout));
        bind(ReactorTextInputLayout.error(getViewModel().lastNameError, getViewBinding().lastNameLayout));
        bind(ReactorTextInputLayout.error(getViewModel().licenseNumberError, getViewBinding().driverLicenseLayout));
        bind(ReactorTextInputLayout.error(getViewModel().licenseBirthDateError, getViewBinding().birthDateLayout));
        bind(ReactorTextInputLayout.error(getViewModel().licenseIssueDateError, getViewBinding().issueDateLayout));
        bind(ReactorTextInputLayout.error(getViewModel().licenseExpiryDateError, getViewBinding().expirationDateLayout));

        addReaction(new ReactorComputationFunction() {
            @Override
            public void react(ReactorComputation reactorComputation) {
                formListener.isValid(getViewModel().isValid.getValue());
            }
        });
    }

    private void showBirthDatePickerDialog() {
        DialogUtils.showDatePicker(
                getContext(),
                R.string.enroll_birth,
                getViewModel().getBirthDate(),
                new DialogUtils.OnDateSelectedListener() {
                    @Override
                    public void onDateSelected(Date selectedDate) {
                        getViewModel().setBirthDate(selectedDate);
                    }
                }
        );
    }

    private void showIssueDatePickerDialog() {
        DialogUtils.showDatePicker(
                getContext(),
                R.string.profile_license_issue_date,
                getViewModel().getLicenseIssueDate(),
                new DialogUtils.OnDateSelectedListener() {
                    @Override
                    public void onDateSelected(Date selectedDate) {
                        getViewModel().setLicenseIssueDate(selectedDate);
                    }
                }
        );
    }

    private void showExpirationDatePickerDialog() {
        DialogUtils.showDatePicker(
                getContext(),
                R.string.profile_license_expiration_date_title,
                getViewModel().getLicenseExpiryDate(),
                new DialogUtils.OnDateSelectedListener() {
                    @Override
                    public void onDateSelected(Date selectedDate) {
                        getViewModel().setLicenseExpiryDate(selectedDate);
                    }
                }
        );
    }

    public void setCountryListener(CountryContract.CountryListener listener) {
        countryListener = listener;
    }

    public void setFormListener(FormContract.FormListener listener) {
        formListener = listener;
    }

    public EHIRenterSearchCriteria getSearchObject(EHICountry country, EHIRegion region) {

        return new EHIRenterSearchCriteria(
                country.getCountryCode(),
                country.isLicenseIssuingAuthorityRequired() ? null : region.getSubdivisionCode(),
                country.isLicenseIssuingAuthorityRequired() ? region.getSubdivisionCode() : null,
                getViewModel().licenseNumber.getValue(),
                getViewModel().lastName.getValue()
        );
    }

    public void setPresetData(EHIEnrollProfile ehiEnrollProfile) {
        getViewModel().setPresetData(ehiEnrollProfile);
    }

    public EHIEnrollProfile updateEnrollProfile(EHIEnrollProfile ehiEnrollProfile) {
        return getViewModel().updateEnrollProfile(ehiEnrollProfile);
    }

    public void showHeader() {
        getViewModel().showHeader();
    }

    public void hideHeader() {
        getViewModel().hideHeader();
    }

    @Override
    public boolean isValid() {
        return getViewModel().isValid();
    }

    @Override
    public void highlightInvalidFields() {
        getViewModel().highlightInvalidFields();
    }

    @Override
    public List<String> getErrorMessageList() {
        return getViewModel().getErrorMessageList();
    }

    @Override
    public void startHighlightInvalidFieldsOnFormChange() {
        getViewModel().startHighlightInvalidFieldsOnFormChange();
    }

    @Override
    public void stopHighlightInvalidFieldsOnFormChange() {
        getViewModel().stopHighlightInvalidFieldsOnFormChange();
    }

    @Override
    public void setCountry(EHICountry selectedCountry) {
        getViewModel().setCountry(selectedCountry);
    }

    @Override
    public EHICountry getCountry() {
        return getViewModel().getCountry();
    }

    @Override
    public String getCountryCode() {
        return getViewModel().getCountryCode();
    }

    @Override
    public void setRegion(EHIRegion selectedRegion) {
        getViewModel().setRegion(selectedRegion);
    }

    @Override
    public void setRegionList(List<EHIRegion> regionList) {
        getViewModel().setRegionList(regionList);
    }

    @Override
    public List<EHIRegion> getRegionList() {
        return getViewModel().getRegionList();
    }
}
