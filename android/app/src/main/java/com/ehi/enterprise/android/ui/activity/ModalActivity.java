package com.ehi.enterprise.android.ui.activity;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.databinding.ModalActivityBinding;
import com.ehi.enterprise.android.ui.fragment.DataBindingViewModelActivity;
import com.ehi.enterprise.android.ui.interfaces.ToolbarNavigationListener;
import com.ehi.enterprise.android.ui.reservation.interfaces.ModalFragment;
import com.ehi.enterprise.android.ui.viewmodel.ManagersAccessViewModel;
import com.ehi.enterprise.android.utils.BaseAppUtils;
import com.ehi.enterprise.android.utils.DLog;
import com.ehi.enterprise.android.utils.FragmentUtils;
import com.ehi.enterprise.android.utils.PermissionUtils;
import com.ehi.enterprise.android.utils.exceptions.NoArgumentsFoundException;
import com.ehi.enterprise.android.utils.permission.PermissionRequestHandler;
import com.ehi.enterprise.android.utils.permission.PermissionRequester;
import com.isobar.android.newinstancer.Extra;
import com.isobar.android.viewmodel.ViewModel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings({"TryWithIdenticalCatches", "ConstantConditions"})
@ViewModel(ManagersAccessViewModel.class)
public class ModalActivity extends DataBindingViewModelActivity<ManagersAccessViewModel, ModalActivityBinding>
        implements PermissionRequestHandler {

    private static final String TAG = "ModalActivity";

    @Extra(Class.class)
    public static final String FRAGMENT_CLASS = "Fragment class";
    @Extra(value = Bundle.class, required = false)
    public static final String FRAGMENT_ARGUMENTS = "FRAGMENT_ARGUMENTS";
    @Extra(value = boolean.class, required = false)
    public static final String SHOW_SEARCH_HEADER = "SHOW_SEARCH_HEADER";

    public static final String MODAL_FRAGMENT = "MODAL_FRAGMENT";

    private Map<Integer, PermissionRequester> mPermissionRequesterMap = new HashMap<>();

    private SearchListener searchListener = new SearchListener() {
        @Override
        public void onSearchTerm(String term) {
        }
    };

    private TextWatcher mSearchTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            searchListener.onSearchTerm(s.toString());
        }
    };

    private View.OnClickListener mOnClearInputClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            getViewBinding().searchInput.setText("");
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setDataBindingContentView(R.layout.ac_modal);
        getViewBinding().toolbarView.toolbar.setTitle("");
        setSupportActionBar(getViewBinding().toolbarView.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ModalActivityHelper.Extractor extractor = new ModalActivityHelper.Extractor(this);
        if (extractor.fragmentClass() == null) {
            DLog.e(TAG, new NoArgumentsFoundException());
            finish();
            return;
        }

        Class fragmentClassToLoad = extractor.fragmentClass();

        Fragment loadedFragment = getSupportFragmentManager().findFragmentByTag(MODAL_FRAGMENT);
        if (loadedFragment != null && loadedFragment.getClass().equals(fragmentClassToLoad)) {
            return;
        }

        Fragment fragmentToLoad = null;
        try {
            fragmentToLoad = (Fragment) fragmentClassToLoad.newInstance();
        } catch (InstantiationException e) {
            DLog.e(TAG, "Fragment load fail", e);
        } catch (IllegalAccessException e) {
            DLog.e(TAG, "Fragment load fail", e);
        }
        if (fragmentToLoad != null) {
            fragmentToLoad.setArguments(extractor.fragmentArguments());

            final Fragment immutableFragmentToLoad = fragmentToLoad;
            if (fragmentToLoad instanceof ToolbarNavigationListener) {
                getViewBinding().toolbarView.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((ToolbarNavigationListener)immutableFragmentToLoad).onNavigationItemClicked();
                    }
                });
            }
        }

        if (extractor.showSearchHeader() != null && extractor.showSearchHeader()) {
            getViewBinding().searchInputArea.setVisibility(View.VISIBLE);
            getViewBinding().clearInputButton.setVisibility(View.VISIBLE);

            getViewBinding().searchInput.addTextChangedListener(mSearchTextWatcher);
            getViewBinding().searchInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                        searchListener.onSearchTerm(getViewBinding().searchInput.getText().toString());
                    }
                    return false;
                }
            });

            getViewBinding().searchInput.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if (event.getAction() == KeyEvent.ACTION_DOWN
                            && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                        searchListener.onSearchTerm(getViewBinding().searchInput.getText().toString());
                    }
                    return false;
                }
            });
            BaseAppUtils.showKeyboardForView(getViewBinding().searchInput);

            getViewBinding().clearInputButton.setOnClickListener(mOnClearInputClickListener);

            if (fragmentToLoad instanceof SearchListener) {
                searchListener = (SearchListener) fragmentToLoad;
            }
        } else {
            getViewBinding().searchInputArea.setVisibility(View.GONE);
            getViewBinding().clearInputButton.setVisibility(View.GONE);
        }

        new FragmentUtils.Transaction(getSupportFragmentManager(), FragmentUtils.REPLACE)
                .fragment(fragmentToLoad, MODAL_FRAGMENT)
                .into(R.id.modal_container)
                .commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        final Fragment fragmentByTag = getSupportFragmentManager().findFragmentByTag(MODAL_FRAGMENT);
        if (fragmentByTag instanceof ModalFragment) {
            if (!((ModalFragment) fragmentByTag).onBackPressed()) {
                super.onBackPressed();
                overridePendingTransition(R.anim.modal_stay, R.anim.modal_slide_out);
            }
        } else {
            super.onBackPressed();
            overridePendingTransition(R.anim.modal_stay, R.anim.modal_slide_out);
        }
    }

    @Override
    public void setTitle(CharSequence title) {
        getViewBinding().toolbarView.title.setText(title);
    }

    @Override
    public void setTitle(int titleId) {
        getViewBinding().toolbarView.title.setText(titleId);
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

    public interface SearchListener {
        void onSearchTerm(String term);
    }
}