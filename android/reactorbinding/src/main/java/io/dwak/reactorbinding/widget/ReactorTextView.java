package io.dwak.reactorbinding.widget;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;

import io.dwak.reactor.ReactorComputation;
import io.dwak.reactor.ReactorVar;
import io.dwak.reactor.interfaces.ReactorComputationFunction;
import io.dwak.reactorbinding.Preconditions;
import io.dwak.reactorbinding.utils.DisplayUtils;

/**
 * Convenience methods for {@link TextView} properties
 */
public class ReactorTextView {

    /**
     * Sets the text for a text view
     * @param source {@link ReactorVar} that contains the {@link CharSequence} (or subclass) to set the text on the target
     * @param target {@link TextView} to set the text on
     * @return {@link ReactorComputationFunction} to use in {@link io.dwak.reactor.Reactor#autoRun(ReactorComputationFunction)}
     * @see TextView#setText(CharSequence)
     */
    public static ReactorComputationFunction text(final ReactorVar<? extends  CharSequence> source, final TextView target) {
        return new ReactorComputationFunction() {
            @Override
            public void react(ReactorComputation reactorComputation) {
                if (Preconditions.checkNotNull(source.getValue(), target))
                    if(!target.getText().toString().equals(source.getValue().toString())) {
                        target.setText(source.getValue());
                    }
            }
        };
    }

    /**
     * Sets the textColor for a text view
     * @param source {@link ReactorVar} that contains the {@link CharSequence} (or subclass) to set the textColor on the target
     * @param target {@link TextView} to set the textColor on
     * @return {@link ReactorComputationFunction} to use in {@link io.dwak.reactor.Reactor#autoRun(ReactorComputationFunction)}
     * @see TextView#setTextColor(int)
     */
    public static ReactorComputationFunction textColor(final ReactorVar<? extends Integer> source, final TextView target) {
        return new ReactorComputationFunction() {
            @Override
            public void react(ReactorComputation reactorComputation) {
                if (Preconditions.checkNotNull(source.getValue(), target)) {
                    target.setTextColor(source.getValue());
                }
            }
        };
    }

    /**
     * Sets the text for a text view
     * @param source {@link ReactorVar} that contains the {@link Integer} (or subclass) to set the text on the target
     * @param target {@link TextView} to set the text on
     * @return {@link ReactorComputationFunction} to use in {@link io.dwak.reactor.Reactor#autoRun(ReactorComputationFunction)}
     * @see TextView#setText(int)
     */
    public static ReactorComputationFunction textRes(final ReactorVar<? extends  Integer> source, final TextView target) {
        return new ReactorComputationFunction() {
            @Override
            public void react(ReactorComputation reactorComputation) {
                if (Preconditions.checkNotNull(source.getValue(), target)) {
                    target.setText(source.getValue());
                }
            }
        };
    }

    public static ReactorVar<String> textChanges(final TextView source) {
        final ReactorVar<String> text = new ReactorVar<>(source.getText().toString());
        source.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                text.setValue(s.toString());
            }
        });
        return text;
    }

    public static void textChanges(final TextView source, final ReactorVar<String> target) {
        source.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                target.setValue(s.toString());
            }
        });
    }

    public static ReactorComputationFunction bindText(final ReactorVar<String> source, final EditText target) {
        target.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(Preconditions.checkNotNull(s) && !s.toString().equals(source.getRawValue())){
                    source.setValue(s.toString());
                }
            }
        });

        return new ReactorComputationFunction() {
            @Override
            public void react(ReactorComputation reactorComputation) {
                if(Preconditions.checkNotNull(source.getValue(), target) && !source.getValue().equals(target.getText().toString())){
                    target.setText(source.getValue());
                    target.setSelection(source.getValue().length());
                }
            }
        };
    }

    public static ReactorComputationFunction drawableLeft(final ReactorVar<Integer> source, final TextView target) {
        return new ReactorComputationFunction() {
            @Override
            public void react(final ReactorComputation reactorComputation) {
                if(Preconditions.checkNotNull(source.getValue(), target))
                    target.setCompoundDrawablesWithIntrinsicBounds(source.getValue(), 0, 0, 0);
            }
        };
    }

    public static ReactorComputationFunction drawableRight(final ReactorVar<Integer> source, final TextView target) {
        return new ReactorComputationFunction() {
            @Override
            public void react(final ReactorComputation reactorComputation) {
                if(Preconditions.checkNotNull(source.getValue(), target))
                    target.setCompoundDrawablesWithIntrinsicBounds(0, 0, source.getValue(), 0);
            }
        };
    }

    public static ReactorComputationFunction compoundDrawablePaddingInDp(final ReactorVar<Integer> source, final TextView target) {
        return new ReactorComputationFunction() {
            @Override
            public void react(final ReactorComputation reactorComputation) {
                if (Preconditions.checkNotNull(source.getValue(), target))
                    target.setCompoundDrawablePadding((int) DisplayUtils.dipToPx(target.getContext(), source.getValue()));
            }
        };
    }

}