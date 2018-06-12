package com.ehi.enterprise.android.ui.enroll;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.app.EHIStringToken;
import com.ehi.enterprise.android.databinding.EnrollAddressFragmentBinding;
import com.ehi.enterprise.android.ui.fragment.DataBindingViewModelFragment;
import com.ehi.enterprise.android.utils.EHITextUtils;
import com.ehi.enterprise.android.utils.analytics.EHIAnalytics;
import com.ehi.enterprise.android.utils.analytics.EHIAnalyticsEvent;
import com.isobar.android.newinstancer.Extra;
import com.isobar.android.tokenizedstring.TokenizedString;
import com.isobar.android.viewmodel.ViewModel;

@ViewModel(EnrollAddressFragmentViewModel.class)
public class EnrollAddressFragment extends DataBindingViewModelFragment<EnrollAddressFragmentViewModel, EnrollAddressFragmentBinding> {

    @Extra(Boolean.class)
    public static final String EMERALD_CLUB = "EMERALD_CLUB";

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View view) {
            if (view == getViewBinding().changeButton) {
                track(EHIAnalytics.Action.ACTION_CHANGE_ADDRESS);
                ((EnrollFlowListener) getActivity()).goToStepTwoWithDriverFound();
            } else if (view == getViewBinding().keepButton) {
                track(EHIAnalytics.Action.ACTION_KEEP_ADDRESS);
                ((EnrollFlowListener) getActivity()).goToStepThree();
            }
        }
    };

    private void track(EHIAnalytics.Action action) {
        EHIAnalyticsEvent.create()
                .screen(EHIAnalytics.Screen.SCREEN_ENROLL_STEP_2.value, TAG)
                .state(getViewModel().isEmeraldClub() ? EHIAnalytics.State.STATE_EMERALD_CLUB.value : EHIAnalytics.State.STATE_NON_LOYALTY.value)
                .action(EHIAnalytics.Motion.MOTION_TAP.value, action.value)
                .tagScreen()
                .tagEvent();
    }

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        EnrollAddressFragmentHelper.Extractor extractor = new EnrollAddressFragmentHelper.Extractor(this);
        getViewModel().setEmeraldClub(extractor.emeraldClub());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        createViewBinding(inflater, R.layout.fr_enroll_address, container);
        initViews();
        return getViewBinding().getRoot();
    }

    private void initViews() {
        getViewBinding().changeButton.setOnClickListener(mOnClickListener);
        getViewBinding().keepButton.setOnClickListener(mOnClickListener);

        getViewBinding().stepTextView.setText(new TokenizedString.Formatter<EHIStringToken>(getResources())
                .addTokenAndValue(EHIStringToken.STEP, "2")
                .addTokenAndValue(EHIStringToken.STEP_COUNT, EnrollActivity.TOTAL_STEPS)
                .formatString(R.string.enroll_step)
                .format());
        final String address = getViewModel().getStreetAddress();
        if (EHITextUtils.isEmpty(address)) {
            ((EnrollFlowListener) getActivity()).goToStepTwoWithDriverFound();
        } else {
            getViewBinding().addressTextView.setText(address);
        }
    }
}
