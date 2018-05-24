package com.ehi.enterprise.android.ui.login.widget;

import android.content.Context;
import android.support.annotation.StringRes;
import android.util.AttributeSet;
import android.view.View;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.databinding.ChangePasswordRequirementBinding;
import com.ehi.enterprise.android.ui.view.DataBindingViewModelView;
import com.ehi.enterprise.android.ui.viewmodel.ManagersAccessViewModel;
import com.ehi.enterprise.android.utils.DisplayUtils;
import com.ehi.enterprise.android.utils.reactor_extensions.viewstate.ReactorConditionRowViewState;
import com.isobar.android.viewmodel.ViewModel;

import io.dwak.reactor.ReactorComputation;
import io.dwak.reactor.ReactorVar;
import io.dwak.reactor.interfaces.ReactorComputationFunction;

@ViewModel(ManagersAccessViewModel.class)
public class ConditionCheckRowView extends DataBindingViewModelView<ManagersAccessViewModel, ChangePasswordRequirementBinding> {

    public ConditionCheckRowView(Context context) {
        this(context, null);
    }

    public ConditionCheckRowView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ConditionCheckRowView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        if (isInEditMode()) {
            addView(inflate(context, R.layout.v_password_checkbox, null));
            return;
        }

        createViewBinding(R.layout.v_password_checkbox);
    }

    public void setText(final @StringRes int text) {
        getViewBinding().text.setText(getResources().getString(text));
    }

    public void measure() {
        measure(
                MeasureSpec.makeMeasureSpec(DisplayUtils.getScreenWidth(getContext()), View.MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(DisplayUtils.getScreenHeight(getContext()), MeasureSpec.AT_MOST)
        );
    }

    public static ReactorComputationFunction bindCheckMarkViewState(final ReactorVar<Boolean> source, final ConditionCheckRowView target) {
        return new ReactorComputationFunction() {
            @Override
            public void react(ReactorComputation reactorComputation) {
                Boolean isValid = source.getValue();
                if (isValid != null) {
                    target.getViewBinding().bulletPoint.setVisibility(GONE);
                    target.getViewBinding().icon.setVisibility(VISIBLE);

                    if (isValid) {
                        target.getViewBinding().icon.setImageDrawable(target.getResources().getDrawable(R.drawable.icon_check_03));
                    } else {
                        target.getViewBinding().icon.setImageDrawable(target.getResources().getDrawable(R.drawable.icon_alert_03));
                    }
                }
            }
        };
    }

    public static ReactorComputationFunction bindCheckStateToCondition(final ReactorVar<ReactorConditionRowViewState.CheckRowIconState> source, final ConditionCheckRowView target) {
        return new ReactorComputationFunction() {
            @Override
            public void react(ReactorComputation reactorComputation) {
                ReactorConditionRowViewState.CheckRowIconState isValid = source.getValue();
                if (isValid != ReactorConditionRowViewState.CheckRowIconState.nil) {
                    target.getViewBinding().bulletPoint.setVisibility(GONE);
                    target.getViewBinding().icon.setVisibility(VISIBLE);

                    if (isValid == ReactorConditionRowViewState.CheckRowIconState.valid) {
                        target.getViewBinding().icon.setImageDrawable(target.getResources().getDrawable(R.drawable.icon_check_03));
                    } else {
                        target.getViewBinding().icon.setImageDrawable(target.getResources().getDrawable(R.drawable.icon_alert_03));
                    }
                }
            }
        };
    }

    public static ReactorComputationFunction bindConditionText(final ReactorVar<Integer> source, final ConditionCheckRowView target) {
        return new ReactorComputationFunction() {
            @Override
            public void react(ReactorComputation reactorComputation) {
                Integer textRes = source.getValue();
                if (textRes != null && textRes != 0) {
                    target.setText(textRes);
                }
            }
        };
    }

}
