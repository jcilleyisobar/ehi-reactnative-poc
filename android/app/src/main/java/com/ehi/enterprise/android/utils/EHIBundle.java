package com.ehi.enterprise.android.utils;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.ehi.enterprise.android.models.EHIModel;
import com.ehi.enterprise.android.utils.manager.DataPassManager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class EHIBundle {

    private Bundle mBundle;

    public EHIBundle() {
        mBundle = new Bundle();
    }

    public EHIBundle(Bundle bundle) {
        mBundle = bundle;
    }

    private Bundle getBundle() {
        return mBundle;
    }

    public boolean containsKey(String key) {
        return mBundle != null && (DataPassManager.getInstance().containsKey(key) || mBundle.containsKey(key));
    }

    public static EHIBundle fromBundle(@NonNull Bundle bundle) {
        return new EHIBundle(bundle);
    }

    @Nullable
    public Serializable getSerializable(@NonNull String key) {
        return mBundle.getSerializable(key);
    }

    @Nullable
    public Parcelable getParcelable(@NonNull String key) {
        return mBundle.getParcelable(key);
    }

    @Nullable
    public Bundle getBundle(@NonNull String key) {
        return mBundle.getBundle(key);
    }

    @Nullable
    public String getString(@NonNull final String key) {
        return mBundle.getString(key);
    }

    public int getInt(@NonNull final String key) {
        return mBundle.getInt(key);
    }

    public int getInt(@NonNull final String key, int defaultValue) {
        return mBundle.getInt(key, defaultValue);
    }

    public long getLong(@NonNull final String key) {
        return mBundle.getLong(key);
    }

    public long getLong(@NonNull final String key, long defaultValue) {
        return mBundle.getLong(key, defaultValue);
    }

    public double getDouble(@NonNull final String key) {
        return mBundle.getDouble(key);
    }

    public double getDouble(@NonNull final String key, double defaultValue) {
        return mBundle.getDouble(key, defaultValue);
    }

    public boolean getBoolean(@NonNull final String key) {
        return mBundle.getBoolean(key);
    }

    public boolean getBoolean(@NonNull final String key, boolean defaultValue) {
        return mBundle.getBoolean(key, defaultValue);
    }

    @Nullable
    public ArrayList getStringArrayList(@NonNull String key) {
        return mBundle.getStringArrayList(key);
    }

    @Nullable
    public ArrayList getIntegerArrayList(@NonNull String key) {
        return mBundle.getIntegerArrayList(key);
    }

    @Nullable
    public <T> T getEHIModel(@NonNull final String key, @NonNull Type type) {
        T fromDiskCache = DataPassManager.getInstance().fetchDataObject(key, type);
        if (fromDiskCache != null) {
            return fromDiskCache;
        }

        Gson gson = BaseAppUtils.getDefaultGson();
        final String serializedValue = mBundle.getString(key, null);

        T deserialzedObject = null;
        if (serializedValue != null) {
            deserialzedObject = gson.fromJson(serializedValue, type);
        }

        return deserialzedObject;
    }

    @Nullable
    public <T> T getEHIModel(@NonNull final String key, @NonNull TypeToken typeToken) {
        T fromDiskCache = DataPassManager.getInstance().fetchDataObject(key, typeToken);
        if (fromDiskCache != null) {
            return fromDiskCache;
        }

        Gson gson = BaseAppUtils.getDefaultGson();
        final String serializedValue = mBundle.getString(key, null);

        T deserialzedObject = null;
        if (serializedValue != null) {
            deserialzedObject = gson.fromJson(serializedValue, typeToken.getType());
        }

        return deserialzedObject;
    }

    public static class Builder {
        private EHIBundle mEHIBundle = new EHIBundle();

        public Builder putSerializable(@NonNull String key, @NonNull Serializable serializable) {
            mEHIBundle.getBundle().putSerializable(key, serializable);
            return this;
        }

        public Builder putParcelable(@NonNull String key, @NonNull Parcelable parcelable) {
            mEHIBundle.getBundle().putParcelable(key, parcelable);
            return this;
        }

        public Builder putBundle(@NonNull String key, @NonNull Bundle bundle) {
            mEHIBundle.getBundle().putBundle(key, bundle);
            return this;
        }

        public Builder putString(@NonNull String key, @NonNull String value) {
            mEHIBundle.getBundle().putString(key, value);
            return this;
        }

        public Builder putInt(@NonNull String key, int value) {
            mEHIBundle.getBundle().putInt(key, value);
            return this;
        }

        public Builder putLong(@NonNull String key, long value) {
            mEHIBundle.getBundle().putLong(key, value);
            return this;
        }

        public Builder putDouble(@NonNull String key, double value) {
            mEHIBundle.getBundle().putDouble(key, value);
            return this;
        }

        public Builder putBoolean(@NonNull String key, boolean value) {
            mEHIBundle.getBundle().putBoolean(key, value);
            return this;
        }

        public Builder putStringArrayList(@NonNull String key, @NonNull ArrayList<String> arrayList) {
            mEHIBundle.getBundle().putStringArrayList(key, arrayList);
            return this;
        }

        public Builder putIntegerArrayList(@NonNull String key, @NonNull ArrayList<Integer> arrayList) {
            mEHIBundle.getBundle().putIntegerArrayList(key, arrayList);
            return this;
        }

        public Builder putEHIModel(@NonNull String key, @NonNull Object value) {
            putEHIModel(key, value, false);
            return this;
        }

        public Builder putEHIModel(@NonNull String key, @NonNull Object value, boolean useDataPasser) {
            if (useDataPasser && value instanceof EHIModel) {
                DataPassManager.getInstance().addDataObject(key, (EHIModel) value);
            } else {
                Gson gson = BaseAppUtils.getDefaultGson();
                String serializedValue = gson.toJson(value);
                mEHIBundle.getBundle().putString(key, serializedValue);
            }
            return this;
        }

        public Bundle createBundle() {
            return mEHIBundle.getBundle();
        }
    }
}
