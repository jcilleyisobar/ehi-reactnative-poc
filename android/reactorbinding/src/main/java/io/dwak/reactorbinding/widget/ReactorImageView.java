package io.dwak.reactorbinding.widget;

import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import io.dwak.reactor.ReactorComputation;
import io.dwak.reactor.ReactorVar;
import io.dwak.reactor.interfaces.ReactorComputationFunction;
import io.dwak.reactorbinding.Preconditions;

public class ReactorImageView {

	/**
	 * Sets the image source for an image view
	 * @param source {@link ReactorVar} that contains the {@link Integer} to set the image on the target
	 * @param target {@link ImageView} to set the image on
	 * @return {@link ReactorComputationFunction} to use in {@link io.dwak.reactor.Reactor#autoRun(ReactorComputationFunction)}
	 * @see ImageView#setImageResource(int)
	 */
	public static ReactorComputationFunction imageResource(final ReactorVar<Integer> source, final ImageView target){
		return new ReactorComputationFunction() {
			@Override
			public void react(ReactorComputation reactorComputation) {
                if (Preconditions.checkNotNull(source.getValue(), target))
                    target.setImageResource(source.getValue());
            }
		};
	}

    /**
     * Sets the image drawable for an image view
     * @param source {@link ReactorVar} that contains the {@link Drawable} to set the image on the target
     * @param target {@link ImageView} to set the image on
     * @return {@link ReactorComputationFunction} to use in {@link io.dwak.reactor.Reactor#autoRun(ReactorComputationFunction)}
     * @see ImageView#setImageDrawable(Drawable)
     */
    public static ReactorComputationFunction imageDrawable(final ReactorVar<Drawable> source, final ImageView target){
        return new ReactorComputationFunction() {
            @Override
            public void react(final ReactorComputation reactorComputation) {
                if(Preconditions.checkNotNull(source.getValue(), target))
                    target.setImageDrawable(source.getValue());
            }
        };
    }
}
