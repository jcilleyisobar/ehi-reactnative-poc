package com.ehi.enterprise.android.utils.manager;

import android.content.Context;
import android.support.v4.BuildConfig;
import android.support.v4.util.LruCache;

import com.ehi.enterprise.android.utils.DLog;
import com.google.gson.Gson;
import com.jakewharton.disklrucache.DiskLruCache;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class DiskCacheManager {
    private static final String TAG = "DiskCacheManager";
    private static final int DEFAULT_CACHE_SIZE = 1024 * 1024 * 10;
	private static final int DEFAULT_MEM_CACHE_SIZE = 20;
	private static final String HASH_ALGORITHM = "MD5";
	private static final String STRING_ENCODING = "UTF-8";
	private static final int VALUE_COUNT = 1;

	private final Gson mGson;

	private DiskLruCache mDiskCache;
	private final LruCache mMemCache;

	private final File mCacheDirectory;

	static DiskCacheManager sManager;

	public static DiskCacheManager getInstance() {
		if (sManager == null) {
			sManager = new DiskCacheManager();
		}
		return sManager;
	}

	private DiskCacheManager() {
		mCacheDirectory = null;
		mMemCache = null;
		mGson = null;
	}

	public DiskCacheManager(Context context) {
		mCacheDirectory = new File(context.getCacheDir() + "/objectcache");
		if (!mCacheDirectory.exists()) {
			mCacheDirectory.mkdirs();
		}
		mMemCache = new LruCache(DEFAULT_MEM_CACHE_SIZE);
		mGson = new Gson();
		open();
	}

	private void open() {
		try {
			mDiskCache = DiskLruCache.open(mCacheDirectory, BuildConfig.VERSION_CODE, VALUE_COUNT, DEFAULT_CACHE_SIZE);
		} catch (IOException e) {
			Thread.dumpStack();
		}
	}

	public void clear() throws IOException {
		mDiskCache.delete();
		mMemCache.evictAll();
		open();
	}

	public boolean delete(String key) throws IOException {
		mMemCache.remove(key);
		return mDiskCache.remove(getHashOf(key));
	}

	public <T> T get(String key, Type type) throws IOException {
        DLog.d(TAG, "get() called with: " + "key = [" + key + "], type = [" + type + "]");
        T ob = (T) mMemCache.get(key);
		if (ob != null) {
            DLog.d(TAG, "get: cache HIT");
            return ob;
		}
		DiskLruCache.Snapshot snapshot = mDiskCache.get(getHashOf(key));
		if (snapshot != null) {
			String value = snapshot.getString(0);
			ob = mGson.fromJson(value, type);
		}
		return ob;
	}

	public void put(String key, Object object) throws IOException {
		DLog.d(TAG, "put() called with: " + "key = [" + key + "], object = [" + object + "]");
		mMemCache.put(key, object);
		DiskLruCache.Editor editor = null;
		try {
			editor = mDiskCache.edit(getHashOf(key));
			if (editor == null) {
				return;
			}
			if (writeValueToCache(mGson.toJson(object), editor)) {
				mDiskCache.flush();
				editor.commit();
			}
			else {
				editor.abort();
			}
		} catch (IOException e) {
			if (editor != null) {
				editor.abort();
			}

			throw e;
		}

	}

	protected boolean writeValueToCache(String value, DiskLruCache.Editor editor) throws IOException {
		OutputStream outputStream = null;
		try {
			outputStream = new BufferedOutputStream(editor.newOutputStream(0));
			outputStream.write(value.getBytes(STRING_ENCODING));
		} finally {
			if (outputStream != null) {
				outputStream.close();
			}
		}

		return true;
	}

	protected String getHashOf(String string) throws UnsupportedEncodingException {
		try {
			MessageDigest messageDigest;
			messageDigest = MessageDigest.getInstance(HASH_ALGORITHM);
			messageDigest.update(string.getBytes(STRING_ENCODING));
			byte[] digest = messageDigest.digest();
			BigInteger bigInt = new BigInteger(1, digest);

			return bigInt.toString(16);
		} catch (NoSuchAlgorithmException e) {

			return string;
		}
	}


}
