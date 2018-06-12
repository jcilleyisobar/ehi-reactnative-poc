package com.ehi.enterprise.android.ui.widget;

import android.content.Context;
import android.util.AttributeSet;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.databinding.ModifyPrePayBannerViewBinding;
import com.ehi.enterprise.android.ui.view.DataBindingViewModelView;
import com.isobar.android.viewmodel.ViewModel;

import io.dwak.reactor.ReactorComputation;
import io.dwak.reactor.ReactorVar;
import io.dwak.reactor.interfaces.ReactorComputationFunction;
import io.dwak.reactorbinding.view.ReactorView;
import io.dwak.reactorbinding.widget.ReactorTextView;

@ViewModel(ModifyPrePayBannerViewModel.class)
public class ModifyPrePayBannerView extends DataBindingViewModelView<ModifyPrePayBannerViewModel, ModifyPrePayBannerViewBinding> {

    public ModifyPrePayBannerView(Context context) {
        this(context, null);
    }

    public ModifyPrePayBannerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ModifyPrePayBannerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        if (isInEditMode()) {
            addView(inflate(context, R.layout.v_modify_prepay_banner, null));
            return;
        }

        createViewBinding(R.layout.v_modify_prepay_banner);
    }

    @Override
    protected void initDependencies() {
        super.initDependencies();
        bind(ReactorTextView.text(getViewModel().bannerText.text(), getViewBinding().bannerDescription));
        bind(ReactorView.visibility(getViewModel().amountView.visibility(), getViewBinding().prepayOriginalAmount));
    }

    public void setOriginalValue(String originalValue) {
        getViewBinding().prepayOriginalAmount.setText(
                getContext().getString(R.string.modify_reservation_prepay_original_amount) + " " + originalValue
        );
    }

    public void setIsNorthAmericaAirport(boolean isNorthAmericaAirport) {
        getViewModel().setIsNorthAmericaAirport(isNorthAmericaAirport);
    }

    public static ReactorComputationFunction updated(final ReactorVar<Boolean> source, final ModifyPrePayBannerView target) {
        return new ReactorComputationFunction() {
            @Override
            public void react(ReactorComputation reactorComputation) {
                if (source.getRawValue()) {
                    target.getViewModel().reservationUpdated();
                }
            }
        };
    }
}