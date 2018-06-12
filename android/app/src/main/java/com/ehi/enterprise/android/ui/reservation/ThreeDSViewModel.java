package com.ehi.enterprise.android.ui.reservation;

import com.ehi.enterprise.android.ui.reservation.widget.ThreeDSView;
import com.ehi.enterprise.android.ui.reservation.widget.ThreeDSViewAuthorizationListener;
import com.ehi.enterprise.android.ui.viewmodel.ManagersAccessViewModel;

import io.dwak.reactor.ReactorVar;
import io.dwak.reactor.unbinder.annotation.AutoUnbindAll;

@AutoUnbindAll
public class ThreeDSViewModel extends ManagersAccessViewModel implements ThreeDSViewAuthorizationListener {

    final ReactorVar<String> mAcsUrl = new ReactorVar<>();
    final ReactorVar<String> mPaReq = new ReactorVar<>();
    final ReactorVar<String> mPostbackUrl = new ReactorVar<>();

    final ReactorVar<String> mPaRes = new ReactorVar<>();

    @Override
    public void onAttachToView() {
        super.onAttachToView();
    }

    public void setAcsUrl(final String acsUrl) {
        mAcsUrl.setValue(acsUrl);
    }

    public void setPaReq(final String paReq) {
        mPaReq.setValue(paReq);
    }

    public void setPostbackUrl(final String postbackUrl) {
        mPostbackUrl.setValue(postbackUrl);
    }

    public String getAcsUrl() {
        return mAcsUrl.getValue();
    }

    public String getPaReq() {
        return mPaReq.getValue();
    }

    public String getPostbackUrl() {
        return mPostbackUrl.getValue();
    }

    public String getPaRes() {
        return mPaRes.getValue();
    }

    @Override
    public void onAuthorizationCompleted(String md, String paRes) {
        mPaRes.setValue(paRes);
    }

    @Override
    public void onAuthorizationStarted(ThreeDSView view) {

    }

    @Override
    public void onAuthorizationWebPageLoadingProgressChanged(int progress) {
        showProgress(progress > 0 && progress < 100);
    }

    @Override
    public void onAuthorizationWebPageLoadingError(int errorCode, String description, String failingUrl) {
        mPaRes.setValue(null);
    }

    @Override
    public void formParsingCallback() {
        // do nothing
    }
}
