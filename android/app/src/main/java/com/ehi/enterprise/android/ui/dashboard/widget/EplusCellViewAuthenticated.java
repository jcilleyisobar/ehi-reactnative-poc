package com.ehi.enterprise.android.ui.dashboard.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.databinding.EplusAuthCellViewBinding;
import com.ehi.enterprise.android.models.profile.EHILoyaltyData;
import com.ehi.enterprise.android.models.profile.ProfileCollection;
import com.ehi.enterprise.android.ui.view.DataBindingViewModelView;
import com.ehi.enterprise.android.ui.viewmodel.ManagersAccessViewModel;
import com.isobar.android.viewmodel.ViewModel;

@ViewModel(ManagersAccessViewModel.class)
public class EplusCellViewAuthenticated extends DataBindingViewModelView<ManagersAccessViewModel, EplusAuthCellViewBinding> {

    private onEplusCellListener eplusCellListener;

    private View.OnClickListener onClickListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view == getViewBinding().profileView) {
                eplusCellListener.onProfileViewRedirect();
            } else if (view ==  getViewBinding().rewardsView) {
                eplusCellListener.onRewardsViewRedirect();
            }
        }
    };

    public EplusCellViewAuthenticated(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EplusCellViewAuthenticated(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (!isInEditMode()) {
            createViewBinding(R.layout.v_eplus_auth_cell);
        }
    }

    public void setupView(ProfileCollection profileCollection) {
        if (profileCollection.getBasicProfile() != null) {
            getViewBinding().userName.setText(profileCollection.getBasicProfile().getFirstName());
            final EHILoyaltyData ehiLoyaltyData = profileCollection.getBasicProfile().getLoyaltyData();
            getViewBinding().pointsView.setText(ehiLoyaltyData != null ? ehiLoyaltyData.getFormattedPointsToDate() : "-");
        }
        getViewBinding().profileView.setOnClickListener(onClickListener);
        getViewBinding().rewardsView.setOnClickListener(onClickListener);
    }

    public void setEplusCellListener(onEplusCellListener listener) {
        eplusCellListener = listener;
    }

    public interface onEplusCellListener {
        void onProfileViewRedirect();
        void onRewardsViewRedirect();
    }
}