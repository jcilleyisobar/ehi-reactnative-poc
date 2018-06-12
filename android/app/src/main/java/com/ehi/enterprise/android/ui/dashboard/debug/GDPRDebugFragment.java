package com.ehi.enterprise.android.ui.dashboard.debug;

import android.os.Bundle;
import android.os.HandlerThread;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.appsee.Appsee;
import com.crittercism.app.Crittercism;
import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.databinding.DebugGDPRFragmentBinding;
import com.ehi.enterprise.android.ui.fragment.DataBindingViewModelFragment;
import com.ehi.enterprise.android.ui.viewmodel.ManagersAccessViewModel;
import com.isobar.android.viewmodel.ViewModel;
import com.localytics.android.Localytics;

/*
 *  General Data Protection Regulation Debug Fragment
 */
@ViewModel(ManagersAccessViewModel.class)
public class GDPRDebugFragment extends DataBindingViewModelFragment<ManagersAccessViewModel, DebugGDPRFragmentBinding> {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        createViewBinding(inflater, R.layout.fr_debug_gdpr, container);

        HandlerThread thread = new HandlerThread("opt-out-check", android.os.Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();
        new android.os.Handler(thread.getLooper()).post(getSdksOptOutStatusRunnable());

        return getViewBinding().getRoot();
    }

    private Runnable getSdksOptOutStatusRunnable() {
        return new Runnable() {
            @Override
            public void run() {
                getViewBinding().localyticsWipe.setImageResource(getIcon(Localytics.isPrivacyOptedOut()));
                getViewBinding().localyticsOptOut.setImageResource(getIcon(Localytics.isOptedOut()));
                getViewBinding().appsee.setImageResource(getIcon(Appsee.getOptOutStatus()));
                getViewBinding().appteligent.setImageResource(getIcon(Crittercism.getOptOutStatus()));
            }
        };
    }

    private int getIcon(boolean optOut) {
        return optOut ? R.drawable.icon_x_red01 : R.drawable.icon_check_04;
    }
}
