package com.ehi.enterprise.android.ui.confirmation.widgets;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.databinding.AdditionalInformationViewBinding;
import com.ehi.enterprise.android.databinding.ItemAdditionalInfoViewBinding;
import com.ehi.enterprise.android.models.reservation.EHIAdditionalInformation;
import com.ehi.enterprise.android.models.reservation.EHIContract;
import com.ehi.enterprise.android.models.reservation.EHISupportedValues;
import com.ehi.enterprise.android.network.requests.reservation.ISO8601DateTypeAdapter;
import com.ehi.enterprise.android.ui.view.DataBindingViewModelView;
import com.ehi.enterprise.android.utils.TimeUtils;
import com.isobar.android.viewmodel.ViewModel;

import java.util.List;

import io.dwak.reactorbinding.view.ReactorView;

@ViewModel(AdditionalInformationViewModel.class)
public class AdditionalInformationView extends DataBindingViewModelView<AdditionalInformationViewModel, AdditionalInformationViewBinding> {

    private static final String TYPE_DROP_DOWN = "dropdown";
    private static final String TYPE_DATE = "date";

    private boolean mIsValid = true;

    public AdditionalInformationView(Context context) {
        this(context, null, 0);
    }

    public AdditionalInformationView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AdditionalInformationView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        if (isInEditMode()) {
            addView(inflate(context, R.layout.v_additional_information, null));
            return;
        }

        createViewBinding(R.layout.v_additional_information);
    }

    @Override
    protected void initDependencies() {
        super.initDependencies();

        bind(ReactorView.visibility(getViewModel().greenArrow.visibility(), getViewBinding().arrowImage));
    }

    public void setupView(List<EHIAdditionalInformation> informationList, EHIContract corporateAccount, boolean readOnly) {
        StringBuilder textBuilder = new StringBuilder();
        textBuilder.append(getResources().getString(R.string.additional_info_add_your_prefix_text));
        textBuilder.append(" ");

        String name;
        String value;

        getViewBinding().container.removeAllViews();

        List<EHIAdditionalInformation> allAdditionalInfo = corporateAccount.getAllAdditionalInformation();

        boolean atLeastOneAfterRateProvided = false;
        boolean allInfoIsPreRate = true;
        mIsValid = true;
        for (int i = 0; i < allAdditionalInfo.size(); i++) {
            ItemAdditionalInfoViewBinding binding = DataBindingUtil.inflate(
                    LayoutInflater.from(getContext()),
                    R.layout.item_additional_info,
                    getViewBinding().container,
                    false);

            EHIAdditionalInformation corpInformation = allAdditionalInfo.get(i);
            EHIAdditionalInformation userInformation = null;

            for (int j = 0; j < informationList.size(); j++) {
                if (informationList.get(j).getId().equals(corpInformation.getId())) {
                    userInformation = informationList.get(j);
                }
            }

            name = corpInformation != null ? corpInformation.getName() : "";
            name = name == null ? "" : name;
            if (corpInformation != null && !corpInformation.isRequired()) {
                name += " " + getResources().getString(R.string.additional_info_header_optional);
            }

            value = userInformation == null ? "" : userInformation.getValue();
            if (corpInformation.getType() != null && corpInformation.getType().equalsIgnoreCase(TYPE_DATE) && value != null) {
                value = TimeUtils.getMediumDate(getContext(), ISO8601DateTypeAdapter.fromString(value));
            } else if (corpInformation.getType() != null && corpInformation.getType().equalsIgnoreCase(TYPE_DROP_DOWN)) {
                List<EHISupportedValues> supportedValues = corpInformation.getSupportedValues();
                for (int j = 0; j < supportedValues.size(); j++) {
                    if (supportedValues.get(j).getValue().equalsIgnoreCase(value)) {
                        value = supportedValues.get(j).getName();
                        break;
                    }
                }
            }

            if (i > 0) {
                textBuilder.append(", ");
            }
            textBuilder.append(name);

            binding.infoName.setText(name + ":");
            binding.infoValue.setText(value);

            if (TextUtils.isEmpty(value)) {
                binding.infoValue.setVisibility(View.GONE);
                binding.notProvidedView.setVisibility(View.VISIBLE);
                if (corpInformation.isRequired()) {
                    mIsValid = false;
                }
            } else {
                if (!corpInformation.isPreRateInfo()) {
                    atLeastOneAfterRateProvided = true;
                }
                binding.infoValue.setVisibility(View.VISIBLE);
                binding.notProvidedView.setVisibility(View.GONE);
            }

            if (!corpInformation.isPreRateInfo()){
                allInfoIsPreRate = false;
            }

            getViewBinding().container.addView(binding.getRoot());
        }

        getViewBinding().additionalInfoEnumText.setText(textBuilder.toString());

        if (atLeastOneAfterRateProvided
                || readOnly
                || allInfoIsPreRate) {
            getViewBinding().container.setVisibility(View.VISIBLE);
            getViewBinding().additionalInfoEnumText.setVisibility(View.GONE);
            getViewBinding().addInformationButton.setVisibility(View.GONE);
            if (readOnly) {
                getViewBinding().arrowImage.setVisibility(View.GONE);
            } else {
                getViewBinding().arrowImage.setVisibility(View.VISIBLE);
            }
        } else {
            getViewBinding().container.setVisibility(View.GONE);
            getViewBinding().additionalInfoEnumText.setVisibility(View.VISIBLE);
            getViewBinding().addInformationButton.setVisibility(View.VISIBLE);
            getViewBinding().arrowImage.setVisibility(View.GONE);
        }
    }

    public boolean isValid() {
        return mIsValid;
    }

    public void hideGreenArrow() {
        getViewModel().hideGreenArrow();
    }

    public void showGreenArrow() {
        getViewModel().showGreenArrow();
    }
}
