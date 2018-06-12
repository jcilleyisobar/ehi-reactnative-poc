package io.dwak.reactorbinding.view;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.view.View;

import io.dwak.reactor.ReactorComputation;
import io.dwak.reactor.ReactorVar;
import io.dwak.reactor.interfaces.ReactorComputationFunction;
import io.dwak.reactorbinding.Preconditions;

/**
 * Convenience methods for binding {@link View} properties to {@link ReactorVar}
 */
public class ReactorView {
	/**
	 * Sets the enabled state of the target {@link View}
	 *
	 * @param source {@link ReactorVar<Boolean>} : true for enabled, false for disable
	 * @param target view to set enabled state
	 * @return {@link ReactorComputationFunction} to use in {@link io.dwak.reactor.Reactor#autoRun(ReactorComputationFunction)}
	 * @see View#setEnabled(boolean)
	 */
	public static ReactorComputationFunction enabled(final ReactorVar<Boolean> source, final View target) {
		return new ReactorComputationFunction() {
			@Override
			public void react(final ReactorComputation reactorComputation) {
                if (Preconditions.checkNotNull(source.getValue(), target))
                    target.setEnabled(source.getValue());
			}
		};
	}

	/**
	 * Sets the alpha state of the target {@link View}
	 *
	 * @param source {@link ReactorVar<Float>} : alpha of the view
	 * @param target view to set enabled state
	 * @return {@link ReactorComputationFunction} to use in {@link io.dwak.reactor.Reactor#autoRun(ReactorComputationFunction)}
	 * @see View#setAlpha(float)
	 */
	public static ReactorComputationFunction alpha(final ReactorVar<Float> source, final View target) {
		return new ReactorComputationFunction() {
			@Override
			public void react(final ReactorComputation reactorComputation) {
				if (Preconditions.checkNotNull(source.getValue(), target))
					target.setAlpha(source.getValue());
			}
		};
	}

	/**
	 * Sets the visibility of a view, uses {@link View#GONE} if false
	 *
	 * @param source {@link ReactorVar<Boolean>} : true for {@link View#VISIBLE} false for {@link View#GONE}
	 * @param target view to set visibility on
	 * @return {@link ReactorComputationFunction} to use in {@link io.dwak.reactor.Reactor#autoRun(ReactorComputationFunction)}
	 * @see View#setVisibility(int)
	 */
	public static ReactorComputationFunction visible(final ReactorVar<Boolean> source, final View target) {
		return new ReactorComputationFunction() {
			@Override
			public void react(final ReactorComputation reactorComputation) {
                if (Preconditions.checkNotNull(source.getValue(), target))
                    target.setVisibility(source.getValue() ? View.VISIBLE : View.GONE);
			}
		};
	}

	/**
	 * @param visibilityWhenFalse visibility for when the source is false
	 * @see #visible(ReactorVar, View), the only difference is that this method allows setting of the false state
	 */
	public static ReactorComputationFunction visible(final ReactorVar<Boolean> source, final View target, final int visibilityWhenFalse) {
		return new ReactorComputationFunction() {
			@Override
			public void react(final ReactorComputation reactorComputation) {
                if (Preconditions.checkNotNull(source.getValue(), target))
                    target.setVisibility(source.getValue() ? View.VISIBLE : visibilityWhenFalse);
			}
		};
	}

	public static ReactorComputationFunction visibility(final ReactorVar<Integer> source, final View target) {
		return new ReactorComputationFunction() {
			@Override
			public void react(final ReactorComputation reactorComputation) {
                if (Preconditions.checkNotNull(source.getValue(), target)) {
                    //noinspection ResourceType
                    if(target.getVisibility() != source.getValue()) {
                        //noinspection ResourceType
                        target.setVisibility(source.getValue());
                    }
                }
            }
        };
	}

	public static ReactorComputationFunction rotation(final ReactorVar<Float> source, final View target) {
		return new ReactorComputationFunction() {
			@Override
			public void react(final ReactorComputation reactorComputation) {
				if (Preconditions.checkNotNull(source.getValue(), target)) {
					target.setRotation(source.getValue());
				}
			}
		};
	}

	/**
	 * Sets the background on a view from an @DrawableRes integer source
	 *
	 * @param source    resource id for the drawable
	 * @param target    view to set background on
	 * @param resources resources to populate the drawable from
	 * @return {@link ReactorComputationFunction} to use in {@link io.dwak.reactor.Reactor#autoRun(ReactorComputationFunction)}
	 * @see View#setBackground(Drawable)
	 */
	public static ReactorComputationFunction backgroundRes(final ReactorVar<Integer> source, final View target) {
		return new ReactorComputationFunction() {
			@Override
			public void react(final ReactorComputation reactorComputation) {
                if (Preconditions.checkNotNull(source.getValue(), target))
                    target.setBackgroundResource(source.getValue());
			}
		};
	}

	/**
	 * Sets the background on a view from an {@link Drawable}
	 *
	 * @param source Drawable to use for the background of the target
	 * @param target {@link View} to set the background of
	 * @return {@link ReactorComputationFunction} to use in {@link io.dwak.reactor.Reactor#autoRun(ReactorComputationFunction)}
	 * @see View#setBackground(Drawable)
	 */
	public static ReactorComputationFunction background(final ReactorVar<Drawable> source, final View target) {
		return new ReactorComputationFunction() {
			@Override
			public void react(final ReactorComputation reactorComputation) {
                if (Preconditions.checkNotNull(source.getValue(), target))
                    if (source.getValue() != null) target.setBackground(source.getValue());
			}
		};
	}

	/**
	 * Sets the background color of a view from a color integer
	 *
	 * @param source {@link ReactorVar<Integer>} containing the color
	 * @param target view to set the background color of
	 * @return {@link ReactorComputationFunction} to use in {@link io.dwak.reactor.Reactor#autoRun(ReactorComputationFunction)}
	 * @see View#setBackgroundColor(int)
	 */
	public static ReactorComputationFunction backgroundColor(final ReactorVar<Integer> source, final View target) {
		return new ReactorComputationFunction() {
			@Override
			public void react(final ReactorComputation reactorComputation) {
                if (Preconditions.checkNotNull(source.getValue(), target))
                    target.setBackgroundColor(source.getValue());
			}
		};
	}

	/**
	 * Sets the selected state of the view
	 *
	 * @param source {@link ReactorVar} containing a boolean
	 * @param target {@link View} to set the selected state of
	 * @return {@link ReactorComputationFunction} to use in {@link io.dwak.reactor.Reactor#autoRun(ReactorComputationFunction)}
	 * @see View#setSelected(boolean)
	 */
	public static ReactorComputationFunction selected(final ReactorVar<Boolean> source, final View target) {
		return new ReactorComputationFunction() {
			@Override
			public void react(final ReactorComputation reactorComputation) {
                if (Preconditions.checkNotNull(source.getValue(), target))
                    target.setSelected(source.getValue());
			}
		};
	}
}
