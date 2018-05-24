package com.ehi.enterprise.android.ui.reservation.widget;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.databinding.EditDCDetailsViewBinding;
import com.ehi.enterprise.android.models.profile.EHIAddressProfile;
import com.ehi.enterprise.android.models.reservation.EHIDCDetails;
import com.ehi.enterprise.android.ui.view.DataBindingViewModelView;
import com.ehi.enterprise.android.ui.viewmodel.ManagersAccessViewModel;
import com.ehi.enterprise.android.utils.EHITextUtils;
import com.ehi.enterprise.android.utils.PhoneFormatTextWatcher;
import com.isobar.android.viewmodel.ViewModel;

import java.util.LinkedList;
import java.util.List;

@ViewModel(ManagersAccessViewModel.class)
public class DeliveryCollectionEditDetailsView extends DataBindingViewModelView<ManagersAccessViewModel, EditDCDetailsViewBinding> {

    private static final String TAG = "DeliveryCollectionEditDetailsView";

    private String mDigitsOnlyPhone = "";

    private DeliveryCollectionEditDetailsListener mListener;

    private TextWatcher mStreetAdressWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            getViewBinding().streetAddressLayout.setError(EHITextUtils.isEmpty(getStreet()) ? " " : null);
            if (mListener != null) {
                mListener.onChange();
            }
        }
    };

    private TextWatcher mCityTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            getViewBinding().cityLayout.setError(EHITextUtils.isEmpty(getCity()) ? " " : null);
            if (mListener != null) {
                mListener.onChange();
            }
        }
    };

    private TextWatcher mPhoneTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            getViewBinding().phoneLayout.setError(EHITextUtils.isEmpty(getPhone()) ? " " : null);
            if (mListener != null) {
                mListener.onChange();
            }
        }
    };


    public DeliveryCollectionEditDetailsView(Context context) {
        this(context, null, 0);
    }

    public DeliveryCollectionEditDetailsView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DeliveryCollectionEditDetailsView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        if (isInEditMode()) {
            addView(inflate(context, R.layout.v_dc_edit_details, null));
            return;
        }

        createViewBinding(R.layout.v_dc_edit_details);
        initViews();
    }

    private void initViews() {
        getViewBinding().phone.addTextChangedListener(new PhoneFormatTextWatcher() {
            @Override
            public void onPhoneNumberFormatted(String formattedNumber, String digitOnlyNumber) {
                mDigitsOnlyPhone = digitOnlyNumber;
                getViewBinding().phone.setText(formattedNumber);
                getViewBinding().phone.setSelection(formattedNumber.length());
            }
        });

        getViewBinding().streetAddress.addTextChangedListener(mStreetAdressWatcher);
        getViewBinding().city.addTextChangedListener(mCityTextWatcher);
        getViewBinding().phone.addTextChangedListener(mPhoneTextWatcher);
    }

    public String getStreet() {
        return getViewBinding().streetAddress.getText().toString();
    }

    public String getStreetSecondLine() {
        return getViewBinding().streetAddressSecondLine.getText().toString();
    }

    public String getCity() {
        return getViewBinding().city.getText().toString();
    }

    public String getZip() {
        return getViewBinding().zip.getText().toString();
    }

    public String getPhone() {
        return mDigitsOnlyPhone;
    }

    public String getComment() {
        return getViewBinding().comment.getText().toString();
    }

    public EHIDCDetails getDetails() {
        List<String> streets = new LinkedList<>();
        streets.add(getStreet());
        streets.add(getStreetSecondLine());

        return new EHIDCDetails(
                streets, getCity(), getZip(), getPhone(), getComment()
        );
    }

    public void setDCDetail(EHIDCDetails details) {
        if (details == null) {
            return;
        }

        final EHIAddressProfile address = details.getAddress();
        if (address != null) {
            final List<String> streetAddresses = address.getStreetAddresses();
            if (streetAddresses != null && streetAddresses.size() > 0) {
                getViewBinding().streetAddress.setText(streetAddresses.get(0));

                if (streetAddresses.size() > 1) {
                    getViewBinding().streetAddressSecondLine.setText(streetAddresses.get(1));
                }
            }

            if (address.getCity() != null) {
                getViewBinding().city.setText(address.getCity());
            }

            if (address.getPostal() != null) {
                getViewBinding().zip.setText(address.getPostal());
            }
        }

        if (details.getPhone() != null
                && details.getPhone().getPhoneNumber() != null) {
            getViewBinding().phone.setText(details.getPhone().getPhoneNumber());
        }

        getViewBinding().comment.setText(details.getComment());
    }

    public void setListener(DeliveryCollectionEditDetailsListener listener) {
        mListener = listener;
    }

    public boolean isReadyForSubmit() {
        return !EHITextUtils.isEmpty(getStreet())
                && !EHITextUtils.isEmpty(getCity())
                && !EHITextUtils.isEmpty(getPhone());
    }

    public void markInvalid() {

        getViewBinding().streetAddressLayout.setError(EHITextUtils.isEmpty(getStreet()) ? " " : null);

        getViewBinding().cityLayout.setError(EHITextUtils.isEmpty(getCity()) ? " " : null);

        getViewBinding().phoneLayout.setError(EHITextUtils.isEmpty(getPhone()) ? " " : null);
    }

    public interface DeliveryCollectionEditDetailsListener {
        void onChange();
    }
}
