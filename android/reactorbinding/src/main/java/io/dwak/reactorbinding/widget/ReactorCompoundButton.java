package io.dwak.reactorbinding.widget;

import android.widget.CompoundButton;

import io.dwak.reactor.ReactorComputation;
import io.dwak.reactor.ReactorVar;
import io.dwak.reactor.interfaces.ReactorComputationFunction;
import io.dwak.reactorbinding.Preconditions;

public class ReactorCompoundButton {
    public static ReactorVar<Boolean> checkedChanges(final CompoundButton source){
        final ReactorVar<Boolean> checkEvent = new ReactorVar<>(source.isChecked());
        source.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(final CompoundButton buttonView, final boolean isChecked) {
                checkEvent.setValue(isChecked);
            }
        });

        return checkEvent;
    }

    /**
     * Two way bind the checked changes of the target compound button
     * @param source ReactorVar to handle state changes
     * @param target compound button to listen in for changes
     * @return ReactorComputationFunction to add to Reactor
     */
    public static ReactorComputationFunction bindChecked(final ReactorVar<Boolean> source, final CompoundButton target){
        target.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(final CompoundButton buttonView, final boolean isChecked) {
                source.setValue(isChecked);
            }
        });

        return new ReactorComputationFunction() {
            @Override
            public void react(final ReactorComputation reactorComputation) {
                if(Preconditions.checkNotNull(source.getValue(), target)){
                    target.setChecked(source.getValue());
                }
            }
        };
    }
}
