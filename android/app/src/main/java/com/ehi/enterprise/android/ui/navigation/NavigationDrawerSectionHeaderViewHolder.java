package com.ehi.enterprise.android.ui.navigation;

import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.ehi.enterprise.android.databinding.NavigationSectionHeaderBinding;
import com.ehi.enterprise.android.ui.navigation.animation.AnimatableDataBindingViewHolder;
import com.ehi.enterprise.android.ui.navigation.widget.NavigationSectionHeaderView;

public class NavigationDrawerSectionHeaderViewHolder extends AnimatableDataBindingViewHolder<NavigationSectionHeaderBinding> {

    public NavigationDrawerSectionHeaderViewHolder(NavigationSectionHeaderBinding binding, float offset) {
        super(binding);
        setState(new AnimatableDataBindingViewHolder.LinearLeftFadeInAnimateState((LinearLayout) getViewBinding().getRoot(), offset));
    }

    public static NavigationDrawerSectionHeaderViewHolder create(final ViewGroup parent, float offset) {
        NavigationSectionHeaderView view = new NavigationSectionHeaderView(parent.getContext(), false);
        return new NavigationDrawerSectionHeaderViewHolder(view.getViewBinding(), offset);
    }

    @Override
    public LinearLayout getLayout() {
        return (LinearLayout) getViewBinding().getRoot();
    }

    public static void bind(NavigationDrawerSectionHeaderViewHolder holder, NavigationDrawerItem navigationDrawerItem) {
        holder.getViewBinding().title.setText(navigationDrawerItem.getTitle());
    }

}