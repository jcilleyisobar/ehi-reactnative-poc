package com.ehi.enterprise.android.utils.manager;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.LruCache;

import com.ehi.enterprise.android.app.Settings;
import com.ehi.enterprise.android.models.EHIModel;
import com.ehi.enterprise.android.utils.BaseAppUtils;
import com.ehi.enterprise.android.utils.Crypto;
import com.ehi.enterprise.android.utils.DLog;
import com.ehi.enterprise.android.utils.EHITextUtils;

import java.io.IOException;
import java.lang.reflect.Type;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.Map;

/**
 * BaseDataManager provides the necessary implementation to read/write
 * from {@link SharedPreferences} with encryption using {@link Crypto}
 */
public abstract class BaseDataManager {
    public static final String TAG = BaseDataManager.class.getSimpleName();
    public static final int CACHE_MAX_SIZE = 50;
    protected Crypto mCrypto;
    protected Context mContext;
    protected SharedPreferences mSharedPreferences;
    private LruCache<String, Object> mMemCache;
    private boolean mInitialized;

    protected abstract String getSharedPreferencesName();

    public void initialize(@NonNull Context context) {
        DLog.d("Initializing " + getSharedPreferencesName() + " Manager");
        mContext = context;
        mSharedPreferences = mContext.getSharedPreferences(getSharedPreferencesName(), Activity.MODE_PRIVATE);
        mSharedPreferences.edit().commit();
        mMemCache = new LruCache<>(CACHE_MAX_SIZE);
        try {
            mCrypto = new Crypto(mContext);
        } catch (final NoSuchAlgorithmException | IOException e) {
            DLog.e(TAG, e.getLocalizedMessage());
        }
        mInitialized = true;
    }

    public boolean isInitialized() {
        return mInitialized;
    }

    protected Map<String, ?> getAll() {
        return mSharedPreferences.getAll();
    }

    protected void clear() {
        mSharedPreferences.edit().clear().apply();
        mMemCache.evictAll();
    }

    /**
     * Removes the key from {@link SharedPreferences}
     *
     * @param key Key to remove
     */
    protected void remove(final String key) {
        if (key == null) {
            return;
        }
        if (Settings.SHOW_LOGS) {
            DLog.v(TAG, "remove() called with " + "key = [" + key + "]");
            DLog.v(TAG + " " + getSharedPreferencesName(), "#remove: " + key);
        }
        mSharedPreferences.edit().remove(key).apply();
        mMemCache.remove(key);
    }

