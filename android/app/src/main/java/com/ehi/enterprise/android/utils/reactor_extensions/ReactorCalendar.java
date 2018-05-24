package com.ehi.enterprise.android.utils.reactor_extensions;

import java.util.Calendar;
import java.util.Date;

import io.dwak.reactor.ReactorDependency;

/**
 * Wrapper around a Calendar to provide fine grained reactivity
 */
public class ReactorCalendar {
    private ReactorDependency mDependency = new ReactorDependency();
    private Calendar mCalendar;

    public ReactorCalendar(Calendar calendar) {
        mCalendar = calendar;
    }

    public ReactorCalendar(){
        mCalendar = null;
    }

    public Date getRawTime(){
        if(mCalendar == null){
            return null;
        }
        return mCalendar.getTime();
    }

    public Date getTime(){
        if(mDependency == null){
            mDependency = new ReactorDependency();
        }

        mDependency.depend();

        if(mCalendar == null){
            return null;
        }

        return mCalendar.getTime();
    }

    public void setTime(Date date){
        if(mCalendar == null){
            mCalendar = Calendar.getInstance();
        }
        mCalendar.setTime(date);

        if(mDependency == null){
            mDependency = new ReactorDependency();
        }

        mDependency.changed();
    }

    public void set(int field, int value){
        if(mCalendar == null){
            mCalendar = Calendar.getInstance();
        }
        mCalendar.set(field, value);
        mDependency.changed();
    }

    public Calendar getCalendar(){
        return mCalendar;
    }

    public void clear(){
        mCalendar = null;
        mDependency.changed();
    }

    public void unbind(){
        mDependency = null;
    }
}
