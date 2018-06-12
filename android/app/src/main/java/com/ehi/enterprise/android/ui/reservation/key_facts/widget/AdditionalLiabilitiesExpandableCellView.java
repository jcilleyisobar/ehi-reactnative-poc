package com.ehi.enterprise.android.ui.reservation.key_facts.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.databinding.AdditionalLiabilitiesViewBinding;
import com.ehi.enterprise.android.ui.view.DataBindingViewModelView;
import com.isobar.android.viewmodel.ViewModel;

import io.dwak.reactorbinding.view.ReactorView;
import io.dwak.reactorbinding.widget.ReactorTextView;

@ViewModel(AdditionalLiabilitiesViewModel.class)
public class AdditionalLiabilitiesExpandableCellView extends DataBindingViewModelView<AdditionalLiabilitiesViewModel, AdditionalLiabilitiesViewBinding>{
    private OnClickListener mClickListener = new OnClickListener() {
        @Override
        public void onClick(final View v) {
            if(v == getViewBinding().cellTitleButton.getRoot()){
                getViewModel().cellTitleClicked();
            }
        }
    };
    //region constructors
    public AdditionalLiabilitiesExpandableCellView(final Context context) {
        this(context, null);
    }

    public AdditionalLiabilitiesExpandableCellView(final Context context, final AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AdditionalLiabilitiesExpandableCellView(final Context context, final AttributeSet attrs, final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if(!isInEditMode()) {
            createViewBinding(R.layout.v_key_facts_additional_liabilities);
            initViews();
        }

    }
    //endregion

    private void initViews() {
        getViewBinding().cellTitleButton.getRoot().setOnClickListener(mClickListener);
    }

    @Override
    protected void initDependencies() {
        super.initDependencies();
        bind(ReactorTextView.textRes(getViewModel().title.textRes(), getViewBinding().cellTitleButton.cellTitle));
        bind(ReactorView.visibility(getViewModel().content.visibility(), getViewBinding().content));
    }
}
