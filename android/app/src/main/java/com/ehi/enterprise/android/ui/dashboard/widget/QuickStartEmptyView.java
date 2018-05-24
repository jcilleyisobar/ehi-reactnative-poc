package com.ehi.enterprise.android.ui.dashboard.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.databinding.EmptyQuickCellBinding;
import com.ehi.enterprise.android.ui.view.DataBindingViewModelView;
import com.ehi.enterprise.android.ui.viewmodel.ManagersAccessViewModel;
import com.isobar.android.viewmodel.ViewModel;

@ViewModel(ManagersAccessViewModel.class)
public class QuickStartEmptyView extends DataBindingViewModelView<ManagersAccessViewModel, EmptyQuickCellBinding> {

    public QuickStartEmptyView(Context context) {
        this(context, null, 0);
    }

    public QuickStartEmptyView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public QuickStartEmptyView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        createViewBinding(R.layout.v_dashboard_empty_quick_start);
    }

    public void setupView(boolean trackingEnabled) {
        if (trackingEnabled) {
            getViewBinding().headerView.setText(getResources().getString(R.string.dashboard_history_cell_enabled_title));
            getViewBinding().headerView.setVisibility(VISIBLE);
            getViewBinding().text.setText(getResources().getString(R.string.dashboard_history_cell_enabled_details));
            getViewBinding().linkText.setVisibility(GONE);
            getViewBinding().linkText.setOnClickListener(null);
            getViewBinding().linkArrow.setVisibility(GONE);
            getViewBinding().iconView.setImageResource(R.drawable.icon_clock_03);
        } else {
            getViewBinding().headerView.setVisibility(GONE);
            getViewBinding().text.setText(getResources().getString(R.string.dashboard_history_cell_disabled_details));
            getViewBinding().linkText.setVisibility(VISIBLE);
            getViewBinding().linkText.setText(getResources().getString(R.string.dashboard_history_cell_track_button_title));
            getViewBinding().linkArrow.setVisibility(VISIBLE);
            getViewBinding().iconView.setImageResource(R.drawable.icon_book_01);
        }
    }

    public View getLinkView() {
        return getViewBinding().linkText;
    }

}