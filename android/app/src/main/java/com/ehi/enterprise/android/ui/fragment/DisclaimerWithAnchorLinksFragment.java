package com.ehi.enterprise.android.ui.fragment;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.ehi.enterprise.android.R;
import com.isobar.android.newinstancer.Extra;

/*
 * Created as a workaround to make anchor links work after chromium update that blocks redirection.
 */
public class DisclaimerWithAnchorLinksFragment extends BaseFragment {

    @Extra(String.class)
    public static final String KEY_TITLE = "KEY_TITLE";
    @Extra(String.class)
    public static final String KEY_BODY = "KEY_BODY";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final DisclaimerWithAnchorLinksFragmentHelper.Extractor extractor = new DisclaimerWithAnchorLinksFragmentHelper.Extractor(this);

        getActivity().setTitle(extractor.keyTitle());
        View view = DataBindingUtil.inflate(inflater, R.layout.fr_generic_disclaimer, container, false).getRoot();
        final WebView disclaimerWebView = view.findViewById(R.id.text_holder);
        final WebSettings webViewSettings = disclaimerWebView.getSettings();

        //Setting up webview to be adjusted by text width
        webViewSettings.setUseWideViewPort(true);
        webViewSettings.setLoadWithOverviewMode(true);
        webViewSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING);

        webViewSettings.setJavaScriptEnabled(true);

        disclaimerWebView.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {
                WebView.HitTestResult hr = ((WebView) v).getHitTestResult();

                if (event.getAction() == MotionEvent.ACTION_UP && hr.getType() == WebView.HitTestResult.SRC_ANCHOR_TYPE) {
                    final String id = hr.getExtra().substring(hr.getExtra().indexOf("#") + 1);
                    // the link can be referred by the id or name field
                    disclaimerWebView.loadUrl("javascript:(document.getElementById(\"" + id + "\") || document.getElementsByName(\"" + id + "\")[0]).scrollIntoView()");
                }
                return false;
            }
        });

        disclaimerWebView.loadData(extractor.keyBody(), "text/html; charset=UTF-8", null);

        return view;
    }

}