package com.ehi.enterprise.android.ui.reservation;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.databinding.ThreeDSFragmentBinding;
import com.ehi.enterprise.android.ui.fragment.DataBindingViewModelFragment;
import com.ehi.enterprise.android.utils.DialogUtils;
import com.ehi.enterprise.android.utils.EHIBundle;
import com.ehi.enterprise.android.utils.FragmentUtils;
import com.isobar.android.newinstancer.Extra;
import com.isobar.android.viewmodel.ViewModel;

import io.dwak.reactor.ReactorComputation;
import io.dwak.reactor.interfaces.ReactorComputationFunction;

@ViewModel(ThreeDSViewModel.class)
public class ThreeDSFragment extends DataBindingViewModelFragment<ThreeDSViewModel, ThreeDSFragmentBinding> {

    public static final String SCREEN_NAME = "3DSFragment";
    public static final String TAG = "3DSFragment";

    @Extra(String.class)
    public static final String ACS_URL = "ACS_URL";
    @Extra(String.class)
    public static final String PA_REQ = "PA_REQ";
    @Extra(value = String.class, required = false)
    public static final String POSTBACK_URL = "POSTBACK_URL";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            ThreeDSFragmentHelper.Extractor extractor = new ThreeDSFragmentHelper.Extractor(this);
            getViewModel().setAcsUrl(extractor.acsUrl());
            getViewModel().setPaReq(extractor.paReq());
            getViewModel().setPostbackUrl(extractor.postbackUrl());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        createViewBinding(inflater, R.layout.fr_3ds_view, container);
        initViews();
        getActivity().setResult(Activity.RESULT_CANCELED);
        return getViewBinding().getRoot();
    }

    private void initViews() {
        getViewBinding().authenticator.setAuthorizationListener(getViewModel());
    }

    @Override
    protected void initDependencies() {
        super.initDependencies();

        bind(FragmentUtils.progress(getViewModel().progress, getActivity()));
        bind(DialogUtils.errorDialog(getViewModel().errorResponse, getActivity()));

        addReaction("AUTH_REACTION", new ReactorComputationFunction() {
            @Override
            public void react(ReactorComputation reactorComputation) {
                String acsUrl = getViewModel().getAcsUrl();
                String paReq = getViewModel().getPaReq();
                String postbackUrl = getViewModel().getPostbackUrl();

                if (acsUrl != null && paReq != null) {
                    getViewBinding().authenticator.authorize(acsUrl, null, paReq, postbackUrl);
                }
            }
        });

        addReaction("AUTH_COMPLETE", new ReactorComputationFunction() {
            @Override
            public void react(ReactorComputation reactorComputation) {
                final String paRes = getViewModel().getPaRes();

                if (!TextUtils.isEmpty(paRes)) {
                    Intent resultIntent = new Intent();
                    final Bundle bundle = new EHIBundle.Builder()
                            .putString(ReviewFragment.KEY_3DS_PARES, paRes)
                            .createBundle();
                    resultIntent.putExtras(bundle);
                    getActivity().setResult(Activity.RESULT_OK, resultIntent);
                    getActivity().finish();
                }
            }
        });
    }
}
