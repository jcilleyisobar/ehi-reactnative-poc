package com.ehi.enterprise.android.utils.databinding.adapter;

import android.databinding.BindingAdapter;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.text.Editable;
import android.text.InputType;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.appsee.Appsee;
import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.ui.reservation.widget.RentalTermsConditionsView;
import com.ehi.enterprise.android.ui.widget.ItemAppliedView;
import com.ehi.enterprise.android.utils.CustomTypefaceSpan;
import com.ehi.enterprise.android.utils.IntentUtils;

import java.util.HashMap;
import java.util.Map;

public class AppAdapters {
    private static Map<String, Typeface> sCache = new HashMap<>();

    @BindingAdapter({"shareText"})
    public static void shareText(final TextView view, final boolean share) {
        if (share) {
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    IntentUtils.shareViaChooser(v.getContext(), view.getText().toString());
                }
            });
        }
    }

    @BindingAdapter({"paddingHorizontal"})
    public static void setHorizontalPadding(View view, float padding) {
        view.setPadding((int) padding,
                view.getPaddingTop(),
                (int) padding,
                view.getPaddingBottom());
    }

    @BindingAdapter({"paddingVertical"})
    public static void setVerticalPadding(View view, float padding) {
        view.setPadding(view.getPaddingLeft(),
                (int) padding,
                view.getPaddingRight(),
                (int) padding);
    }

    @BindingAdapter({"itemTitle"})
    public static void setItemTitle(ItemAppliedView itemAppliedView, String title) {
        itemAppliedView.setTitle(title);
    }

    @BindingAdapter({"sensitiveInformation"})
    public static void setSensitiveInformation(EditText editText, boolean blockAction) {
        if (blockAction) {
            editText.setInputType(editText.getInputType() | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
            editText.setCustomSelectionActionModeCallback(new ActionMode.Callback() {
                @Override
                public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                    return false;
                }

                @Override
                public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                    return false;
                }

                @Override
                public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                    return false;
                }

                @Override
                public void onDestroyActionMode(ActionMode mode) {

                }
            });
            Appsee.markViewAsSensitive(editText);
        }
    }

    @BindingAdapter({"cleanable"})
    public static void setCleanable(final EditText editText, boolean cleanable) {
        if (cleanable) {
            if (editText.length() > 0) {
                editText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icon_x_green, 0);
            } else {
                editText.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            }
            editText.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    final Drawable icon = editText.getCompoundDrawables()[2];
                    if (event.getAction() == MotionEvent.ACTION_UP && icon != null) {
                        if (event.getRawX() >= (editText.getRight() - editText.getPaddingRight() - icon.getBounds().width())) {
                            editText.setText("");
                            editText.requestFocus();
                            return true;
                        }
                    }
                    return false;
                }
            });

            editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (s.length() > 0) {
                        editText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icon_x_green, 0);
                    } else {
                        editText.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                    }
                }
            });
        }
    }

    @BindingAdapter({"allCaps"})
    public static void setAllCaps(RentalTermsConditionsView view, boolean allCaps) {
        view.setAllCaps(allCaps);
    }



    private static void appendToTextView(TextView view, SpannableString string, SymbolPosition position){
        SpannableStringBuilder bld = new SpannableStringBuilder();

        if (position.equals(SymbolPosition.AFTER)) {
            bld.append(view.getText())
                    .append(" ")
                    .append(string);
        } else {
            bld.append(string)
                    .append(" ")
                    .append(view.getText());
        }
        view.setText(bld);
    }

    @BindingAdapter({"footNote"})
    public static void appendFootNoteSymbol(TextView view, SymbolPosition position) {
        Typeface tf = ResourcesCompat.getFont(view.getContext(), R.font.source_sans_bold);
        SpannableString footNote = new SpannableString("§");
        footNote.setSpan(new CustomTypefaceSpan("", tf), 0, footNote.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        appendToTextView(view, footNote, position);

    }

    @BindingAdapter({"requiredField"})
    public static void setFieldAsNecessary(final TextView textView, SymbolPosition symbolPosition) {
        SpannableString addedSymbolSpan = new SpannableString("*");
        appendToTextView(textView, addedSymbolSpan, symbolPosition);
    }





    public enum SymbolPosition {BEFORE, AFTER}

}
