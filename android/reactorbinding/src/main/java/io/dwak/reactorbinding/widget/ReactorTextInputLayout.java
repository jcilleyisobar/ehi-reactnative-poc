package io.dwak.reactorbinding.widget;

import android.support.design.widget.TextInputLayout;
import android.widget.TextView;

import io.dwak.reactor.ReactorComputation;
import io.dwak.reactor.ReactorVar;
import io.dwak.reactor.interfaces.ReactorComputationFunction;

public class ReactorTextInputLayout {

	/**
	 * Sets the error text for a text input layout
	 * @param source {@link ReactorVar} that contains the {@link CharSequence} (or subclass) to set the error text on the target
	 * @param target {@link TextView} to set the error text on
	 * @return {@link ReactorComputationFunction} to use in {@link io.dwak.reactor.Reactor#autoRun(ReactorComputationFunction)}
	 * @see TextInputLayout#setError(CharSequence)
	 */
	public static ReactorComputationFunction error(final ReactorVar<? extends  CharSequence> source, final TextInputLayout target) {
		return new ReactorComputationFunction() {
			@Override
			public void react(ReactorComputation reactorComputation) {
				target.setError(source.getValue());
//				if ((target.getError() != null && !target.getError().equals(source.getValue()))
//						|| target.getError() == null && source.getValue() != null) {
//					target.setError(source.getValue());
//				}
			}
		};
	}

	/**
	 * Sets the error enabled for a text input layout
	 * @param source {@link ReactorVar} that contains the {@link CharSequence} (or subclass) to set the error enabled on the target
	 * @param target {@link TextView} to set the error enabled on
	 * @return {@link ReactorComputationFunction} to use in {@link io.dwak.reactor.Reactor#autoRun(ReactorComputationFunction)}
	 * @see TextInputLayout#setErrorEnabled(boolean)
	 */
	public static ReactorComputationFunction errorEnabled(final ReactorVar<Boolean> source, final TextInputLayout target) {
		return new ReactorComputationFunction() {
			@Override
			public void react(ReactorComputation reactorComputation) {
//				if(target.isErrorEnabled() != source.getValue()) {
					target.setErrorEnabled(source.getValue());
//				}
			}
		};
	}
}
