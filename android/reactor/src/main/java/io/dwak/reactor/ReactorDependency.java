package io.dwak.reactor;

import android.util.SparseArray;

import io.dwak.reactor.interfaces.ReactorInvalidateCallback;

/**
 * A Dependency represents an atomic unit of reactive data that a
 * computation might depend on.
 * When the data changes, the computations are invalidated.
 */
public final class ReactorDependency {
    private SparseArray<ReactorComputation> mDependentsById;
    private boolean mLogCallPath = false;

    public ReactorDependency() {
        mDependentsById = new SparseArray<ReactorComputation>();
    }

    public boolean depend() {
        return depend(null);
    }

    /**
     * Declares that the current computation (or {@link ReactorComputation} if given) depends on `dependency`.
     * The computation will be invalidated the next time `dependency` changes.
     * If there is no current computation and {@link #depend()}} is called with no arguments, it does nothing and returns false.
     *
     * @param reactorComputation computation that depends on this dependency
     * @return true if computation is a new dependant rather than an existing one
     */
    public boolean depend(ReactorComputation reactorComputation) {
        if (reactorComputation == null) {
            if (!Reactor.mActive)
                return false;

            reactorComputation = Reactor.getInstance().getCurrentReactorComputation();
        }

        final int id = reactorComputation.getId();
        if (mDependentsById.get(id) == null) {
            mDependentsById.put(id, reactorComputation);
            reactorComputation.addInvalidateComputationFunction(new ReactorInvalidateCallback() {
                @Override
                public void onInvalidate() {
                    mDependentsById.remove(id);
                }
            });

            if(Reactor.getInstance().shouldLog() && mLogCallPath){
                Exception e = getCallStack();
                Lumberjack.logError("Reactor callpath #depend", null, e);
            }
            return true;
        }
        return false;
    }

    public void logCallPath(boolean logPath){
        mLogCallPath = logPath;
    }

    /**
     * Invalidate all dependent computations immediately and remove them as dependents.
     */
    public void changed() {
        int key;
        final int size = mDependentsById.size();

        if(Reactor.getInstance().shouldLog() && mLogCallPath){
            Exception e = getCallStack();
            Lumberjack.logError("Reactor callpath #changed", null, e);
        }

        while(mDependentsById.size() != 0) {
            /*Work around, if the reactor computation was already invalidated,
             lets break the while loop to avoid app being in loop*/
            if (mDependentsById.valueAt(0).isInvalidated()) {
                break;
            }
            mDependentsById.valueAt(0).invalidate();
        }

    }

    public Exception getCallStack() {
        try{
            throw new IllegalStateException();
        }
        catch (Exception e){
            StackTraceElement[] trace = e.getStackTrace();
            StackTraceElement[] cleanedTrace = new StackTraceElement[trace.length -2];
            for(int i= 2; i < trace.length; i++){
                cleanedTrace[i - 2] = trace[i];
            }
            e.setStackTrace(cleanedTrace);
            return e;
        }
    }

    /**
     * True if this Dependency has one or more dependent {@link ReactorComputation},
     * which would be invalidated if this {@link ReactorDependency} were to change.
     * @return true if the dependency has dependants
     */
    public boolean hasDependants() {
        return mDependentsById.size() > 0;
    }


    /**
     * Removes all dependants from this dependency object
     */
    void unbind() {
        int key;
        for(int i = 0; i < mDependentsById.size(); i++){
            key = mDependentsById.keyAt(i);
            mDependentsById.get(key).stop();
        }

        mDependentsById.clear();
    }
}
