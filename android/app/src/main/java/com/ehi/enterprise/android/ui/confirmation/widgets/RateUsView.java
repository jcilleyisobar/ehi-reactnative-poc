package com.ehi.enterprise.android.ui.confirmation.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.databinding.RateUsViewBinding;
import com.ehi.enterprise.android.ui.view.DataBindingViewModelView;
import com.ehi.enterprise.android.ui.viewmodel.ManagersAccessViewModel;
import com.isobar.android.viewmodel.ViewModel;

@ViewModel(ManagersAccessViewModel.class)
public class RateUsView extends DataBindingViewModelView<ManagersAccessViewModel, RateUsViewBinding> {

    private OnRateUsClickListener listener;

    private OnClickListener mOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view == getViewBinding().rateUsButton) {
                if (listener != null) {
                    listener.onRateUsClicked();
                }
            }
            if (view == getViewBinding().dismissButton) {
                if (listener != null) {
                    listener.onDismissClicked();
                }
            }
        }
    };

    public RateUsView(Context context) {
        this(context, null);
    }

    public RateUsView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RateUsView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        if (isInEditMode()) {
            addView(inflate(context, R.layout.v_rate_us, null));
            return;
        }

        createViewBinding(R.layout.v_rate_us);
        initViews();
    }

    private void initViews() {
        getViewBinding().rateUsButton.setOnClickListener(mOnClickListener);
        getViewBinding().dismissButton.setOnClickListener(mOnClickListener);
    }

    public void setOnRateUsClickListener(OnRateUsClickListener listener) {
        this.listener = listener;
    }

    public interface OnRateUsClickListener {
        void onRateUsClicked();

        void onDismissClicked();
    }
}
