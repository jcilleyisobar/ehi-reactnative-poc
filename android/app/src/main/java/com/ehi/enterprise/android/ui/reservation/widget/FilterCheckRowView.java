package com.ehi.enterprise.android.ui.reservation.widget;

import android.content.Context;
import android.view.View;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.databinding.FilterCheckRowBinding;
import com.ehi.enterprise.android.models.reservation.EHIFilterValue;
import com.ehi.enterprise.android.ui.reservation.CarFilterFragment;
import com.ehi.enterprise.android.ui.view.DataBindingViewModelView;
import com.ehi.enterprise.android.ui.viewmodel.ManagersAccessViewModel;
import com.isobar.android.viewmodel.ViewModel;

@ViewModel(ManagersAccessViewModel.class)
public class FilterCheckRowView extends DataBindingViewModelView<ManagersAccessViewModel, FilterCheckRowBinding> {

    private final EHIFilterValue mFilterValue;
    private final CarFilterFragment.IFilterChanged mCallback;

    private OnClickListener mOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            mFilterValue.setActive(getViewBinding().checkRow.isChecked());
            mCallback.filterChanged();
        }
    };


    public FilterCheckRowView(Context context, EHIFilterValue filterValue, CarFilterFragment.IFilterChanged callback) {
        super(context);
        mFilterValue = filterValue;
        mCallback = callback;

        createViewBinding(R.layout.v_filter_check_row);
        initView();
    }

    private void initView() {
        getViewBinding().checkBoxText.setText(mFilterValue.getDescription());
        getViewBinding().checkRow.setOnClickListener(mOnClickListener);
        getViewBinding().checkRow.setChecked(mFilterValue.isActive());
    }

    public void setChecked(boolean checked) {
        getViewBinding().checkRow.setChecked(checked);
        mFilterValue.setActive(false);
    }
}
