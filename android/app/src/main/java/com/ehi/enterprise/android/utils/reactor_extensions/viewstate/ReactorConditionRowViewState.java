package com.ehi.enterprise.android.utils.reactor_extensions.viewstate;

import io.dwak.reactor.ReactorVar;
import io.dwak.reactor.unbinder.annotation.AutoUnbindAll;

@AutoUnbindAll
public class ReactorConditionRowViewState extends ReactorTextViewState {

    final ReactorVar<CheckRowIconState> mIconState = new ReactorVar<>(CheckRowIconState.nil);

    public ReactorVar<CheckRowIconState> getIconStateVar() {
        return mIconState;
    }

    public CheckRowIconState getIconState(){
        return mIconState.getValue();
    }

    public void setIconState(CheckRowIconState state){
        mIconState.setValue(state);
    }

    public enum CheckRowIconState{
        nil,
        valid,
        invalid;

        /**
         * Used to convert a primitive boolean to a CheckRowIconState for interchangeability for conditions
         * @param state
         * @return
         */
        public static CheckRowIconState fromBoolean(boolean state){
            return state ? valid : invalid;
        }
    }
}
