package com.ehi.enterprise.android.ui.reservation.history;

import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.view.View;

public class MyRentalsFooter {
    @DrawableRes
    public final int icon;
    @StringRes
    public final int footerPrompt;
    public final View.OnClickListener onClickListener;
    @StringRes
    public final int buttonText;

    public MyRentalsFooter(@StringRes int footerPrompt, @DrawableRes int icon, @StringRes int buttonText, View.OnClickListener onClickListener) {
        this.buttonText = buttonText;
        this.icon = icon;
        this.footerPrompt = footerPrompt;
        this.onClickListener = onClickListener;
    }
}
