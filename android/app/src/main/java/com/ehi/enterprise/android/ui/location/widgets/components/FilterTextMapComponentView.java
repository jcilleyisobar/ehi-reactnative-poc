package com.ehi.enterprise.android.ui.location.widgets.components;

import android.content.Context;
import android.util.AttributeSet;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.databinding.FilterTextMapViewBinding;
import com.ehi.enterprise.android.ui.view.DataBindingViewModelView;
import com.ehi.enterprise.android.ui.viewmodel.ManagersAccessViewModel;
import com.isobar.android.viewmodel.ViewModel;

import io.dwak.reactor.ReactorComputation;
import io.dwak.reactor.ReactorVar;
import io.dwak.reactor.interfaces.ReactorComputationFunction;
import io.dwak.reactorbinding.Preconditions;

@ViewModel(ManagersAccessViewModel.class)
public class FilterTextMapComponentView extends DataBindingViewModelView<ManagersAccessViewModel, FilterTextMapViewBinding> {

    public FilterTextMapComponentView(Context context) {
        this(context, null);
    }

    public FilterTextMapComponentView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FilterTextMapComponentView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (!isInEditMode()) {
            createViewBinding(R.layout.v_filters_text_map_component);
        } else {
            addView(inflate(context, R.layout.v_filters_text_map_component, null));
        }
    }

    public void setFiltersText(String text) {
        if (getViewBinding() != null) {
            getViewBinding().filtersTextView.setText(text);
        }
    }

    public static ReactorComputationFunction title(final ReactorVar<? extends String> source, final FilterTextMapComponentView target) {
        return new ReactorComputationFunction() {
            @Override
            public void react(ReactorComputation reactorComputation) {
                if (Preconditions.checkNotNull(source.getValue(), target)) {
                    target.setFiltersText(source.getValue());
                }
            }
        };
    }

    public static ReactorComputationFunction[] bind(final FilterTextMapComponentViewState source, final FilterTextMapComponentView target) {
        ReactorComputationFunction[] functions = new ReactorComputationFunction[7];
        functions[0] = title(source.title(), target);
        return functions;
    }
}
