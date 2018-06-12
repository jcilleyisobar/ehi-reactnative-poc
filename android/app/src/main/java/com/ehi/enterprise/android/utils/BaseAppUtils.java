package com.ehi.enterprise.android.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityManagerCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.util.Pair;
import android.text.InputType;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.models.profile.EHICreditCard;
import com.ehi.enterprise.android.network.requests.reservation.ISO8601DateTypeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class BaseAppUtils {

    private static final String TAG = "BaseAppUtils";
    private static Gson sGson;

    public static int getCardIcon(String number) {
        if (number.length() > 1) {
            if (number.substring(0, 1).contains("4")) {
                return R.drawable.creditcard_01_visa;
            }
            if (number.substring(0, 1).contains("5")) {
                return R.drawable.creditcard_02_mastercard;
            }
            if (number.substring(0, 2).contains("37")) {
                return R.drawable.creditcard_03_amex;
            }

        }
        return R.drawable.icon_card_default;
    }

    public static int getCardIconByType(String type) {
        if (type != null) {
            switch (type) {
                case EHICreditCard.VISA:
                    return R.drawable.creditcard_01_visa;
                case EHICreditCard.MASTER_CARD:
                    return R.drawable.creditcard_02_mastercard;
                case EHICreditCard.AMEX:
                case EHICreditCard.AMERICAN_EXPRESS:
                    return R.drawable.creditcard_03_amex;
            }
        }
        return R.drawable.icon_card_default;
    }

    public static void showKeyboardForView(EditText editText) {
        editText.requestFocus();
        InputMethodManager imm = (InputMethodManager) editText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
    }

    public static void hideKeyboard(FragmentActivity activity) {
        InputMethodManager mgr = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(activity.getWindow().getDecorView().getWindowToken(), 0);
    }

    public static void hidePassword(EditText editText, Typeface typeface, boolean hidePassword) {
        if (hidePassword) {
            editText.setInputType(InputType.TYPE_CLASS_TEXT |
                    InputType.TYPE_TEXT_VARIATION_PASSWORD);
            editText.setTypeface(typeface);
        } else {
            editText.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            editText.setTypeface(typeface);
        }
        editText.setSelection(editText.getText().toString().length());
    }

    public static Gson getDefaultGson() {
        if (sGson == null) {
            sGson = new GsonBuilder().registerTypeAdapter(Date.class, new ISO8601DateTypeAdapter()).create();
        }
        return sGson;
    }

    public static Date getLinuxEpochTime() {
        Date epoch = new Date(0);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(epoch);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        return calendar.getTime();
    }

    /**
     * Return a {@link Pair} containing the default width/height measure specs for our car images
     */
    public static Pair<Integer, Integer> getDefaultCarImageMeasureSpec(@NonNull Context context) {
        int viewWidth = DisplayUtils.getScreenWidth(context);
        int viewHeight = (int) context.getResources().getDimension(R.dimen.class_select_list_item_height);
        int widthSpec = View.MeasureSpec.makeMeasureSpec(viewWidth, View.MeasureSpec.EXACTLY);
        int heightSpec = View.MeasureSpec.makeMeasureSpec(viewHeight, View.MeasureSpec.EXACTLY);

        return new Pair<>(widthSpec, heightSpec);
    }

    public static <I, J> boolean contains(I val, List<J> list, CompareTwo<I, J> compareTwo) {

        for (int i = 0; i < list.size(); i++) {
            if (compareTwo.equals(val, list.get(i))) {
                return true;
            }
        }

        return false;
    }

    /**
     * @param val        Value to be found
     * @param list       List of objects containing possible field
     * @param compareTwo comparing function
     * @param <I>        type of value
     * @param <J>        type of list of objects
     * @return
     */
    public static <I, J> int indexOf(I val, List<J> list, CompareTwo<I, J> compareTwo) {

        for (int i = 0; i < list.size(); i++) {
            if (compareTwo.equals(val, list.get(i))) {
                return i;
            }
        }

        return -1;
    }

    /**
     * Checks if the device has low total memory.
     * If the device is on API 19 or above, it uses {@link ActivityManagerCompat#isLowRamDevice(ActivityManager)} otherwise it calculates from {@link ActivityManager.MemoryInfo}
     **/
    public static boolean isLowMemoryDevice(Activity activity) {
        ActivityManager activityManager = (ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo output = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(output);
        return ActivityManagerCompat.isLowRamDevice(activityManager) || output.totalMem / 1024 / 1024 < 512;
    }

    public interface CompareTwo<I, J> {
        boolean equals(I first, J second);
    }

}
