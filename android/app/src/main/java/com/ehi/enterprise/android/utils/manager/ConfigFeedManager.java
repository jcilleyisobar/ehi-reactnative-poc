package com.ehi.enterprise.android.utils.manager;

import android.content.Context;
import android.support.annotation.NonNull;

import com.ehi.enterprise.android.models.support.EHIConfigFeed;

public class ConfigFeedManager extends BaseDataManager {

    private static final String CONFIG_FEED_MANAGER_NAME = "CONFIG_FEED_MANAGER_NAME";
    private static final String CONFIG_FEED = "ehi.CONFIG_FEED";

    private static ConfigFeedManager sManager;

    private ConfigFeedManager() {
    }

    @Override
    protected String getSharedPreferencesName() {
        return CONFIG_FEED_MANAGER_NAME;
    }

    @NonNull
    public static ConfigFeedManager getInstance() {
        if (sManager == null) {
            sManager = new ConfigFeedManager();
        }
        return sManager;
    }

    @Override
    public void initialize(@NonNull Context context) {
        super.initialize(context);
        sManager = this;
    }

    public void saveConfigFeed(EHIConfigFeed feed) {
        set(CONFIG_FEED, feed);
    }

    public EHIConfigFeed getConfigFeed() {
        return getEhiModel(CONFIG_FEED, EHIConfigFeed.class);
    }
}