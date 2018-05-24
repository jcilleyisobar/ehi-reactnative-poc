package com.ehi.enterprise.android.ui.profile;

import android.content.DialogInterface;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.ui.fragment.DataBindingViewModelFragment;
import com.ehi.enterprise.android.utils.DialogUtils;
import com.ehi.enterprise.android.utils.FragmentUtils;

public abstract class EditPaymentMethodFragment<T extends EditPaymentMethodFragmentViewModel, V extends ViewDataBinding> extends DataBindingViewModelFragment<T, V> {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_edit_payment, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_delete) {
            delete();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void initDependencies() {
        super.initDependencies();

        bind(FragmentUtils.progress(getViewModel().progress, getActivity()));
        bind(DialogUtils.errorDialog(getViewModel().errorResponse, getActivity()));
    }

    protected abstract int getDeleteAlertTitleStringResId();

    protected abstract int getDeleteAlertMessageStringResId();

    private void delete() {
        new AlertDialog.Builder(getContext())
                .setTitle(getDeleteAlertTitleStringResId())
                .setMessage(getDeleteAlertMessageStringResId())
                .setPositiveButton(getString(R.string.profile_payment_options_delete_action_text), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getViewModel().delete();
                    }
                })
                .setNegativeButton(getString(R.string.standard_cancel_button_title), null)
                .create()
                .show();
    }
}
