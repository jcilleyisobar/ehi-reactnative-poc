package io.dwak.reactor;

import java.util.ArrayList;

import io.dwak.reactor.interfaces.ReactorComputationFunction;
import io.dwak.reactor.interfaces.ReactorInvalidateCallback;

/**
 *
 * A ReactorComputation object represents code that is repeatedly rerun
 * in response to reactive data changes.
 * Computations don't have return values; they just
 * perform actions, such as rerendering a template on the screen. Computations
 * are created using {@link Reactor#autoRun(io.dwak.reactor.interfaces.ReactorComputationFunction)}.
 * Use {@link #stop()} to prevent further rerunning of a
 * computation.
 */
public final class ReactorComputation {
    private static final String TAG = ReactorComputation.class.getSimpleName();
    private final int mId;

    /**
     * Collection of callbacks to run on invalidation
     */
    private final ArrayList<ReactorInvalidateCallback> mInvalidateCallbacks;

    /**
     * Possible parent computation for nested computations
     */
    private final ReactorComputation mParent;

    /**
     * Function that this computation will run
     */
    private final ReactorComputationFunction mFunction;

    /**
     * true if this computation has been stopped
     */
    private boolean mStopped;

    /**
     * true if this computation has been invalidated or stopped
     */
    private boolean mInvalidated;

    /**
     * True if the computation is recomputing
     */
    private boolean mRecomputing;

    /**
     * True during the initial run of the computation at the time {@link Reactor#autoRun(ReactorComputationFunction)}
     * is called, and false on subsequent reruns and at other times.
     */
    private boolean mFirstRun;

    /**
     * True if the computation has errored
     */
    private boolean mErrored;

    /**
     * True if the computation is in the process of being constructed
     */
    private boolean mConstructingComputation = false;
    private StopCallback mStopCallback;
    private Exception mOriginError;

    ReactorComputation(ReactorComputationFunction function, ReactorComputation parent) {
        this(function, parent, null);
    }

    public ReactorComputation(ReactorComputationFunction function, ReactorComputation parent, Exception e) {
        Lumberjack.log(TAG, "Computation in construction", LogLevel.ALL);
        mOriginError = e;
        mStopped = false;
        mInvalidated = false;
        mId = Reactor.nextId++;
        mInvalidateCallbacks = new ArrayList<ReactorInvalidateCallback>();
        mFirstRun = true;
        mParent = parent;
        mFunction = function;
        mRecomputing = false;
        mErrored = true;

        try {
            compute();
            mErrored = false;
        } finally {
            mFirstRun = false;
            if (mErrored) {
                stop();
            }
        }
    }

    /**
     * Prevents this computation from rerunning.
     */
    public void stop() {
        if (!mStopped) {
            Lumberjack.log(TAG, "Computation " + mId + " stopped", LogLevel.ALL);
            mStopped = true;
            if(mStopCallback != null){
                mStopCallback.onStop();
            }
            invalidate();
        }
    }

    /**
     * Invalidates this computation so that it will be rerun.
     */
    public void invalidate() {
        if (!mInvalidated) {
            Lumberjack.log(TAG, "Computation " + mId + " invalidating", LogLevel.ALL);
            // if we're currently in _recompute(), don't enqueue
            // ourselves, since we'll rerun immediately anyway.
            if (!mRecomputing && !mStopped) {
                Reactor.getInstance().requireFlush();
                Reactor.getInstance().getPendingReactorComputations().add(this);
            }

            mInvalidated = true;

            // callbacks can't add callbacks, because
            // self.invalidated === true.
            for (final ReactorInvalidateCallback invalidateCallback : mInvalidateCallbacks) {
                Reactor.getInstance().nonReactive(new ReactorComputationFunction() {
                    @Override
                    public void react(ReactorComputation reactorComputation) {
                        invalidateCallback.onInvalidate();
                    }
                });
            }
            mInvalidateCallbacks.clear();
        }
    }

