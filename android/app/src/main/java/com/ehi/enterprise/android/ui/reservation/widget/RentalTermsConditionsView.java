package com.ehi.enterprise.android.ui.reservation.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.ColorRes;
import android.util.AttributeSet;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.databinding.RentalTermsConditionsViewBinding;
import com.ehi.enterprise.android.ui.view.DataBindingViewModelView;
import com.ehi.enterprise.android.ui.viewmodel.ManagersAccessViewModel;
import com.isobar.android.viewmodel.ViewModel;

@ViewModel(ManagersAccessViewModel.class)
public class RentalTermsConditionsView extends DataBindingViewModelView<ManagersAccessViewModel, RentalTermsConditionsViewBinding> {
    private String mText;

    public RentalTermsConditionsView(Context context) {
        this(context, true);
    }

    public RentalTermsConditionsView(final Context context, AttributeSet attrs) {
        super(context, attrs);
        if (isInEditMode()) {
            addView(inflate(context, R.layout.v_rental_terms_conditions, null));
        } else {
            createView(true);
            loadTextFromAttributes(context, attrs);
            if (mText != null) {
                getViewBinding().termsAndConditionTextView.setText(mText);
            }
        }
    }

    public RentalTermsConditionsView(Context context, boolean attachToParent) {
        super(context);
        createView(attachToParent);
    }

    private void createView(boolean attachToParent) {
        createViewBinding(R.layout.v_rental_terms_conditions, attachToParent);
    }

    public void setBackgroundColor(@ColorRes int color) {
        getViewBinding().termsAndConditionsContainer.setBackgroundColor(color);
    }

    public void setAllCaps(boolean allCaps) {
        getViewBinding().termsAndConditionTextView.setAllCaps(allCaps);
    }

    private void loadTextFromAttributes(final Context context, final AttributeSet attributeSet) {
        final TypedArray array = context.getTheme().obtainStyledAttributes(
                attributeSet,
                R.styleable.RentalTermsConditionsView,
                0,
                0
        );

        try {
            mText = array.getString(R.styleable.RentalTermsConditionsView_text);
        } finally {
            array.recycle();
        }
    }


}