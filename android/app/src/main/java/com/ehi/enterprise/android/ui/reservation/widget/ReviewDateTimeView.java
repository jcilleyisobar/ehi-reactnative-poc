package com.ehi.enterprise.android.ui.reservation.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.text.format.DateUtils;
import android.util.AttributeSet;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.databinding.ReviewDateTimeViewBinding;
import com.ehi.enterprise.android.ui.view.DataBindingViewModelView;
import com.isobar.android.viewmodel.ViewModel;

import java.util.Date;

import io.dwak.reactorbinding.view.ReactorView;
import io.dwak.reactorbinding.widget.ReactorTextView;

@ViewModel(ReviewDateTimeViewModel.class)
public class ReviewDateTimeView extends DataBindingViewModelView<ReviewDateTimeViewModel, ReviewDateTimeViewBinding> {

    //region constructors
    public ReviewDateTimeView(Context context) {
        this(context, null, 0);
    }

    public ReviewDateTimeView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ReviewDateTimeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        if (isInEditMode()) {
            addView(inflate(context, R.layout.v_pickup_return_cell, null));
            return;
        }

        createViewBinding(R.layout.v_pickup_return_cell);
        populateCellFromAttributes(context, attrs);
    }
    //endregion

    @Override
    protected void initDependencies() {
        super.initDependencies();
        bind(ReactorTextView.text(getViewModel().titleText.text(), getViewBinding().cellTitle));
        bind(ReactorTextView.text(getViewModel().labelText.text(), getViewBinding().cellText));
        bind(ReactorView.visibility(getViewModel().greenArrowView.visibility(), getViewBinding().greenArrow));
    }

    private void populateCellFromAttributes(Context context, AttributeSet attrs) {
        if (attrs == null) {
            return;
        }
        TypedArray array = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.PickupReturnCell,
                0, 0);

        try {
            getViewModel().setTitleText(array.getString(R.styleable.PickupReturnCell_pickupReturnCellTitle));
            getViewModel().setLabelText(array.getString(R.styleable.PickupReturnCell_pickupReturnCellText));
        } finally {
            array.recycle();
        }
    }

    public void hideGreenArrow() {
        getViewModel().hideGreenArrow();
    }

    public void showGreenArrow() {
        getViewModel().showGreenArrow();
    }

    public void setTitle(@NonNull String title) {
        getViewModel().setTitleText(title);
    }

    public void setText(@NonNull String text) {
        getViewModel().setLabelText(text);
    }

    public void setTime(Date time) {
        String dateString = DateUtils.formatDateTime(getContext(), time.getTime(), DateUtils.FORMAT_SHOW_DATE |
                DateUtils.FORMAT_SHOW_WEEKDAY |
                DateUtils.FORMAT_SHOW_TIME |
                DateUtils.FORMAT_ABBREV_MONTH |
                DateUtils.FORMAT_ABBREV_WEEKDAY);
        setText(dateString);
    }
}