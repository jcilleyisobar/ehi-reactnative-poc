package com.ehi.enterprise.android.ui.dashboard.widget;

import android.annotation.SuppressLint;

import com.ehi.enterprise.android.utils.manager.DateUtilManager;
import com.ehi.enterprise.helpers.MockableObject;

import org.mockito.invocation.InvocationOnMock;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;

@SuppressLint("SimpleDateFormat")
public class DateUtilFormatDateTimeAnswer implements MockableObject.TestAnswer {
    @Override
    public Object provideAnswer(final InvocationOnMock invocation) {
        if(invocation.getMethod().getName().equals("formatDateTime")) {
            final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd-yyyy");
            final SimpleDateFormat simpleDateTimeFormat = new SimpleDateFormat("MM-dd-yyyy hh:mm");
            final SimpleDateFormat simpleTimeFormat = new SimpleDateFormat("hh:mm");
            final List<Object> arguments = Arrays.asList(invocation.getArguments());

            if (arguments.size() == 2 && arguments.contains(DateUtilManager.FORMAT_SHOW_TIME)) {
                return simpleTimeFormat.format(invocation.getArgument(0));
            }
            else if(arguments.size() > 2 && arguments.contains(DateUtilManager.FORMAT_SHOW_TIME)){
                return simpleDateTimeFormat.format(invocation.getArgument(0));
            }
            else {
                return simpleDateFormat.format(invocation.getArgument(0));
            }
        }

        return null;
    }
}
