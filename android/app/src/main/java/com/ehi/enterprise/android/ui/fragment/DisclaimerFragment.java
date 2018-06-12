package com.ehi.enterprise.android.ui.fragment;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.ehi.enterprise.android.R;
import com.isobar.android.newinstancer.Extra;

public class DisclaimerFragment extends BaseFragment {

    @Extra(String.class)
    public static final String KEY_TITLE = "KEY_TITLE";
    @Extra(String.class)
    public static final String KEY_BODY = "KEY_BODY";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        DisclaimerFragmentHelper.Extractor extractor = new DisclaimerFragmentHelper.Extractor(this);

        getActivity().setTitle(extractor.keyTitle());
        View view = DataBindingUtil.inflate(inflater, R.layout.fr_generic_disclaimer, container, false).getRoot();
        WebView disclaimerWebView = (WebView) view.findViewById(R.id.text_holder);
        disclaimerWebView.loadData(extractor.keyBody(), "text/html; charset=UTF-8", null);

        return view;
    }

}