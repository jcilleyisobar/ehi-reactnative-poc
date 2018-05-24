package com.ehi.enterprise.android.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.MenuItem;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.utils.FragmentUtils;

public abstract class BaseFragment extends Fragment {

    private boolean shouldHideCallMenuItem = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(shouldHideCallMenuItem);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        if (shouldHideCallMenuItem) {
            MenuItem item = menu.findItem(R.id.action_call);
            if (item != null) {
                item.setVisible(false);
            }
        }
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onStop() {
        super.onStop();
        FragmentUtils.removeProgressFragment(getActivity());
    }

    public void setShouldHideCallMenuItem(boolean shouldHide) {
        shouldHideCallMenuItem = shouldHide;
    }
}
