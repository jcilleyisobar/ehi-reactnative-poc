package com.ehi.enterprise.android.ui.reservation.key_facts;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.databinding.KeyFactsFragmentBinding;
import com.ehi.enterprise.android.models.location.EHILocation;
import com.ehi.enterprise.android.models.reservation.EHIExtraItem;
import com.ehi.enterprise.android.models.reservation.EHIExtras;
import com.ehi.enterprise.android.models.reservation.EHIKeyFactsPolicy;
import com.ehi.enterprise.android.ui.activity.EHIBaseActivity;
import com.ehi.enterprise.android.ui.fragment.DataBindingViewModelFragment;
import com.ehi.enterprise.android.ui.reservation.KeyFactsActionDelegate;
import com.ehi.enterprise.android.ui.reservation.key_facts.widget.AdditionalRentalPoliciesExpandableCellView;
import com.ehi.enterprise.android.ui.reservation.key_facts.widget.DisputeCellView;
import com.ehi.enterprise.android.ui.reservation.key_facts.widget.EquipmentProductsExpandableCellView;
import com.ehi.enterprise.android.ui.reservation.key_facts.widget.KeyFactsPolicyCell;
import com.ehi.enterprise.android.ui.reservation.key_facts.widget.KeyFactsPolicyWithExclusionCell;
import com.ehi.enterprise.android.utils.FragmentUtils;
import com.ehi.enterprise.android.utils.IntentUtils;
import com.isobar.android.newinstancer.Extra;
import com.isobar.android.viewmodel.ViewModel;

import java.util.List;

import io.dwak.reactor.ReactorComputation;
import io.dwak.reactor.interfaces.ReactorComputationFunction;
import io.dwak.reactorbinding.activity.ReactorActivity;
import io.dwak.reactorbinding.view.ReactorView;
import io.dwak.reactorbinding.widget.ReactorTextView;

@ViewModel(KeyFactsViewModel.class)
public class KeyFactsFragment extends DataBindingViewModelFragment<KeyFactsViewModel, KeyFactsFragmentBinding> {

    public static final String TAG = "KeyFactsFragment";

    @Extra(value = List.class, type = EHIKeyFactsPolicy.class)
    public static final String KEY_FACTS_LIST = "KEY_FACTS_LIST";
    @Extra(EHILocation.class)
    public static final String PICKUP_LOCATION = "PICKUP LOCATION";
    @Extra(EHIExtras.class)
    public static final String CAR_CLASS_DETAILS_EXTRAS = "CCDEXTRAS";
    @Extra(EHIExtras.class)
    public static final String VEHICLE_RATE_EXTRAS = "VREXTRAS";

    public static final String MINIMUM_REQUIREMENTS_REACTION = "MINIMUM REQUIREMENTS REACTION";
    public static final String PROTECTION_PRODUCTS_REACTION = "PROTECTION PRODUCTS REACTION";
    public static final String DISPUTES_REACTION = "DISPUTES REACTION";
    public static final String ADDITIONAL_POLICIES_REACTION = "ADDITIONAL POLICIES REACTION";
    public static final String EQUIPMENT_PRODUCTS_REACTION = "EQUIPMENT PRODUCTS REACTION";

    private KeyFactsActionDelegate delegate;

    //region clickListeners
    private KeyFactsPolicyWithExclusionCell.KeyFactsPolicyCellClickListener mCellClickListener
            = new KeyFactsPolicyWithExclusionCell.KeyFactsPolicyCellClickListener() {
        @Override
        public void onPolicyClicked(final EHIKeyFactsPolicy policy) {
            delegate.onKeyFactsPolicyClicked(policy);
        }

        @Override
        public void onExclusionsClicked(final List<EHIKeyFactsPolicy> exclusions) {
            delegate.onKeyFactsExclusionsClicked(exclusions);
        }
    };

    private KeyFactsPolicyCell.KeyFactsPolicyCellClickListener mPolicyCellClickListener
            = new KeyFactsPolicyCell.KeyFactsPolicyCellClickListener() {
        @Override
        public void onPolicyClicked(final EHIKeyFactsPolicy policy) {
            delegate.onKeyFactsPolicyClicked(policy);
        }
    };

    private DisputeCellView.DisputeCellListener mDisputeCellListener = new DisputeCellView.DisputeCellListener() {
        @Override
        public void onEmailClicked(final String email) {
            IntentUtils.sendMessageToEmail(getActivity(), email);
        }

        @Override
        public void onTelephoneClicked(final String telephone) {
            IntentUtils.callNumber(getActivity(), telephone);
        }

        @Override
        public void onRentalPolicyClicked(EHIKeyFactsPolicy policy) {
             delegate.onKeyFactsPolicyClicked(policy);
        }
    };

