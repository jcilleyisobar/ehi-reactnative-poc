package com.ehi.enterprise.android.ui.location.widgets.components;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.databinding.LocationFilterReadOnlyComponentViewBinding;
import com.ehi.enterprise.android.ui.view.DataBindingViewModelView;
import com.ehi.enterprise.android.ui.viewmodel.ManagersAccessViewModel;
import com.isobar.android.viewmodel.ViewModel;

import io.dwak.reactor.ReactorComputation;
import io.dwak.reactor.ReactorVar;
import io.dwak.reactor.interfaces.ReactorComputationFunction;
import io.dwak.reactorbinding.Preconditions;

@ViewModel(ManagersAccessViewModel.class)
public class FilterMapComponentView extends DataBindingViewModelView<ManagersAccessViewModel, LocationFilterReadOnlyComponentViewBinding> {

    private CharSequence mPickupTitle;
    private CharSequence mReturnTitle;

    private OnClickListener mClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mFilterListener != null) {
                if (getViewBinding().pickupDateTextView == v) {
                    mFilterListener.onPickupDateClick();
                } else if (getViewBinding().pickupTimeTextView == v) {
                    mFilterListener.onPickupTimeClick();
                } else if (getViewBinding().dropoffDateTextView == v) {
                    mFilterListener.onReturnDateClick();
                } else if (getViewBinding().dropoffTimeTextView == v) {
                    mFilterListener.onReturnTimeClick();
                }
            }
        }
    };

    private FilterMapViewListener mFilterListener;

    public FilterMapComponentView(Context context) {
        this(context, null);
    }

    public FilterMapComponentView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FilterMapComponentView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        createViewBinding(R.layout.v_location_filter_read_only_component);
        loadTextFromAttributes(context, attrs);
        getViewBinding().pickupTitleTextView.setText(mPickupTitle);
        getViewBinding().dropoffTitleTextView.setText(mReturnTitle);
        initViews();
    }

    public void initViews() {
        getViewBinding().pickupDateTextView.setOnClickListener(mClickListener);
        getViewBinding().pickupTimeTextView.setOnClickListener(mClickListener);
        getViewBinding().dropoffDateTextView.setOnClickListener(mClickListener);
        getViewBinding().dropoffTimeTextView.setOnClickListener(mClickListener);
    }

    public void setFilterViewClickListener(FilterMapViewListener listener) {
        mFilterListener = listener;
    }

    private void loadTextFromAttributes(Context context, AttributeSet attrs) {
        TypedArray array = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.LocationFilterMapView,
                0, 0);
        try {
            mPickupTitle = array.getString(R.styleable.LocationFilterMapView_pickupTitle);
            mReturnTitle = array.getString(R.styleable.LocationFilterMapView_returnTitle);
        } finally {
            array.recycle();
        }
    }

    public void setPickupDateText(String text) {
        if (getViewBinding().pickupDateTextView != null) {
            getViewBinding().pickupDateTextView.setText(text);
        }
    }

    public void setPickupTimeText(String text) {
        if (getViewBinding().pickupTimeTextView != null) {
            getViewBinding().pickupTimeTextView.setText(text);
        }
    }

    public void setReturnDateText(String text) {
        if (getViewBinding().dropoffDateTextView != null) {
            getViewBinding().dropoffDateTextView.setText(text);
        }
    }

    public void setReturnTimeText(String text) {
        if (getViewBinding().dropoffTimeTextView != null) {
            getViewBinding().dropoffTimeTextView.setText(text);
        }
    }

    public void setPickupDefaultState(Boolean defaultState) {
        if (getViewBinding().pickupTimeTextView != null && getViewBinding().pickupSeparatorView!= null) {
            if (defaultState) {
                getViewBinding().pickupTimeTextView.setVisibility(View.INVISIBLE);
                getViewBinding().pickupSeparatorView.setVisibility(View.INVISIBLE);
            } else {
                getViewBinding().pickupTimeTextView.setVisibility(View.VISIBLE);
                getViewBinding().pickupSeparatorView.setVisibility(View.VISIBLE);
            }
        }
    }

    public void setDropoffDefaultState(Boolean defaultState) {
        if (getViewBinding().dropoffTimeTextView != null && getViewBinding().dropoffSeparatorView!= null) {
            if (defaultState) {
                getViewBinding().dropoffTimeTextView.setVisibility(View.INVISIBLE);
                getViewBinding().dropoffSeparatorView.setVisibility(View.INVISIBLE);
            } else {
                getViewBinding().dropoffTimeTextView.setVisibility(View.VISIBLE);
                getViewBinding().dropoffSeparatorView.setVisibility(View.VISIBLE);
            }
        }
    }

    public void setPickupDateClickListener(OnClickListener onClickListener) {
        getViewBinding().pickupDateTextView.setOnClickListener(onClickListener);
    }

    public void setPickupTimeClickListener(OnClickListener onClickListener) {
        getViewBinding().pickupTimeTextView.setOnClickListener(onClickListener);
    }

    public void setReturnDateClickListener(OnClickListener onClickListener) {
        getViewBinding().dropoffDateTextView.setOnClickListener(onClickListener);
    }

    public void setReturnTimeClickListener(OnClickListener onClickListener) {
        getViewBinding().dropoffTimeTextView.setOnClickListener(onClickListener);
    }

    public static ReactorComputationFunction pickupDateTitle(final ReactorVar<? extends String> source, final FilterMapComponentView target) {
        return new ReactorComputationFunction() {
            @Override
            public void react(ReactorComputation reactorComputation) {
                if (Preconditions.checkNotNull(source.getValue(), target)) {
                    target.setPickupDateText(source.getValue());
                }
            }
        };
    }

    public static ReactorComputationFunction pickupTimeTitle(final ReactorVar<? extends String> source, final FilterMapComponentView target) {
        return new ReactorComputationFunction() {
            @Override
            public void react(ReactorComputation reactorComputation) {
                if (Preconditions.checkNotNull(source.getValue(), target)) {
                    target.setPickupTimeText(source.getValue());
                }
            }
        };
    }

    public static ReactorComputationFunction dropoffDateTitle(final ReactorVar<? extends String> source, final FilterMapComponentView target) {
        return new ReactorComputationFunction() {
            @Override
            public void react(ReactorComputation reactorComputation) {
                if (Preconditions.checkNotNull(source.getValue(), target)) {
                    target.setReturnDateText(source.getValue());
                }
            }
        };
    }

    public static ReactorComputationFunction dropoffTimeTitle(final ReactorVar<? extends String> source, final FilterMapComponentView target) {
        return new ReactorComputationFunction() {
            @Override
            public void react(ReactorComputation reactorComputation) {
                if (Preconditions.checkNotNull(source.getValue(), target)) {
                    target.setReturnTimeText(source.getValue());
                }
            }
        };
    }

    public static ReactorComputationFunction resetPickupDefaultState(final ReactorVar<? extends Boolean> source, final FilterMapComponentView target) {
        return new ReactorComputationFunction() {
            @Override
            public void react(ReactorComputation reactorComputation) {
                if (Preconditions.checkNotNull(source.getValue(), target)) {
                    target.setPickupDefaultState(source.getValue());
                }
            }
        };
    }

    public static ReactorComputationFunction resetDropoffDefaultState(final ReactorVar<? extends Boolean> source, final FilterMapComponentView target) {
        return new ReactorComputationFunction() {
            @Override
            public void react(ReactorComputation reactorComputation) {
                if (Preconditions.checkNotNull(source.getValue(), target)) {
                    target.setDropoffDefaultState(source.getValue());
                }
            }
        };
    }

    public static ReactorComputationFunction[] bind(final FilterMapComponentViewState source, final FilterMapComponentView target) {
        ReactorComputationFunction[] functions = new ReactorComputationFunction[7];

        functions[0] = pickupDateTitle(source.pickupDateTitle(), target);
        functions[1] = pickupTimeTitle(source.pickupTimeTitle(), target);
        functions[2] = dropoffDateTitle(source.dropoffDateTitle(), target);
        functions[3] = dropoffTimeTitle(source.dropoffTimeTitle(), target);
        functions[4] = resetPickupDefaultState(source.pickupDefaultState(), target);
        functions[5] = resetDropoffDefaultState(source.dropoffDefaultState(), target);

        return functions;
    }

    public interface FilterMapViewListener {
        void onPickupDateClick();
        void onPickupTimeClick();
        void onReturnDateClick();
        void onReturnTimeClick();
    }
}
