package com.ehi.enterprise.android.utils.fingerprint;

import android.annotation.TargetApi;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;
import android.support.v4.os.CancellationSignal;

@TargetApi(Build.VERSION_CODES.M)
public class FingerprintAuthDelegate extends FingerprintManagerCompat.AuthenticationCallback {
    private final FingerprintManagerCompat mFingerprintManager;
    private android.support.v4.os.CancellationSignal mCancellationSignal;
    private boolean mSelfCancelled;
    @Nullable private FingerprintCallbacks mCallbacks;

    private FingerprintAuthDelegate(final FingerprintManagerCompat fingerprintManager, @Nullable final FingerprintCallbacks callbacks) {
        super();
        mFingerprintManager = fingerprintManager;
        mCallbacks = callbacks;
    }

    @Override
    public void onAuthenticationError(final int errMsgId, final CharSequence errString) {
        super.onAuthenticationError(errMsgId, errString);
        if(mCallbacks != null) mCallbacks.onError(errMsgId, errString);
    }

    @Override
    public void onAuthenticationHelp(final int helpMsgId, final CharSequence helpString) {
        super.onAuthenticationHelp(helpMsgId, helpString);
    }

    @Override
    public void onAuthenticationSucceeded(final FingerprintManagerCompat.AuthenticationResult result) {
        super.onAuthenticationSucceeded(result);
        if(mCallbacks != null) mCallbacks.onSuccess();
    }

    @Override
    public void onAuthenticationFailed() {
        super.onAuthenticationFailed();
        if(mCallbacks != null) mCallbacks.onError(-1, null);
    }

    public void startListening(FingerprintManagerCompat.CryptoObject cryptoObject) {
        if (!isFingerprintAuthAvailable()) {
            return;
        }
        mCancellationSignal = new CancellationSignal();
        mSelfCancelled = false;
        mFingerprintManager.authenticate(cryptoObject, 0 /* flags */, mCancellationSignal, this, null);
    }

    public void stopListening() {
        if (mCancellationSignal != null) {
            mSelfCancelled = true;
            mCancellationSignal.cancel();
            mCancellationSignal = null;
            mCallbacks = null;
        }
    }


    public boolean isFingerprintAuthAvailable() {
        return mFingerprintManager.isHardwareDetected() && mFingerprintManager.hasEnrolledFingerprints();
    }

    public static class FingerprintAuthDelegateBuilder {
        private FingerprintManagerCompat mFingerprintManager;
        private FingerprintCallbacks mFingerprintCallbacks;

        public FingerprintAuthDelegateBuilder fingerprintManager(final FingerprintManagerCompat fingerprintManager) {
            mFingerprintManager = fingerprintManager;
            return this;
        }

        public FingerprintAuthDelegateBuilder callback(@NonNull final FingerprintCallbacks fingerprintCallbacks){
            mFingerprintCallbacks = fingerprintCallbacks;
            return this;
        }

        public FingerprintAuthDelegate build() {
            return new FingerprintAuthDelegate(mFingerprintManager, mFingerprintCallbacks);
        }
    }

    public interface FingerprintCallbacks {
        void onSuccess();
        void onError(final int errMsgId, @Nullable final CharSequence errString);
    }
}
