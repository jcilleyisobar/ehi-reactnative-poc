package com.ehi.enterprise.android.ui.reservation.widget.time_selection;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.utils.DisplayUtils;

public class DividersDecoration extends RecyclerView.ItemDecoration {

    private Drawable mPlainDivider;
    private Drawable mTransparentDivider;
    private Drawable mBoldDivider;
    private int mDividerHeight;
    private int mMode;

    public DividersDecoration(Context context, @TimeAdapter.AdapterMode int mode) {
        mMode = mode;
        mDividerHeight = (int) DisplayUtils.dipToPixels(context, 1);
        mPlainDivider = context.getResources().getDrawable(R.drawable.sh_time_divider);
        mBoldDivider = context.getResources().getDrawable(R.drawable.sh_bold_time_divider);
        mTransparentDivider = context.getResources().getDrawable(R.drawable.color_transparent);
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        final int left = parent.getPaddingLeft();
        final int right = parent.getWidth() - parent.getPaddingRight();

        int overscrollItems = 0;
        if (parent.getAdapter() instanceof TimeAdapter) {
            overscrollItems = ((TimeAdapter) parent.getAdapter()).getOverscrollItemsCount() / 2;
        }

        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);

            int position = parent.getChildPosition(child);
            if (position < overscrollItems - 1
                    || position >= parent.getAdapter().getItemCount() - overscrollItems) {
                continue;
            }

            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                    .getLayoutParams();
            final int top = child.getBottom() + params.bottomMargin;
            final int bottom = top + mDividerHeight;


            if (mMode == TimeAdapter.MODE_BACKGROUND) {
                TimeAdapter adapter = (TimeAdapter) parent.getAdapter();
                EHITimeSpan thisSpan = adapter.getItemForPosition(position);
                EHITimeSpan nextSpan = adapter.getItemForPosition(position + 1);
                if (thisSpan != null
                        && nextSpan != null
                        && thisSpan.getWorkingSpanType() != nextSpan.getWorkingSpanType()) {
                    mBoldDivider.setBounds(left, top, right, bottom);
                    mBoldDivider.draw(c);
                } else {
                    mPlainDivider.setBounds(left, top, right, bottom);
                    mPlainDivider.draw(c);
                }
            } else if (mMode == TimeAdapter.MODE_FOREGROUND) {
                mTransparentDivider.setBounds(left, top, right, bottom);
                mTransparentDivider.draw(c);
            }
        }
    }

//	@Override
//	public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
//
//	}

}
