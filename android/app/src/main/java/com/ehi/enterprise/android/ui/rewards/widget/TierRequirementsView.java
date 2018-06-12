package com.ehi.enterprise.android.ui.rewards.widget;


import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.databinding.TierRequirementsViewBinding;
import com.ehi.enterprise.android.ui.view.DataBindingViewModelView;
import com.ehi.enterprise.android.ui.viewmodel.ManagersAccessViewModel;
import com.isobar.android.viewmodel.ViewModel;

@ViewModel(ManagersAccessViewModel.class)
public class TierRequirementsView extends DataBindingViewModelView<ManagersAccessViewModel, TierRequirementsViewBinding> {

    public TierRequirementsView(Context context) {
        this(context, null, 0);
    }

    public TierRequirementsView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TierRequirementsView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        if (isInEditMode()) {
            addView(inflate(context, R.layout.v_tier_requirements, null));
            return;
        }

        createViewBinding(R.layout.v_tier_requirements);
    }

    public void setNeededRentals(String rentals) {
        getViewBinding().rentals.setText(rentals);
    }

    public void setNeededDays(String days) {
        getViewBinding().days.setText(days);
    }

    public void setUpgradePerYear(String upgrades) {
        getViewBinding().upgrades.setText(upgrades);
    }

    public void setBonusPoints(String bonusPoints) {
        getViewBinding().bonusPoints.setText(bonusPoints);
    }

    public void showPlusBenefits() {
        getViewBinding().plusBenefitsContainer.setVisibility(View.VISIBLE);
        getViewBinding().upgradesContainer.setVisibility(View.GONE);
        getViewBinding().bonusPointsContainer.setVisibility(View.GONE);
    }

    public void hideNeededDays() {
        getViewBinding().daysContainer.setVisibility(
                getViewBinding().bonusPointsContainer.getVisibility() == View.GONE ?
                        View.GONE :
                        View.INVISIBLE);
    }

}