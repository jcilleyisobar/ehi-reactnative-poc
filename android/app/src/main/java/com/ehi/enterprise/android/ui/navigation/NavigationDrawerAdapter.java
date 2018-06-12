package com.ehi.enterprise.android.ui.navigation;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.ui.navigation.animation.AnimatableDataBindingViewHolder;
import com.ehi.enterprise.android.utils.DisplayUtils;

import java.util.ArrayList;
import java.util.List;

public class NavigationDrawerAdapter extends RecyclerView.Adapter<AnimatableDataBindingViewHolder> {
    private List<NavigationDrawerItem> mDrawerItems;
    private NavigationDrawerAdapterCallbacks mListener;
    private ArrayList<AnimatableDataBindingViewHolder> mViewHolders = new ArrayList<>();

    public NavigationDrawerAdapter(List<NavigationDrawerItem> drawerItems, NavigationDrawerAdapterCallbacks listener) {
        mDrawerItems = drawerItems;
        mListener = listener;
    }

    public void setDrawerItems(List<NavigationDrawerItem> drawerItems) {
        mDrawerItems = new ArrayList<>(drawerItems);
        notifyDataSetChanged();
    }

    public void addDrawerItem(NavigationDrawerItem drawerItem, int position) {
        position = (position == -1) ? getItemCount() : position;
        mDrawerItems.add(position, drawerItem);
        notifyItemInserted(position);
    }

    public void popDrawerItem() {
        removeDrawerItem(-1);
    }

    public void removeDrawerItem(int position) {
        if (position >= mDrawerItems.size() || mDrawerItems.size() == 0) {
            return;
        }
        position = (position == -1) ? mDrawerItems.size() - 1 : position;
        mDrawerItems.remove(position);
        notifyItemRemoved(position);
    }

    public ArrayList<AnimatableDataBindingViewHolder> getViewHolders() {
        return mViewHolders;
    }

    @Override
    public AnimatableDataBindingViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        AnimatableDataBindingViewHolder baseHolder;
        if (viewType == NavigationDrawerItem.TYPE_SEPARATOR) {
            int height = (int) DisplayUtils.dipToPixels(parent.getContext(), 1f);
            LinearLayout lineView = new LinearLayout(parent.getContext());
            lineView.setLayoutParams(new LinearLayout.LayoutParams(DisplayUtils.getScreenWidth(parent.getContext()), height));
            lineView.setBackgroundColor(parent.getResources().getColor(R.color.navigation_light_divider));
            baseHolder = new AnimatableDataBindingViewHolder(lineView) {
                @Override
                public LinearLayout getLayout() {
                    return (LinearLayout) itemView;
                }
            };
        } else if (viewType == NavigationDrawerItem.TYPE_WEEKEND_SPECIAL_VIEW) {
            baseHolder = NavigationDrawerWeekendSpecialViewHolder.create(parent, mListener, calculateOffset(mViewHolders.size()));
        } else if (viewType == NavigationDrawerItem.TYPE_HEADER) {
            baseHolder = NavigationDrawerSectionHeaderViewHolder.create(parent, calculateOffset(mViewHolders.size()));
        } else if (viewType == NavigationDrawerItem.TYPE_SIGN_IN) {
            baseHolder = NavigationDrawerSignInViewHolder.create(parent, mListener, calculateOffset(mViewHolders.size()));
        } else if (viewType == NavigationDrawerItem.TYPE_SIGN_OUT) {
            baseHolder = NavigationDrawerSignOutViewHolder.create(parent, mListener, calculateOffset(mViewHolders.size()));
        } else {
            baseHolder = (viewType == NavigationDrawerItem.TYPE_SECONDARY_ITEM) ?
                    NavigationDrawerSecondaryViewHolder.create(parent, mListener, calculateOffset(mViewHolders.size())) :
                    NavigationDrawerPrimaryViewHolder.create(parent, mListener, calculateOffset(mViewHolders.size()));
        }
        mViewHolders.add(baseHolder);
        return baseHolder;
    }

    private float calculateOffset(int position) {
        return (position + 1) / 7f;
    }


    @Override
    public void onBindViewHolder(AnimatableDataBindingViewHolder holder, int position) {
        //updating logic, if viewholder moved in adapter
        holder.setState(new AnimatableDataBindingViewHolder.LinearLeftFadeInAnimateState(holder.getLayout(), calculateOffset(position)));
        if (mDrawerItems.get(position).getType() == NavigationDrawerItem.TYPE_SEPARATOR) {
            return;
        } else if (mDrawerItems.get(position).getType() == NavigationDrawerItem.TYPE_BUTTON_ITEM) {
            return;
        }
        if (mDrawerItems.get(position).getType() == NavigationDrawerItem.TYPE_PRIMARY_ITEM ||
                mDrawerItems.get(position).getType() == NavigationDrawerItem.TYPE_LOCATION_ITEM) {
            NavigationDrawerPrimaryViewHolder.bind((NavigationDrawerPrimaryViewHolder) holder, mDrawerItems.get(position));
        } else if (mDrawerItems.get(position).getType() == NavigationDrawerItem.TYPE_SECONDARY_ITEM) {
            NavigationDrawerSecondaryViewHolder.bind((NavigationDrawerSecondaryViewHolder) holder, mDrawerItems.get(position));
        } else if (mDrawerItems.get(position).getType() == NavigationDrawerItem.TYPE_WEEKEND_SPECIAL_VIEW) {
            NavigationDrawerWeekendSpecialViewHolder.bind((NavigationDrawerWeekendSpecialViewHolder) holder, mDrawerItems.get(position));
        } else if (mDrawerItems.get(position).getType() == NavigationDrawerItem.TYPE_HEADER) {
            NavigationDrawerSectionHeaderViewHolder.bind((NavigationDrawerSectionHeaderViewHolder) holder, mDrawerItems.get(position));
        } else if (mDrawerItems.get(position).getType() == NavigationDrawerItem.TYPE_SIGN_IN) {
            NavigationDrawerSignInViewHolder.bind((NavigationDrawerSignInViewHolder) holder, mDrawerItems.get(position));
        } else if (mDrawerItems.get(position).getType() == NavigationDrawerItem.TYPE_SIGN_OUT) {
            NavigationDrawerSignOutViewHolder.bind((NavigationDrawerSignOutViewHolder) holder, mDrawerItems.get(position));
        }

    }

    @Override
    public int getItemViewType(int position) {
        return mDrawerItems.get(position).getType();
    }

    public boolean isCurrentlySelected(int position) {
        return mDrawerItems.get(position).isSelected();
    }

    @Override
    public int getItemCount() {
        return mDrawerItems.size();
    }

    public interface NavigationDrawerAdapterCallbacks {
        void onNavigationItemSelected(int position);
    }

}