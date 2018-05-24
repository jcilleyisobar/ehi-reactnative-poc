package com.ehi.enterprise.android.ui.dashboard.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.databinding.WeekendSpecialDashboardViewBinding;
import com.ehi.enterprise.android.ui.view.DataBindingViewModelView;
import com.ehi.enterprise.android.ui.viewmodel.ManagersAccessViewModel;
import com.isobar.android.viewmodel.ViewModel;

@ViewModel(ManagersAccessViewModel.class)
public class WeekendSpecialDashboardView extends DataBindingViewModelView<ManagersAccessViewModel, WeekendSpecialDashboardViewBinding> {

    public WeekendSpecialDashboardView(final Context context, AttributeSet attrs) {
        super(context, attrs);
        createView(true);
    }

    public WeekendSpecialDashboardView(Context context) {
        this(context, true);
    }

    public WeekendSpecialDashboardView(Context context, boolean attachToParent) {
        super(context);
        createView(attachToParent);
    }

    private void createView(boolean attachToParent) {
        createViewBinding(R.layout.v_weekend_special_dashboard, attachToParent);
    }

    public void setTitle(String title) {
        getViewBinding().title.setText(title);
    }

    //necessary LinearLayout for menu animations
    public View getLayout() {
        removeAllViews();
        return getViewBinding().container;
    }

    public void setOnGetStartedClickListener(OnClickListener listener) {
        getViewBinding().getStartedButton.setOnClickListener(listener);
    }

    @Override
    public WeekendSpecialDashboardViewBinding getViewBinding() {
        return super.getViewBinding();
    }

}