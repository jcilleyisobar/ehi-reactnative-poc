package com.ehi.enterprise.android.ui.navigation.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.databinding.NavigationSectionHeaderBinding;
import com.ehi.enterprise.android.ui.view.DataBindingViewModelView;
import com.ehi.enterprise.android.ui.viewmodel.ManagersAccessViewModel;
import com.isobar.android.viewmodel.ViewModel;

@ViewModel(ManagersAccessViewModel.class)
public class NavigationSectionHeaderView extends DataBindingViewModelView<ManagersAccessViewModel, NavigationSectionHeaderBinding> {

    public NavigationSectionHeaderView(final Context context, AttributeSet attrs) {
        super(context, attrs);
        createView(true);
    }

    public NavigationSectionHeaderView(Context context) {
        this(context, true);
    }

    public NavigationSectionHeaderView(Context context, boolean attachToParent) {
        super(context);
        createView(attachToParent);
    }

    private void createView(boolean attachToParent) {
        createViewBinding(R.layout.v_navigation_section_header, attachToParent);
    }

    public void setTitle(String title) {
        getViewBinding().title.setText(title);
    }

    // necessary LinearLayout for menu animations
    public View getLayout() {
        removeAllViews();
        return getViewBinding().navigationLinearLayout;
    }

    @Override
    public NavigationSectionHeaderBinding getViewBinding() {
        return super.getViewBinding();
    }

}