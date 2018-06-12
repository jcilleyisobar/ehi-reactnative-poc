package com.ehi.enterprise.android.ui.profile;

import android.os.Bundle;
import android.view.MenuItem;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.databinding.ToolbarActivityBinding;
import com.ehi.enterprise.android.ui.fragment.DataBindingViewModelActivity;
import com.ehi.enterprise.android.ui.viewmodel.ManagersAccessViewModel;
import com.ehi.enterprise.android.utils.FragmentUtils;
import com.isobar.android.newinstancer.NoExtras;
import com.isobar.android.viewmodel.ViewModel;

@NoExtras
@ViewModel(ManagersAccessViewModel.class)
public class MemberInfoActivity
        extends DataBindingViewModelActivity<ManagersAccessViewModel, ToolbarActivityBinding> {

    private static final String TAG = "MemberInfoActivity";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setDataBindingContentView(R.layout.ac_toolbar_activity);
        initViews();
        commitFragments();
    }

    private void initViews() {
        getViewBinding().toolbarInclude.toolbar.setTitle("");
        getViewBinding().toolbarInclude.title.setText(R.string.profile_edit_member_info_title);
        setSupportActionBar(getViewBinding().toolbarInclude.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void commitFragments() {
        new FragmentUtils.Transaction(getSupportFragmentManager(), FragmentUtils.ADD)
                .fragment(new MemberInfoFragmentHelper.Builder().build())
                .into(R.id.ac_single_fragment_container)
                .commit();
    }

    @Override
    public void setTitle(CharSequence title) {
        getViewBinding().toolbarInclude.title.setText(title);
    }

    @Override
    public void setTitle(int titleId) {
        getViewBinding().toolbarInclude.title.setText(titleId);
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
