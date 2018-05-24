package com.ehi.enterprise.android.ui.reservation;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.databinding.DeliveryAndCollectionFragmentBinding;
import com.ehi.enterprise.android.models.reservation.EHIDCDetails;
import com.ehi.enterprise.android.models.reservation.EHIReservation;
import com.ehi.enterprise.android.ui.fragment.DataBindingViewModelFragment;
import com.ehi.enterprise.android.ui.reservation.widget.DeliveryCollectionEditDetailsView;
import com.ehi.enterprise.android.utils.DialogUtils;
import com.ehi.enterprise.android.utils.FragmentUtils;
import com.ehi.enterprise.android.utils.analytics.EHIAnalytics;
import com.ehi.enterprise.android.utils.analytics.EHIAnalyticsDictionaryUtils;
import com.ehi.enterprise.android.utils.analytics.EHIAnalyticsEvent;
import com.isobar.android.newinstancer.Extra;
import com.isobar.android.viewmodel.ViewModel;

import io.dwak.reactor.ReactorComputation;
import io.dwak.reactor.interfaces.ReactorComputationFunction;

@ViewModel(DeliveryAndCollectionViewModel.class)
public class DeliveryAndCollectionFragment extends DataBindingViewModelFragment<DeliveryAndCollectionViewModel, DeliveryAndCollectionFragmentBinding> {

    public static final String SCREEN_TAG = "DeliveryAndCollectionFragment";

