package com.ehi.enterprise.android.ui.settings;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.databinding.NotificationSettingsFragmentBinding;
import com.ehi.enterprise.android.ui.fragment.DataBindingViewModelFragment;
import com.isobar.android.newinstancer.Extra;
import com.isobar.android.viewmodel.ViewModel;

import io.dwak.reactorbinding.activity.ReactorActivity;
import io.dwak.reactorbinding.view.ReactorRadioGroup;

@ViewModel(NotificationSettingsViewModel.class)
public class NotificationSettingsFragment extends DataBindingViewModelFragment<NotificationSettingsViewModel, NotificationSettingsFragmentBinding> {
    @Extra(boolean.class)
    public static final String IS_PICKUP = "IS_PICKUP";

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getViewModel().setIsPickup(new NotificationSettingsFragmentHelper.Extractor(this).isPickup());
    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        createViewBinding(inflater, R.layout.fr_notification_settings, container);
        return getViewBinding().getRoot();
    }

    @Override
    protected void initDependencies() {
        super.initDependencies();
        bind(ReactorActivity.titleRes(getViewModel().title, getActivity()),
                ReactorActivity.finish(getViewModel().finish, getActivity()));
        bind(ReactorRadioGroup.bindChecked(getViewModel().radioGroup.checkedId(), getViewBinding().radioGroup));
        getViewModel().setCanFinish(true);
    }
}
