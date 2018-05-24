package com.ehi.enterprise.android.ui.reservation;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.databinding.TripPurposeFragmentBinding;
import com.ehi.enterprise.android.ui.fragment.DataBindingViewModelFragment;
import com.ehi.enterprise.android.ui.viewmodel.ManagersAccessViewModel;
import com.isobar.android.newinstancer.NoExtras;
import com.isobar.android.viewmodel.ViewModel;

@NoExtras
@ViewModel(ManagersAccessViewModel.class)
public class TripPurposeFragment extends DataBindingViewModelFragment<ManagersAccessViewModel, TripPurposeFragmentBinding> {

    private static final String TAG = "TripPurposeFragment";

    public static final String TRIP_TYPE = "TRIP_TYPE";
    public static final String TRIP_TYPE_BUSINESS = "BUSINESS";
    public static final String TRIP_TYPE_LEISURE = "LEISURE";

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view == getViewBinding().businessButton) {
                Intent intent = new Intent();
                intent.putExtra(TRIP_TYPE, TRIP_TYPE_BUSINESS);
                getActivity().setResult(Activity.RESULT_OK, intent);
                getActivity().finish();
            } else if (view == getViewBinding().leisureButton) {
                Intent intent = new Intent();
                intent.putExtra(TRIP_TYPE, TRIP_TYPE_LEISURE);
                getActivity().setResult(Activity.RESULT_OK, intent);
                getActivity().finish();
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        createViewBinding(inflater, R.layout.fr_trip_purpose, container);
        initViews();
        return getViewBinding().getRoot();
    }

    private void initViews() {
        getViewBinding().businessButton.setOnClickListener(mOnClickListener);
        getViewBinding().leisureButton.setOnClickListener(mOnClickListener);
    }

}