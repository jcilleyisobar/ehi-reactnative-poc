package com.ehi.enterprise.android.ui.reservation.widget;

import android.view.View;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.app.EHIStringToken;
import com.ehi.enterprise.android.models.location.EHIImage;
import com.ehi.enterprise.android.models.reservation.EHICarClassDetails;
import com.ehi.enterprise.android.models.reservation.EHIPriceDifferences;
import com.ehi.enterprise.android.models.reservation.EHIReservation;
import com.ehi.enterprise.android.ui.viewmodel.ManagersAccessViewModel;
import com.ehi.enterprise.android.utils.ListUtils;
import com.ehi.enterprise.android.utils.image.EHIImageUtils;
import com.ehi.enterprise.android.utils.reactor_extensions.viewstate.ReactorTextViewState;
import com.ehi.enterprise.android.utils.reactor_extensions.viewstate.ReactorViewState;
import com.isobar.android.tokenizedstring.TokenizedString;

import java.text.NumberFormat;
import java.util.Currency;
import java.util.List;

import io.dwak.reactor.ReactorVar;
import io.dwak.reactor.unbinder.annotation.AutoUnbindAll;

@AutoUnbindAll
public class ReviewCarClassViewModel extends ManagersAccessViewModel {

    //region reactive states/vars
    final ReactorTextViewState carClassType = new ReactorTextViewState();
    final ReactorTextViewState carClassTransmission = new ReactorTextViewState();
    final ReactorTextViewState carModelText = new ReactorTextViewState();
    final ReactorVar<List<EHIImage>> classImages = new ReactorVar<>();
    final ReactorViewState carChangeArrow = new ReactorViewState();
    //endregion

    @EHIImageUtils.ImageType
    final int imageType = EHIImageUtils.IMAGE_TYPE_SIDE_PROFILE;

    final ReactorVar<CharSequence> carUpgradeTextInformation = new ReactorVar<>();
    final ReactorViewState carUpgradeContainer = new ReactorViewState();
    private EHIReservation mReservation;
    final ReactorVar<List<EHIImage>> upgradeImages = new ReactorVar<>(null);

    private void setCarClassDetails(EHICarClassDetails carClassDetails) {
        if (carClassDetails != null) {
            classImages.setValue(carClassDetails.getImages());
            carClassType.setText(carClassDetails.getName());
            if (carClassDetails.getMakeModelOrSimilarText() != null) {
                carModelText.setText(new TokenizedString.Formatter<EHIStringToken>(getResources())
                        .formatString(R.string.reservation_car_class_make_model_title)
                        .addTokenAndValue(EHIStringToken.MAKE_MODEL, carClassDetails.getMakeModelOrSimilarText().trim())
                        .format().toString());
            }
            if (carClassDetails.getFeatures() != null) {
                carClassTransmission.setText(EHICarClassDetails.getTransmissionDescription(carClassDetails.getFeatures()));
            }
            if (carClassDetails.isTransmissionTypeManual()) {
                carClassTransmission.setDrawableLeft(R.drawable.icon_manual_transmission);
            }
        }
    }

    @EHIImageUtils.ImageType
    public int getImageType() {
        return imageType;
    }

    public void setUpReservation(EHIReservation currentReservation, boolean shouldShowUpgradeOption) {
        mReservation = currentReservation;
        setCarClassDetails(currentReservation.getCarClassDetails());

        if (!ListUtils.isEmpty(currentReservation.getUpgradeCarClassDetails())
                && shouldShowUpgradeOption) {

            final EHICarClassDetails upgradeCarClass = currentReservation.getUpgradeCarClassDetails().get(0);
            final boolean isPrepay = currentReservation.isPrepaySelected();
            final EHIPriceDifferences upgradePriceDifference = upgradeCarClass.getUpgradePriceDifference(isPrepay);

            if (upgradePriceDifference != null) {

                carUpgradeContainer.setVisibility(View.VISIBLE);

                Currency currency = Currency.getInstance(currentReservation.getCarClassDetails().getPriceSummary(isPrepay).getEstimatedTotalView().getCurrencyCode());
                NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance();
                currencyFormatter.setCurrency(currency);

                String subPrice = String.valueOf(upgradePriceDifference.getDifferenceAmountView().getAmmount());
                String price = currencyFormatter.format(Double.parseDouble(subPrice));

                CharSequence costSummary = new TokenizedString.Formatter<EHIStringToken>(getResources())
                        .formatString(getResources().getString(R.string.review_reservation_upgrade_text))
                        .addTokenAndValue(EHIStringToken.NAME, upgradeCarClass.getName())
                        .addTokenAndValue(EHIStringToken.DIFFERENCE, price)
                        .format();
                carUpgradeTextInformation.setValue(costSummary);

                upgradeImages.setValue(upgradeCarClass.getImages());
            } else {
                carUpgradeContainer.setVisibility(View.GONE);
            }
        } else {
            carUpgradeContainer.setVisibility(View.GONE);
        }
    }

    public String getUpgradeCarId() {
        return mReservation.getUpgradeCarClassDetails().get(0).getCode();
    }

    public void hideGreenArrow() {
        carChangeArrow.setVisibility(ReactorTextViewState.GONE);
    }

    public void showGreenArrow() {
        carChangeArrow.setVisibility(ReactorTextViewState.VISIBLE);
    }
}