    protected <T extends EHIModel> void set(final String key, final Collection<T> value) {
        if (key == null) {
            return;
        }
        if (Settings.SHOW_LOGS)
            DLog.v(TAG, "set(String, Collection<T>) called with " + "key = [" + key + "], value = [" + value.size() + "]");
        mMemCache.put(key, value);

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                String serializedValue = BaseAppUtils.getDefaultGson().toJson(value);
                set(key, serializedValue);
                return null;
            }
        }.execute();
    }

    protected <T extends EHIModel> void set(final String key, final T value) {
        if (key == null) {
            return;
        }
        if (Settings.SHOW_LOGS)
            DLog.v(TAG, "set(String, T) called with " + "key = [" + key + "], value = [" + value + "]");
        mMemCache.put(key, value);

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                String serializedValue = BaseAppUtils.getDefaultGson().toJson(value);
                set(key, serializedValue);
                return null;
            }
        }.execute();
    }

    @Nullable
    protected <T> T getEhiModel(final String key, Type type) {
        if (Settings.SHOW_LOGS)
            DLog.v(TAG, "getEhiModel() called with " + "key = [" + key + "], type = [" + type + "]");
        if (key == null) {
            return null;
        }
        @SuppressWarnings("unchecked")
        T cacheValue = (T) mMemCache.get(key);
        if (cacheValue != null) {
            if (Settings.SHOW_LOGS) DLog.v(TAG, "HIT: getEhiModel() returned " + cacheValue);
            return cacheValue;
        }

        final T diskValue;
        String serializedValue = getString(key, null);
        if (EHITextUtils.isEmpty(serializedValue)) {
            diskValue = null;
        } else {
            diskValue = BaseAppUtils.getDefaultGson().fromJson(serializedValue, type);
            if (Settings.SHOW_LOGS)
                DLog.v(TAG, "PUT: key = [" + key + "], value = [" + diskValue + "]");
            if (diskValue != null) {
                mMemCache.put(key, diskValue);
            }
        }
        if (Settings.SHOW_LOGS) DLog.v(TAG, "MISS: getEhiModel() returned " + diskValue);
        return diskValue;
    }

    @Nullable
    protected <T> T getEhiModelNoCache(final String key, Type type) {
        if (Settings.SHOW_LOGS)
            DLog.v(TAG, "getEhiModel() called with " + "key = [" + key + "], type = [" + type + "]");
        if (key == null) {
            return null;
        }
        final T diskValue;
        String serializedValue = getString(key, null);
        if (serializedValue == null) {
            diskValue = null;
        } else {
            diskValue = BaseAppUtils.getDefaultGson().fromJson(serializedValue, type);
            if (Settings.SHOW_LOGS)
                DLog.v(TAG, "PUT: key = [" + key + "], value = [" + diskValue + "]");
            if (diskValue != null) {
                mMemCache.put(key, diskValue);
            }
        }
        if (Settings.SHOW_LOGS) DLog.v(TAG, "MISS: getEhiModel() returned " + diskValue);
        return diskValue;
    }

    /**
     * Gets a {@link String} for the given {@link String} key
     *
     * @param key {@link String} key
     * @param def {@link String} default string to return if key has no value
     * @return the {@link String} value for the given key, or returns the passed in default
     */
    protected String getString(final String key, final String def) {
        String decrypted = def;

        try {
            final String encrypted = mSharedPreferences.getString(key, null);

            if (encrypted != null) {
                decrypted = mCrypto.decrypt(encrypted);
            }
        } catch (final Exception ignored) {
        }

        if (Settings.SHOW_LOGS)
            DLog.v(TAG + " " + getSharedPreferencesName(), "#getString: " + key + "\nvalue: " + decrypted);
        return decrypted;
    }

    /**
     * Gets a boolean for the given {@link String} key
     *
     * @param key {@link String} key
     * @param def default boolean to return if key has no value
     * @return the boolean value for the given key, or returns the passed in default
     */
    protected boolean getBoolean(final String key, final boolean def) {
        // Convert to strings to we aren't sending one bit
        boolean decrypted = def;

        try {
            final String encrypted = mSharedPreferences.getString(key, null);

            if (encrypted != null) {
                final String temp = mCrypto.decrypt(encrypted);
                decrypted = Boolean.valueOf(temp);
            }
        } catch (final Exception ignored) {
        }

        if (Settings.SHOW_LOGS)
            DLog.v(TAG + " " + getSharedPreferencesName(), "#getBoolean: " + key + "\nvalue: " + decrypted);
        return decrypted;
    }

    protected int getInt(final String key, final int def) {
        if (Settings.SHOW_LOGS) DLog.v(TAG + " " + getSharedPreferencesName(), "#getInt: " + key);
        return mSharedPreferences.getInt(key, def);
    }

    protected long getLong(final String key, final long def) {
        if (Settings.SHOW_LOGS) DLog.v(TAG + " " + getSharedPreferencesName(), "#getLong: " + key);
        return mSharedPreferences.getLong(key, def);
    }

    /**
     * Sets a String value to a key in {@link SharedPreferences}
     *
     * @param key   key to add
     * @param value value to apply to provided key
     */
    protected void set(@NonNull final String key, @NonNull final String value) {
        if (Settings.SHOW_LOGS)
            DLog.v(TAG + " " + getSharedPreferencesName(), "#set: " + key + "\n value: " + value);
        mSharedPreferences.edit().putString(key, mCrypto.encrypt(value)).apply();
    }

    /**
     * Sets a boolean value to a key in {@link SharedPreferences}
     *
     * @param key   key to add
     * @param value value to apply to provided key
     */
    protected void set(@NonNull final String key, final boolean value) {
        if (Settings.SHOW_LOGS)
            DLog.v(TAG + " " + getSharedPreferencesName(), "#set: " + key + "\n value: " + value);
        mSharedPreferences.edit().putString(key, mCrypto.encrypt(String.valueOf(value))).apply();
    }

    /**
     * Sets a boolean value to a key in {@link SharedPreferences}
     *
     * @param key   key to add
     * @param value value to apply to provided key
     */
    protected void set(@NonNull final String key, final int value) {
        if (Settings.SHOW_LOGS)
            DLog.v(TAG + " " + getSharedPreferencesName(), "#set: " + key + "\n value: " + value);
        mSharedPreferences.edit().putInt(key, value).apply();
    }

    /**
     * Sets a long value to a key in {@link SharedPreferences}
     *
     * @param key   key to add
     * @param value value to apply to provided key
     */
    protected void set(@NonNull final String key, final long value) {
        if (Settings.SHOW_LOGS)
            DLog.v(TAG + " " + getSharedPreferencesName(), "#set: " + key + "\n value: " + value);
        mSharedPreferences.edit().putLong(key, value).apply();
    }

    protected String decrypt(@NonNull String encryptedString) {
        return mCrypto.decrypt(encryptedString);
    }

    protected final Context getContext() {
        return mContext;
    }

    public boolean containsKey(String key) {
        return mSharedPreferences.contains(key)
                || mMemCache.get(key) != null;
    }
}
