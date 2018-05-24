package com.ehi.enterprise.android.ui.navigation;

import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.databinding.StartRentalItemBinding;
import com.ehi.enterprise.android.ui.navigation.animation.AnimatableDataBindingViewHolder;

public class NavigationDrawerButtonViewHolder
        extends AnimatableDataBindingViewHolder<StartRentalItemBinding>
        implements View.OnClickListener {

    private final NavigationDrawerAdapter.NavigationDrawerAdapterCallbacks mListener;

    public NavigationDrawerButtonViewHolder(StartRentalItemBinding binding, NavigationDrawerAdapter.NavigationDrawerAdapterCallbacks listener, float offset) {
        super(binding);
        binding.getRoot().setOnClickListener(this);
        mListener = listener;
        setState(new LinearLeftFadeInAnimateState(getLayout(), offset));
    }

    public static NavigationDrawerButtonViewHolder create(ViewGroup parent, final NavigationDrawerAdapter.NavigationDrawerAdapterCallbacks listener, float offset) {
        return new NavigationDrawerButtonViewHolder(
                (StartRentalItemBinding) createViewBinding(parent.getContext(), R.layout.item_navigation_drawer_start_rental, parent),
                listener,
                offset);
    }

    public LinearLayout getLayout() {
        return getViewBinding().navigationLinearLayout;
    }

    @Override
    public void onClick(View v) {
        mListener.onNavigationItemSelected(getPosition());
    }

}