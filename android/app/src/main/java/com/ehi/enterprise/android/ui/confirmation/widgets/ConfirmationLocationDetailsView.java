package com.ehi.enterprise.android.ui.confirmation.widgets;

import android.content.Context;
import android.support.annotation.StringRes;
import android.util.AttributeSet;
import android.view.View;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.databinding.ConfirmationLocationDetailsViewBinding;
import com.ehi.enterprise.android.models.location.EHILocation;
import com.ehi.enterprise.android.ui.location.interfaces.OnLocationDetailEventsListener;
import com.ehi.enterprise.android.ui.view.DataBindingViewModelView;
import com.isobar.android.viewmodel.ViewModel;

import io.dwak.reactorbinding.view.ReactorView;
import io.dwak.reactorbinding.widget.ReactorImageView;
import io.dwak.reactorbinding.widget.ReactorTextView;

@ViewModel(ConfirmationLocationDetailsViewModel.class)
public class ConfirmationLocationDetailsView
        extends DataBindingViewModelView<ConfirmationLocationDetailsViewModel, ConfirmationLocationDetailsViewBinding> {

    private static final String TAG = "ConfirmationLocationDetailsView";

    private OnLocationDetailEventsListener mListener;

    private OnClickListener mOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            if (mListener == null) {
                return;
            }

            if (view == getViewBinding().phoneTextView) {
                mListener.onCallLocation(getViewModel().getLocation().getFormattedPhoneNumber(false));
            } else if (view == getViewBinding().nameTextView
                    || view == getViewBinding().iconImageView) {
                mListener.onShowLocationDetails(getViewModel().getLocation());
            }
        }
    };

    public ConfirmationLocationDetailsView(Context context) {
        this(context, null, 0);
    }

    public ConfirmationLocationDetailsView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ConfirmationLocationDetailsView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        if (isInEditMode()) {
            addView(inflate(context, R.layout.v_confirmation_location_details, null));
            return;
        }

        createViewBinding(R.layout.v_confirmation_location_details);
        initViews();
    }

    private void initViews() {
        getViewBinding().iconImageView.setOnClickListener(mOnClickListener);
        getViewBinding().nameTextView.setOnClickListener(mOnClickListener);
        getViewBinding().phoneTextView.setOnClickListener(mOnClickListener);
    }

    @Override
    protected void initDependencies() {
        super.initDependencies();
        bind(ReactorImageView.imageResource(getViewModel().iconImageView.imageResource(), getViewBinding().iconImageView));
        bind(ReactorView.visibility(getViewModel().iconImageView.visibility(), getViewBinding().iconImageView));
        bind(ReactorTextView.text(getViewModel().nameTextView.text(), getViewBinding().nameTextView));
        bind(ReactorTextView.text(getViewModel().phoneTextView.text(), getViewBinding().phoneTextView));
        bind(ReactorTextView.text(getViewModel().addressTextView.text(), getViewBinding().addressTextView));
    }

    public void setTitle(@StringRes int stringResId) {
        getViewBinding().titleTextView.setText(stringResId);
    }

    public void setOnLocationDetailEventsListener(OnLocationDetailEventsListener listener) {
        mListener = listener;
    }

    public void setLocation(EHILocation location) {
        getViewModel().setLocation(location);
    }
}
