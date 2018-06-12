package com.ehi.enterprise.android.ui.rewards.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.databinding.ExpandingHeaderContentViewBinding;
import com.ehi.enterprise.android.ui.view.DataBindingViewModelView;
import com.ehi.enterprise.android.ui.viewmodel.ManagersAccessViewModel;
import com.isobar.android.viewmodel.ViewModel;

@ViewModel(ManagersAccessViewModel.class)
public class ExpandingHeaderContentView extends DataBindingViewModelView<ManagersAccessViewModel, ExpandingHeaderContentViewBinding> {

    public ExpandingHeaderContentView(Context context) {
        this(context, null, 0);
    }

    public ExpandingHeaderContentView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ExpandingHeaderContentView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        if (isInEditMode()) {
            addView(inflate(context, R.layout.v_expanding_header_content_view, null));
            return;
        }

        createViewBinding(R.layout.v_expanding_header_content_view);
    }

    public void setupView(String title, int color, boolean showCheck, boolean showCurrentTier) {
        getViewBinding().sectionHeader.setText(title);
        getViewBinding().sectionHeader.setBackgroundColor(color);
        getViewBinding().tierCheck.setVisibility(showCheck ? View.VISIBLE : View.GONE);
        getViewBinding().yourTier.setVisibility(showCurrentTier ? View.VISIBLE : View.GONE);
    }

}