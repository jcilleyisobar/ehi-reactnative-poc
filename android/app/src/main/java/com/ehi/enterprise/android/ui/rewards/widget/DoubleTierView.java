package com.ehi.enterprise.android.ui.rewards.widget;

import android.content.Context;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.databinding.DoubleTierViewBinding;
import com.ehi.enterprise.android.models.profile.EHILoyaltyData;
import com.ehi.enterprise.android.ui.view.DataBindingViewModelView;
import com.ehi.enterprise.android.ui.viewmodel.ManagersAccessViewModel;
import com.isobar.android.viewmodel.ViewModel;

@ViewModel(ManagersAccessViewModel.class)
public class DoubleTierView extends DataBindingViewModelView<ManagersAccessViewModel, DoubleTierViewBinding> implements IAnimatedTier {
    SimpleTierView mFirstGauge;
    SimpleTierView mSecondGauge;

    public DoubleTierView(Context context, EHILoyaltyData loyaltyData, int firstGaugeTotalType, int secondGaugeTotalType) {
        super(context);
        createViewBinding(R.layout.v_eplus_double_gauge);
        mFirstGauge = new SimpleTierView(context, firstGaugeTotalType, loyaltyData);
        mSecondGauge = new SimpleTierView(context, secondGaugeTotalType, loyaltyData);
        getViewBinding().gauge1.addView(mFirstGauge);
        getViewBinding().gauge2.addView(mSecondGauge);
    }

    @Override
    public void setPercentage(float percentage) {
        mFirstGauge.setPercentage(percentage);
        mSecondGauge.setPercentage(percentage);
    }

}
