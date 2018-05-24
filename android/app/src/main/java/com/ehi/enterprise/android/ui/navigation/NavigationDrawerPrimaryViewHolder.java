package com.ehi.enterprise.android.ui.navigation;

import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.databinding.NavigationDrawerItemBinding;
import com.ehi.enterprise.android.ui.navigation.animation.AnimatableDataBindingViewHolder;

public class NavigationDrawerPrimaryViewHolder
        extends AnimatableDataBindingViewHolder<NavigationDrawerItemBinding>
        implements View.OnClickListener {

    private final NavigationDrawerAdapter.NavigationDrawerAdapterCallbacks mListener;

    public NavigationDrawerPrimaryViewHolder(NavigationDrawerItemBinding binding, NavigationDrawerAdapter.NavigationDrawerAdapterCallbacks listener, float offset) {
        super(binding);
        binding.getRoot().setOnClickListener(this);
        mListener = listener;
        setState(new LinearLeftFadeInAnimateState(getLayout(), offset));
    }

    public static NavigationDrawerPrimaryViewHolder create(ViewGroup parent, final NavigationDrawerAdapter.NavigationDrawerAdapterCallbacks listener, float offset) {
        return new NavigationDrawerPrimaryViewHolder(
                (NavigationDrawerItemBinding) createViewBinding(parent.getContext(), R.layout.item_navigation_drawer_item, parent),
                listener,
                offset);
    }

    public static void bind(NavigationDrawerPrimaryViewHolder holder, NavigationDrawerItem navigationDrawerItem) {
        holder.getLayout().setSelected(navigationDrawerItem.isSelected());
        if (navigationDrawerItem.getIcon() != 0) {
            holder.getViewBinding().itemIcon.setImageDrawable(holder.getLayout().getResources().getDrawable(navigationDrawerItem.getIcon()));
        }
        holder.getViewBinding().title.setText(navigationDrawerItem.getTitle());
        holder.getViewBinding().divider.setBackgroundColor(navigationDrawerItem.getBottomColor());
    }

    public LinearLayout getLayout() {
        return getViewBinding().navigationLinearLayout;
    }

    public TextView getTitle() {
        return getViewBinding().title;
    }

    @Override
    public void onClick(View v) {
        mListener.onNavigationItemSelected(getPosition());
    }

}