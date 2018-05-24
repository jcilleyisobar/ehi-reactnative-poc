package com.ehi.enterprise.android.ui.location.widgets.components;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.View;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.app.EHIStringToken;
import com.ehi.enterprise.android.models.location.solr.EHISolrLocation;
import com.ehi.enterprise.android.models.location.solr.EHISolrLocationValidity;
import com.ehi.enterprise.android.ui.viewmodel.ManagersAccessViewModel;
import com.ehi.enterprise.android.utils.reactor_extensions.viewstate.ReactorTextViewState;
import com.ehi.enterprise.android.utils.reactor_extensions.viewstate.ReactorViewState;
import com.isobar.android.tokenizedstring.TokenizedString;

import java.util.Date;

import io.dwak.reactor.unbinder.annotation.AutoUnbindAll;

@AutoUnbindAll
public class TimeConflictComponentViewModel extends ManagersAccessViewModel {
    public ReactorViewState rootView = new ReactorViewState();
    public ReactorViewState collapsibleContainer = new ReactorViewState();
    public ReactorViewState arrowView = new ReactorViewState();
    public ReactorTextViewState titleView = new ReactorTextViewState();
    public ReactorViewState firstTimeView = new ReactorViewState();
    public ReactorViewState secondTimeView = new ReactorViewState();
    public ReactorTextViewState hoursDetailsView = new ReactorTextViewState();

    private EHISolrLocation mSolrLocation;
    private Date mPickupDate;
    private Date mDropoffDate;
    private String mSearchArea;
    private int mFlow;

    public Date getmPickupDate() {
        return mPickupDate;
    }

    public Date getmDropoffDate() {
        return mDropoffDate;
    }

    public void setPickupDate(Date pickupDate) {
        this.mPickupDate = pickupDate;
    }

    public void setDropoffDate(Date mDropoffDate) {
        this.mDropoffDate = mDropoffDate;
    }

    public void setSolrLocation(EHISolrLocation mSolrLocation) {
        this.mSolrLocation = mSolrLocation;
        updateContainer();
        updateTimesViewsVisibility();
    }

    public boolean shouldShowConflictMessage() {
        return mSolrLocation != null && (mSolrLocation.isInvalidForDropoff() || mSolrLocation.isInvalidForPickup());
    }

    public boolean isContainerVisible() {
        return collapsibleContainer.visibility().getValue() != null && collapsibleContainer.visibility().getValue() == View.VISIBLE;
    }

    private void updateTitleText() {
        final CharSequence titleText = getTitleText();
        if (titleText != null) {
            titleView.setText(titleText);
        }
    }

    public CharSequence getTitleText() {
        if (mSolrLocation == null) {
            return null;
        }
        SpannableStringBuilder titleText = new SpannableStringBuilder();
        if (mSolrLocation.isInvalidForPickup() && mSolrLocation.isInvalidForDropoff()) {
            titleText.append(getResources().getString(R.string.locations_map_closed_pickup))
                    .append(" & ")
                    .append(getResources().getString(R.string.locations_map_closed_return));
        } else if (mSolrLocation.isInvalidForDropoff()) {
            titleText.append(getResources().getString(R.string.locations_map_closed_return));
        } else if (mSolrLocation.isInvalidForPickup()) {
            titleText.append(getResources().getString(R.string.locations_map_closed_pickup));
        }

        final SpannableString containerHoursDetailDescription = getFormattedContainerHoursDescription();
        titleText.append(" - ")
                .append(containerHoursDetailDescription);

        return new TokenizedString.Formatter<EHIStringToken>(getResources())
                .formatString(R.string.locations_map_closed_your_pickup)
                .addTokenAndValue(EHIStringToken.CLOSED_ON, titleText)
                .format();
    }

    @NonNull
    private SpannableString getFormattedContainerHoursDescription() {
        final SpannableString containerHoursDetailDescription = new SpannableString(getCollapsibleContainerHoursDescription());
        containerHoursDetailDescription.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.ehi_primary)), 0, containerHoursDetailDescription.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return containerHoursDetailDescription;
    }

    private String getCollapsibleContainerHoursDescription() {
        if (isContainerVisible()) {
            return getResources().getString(R.string.locations_map_hide_hours);
        } else {
            return getResources().getString(R.string.locations_map_view_hours);
        }
    }

    public void toggleContainer() {
        if (isContainerVisible()) {
            collapseContainer();
        } else {
            expandContainer();
        }
    }

    public void collapseContainer() {
        arrowView.setRotation(90f);
        collapsibleContainer.setVisibility(View.GONE);
        updateTitleText();
    }

    public void expandContainer() {
        arrowView.setRotation(270f);
        collapsibleContainer.setVisibility(View.VISIBLE);
        updateTitleText();
    }

    public void updateContainer() {
        if (shouldShowConflictMessage()) {
            updateTitleText();
            updateTimesViewsVisibility();
            rootView.setVisibility(View.VISIBLE);
        } else {
            rootView.setVisibility(View.GONE);
        }
    }

    @Nullable
    public Date getDateForFirstTimeView() {
        if (mSolrLocation != null && mSolrLocation.isInvalidForPickup()) {
            return mPickupDate;
        } else {
            return mDropoffDate;
        }
    }

    public Date getDateForSecondTimeView() {
        if (mSolrLocation != null && mSolrLocation.isInvalidForPickup() && mSolrLocation.isInvalidForDropoff()) {
            return mDropoffDate;
        } else {
            return null;
        }
    }

    @Nullable
    public EHISolrLocationValidity getValidityForFirstTimeView() {
        if (mSolrLocation == null) {
            return null;
        }
        if (mSolrLocation.isInvalidForPickup()) {
            return mSolrLocation.getPickupValidity();
        } else {
            return mSolrLocation.getDropoffValidity();
        }
    }

    @Nullable
    public EHISolrLocationValidity getValidityForSecondTimeView() {
        if (mSolrLocation != null && mSolrLocation.isInvalidForPickup() && mSolrLocation.isInvalidForDropoff()) {
            return mSolrLocation.getDropoffValidity();
        } else {
            return null;
        }
    }

    public void updateTimesViewsVisibility() {
        if (mSolrLocation != null) {
            if (mSolrLocation.isInvalidForDropoff() || mSolrLocation.isInvalidForPickup()) {
                firstTimeView.setVisibility(View.VISIBLE);
            } else {
                firstTimeView.setVisibility(View.GONE);
            }

            if (mSolrLocation.isClosedForPickupAndDropoff()) {
                secondTimeView.setVisibility(View.VISIBLE);
            } else {
                secondTimeView.setVisibility(View.GONE);
            }
        }
    }

    public String getSearchArea() {
        return mSearchArea;
    }

    public void setSearchArea(String value) {
        mSearchArea = value;
    }

    public EHISolrLocation getLocation() {
        return mSolrLocation;
    }

    public void setFlow(int value) {
        mFlow = value;
    }

    public int getFlow() {
        return mFlow;
    }
}