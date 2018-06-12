package com.ehi.enterprise.android.ui.enroll.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.app.EHIStringToken;
import com.ehi.enterprise.android.databinding.EnrollStepTwoViewBinding;
import com.ehi.enterprise.android.models.enroll.EHIEnrollProfile;
import com.ehi.enterprise.android.models.profile.EHICountry;
import com.ehi.enterprise.android.models.profile.EHIRegion;
import com.ehi.enterprise.android.ui.enroll.CountryContract;
import com.ehi.enterprise.android.ui.enroll.FormContract;
import com.ehi.enterprise.android.ui.view.DataBindingViewModelView;
import com.isobar.android.tokenizedstring.TokenizedString;
import com.isobar.android.viewmodel.ViewModel;

import java.util.List;

import io.dwak.reactor.ReactorComputation;
import io.dwak.reactor.interfaces.ReactorComputationFunction;
import io.dwak.reactorbinding.view.ReactorView;
import io.dwak.reactorbinding.widget.ReactorTextInputLayout;
import io.dwak.reactorbinding.widget.ReactorTextView;

@ViewModel(EnrollStepTwoViewModel.class)
public class EnrollStepTwoView extends DataBindingViewModelView<EnrollStepTwoViewModel, EnrollStepTwoViewBinding>
        implements FormContract.FormView, CountryContract.CountryView {

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View view) {
            if (view == getViewBinding().country) {
                countryListener.onCountryClick();
            } else if (view == getViewBinding().subdivision) {
                countryListener.onRegionClick();
            }
        }
    };

    private CountryContract.CountryListener countryListener;
    private FormContract.FormListener formListener;

    public EnrollStepTwoView(Context context) {
        this(context, null);
    }

    public EnrollStepTwoView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EnrollStepTwoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        if (isInEditMode()) {
            addView(inflate(context, R.layout.v_enroll_step_two, null));
            return;
        }
        createViewBinding(R.layout.v_enroll_step_two);

        initViews();
    }

    private void initViews() {
        getViewBinding().country.setOnClickListener(mOnClickListener);
        getViewBinding().subdivision.setOnClickListener(mOnClickListener);

        getViewBinding().address1TextView.setText(new TokenizedString.Formatter<EHIStringToken>(getResources())
                .addTokenAndValue(EHIStringToken.NUMBER, "1")
                .formatString(R.string.street_address)
                .format());

        getViewBinding().address2TextView.setText(new TokenizedString.Formatter<EHIStringToken>(getResources())
                .addTokenAndValue(EHIStringToken.NUMBER, "2")
                .formatString(R.string.street_address)
                .format());
    }

    @Override
    protected void initDependencies() {
        super.initDependencies();

        bind(ReactorTextView.bindText(getViewModel().address, getViewBinding().address1));
        bind(ReactorTextView.bindText(getViewModel().address2, getViewBinding().address2));
        bind(ReactorTextView.bindText(getViewModel().city, getViewBinding().city));
        bind(ReactorTextView.bindText(getViewModel().zipcode, getViewBinding().zipcode));
        bind(ReactorTextView.text(getViewModel().country, getViewBinding().country));
        bind(ReactorTextView.text(getViewModel().subdivision, getViewBinding().subdivision));
        bind(ReactorView.visibility(getViewModel().subdivisionArea.visibility(), getViewBinding().subdivisionArea));

        bind(ReactorView.visibility(getViewModel().enrollStepTitleArea.visibility(), getViewBinding().enrollStepTitleArea));
        bind(ReactorTextView.text(getViewModel().enrollStepTitle, getViewBinding().enrollStepTitle));

        bind(ReactorTextInputLayout.error(getViewModel().addressError, getViewBinding().addressLayout));
        bind(ReactorTextInputLayout.error(getViewModel().cityError, getViewBinding().cityLayout));
        bind(ReactorTextInputLayout.error(getViewModel().zipcodeError, getViewBinding().zipcodeLayout));

        addReaction(new ReactorComputationFunction() {
            @Override
            public void react(ReactorComputation reactorComputation) {
                formListener.isValid(getViewModel().isValid.getValue());
            }
        });
    }

    public void showHeader() {
        getViewModel().showHeader();
    }

    public void hideHeader() {
        getViewModel().hideHeader();
    }

    public void setCountryListener(CountryContract.CountryListener listener) {
        countryListener = listener;
    }

    public void setFormListener(FormContract.FormListener listener) {
        formListener = listener;
    }

    public void setPresetData(EHIEnrollProfile ehiEnrollProfile) {
        getViewModel().setPresetData(ehiEnrollProfile);
    }

    public EHIEnrollProfile updateEnrollProfile(EHIEnrollProfile ehiEnrollProfile) {
        return getViewModel().updateEnrollProfile(ehiEnrollProfile);
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
