package com.ehi.enterprise.android.ui.location.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.View;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.databinding.LocationDetailsConflictMessageViewBinding;
import com.ehi.enterprise.android.models.location.solr.EHISolrLocationValidity;
import com.ehi.enterprise.android.ui.view.DataBindingViewModelView;
import com.ehi.enterprise.android.ui.viewmodel.ManagersAccessViewModel;
import com.isobar.android.viewmodel.ViewModel;

import java.util.Date;

import io.dwak.reactor.ReactorComputation;
import io.dwak.reactor.ReactorVar;
import io.dwak.reactor.interfaces.ReactorComputationFunction;
import io.dwak.reactorbinding.Preconditions;

@ViewModel(ManagersAccessViewModel.class)
public class LocationDetailsConflictMessageView  extends DataBindingViewModelView<ManagersAccessViewModel, LocationDetailsConflictMessageViewBinding> {

    public LocationDetailsConflictMessageView(Context context) {
        this(context, null);
    }

    public LocationDetailsConflictMessageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LocationDetailsConflictMessageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        createViewBinding(R.layout.v_location_details_conflict_message);
    }

    private void setTitleText(String text) {
        getViewBinding().title.setText(text);
        getViewBinding().title.setVisibility(View.VISIBLE);
    }

    private void setSubtitleText(Pair<Date, EHISolrLocationValidity> data) {
        getViewBinding().subtitle.setFormattedText(data.first, data.second);
        getViewBinding().subtitle.setVisibility(View.VISIBLE);
    }

    private void setExtraSubtitleText(Pair<Date, EHISolrLocationValidity> data) {
        getViewBinding().extraSubtitle.setFormattedText(data.first, data.second);;
        getViewBinding().extraSubtitle.setVisibility(View.VISIBLE);

    }

    public static ReactorComputationFunction title(final ReactorVar<? extends String> source, final LocationDetailsConflictMessageView target) {
        return new ReactorComputationFunction() {
            @Override
            public void react(ReactorComputation reactorComputation) {
                if (Preconditions.checkNotNull(source.getValue(), target)) {
                    target.setTitleText(source.getValue());
                }
            }
        };
    }

    public static ReactorComputationFunction subtitle(final ReactorVar<Pair<Date, EHISolrLocationValidity>> source, final LocationDetailsConflictMessageView target) {
        return new ReactorComputationFunction() {
            @Override
            public void react(ReactorComputation reactorComputation) {
                if (Preconditions.checkNotNull(source.getValue(), target)) {
                    target.setSubtitleText(source.getValue());
                }
            }
        };
    }

    public static ReactorComputationFunction extraSubtitle(final ReactorVar<Pair<Date, EHISolrLocationValidity>> source, final LocationDetailsConflictMessageView target) {
        return new ReactorComputationFunction() {
            @Override
            public void react(ReactorComputation reactorComputation) {
                if (Preconditions.checkNotNull(source.getValue(), target)) {
                    target.setExtraSubtitleText(source.getValue());
                }
            }
        };
    }

    public static ReactorComputationFunction[] bind(final LocationDetailsConflictMessageViewState source, final LocationDetailsConflictMessageView target) {
        ReactorComputationFunction[] functions = new ReactorComputationFunction[3];
        functions[0] = title(source.title(), target);
        functions[1] = subtitle(source.subtitle(), target);
        functions[2] = extraSubtitle(source.extraSubtitle(), target);
        return functions;
    }
}
