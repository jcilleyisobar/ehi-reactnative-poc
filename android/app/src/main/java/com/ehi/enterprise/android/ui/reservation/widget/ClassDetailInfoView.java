package com.ehi.enterprise.android.ui.reservation.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.util.AttributeSet;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.databinding.ClassDetailsCellViewBinding;
import com.ehi.enterprise.android.ui.view.DataBindingViewModelView;
import com.ehi.enterprise.android.ui.viewmodel.ManagersAccessViewModel;
import com.isobar.android.viewmodel.ViewModel;

import io.dwak.reactor.ReactorComputation;
import io.dwak.reactor.ReactorVar;
import io.dwak.reactor.interfaces.ReactorComputationFunction;

@ViewModel(ManagersAccessViewModel.class)
public class ClassDetailInfoView extends DataBindingViewModelView<ManagersAccessViewModel, ClassDetailsCellViewBinding> {

    private String mTitle;
    private String mLabelText;
    private Drawable mIcon;

    public ClassDetailInfoView(Context context) {
        this(context, null, 0);
    }

    public ClassDetailInfoView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ClassDetailInfoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (!isInEditMode()) {
            populateCellFromAttributes(context, attrs);
        }
        createViewBinding(R.layout.v_class_detail_cell);
        initViews();

    }

    private void initViews() {
        getViewBinding().cellTitle.setText(mTitle);
        getViewBinding().cellIcon.setImageDrawable(mIcon);
        getViewBinding().cellText.setText(mLabelText);
    }

    private void populateCellFromAttributes(Context context, AttributeSet attrs) {
        TypedArray array = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.ClassSelectDetailCell,
                0, 0);

        try {
            mTitle = array.getString(R.styleable.ClassSelectDetailCell_cellTitle);
            mLabelText = array.getString(R.styleable.ClassSelectDetailCell_cellText);
            mIcon = array.getDrawable(R.styleable.ClassSelectDetailCell_cellIcon);
        } finally {
            array.recycle();
        }
    }

    public void setTitle(@NonNull String title) {
        mTitle = title;
        getViewBinding().cellTitle.setText(mTitle);
    }


    public void setText(@NonNull String text) {
        mLabelText = text;
        getViewBinding().cellText.setText(mLabelText);
    }

    public void setIcon(@NonNull Drawable drawable) {
        mIcon = drawable;
        getViewBinding().cellIcon.setImageDrawable(mIcon);
    }

    public static ReactorComputationFunction text(final ReactorVar<String> source, final ClassDetailInfoView target) {
        return new ReactorComputationFunction() {
            @Override
            public void react(final ReactorComputation reactorComputation) {
                target.setText(source.getValue());
            }
        };
    }
}
