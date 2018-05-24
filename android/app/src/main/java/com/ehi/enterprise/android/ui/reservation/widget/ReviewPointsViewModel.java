package com.ehi.enterprise.android.ui.reservation.widget;

import android.view.View;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.app.EHIStringToken;
import com.ehi.enterprise.android.models.reservation.EHICarClassDetails;
import com.ehi.enterprise.android.ui.viewmodel.ManagersAccessViewModel;
import com.ehi.enterprise.android.utils.reactor_extensions.viewstate.ReactorTextViewState;
import com.ehi.enterprise.android.utils.reactor_extensions.viewstate.ReactorViewState;
import com.isobar.android.tokenizedstring.TokenizedString;

import java.text.NumberFormat;

import io.dwak.reactor.unbinder.annotation.AutoUnbindAll;

@AutoUnbindAll
public class ReviewPointsViewModel extends ManagersAccessViewModel {

    //region reactive states

    public final ReactorViewState redemptionEnabledTitle = new ReactorViewState();
    public final ReactorViewState redemptionEnabledTriangle = new ReactorViewState();
    public final ReactorViewState notEnoughtPointsView = new ReactorViewState();
    public final ReactorViewState enoughtPointsTitleView = new ReactorViewState();
    public final ReactorViewState redeemPointsButton = new ReactorViewState();
    public final ReactorViewState pointsExpandedView = new ReactorViewState();
    public final ReactorViewState removePointsFromRentalButton = new ReactorViewState();
    public final ReactorViewState pointsPriceDetailsView = new ReactorViewState();

    public final ReactorTextViewState enoughtPointsValueView = new ReactorTextViewState();
    public final ReactorTextViewState priceDetailsDaysValue = new ReactorTextViewState();
    public final ReactorTextViewState priceDetailsPointsValue = new ReactorTextViewState();

    //endregion

    private boolean mShowPointGlobal = false;
    private boolean mRedeemingPoints = false;
    private boolean mRedemptionAvailableForCarClass = false;
    private int mMaxFreeDaysForCurrentClass = 0;

    private int mPointsUsed = 0;
    private int mDaysUsed = 0;

    private void updateViewState() {
        resetViewsVisibility();
        if (!mRedeemingPoints) {
            redemptionEnabledTitle.setVisibility(ReactorViewState.GONE);
            redemptionEnabledTriangle.setVisibility(ReactorViewState.GONE);
            if (mMaxFreeDaysForCurrentClass == 0
                    || !mRedemptionAvailableForCarClass) {
                //can't redeem
                notEnoughtPointsView.setVisibility(ReactorViewState.VISIBLE);
            } else {
                //can redeem - false here is a demi-flag for EA-3914
                if (false && mShowPointGlobal) {
                    //expanded view
                    enoughtPointsTitleView.setVisibility(ReactorViewState.VISIBLE);
                    enoughtPointsValueView.setVisibility(ReactorViewState.VISIBLE);
                    TokenizedString.Formatter freeDaysString = new TokenizedString.Formatter<>(getResources());
                    if (mMaxFreeDaysForCurrentClass > 1) {
                        freeDaysString.formatString(R.string.redemption_free_days_subtitle)
                                .addTokenAndValue(EHIStringToken.NUMBER_OF_DAYS, String.valueOf(mMaxFreeDaysForCurrentClass));
                    } else {
                        freeDaysString.formatString(R.string.redemption_free_day_subtitle)
                                .addTokenAndValue(EHIStringToken.NUMBER_OF_DAYS, String.valueOf(mMaxFreeDaysForCurrentClass));
                    }
                    enoughtPointsValueView.setText(freeDaysString.format());

                    pointsExpandedView.setVisibility(View.VISIBLE);
                } else {
                    //collapsed view
                    redeemPointsButton.setVisibility(View.VISIBLE);
                }
            }
        } else {
            //actively redeeming
            redemptionEnabledTitle.setVisibility(ReactorViewState.VISIBLE);
            redemptionEnabledTriangle.setVisibility(ReactorViewState.VISIBLE);
            removePointsFromRentalButton.setVisibility(ReactorViewState.VISIBLE);
            pointsPriceDetailsView.setVisibility(ReactorViewState.VISIBLE);

            priceDetailsDaysValue.setText(mDaysUsed + " " + getResources().getString(R.string.reservation_rate_daily_unit_plural));
            priceDetailsPointsValue.setText(NumberFormat.getNumberInstance().format(mPointsUsed) + " " + getResources().getString(R.string.redemption_points_unit));
        }
    }

    private void resetViewsVisibility() {
        redemptionEnabledTitle.setVisibility(ReactorViewState.GONE);
        redemptionEnabledTriangle.setVisibility(ReactorViewState.GONE);

        removePointsFromRentalButton.setVisibility(ReactorViewState.GONE);
        redeemPointsButton.setVisibility(ReactorViewState.GONE);
        notEnoughtPointsView.setVisibility(ReactorViewState.GONE);
        enoughtPointsTitleView.setVisibility(ReactorViewState.GONE);
        enoughtPointsValueView.setVisibility(ReactorViewState.GONE);

        pointsExpandedView.setVisibility(ReactorViewState.GONE);
        pointsPriceDetailsView.setVisibility(ReactorViewState.GONE);
    }

    public void setShowPoints(boolean showPoints) {
        mShowPointGlobal = showPoints;
        updateViewState();
    }

    public void setCarClassDetails(EHICarClassDetails details) {
        mMaxFreeDaysForCurrentClass = details.getMaxRedemptionDays();
        mRedemptionAvailableForCarClass = details.isRedemptionAvailable();
        updateViewState();
    }

    public void setRedeemInformation(int pointsUsed, int daysUsed) {
        mPointsUsed = pointsUsed;
        mDaysUsed = daysUsed;
        mRedeemingPoints = mDaysUsed > 0;
        updateViewState();
    }
}
