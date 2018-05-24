package com.ehi.enterprise.android.ui.reservation.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.app.EHIStringToken;
import com.ehi.enterprise.android.databinding.DetailsSectionViewBinding;
import com.ehi.enterprise.android.models.profile.ProfileCollection;
import com.ehi.enterprise.android.models.reservation.EHIAdditionalInformation;
import com.ehi.enterprise.android.models.reservation.EHIAirlineDetails;
import com.ehi.enterprise.android.models.reservation.EHIContract;
import com.ehi.enterprise.android.models.reservation.EHIDriverInfo;
import com.ehi.enterprise.android.models.reservation.EHIReservation;
import com.ehi.enterprise.android.ui.reservation.TripPurposeFragment;
import com.ehi.enterprise.android.ui.reservation.interfaces.OnReviewTripPurposeListener;
import com.ehi.enterprise.android.ui.view.DataBindingViewModelView;
import com.ehi.enterprise.android.utils.ListUtils;
import com.isobar.android.tokenizedstring.TokenizedString;
import com.isobar.android.viewmodel.ViewModel;

import java.util.List;

@ViewModel(DetailsSectionViewModel.class)
public class DetailsSectionView extends DataBindingViewModelView<DetailsSectionViewModel, DetailsSectionViewBinding> {

    private static final String TAG = "DetailsSectionView";

