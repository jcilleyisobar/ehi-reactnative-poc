package com.ehi.enterprise.android.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.TextView;

/**
 * Can fire onClick events while disabled.
 * Created by kedzie on 2/24/16.
 */
public class DisabledClickButton extends TextView {

    private OnClickListener mOnDisabledClickListener;

    public DisabledClickButton(Context context) {
        super(context);
    }

    public DisabledClickButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DisabledClickButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * Set the listener to be invoked when user clicks the disabled button
     *
     * @param disabledClickListener
     */
    public void setOnDisabledClickListener(OnClickListener disabledClickListener) {
        this.mOnDisabledClickListener = disabledClickListener;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        return super.dispatchTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isEnabled() && mOnDisabledClickListener != null) {
            final int action = event.getActionMasked();
            if (action == MotionEvent.ACTION_DOWN) {
                mOnDisabledClickListener.onClick(this);
                return true;
            }
        } else {
            return super.onTouchEvent(event);
        }
        return false;
    }
}
