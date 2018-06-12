package io.dwak.reactorbinding.view;

import android.widget.RadioGroup;

import io.dwak.reactor.ReactorComputation;
import io.dwak.reactor.ReactorVar;
import io.dwak.reactor.interfaces.ReactorComputationFunction;
import io.dwak.reactorbinding.Preconditions;

public class ReactorRadioGroup {
    public static ReactorComputationFunction checkedId(final ReactorVar<Integer> source, final RadioGroup target){
        return new ReactorComputationFunction() {
            @Override
            public void react(final ReactorComputation reactorComputation) {
               if(Preconditions.checkNotNull(source, source.getValue(), target)){
                   if(target.getCheckedRadioButtonId() != source.getValue()){
                       target.check(source.getValue());
                   }
               }
            }
        };
    }

    public static ReactorComputationFunction bindChecked(final ReactorVar<Integer> source, final RadioGroup target){
        if(Preconditions.checkNotNull(source, source.getValue(), target)){
            target.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(final RadioGroup group, final int checkedId) {
                    source.setValue(checkedId);
                }
            });

        }

        return  checkedId(source, target);
    }
}
