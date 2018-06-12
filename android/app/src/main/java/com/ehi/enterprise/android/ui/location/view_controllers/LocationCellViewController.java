package com.ehi.enterprise.android.ui.location.view_controllers;

import android.content.res.Resources;
import android.view.View;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.models.location.solr.EHISolrLocation;
import com.ehi.enterprise.android.ui.location.interfaces.OnSolrLocationInfoClickListener;
import com.ehi.enterprise.android.ui.location.view_holders.SearchLocationsItemViewHolder;

public class LocationCellViewController {

    private final Resources mResources;

    public LocationCellViewController(Resources resources) {
        mResources = resources;
    }

    public void fillCellWithData(final SearchLocationsItemViewHolder holder,
                                 final EHISolrLocation location,
                                 final OnSolrLocationInfoClickListener listener,
                                 boolean markFavoriteLocation) {

        if (holder.iconType != null) {
            if (location.getGreenLocationCellIconDrawable(markFavoriteLocation) > 0) {
                holder.iconType.setVisibility(View.VISIBLE);
                holder.iconType.setImageResource(location.getGreenLocationCellIconDrawable(markFavoriteLocation));
            } else {
                holder.iconType.setVisibility(View.GONE);
                holder.iconType.setImageBitmap(null);
            }
        }

        if (holder.headerTitle != null) {
            holder.headerTitle.setText(location.getLocationDetailsTitle());
        }

        if (holder.subheaderTitle != null) {
            if (location.getReadableAddress() != null) {
                holder.subheaderTitle.setText(location.getReadableAddress());
            }
        }

        if (holder.distanceText != null) {
            if (location.getDistanceToUserLocation() != null) {
                holder.distanceText.setVisibility(View.VISIBLE);
                holder.distanceText.setText(location.getDistanceToUserLocation());
            } else {
                holder.distanceText.setVisibility(View.GONE);
            }
        }

        if (holder.leftContainer != null) {
            holder.leftContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onShowLocationDetails(location);
                    }
                }
            });
        }

        if (holder.rightContainer != null) {
            holder.rightContainer.setVisibility(View.VISIBLE);
            holder.rightContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onSelectLocation(location);
                    }
                }
            });
        }

        if (holder.distanceText != null) {
            if (location.getDistanceToUserLocation() != null) {
                holder.distanceText.setVisibility(View.VISIBLE);
                holder.distanceText.setText(location.getDistanceToUserLocation());
            } else {
                holder.distanceText.setVisibility(View.GONE);
            }
        }
    }

}