package com.ehi.enterprise.android.ui.navigation;

import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.databinding.NavigationSignInBinding;
import com.ehi.enterprise.android.ui.navigation.animation.AnimatableDataBindingViewHolder;

public class NavigationDrawerSignInViewHolder extends AnimatableDataBindingViewHolder<NavigationSignInBinding> implements View.OnClickListener  {

    private final NavigationDrawerAdapter.NavigationDrawerAdapterCallbacks mListener;

    public NavigationDrawerSignInViewHolder(NavigationSignInBinding binding, NavigationDrawerAdapter.NavigationDrawerAdapterCallbacks listener, float offset) {
        super(binding);
        binding.getRoot().setOnClickListener(this);
        mListener = listener;
        setState(new LinearLeftFadeInAnimateState(getLayout(), offset));
    }

    public static NavigationDrawerSignInViewHolder create(ViewGroup parent, final NavigationDrawerAdapter.NavigationDrawerAdapterCallbacks listener, float offset) {
        return new NavigationDrawerSignInViewHolder(
                (NavigationSignInBinding) createViewBinding(parent.getContext(), R.layout.item_navigation_drawer_sign_in, parent),
                listener,
                offset);
    }

    public static void bind(NavigationDrawerSignInViewHolder holder, NavigationDrawerItem navigationDrawerItem) {
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