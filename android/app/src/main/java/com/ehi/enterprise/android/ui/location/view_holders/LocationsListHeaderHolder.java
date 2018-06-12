package com.ehi.enterprise.android.ui.location.view_holders;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.databinding.LocationListHeaderBinding;
import com.ehi.enterprise.android.ui.viewholder.DataBindingViewHolder;
import com.ehi.enterprise.android.utils.DisplayUtils;

public class LocationsListHeaderHolder extends DataBindingViewHolder<LocationListHeaderBinding> {

    private float width;

    public LocationsListHeaderHolder(LocationListHeaderBinding viewBinding) {
        super(viewBinding);
        final Context context = getViewBinding().getRoot().getContext();
        width = DisplayUtils.getDisplayMetrics(context).widthPixels;
    }

    public static LocationsListHeaderHolder create(Context context, ViewGroup parent) {
        return new LocationsListHeaderHolder((LocationListHeaderBinding) createViewBinding(context,
                R.layout.item_location_list_header,
                parent));
    }

    public void setFilterButtonListener(View.OnClickListener onClickListener) {
        getViewBinding().filterButton.setOnClickListener(onClickListener);
    }

    public void onHeaderAnimate(float percentage) {
        final float middleScreenPoint = width / 2;
        final float middleHeaderPoint = getViewBinding().headerText.getWidth() / 2;
        final float headerBegginingPosition = middleScreenPoint - middleHeaderPoint;
        getViewBinding().headerText.setX(headerBegginingPosition - (headerBegginingPosition * percentage));
    }

    public void setFilterButtonVisibility(boolean isVisible) {
        getViewBinding().filterButton.setVisibility(isVisible ? View.VISIBLE : View.GONE);
        getViewBinding().separatorView.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }
}