    /**
     * Registers a {@link ReactorComputationFunction} to run when this computation is next invalidated,
     * or runs it immediately if the computation is already invalidated.
     * The react is run exactly once and not upon future invalidations unless {@link #addInvalidateComputationFunction(io.dwak.reactor.interfaces.ReactorInvalidateCallback)}
     * is called again after the computation becomes valid again.
     *
     * @param callback Callback to run on invalidation
     *
     * @return ReactorComputation with the added invalidation callback
     */
    public ReactorComputation addInvalidateComputationFunction(ReactorInvalidateCallback callback) {
        mInvalidateCallbacks.add(callback);

        return this;
    }

    private void compute() {
        mInvalidated = false;
        final ReactorComputation previousReactorComputation = Reactor.getInstance().getCurrentReactorComputation();
        Reactor.getInstance().setCurrentReactorComputation(this);
        boolean previousInCompute = Reactor.getInstance().isInCompute();
        Reactor.getInstance().setInCompute(true);
        try {
            mFunction.react(this);
        }catch (RuntimeException e){
            pretiffyError(e);
            throw e;
        }
        catch (Exception e){
            pretiffyError(e);
            throw e;
        }

        finally {
            Reactor.getInstance().setCurrentReactorComputation(previousReactorComputation);
            Reactor.getInstance().setInCompute(previousInCompute);
        }

        if(Reactor.getInstance().shouldLog()) {
            Lumberjack.log(TAG, this.toString(), LogLevel.COMPUTE);
        }
    }

    /**
     * Takes a real exception and given a {@link #mOriginError} will attempt to create a readable stacktrace
     * @param e
     */
    private void pretiffyError(Exception e) {
        if(mOriginError != null) {
            StackTraceElement[] errorStack = e.getStackTrace();
            StackTraceElement[] originStack = mOriginError.getStackTrace();
            StackTraceElement[] mergedStackTrace = new StackTraceElement[errorStack.length + 6];

            mergedStackTrace[0] = errorStack[0];
            int index =1;
            for(int i=1; i < 7; i++){
                mergedStackTrace[index] = originStack[i];
                index ++;
            }
            for(int i=1; i < errorStack.length; i++){
                mergedStackTrace[index] = errorStack[i];
                index ++;
            }
            e.setStackTrace(mergedStackTrace);
        }
    }

    void reCompute() {
        Lumberjack.log(TAG, String.format("Computation %d recomputing", mId), LogLevel.ALL);
        mRecomputing = true;
        try {
            while (mInvalidated && !mStopped) {
                try {
                    compute();
                } catch (Exception e) {
                    throw e;
                }
                // If _compute() invalidated us, we run again immediately.
                // A computation that invalidates itself indefinitely is an
                // infinite loop, of course.
                //
                // We could put an iteration counter here and catch run-away
                // loops.
            }
        } finally {
            mRecomputing = false;
        }

    }

    public boolean isStopped() {
        return mStopped;
    }

    public boolean isInvalidated() {
        return mInvalidated;
    }

    public boolean isErrored() {
        return mErrored;
    }

    public boolean isRecomputing() {
        return mRecomputing;
    }

    public boolean isConstructingComputation() {
        return mConstructingComputation;
    }

    public ReactorComputation getParent() {
        return mParent;
    }


    public int getId() {
        return mId;
    }

    public boolean isFirstRun() {
        return mFirstRun;
    }

    /**
     * Sets a callback to be called when the computation is stopped
     * @param stopCallback {@link StopCallback} to be called
     */
    public final void setOnStopCallback(StopCallback stopCallback){
        mStopCallback = stopCallback;
    }

    @Override
    public String toString() {
        return "ReactorComputation{" +
                "mId=" + mId +
                ", mInvalidateCallbacks=" + mInvalidateCallbacks +
                ", mParent=" + mParent +
                ", mFunction=" + mFunction +
                ", mStopped=" + mStopped +
                ", mInvalidated=" + mInvalidated +
                ", mRecomputing=" + mRecomputing +
                ", mFirstRun=" + mFirstRun +
                ", mErrored=" + mErrored +
                ", mConstructingComputation=" + mConstructingComputation +
                ", Created at: " + Thread.currentThread().getStackTrace()[7].toString() +
                "}";
    }

    public void registerStackTrace(Exception exception) {
        mOriginError = exception;
    }

    /**
     * Callback for when a computation stops
     */
    public interface StopCallback {
        void onStop();
    }
}
