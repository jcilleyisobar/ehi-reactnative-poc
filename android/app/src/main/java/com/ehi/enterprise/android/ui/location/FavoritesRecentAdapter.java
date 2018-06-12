package com.ehi.enterprise.android.ui.location;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.models.location.solr.EHISolrLocation;
import com.ehi.enterprise.android.ui.adapter.SectionHeader;
import com.ehi.enterprise.android.ui.adapter.SectionHeaderViewHolder;
import com.ehi.enterprise.android.ui.location.interfaces.OnSolrLocationInfoClickListener;
import com.ehi.enterprise.android.ui.location.view_controllers.LocationCellViewController;
import com.ehi.enterprise.android.ui.location.view_holders.SearchLocationsItemViewHolder;
import com.ehi.enterprise.android.utils.DLog;

import java.util.ArrayList;
import java.util.List;

public class FavoritesRecentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = FavoritesRecentAdapter.class.getSimpleName();

    @NonNull
    private List<FavoriteRecentItem> mItems;

    private OnSolrLocationInfoClickListener mListener;
    private LocationCellViewController mViewController;
    private EHISolrLocation mLocationToRemove;
    private FavoriteRecentItem mLocationItemToRemove;
    private int mIndexOfRemovedLocation = -1;
    private final SectionHeader mFavoriteSectionHeader;
    private final SectionHeader mRecentSectionHeader;
    private boolean mFavoriteSectionHeaderAdded = false;
    private boolean mRecentSectionHeaderAdded = false;
    private int mRecentSectionHeaderInsertionPointer = 0;
    private Context mContext;

    public FavoritesRecentAdapter(@NonNull Context context, @Nullable OnSolrLocationInfoClickListener listener, final OnRecentsClearListener onRecentsClearListener) {
        mContext = context;
        mViewController = new LocationCellViewController(context.getResources());
        mListener = listener;
        mItems = new ArrayList<>();

        mFavoriteSectionHeader = SectionHeader.Builder
                .atPosition(0)
                .setTitle(context.getResources().getString(R.string.locations_favorites_header_title))
                .build();

        mRecentSectionHeader = SectionHeader.Builder
                .atPosition(0)
                .setTitle(context.getResources().getString(R.string.locations_recents_header_title))
                .withButton(context.getResources().getString(R.string.locations_clear_section_title), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        onRecentsClearListener.onClearRecents();
                    }
                })
                .build();
    }

    /**
     * Use this to add a location to the favorites sub section of the list
     *
     * @param ehiSolrLocation
     */
    public void addFavoriteLocation(EHISolrLocation ehiSolrLocation) {
        boolean addedHeader = addFavoriteHeader();
        mItems.add(new FavoriteRecentItem<>(ehiSolrLocation, FavoriteRecentItem.ITEM, FavoriteRecentItem.FAVORITE));
        mRecentSectionHeaderInsertionPointer++;

        if (addedHeader) {
            notifyItemRangeInserted(mItems.size(), 2);
        } else {
            notifyItemInserted(mItems.size());
        }

    }

    private boolean addFavoriteHeader() {
        boolean addedHeader = false;
        if (!mFavoriteSectionHeaderAdded) {
            mItems.add(0, new FavoriteRecentItem<>(mFavoriteSectionHeader, FavoriteRecentItem.HEADER));
            addedHeader = true;
            mFavoriteSectionHeaderAdded = true;
            mRecentSectionHeaderInsertionPointer++;
        }
        return addedHeader;
    }

    /**
     * Use this to add a location the recents sub section of the list
     *
     * @param ehiSolrLocation
     */
    public void addRecentItem(EHISolrLocation ehiSolrLocation) {
        boolean addedHeader = addRecentsHeader();
        mItems.add(new FavoriteRecentItem<>(ehiSolrLocation, FavoriteRecentItem.ITEM, FavoriteRecentItem.RECENT));

        if (addedHeader) {
            notifyItemRangeInserted(mRecentSectionHeaderInsertionPointer, 2);
        } else {
            notifyItemInserted(mItems.size());
        }
    }

    private boolean addRecentsHeader() {
        boolean addedHeader = false;
        if (!mRecentSectionHeaderAdded) {
            mItems.add(new FavoriteRecentItem<>(mRecentSectionHeader, FavoriteRecentItem.HEADER));
            mRecentSectionHeaderInsertionPointer = mItems.size() - 1;
            addedHeader = true;
            mRecentSectionHeaderAdded = true;
        }
        return addedHeader;
    }

    public void setListener(OnSolrLocationInfoClickListener listener) {
        mListener = listener;
    }

    public void clear() {
        mRecentSectionHeaderInsertionPointer = 0;
        mFavoriteSectionHeaderAdded = false;
        mRecentSectionHeaderAdded = false;
        mItems.clear();
        notifyDataSetChanged();
    }

    public EHISolrLocation markPositionForRemoval(int position) {
        mIndexOfRemovedLocation = position;
        mLocationItemToRemove = mItems.get(mIndexOfRemovedLocation);
        mLocationToRemove = (EHISolrLocation) mLocationItemToRemove.getObject();
        mItems.remove(mIndexOfRemovedLocation);

        // If we remove a favorite, make sure we update our recents header pointer
        if (mLocationItemToRemove.getItemType() == FavoriteRecentItem.FAVORITE) {
            mRecentSectionHeaderInsertionPointer--;
        }

        if (mRecentSectionHeaderInsertionPointer == 1) { // if we're out of favorites, make sure we remove the favorites header also
            mRecentSectionHeaderInsertionPointer = 0;
            mItems.remove(0);
            mFavoriteSectionHeaderAdded = false;
            notifyItemRangeRemoved(0, 2);
        } else if (mRecentSectionHeaderInsertionPointer == mItems.size() - 1) { // same thing with recents
            mItems.remove(mRecentSectionHeaderInsertionPointer);
            mRecentSectionHeaderAdded = false;
            notifyItemRangeRemoved(mRecentSectionHeaderInsertionPointer, 2);
            mRecentSectionHeaderInsertionPointer = 0;
        } else if (mItems.size() > 0) {
            notifyItemRemoved(mIndexOfRemovedLocation);
        } else {
            notifyDataSetChanged();
        }

        DLog.d("mItems Size: " + mItems.size() +
                "\nmRecentSectionHeaderInsertionPointer: " + mRecentSectionHeaderInsertionPointer);
        return mLocationToRemove;
    }

    public void undoLocationRemoval() {
        boolean addFavoriteHeader = false;
        boolean addRecentHeader = false;
        // if we're adding a favorite back to the list, make sure to update the pointer for recents
        switch (mLocationItemToRemove.getItemType()) {
            case FavoriteRecentItem.FAVORITE:
                addFavoriteHeader = addFavoriteHeader();
                mRecentSectionHeaderInsertionPointer++;
                break;
            case FavoriteRecentItem.RECENT:
                addRecentHeader = addRecentsHeader();
                break;
        }

        mItems.add(mIndexOfRemovedLocation, mLocationItemToRemove);

        if (addFavoriteHeader) {  // handle adding the favorites section header back
            notifyItemRangeInserted(0, 2);
        } else if (addRecentHeader) { //handle adding the recents section header back
            notifyItemRangeInserted(mRecentSectionHeaderInsertionPointer, 2);
        } else {
            notifyItemInserted(mIndexOfRemovedLocation);
        }
        mLocationToRemove = null;
        mIndexOfRemovedLocation = -1;
    }

    private FavoriteRecentItem getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public int getItemViewType(int position) {
        return mItems.get(position).getViewType();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case FavoriteRecentItem.HEADER:
                return SectionHeaderViewHolder.create(mContext, parent);
            case FavoriteRecentItem.ITEM:
                View view = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_search_locations_item, parent, false).getRoot();
                return new SearchLocationsItemViewHolder(view, true);
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case FavoriteRecentItem.HEADER:
                SectionHeaderViewHolder.bind((SectionHeaderViewHolder) holder,
                        (SectionHeader) getItem(position).getObject());
                break;
            case FavoriteRecentItem.ITEM:
                final SearchLocationsItemViewHolder viewHolder = (SearchLocationsItemViewHolder) holder;
                final EHISolrLocation location = (EHISolrLocation) mItems.get(position).getObject();
                mViewController.fillCellWithData(viewHolder, location, mListener, true);
                break;
        }

    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public void reset() {
        notifyDataSetChanged();
    }

    interface OnRecentsClearListener {
        void onClearRecents();
    }
}