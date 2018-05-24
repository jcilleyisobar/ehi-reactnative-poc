package com.ehi.enterprise.android.ui.location;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.MenuItem;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.databinding.ToolbarActivityBinding;
import com.ehi.enterprise.android.models.location.EHIPolicy;
import com.ehi.enterprise.android.ui.fragment.DataBindingViewModelActivity;
import com.ehi.enterprise.android.ui.viewmodel.ManagersAccessViewModel;
import com.ehi.enterprise.android.utils.DLog;
import com.ehi.enterprise.android.utils.FragmentUtils;
import com.ehi.enterprise.android.utils.exceptions.NoArgumentsFoundException;
import com.isobar.android.newinstancer.Extra;
import com.isobar.android.viewmodel.ViewModel;

import java.util.List;

@ViewModel(ManagersAccessViewModel.class)
public class LocationPoliciesListActivity
        extends DataBindingViewModelActivity<ManagersAccessViewModel, ToolbarActivityBinding> {

    private static final String TAG = "LocationPoliciesListActivity";

    @Extra(value = List.class, type = EHIPolicy.class, required = false)
    public static final String EXTRA_POLICIES = "ehi.EXTRA_POLICIES";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setDataBindingContentView(R.layout.ac_toolbar_activity);

        LocationPoliciesListActivityHelper.Extractor extractor = new LocationPoliciesListActivityHelper.Extractor(this);
        if (extractor.extraPolicies() == null) {
            DLog.e(TAG, new NoArgumentsFoundException());
            finish();
            return;
        }

        initViews();
        commitFragments(extractor.extraPolicies());
    }

    private void initViews() {
        getViewBinding().toolbarInclude.toolbar.setTitle("");
        getViewBinding().toolbarInclude.title.setText(R.string.policy_details_title);
        setSupportActionBar(getViewBinding().toolbarInclude.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void commitFragments(List<EHIPolicy> policies) {
        Fragment fragment = new LocationPoliciesListFragmentHelper.Builder()
                .extraPolicies(policies)
                .build();

        new FragmentUtils.Transaction(getSupportFragmentManager(), FragmentUtils.ADD)
                .fragment(fragment)
                .into(R.id.ac_single_fragment_container)
                .commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
