package com.ehi.enterprise.android.ui.location;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.models.location.solr.EHISolrLocation;
import com.ehi.enterprise.android.ui.location.view_holders.LocationOnMapCellViewHolder;
import com.ehi.enterprise.android.ui.location.view_holders.LocationsListHeaderHolder;
import com.ehi.enterprise.android.ui.location.view_holders.ReturnToTopViewHolder;
import com.ehi.enterprise.android.utils.ListUtils;

import java.util.Date;
import java.util.List;

public class LocationsOnMapListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = LocationsOnMapListAdapter.class.getSimpleName();

    public static final int VIEW_TYPE_LOCATION = 0;
    public static final int VIEW_TYPE_FOOTER = 1;
    public static final int VIEW_NO_LOCATIONS_HEADER = 2;
    public static final int VIEW_TRANSPARENT_HEADER = 3;
    public static final int VIEW_LOCATION_COUNT_HEADER = 4;

    private List<EHISolrLocation> mListItems;

    private View.OnClickListener mOnReturnToTopClickListener;
    private View.OnClickListener mOnHeaderClickListener;
    private View.OnClickListener onFilterButtonListener;
    private Date mPickupDate;
    private Date mDropoffDate;
    private float percentage;
    private Context mContext;
    private LocationOnMapCellViewHolder.LocationMapListListener mLocationListListener;
    private boolean shouldAnimate;
    private boolean isFilterButtonVisible;
    private String mSearchArea;
    private int mFlow;

    public LocationsOnMapListAdapter(@NonNull Context context, List<EHISolrLocation> locations, Date pickupDate, Date dropoffDate, String searchArea, int flow) {
        mContext = context;
        mListItems = locations;
        mPickupDate = pickupDate;
        mDropoffDate = dropoffDate;
        mSearchArea = searchArea;
        mFlow = flow;
    }

    public void setData(List<EHISolrLocation> locations) {
        mListItems = locations;
        shouldAnimate = false;
        notifyDataSetChanged();
    }

    public boolean isEmpty() {
        return ListUtils.isEmpty(mListItems);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEW_TRANSPARENT_HEADER: {
                View view = new FrameLayout(parent.getContext());


                view.setLayoutParams(
                        new ViewGroup.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                (int) (parent.getBottom() - parent.getTop()
                                        - parent.getContext().getResources().getDimension(R.dimen.sliding_panel_height_initial))
                        ));
                return new RecyclerView.ViewHolder(view) {
                };
            }
            case VIEW_NO_LOCATIONS_HEADER: {
                ViewDataBinding viewBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_no_locations_found, parent, false);
                viewBinding.executePendingBindings();
                return new RecyclerView.ViewHolder(viewBinding.getRoot()) {
                };
            }
            case VIEW_TYPE_FOOTER: {
                return ReturnToTopViewHolder.create(parent.getContext(), parent);
            }
            case VIEW_LOCATION_COUNT_HEADER: {
                return LocationsListHeaderHolder.create(parent.getContext(), parent);
            }
            case VIEW_TYPE_LOCATION:
            default: {
                return LocationOnMapCellViewHolder.create(parent.getContext(), parent);
            }
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case VIEW_TYPE_LOCATION: {
                LocationOnMapCellViewHolder viewHolder = (LocationOnMapCellViewHolder) holder;
                EHISolrLocation location = mListItems.get(position - 2);
                LocationOnMapCellViewHolder.bind(viewHolder, location, mPickupDate, mDropoffDate, mContext.getResources(), mLocationListListener, mSearchArea, mFlow);
                break;
            }
            case VIEW_TYPE_FOOTER: {
                ReturnToTopViewHolder.bind((ReturnToTopViewHolder) holder, mOnReturnToTopClickListener);
                break;
            }
            case VIEW_LOCATION_COUNT_HEADER: {
                final LocationsListHeaderHolder viewHolder = (LocationsListHeaderHolder) holder;
                viewHolder.setFilterButtonListener(onFilterButtonListener);

                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (mOnHeaderClickListener != null) {
                            mOnHeaderClickListener.onClick(view);
                        }
                    }
                });

                if (shouldAnimate) {
                    viewHolder.onHeaderAnimate(percentage);
                }
                viewHolder.setFilterButtonVisibility(isFilterButtonVisible);
                break;
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return VIEW_TRANSPARENT_HEADER;
        }
        if (position == 1) {
            return VIEW_LOCATION_COUNT_HEADER;
        }
        if (mListItems.size() > 0) {
            if (position - 2 < mListItems.size()) {
                return VIEW_TYPE_LOCATION;
            } else {
                return VIEW_TYPE_FOOTER;
            }
        } else {
            return VIEW_NO_LOCATIONS_HEADER;
        }
    }

    @Override
    public int getItemCount() {
        if (mListItems == null) {
            return 2; // no search done yet
        } else if (mListItems.size() == 0) {
            return 3; // no locations header
        } else {
            if (mListItems.size() > 10) {
                return mListItems.size() + 3;
            } else {
                return mListItems.size() + 2;
            }
        }

    }

    public void setOnReturnToTopClickListener(View.OnClickListener listener) {
        mOnReturnToTopClickListener = listener;
    }

    public void setOnHeaderClickListener(View.OnClickListener listener) {
        mOnHeaderClickListener = listener;
    }

    public void setLocationListListener(LocationOnMapCellViewHolder.LocationMapListListener listener) {
        mLocationListListener = listener;
    }

    public void setOnFilterButtonListener(View.OnClickListener onFilterButtonListener) {
        this.onFilterButtonListener = onFilterButtonListener;
    }

    public void setFilterDates(Date pickupDate, Date dropoffDate) {
        mPickupDate = pickupDate;
        mDropoffDate = dropoffDate;
        shouldAnimate = false;
        notifyDataSetChanged();
    }

    public void onAnimateHeader(float value) {
        percentage = value;
        shouldAnimate = true;
        notifyItemChanged(1);
    }

    public void setFilterButtonVisible(boolean visible) {
        isFilterButtonVisible = visible;
        notifyItemChanged(1);
    }

    public int getClosedLocationsCount() {
        int count = 0;
        if (mListItems != null) {
            for (EHISolrLocation location : mListItems) {
                if (location.isLocationInvalid()) {
                    count++;
                }
            }
        }
        return count;
    }
}