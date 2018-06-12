package com.ehi.enterprise.android.ui.reservation.view_pager;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.ehi.enterprise.android.utils.image.EHIImageLoader;

import java.util.ArrayList;

public class ImageViewPagerAdapter extends PagerAdapter {

	ArrayList<String> mImages;
	Context mContext;

	public ImageViewPagerAdapter(ArrayList<String> images, Context context) {
		mImages = images;
		mContext = context;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView(((View) object));
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		ImageView newView = new ImageView(mContext);
		if (mImages.get(position) != null) {
			EHIImageLoader.with(mContext).load(mImages.get(position)).into(newView);
		}
		else {
			View blankView = new View(mContext);
			container.addView(blankView, position);
			return blankView;
		}
		container.addView(newView, position);
		return newView;
	}

	@Override
	public int getCount() {
		return mImages.size();
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view == object;
	}
}
