package com.ehi.enterprise.android.ui.widget;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v4.view.PagerAdapter;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.databinding.ViewPagerViewBinding;
import com.ehi.enterprise.android.utils.DLog;

public class ViewPagerIndicator extends LinearLayout {
    private PagerAdapter mAdapter;
    private ViewPagerViewBinding mViewBinding;
    private static final String TAG = "VIEW_PAGER_INDICATOR";

    public ViewPagerIndicator(Context context) {
        this(context, null);
    }

    public ViewPagerIndicator(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ViewPagerIndicator(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mViewBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.view_pager_indicator, this, true);
    }

    public void setAdapter(PagerAdapter pagerAdapter) {
        if (pagerAdapter == null) {
            DLog.e(TAG ,"Pager Adapter can`t be null");
            return;
        }
        mAdapter = pagerAdapter;
        notifyDataSetChanged();
    }

    private void notifyDataSetChanged() {
        mViewBinding.viewPagerIndicator.removeAllViews();
        for (int i = 0; i < mAdapter.getCount(); i++) {
            View v = new View(getContext());
            LinearLayout.LayoutParams lParams = new LayoutParams((int)getResources().getDimension(R.dimen.view_pager_indicator_size), (int)getResources().getDimension(R.dimen.view_pager_indicator_size));
            if (i > 0) {
                lParams.setMargins((int)getResources().getDimension(R.dimen.margin_small), 0, 0, 0);
            }
            v.setLayoutParams(lParams);
            mViewBinding.viewPagerIndicator.addView(v);
        }
        updatePageIndicator(0);
    }

    public void updatePageIndicator(int position) {
        if (mViewBinding.viewPagerIndicator.getChildCount() > 0) {
            for (int i = 0; i < mViewBinding.viewPagerIndicator.getChildCount(); i++) {
                mViewBinding.viewPagerIndicator.getChildAt(i).setBackgroundResource(R.drawable.round_gray_shape);
            }
            mViewBinding.viewPagerIndicator.getChildAt(position).setBackgroundResource(R.drawable.round_black_shape);
        }
    }
}
