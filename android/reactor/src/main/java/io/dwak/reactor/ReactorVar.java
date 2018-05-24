package io.dwak.reactor;

/**
 * Object that wraps an object and it's {@link io.dwak.reactor.ReactorDependency}
 * If you'd like to use primitive data types, you'll have to handle your own instance of
 * {@link io.dwak.reactor.ReactorDependency} alongside your primitive value
 * Created by vishnu on 1/25/15.
 */
public class ReactorVar<T> {
    private T mValue;
    protected ReactorDependency mDependency = new ReactorDependency();
    private DependencyUnboundListener mDependencyUnboundListener;

    public ReactorVar() {
    }

    public ReactorVar(T value) {
        mValue = value;
    }

    /**
     * Unbind and null out the object's {@link ReactorDependency}
     */
    public void unbindDependency(){
        if(mDependency != null) {
            mDependency.unbind();
            mDependency = null;
            if(mDependencyUnboundListener != null){
                mDependencyUnboundListener.onDependencyUnbound();
            }
        }
    }

    /**
     * Gets the value, and adds a dependency
     * @return Value that this ReactorVar contains
     */
    public T getValue() {
        if (mDependency == null)
            mDependency = new ReactorDependency();

        mDependency.depend();
        return mValue;
    }

    /**
     * Sets the contained value and marks the dependency as changed
     * @param value Value to set
     */
    public void setValue(T value) {
        this.mValue = value;
        if (mDependency == null)
            mDependency = new ReactorDependency();

        mDependency.changed();
    }

    public ReactorDependency getDependency() {
        return mDependency;
    }

    public void setDependency(ReactorDependency dependency) {
        mDependency = dependency;
    }

    /**
     * Gets the contained value without creating a dependency
     * @return The contained value
     */
    public T getRawValue(){
        return mValue;
    }

    /**
     * Sets the value without changing the {@link ReactorDependency}
     * @param value Value to set
     */
    public void setRawValue(T value){
        mValue = value;
    }

    public void setDependencyUnboundListener(DependencyUnboundListener listener){
        mDependencyUnboundListener = listener;
    }

    @Override
    public int hashCode() {
        return mValue != null ? mValue.hashCode() : 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final ReactorVar that = (ReactorVar) o;

        if (mValue != null ? !mValue.equals(that.mValue) : that.mValue != null) return false;

        return true;
    }

    public interface DependencyUnboundListener{
        void onDependencyUnbound();
    }
}