    private OnDetailsSectionEventListener onDetailsSectionEventListener;
    private OnDetailsSectionPaymentMethodEventListener mOnDetailsSectionPaymentMethodEventListener;
    private OnReviewTripPurposeListener onReviewTripPurposeListener;
    private BillingAccountView.IBillingCallBack iBillingCallBack;

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view == getViewBinding().reviewDriverInfoView) {
                if (onDetailsSectionEventListener != null) {
                    onDetailsSectionEventListener.onDriverInfoClicked();
                }
            } else if (view == getViewBinding().dcDetailsView) {
                if (onDetailsSectionEventListener != null) {
                    onDetailsSectionEventListener.onDeliveryCollectDetailsClicked();
                }
            } else if (view == getViewBinding().additionalInformationView) {
                if (onDetailsSectionEventListener != null) {
                    onDetailsSectionEventListener.onAdditionalInformationClicked();
                }
            }
        }
    };

    private View.OnClickListener onPaymentMethodDetailsClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mOnDetailsSectionPaymentMethodEventListener != null) {
                mOnDetailsSectionPaymentMethodEventListener.onPaymentMethodDetailsClicked();
            }
        }
    };

    private View.OnClickListener mFlightDetailsListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (onDetailsSectionEventListener != null) {
                onDetailsSectionEventListener.onFlightDetailsClicked();
            }
        }
    };

    private OnReviewTripPurposeListener mOnReviewTripPurposeListener = new OnReviewTripPurposeListener() {
        @Override
        public void onTripPurposeChanged(String tripPurpose) {
            getViewModel().setTripPurpose(tripPurpose);

            populateDeliveryAndCollection();

            if (tripPurpose.equalsIgnoreCase(TripPurposeFragment.TRIP_TYPE_LEISURE)) {
                getViewBinding().billingAccountView.setVisibility(View.GONE);
            } else {
                getViewBinding().billingAccountView.setVisibility(View.VISIBLE);
            }

            if (onReviewTripPurposeListener != null) {
                onReviewTripPurposeListener.onTripPurposeChanged(
                        tripPurpose
                );
            }
        }
    };

    public DetailsSectionView(Context context) {
        this(context, null);
    }

    public DetailsSectionView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DetailsSectionView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        if (isInEditMode()) {
            addView(inflate(context, R.layout.v_details_section, null));
            return;
        }

        createViewBinding(R.layout.v_details_section);
        initViews();
    }

    public void setDetailsForConfirmation(EHIReservation ehiReservation, boolean isModify,
                                          EHIDriverInfo ehiDriverInfo, ProfileCollection ProfileCollection,
                                          List<EHIAdditionalInformation> additionalInformationList) {
        setDetails(ehiReservation, isModify, ehiDriverInfo, ProfileCollection, additionalInformationList, true);
    }

    public void setDetailsForReview(EHIReservation ehiReservation, boolean isModify,
                                    EHIDriverInfo ehiDriverInfo, ProfileCollection ProfileCollection,
                                    List<EHIAdditionalInformation> additionalInformationList) {
        setDetails(ehiReservation, isModify, ehiDriverInfo, ProfileCollection, additionalInformationList, false);
    }

    private void setDetails(EHIReservation ehiReservation,
                            boolean isModify,
                            EHIDriverInfo ehiDriverInfo,
                            ProfileCollection ehiProfile,
                            List<EHIAdditionalInformation> additionalInformationList,
                            boolean isConfirmation) {
        getViewModel().setEhiReservation(ehiReservation);
        getViewModel().setModify(isModify);
        getViewModel().setAdditionalInformationList(additionalInformationList);
        getViewModel().setConfirmation(isConfirmation);

        setDriverInfo(ehiDriverInfo);
        setConfirmationPaymentMethod();
        setAdditionalInformation();
        setTripPurposeTitle();
        showFlightDetailsView();
        populateDeliveryAndCollection();
        setBilling(ehiProfile);
    }

    public void populateDeliveryAndCollection(EHIReservation ehiReservation) {
        getViewModel().setEhiReservation(ehiReservation);
        populateDeliveryAndCollection();
    }

    public void setTripPurpose(String tripPurpose) {
        getViewModel().setTripPurpose(tripPurpose);
    }

    public void setOnDetailsSectionEventListener(OnDetailsSectionEventListener onDetailsSectionEventListener) {
        this.onDetailsSectionEventListener = onDetailsSectionEventListener;
    }

    public void setOnDetailsSectionPaymentMethodEventListener(OnDetailsSectionPaymentMethodEventListener onDetailsSectionPaymentMethodEventListener) {
        mOnDetailsSectionPaymentMethodEventListener = onDetailsSectionPaymentMethodEventListener;
    }

    public void setiBillingCallBack(BillingAccountView.IBillingCallBack iBillingCallBack) {
        this.iBillingCallBack = iBillingCallBack;
    }

    public void setOnReviewTripPurposeListener(OnReviewTripPurposeListener onReviewTripPurposeListener) {
        this.onReviewTripPurposeListener = onReviewTripPurposeListener;
    }


    public void setDriverInfo(EHIDriverInfo driverInfo) {
        if (driverInfo != null) {
            getViewBinding().reviewDriverInfoView.setVisibility(VISIBLE);
            getViewBinding().reviewDriverInfoView.setDriverInfo(driverInfo);
        } else {
            getViewBinding().reviewDriverInfoView.setVisibility(GONE);
        }
    }

    public void setConfirmationPaymentMethod() {
        getViewBinding().paymentMethodConfirmationView.setupView(getViewModel().getEhiReservation(), getViewModel().isModify(), getViewModel().isConfirmation());
    }

    public void setAdditionalInformation() {
        final EHIReservation ehiReservation = getViewModel().getEhiReservation();

        final EHIContract account = ehiReservation.getCorporateAccount();
        if (account != null && !ListUtils.isEmpty(account.getAllAdditionalInformation())
                && (!ListUtils.isEmpty(getViewModel().getAdditionalInformationList()) || !getViewModel().isConfirmation())) {
            getViewBinding().additionalInformationView.setVisibility(View.VISIBLE);
            getViewBinding().additionalInformationView.setupView(
                    getViewModel().getAdditionalInformationList(),
                    account,
                    false
            );
        } else {
            getViewBinding().additionalInformationView.setVisibility(View.GONE);
        }
    }

    public void setTripPurposeTitle() {
        final EHIReservation ehiReservation = getViewModel().getEhiReservation();
        if (ehiReservation.getCorporateAccount() != null) {
            getViewBinding().reviewTripPurposeView.setTripPurposeTitle(new TokenizedString.Formatter<EHIStringToken>(getResources())
                    .formatString(R.string.reservation_review_travel_purpose_question)
                    .addTokenAndValue(EHIStringToken.NAME, ehiReservation.getCorporateAccount().getContractName())
                    .format());
        }
    }

    public void hideGreenArrows() {
        getViewBinding().reviewDriverInfoView.hideGreenArrow();
        getViewBinding().flightDetailsView.hideGreenArrow();
        getViewBinding().additionalInformationView.hideGreenArrow();
        getViewBinding().dcDetailsView.hideGreenArrow();
    }

    public void showTripPurpose() {
        getViewBinding().reviewTripPurposeView.setVisibility(View.VISIBLE);
    }

    public void showFlightDetailsView() {
        getViewBinding().flightDetailsView.setVisibility(VISIBLE);
    }

    public void setCurrentFlightDetails(final EHIAirlineDetails value, final String flightNumber, boolean isMultiTerminal) {
        getViewBinding().flightDetailsView.setCurrentFlightDetails(value, flightNumber, isMultiTerminal);

        if (getViewModel().isConfirmation()) {
            getViewBinding().flightDetailsView.setVisibilityForContent();
        }
    }

    public void hideFlightDetailsView() {
        getViewBinding().flightDetailsView.setVisibility(GONE);
    }

    private void initViews() {
        getViewBinding().reviewDriverInfoView.setOnClickListener(mOnClickListener);
        getViewBinding().paymentMethodConfirmationView.setTermsAndConditionsClickListener(onPaymentMethodDetailsClickListener);
        getViewBinding().flightDetailsView.setExternalClickListener(mFlightDetailsListener);
        getViewBinding().dcDetailsView.setOnClickListener(mOnClickListener);
        getViewBinding().additionalInformationView.setOnClickListener(mOnClickListener);
        getViewBinding().reviewTripPurposeView.setOnReviewTripPurposeListener(mOnReviewTripPurposeListener);
    }

    private void populateDeliveryAndCollection() {
        final EHIReservation ehiReservation = getViewModel().getEhiReservation();

        if (getViewModel().isConfirmation()) {
            getViewBinding().dcDetailsView.populateWithData(
                    ehiReservation.isDeliveryAllowed(),
                    ehiReservation.isCollectionAllowed(),
                    true,
                    true,
                    ehiReservation.getVehicleLogistic(),
                    true
            );
        } else {
            getViewBinding().dcDetailsView.populateWithData(
                    ehiReservation.isDeliveryAllowed(),
                    ehiReservation.isCollectionAllowed(),
                    ehiReservation.contractHasAdditionalBenefits(),
                    TripPurposeFragment.TRIP_TYPE_BUSINESS.equalsIgnoreCase(getViewModel().getTripPurpose()),
                    ehiReservation.getVehicleLogistic(),
                    false
            );
            getViewBinding().dcDetailsView.setIconsVisibility(View.VISIBLE);
            getViewBinding().dcDetailsView.setIconsRes(R.drawable.arrow_green);
        }
    }

    private void setBilling(ProfileCollection ProfileCollection) {
        final EHIReservation ehiReservation = getViewModel().getEhiReservation();

        if (getViewModel().isModify() || getViewModel().isConfirmation()) {
            getViewBinding().reviewTripPurposeView.setVisibility(View.GONE);
            getViewBinding().billingAccountView.setVisibility(View.GONE);
        } else {
            getViewBinding().billingAccountView.populateView(
                    ProfileCollection,
                    ehiReservation,
                    iBillingCallBack
            );
        }
    }

    public boolean isAdditionalInformationValid() {
        return getViewBinding().additionalInformationView.isValid();
    }

    public float getAdditionalInformationY() {
        return getViewBinding().additionalInformationView.getY();
    }

    public interface OnDetailsSectionEventListener {
        void onDriverInfoClicked();
        void onFlightDetailsClicked();
        void onDeliveryCollectDetailsClicked();
        void onAdditionalInformationClicked();
    }

    public interface OnDetailsSectionPaymentMethodEventListener {
        void onPaymentMethodDetailsClicked();
    }

}