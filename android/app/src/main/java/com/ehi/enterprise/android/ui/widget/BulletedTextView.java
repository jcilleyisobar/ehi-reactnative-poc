package com.ehi.enterprise.android.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.databinding.DataBindingUtil;
import android.support.annotation.ColorInt;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.databinding.BulletedTextViewBinding;

public class BulletedTextView extends FrameLayout {
    private BulletedTextViewBinding mBinding;
    private String mText;

    @ColorInt
    private int mTextColor;

    @ColorInt
    private int mBulletColor;

    private float mTextSize;

    public BulletedTextView(final Context context) {
        this(context, null);
    }

    public BulletedTextView(final Context context, final AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BulletedTextView(final Context context, final AttributeSet attrs, final int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        if (isInEditMode()) {
            addView(inflate(context, R.layout.v_bulleted_text_view, null));
            return;
        }

        mBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.v_bulleted_text_view, this, true);
        loadTextFromAttributes(context, attrs);
        setBulletedText(mText);
        setBulletedTextColor(mTextColor);
        setBulletColor(mBulletColor);
        setBulletedTextSize(mTextSize);
    }

    private void loadTextFromAttributes(final Context context, final AttributeSet attrs) {
        TypedArray array = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.BulletedTextView,
                0, 0);
        try {
            mText = array.getString(R.styleable.BulletedTextView_bulletedText);
            mTextColor = array.getColor(R.styleable.BulletedTextView_bulletedTextColor, ContextCompat.getColor(context, R.color.ehi_black));
            mBulletColor = array.getColor(R.styleable.BulletedTextView_bulletColor, ContextCompat.getColor(context, R.color.ehi_black));
            mTextSize = array.getDimension(R.styleable.BulletedTextView_bulletTextSize, 12.0f);
        } finally {
            array.recycle();
        }
    }

    public void setBulletedText(String text) {
        mText = text;
        mBinding.text.setText(mText);
    }

    public void setBulletedTextColor(@ColorInt int textColor) {
        mTextColor = textColor;
        mBinding.text.setTextColor(mTextColor);
    }

    public void setBulletColor(@ColorInt final int bulletColor) {
        mBulletColor = bulletColor;
        mBinding.bullet.setTextColor(mBulletColor);
    }

    public void setBulletedTextSize(float textSize) {
        mTextSize = textSize;
        mBinding.text.setTextSize(textSize);
    }

    public TextView getTextView() {
        return mBinding.text;
    }
}
