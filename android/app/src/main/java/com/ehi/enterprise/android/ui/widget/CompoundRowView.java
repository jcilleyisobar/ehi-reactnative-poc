package com.ehi.enterprise.android.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.IdRes;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Checkable;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.ehi.enterprise.android.R;

import java.util.ArrayList;

public class CompoundRowView extends FrameLayout implements Checkable {

    private CompoundButton mCompoundButton;
    private TextView mTextView;

    private OnClickListener mExternalClickListener = null;

    private OnClickListener mOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            if (mCompoundButton == null) {
                return;
            }
            if ((mOnFalse && !mCompoundButton.isChecked()) || (mOnTrue && mCompoundButton.isChecked())) {
                return;
            }
            mCompoundButton.setChecked(!mCompoundButton.isChecked());
            if (mExternalClickListener != null) {
                mExternalClickListener.onClick(view);
            }
        }
    };
    private int mCheckId;
    private int mTextId;
    private boolean mInternalCall = false;
    private Runnable mRunnable = null;
    private ArrayList<Runnable> mMessageQueue = new ArrayList<>();
    private boolean mInitialized = false;
    private boolean mOnFalse;
    private boolean mOnTrue;

    public CompoundRowView(Context context) {
        super(context);
    }

    public CompoundRowView(Context context, AttributeSet attrs) {
        super(context, attrs);
        extractResources(attrs, context);
    }

    public CompoundRowView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        extractResources(attrs, context);
    }

    private void extractResources(AttributeSet attrs, Context context) {
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.CompoundRowView, 0, 0);

        try {
            mTextId = array.getResourceId(R.styleable.CompoundRowView_textViewId, 0);
            mCheckId = array.getResourceId(R.styleable.CompoundRowView_compoundViewId, 0);
        } finally {
            array.recycle();
        }

    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        initViews();
    }

    public void postAfterInitialize(Runnable runnable) {
        if (mInitialized) {
            post(runnable);
            return;
        }
        mRunnable = runnable;
    }

    private void initViews() {
        if (mCheckId != 0) {
            setCompoundButton(mCheckId, null);
        }
        if (mTextId != 0) {
            setTextView(mTextId, null);
        }
        mInternalCall = true;
        setOnClickListener(mOnClickListener);
        mInternalCall = false;

        mInitialized = true;
        post(mRunnable);

        for (Runnable r : mMessageQueue) {
            r.run();
        }
    }

    @Override
    public boolean isChecked() {
        return mCompoundButton != null && mCompoundButton.isChecked();
    }

    @Override
    public void toggle() {
        setChecked(!isChecked());
    }

    public void setCompoundButton(@IdRes int id, Boolean checked) {
        mCompoundButton = (CompoundButton) findViewById(id);
        mCompoundButton.setClickable(false);
        if (checked != null) {
            mCompoundButton.setChecked(checked);
        }
    }

    private void addToMessageQueue(Runnable runnable) {
        if (mInitialized) {
            runnable.run();
        } else {
            mMessageQueue.add(runnable);
        }
    }


    public void setTextView(@IdRes int id, String text) {
        mTextView = (TextView) findViewById(id);
        if (text != null) {
            mTextView.setText(text);
        }
    }

    @Override
    public void setOnClickListener(OnClickListener l) {
        if (mInternalCall) {
            super.setOnClickListener(l);
        } else {
            mExternalClickListener = l;
        }
    }


    /**
     * Null safe operation
     *
     * @param checked
     */
    @Override
    public void setChecked(final boolean checked) {
        if (mInitialized) {
            if (mCompoundButton == null) {
                return;
            }
            mCompoundButton.setChecked(checked);
        } else {
            addToMessageQueue(new Runnable() {
                @Override
                public void run() {
                    if (mCompoundButton != null) {
                        mCompoundButton.setChecked(checked);
                    }
                }
            });
        }
    }

    public void setDisabledOn(boolean onTrue, boolean onFalse) {
        mOnTrue = onTrue;
        mOnFalse = onFalse;
    }

    public void setDisableOff() {
        mOnTrue = false;
        mOnFalse = false;
    }

    /**
     * Null safe operation
     *
     * @param text
     */
    public void setText(final String text) {
        if (mInitialized) {
            if (mTextView == null) {
                return;
            }
            mTextView.setText(text);
        } else {
            addToMessageQueue(new Runnable() {
                @Override
                public void run() {
                    if (mTextView != null) {
                        mTextView.setText(text);
                    }
                }
            });
        }
    }

    /**
     * Will be null if called before {@link #onAttachedToWindow()}
     *
     * @return
     */
    public CompoundButton getCompoundButton() {
        return mCompoundButton;
    }

    /**
     * Will be null if called before {@link #onAttachedToWindow()}
     *
     * @return
     */
    public TextView getTextView() {
        return mTextView;
    }

}
