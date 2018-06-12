package com.ehi.enterprise.android.ui.reservation.widget.time_selection;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.IntDef;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.ui.reservation.view_holders.TimeViewHolder;
import com.ehi.enterprise.android.ui.reservation.widget.time_selection.snap_scroll.IOverscrollAdapter;
import com.ehi.enterprise.android.utils.DisplayUtils;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;

public class TimeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements IOverscrollAdapter {

    public interface OnTimeClickListener {
        void onTimeClicked(int position);
    }

    private final Context mContext;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({
            VIEW_TYPE_FOOTER_HEADER,
            VIEW_TYPE_TIME
    })
    public @interface ViewType {
    }

    public static final int VIEW_TYPE_FOOTER_HEADER = 0;
    public static final int VIEW_TYPE_TIME = 1;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({
            MODE_BACKGROUND,
            MODE_FOREGROUND
    })
    public @interface AdapterMode {
    }

    public static final int MODE_BACKGROUND = 0;
    public static final int MODE_FOREGROUND = 1;

    @AdapterMode
    private int mMode;

    private int mOverscrollItemsCount;

    private List<EHITimeSpan> mSpansList;
    private OnTimeClickListener mListener;
    private boolean mAllDayLocation = true;

    public TimeAdapter(Context context, List<EHITimeSpan> spansList, @AdapterMode int mode) {
        mContext = context;
        mMode = mode;
        mSpansList = spansList;
        mOverscrollItemsCount = (int) (DisplayUtils.getScreenHeight(context) / context.getResources().getDimension(R.dimen.time_cell_size));

        // we need equals amount of overscroll items in the beginning and in the end
        // so if your screen fits like 13
        // this will cut it down to 12
        mOverscrollItemsCount = (mOverscrollItemsCount / 2) * 2;
    }

    public void setAllDayLocation(boolean allDayLocation) {
        mAllDayLocation = allDayLocation;
    }

    public void setOnTimeClickListener(OnTimeClickListener listener) {
        mListener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, @ViewType int viewType) {
        switch (viewType) {
            case VIEW_TYPE_FOOTER_HEADER: {
                View view = new FrameLayout(parent.getContext());
                view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) parent.getContext().getResources().getDimension(R.dimen.time_cell_size)));
                return new RecyclerView.ViewHolder(view) {
                };
            }
            case VIEW_TYPE_TIME:
            default:
                return TimeViewHolder.create(parent.getContext(), parent);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final Resources resources = mContext.getResources();

        if (!(position < mOverscrollItemsCount / 2
                || position >= getItemCount() - mOverscrollItemsCount / 2)) {
            TimeViewHolder timeViewHolder = (TimeViewHolder) holder;
            EHITimeSpan span = getItemForPosition(position);
            timeViewHolder.getViewBinding().dotView.setVisibility(View.INVISIBLE);
            if (mMode == MODE_BACKGROUND) {
                timeViewHolder.getViewBinding().text.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mListener != null) {
                            mListener.onTimeClicked(position);
                        }
                    }
                });
                if (span.getWorkingSpanType() == EHITimeSpan.TYPE_WORKING_TIME) {
                    timeViewHolder.getViewBinding().text.setBackgroundColor(resources.getColor(R.color.time_selection_background_available));
                    timeViewHolder.getViewBinding().text.setTextColor(resources.getColor(R.color.ehi_black));
                } else {
                    if (span.getClosedSpanType() == EHITimeSpan.TYPE_AFTER_HOURS_TIME) {
                        timeViewHolder.getViewBinding().text.setBackgroundColor(resources.getColor(R.color.time_selection_background_not_available));
                        timeViewHolder.getViewBinding().text.setTextColor(resources.getColor(R.color.ehi_black));
                        timeViewHolder.getViewBinding().dotView.setVisibility(View.VISIBLE);
                    } else if (span.getWorkingSpanType() == EHITimeSpan.TYPE_NOT_WORKING_TIME) {
                        timeViewHolder.getViewBinding().text.setBackgroundColor(resources.getColor(R.color.time_selection_background_not_available));
                        timeViewHolder.getViewBinding().text.setTextColor(resources.getColor(R.color.time_selection_divider));
                    }
                }
            } else if (mMode == MODE_FOREGROUND) {
                timeViewHolder.getViewBinding().text.setTextColor(resources.getColor(R.color.white));
                if (span.getWorkingSpanType() == EHITimeSpan.TYPE_WORKING_TIME) {
                    timeViewHolder.getViewBinding().text.setBackgroundColor(resources.getColor(R.color.time_selection_foreground_available));
                } else {
                    if (span.getClosedSpanType() == EHITimeSpan.TYPE_AFTER_HOURS_TIME) {
                        timeViewHolder.getViewBinding().text.setBackgroundColor(resources.getColor(R.color.time_selection_foreground_available));
                    } else if (span.getWorkingSpanType() == EHITimeSpan.TYPE_NOT_WORKING_TIME) {
                        timeViewHolder.getViewBinding().text.setBackgroundColor(resources.getColor(R.color.time_selection_foreground_not_available));
                    }
                }
            }
            timeViewHolder.getViewBinding().text.setText(span.getFormattedTimeString(mContext));
        } else {
            if (mMode == MODE_BACKGROUND) {
                if (mAllDayLocation) {
                    holder.itemView.setBackgroundColor(resources.getColor(R.color.white));
                } else {
                    holder.itemView.setBackgroundColor(resources.getColor(R.color.time_selection_background_not_available));
                }
            } else if (mMode == MODE_FOREGROUND) {
                if (mAllDayLocation) {
                    holder.itemView.setBackgroundColor(resources.getColor(R.color.ehi_primary));
                } else {
                    holder.itemView.setBackgroundColor(resources.getColor(R.color.time_selection_foreground_not_available));
                }
            }
        }
    }

    public EHITimeSpan getItemForPosition(int position) {
        try {
            return mSpansList.get(position - mOverscrollItemsCount / 2);
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    @Override
    public int getItemCount() {
        return mSpansList.size() + mOverscrollItemsCount;
    }

    @Override
    public
    @ViewType
    int getItemViewType(int position) {
        if (position < mOverscrollItemsCount / 2
                || position >= getItemCount() - mOverscrollItemsCount / 2) {
            return VIEW_TYPE_FOOTER_HEADER;
        } else {
            return VIEW_TYPE_TIME;
        }
    }

    @Override
    public int getOverscrollItemsCount() {
        return mOverscrollItemsCount;
    }

}
