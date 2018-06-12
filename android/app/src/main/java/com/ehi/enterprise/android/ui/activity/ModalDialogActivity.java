package com.ehi.enterprise.android.ui.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.databinding.ModalDialogActivityViewBinding;
import com.ehi.enterprise.android.ui.fragment.DataBindingViewModelActivity;
import com.ehi.enterprise.android.ui.viewmodel.ManagersAccessViewModel;
import com.ehi.enterprise.android.utils.DLog;
import com.ehi.enterprise.android.utils.DisplayUtils;
import com.ehi.enterprise.android.utils.FragmentUtils;
import com.ehi.enterprise.android.utils.exceptions.NoArgumentsFoundException;
import com.isobar.android.newinstancer.Extra;
import com.isobar.android.viewmodel.ViewModel;

@SuppressWarnings({"TryWithIdenticalCatches", "ConstantConditions"})
@ViewModel(ManagersAccessViewModel.class)
public class ModalDialogActivity extends DataBindingViewModelActivity<ManagersAccessViewModel, ModalDialogActivityViewBinding> {

    @Extra(value = boolean.class, required = false)
    public static final String SHOW_CLOSE_BUTTON = "SHOW_CLOSE_BUTTON";
    @Extra(Class.class)
    public static final String FRAGMENT_CLASS = "FRAGMENT_CLASS";
    @Extra(value = Bundle.class, required = false)
    public static final String FRAGMENT_ARGUMENTS = "FRAGMENT_ARGUMENTS";

    private static final String TAG = "ModalDialogActivity";

    private boolean showCloseButton;

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View view) {
            if (view == getViewBinding().closeButton) {
                finish();
            }
        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setDataBindingContentView(R.layout.ac_modal_dialog);

        ModalDialogActivityHelper.Extractor extractor = new ModalDialogActivityHelper.Extractor(this);
        if (extractor.fragmentClass() == null) {
            DLog.e(TAG, new NoArgumentsFoundException());
            finish();
            return;
        }

        showCloseButton = extractor.showCloseButton() != null && extractor.showCloseButton();

        initViews();

        final Class fragmentClassToLoad = extractor.fragmentClass();

        Fragment fragmentToLoad = null;
        try {
            fragmentToLoad = (Fragment) fragmentClassToLoad.newInstance();
        } catch (InstantiationException e) {
            DLog.e(TAG, "Fragment url fail", e);
        } catch (IllegalAccessException e) {
            DLog.e(TAG, "Fragment url fail", e);
        }

        if (fragmentToLoad != null) {
            fragmentToLoad.setArguments(extractor.fragmentArguments());
        }

        getViewBinding().container.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                float maxHeight = DisplayUtils.getScreenHeight(ModalDialogActivity.this) * .71f;
                if (getViewBinding().container.getHeight() > maxHeight) {
                    getViewBinding().container.getLayoutParams().height = (int) maxHeight;
                }
            }
        });

        new FragmentUtils.Transaction(getSupportFragmentManager(), FragmentUtils.REPLACE)
                .fragment(fragmentToLoad)
                .into(R.id.container)
                .commit();
    }

    private void initViews() {
        getViewBinding().titleBar.setVisibility(
                showCloseButton ? View.VISIBLE : View.GONE
        );

        getViewBinding().closeButton.setOnClickListener(mOnClickListener);

        getViewBinding().closeButton.setVisibility(
                showCloseButton ? View.VISIBLE : View.GONE
        );
    }

    @Override
    public void setTitle(CharSequence title) {
        getViewBinding().titleBar.setVisibility(
                TextUtils.isEmpty(title) && !showCloseButton ? View.GONE : View.VISIBLE
        );

        if (!TextUtils.isEmpty(title)) {
            getViewBinding().title.setVisibility(View.VISIBLE);
            getViewBinding().title.setText(title);
        } else {
            getViewBinding().title.setVisibility(View.GONE);
        }
    }

}