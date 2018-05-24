package com.ehi.enterprise.android.ui.location;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.models.location.EHICityLocation;
import com.ehi.enterprise.android.models.location.solr.EHISolrLocation;
import com.ehi.enterprise.android.network.responses.location.solr.EHIPostalCodeLocation;
import com.ehi.enterprise.android.ui.location.interfaces.OnSolrLocationInfoClickListener;
import com.ehi.enterprise.android.ui.location.view_controllers.LocationCellViewController;
import com.ehi.enterprise.android.ui.location.view_holders.SearchLocationsItemViewHolder;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

public class SearchSolrLocationsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = SearchSolrLocationsAdapter.class.getSimpleName();

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({
            VIEW_TYPE_LOCATION,
            VIEW_TYPE_CITY,
            VIEW_TYPE_POSTAL_CODE
    })
    public @interface ViewType {
    }

    public static final int VIEW_TYPE_LOCATION = 0;
    public static final int VIEW_TYPE_CITY = 1;
    public static final int VIEW_TYPE_POSTAL_CODE = 2;

    private class SearchLocationListItem<T> {
        @ViewType
        int mViewType;
        T mObject;

        private SearchLocationListItem(int viewType, T object) {
            mViewType = viewType;
            mObject = object;
        }

        public int getViewType() {
            return mViewType;
        }

        public T getObject() {
            return mObject;
        }
    }

    @NonNull
    private List<SearchLocationListItem> mListItems;


    private LocationCellViewController mViewController;
    private OnSolrLocationInfoClickListener mListener;

    public SearchSolrLocationsAdapter(@NonNull Context context, @Nullable OnSolrLocationInfoClickListener listener) {
        mViewController = new LocationCellViewController(context.getResources());
        mListener = listener;
        mListItems = new ArrayList<>();
    }

    public void addPostalCodeLocation(@NonNull EHIPostalCodeLocation ehiPostalCodeLocation) {
        SearchLocationListItem<EHIPostalCodeLocation> listItem = new SearchLocationListItem<>(VIEW_TYPE_POSTAL_CODE, ehiPostalCodeLocation);
        mListItems.add(listItem);
        notifyItemInserted(mListItems.size());
    }

    public void addCity(@NonNull EHICityLocation ehiCityLocation) {
        SearchLocationListItem<EHICityLocation> listItem = new SearchLocationListItem<>(VIEW_TYPE_CITY, ehiCityLocation);
        mListItems.add(listItem);
        notifyItemInserted(mListItems.size());
    }

    public void addLocation(@NonNull EHISolrLocation ehiSolrLocation) {
        SearchLocationListItem<EHISolrLocation> listItem = new SearchLocationListItem<>(VIEW_TYPE_LOCATION, ehiSolrLocation);
        int indexToInsert = 0;
        for (SearchLocationListItem item : mListItems) {
            if (item.getViewType() == VIEW_TYPE_LOCATION) {
                indexToInsert++;
            }
        }
        mListItems.add(indexToInsert, listItem);
        notifyItemInserted(indexToInsert);
    }

    public void addCities(@NonNull List<EHICityLocation> ehiCityLocations) {
        for (EHICityLocation ehiCityLocation : ehiCityLocations) {
            addCity(ehiCityLocation);
        }
    }

    public void addLocations(@NonNull List<EHISolrLocation> locations) {
        for (EHISolrLocation location : locations) {
            addLocation(location);
        }
    }

    public void addPostalLocations(List<EHIPostalCodeLocation> postalCodeLocations) {
        for (EHIPostalCodeLocation postalCodeLocation : postalCodeLocations) {
            addPostalCodeLocation(postalCodeLocation);
        }
    }

    public void clear() {
        mListItems.clear();
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_search_locations_short_item, parent, false).getRoot();
        return new SearchLocationsItemViewHolder(view, false);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final SearchLocationsItemViewHolder viewHolder = (SearchLocationsItemViewHolder) holder;
        switch (getItemViewType(position)) {
            case VIEW_TYPE_CITY:
                final EHICityLocation cityLocation = (EHICityLocation) mListItems.get(position).getObject();
                viewHolder.iconType.setVisibility(View.GONE);
                viewHolder.headerTitle.setText(cityLocation.getLongName());
                viewHolder.rightContainer.setVisibility(View.GONE);
                viewHolder.leftContainer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mListener != null) {
                            mListener.onShowCityLocation(cityLocation);
                        }
                    }
                });
                break;
            case VIEW_TYPE_LOCATION:
                final EHISolrLocation location = (EHISolrLocation) mListItems.get(position).getObject();
                mViewController.fillCellWithData(viewHolder, location, mListener, false);
                break;
            case VIEW_TYPE_POSTAL_CODE:
                final EHIPostalCodeLocation postalCodeLocation = (EHIPostalCodeLocation) mListItems.get(position).getObject();
                viewHolder.iconType.setVisibility(View.GONE);
                viewHolder.headerTitle.setText(postalCodeLocation.getLongName());
                viewHolder.rightContainer.setVisibility(View.GONE);
                viewHolder.leftContainer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mListener != null) {
                            mListener.onShowPostalLocation(postalCodeLocation);
                        }
                    }
                });
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        return mListItems.get(position).getViewType();
    }

    @Override
    public int getItemCount() {
        return mListItems.size();
    }

    public void setListener(OnSolrLocationInfoClickListener listener) {
        mListener = listener;
    }


}