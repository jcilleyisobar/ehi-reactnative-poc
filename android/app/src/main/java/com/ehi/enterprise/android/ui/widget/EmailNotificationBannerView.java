package com.ehi.enterprise.android.ui.widget;

import android.content.Context;
import android.util.AttributeSet;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.databinding.EmailNotificationViewBinding;
import com.ehi.enterprise.android.ui.view.DataBindingViewModelView;
import com.ehi.enterprise.android.ui.viewmodel.ManagersAccessViewModel;
import com.isobar.android.viewmodel.ViewModel;

@ViewModel(ManagersAccessViewModel.class)
public class EmailNotificationBannerView extends DataBindingViewModelView<ManagersAccessViewModel, EmailNotificationViewBinding> {

    public EmailNotificationBannerView(Context context, AttributeSet attrs) {
        super(context, attrs);

        if (isInEditMode()) {
            addView(inflate(context, R.layout.v_email_notification_banner, null));
        } else {
            createViewBinding(R.layout.v_email_notification_banner);
        }
    }

    public void setUp(CharSequence title) {
        setVisibility(VISIBLE);
        getViewBinding().itemTitle.setText(title);
    }
}
