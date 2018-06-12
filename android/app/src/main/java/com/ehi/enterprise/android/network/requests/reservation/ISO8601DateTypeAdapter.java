package com.ehi.enterprise.android.network.requests.reservation;

import android.support.annotation.Nullable;

import com.ehi.enterprise.android.utils.DLog;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ISO8601DateTypeAdapter implements JsonDeserializer<Date>, JsonSerializer<Date> {

    private final static ThreadLocal<SimpleDateFormat> dateFormat = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
        }
    };
    private static final String TAG = "ISO8601DateTypeAdapter";

    @Override
    public Date deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        Date parsedDate = null;
        try {
            parsedDate = dateFormat.get().parse(jsonElement.getAsString());
        } catch (ParseException e) {
        }

        return parsedDate;
    }

    @Override
    public JsonElement serialize(Date src, Type typeOfSrc, JsonSerializationContext context) {
        return context.serialize(dateFormat.get().format(src));
    }

    public static String fromDateObject(Date date) {
        return dateFormat.get().format(date);
    }

    @Nullable
    public static Date fromString(String date) {
        try {
            return dateFormat.get().parse(date);
        } catch (ParseException e) {
            DLog.e(TAG, "", e);
            return null;
        }
    }

}
