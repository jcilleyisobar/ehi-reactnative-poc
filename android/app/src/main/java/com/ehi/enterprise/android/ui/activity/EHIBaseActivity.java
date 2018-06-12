package com.ehi.enterprise.android.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.ui.util.ProgressFragment;
import com.ehi.enterprise.android.utils.FragmentUtils;
import com.ehi.enterprise.android.utils.manager.AnalyticsManager;


public abstract class EHIBaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AnalyticsManager.getInstance().initialize(this);
    }


    @Override
    public void onBackPressed() {
        ProgressFragment fragment = (ProgressFragment) getSupportFragmentManager().findFragmentByTag(FragmentUtils.PROGRESS_TAG);
        if (fragment != null && fragment.isVisible()) {
            if (fragment.isCancelable()) {
                super.onBackPressed();
            }
        } else {
            super.onBackPressed();
        }
    }

    public void showModal(Fragment fragment) {
        Intent intent = new ModalActivityHelper.Builder()
                .fragmentClass(fragment.getClass())
                .fragmentArguments(fragment.getArguments())
                .build(this);
        startActivity(intent);
        overridePendingTransition(R.anim.modal_slide_in, R.anim.modal_stay);
    }

    protected void showModalForResult(Fragment fragment, int requestCode) {
        Intent intent = new ModalActivityHelper.Builder()
                .fragmentClass(fragment.getClass())
                .fragmentArguments(fragment.getArguments())
                .build(this);
        startActivityForResult(intent, requestCode);
        overridePendingTransition(R.anim.modal_slide_in, R.anim.modal_stay);
    }

    /**
     * Display fragment in a modal dialog
     *
     * @param fragment Fragment to display in the dialog
     */
    public void showModalDialog(@NonNull Fragment fragment) {
        Intent intent = new ModalDialogActivityHelper.Builder()
                .fragmentClass(fragment.getClass())
                .fragmentArguments(fragment.getArguments())
                .build(this);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    /**
     * Display a DialogFragment
     *
     * @param dialogFragment DialogFragment to display in the dialog
     */
    protected void showDialog(@NonNull DialogFragment dialogFragment) {
        dialogFragment.show(getSupportFragmentManager(), dialogFragment.getClass().getName());
    }

    public void showModalDialogForResult(@NonNull Fragment fragment, int requestCode) {
        Intent intent = new ModalDialogActivityHelper.Builder()
                .fragmentClass(fragment.getClass())
                .fragmentArguments(fragment.getArguments())
                .build(this);
        startActivityForResult(intent, requestCode);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}
