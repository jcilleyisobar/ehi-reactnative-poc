package com.ehi.enterprise.android.ui.location.widgets.components;


import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.content.res.ResourcesCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.view.View;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.app.EHIStringToken;
import com.ehi.enterprise.android.databinding.LocationMapDetailsViewBinding;
import com.ehi.enterprise.android.models.location.solr.EHISolrLocation;
import com.ehi.enterprise.android.ui.view.DataBindingViewModelView;
import com.isobar.android.tokenizedstring.TokenizedString;
import com.isobar.android.viewmodel.ViewModel;

import java.util.Date;

import io.dwak.reactorbinding.view.ReactorView;
import io.dwak.reactorbinding.widget.ReactorImageView;
import io.dwak.reactorbinding.widget.ReactorTextView;

@ViewModel(LocationMapBubbleDetailsViewModel.class)
public class LocationMapBubbleDetailsView extends DataBindingViewModelView<LocationMapBubbleDetailsViewModel, LocationMapDetailsViewBinding> {

    private MapDetailsListener mMapDetailsListener;
    private final OnClickListener mOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (getViewBinding().rightContainer == v) {
                mMapDetailsListener.onLocationSelectedClick();
            } else if (getViewBinding().headerTitle == v) {
                mMapDetailsListener.onMoreInfoMapDetailsClick();
            }
        }
    };

    private ClickableSpan mClickableSpan = new ClickableSpan() {
        @Override
        public void onClick(View widget) {
            mMapDetailsListener.onAfterHoursClick();
        }
    };

    public LocationMapBubbleDetailsView(Context context) {
        this(context, null);
    }

    public LocationMapBubbleDetailsView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LocationMapBubbleDetailsView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        createViewBinding(R.layout.v_location_map_bubble_details);
        init();
    }

    private void init() {
        getViewBinding().rightContainer.setOnClickListener(mOnClickListener);
        getViewBinding().headerTitle.setOnClickListener(mOnClickListener);
    }

    @Override
    protected void initDependencies() {
        super.initDependencies();
        bind(ReactorTextView.text(getViewModel().headerTextView.text(), getViewBinding().headerTitle));
        bind(ReactorTextView.text(getViewModel().subheaderTextView.text(), getViewBinding().subheaderTitle));
        bind(ReactorView.visibility(getViewModel().flexibleTravelView.visibility(), getViewBinding().flexibleTravelText));
        bind(ReactorView.background(getViewModel().selectButtonBackground.background(), getViewBinding().rightContainer));
        bind(ReactorImageView.imageDrawable(getViewModel().selectButtonImage.imageDrawable(), getViewBinding().arrowView));
    }

    public void setMapDetailsListener(MapDetailsListener listener) {
        mMapDetailsListener = listener;
    }

    public void setSolrLocation(@Nullable Date pickupDate, @Nullable Date dropoffDate, EHISolrLocation solrLocation, String searchArea, int flow) {
        getViewModel().setSolrLocation(solrLocation);
        getViewBinding().timeConflictView.setData(pickupDate, dropoffDate, solrLocation, searchArea, flow, false);

        if (getViewModel().shouldShowAfterHoursDropoff()) {
            SpannableString aboutText = new SpannableString(getResources().getString(R.string.locations_map_after_hours_about_button));
            aboutText.setSpan(
                    ResourcesCompat.getFont(getContext(), R.font.source_sans_light_italic),
                    0,
                    aboutText.length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            );
            aboutText.setSpan(mClickableSpan, 0, aboutText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            aboutText.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.ehi_primary)), 0, aboutText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            final CharSequence formattedTitle = new TokenizedString.Formatter<EHIStringToken>(getResources())
                    .formatString(R.string.locations_map_after_hours_return_label)
                    .addTokenAndValue(EHIStringToken.ABOUT, aboutText)
                    .format();

            getViewBinding().afterHoursReturn.setText(formattedTitle);
            getViewBinding().afterHoursReturn.setMovementMethod(LinkMovementMethod.getInstance());
            getViewBinding().afterHoursReturn.setVisibility(View.VISIBLE);
        } else {
            getViewBinding().afterHoursReturn.setVisibility(View.GONE);
        }
    }

    public void onViewCollapsed() {
        getViewBinding().timeConflictView.onViewCollapsed();
    }

    public interface MapDetailsListener {
        void onMoreInfoMapDetailsClick();
        void onLocationSelectedClick();
        void onAfterHoursClick();
    }
}
