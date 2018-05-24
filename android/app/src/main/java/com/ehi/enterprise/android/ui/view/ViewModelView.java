package com.ehi.enterprise.android.ui.view;

import android.content.Context;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.v4.util.ArrayMap;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.ehi.enterprise.android.BuildConfig;
import com.ehi.enterprise.android.ui.viewmodel.ManagersAccessViewModel;
import com.ehi.enterprise.android.utils.manager.ManagerDelegateNotAllowedImpl;
import com.isobar.android.viewmodel.InjectViewModel;
import com.isobar.android.viewmodel.ViewModelInjector;

import java.util.ArrayList;
import java.util.List;

import io.dwak.reactor.Reactor;
import io.dwak.reactor.ReactorComputation;
import io.dwak.reactor.interfaces.ReactorComputationFunction;

public class ViewModelView<T extends ManagersAccessViewModel> extends FrameLayout {

	private ArrayMap<String, ReactorComputation> mReactiveFunctions;

	private T mViewModel;
	private boolean mViewModelCreated;

	//region constructors
	public ViewModelView(Context context) {
		this(context, null);
	}

	public ViewModelView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public ViewModelView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		ViewModelInjector.inject(this);
        mViewModel.prepareToAttachToView();
	}
	//endregion

	//region lifecycle
	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
        if(!isInEditMode()) {
            mReactiveFunctions = new ArrayMap<>();
            mViewModel.onAttachToView();
            initDependencies();
        }
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		for (ReactorComputation c : mReactiveFunctions.values()) {
			c.stop();
		}
		mReactiveFunctions.clear();
		getViewModel().onDetachFromView();
	}
	//endregion lifecycle

	//region viewModel
	protected T getViewModel() {
		return mViewModel;
	}

	@InjectViewModel
	public void setViewModel(T viewModel) {
		if (mViewModelCreated && mViewModel != null) {
			return;
		}

		mViewModel = viewModel;
		mViewModel.setResources(getResources());
		mViewModel.setManagersDelegate(new ManagerDelegateNotAllowedImpl());
		mViewModelCreated = true;
	}
	//endregion

	//region Reactor

	/**
	 * Initialize your reactions in this method
	 * {@link #initDependencies()} is called from within the views{@link #onAttachedToWindow()} ()} lifecycle method for you
	 * This method may be called safely from multiple locations if reactions are initialized using
	 * {@link #addReaction(String, io.dwak.reactor.interfaces.ReactorComputationFunction)}
	 */
	@CallSuper
	protected void initDependencies() {

	}

	/**
	 * Use this to bindCheckMarkViewState multiple functions at once
	 * @param functions {@link List} of {@link ReactorComputationFunction}
	 * @return {@link List} of the created {@link ReactorComputation} objects from {@link Reactor#autoRun(ReactorComputationFunction)}
	 */
	protected List<ReactorComputation> bind(ReactorComputationFunction... functions){
		List<ReactorComputation> reactorComputations = new ArrayList<>();
		for (ReactorComputationFunction function : functions) {
			reactorComputations.add(bind(function));
		}

		return reactorComputations;
	}

	/**
	 * Semantic wrapper around {@link #addReaction(ReactorComputationFunction)}
	 * @see #addReaction(ReactorComputationFunction)
	 */
	protected ReactorComputation bind(@NonNull ReactorComputationFunction function){
		Exception exception = null;
		if(BuildConfig.DEBUG) {
			try {
				throw new IllegalStateException();
			} catch (Exception e) {
				exception = e;
			}
		}
		ReactorComputation computation = addReaction(function.toString(), function, exception);
		return computation;
	}

	protected ReactorComputation addReaction(@NonNull String id, @NonNull ReactorComputationFunction reactorComputationFunction, Exception exception) {
		if (!mReactiveFunctions.containsKey(id)) {
			final ReactorComputation reactorComputation = Reactor.getInstance().autoRun(reactorComputationFunction, exception);
			mReactiveFunctions.put(id, reactorComputation);
			return reactorComputation;
		}
		return null;
	}

	/**
	 * Convenience method to auto generate a reaction's tag
	 * @see #addReaction(String, ReactorComputationFunction)
	 */
	protected ReactorComputation addReaction(@NonNull ReactorComputationFunction function){
		return addReaction(function.toString(), function);
	}

	/**
	 * Add a unique {@link io.dwak.reactor.interfaces.ReactorComputationFunction} to the {@link io.dwak.reactor.Reactor}
	 *
	 * @param id                         Unique id of the function
	 * @param reactorComputationFunction computation to run on invalidation
	 * @return The {@link ReactorComputation} created by {@link io.dwak.reactor.Reactor}
	 */
	protected ReactorComputation addReaction(@NonNull String id, @NonNull ReactorComputationFunction reactorComputationFunction) {
		return addReaction(id, reactorComputationFunction, null);
	}
	/**
	 * Run a computation function in a non-reactive block
	 *
	 * @param reactorComputationFunction {@link ReactorComputationFunction} to run without binding dependencies
	 * @return same function reference
	 */
	protected ReactorComputationFunction runNonReactive(@NonNull ReactorComputationFunction reactorComputationFunction) {
		return Reactor.getInstance().nonReactive(reactorComputationFunction);
	}
	//endregion
}
