package com.ehi.enterprise.android.ui.reservation;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.databinding.RentalTermsConditionsFragmentBinding;
import com.ehi.enterprise.android.ui.fragment.DataBindingViewModelFragment;
import com.ehi.enterprise.android.utils.DialogUtils;
import com.ehi.enterprise.android.utils.FragmentUtils;
import com.ehi.enterprise.android.utils.analytics.EHIAnalytics;
import com.ehi.enterprise.android.utils.analytics.EHIAnalyticsDictionaryUtils;
import com.ehi.enterprise.android.utils.analytics.EHIAnalyticsEvent;
import com.isobar.android.newinstancer.Extra;
import com.isobar.android.viewmodel.ViewModel;

import io.dwak.reactor.ReactorComputation;
import io.dwak.reactor.interfaces.ReactorComputationFunction;

@ViewModel(RentalTermsConditionsViewModel.class)
public class RentalTermsConditionsFragment extends DataBindingViewModelFragment<RentalTermsConditionsViewModel, RentalTermsConditionsFragmentBinding> {

    public static final String SCREEN_NAME = "RentalTermsConditionsFragment";

    @Extra(value = String.class, required = false)
    public static final String EXTRA_SESSION_ID = "extra.SESSION_ID";

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view == getViewBinding().country) {
                populateCountrySelector();
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return createViewBinding(inflater, R.layout.fr_terms_conditions, container);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
        getActivity().setTitle(R.string.eu_terms_screen_title);
        RentalTermsConditionsFragmentHelper.Extractor extractor = new RentalTermsConditionsFragmentHelper.Extractor(this);
        getViewModel().setInjectedResSessionId(extractor.extraSessionId());
    }

    @Override
    public void onResume() {
        super.onResume();
        EHIAnalyticsEvent.create()
                .screen(EHIAnalytics.Screen.TERMS_AND_CONDITIONS.value, SCREEN_NAME)
                .addDictionary(EHIAnalyticsDictionaryUtils.termsAndConditions(getViewModel().getCountryCode()))
                .state(EHIAnalytics.State.STATE_MODAL.value)
                .tagScreen()
                .tagEvent();
    }

    private void initView() {
        getViewBinding().country.setOnClickListener(mOnClickListener);
    }

    @Override
    protected void initDependencies() {
        super.initDependencies();
        bind(DialogUtils.errorDialog(getViewModel().errorResponse, getActivity()));
        bind(FragmentUtils.progress(getViewModel().progress, getActivity()));

        addReaction("CHOOSE_COUNTRY_REACTION", new ReactorComputationFunction() {
            @Override
            public void react(ReactorComputation reactorComputation) {
                String selectedLocale = getViewModel().getSelectedLocaleLabel();
                if (selectedLocale == null) {
                    return;
                }
                getViewBinding().country.setText(selectedLocale);
                getViewBinding().termsConditionsWebview.loadData(getViewModel().getTermsAndConditionsByLocale(selectedLocale), "text/html; charset=utf-8", "UTF-8");
            }
        });
    }

    private void populateCountrySelector() {
        if (getViewModel().getTermsAndConditionsResponse() == null) {
            return;
        }
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.select_dialog_item);
        arrayAdapter.addAll(getViewModel().getAvailableLocales());
        alertDialog.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                EHIAnalyticsEvent.create()
                        .screen(EHIAnalytics.Screen.TERMS_AND_CONDITIONS.value, SCREEN_NAME)
                        .addDictionary(EHIAnalyticsDictionaryUtils.termsAndConditions(getViewModel().getCountryCode()))
                        .state(EHIAnalytics.State.STATE_MODAL.value)
                        .action(EHIAnalytics.Motion.MOTION_TAP.value, EHIAnalytics.Action.ACTION_MODIFY_LANGUAGE.value)
                        .tagScreen()
                        .tagEvent();

                getViewModel().setSelectedLocalLabel(getViewModel().getAvailableLocales().get(which));
            }
        });
        alertDialog.show();
    }

}