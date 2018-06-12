package com.ehi.enterprise.android.ui.reservation.widget;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.support.v4.content.res.ResourcesCompat;
import android.text.Editable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.databinding.AdditionalInfoChildViewBinding;
import com.ehi.enterprise.android.models.reservation.EHIAdditionalInformation;
import com.ehi.enterprise.android.models.reservation.EHISupportedValues;
import com.ehi.enterprise.android.ui.viewholder.DataBindingViewHolder;
import com.ehi.enterprise.android.utils.CustomTypefaceSpan;
import com.ehi.enterprise.android.utils.DLog;
import com.ehi.enterprise.android.utils.TimeUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class AdditionalInfoViewHolder extends DataBindingViewHolder<AdditionalInfoChildViewBinding>{

    private static final String TAG = "AdditionalInfoViewHolder";

    private static final String TYPE_DROP_DOWN = "dropdown";
    private static final String TYPE_DATE = "date";

    private EHIAdditionalInformation mEHIAdditionalInformation;
    private Calendar mCalendar;
    private boolean mDateSet = false;

    private OnDataChangedListener onDataChangedListener;

    private DatePickerDialog.OnDateSetListener mDateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int year, int month, int day) {
            mCalendar.set(year, month, day);
            getViewBinding().datePicker.setText(TimeUtils.getMediumDate(getViewBinding().getRoot().getContext(), mCalendar.getTime()));
            mDateSet = true;
            notifyOnDataChanged();
        }
    };

    private final DialogInterface.OnClickListener mSpinnerCallback = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            getViewBinding().billingCodeSpinner.setValid(isValid());
            notifyOnDataChanged();
        }
    };

    private final TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            //
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            //
        }

        @Override
        public void afterTextChanged(Editable s) {
            notifyOnDataChanged();
        }
    };

    public static AdditionalInfoViewHolder create(ViewGroup parent, EHIAdditionalInformation information){
        return new AdditionalInfoViewHolder(
                (AdditionalInfoChildViewBinding) createViewBinding(
                        parent.getContext(),
                        R.layout.v_additional_information_child_view,
                        parent),
                information);
    }

    public AdditionalInfoViewHolder(AdditionalInfoChildViewBinding binding, EHIAdditionalInformation information) {
        super(binding);
        mDateSet = false;
        mEHIAdditionalInformation = information;
        mCalendar = Calendar.getInstance();
        String title = information.getName();
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
        SpannableString spannableString = new SpannableString(title);
        spannableString.setSpan(new CustomTypefaceSpan("sans-serif",
                        ResourcesCompat.getFont(itemView.getContext(), R.font.source_sans_bold)),
                0, title.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        spannableStringBuilder.append(spannableString);

        if (information.isRequired()){
            spannableStringBuilder.append(" *");
        }

        getViewBinding().title.setText(spannableStringBuilder);

        if (information.getHelperText() != null) {
            getViewBinding().helperText.setText(information.getHelperText());
        } else {
            getViewBinding().helperText.setVisibility(View.GONE);
        }


        if (information.getType().equalsIgnoreCase(TYPE_DROP_DOWN)) {

            List<CharSequence> list = new ArrayList<>();
            EHISupportedValues selection;
            int selectedIndex = -1;
            for (int i = 0; i < information.getSupportedValues().size(); i++) {
                selection = information.getSupportedValues().get(i);
                list.add(selection.getName());
                if (mEHIAdditionalInformation.getValue() != null) {
                    if (mEHIAdditionalInformation.getValue().equalsIgnoreCase(selection.getValue())) {
                        selectedIndex = i;
                    }
                }
            }
            getViewBinding().billingCodeSpinner.populateView(list, selectedIndex, getViewBinding().getRoot().getResources().getString(R.string.reservation_review_additional_info_section_title));
            getViewBinding().billingCodeSpinner.setVisibility(View.VISIBLE);

            getViewBinding().billingCodeSpinner.setCallback(mSpinnerCallback);
        } else if (information.getType().equalsIgnoreCase(TYPE_DATE)) {

            getViewBinding().datePicker.setVisibility(View.VISIBLE);
            getViewBinding().datePicker.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new DatePickerDialog(getViewBinding().getRoot().getContext(), mDateListener, mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH))
                            .show();
                }
            });
            String readableDateFormat = ((SimpleDateFormat) DateFormat.getDateFormat(getViewBinding().getRoot().getContext())).toLocalizedPattern().toUpperCase();
            readableDateFormat = readableDateFormat.replace("Y", "YY");
            readableDateFormat = readableDateFormat.replace("M", "MM");
            readableDateFormat = readableDateFormat.replace("D", "DD");
            getViewBinding().datePicker.setText(readableDateFormat);
            if (mEHIAdditionalInformation.getValue() != null) {

                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
                Date date = null;
                try {
                    date = format.parse(mEHIAdditionalInformation.getValue());
                } catch (ParseException e) {
                    DLog.w(TAG, e);
                }
                if (date != null) {
                    mCalendar = Calendar.getInstance();
                    mCalendar.setTime(date);
                    mDateListener.onDateSet(null,
                            mCalendar.get(Calendar.YEAR),
                            mCalendar.get(Calendar.MONTH),
                            mCalendar.get(Calendar.DAY_OF_MONTH));
                }
            }
        } else {
            getViewBinding().inputField.setVisibility(View.VISIBLE);
            if (mEHIAdditionalInformation.getValue() != null) {
                getViewBinding().inputField.setText(mEHIAdditionalInformation.getValue());
            }
            getViewBinding().inputField.addTextChangedListener(textWatcher);
        }

    }

    public boolean isValid() {
        if (!mEHIAdditionalInformation.isRequired()) {
            return true;
        }

        cleanUpErrorState();

        if (mEHIAdditionalInformation.getType().equalsIgnoreCase(TYPE_DATE)) {
            if (!mDateSet) {
                getViewBinding().datePicker.setBackground(getViewBinding().getRoot().getResources().getDrawable(R.drawable.edit_text_red_border));
                return false;
            }
            return true;
        }

        if (mEHIAdditionalInformation.getType().equalsIgnoreCase(TYPE_DROP_DOWN)) {
            if (getInformation().getValue().length() == 0) {
                getViewBinding().billingCodeSpinner.setValid(false);
                return false;
            }
            return true;
        }

        if (getViewBinding().inputField.getText().length() == 0) {
            getViewBinding().inputField.setBackgroundDrawable(getViewBinding().getRoot().getResources().getDrawable(R.drawable.edit_text_red_border));
            return false;
        }

        return true;
    }

    private void cleanUpErrorState() {
        if (mEHIAdditionalInformation.getType().equalsIgnoreCase(TYPE_DATE)) {
            getViewBinding().datePicker.setBackground(getViewBinding().getRoot().getResources().getDrawable(R.drawable.edit_text_white_border));
            return;
        }

        if (mEHIAdditionalInformation.getType().equalsIgnoreCase(TYPE_DROP_DOWN)) {
            getViewBinding().billingCodeSpinner.setValid(true);
            return;
        }

        getViewBinding().inputField.setBackgroundDrawable(getViewBinding().getRoot().getResources().getDrawable(R.drawable.edit_text_white_border));
    }

    public boolean isValidEntry() {
        if (!mEHIAdditionalInformation.isRequired()) {
            return true;
        }

        if (mEHIAdditionalInformation.getType().equalsIgnoreCase(TYPE_DATE)) {
            return mDateSet;
        }

        if (mEHIAdditionalInformation.getType().equalsIgnoreCase(TYPE_DROP_DOWN)) {
            return getInformation().getValue().length() != 0;
        }

        return getViewBinding().inputField.getText().length() != 0;
    }

    public EHIAdditionalInformation getInformation() {
        EHIAdditionalInformation info = new EHIAdditionalInformation();
        info.setId(mEHIAdditionalInformation.getId());
        if (mEHIAdditionalInformation.getType().equalsIgnoreCase(TYPE_DROP_DOWN)) {
            if (getViewBinding().billingCodeSpinner.getSelectedIndex() == -1) {
                info.setValue("");
            } else {
                info.setValue(
                        mEHIAdditionalInformation.getSupportedValues()
                                .get(getViewBinding().billingCodeSpinner.getSelectedIndex())
                                .getValue()
                );
            }
        } else if (mEHIAdditionalInformation.getType().equalsIgnoreCase(TYPE_DATE)) {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
            info.setValue(format.format(mCalendar.getTime()));
        } else { //type text
            info.setValue(getViewBinding().inputField.getText().toString());
        }
        return info;
    }

    public void setOnDataChangedListener(OnDataChangedListener onDataChangedListener) {
        this.onDataChangedListener = onDataChangedListener;
    }

    private void notifyOnDataChanged() {
        isValid();

        if (onDataChangedListener != null) {
            onDataChangedListener.onDataChanged();
        }
    }

    public interface OnDataChangedListener {
        void onDataChanged();
    }

}
