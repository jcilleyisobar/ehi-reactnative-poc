package com.ehi.enterprise.android.ui.location.view_holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ehi.enterprise.android.R;

public class SearchLocationsItemViewHolder extends RecyclerView.ViewHolder {

    public final ImageView iconType;
    public final TextView headerTitle;
    public final TextView subheaderTitle;
    public final TextView distanceText;
    public final View bottomDivider;

    public final View leftContainer;
    public final View rightContainer;
    public final boolean allowSwipe;

    public SearchLocationsItemViewHolder(View view, boolean allowSwipe) {
        super(view);
        this.allowSwipe = allowSwipe;
        this.iconType = (ImageView) view.findViewById(R.id.icon_type);
        this.headerTitle = (TextView) view.findViewById(R.id.header_title);
        this.subheaderTitle = (TextView) view.findViewById(R.id.subheader_title);
        this.leftContainer = view.findViewById(R.id.left_container);
        this.rightContainer = view.findViewById(R.id.right_container);
        this.distanceText = (TextView) view.findViewById(R.id.distance_text);
        this.bottomDivider = view.findViewById(R.id.bottom_divider);
    }
}