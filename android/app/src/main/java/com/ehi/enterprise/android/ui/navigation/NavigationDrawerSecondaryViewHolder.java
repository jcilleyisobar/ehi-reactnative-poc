package com.ehi.enterprise.android.ui.navigation;

import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.databinding.SmallerNavigationDrawerItemBinding;
import com.ehi.enterprise.android.ui.navigation.animation.AnimatableDataBindingViewHolder;

public class NavigationDrawerSecondaryViewHolder extends AnimatableDataBindingViewHolder<SmallerNavigationDrawerItemBinding> implements View.OnClickListener {

    private final NavigationDrawerAdapter.NavigationDrawerAdapterCallbacks mListener;

    public NavigationDrawerSecondaryViewHolder(SmallerNavigationDrawerItemBinding binding, NavigationDrawerAdapter.NavigationDrawerAdapterCallbacks listener, float offset) {
        super(binding);
        binding.getRoot().setOnClickListener(this);
        mListener = listener;
        setState(new LinearLeftFadeInAnimateState(getLayout(), offset));
    }

    public static NavigationDrawerSecondaryViewHolder create(ViewGroup parent, final NavigationDrawerAdapter.NavigationDrawerAdapterCallbacks listener, float offset) {
        return new NavigationDrawerSecondaryViewHolder(
                (SmallerNavigationDrawerItemBinding) createViewBinding(parent.getContext(),R.layout.item_navigation_drawer_item_smaller,parent),
                listener,
                offset);
    }

    public static void bind(NavigationDrawerSecondaryViewHolder holder, NavigationDrawerItem navigationDrawerItem) {
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