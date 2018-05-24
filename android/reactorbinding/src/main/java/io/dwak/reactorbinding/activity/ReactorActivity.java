package io.dwak.reactorbinding.activity;

import android.app.Activity;

import io.dwak.reactor.ReactorComputation;
import io.dwak.reactor.ReactorVar;
import io.dwak.reactor.interfaces.ReactorComputationFunction;
import io.dwak.reactorbinding.Preconditions;

/**
 * Convenience methods for {@link Activity}
 */
public class ReactorActivity {

    /**
     * Sets the title on an activity
     * @param source {@link ReactorVar} that contains the {@link CharSequence} (or subclass) to set the text on the target
     * @param target {@link Activity} on which to set the title on
     * @return {@link ReactorComputationFunction} to use in {@link io.dwak.reactor.Reactor#autoRun(ReactorComputationFunction)}
     * @see Activity#setTitle(CharSequence)
     */
    public static ReactorComputationFunction title(final ReactorVar<? extends CharSequence> source, final Activity target){
        return new ReactorComputationFunction() {
            @Override
            public void react(final ReactorComputation reactorComputation) {
                if (Preconditions.checkNotNull(source.getValue(), target))
                    target.setTitle(source.getValue());
            }
        };
    }

    /**
     * Sets the title on an activity
     * @param source {@link ReactorVar} that contains the {@link CharSequence} (or subclass) to set the text on the target
     * @param target {@link Activity} on which to set the title on
     * @return {@link ReactorComputationFunction} to use in {@link io.dwak.reactor.Reactor#autoRun(ReactorComputationFunction)}
     * @see Activity#setTitle(CharSequence)
     */
    public static ReactorComputationFunction titleRes(final ReactorVar<? extends Integer> source, final Activity target){
        return new ReactorComputationFunction() {
            @Override
            public void react(final ReactorComputation reactorComputation) {
                if (Preconditions.checkNotNull(source.getValue(), target))
                    target.setTitle(source.getValue());
            }
        };
    }

    public static ReactorComputationFunction finish(final ReactorVar<Boolean> source, final Activity target){
        return new ReactorComputationFunction() {
            @Override
            public void react(final ReactorComputation reactorComputation) {
                if(Preconditions.checkNotNull(source.getValue(), target)){
                    if(source.getValue()){
                        target.finish();
                        source.setValue(false);
                    }
                }
            }
        };
    }
}
