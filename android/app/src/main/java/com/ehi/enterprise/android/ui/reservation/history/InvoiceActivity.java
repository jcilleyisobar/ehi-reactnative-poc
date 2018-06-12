package com.ehi.enterprise.android.ui.reservation.history;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.databinding.ToolbarActivityBinding;
import com.ehi.enterprise.android.ui.fragment.DataBindingViewModelActivity;
import com.ehi.enterprise.android.ui.viewmodel.ManagersAccessViewModel;
import com.ehi.enterprise.android.utils.FragmentUtils;
import com.ehi.enterprise.android.utils.PermissionUtils;
import com.ehi.enterprise.android.utils.permission.PermissionRequestHandler;
import com.ehi.enterprise.android.utils.permission.PermissionRequester;
import com.isobar.android.newinstancer.Extra;
import com.isobar.android.viewmodel.ViewModel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ViewModel(ManagersAccessViewModel.class)
public class InvoiceActivity extends DataBindingViewModelActivity<ManagersAccessViewModel, ToolbarActivityBinding>
        implements PermissionRequestHandler {

    @Extra(value = String.class)
    public static final String INVOICE_NUMBER = "EXTRA_INVOICE_NUMBER";

    private Map<Integer, PermissionRequester> mPermissionRequesterMap = new HashMap<>();

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view == getViewBinding().toolbarInclude.icon) {
                onBackPressed();
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setDataBindingContentView(R.layout.ac_toolbar_activity);
        initViews();

        InvoiceActivityHelper.Extractor extractor = new InvoiceActivityHelper.Extractor(this);

        InvoiceFragment fragment = new InvoiceFragmentHelper
                .Builder()
                .invoiceNumber(extractor.invoiceNumber())
                .build();
        new FragmentUtils.Transaction(getSupportFragmentManager(), FragmentUtils.ADD)
                .fragment(fragment)
                .into(R.id.ac_single_fragment_container)
                .commit();
    }

    private void initViews() {
        getViewBinding().toolbarInclude.toolbar.setTitle("");

        getViewBinding().toolbarInclude.title.setText(R.string.invoice_title);

        getViewBinding().toolbarInclude.icon.setVisibility(View.VISIBLE);
        getViewBinding().toolbarInclude.icon.setBackground(getResources().getDrawable(R.drawable.icon_x_white01));
        getViewBinding().toolbarInclude.icon.setOnClickListener(mOnClickListener);

        setSupportActionBar(getViewBinding().toolbarInclude.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
    }

    @Override
    public void requestPermissions(final int permissionRequestCode, final PermissionRequester requester, final String... permissions) {
        final List<String> checkedPermissions = PermissionUtils.checkPermissions(this, permissions);
        if (checkedPermissions.isEmpty()) {
            requester.onRequestPermissionResult(permissionRequestCode, permissions, new int[]{PackageManager.PERMISSION_GRANTED});
        } else {
            PermissionUtils.requestCheckedPermissions(this,
                    checkedPermissions,
                    permissionRequestCode);
            mPermissionRequesterMap.put(permissionRequestCode, requester);
        }
    }

    @Override
    public void onRequestPermissionsResult(final int requestCode,
                                           @NonNull final String[] permissions,
                                           @NonNull final int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mPermissionRequesterMap.get(requestCode)
                .onRequestPermissionResult(requestCode,
                        permissions,
                        grantResults);
        mPermissionRequesterMap.remove(requestCode);
    }
}
