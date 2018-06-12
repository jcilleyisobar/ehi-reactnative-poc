package com.ehi.enterprise.android.ui.location;

import android.os.Bundle;
import android.view.MenuItem;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.databinding.ToolbarActivityBinding;
import com.ehi.enterprise.android.models.location.EHIWayfindingStep;
import com.ehi.enterprise.android.ui.fragment.DataBindingViewModelActivity;
import com.ehi.enterprise.android.ui.viewmodel.ManagersAccessViewModel;
import com.ehi.enterprise.android.utils.FragmentUtils;
import com.isobar.android.newinstancer.Extra;
import com.isobar.android.viewmodel.ViewModel;

import java.util.List;

@ViewModel(ManagersAccessViewModel.class)
public class DirectionsFromTerminalActivity extends DataBindingViewModelActivity<ManagersAccessViewModel, ToolbarActivityBinding> {

    private static final String TAG = DirectionsFromTerminalActivity.class.getSimpleName();

    @Extra(value = List.class, type = EHIWayfindingStep.class)
    public static final String WAYFINDING_STEPS = "ehi.WAYFINDING_STEPS";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setDataBindingContentView(R.layout.ac_toolbar_activity);
        initViews();
        commitFragments(new DirectionsFromTerminalActivityHelper.Extractor(this).wayfindingSteps());
    }

    private void initViews() {
        getViewBinding().toolbarInclude.toolbar.setTitle("");
        getViewBinding().toolbarInclude.title.setText(R.string.terminal_directions_title);
        setSupportActionBar(getViewBinding().toolbarInclude.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void commitFragments(List<EHIWayfindingStep> steps) {
        new FragmentUtils.Transaction(getSupportFragmentManager(), FragmentUtils.ADD)
                .fragment(new DirectionsFromTerminalFragmentHelper.Builder().wayfindingSteps(steps).build())
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
