package com.ehi.enterprise.android.ui.reservation;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.ehi.enterprise.android.models.location.solr.EHISolrWorkingDayInfo;
import com.ehi.enterprise.android.ui.reservation.interfaces.OnTimeSelectListener;
import com.ehi.enterprise.android.ui.reservation.widget.time_selection.TimeSelectionView;

public class TimePagerAdapter extends PagerAdapter {

    private TimeSelectionView mPickupTimeView;
    private TimeSelectionView mReturnTimeView;

    private OnTimeSelectListener mOnTimeSelectListener;

    public TimePagerAdapter(OnTimeSelectListener onTimeSelectListener) {
        mOnTimeSelectListener = onTimeSelectListener;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        switch (position) {
            case 0:
                mPickupTimeView = new TimeSelectionView(container.getContext());
                mPickupTimeView.setSelectionMode(TimeSelectionView.MODE_PICKUP_TIME);
                mPickupTimeView.setOnTimeSelectListener(mOnTimeSelectListener);
                container.addView(mPickupTimeView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                return mPickupTimeView;
            case 1:
            default:
                mReturnTimeView = new TimeSelectionView(container.getContext());
                mReturnTimeView.setSelectionMode(TimeSelectionView.MODE_RETURN_TIME);
                mReturnTimeView.setOnTimeSelectListener(mOnTimeSelectListener);
                container.addView(mReturnTimeView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                return mReturnTimeView;
        }
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    public void setPickupWorkingDayInfo(EHISolrWorkingDayInfo pickupDayInfo) {
        mPickupTimeView.setWorkingDayInfo(pickupDayInfo);

    }

    public void setReturnWorkingDayInfo(EHISolrWorkingDayInfo returDayInfo) {
        mReturnTimeView.setWorkingDayInfo(returDayInfo);

    }
}
