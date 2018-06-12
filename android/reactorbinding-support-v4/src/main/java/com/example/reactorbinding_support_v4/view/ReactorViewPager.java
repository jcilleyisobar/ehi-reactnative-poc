package com.example.reactorbinding_support_v4.view;

import android.support.v4.view.ViewPager;

import io.dwak.reactor.ReactorComputation;
import io.dwak.reactor.ReactorVar;
import io.dwak.reactor.interfaces.ReactorComputationFunction;

/**
 * Convenience methods for {@link ViewPager}
 */
public class ReactorViewPager {
    public static ReactorComputationFunction currentItem(final ReactorVar<Integer> source, final ViewPager target){
        return new ReactorComputationFunction() {
            @Override
            public void react(final ReactorComputation reactorComputation) {
                target.setCurrentItem(source.getValue());
            }
        };
    }

    /**
     * Sets the current item on a view pager from the integer source
     * @param source {@link ReactorVar<Integer>} containing the page to set on the view pager
     * @param target {@link ViewPager} on which to set the current page
     * @param smoothScroll whether or not to use smooth scrolling
     * @return {@link ReactorComputationFunction} to use in {@link io.dwak.reactor.Reactor#autoRun(ReactorComputationFunction)}
     * @see ViewPager#setCurrentItem(int, boolean)
     */
    public static ReactorComputationFunction currentItem(final ReactorVar<Integer> source, final ViewPager target, final boolean smoothScroll){
        return new ReactorComputationFunction() {
            @Override
            public void react(final ReactorComputation reactorComputation) {
                target.setCurrentItem(source.getValue(), smoothScroll);
            }
        };
    }
}
