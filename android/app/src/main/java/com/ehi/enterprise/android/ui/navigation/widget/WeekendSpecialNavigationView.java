package com.ehi.enterprise.android.ui.navigation.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.databinding.WeekendSpecialNavigationViewBinding;
import com.ehi.enterprise.android.ui.view.DataBindingViewModelView;
import com.ehi.enterprise.android.ui.viewmodel.ManagersAccessViewModel;
import com.isobar.android.viewmodel.ViewModel;

@ViewModel(ManagersAccessViewModel.class)
public class WeekendSpecialNavigationView extends DataBindingViewModelView<ManagersAccessViewModel, WeekendSpecialNavigationViewBinding> {

    public WeekendSpecialNavigationView(final Context context, AttributeSet attrs) {
        super(context, attrs);
        createView(true);
    }

    public WeekendSpecialNavigationView(Context context) {
        this(context, true);
    }

    public WeekendSpecialNavigationView(Context context, boolean attachToParent) {
        super(context);
        createView(attachToParent);
    }

    private void createView(boolean attachToParent) {
        createViewBinding(R.layout.v_weekend_special_navigation, attachToParent);
    }

    public void setTitle(String title) {
        getViewBinding().title.setText(title);
    }

    //necessary LinearLayout for menu animations
    public View getLayout() {
        removeAllViews();
        return getViewBinding().container;
    }

    @Override
    public WeekendSpecialNavigationViewBinding getViewBinding() {
        return super.getViewBinding();
    }

}