    private AdditionalRentalPoliciesExpandableCellView.AdditionalRentalPoliciesListener mPoliciesListener
            = new AdditionalRentalPoliciesExpandableCellView.AdditionalRentalPoliciesListener() {
        @Override
        public void onRentalPolicyClicked(final EHIKeyFactsPolicy policy) {
             delegate.onKeyFactsPolicyClicked(policy);
        }
    };

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(final View v) {
            if (v == getViewBinding().rulesOfTheRoad) {
                IntentUtils.openUrlViaCustomTab(getActivity(), getViewModel().getRulesOfTheRoadUrl());
            }
        }
    };

    private EquipmentProductsExpandableCellView.EquipmentProductListener mEquipmentProductListener
            = new EquipmentProductsExpandableCellView.EquipmentProductListener() {
        @Override
        public void onExtraClicked(final EHIExtraItem ehiExtraItem) {
             delegate.onExtraItemClicked(ehiExtraItem);
        }
    };

    //endregion


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        delegate = new KeyFactsActionDelegate((EHIBaseActivity) getActivity());
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        KeyFactsFragmentHelper.Extractor extractor = new KeyFactsFragmentHelper.Extractor(this);
        getViewModel().setKeyFactsPolicies(extractor.keyFactsList());
        getViewModel().setCarClassDetailsExtras(extractor.carClassDetailsExtras());
        getViewModel().setVehicleRatesExtras(extractor.vehicleRateExtras());
        getViewModel().setPickupLocation(extractor.pickupLocation());
    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        createViewBinding(inflater, R.layout.fr_key_facts, container);
        initViews();
        return getViewBinding().getRoot();
    }

    private void initViews() {
        getViewBinding().rulesOfTheRoad.setOnClickListener(mOnClickListener);
    }


    @Override
    protected void initDependencies() {
        super.initDependencies();
        bind(FragmentUtils.progress(getViewModel().progress, getActivity()));
        bind(ReactorActivity.titleRes(getViewModel().title, getActivity()));
        bind(ReactorView.visibility(getViewModel().disputeCell.visibility(), getViewBinding().disputes));
        bind(ReactorTextView.text(getViewModel().viewRoadRules.text(), getViewBinding().rulesOfTheRoad));

        addReaction(MINIMUM_REQUIREMENTS_REACTION, new ReactorComputationFunction() {
            @Override
            public void react(final ReactorComputation reactorComputation) {
                if (getViewModel().getMinimumRequirementsPolicies() != null) {
                    getViewBinding().minimumRequirements.setPolicyCellClickListener(mPolicyCellClickListener);
                    getViewBinding().minimumRequirements.setPolicies(getViewModel().getMinimumRequirementsPolicies());
                }
            }
        });

        addReaction(PROTECTION_PRODUCTS_REACTION, new ReactorComputationFunction() {
            @Override
            public void react(final ReactorComputation reactorComputation) {
                if (getViewModel().getProtectionProducts() != null && getViewModel().getCarClassDetailsExtras() != null) {
                    getViewBinding().protectionProducts.setCellClickListener(mCellClickListener);
                    getViewBinding().protectionProducts.setPoliciesAndExtras(getViewModel().getProtectionProducts(),
                            getViewModel().getCarClassDetailsExtras());
                }
            }
        });

        addReaction(EQUIPMENT_PRODUCTS_REACTION, new ReactorComputationFunction() {
            @Override
            public void react(final ReactorComputation reactorComputation) {
                if (getViewModel().getCarClassDetailsExtras() != null) {
                    getViewBinding().equipmentProducts.setEquipmentProductListener(mEquipmentProductListener);
                    getViewBinding().equipmentProducts.setExtras(getViewModel().getCarClassDetailsExtras());
                }
            }
        });

        addReaction(ADDITIONAL_POLICIES_REACTION, new ReactorComputationFunction() {
            @Override
            public void react(final ReactorComputation reactorComputation) {
                if (getViewModel().getAdditionalPolicies() != null) {
                    getViewBinding().additionalPolicies.setPoliciesListener(mPoliciesListener);
                    getViewBinding().additionalPolicies.setPolicies(getViewModel().getAdditionalPolicies());
                }
            }
        });

        addReaction(DISPUTES_REACTION, new ReactorComputationFunction() {
            @Override
            public void react(final ReactorComputation reactorComputation) {
                getViewBinding().disputes.setDisputeCellListener(mDisputeCellListener);
                getViewBinding().disputes.setDisputeInfo(getViewModel().getDisputeInfo());
                getViewBinding().disputes.setPolicies(getViewModel().getQuestionsPolicies());
            }
        });
    }
}
