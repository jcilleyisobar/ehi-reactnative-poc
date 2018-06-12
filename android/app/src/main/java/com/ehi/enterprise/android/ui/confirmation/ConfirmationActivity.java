package com.ehi.enterprise.android.ui.confirmation;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.databinding.ToolbarActivityBinding;
import com.ehi.enterprise.android.models.notification.EHINotification;
import com.ehi.enterprise.android.models.reservation.EHIReservation;
import com.ehi.enterprise.android.ui.fragment.DataBindingViewModelActivity;
import com.ehi.enterprise.android.ui.reservation.KeyFactsActionDelegate;
import com.ehi.enterprise.android.ui.reservation.interfaces.IKeyFactsActionDelegate;
import com.ehi.enterprise.android.ui.viewmodel.ManagersAccessViewModel;
import com.ehi.enterprise.android.utils.DLog;
import com.ehi.enterprise.android.utils.FragmentUtils;
import com.ehi.enterprise.android.utils.IntentUtils;
import com.ehi.enterprise.android.utils.exceptions.NoArgumentsFoundException;
import com.isobar.android.newinstancer.Extra;
import com.isobar.android.viewmodel.ViewModel;

@ViewModel(ManagersAccessViewModel.class)
public class ConfirmationActivity
        extends DataBindingViewModelActivity<ManagersAccessViewModel, ToolbarActivityBinding> {

    private static final String TAG = ConfirmationActivity.class.getSimpleName();

    @Extra(value = EHIReservation.class, required = false, large = true)
    public static final String EXTRA_RESERVATION = "ehi.EXTRA_RESERVATION";
    @Extra(value = EHINotification.class, required = false)
    public static final String EXTRA_NOTIFICATION = "ehi.EXTRA_NOTIFICATION";
    @Extra(boolean.class)
    public static final String IS_MODIFY = "ehi.IS_MODIFY";
    @Extra(value = boolean.class, required = false)
    public static final String EXIT_GOES_HOME = "EXIT_GOES_HOME";

    private boolean mExitGoesHome;
    private IKeyFactsActionDelegate mKeyFactsActionDelegate;
    private ConfirmationFragment confirmationFragment;


    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view == getViewBinding().toolbarInclude.icon) {
                if (mExitGoesHome) {
                    confirmationFragment.returnToHomeScreen();
                } else {
                    onBackPressed();
                }
            }
        }
    };

    private ConfirmationFragment getConfirmationFragment() {
        return (ConfirmationFragment) getSupportFragmentManager().findFragmentById(R.id.ac_single_fragment_container);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setDataBindingContentView(R.layout.ac_toolbar_activity);
        if (getIntent().getExtras() != null) {
            ConfirmationActivityHelper.Extractor extractor = new ConfirmationActivityHelper.Extractor(this);
            if (extractor.extraNotification() != null) {
                mExitGoesHome = extractor.exitGoesHome() != null && extractor.exitGoesHome();
                confirmationFragment = new ConfirmationFragmentHelper
                        .Builder()
                        .extraNotification(extractor.extraNotification())
                        .isModify(extractor.isModify())
                        .build();
                new FragmentUtils.Transaction(getSupportFragmentManager(), FragmentUtils.ADD)
                        .fragment(confirmationFragment)
                        .into(R.id.ac_single_fragment_container)
                        .commit();
            } else if (extractor.extraReservation() != null) {
                mExitGoesHome = extractor.exitGoesHome() != null && extractor.exitGoesHome();
                confirmationFragment = new ConfirmationFragmentHelper
                        .Builder()
                        .extraReservation(extractor.extraReservation())
                        .isModify(extractor.isModify())
                        .build();
                new FragmentUtils.Transaction(getSupportFragmentManager(), FragmentUtils.ADD)
                        .fragment(confirmationFragment)
                        .into(R.id.ac_single_fragment_container)
                        .commit();
            } else {
                // this can happen in case screen was restored from a saved state
                //in this case disk cash for arguments will be empty and we would hae nothing to show
                //TODO TODO TODO
                finish();
                return;
            }
        } else {
            DLog.e(TAG, new NoArgumentsFoundException());
            finish();
            return;
        }
        mKeyFactsActionDelegate = new KeyFactsActionDelegate(this);
        initViews();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        ConfirmationFragment frag = getConfirmationFragment();
        if (frag != null) {
            frag.setIsModify(true);
            frag.updateReservationInfo();
        }
    }

    private void initViews() {
        getViewBinding().toolbarInclude.toolbar.setTitle("");

        getViewBinding().toolbarInclude.title.setText(R.string.reservation_confirmation_navigation_title);

        getViewBinding().toolbarInclude.icon.setVisibility(View.VISIBLE);
        getViewBinding().toolbarInclude.icon.setBackground(getResources().getDrawable(R.drawable.icon_x_white01));
        getViewBinding().toolbarInclude.icon.setOnClickListener(mOnClickListener);


        setSupportActionBar(getViewBinding().toolbarInclude.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
    }

    @Override
    public void setTitle(CharSequence title) {
        getViewBinding().toolbarInclude.title.setText(title);
    }

    @Override
    public void setTitle(int titleId) {
        getViewBinding().toolbarInclude.title.setText(titleId);
    }

}