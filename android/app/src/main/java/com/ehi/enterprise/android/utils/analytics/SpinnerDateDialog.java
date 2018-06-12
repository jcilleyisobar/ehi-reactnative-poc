package com.ehi.enterprise.android.utils.analytics;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.content.res.Resources;

import com.ehi.enterprise.android.R;

import java.util.Calendar;
import java.util.Date;

public class SpinnerDateDialog extends DialogFragment {

    private DatePickerDialog.OnDateSetListener onDateSetListener;

    private Date initialDate;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        View view = LayoutInflater.from(getContext()).inflate(R.layout.view_date_picker_spinner, null);

        builder.setView(view);

        final DatePicker datePicker = (DatePicker) view;
        datePicker.setMinDate(new Date().getTime());

        if (initialDate != null) {
            final Calendar calendar = Calendar.getInstance();
            calendar.setTime(initialDate);

            datePicker.updateDate(
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
            );
        }

        int daySpinnerId = Resources.getSystem().getIdentifier("day", "id", "android");
        if (daySpinnerId != 0) {
            View dayView = view.findViewById(daySpinnerId);
            if (dayView != null) {
                dayView.setVisibility(View.GONE);
            }
        }

        builder.setPositiveButton(R.string.standard_ok_text, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (onDateSetListener != null) {
                    datePicker.clearFocus();
                    onDateSetListener.onDateSet(
                            datePicker,
                            datePicker.getYear(),
                            datePicker.getMonth(),
                            datePicker.getDayOfMonth()
                    );
                }
            }
        });

        builder.setNegativeButton(R.string.settings_cancel_button, null);

        return builder.create();
    }

    public void setInitialDate(Date initialDate) {
        this.initialDate = initialDate;
    }

    public void setOnDateSetListener(DatePickerDialog.OnDateSetListener onDateSetListener) {
        this.onDateSetListener = onDateSetListener;
    }
}
