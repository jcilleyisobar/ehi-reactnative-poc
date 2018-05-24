package com.ehi.enterprise.android.ui.location.widgets;

import android.util.Pair;

import com.ehi.enterprise.android.models.location.solr.EHISolrLocationValidity;
import com.ehi.enterprise.android.utils.reactor_extensions.viewstate.ReactorPropertyChangedListener;
import com.ehi.enterprise.android.utils.reactor_extensions.viewstate.ReactorViewState;

import java.util.Date;

import io.dwak.reactor.ReactorVar;
import io.dwak.reactor.unbinder.annotation.AutoUnbindAll;

@AutoUnbindAll
public class LocationDetailsConflictMessageViewState extends ReactorViewState {
    ReactorVar<String> mTitle;
    private ReactorPropertyChangedListener<String> mTitleChangedListener;
    ReactorVar<Pair<Date, EHISolrLocationValidity>> mSubtitle;
    private ReactorPropertyChangedListener<Pair<Date, EHISolrLocationValidity>> mSubtitleChangedListener;
    ReactorVar<Pair<Date, EHISolrLocationValidity>> mExtraSubtitle;
    private ReactorPropertyChangedListener<Pair<Date, EHISolrLocationValidity>> mExtraSubtitleChangedListener;

    public ReactorVar<String> title(){
        if(mTitle == null) mTitle = new ReactorVar<String>(){
            @Override
            public void setValue(final String value) {
                super.setValue(value);
                if(mTitleChangedListener != null){
                    mTitleChangedListener.onPropertyChanged(value);
                }
            }
        };

        return mTitle;
    }

    public ReactorVar<Pair<Date, EHISolrLocationValidity>> subtitle(){
        if(mSubtitle == null) mSubtitle = new ReactorVar<Pair<Date, EHISolrLocationValidity>>(){
            @Override
            public void setValue(final Pair<Date, EHISolrLocationValidity> value) {
                super.setValue(value);
                if(mSubtitleChangedListener != null){
                    mSubtitleChangedListener.onPropertyChanged(value);
                }
            }
        };

        return mSubtitle;
    }

    public ReactorVar<Pair<Date, EHISolrLocationValidity>> extraSubtitle(){
        if(mExtraSubtitle == null) mExtraSubtitle = new ReactorVar<Pair<Date, EHISolrLocationValidity>>(){
            @Override
            public void setValue(final Pair<Date, EHISolrLocationValidity> value) {
                super.setValue(value);
                if(mExtraSubtitleChangedListener != null){
                    mExtraSubtitleChangedListener.onPropertyChanged(value);
                }
            }
        };

        return mExtraSubtitle;
    }

    @Override
    public void unbindDependency() {
        super.unbindDependency();
        mTitleChangedListener = null;
        mSubtitleChangedListener = null;
        mExtraSubtitleChangedListener = null;
    }
}