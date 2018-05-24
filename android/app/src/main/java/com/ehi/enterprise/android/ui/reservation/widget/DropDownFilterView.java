package com.ehi.enterprise.android.ui.reservation.widget;

import android.content.Context;
import android.content.DialogInterface;
import android.util.AttributeSet;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.databinding.DropDownFilterBinding;
import com.ehi.enterprise.android.models.reservation.EHIAvailableCarFilters;
import com.ehi.enterprise.android.models.reservation.EHIFilterValue;
import com.ehi.enterprise.android.ui.reservation.CarFilterFragment;
import com.ehi.enterprise.android.ui.view.DataBindingViewModelView;
import com.ehi.enterprise.android.ui.viewmodel.ManagersAccessViewModel;
import com.isobar.android.viewmodel.ViewModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@ViewModel(ManagersAccessViewModel.class)
public class DropDownFilterView extends DataBindingViewModelView<ManagersAccessViewModel, DropDownFilterBinding> {

    private final EHIAvailableCarFilters mFilter;
    private final List<EHIFilterValue> mFilterValues;
    private final List<CharSequence> mFilterTitles;
    private final CarFilterFragment.IFilterChanged mCallback;
    private int mSelected;

    public DropDownFilterView(Context context, EHIAvailableCarFilters filter, String defaultChoice, CarFilterFragment.IFilterChanged callback) {
        super(context);
        mFilter = filter;
        mFilterValues = mFilter.getFilterValues();
        mFilterTitles = new ArrayList<>(mFilterValues.size());
        mCallback = callback;

        if (filter.sortable()) {
            Collections.sort(mFilterValues, new Comparator<EHIFilterValue>() {
                @Override
                public int compare(EHIFilterValue lhs, EHIFilterValue rhs) {
                    if (lhs.isCodeNull()) {
                        return 1;
                    }
                    if (rhs.isCodeNull()) {
                        return -1;
                    }

                    return (Integer.parseInt(lhs.getCode()) - Integer.parseInt(rhs.getCode()) < 0) ? -1 : 1;
                }
            });
        }

        mSelected = 0;
        mFilterTitles.add(defaultChoice);
        int indexOfNull = -1;
        for (int i = 0; i < mFilterValues.size(); i++) {
            if (mFilterValues.get(i).isCodeNull()) {
                indexOfNull = indexOfNull == -1 ? i : indexOfNull;
                continue;
            }

            mSelected = mFilterValues.get(i).isActive() ? i + 1 : mSelected;
            mFilterTitles.add(mFilterValues.get(i).getDescription());
        }


        createViewBinding(R.layout.v_drop_down_filter);
        initView();

    }

    public DropDownFilterView(Context context, AttributeSet attrs) {
        super(context, attrs);
        throw new IllegalStateException("This has no logic for being put into XML. Must be programmatically injected");
    }

    public DropDownFilterView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        throw new IllegalStateException("This has no logic for being put into XML. Must be programmatically injected");
    }

    private void initView() {
        getViewBinding().title.setText(mFilter.getFilterDescription());
        getViewBinding().spinner.populateView(mFilterTitles, mSelected, "");
        getViewBinding().spinner.setCallback(new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    mFilter.clearFilters();
                    mCallback.filterChanged();
                } else {
                    mFilter.clearFilters();
                    mFilter.addFilter(mFilter.getFilterValues().get(which - 1));
                    mCallback.filterChanged();
                }
            }
        });
    }

    public void reset() {
        getViewBinding().spinner.setSelectedIndex(0);
    }

}
