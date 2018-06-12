package com.ehi.enterprise.android.ui.reservation;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.databinding.ClassDetailsPaymentItemViewBinding;
import com.ehi.enterprise.android.databinding.PriceDetailsFragmentBinding;
import com.ehi.enterprise.android.models.reservation.EHIPaymentLineItem;
import com.ehi.enterprise.android.network.responses.ResponseWrapper;
import com.ehi.enterprise.android.ui.fragment.DataBindingViewModelFragment;
import com.ehi.enterprise.android.utils.DLog;
import com.ehi.enterprise.android.utils.DialogUtils;
import com.ehi.enterprise.android.utils.FragmentUtils;
import com.ehi.enterprise.android.utils.analytics.EHIAnalytics;
import com.ehi.enterprise.android.utils.analytics.EHIAnalyticsDictionaryUtils;
import com.ehi.enterprise.android.utils.analytics.EHIAnalyticsEvent;
import com.ehi.enterprise.android.utils.exceptions.NoArgumentsFoundException;
import com.isobar.android.newinstancer.Extra;
import com.isobar.android.viewmodel.ViewModel;

import java.util.List;

import io.dwak.reactor.ReactorComputation;
import io.dwak.reactor.interfaces.ReactorComputationFunction;

@ViewModel(PriceDetailViewModel.class)
public class PriceDetailFragment
        extends DataBindingViewModelFragment<PriceDetailViewModel, PriceDetailsFragmentBinding> {

    public static final String TAG = "PriceDetailFragment";

    public static final String SCREEN_NAME = "PriceDetailFragment";

    @Extra(value = List.class, type = EHIPaymentLineItem.class, required = false)
    public static final String PAYMENT_LINE_ITEMS = "PAYMENT_LINE_ITEMS";

    private int mLineItemIndex = 0;
    private boolean mLineItemsPopulated = false;

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v == getViewBinding().gotItButton) {
                EHIAnalyticsEvent.create()
                        .screen(EHIAnalytics.Screen.SCREEN_CLASS.value, PriceDetailFragment.SCREEN_NAME)
                        .state(EHIAnalytics.State.STATE_FEES.value)
                        .action(EHIAnalytics.Motion.MOTION_TAP.value, EHIAnalytics.Action.ACTION_DONE.value)
                        .addDictionary(EHIAnalyticsDictionaryUtils.reservation())
                        .tagScreen()
                        .tagEvent();
                getActivity().finish();
            } else if (v == getViewBinding().learnMoreButton) {
                getViewBinding().taxesFeesMoreInfo.setVisibility(getViewBinding().taxesFeesMoreInfo.getVisibility() == View.GONE ? View.VISIBLE
                        : View.GONE);
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PriceDetailFragmentHelper.Extractor extractor = new PriceDetailFragmentHelper.Extractor(this);
        if (extractor.paymentLineItems() == null) {
            DLog.e(TAG, new NoArgumentsFoundException());
            getActivity().finish();
            return;
        }

        getViewModel().setPaymentLineItems(extractor.paymentLineItems());

        getViewModel().requestLearnMore();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = createViewBinding(inflater, R.layout.fr_price_detail, container);
        initViews();
        return rootView;
    }

    private void initViews() {
        getViewBinding().gotItButton.setOnClickListener(mOnClickListener);
        getViewBinding().learnMoreButton.setOnClickListener(mOnClickListener);
    }

    @Override
    public void onResume() {
        super.onResume();
        EHIAnalyticsEvent.create()
                .screen(EHIAnalytics.Screen.SCREEN_CLASS.value, PriceDetailFragment.SCREEN_NAME)
                .state(EHIAnalytics.State.STATE_FEES.value)
                .addDictionary(EHIAnalyticsDictionaryUtils.reservation())
                .tagScreen()
                .tagEvent();
    }

    @Override
    protected void initDependencies() {
        super.initDependencies();
        addReaction("PRICE_DETAILS_REACTION", new ReactorComputationFunction() {
            @Override
            public void react(ReactorComputation reactorComputation) {
                if (getViewModel().getPaymentLineItems() != null) {
                    getActivity().setTitle(getResources().getString(R.string.class_details_taxes_fees_summary_title));
                    populateLineItems(getViewModel().getPaymentLineItems());
                }
            }
        });

        addReaction("MORE_INFORMATION_REACTION", new ReactorComputationFunction() {
            @Override
            public void react(ReactorComputation reactorComputation) {
                if (getViewModel().getTaxesAndFeesMoreInformtion() != null) {
                    getViewBinding().taxesFeesMoreInfo.setText(getViewModel().getTaxesAndFeesMoreInformtion());
                }
            }
        });
        addReaction("ERROR_REACTION", new ReactorComputationFunction() {
            @Override
            public void react(ReactorComputation reactorComputation) {
                ResponseWrapper wrapper = getViewModel().getErrorResponse();
                if (wrapper != null) {
                    FragmentUtils.removeProgressFragment(getActivity());
                    DialogUtils.showErrorDialog(getActivity(), wrapper);
                    getViewModel().setErrorResponse(null);
                }
            }
        });
    }

    private void populateLineItems(List<EHIPaymentLineItem> paymentLineItems) {
        if (mLineItemsPopulated) {
            return;
        }
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        for (EHIPaymentLineItem paymentLineItem : paymentLineItems) {
            if (EHIPaymentLineItem.FEE.equals(paymentLineItem.getCategory())) {
                ClassDetailsPaymentItemViewBinding viewBinding = DataBindingUtil.inflate(inflater,
                        R.layout.item_class_detail_payment_item,
                        getViewBinding().container,
                        false);
                if (EHIPaymentLineItem.STATUS_INCLUDED.equalsIgnoreCase(paymentLineItem.getStatus())) {
                    viewBinding.totalPrice.setText(R.string.payment_line_item_included);
                } else {
                    viewBinding.totalPrice.setText(paymentLineItem.getTotalAmountView().getFormattedPrice(false));
                }
                viewBinding.lineItemText.setText(paymentLineItem.getDescription(getResources()));
                getViewBinding().container.addView(viewBinding.getRoot(), mLineItemIndex);
                mLineItemIndex++;
            }
        }

        mLineItemsPopulated = true;
    }
}
