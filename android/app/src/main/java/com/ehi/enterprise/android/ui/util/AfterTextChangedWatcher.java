package com.ehi.enterprise.android.ui.util;

import android.text.Editable;
import android.text.TextWatcher;

public class AfterTextChangedWatcher implements TextWatcher {
    private AfterTextChangedListener mListener;

    public interface AfterTextChangedListener {
        void afterTextChanged(String s);
    }

    public AfterTextChangedWatcher(AfterTextChangedListener listener) {
        mListener = listener;
    }



    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        mListener.afterTextChanged(s.toString());
    }
}
