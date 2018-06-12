package com.ehi.enterprise.android.utils.manager;

import android.content.Context;
import android.support.annotation.NonNull;

import com.ehi.enterprise.android.models.EHIModel;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

public class DataPassManager extends BaseDataManager {

	private static final String TAG = "DataPassManager";

	private static final String DATE_PASS_MANAGER = "DATE_PASS_MANAGER";

	private static DataPassManager mManager;

	private DataPassManager() {
	}

	@Override
	protected String getSharedPreferencesName() {
		return DATE_PASS_MANAGER;
	}

	@Override
	public void initialize(@NonNull Context context) {
		super.initialize(context);
		mManager = this;
	}

	@NonNull
	public static DataPassManager getInstance() {
		if (mManager == null) {
			mManager = new DataPassManager();
		}
		return mManager;
	}

	public void addDataObject(String key, EHIModel value) {
		set(key, value);
	}

	public <T> T fetchDataObject(String key, Type type) {
		T object = getEhiModel(key, type);
//		remove(key);
		return object;
	}

	public <T> T fetchDataObject(String key, TypeToken typeToken) {
		T object = getEhiModel(key, typeToken.getType());
//		remove(key);
		return object;
	}

}
