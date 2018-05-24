package com.ehi.enterprise.android.ui.location.widgets.components;

import android.support.v4.content.res.ResourcesCompat;
import android.view.View;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.models.location.solr.EHISolrLocation;
import com.ehi.enterprise.android.ui.viewmodel.ManagersAccessViewModel;
import com.ehi.enterprise.android.utils.reactor_extensions.viewstate.ReactorImageViewState;
import com.ehi.enterprise.android.utils.reactor_extensions.viewstate.ReactorTextViewState;
import com.ehi.enterprise.android.utils.reactor_extensions.viewstate.ReactorViewState;

import io.dwak.reactor.unbinder.annotation.AutoUnbindAll;

@AutoUnbindAll
public class LocationMapBubbleDetailsViewModel extends ManagersAccessViewModel {
    ReactorTextViewState headerTextView = new ReactorTextViewState();
    ReactorTextViewState subheaderTextView = new ReactorTextViewState();
    ReactorViewState flexibleTravelView = new ReactorTextViewState();
    ReactorViewState selectButtonBackground = new ReactorViewState();
    ReactorImageViewState selectButtonImage = new ReactorImageViewState();

    private EHISolrLocation mSolrLocation;

    public void setSolrLocation(EHISolrLocation solrLocation) {
        this.mSolrLocation = solrLocation;
        updateViews();
    }

    private void updateViews() {
        headerTextView.setText(mSolrLocation.getLocationDetailsTitle());
        if (mSolrLocation.getReadableAddress() != null) {
            subheaderTextView.setText(mSolrLocation.getReadableAddress());
        }
        if (isTimeConflict()) {
            selectButtonBackground.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.green_border_white_background_button_overlay, null));
            selectButtonImage.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.arrow_green, null));
            flexibleTravelView.setVisibility(View.VISIBLE);
        } else {
            selectButtonBackground.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.green_button_touch_overlay, null));
            selectButtonImage.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.arrow_white, null));
            flexibleTravelView.setVisibility(View.GONE);
        }
    }

    private boolean isTimeConflict() {
        return mSolrLocation != null && (mSolrLocation.isInvalidForDropoff() || mSolrLocation.isInvalidForPickup());
    }

    boolean shouldShowAfterHoursDropoff() {
        return mSolrLocation.isDropoffAfterHours();
    }
}