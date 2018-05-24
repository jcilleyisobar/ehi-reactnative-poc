package com.ehi.enterprise.android.ui.location;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.ehi.enterprise.android.ui.location.view_holders.SearchFilterViewHolder;

import java.util.ArrayList;

public class SearchFilterAdapter extends RecyclerView.Adapter<SearchFilterViewHolder> {

	private ArrayList<SearchLocationsFilterItem> mItems;
	private FilterCallback mCallback;

	public SearchFilterAdapter(ArrayList<SearchLocationsFilterItem> items, FilterCallback callback) {
		mCallback = callback;
		mItems = items;
	}

	@Override
	public SearchFilterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		return SearchFilterViewHolder.create(parent, mCallback, viewType);
	}

	@Override
	public void onBindViewHolder(SearchFilterViewHolder holder, int position) {
		SearchFilterViewHolder.bind(holder, mItems.get(position), position);
	}

	public SearchLocationsFilterItem getItem(int position) {
		return mItems.get(position);
	}

	public void setCheckedItem(int position, boolean check) {
		if (mItems.get(position).getType() != SearchLocationsFilterItem.TYPE_PRIMARY_ITEM) {
			return;
		}
		mItems.get(position).setChecked(check);
		notifyDataSetChanged(); //using this because of weird interaction with sectioned adapter
	}

	@Override
	public int getItemCount() {
		return mItems.size();
	}

	@SearchLocationsFilterItem.SearchLocationsItemType
	@Override
	public int getItemViewType(int position) {
		return mItems.get(position).getType();
	}

	public interface FilterCallback {
		void itemClicked(int position, View clickedView);
	}
}
