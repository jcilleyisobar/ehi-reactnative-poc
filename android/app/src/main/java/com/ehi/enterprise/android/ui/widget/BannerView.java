package com.ehi.enterprise.android.ui.widget;

import android.content.Context;
import android.util.AttributeSet;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.databinding.BannerBinding;
import com.ehi.enterprise.android.ui.view.DataBindingViewModelView;
import com.ehi.enterprise.android.ui.viewmodel.ManagersAccessViewModel;
import com.isobar.android.viewmodel.ViewModel;

@ViewModel(ManagersAccessViewModel.class)
public class BannerView extends DataBindingViewModelView<ManagersAccessViewModel, BannerBinding> {

    public BannerView(Context context, AttributeSet attrs) {
        super(context, attrs);

        if (isInEditMode()) {
            addView(inflate(context, R.layout.v_banner, null));
        } else {
            createViewBinding(R.layout.v_banner);
        }
    }

    public void setMessage(CharSequence title) {
        getViewBinding().message.setText(title);
    }

    public void setMessage(int res) {
        getViewBinding().message.setText(res);
    }

    public void setIcon(int resource) {
        getViewBinding().icon.setBackgroundResource(resource);
    }
}
