package com.ehi.enterprise.android.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.network.interfaces.IApiServiceListener;
import com.ehi.enterprise.android.network.interfaces.IRequestProcessorService;
import com.ehi.enterprise.android.ui.activity.IEhiNetworkConnection;
import com.ehi.enterprise.android.ui.activity.ModalActivityHelper;
import com.ehi.enterprise.android.ui.activity.ModalDialogActivityHelper;

public abstract class ApiServiceFragment extends BaseFragment implements IEhiNetworkConnection {

    public static final String TAG = "ApiServiceFragment";

    private IApiServiceListener mApiUserDelegate = new IApiServiceListener() {
        @Override
        public void onApiServiceConnected(IRequestProcessorService apiService) {
            ApiServiceFragment.this.onApiServiceConnected();
        }
    };

    protected void onApiServiceConnected() {
    }

    @Override
    public IRequestProcessorService getApiService() {
        return ((IEhiNetworkConnection) getActivity()).getApiService();
    }

    @Override
    public boolean isApiServiceConnected() {
        return ((IEhiNetworkConnection) getActivity()).isApiServiceConnected();
    }

    @Override
    public void addServiceConnectionObservable(IApiServiceListener apiUserDelegate) {
        ((IEhiNetworkConnection) getActivity()).addServiceConnectionObservable(apiUserDelegate);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getActivity() instanceof IEhiNetworkConnection) {
            IEhiNetworkConnection network = (IEhiNetworkConnection) getActivity();
            if (network.isApiServiceConnected()) {
                onApiServiceConnected();
            } else {
                network.addServiceConnectionObservable(mApiUserDelegate);
            }
        } else {
            throw new IllegalStateException("You can use ApiServiceFragment only inside Activity which implements IEHINetworkConnection");
        }
    }

    protected void showModalDialogForResult(@NonNull Activity activity, @NonNull Fragment fragment, int requestCode) {
        Intent intent = new ModalDialogActivityHelper.Builder()
                .fragmentClass(fragment.getClass())
                .fragmentArguments(fragment.getArguments())
                .build(activity);
        startActivityForResult(intent, requestCode);
        getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    /**
     * Display fragment in a modal dialog
     *
     * @param activity        Activity to use as starting context
     * @param fragment        Fragment to display in the dialog
     * @param showCloseButton If should show close button
     */
    protected void showModalDialog(@NonNull Activity activity, @NonNull Fragment fragment,
                                   boolean showCloseButton) {
        Intent intent = new ModalDialogActivityHelper.Builder()
                .fragmentClass(fragment.getClass())
                .fragmentArguments(fragment.getArguments())
                .showCloseButton(showCloseButton)
                .build(activity);
        startActivity(intent);
        getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    protected void showModalDialog(@NonNull Activity activity, @NonNull Fragment fragment,
                                   boolean showCloseButton, int requestCode) {
        Intent intent = new ModalDialogActivityHelper.Builder()
                .fragmentClass(fragment.getClass())
                .fragmentArguments(fragment.getArguments())
                .showCloseButton(showCloseButton)
                .build(activity);
        startActivityForResult(intent, requestCode);
        getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    protected void showModalDialog(@NonNull Activity activity, @NonNull Fragment fragment) {
        Intent intent = new ModalDialogActivityHelper.Builder()
                .fragmentClass(fragment.getClass())
                .fragmentArguments(fragment.getArguments())
                .build(activity);
        startActivity(intent);
        getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    protected void showModal(@NonNull Activity activity, @NonNull Fragment fragment) {
        Intent intent = new ModalActivityHelper.Builder()
                .fragmentClass(fragment.getClass())
                .fragmentArguments(fragment.getArguments())
                .build(activity);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.modal_slide_in, R.anim.modal_stay);
    }

    protected void showModalWithSearchHeader(@NonNull Activity activity, @NonNull Fragment fragment) {
        Intent intent = new ModalActivityHelper.Builder()
                .fragmentClass(fragment.getClass())
                .fragmentArguments(fragment.getArguments())
                .showSearchHeader(true)
                .build(activity);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.modal_slide_in, R.anim.modal_stay);
    }

    protected void showModalForResult(@NonNull Activity activity, @NonNull Fragment fragment, int requestCode) {
        Intent intent = new ModalActivityHelper.Builder()
                .fragmentClass(fragment.getClass())
                .fragmentArguments(fragment.getArguments())
                .build(activity);
        startActivityForResult(intent, requestCode);
        getActivity().overridePendingTransition(R.anim.modal_slide_in, R.anim.modal_stay);
    }

    /**
     * Display a DialogFragment
     *
     * @param dialogFragment DialogFragment to display in the dialog
     */
    protected void showDialog(@NonNull DialogFragment dialogFragment) {
        dialogFragment.show(getFragmentManager(), dialogFragment.getClass().getName());
    }

    protected void showModalWithSearchHeaderForResult(@NonNull Activity activity, @NonNull Fragment fragment, int requestCode) {
        Intent intent = new ModalActivityHelper.Builder()
                .fragmentClass(fragment.getClass())
                .fragmentArguments(fragment.getArguments())
                .showSearchHeader(true)
                .build(activity);
        startActivityForResult(intent, requestCode);
        getActivity().overridePendingTransition(R.anim.modal_slide_in, R.anim.modal_stay);
    }

}
