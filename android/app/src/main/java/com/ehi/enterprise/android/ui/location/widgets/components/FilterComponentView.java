package com.ehi.enterprise.android.ui.location.widgets.components;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.content.res.ResourcesCompat;
import android.util.AttributeSet;
import android.view.View;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.databinding.FilterComponentViewBinding;
import com.ehi.enterprise.android.ui.view.DataBindingViewModelView;
import com.ehi.enterprise.android.ui.viewmodel.ManagersAccessViewModel;
import com.isobar.android.viewmodel.ViewModel;

import io.dwak.reactor.ReactorComputation;
import io.dwak.reactor.ReactorVar;
import io.dwak.reactor.interfaces.ReactorComputationFunction;
import io.dwak.reactorbinding.Preconditions;

@ViewModel(ManagersAccessViewModel.class)
public class FilterComponentView extends DataBindingViewModelView<ManagersAccessViewModel, FilterComponentViewBinding> {

    public FilterComponentView(Context context) {
        this(context, null);
    }

    public FilterComponentView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FilterComponentView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (!isInEditMode()) {
            createViewBinding(R.layout.v_filter_component);
        } else {
            addView(inflate(context, R.layout.v_filter_component, null));
        }
    }

    public void setResetOnClickListener(OnClickListener onClickListener) {
        getViewBinding().closeButton.setOnClickListener(onClickListener);
    }

    public void setOnFilterClickListener(OnClickListener onClickListener) {
        getViewBinding().textView.setOnClickListener(onClickListener);
    }

    public void setText(String text) {
        getViewBinding().textView.setText(text);
    }

    private void setTitle(String text) {
        if (getViewBinding().textView != null) {
            getViewBinding().textView.setText(text);
        }
    }

    public void setResetButtonVisibility(Integer visibility) {
        if (getViewBinding().closeButton != null) {
            getViewBinding().closeButton.setVisibility(visibility);
        }
    }

    public void setDefaultState(Boolean defaultState) {
        if (getViewBinding().textView != null && getViewBinding().closeButton != null) {
            if (defaultState) {
                Typeface boldTypeface = ResourcesCompat.getFont(getContext(), R.font.source_sans_bold);
                getViewBinding().textView.setTypeface(boldTypeface);
                getViewBinding().closeButton.setVisibility(View.GONE);
            } else {
                Typeface lightTypeface = ResourcesCompat.getFont(getContext(), R.font.source_sans_light);
                getViewBinding().textView.setTypeface(lightTypeface);
                getViewBinding().closeButton.setVisibility(View.VISIBLE);
            }
        }
    }

    public static ReactorComputationFunction title(final ReactorVar<? extends String> source, final FilterComponentView target) {
        return new ReactorComputationFunction() {
            @Override
            public void react(ReactorComputation reactorComputation) {
                if (Preconditions.checkNotNull(source.getValue(), target)) {
                    target.setTitle(source.getValue());
                }
            }
        };
    }

    public static ReactorComputationFunction resetButtonVisibility(final ReactorVar<? extends Integer> source, final FilterComponentView target) {
        return new ReactorComputationFunction() {
            @Override
            public void react(ReactorComputation reactorComputation) {
                if (Preconditions.checkNotNull(source.getValue(), target)) {
                    target.setResetButtonVisibility(source.getValue());
                }
            }
        };
    }

    public static ReactorComputationFunction resetDefaultState(final ReactorVar<? extends Boolean> source, final FilterComponentView target) {
        return new ReactorComputationFunction() {
            @Override
            public void react(ReactorComputation reactorComputation) {
                if (Preconditions.checkNotNull(source.getValue(), target)) {
                    target.setDefaultState(source.getValue());
                }
            }
        };
    }

    public static ReactorComputationFunction[] bind(final FilterComponentViewState source, final FilterComponentView target) {
        ReactorComputationFunction[] functions = new ReactorComputationFunction[7];

        functions[0] = title(source.title(), target);
        functions[1] = resetButtonVisibility(source.resetButtonVisibility(), target);
        functions[2] = resetDefaultState(source.resetDefaultState(), target);

        return functions;
    }
}
