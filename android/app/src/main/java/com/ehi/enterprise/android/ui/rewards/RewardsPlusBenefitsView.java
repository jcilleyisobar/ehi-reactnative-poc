package com.ehi.enterprise.android.ui.rewards;

import android.content.Context;
import android.util.AttributeSet;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.databinding.RewardsPlusBenefitsViewBinding;
import com.ehi.enterprise.android.ui.view.DataBindingViewModelView;
import com.ehi.enterprise.android.ui.viewmodel.ManagersAccessViewModel;
import com.isobar.android.viewmodel.ViewModel;

@ViewModel(ManagersAccessViewModel.class)
public class RewardsPlusBenefitsView extends DataBindingViewModelView<ManagersAccessViewModel, RewardsPlusBenefitsViewBinding> {
    public RewardsPlusBenefitsView(Context context) {
        this(context, null, 0);
    }

    public RewardsPlusBenefitsView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RewardsPlusBenefitsView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        createViewBinding(R.layout.v_rewards_plus_benefits);
    }
}
