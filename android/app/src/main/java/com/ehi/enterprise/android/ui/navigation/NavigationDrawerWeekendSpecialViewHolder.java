package com.ehi.enterprise.android.ui.navigation;

import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.ehi.enterprise.android.databinding.WeekendSpecialNavigationViewBinding;
import com.ehi.enterprise.android.ui.navigation.animation.AnimatableDataBindingViewHolder;
import com.ehi.enterprise.android.ui.navigation.widget.WeekendSpecialNavigationView;

public class NavigationDrawerWeekendSpecialViewHolder
        extends AnimatableDataBindingViewHolder<WeekendSpecialNavigationViewBinding>
        implements View.OnClickListener {

    private NavigationDrawerAdapter.NavigationDrawerAdapterCallbacks mListener;

    public NavigationDrawerWeekendSpecialViewHolder(WeekendSpecialNavigationViewBinding binding, NavigationDrawerAdapter.NavigationDrawerAdapterCallbacks listener, float offset) {
        super(binding);
        mListener = listener;
        getViewBinding().learnMoreButton.setOnClickListener(this);
        setState(new AnimatableDataBindingViewHolder.LinearLeftFadeInAnimateState((LinearLayout) getViewBinding().getRoot(), offset));
    }

    public static NavigationDrawerWeekendSpecialViewHolder create(final ViewGroup parent, NavigationDrawerAdapter.NavigationDrawerAdapterCallbacks listener, float offset) {
        WeekendSpecialNavigationView view = new WeekendSpecialNavigationView(parent.getContext(), false);
        return new NavigationDrawerWeekendSpecialViewHolder(view.getViewBinding(), listener, offset);
    }

    @Override
    public LinearLayout getLayout() {
        return (LinearLayout) getViewBinding().getRoot();
    }

    public static void bind(NavigationDrawerWeekendSpecialViewHolder holder, NavigationDrawerItem navigationDrawerItem) {
        holder.getViewBinding().title.setText(navigationDrawerItem.getTitle());
    }

    @Override
    public void onClick(View v) {
        mListener.onNavigationItemSelected(getPosition());
    }

}