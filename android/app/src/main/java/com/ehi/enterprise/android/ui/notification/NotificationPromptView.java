package com.ehi.enterprise.android.ui.notification;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.databinding.NotificationPromptFragmentBinding;
import com.ehi.enterprise.android.ui.view.DataBindingViewModelView;
import com.isobar.android.viewmodel.ViewModel;

@ViewModel(NotificationPromptViewModel.class)
public class NotificationPromptView extends DataBindingViewModelView<NotificationPromptViewModel, NotificationPromptFragmentBinding> {

    public static final String SCREEN_NAME = "NotificationPromptFragment";

    @Nullable
    private NotificationPromptListener mListener;

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(final View v) {
            if (v == getViewBinding().confirmButton) {
                if (mListener != null) {
                    mListener.onConfirmClicked();
                }
            } else if (v == getViewBinding().denyButton || v == getViewBinding().closeButton) {
                if (mListener != null) {
                    mListener.onDenyClicked();
                }
            }
        }
    };

    //region constructors
    public NotificationPromptView(final Context context) {
        this(context, null);
    }

    public NotificationPromptView(final Context context, final AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NotificationPromptView(final Context context, final AttributeSet attrs, final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        createViewBinding(R.layout.fr_notification_prompt);
        initViews();
    }
    //endregion


    private void initViews() {
        getViewBinding().confirmButton.setOnClickListener(mOnClickListener);
        getViewBinding().denyButton.setOnClickListener(mOnClickListener);
        getViewBinding().closeButton.setOnClickListener(mOnClickListener);

    }

    public void setListener(@Nullable final NotificationPromptListener listener) {
        mListener = listener;
    }

    public interface NotificationPromptListener {
        void onConfirmClicked();

        void onDenyClicked();
    }

}