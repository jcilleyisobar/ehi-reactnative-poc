package com.ehi.enterprise.android.ui.rewards.widget;

import android.content.Context;
import android.util.AttributeSet;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.databinding.SimpleTierViewBinding;
import com.ehi.enterprise.android.models.profile.EHILoyaltyData;
import com.ehi.enterprise.android.ui.view.DataBindingViewModelView;
import com.ehi.enterprise.android.ui.widget.GaugeView;
import com.isobar.android.viewmodel.ViewModel;

import io.dwak.reactorbinding.widget.ReactorTextView;

@ViewModel(SimpleTierViewModel.class)
public class SimpleTierView extends DataBindingViewModelView<SimpleTierViewModel, SimpleTierViewBinding> implements IAnimatedTier {

    public SimpleTierView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SimpleTierView(Context context, int loyaltyTotalType, EHILoyaltyData loyaltyData) {
        super(context);
        createViewBinding(R.layout.v_eplus_simple_gauge);
        getViewModel().setLoyaltyTotalType(loyaltyTotalType);
        getViewModel().setupGaugeData(loyaltyData);
        initViews();
    }

    public SimpleTierView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        createViewBinding(R.layout.v_eplus_simple_gauge);
        initViews();
    }

    @Override
    protected void initDependencies() {
        super.initDependencies();
        bind(ReactorTextView.text(getViewModel().gaugeLength.text(), getViewBinding().gaugeLength));
        bind(ReactorTextView.text(getViewModel().gaugeType.text(), getViewBinding().gaugeType));
        bind(ReactorTextView.text(getViewModel().gaugeLengthDescription.textCharSequence(), getViewBinding().gaugeLengthDescription));
    }

    private void initViews() {
        GaugeView mGaugeView = getViewBinding().gauge;
        mGaugeView.setFillColor(getViewModel().getFillColor());
        mGaugeView.setNumberOfSections(getViewModel().getNumberOfSections());
        mGaugeView.setFilledSections(getViewModel().getFilledSectionsNumber());
        mGaugeView.setAreSectionsVisible(getViewModel().areSectionsVisible());
    }


    public void setPercentage(float percentage) {
        getViewBinding().gauge.setInnerArchPercentage(percentage);
    }
}
