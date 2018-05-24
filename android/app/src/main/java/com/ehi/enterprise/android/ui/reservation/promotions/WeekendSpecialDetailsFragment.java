package com.ehi.enterprise.android.ui.reservation.promotions;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.databinding.WeekendSpecialDetailsFragmentBinding;
import com.ehi.enterprise.android.models.reservation.EHIContract;
import com.ehi.enterprise.android.ui.fragment.DataBindingViewModelFragment;
import com.ehi.enterprise.android.ui.location.SearchLocationsActivity;
import com.ehi.enterprise.android.ui.location.SearchLocationsActivityHelper;
import com.ehi.enterprise.android.ui.reservation.HtmlParseFragmentHelper;
import com.ehi.enterprise.android.ui.reservation.promotions.dialogs.DealsContractCombinationDialogHelper;
import com.ehi.enterprise.android.utils.analytics.EHIAnalytics;
import com.ehi.enterprise.android.utils.analytics.EHIAnalyticsDictionaryUtils;
import com.ehi.enterprise.android.utils.analytics.EHIAnalyticsEvent;
import com.isobar.android.newinstancer.NoExtras;
import com.isobar.android.viewmodel.ViewModel;

import java.util.List;

@NoExtras
@ViewModel(WeekendSpecialDetailsViewModel.class)
public class WeekendSpecialDetailsFragment extends DataBindingViewModelFragment<WeekendSpecialDetailsViewModel, WeekendSpecialDetailsFragmentBinding> {

    private static final String TAG = "WeekendSpecialDetailsFragment";

    private static final int CODE_DEAL_CONTRACT_COMBINATION_DIALOG = 11;

    private EHIContract contract;

    private View.OnClickListener startReservationListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            EHIAnalyticsEvent.create()
                    .screen(EHIAnalytics.Screen.SCREEN_PROMOTION_DETAILS.value, TAG)
                    .state(EHIAnalytics.State.STATE_WEEKEND_SPECIAL.value)
                    .action(EHIAnalytics.Motion.MOTION_TAP.value, EHIAnalytics.Action.ACTION_START_RESERVATION.value)
                    .addDictionary(EHIAnalyticsDictionaryUtils.signIn())
                    .tagScreen()
                    .tagEvent();
            if (getViewModel().needShowContractDialog()){
                showModalDialogForResult(getActivity(),
                        new DealsContractCombinationDialogHelper.Builder().build(),
                        CODE_DEAL_CONTRACT_COMBINATION_DIALOG);
            } else {
                startReservationWithWES();
            }
        }
    };

    private View.OnClickListener termsAndConditionsListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            EHIAnalyticsEvent.create()
                    .screen(EHIAnalytics.Screen.SCREEN_PROMOTION_DETAILS.value, TAG)
                    .state(EHIAnalytics.State.STATE_WEEKEND_SPECIAL.value)
                    .action(EHIAnalytics.Motion.MOTION_TAP.value, EHIAnalytics.Action.ACTION_TERMS.value)
                    .addDictionary(EHIAnalyticsDictionaryUtils.signIn())
                    .tagScreen()
                    .tagEvent();
            showModal(
                    getActivity(),
                    new HtmlParseFragmentHelper.Builder()
                            .title(getResources().getString(R.string.weekend_special_terms_and_conditions_navigation_ti))
                            .message(contract.getTermsAndConditions())
                            .build()
            );
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = createViewBinding(inflater, R.layout.fr_weekend_special_details, container);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        contract = getViewModel().getWeekendSpecialContract();
        if (contract == null) {
            getActivity().finish();
            return;
        }
        getActivity().setTitle(R.string.weekend_special_navigation_title);
        initPromotionDetailsAdapter();
    }

    @Override
    public void onResume() {
        super.onResume();
        EHIAnalyticsEvent.create()
                .screen(EHIAnalytics.Screen.SCREEN_PROMOTION_DETAILS.value, TAG)
                .state(EHIAnalytics.State.STATE_WEEKEND_SPECIAL.value)
                .addDictionary(EHIAnalyticsDictionaryUtils.signIn())
                .tagScreen()
                .tagEvent();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == CODE_DEAL_CONTRACT_COMBINATION_DIALOG){
                startReservationWithWES();
            }
        }
    }

    private void initPromotionDetailsAdapter() {
        getViewBinding().weekendSpecialDetailsRecyclerView.setLayoutManager(
                new LinearLayoutManager(getActivity())
        );

        WeekendSpecialDetailsAdapter adapter = new WeekendSpecialDetailsAdapter();

        // add view types
        adapter.addViewType(new WeekendSpecialDetailsViewTypeImage());
        adapter.addViewType(new WeekendSpecialDetailsViewTypeTitle());
        adapter.addViewType(new WeekendSpecialDetailsViewTypeBullet());
        adapter.addViewType(new WeekendSpecialDetailsViewTypeButton());
        adapter.addViewType(new WeekendSpecialDetailsViewTypeLink());
        adapter.addViewType(new WeekendSpecialDetailsViewTypeDivider());

        // add data
        adapter.addItem(
                new WeekendSpecialDetailsViewTypeImage.WeekendSpecialDetailsDatumImage(R.drawable.weekend_special)
        );
        adapter.addItem(
                new WeekendSpecialDetailsViewTypeTitle.WeekendSpecialDetailsDatumTitle(
                        contract.getContractName()
                )
        );

        final List<String> mobDescriptions = contract.getDescription();
        if (mobDescriptions != null) {
            for (int i = 0, size = mobDescriptions.size(); i < size; i++) {
                adapter.addItem(
                        new WeekendSpecialDetailsViewTypeBullet.WeekendSpecialDetailsDatumBullet(
                                mobDescriptions.get(i)
                        )
                );
            }
        }

        adapter.addItem(
                new WeekendSpecialDetailsViewTypeButton.WeekendSpecialDetailsDatumButton(
                        getString(R.string.weekend_special_start_reservation_button_title),
                        startReservationListener
                )
        );
        adapter.addItem(
                new WeekendSpecialDetailsViewTypeDivider.WeekendSpecialDetailsDatumDivider()
        );
        adapter.addItem(
                new WeekendSpecialDetailsViewTypeLink.WeekendSpecialDetailsDatumLink(
                        getString(R.string.weekend_special_terms_and_conditions_button_title),
                        termsAndConditionsListener
                )
        );

        getViewBinding().weekendSpecialDetailsRecyclerView.setAdapter(adapter);
    }

    private void startReservationWithWES(){
        getViewModel().enableWeekendSpecial();
        startActivity(new SearchLocationsActivityHelper.Builder()
                .extraFlow(SearchLocationsActivity.FLOW_PICKUP_LOCATION_ROUND_TRIP)
                .extraShowStartReservation(true)
                .isModify(false)
                .build(getActivity()));
    }
}
