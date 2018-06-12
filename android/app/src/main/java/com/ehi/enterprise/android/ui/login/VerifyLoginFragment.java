package com.ehi.enterprise.android.ui.login;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.databinding.VerifyLoginFragmentBinding;
import com.ehi.enterprise.android.ui.fragment.DataBindingViewModelFragment;
import com.ehi.enterprise.android.utils.DialogUtils;
import com.ehi.enterprise.android.utils.EHITextUtils;
import com.ehi.enterprise.android.utils.FingerprintUtils;
import com.ehi.enterprise.android.utils.FragmentUtils;
import com.ehi.enterprise.android.utils.ToastUtils;
import com.ehi.enterprise.android.utils.fingerprint.FingerprintAuthDelegate;
import com.isobar.android.newinstancer.NoExtras;
import com.isobar.android.viewmodel.ViewModel;

import java.security.KeyStore;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;

import io.dwak.reactor.ReactorComputation;
import io.dwak.reactor.interfaces.ReactorComputationFunction;
import io.dwak.reactorbinding.view.ReactorView;
import io.dwak.reactorbinding.widget.ReactorTextView;

@NoExtras
@ViewModel(VerifyLoginViewModel.class)
public class VerifyLoginFragment extends DataBindingViewModelFragment<VerifyLoginViewModel, VerifyLoginFragmentBinding> {

    private FingerprintAuthDelegate mFingerprintAuth;

    //region OnClickListeners
    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view == getViewBinding().continueButton) {
                getViewModel().attemptLogin(false);
            } else if (view == getViewBinding().cancelButton) {
                getActivity().setResult(Activity.RESULT_CANCELED);
                getActivity().finish();
            } else if (view == getViewBinding().showPasswordContainer) {
                if (getViewBinding().editPasswordField.getInputType() == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
                    getViewBinding().editPasswordField.setInputType(InputType.TYPE_CLASS_TEXT |
                            InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    getViewBinding().editPasswordField.setTypeface(getViewBinding().editPasswordField.getTypeface());
                    getViewBinding().showPasswordContainer.setImageDrawable(getResources().getDrawable(R.drawable.icon_show_02));
                } else {
                    getViewBinding().editPasswordField.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    getViewBinding().editPasswordField.setTypeface(getViewBinding().editPasswordField.getTypeface());
                    getViewBinding().showPasswordContainer.setImageDrawable(getResources().getDrawable(R.drawable.icon_show));
                }
                getViewBinding().editPasswordField.setSelection(getViewBinding().editPasswordField.getText().toString().length());
            } else if (view == getViewBinding().forgotPassword) {
                showModalDialog(getActivity(), new ForgotPasswordFragmentHelper.Builder().build());
            } else if (view == getViewBinding().passwordButton) {
                getViewBinding().fingerprintLoginContainer.setVisibility(View.GONE);
                getViewBinding().passwordLoginContainer.setVisibility(View.VISIBLE);
            }
        }
    };
    private boolean mFingerPrintHasErrorred;
    //endregion

    //region lifecycle
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        createViewBinding(inflater, R.layout.fr_login_module, container);
        FingerprintManagerCompat fingerprintManager = FingerprintManagerCompat.from(getActivity());
        mFingerprintAuth = new FingerprintAuthDelegate.FingerprintAuthDelegateBuilder()
                .fingerprintManager(fingerprintManager)
                .callback(new FingerprintAuthDelegate.FingerprintCallbacks() {
                    @Override
                    public void onSuccess() {
                        if (mFingerPrintHasErrorred) {
                            mFingerPrintHasErrorred = false;
                        }
                        getViewModel().attemptLogin(true);
                    }

                    @Override
                    public void onError(final int errMsgId, final CharSequence errMsgString) {
                        mFingerPrintHasErrorred = true;
                        final ObjectAnimator shake = ObjectAnimator.ofFloat(
                                getViewBinding().fingerprintIcon,
                                "rotation",
                                -5f,
                                5f
                        );
                        shake.setRepeatMode(ValueAnimator.REVERSE);
                        shake.setRepeatCount(4);
                        shake.setDuration(200);
                        shake.start();

                        if (errMsgId != -1 && !EHITextUtils.isEmpty(errMsgString)) {
                            ToastUtils.showToast(getActivity(), errMsgString);
                        } else {
                            ToastUtils.showToast(getActivity(), R.string.profile_fingerprint_unlock_error_toast);
                        }
                    }
                })
                .build();
        initViews();
        return getViewBinding().getRoot();
    }
    //endregion

    private void initViews() {
        getViewBinding().cancelButton.setOnClickListener(mOnClickListener);
        getViewBinding().continueButton.setOnClickListener(mOnClickListener);
        getViewBinding().showPasswordContainer.setOnClickListener(mOnClickListener);
        getViewBinding().forgotPassword.setOnClickListener(mOnClickListener);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && getViewModel().shouldUseFingerprint()
                && mFingerprintAuth.isFingerprintAuthAvailable()) {
            getViewBinding().fingerprintLoginContainer.setVisibility(View.VISIBLE);
            getViewBinding().passwordLoginContainer.setVisibility(View.GONE);
            getViewBinding().passwordButton.setOnClickListener(mOnClickListener);
            KeyGenerator keyGenerator = FingerprintUtils.getKeyGenerator();
            KeyStore keyStore = FingerprintUtils.getKeyStore();
            Cipher cipher = FingerprintUtils.getCipher(keyStore);
            FingerprintUtils.createKey(keyStore, keyGenerator);

            if (FingerprintUtils.initCipher(keyStore, cipher)) {
                mFingerprintAuth.startListening(new FingerprintManagerCompat.CryptoObject(cipher));
            }
        } else {
            getViewBinding().fingerprintLoginContainer.setVisibility(View.GONE);
            getViewBinding().passwordLoginContainer.setVisibility(View.VISIBLE);
        }

    }

    @Override
    protected void initDependencies() {
        super.initDependencies();

        bind(ReactorTextView.bindText(getViewModel().passwordEditText.text(), getViewBinding().editPasswordField));
        bind(ReactorView.enabled(getViewModel().isValidPassword, getViewBinding().continueButton));
        bind(FragmentUtils.progress(getViewModel().progress, getActivity()));

        addReaction("LOGIN_SUCCESS_MODULE_REACTION", new ReactorComputationFunction() {
            @Override
            public void react(ReactorComputation reactorComputation) {
                if (getViewModel().isUserLoggedIn()) {
                    getActivity().setResult(Activity.RESULT_OK);
                    getActivity().finish();
                }
            }
        });

        addReaction("LOGIN_FAILED_MODULE_REACTION", new ReactorComputationFunction() {
            @Override
            public void react(ReactorComputation reactorComputation) {
                if (getViewModel().getError() != null) {
                    DialogUtils.showErrorDialog(getActivity(), getViewModel().getError());
                    getViewBinding().editPasswordField.setBackground(getResources().getDrawable(R.drawable.edit_text_red_border));
                    getViewModel().setErrorResponse(null);
                }
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        mFingerprintAuth.stopListening();
    }
}
