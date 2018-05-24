package com.ehi.enterprise.android.ui.location.view_holders;

import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.databinding.FilterPrimaryItemBinding;
import com.ehi.enterprise.android.ui.location.SearchFilterAdapter;
import com.ehi.enterprise.android.ui.location.SearchLocationsFilterItem;
import com.ehi.enterprise.android.ui.viewholder.DataBindingViewHolder;

public class SearchFilterViewHolder
        extends DataBindingViewHolder<FilterPrimaryItemBinding>
        implements View.OnClickListener {

    SearchFilterAdapter.FilterCallback mCallback;
    private int mPosition;

    @SearchLocationsFilterItem.SearchLocationsItemType
    private int mType;

    public SearchFilterViewHolder(FilterPrimaryItemBinding viewBinding, SearchFilterAdapter.FilterCallback callback) {
        super(viewBinding);
        mCallback = callback;
    }

    public static SearchFilterViewHolder create(ViewGroup parent, SearchFilterAdapter.FilterCallback callback, @SearchLocationsFilterItem.SearchLocationsItemType int type) {
        FilterPrimaryItemBinding binding = (FilterPrimaryItemBinding) createViewBinding(
                parent.getContext(),
                R.layout.item_search_locations_filter_primary_item,
                parent);
        SearchFilterViewHolder holder = new SearchFilterViewHolder(binding, callback);

        if (type == SearchLocationsFilterItem.TYPE_SECONDARY_ITEM) {
            holder.getCheckBox().setVisibility(View.GONE);
        }
        return holder;
    }

    public static void bind(final SearchFilterViewHolder holder, final SearchLocationsFilterItem item, int position) {
        holder.getTitleView().setText(item.getTitle());
        holder.mPosition = position;
        holder.mType = item.getType();
        if (item.getIconId() > 0) {
            holder.getViewBinding().itemIcon.setImageResource(item.getIconId());
            holder.getViewBinding().itemIcon.setVisibility(View.VISIBLE);
        } else {
            holder.getViewBinding().itemIcon.setImageBitmap(null);
            holder.getViewBinding().itemIcon.setVisibility(View.GONE);
        }
        if (item.getType() == SearchLocationsFilterItem.TYPE_PRIMARY_ITEM) {
            holder.itemView.setOnClickListener(holder);
            holder.getCheckBox().setChecked(item.isChecked());
        } else if (item.getType() == SearchLocationsFilterItem.TYPE_SECONDARY_ITEM) {
            holder.getParent().setOnClickListener(holder);
        }
    }

    @Override
    public void onClick(View v) {
        mCallback.itemClicked(mPosition, (mType == SearchLocationsFilterItem.TYPE_PRIMARY_ITEM) ? getCheckBox() : getParent());
    }

    public View getParent() {
        return getViewBinding().getRoot();
    }

    public TextView getTitleView() {
        return getViewBinding().title;
    }

    public CheckBox getCheckBox() {
        return getViewBinding().filterCheckBox;
    }
}
