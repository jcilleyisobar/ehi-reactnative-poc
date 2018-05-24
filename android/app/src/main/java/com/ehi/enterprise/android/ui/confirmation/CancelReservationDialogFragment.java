package com.ehi.enterprise.android.ui.confirmation;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.app.EHIStringToken;
import com.ehi.enterprise.android.databinding.CancelReservationDialogFragmentBinding;
import com.ehi.enterprise.android.ui.fragment.DataBindingViewModelFragment;
import com.ehi.enterprise.android.ui.viewmodel.ManagersAccessViewModel;
import com.ehi.enterprise.android.utils.analytics.EHIAnalytics;
import com.ehi.enterprise.android.utils.analytics.EHIAnalyticsDictionaryUtils;
import com.ehi.enterprise.android.utils.analytics.EHIAnalyticsEvent;
import com.isobar.android.newinstancer.Extra;
import com.isobar.android.tokenizedstring.TokenizedString;
import com.isobar.android.viewmodel.ViewModel;

@ViewModel(ManagersAccessViewModel.class)
public class CancelReservationDialogFragment extends DataBindingViewModelFragment<ManagersAccessViewModel, CancelReservationDialogFragmentBinding> {

    public static final String SCREEN_NAME = "CancelReservationDialogFragment";

    @Extra(value = String.class)
    public static final String EXTRA_CONTRACT_NAME = "ehi.EXTRA_CONTRACT_NAME";

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v == getViewBinding().cancelReservationButton) {
                EHIAnalyticsEvent.create()
                        .screen(EHIAnalytics.Screen.SCREEN_RESERVATION.value, SCREEN_NAME)
                        .state(EHIAnalytics.State.STATE_CANCELLATION.value)
                        .action(EHIAnalytics.Motion.MOTION_TAP.value, EHIAnalytics.Action.ACTION_YES.value)
                        .tagScreen()
                        .tagEvent();

                getActivity().setResult(Activity.RESULT_OK);
                getActivity().finish();
            } else if (v == getViewBinding().keepReservationButton) {
                EHIAnalyticsEvent.create()
                        .screen(EHIAnalytics.Screen.SCREEN_RESERVATION.value, SCREEN_NAME)
                        .state(EHIAnalytics.State.STATE_CANCELLATION.value)
                        .action(EHIAnalytics.Motion.MOTION_TAP.value, EHIAnalytics.Action.ACTION_NO.value)
                        .tagScreen()
                        .tagEvent();

                getActivity().finish();
            }
        }
    };

    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        createViewBinding(inflater, R.layout.fr_reservation_cancel_dialog, container);

        initViews();

        return getViewBinding().getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        EHIAnalyticsEvent.create()
                .screen(EHIAnalytics.Screen.SCREEN_RESERVATION.value, SCREEN_NAME)
                .state(EHIAnalytics.State.STATE_CANCELLATION.value)
                .tagScreen()
                .tagEvent();
    }

    private void initViews() {
        getActivity().setTitle(R.string.reservation_confirmation_cancel_reservation_action_title);

        getViewBinding().cancelReservationButton.setOnClickListener(mOnClickListener);
        getViewBinding().keepReservationButton.setOnDisabledClickListener(mOnClickListener);

        final CancelReservationDialogFragmentHelper.Extractor extractor = new CancelReservationDialogFragmentHelper.Extractor(this);
        final String contract = extractor.extraContractName();
        if (!TextUtils.isEmpty(contract)) {
            CharSequence bannerText = new TokenizedString.Formatter<EHIStringToken>(getResources())
                    .formatString(R.string.confirmation_cancel_contract_details)
                    .addTokenAndValue(EHIStringToken.CONTRACT_NAME, contract)
                    .format();
            getViewBinding().emailNotificationBanner.setUp(bannerText);
        }
    }
}