    @Extra(boolean.class)
    public static final String IS_MODIFY = "ehi.EXTRA_IS_MODIFY";

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view == getViewBinding().deliveryWantCheckRow) {
                showDeliveryDetails(getViewBinding().deliveryWantCheckRow.isChecked());
            } else if (view == getViewBinding().collectionWantCheckRow) {
                showCollectionDetails(getViewBinding().collectionWantCheckRow.isChecked());
            } else if (view == getViewBinding().collectionSameAsDeliveryCheckRow) {
                setSameAsDelivery(getViewBinding().collectionSameAsDeliveryCheckRow.isChecked());
            } else if (view == getViewBinding().saveDetails) {
                saveDCDetails();
            }
        }
    };

    private View.OnClickListener mDisabledOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view == getViewBinding().saveDetails) {
                checkErrors();
            }
        }
    };

    private DeliveryCollectionEditDetailsView.DeliveryCollectionEditDetailsListener mDeliveryCollectionEditDetailsListener = new DeliveryCollectionEditDetailsView.DeliveryCollectionEditDetailsListener() {
        @Override
        public void onChange() {
            updateSaveButton();
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getViewModel().setIsModify(new DeliveryAndCollectionFragmentHelper.Extractor(this).isModify());
        getViewModel().populateReservationObject();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        createViewBinding(inflater, R.layout.fr_delivery_and_collection, container);
        initViews();
        return getViewBinding().getRoot();
    }

    private void initViews() {
        getViewBinding().deliveryWantCheckRow.setOnClickListener(mOnClickListener);
        getViewBinding().collectionWantCheckRow.setOnClickListener(mOnClickListener);
        getViewBinding().collectionSameAsDeliveryCheckRow.setOnClickListener(mOnClickListener);
        getViewBinding().saveDetails.setOnClickListener(mOnClickListener);
        getViewBinding().saveDetails.setOnDisabledClickListener(mDisabledOnClickListener);

        getViewBinding().saveDetails.setEnabled(false);

        getViewBinding().deliveryAddressView.setListener(mDeliveryCollectionEditDetailsListener);
        getViewBinding().collectionAddressView.setListener(mDeliveryCollectionEditDetailsListener);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        getActivity().setTitle(getString(R.string.delivery_collection_header_title));
    }

    @Override
    public void onResume() {
        super.onResume();
        EHIAnalyticsEvent.create()
                .screen(EHIAnalytics.Screen.SCREEN_CORP_RES.value, DeliveryAndCollectionFragment.SCREEN_TAG)
                .state(EHIAnalytics.State.STATE_COLLECT_DELIVERY_INFO.value)
                .addDictionary(EHIAnalyticsDictionaryUtils.reservation())
                .tagScreen()
                .tagEvent();
    }

    @Override
    protected void initDependencies() {
        super.initDependencies();
        bind(FragmentUtils.progress(getViewModel().progress, getActivity()));
        bind(DialogUtils.errorDialog(getViewModel().errorResponse, getActivity()));

        addReaction("RESERVATION_OBJECT_REACTION", new ReactorComputationFunction() {
            @Override
            public void react(ReactorComputation reactorComputation) {
                EHIReservation reservation = getViewModel().getReservationObject();
                if (reservation != null) {
                    if (reservation.isDeliveryAllowed()) {
                        getViewBinding().deliveryNotAvailable.setVisibility(View.GONE);
                        getViewBinding().deliveryWantCheckRow.setVisibility(View.VISIBLE);
                        getViewBinding().deliveryWantCheckRow.setChecked(true);
                        getViewBinding().deliveryAddressHeader.setVisibility(View.VISIBLE);
                        getViewBinding().deliveryAddressView.setVisibility(View.VISIBLE);
                        if (reservation.getVehicleLogistic() != null
                                && reservation.getVehicleLogistic().getDeliveryInfo() != null) {
                            getViewBinding().deliveryAddressView.setDCDetail(reservation.getVehicleLogistic().getDeliveryInfo());
                        }
                    } else {
                        getViewBinding().deliveryNotAvailable.setVisibility(View.VISIBLE);
                        getViewBinding().deliveryAddressHeader.setVisibility(View.GONE);
                        getViewBinding().deliveryAddressView.setVisibility(View.GONE);
                        getViewBinding().deliveryWantCheckRow.setVisibility(View.GONE);
                    }

                    if (reservation.isCollectionAllowed()) {
                        getViewBinding().collectionNotAvailable.setVisibility(View.GONE);
                        getViewBinding().collectionWantCheckRow.setVisibility(View.VISIBLE);
                        getViewBinding().collectionWantCheckRow.setChecked(true);
                        getViewBinding().collectionAddressHeader.setVisibility(View.VISIBLE);
                        if (reservation.isDeliveryAllowed()
                                && getViewBinding().deliveryWantCheckRow.isChecked()) {
                            getViewBinding().collectionSameAsDeliveryCheckRow.setVisibility(View.VISIBLE);
                            getViewBinding().collectionSameAsDeliveryCheckRow.setChecked(true);
                            getViewBinding().collectionAddressView.setVisibility(View.GONE);
                        } else {
                            getViewBinding().collectionSameAsDeliveryCheckRow.setVisibility(View.GONE);
                            getViewBinding().collectionAddressView.setVisibility(View.VISIBLE);
                        }
                        if (reservation.getVehicleLogistic() != null
                                && reservation.getVehicleLogistic().getCollectionInfo() != null) {

                            if (reservation.getVehicleLogistic().isSameAsDelivery()) {
                                getViewBinding().collectionAddressView.setVisibility(View.GONE);
                                getViewBinding().collectionSameAsDeliveryCheckRow.setChecked(true);
                            } else {
                                getViewBinding().collectionAddressView.setDCDetail(reservation.getVehicleLogistic().getCollectionInfo());
                                getViewBinding().collectionAddressView.setVisibility(View.VISIBLE);
                                getViewBinding().collectionSameAsDeliveryCheckRow.setChecked(false);
                            }
                        }

                    } else {
                        getViewBinding().collectionNotAvailable.setVisibility(View.VISIBLE);
                        getViewBinding().collectionWantCheckRow.setVisibility(View.GONE);
                        getViewBinding().collectionSameAsDeliveryCheckRow.setVisibility(View.GONE);
                        getViewBinding().collectionAddressHeader.setVisibility(View.GONE);
                        getViewBinding().collectionAddressView.setVisibility(View.GONE);
                    }
                }
            }
        });

        addReaction("SUCCESS_REACTION", new ReactorComputationFunction() {
            @Override
            public void react(ReactorComputation reactorComputation) {
                if (getViewModel().getSuccessWrapper() != null) {
                    getFragmentManager().popBackStack();
                }
            }
        });
    }

    private void saveDCDetails() {
        EHIDCDetails delivery = null;
        EHIDCDetails collection = null;

        if (getViewModel().getReservationObject().isDeliveryAllowed()) {
            if (getViewBinding().deliveryWantCheckRow.isChecked()) {
                delivery = getViewBinding().deliveryAddressView.getDetails();
            } else {
                delivery = null;
            }

        }

        if (getViewModel().getReservationObject().isCollectionAllowed()) {
            if (getViewBinding().collectionWantCheckRow.isChecked())
                if (getViewBinding().deliveryWantCheckRow.isChecked()
                        && getViewBinding().collectionSameAsDeliveryCheckRow.isChecked()) {
                    if (delivery != null) {
                        collection = new EHIDCDetails(delivery.getAddress(), delivery.getPhone(), "");
                    }
                } else {
                    collection = getViewBinding().collectionAddressView.getDetails();
                }
        } else {
            collection = null;
        }

        if (delivery != null) {
            delivery.getAddress().setCountryCode(getViewModel().getReservationObject().getPickupLocation().getAddress().getCountryCode());
        }

        if (collection != null) {
            collection.getAddress().setCountryCode(getViewModel().getReservationObject().getPickupLocation().getAddress().getCountryCode());
        }

        getViewModel().saveDetails(delivery, collection);
    }


    private void setSameAsDelivery(boolean checked) {
        if (checked) {
            getViewBinding().collectionAddressView.setVisibility(View.GONE);
        } else {
            getViewBinding().collectionAddressView.setVisibility(View.VISIBLE);
        }

        updateSaveButton();
    }

    private void showDeliveryDetails(boolean isDeliveryChecked) {
        if (isDeliveryChecked) {
            getViewBinding().deliveryAddressHeader.setVisibility(View.VISIBLE);
            getViewBinding().deliveryAddressView.setVisibility(View.VISIBLE);
        } else {
            getViewBinding().deliveryAddressHeader.setVisibility(View.GONE);
            getViewBinding().deliveryAddressView.setVisibility(View.GONE);
        }

        //update collection view
        if (getViewModel().getReservationObject().isCollectionAllowed()) {
            boolean isCollectionChecked = getViewBinding().collectionWantCheckRow.isChecked();
            if(isCollectionChecked) {
                getViewBinding().collectionSameAsDeliveryCheckRow.setVisibility(isDeliveryChecked ? View.VISIBLE : View.GONE);
            }
            showCollectionDetails(isCollectionChecked);
        }

        updateSaveButton();
    }

    private void showCollectionDetails(boolean checked) {
        if (checked) {
            getViewBinding().collectionAddressHeader.setVisibility(View.VISIBLE);
            getViewBinding().collectionAddressView.setVisibility(View.VISIBLE);
            if (getViewModel().getReservationObject().isDeliveryAllowed()
                    && getViewBinding().deliveryWantCheckRow.isChecked()) {
                getViewBinding().collectionSameAsDeliveryCheckRow.setVisibility(View.VISIBLE);
                if (getViewBinding().collectionSameAsDeliveryCheckRow.isChecked()) {
                    getViewBinding().collectionAddressView.setVisibility(View.GONE);
                } else {
                    getViewBinding().collectionAddressView.setVisibility(View.VISIBLE);
                }
            } else {
                getViewBinding().collectionSameAsDeliveryCheckRow.setVisibility(View.GONE);
            }
        } else {
            getViewBinding().collectionAddressHeader.setVisibility(View.GONE);
            getViewBinding().collectionAddressView.setVisibility(View.GONE);
            getViewBinding().collectionSameAsDeliveryCheckRow.setVisibility(View.GONE);
        }

        updateSaveButton();
    }

    private void checkErrors() {
        if (shouldVerifyDeliveryContainer()) {
            getViewBinding().deliveryAddressView.markInvalid();
        }

        if (shouldVerifyCollectionContainer()) {
            getViewBinding().collectionAddressView.markInvalid();
        }
    }

    private void updateSaveButton() {
        boolean isReady = true;

        if (shouldVerifyDeliveryContainer()) {
            isReady = getViewBinding().deliveryAddressView.isReadyForSubmit();
        }

        if (shouldVerifyCollectionContainer()) {
            isReady = getViewBinding().collectionAddressView.isReadyForSubmit();
        }

        getViewBinding().saveDetails.setEnabled(isReady);
    }

    private boolean shouldVerifyDeliveryContainer() {
        return getViewModel().getReservationObject().isDeliveryAllowed()
                && getViewBinding().deliveryWantCheckRow.getCompoundButton().isChecked();
    }

    private boolean shouldVerifyCollectionContainer() {
        return getViewModel().getReservationObject().isCollectionAllowed()
                && getViewBinding().collectionWantCheckRow.getCompoundButton().isChecked()
                && (!getViewBinding().deliveryWantCheckRow.getCompoundButton().isChecked()
                || !getViewBinding().collectionSameAsDeliveryCheckRow.getCompoundButton().isChecked());
    }

}
