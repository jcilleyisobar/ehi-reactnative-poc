package com.ehi.enterprise.android.ui.reservation.widget.time_selection;

import android.animation.Animator;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.databinding.TimeSelectionViewBinding;
import com.ehi.enterprise.android.models.location.solr.EHISolrWorkingDayInfo;
import com.ehi.enterprise.android.ui.reservation.interfaces.OnTimeSelectListener;
import com.ehi.enterprise.android.ui.reservation.widget.time_selection.snap_scroll.SnappyLinearLayoutManager;
import com.ehi.enterprise.android.ui.view.DataBindingViewModelView;
import com.ehi.enterprise.android.ui.viewmodel.ManagersAccessViewModel;
import com.isobar.android.viewmodel.ViewModel;

import java.util.LinkedList;
import java.util.List;

@ViewModel(ManagersAccessViewModel.class)
public class TimeSelectionView extends DataBindingViewModelView<ManagersAccessViewModel, TimeSelectionViewBinding> {

    private static final String TAG = TimeSelectionView.class.getSimpleName();

    public static final int MODE_PICKUP_TIME = 1;
    public static final int MODE_RETURN_TIME = 2;

    private SnappyLinearLayoutManager mBgLayoutManager;
    private TimeAdapter mBgAdapter;

    private TimeAdapter mFgAdapter;

    private List<EHITimeSpan> mTimeSpanList = new LinkedList<>();

    private OnTimeSelectListener mListener;

    private EHISolrWorkingDayInfo mWorkingDayInfo;
    private int mSelectionMode;

    private TimeAdapter.OnTimeClickListener mOnTimeClickListener = new TimeAdapter.OnTimeClickListener() {

        @Override
        public void onTimeClicked(int position) {
            getViewBinding().backgroundRecyclerView.smoothScrollToPosition(position);
        }
    };

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v == getViewBinding().selectTimeButton) {
                onTimeSpanSelected(getCenteredTimeSpan());
            } else  if (v == getViewBinding().searchForLocationsButton) {
                mListener.onSearchOpenLocationsInMapClicked(mSelectionMode, getCenteredTimeSpan().getTime());
            }
        }
    };

    public TimeSelectionView(Context context) {
        this(context, null, 0);
    }

    public TimeSelectionView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TimeSelectionView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        createViewBinding(R.layout.v_time_selection);
        initViews();
        if (!isInEditMode()) {
            populateTimeList();
            initListAdapter();
        }
    }

    private void initListAdapter() {
        mBgLayoutManager = new SnappyLinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        mBgAdapter = new TimeAdapter(getContext(), mTimeSpanList, TimeAdapter.MODE_BACKGROUND);
        mBgAdapter.setOnTimeClickListener(mOnTimeClickListener);
        getViewBinding().backgroundRecyclerView.setLayoutManager(mBgLayoutManager);
        getViewBinding().backgroundRecyclerView.setAdapter(mBgAdapter);
        getViewBinding().backgroundRecyclerView.addItemDecoration(new DividersDecoration(getContext(), TimeAdapter.MODE_BACKGROUND));
        getViewBinding().backgroundRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                getViewBinding().foregroundRecyclerView.scrollBy(dx, dy);
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING
                        || newState == RecyclerView.SCROLL_STATE_SETTLING) {
                    hideWhenScrolling();
                    hideWarningAreas();
                }
                else if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    checkTimeSpan();
                    showWarningForCentralSpan();
                }
            }
        });

        mFgAdapter = new TimeAdapter(getContext(), mTimeSpanList, TimeAdapter.MODE_FOREGROUND);
        getViewBinding().foregroundRecyclerView.setLayoutManager( new SnappyLinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        getViewBinding().foregroundRecyclerView.setAdapter(mFgAdapter);
        getViewBinding().foregroundRecyclerView.addItemDecoration(new DividersDecoration(getContext(), TimeAdapter.MODE_FOREGROUND));
        getViewBinding().foregroundRecyclerView.setTouchEnabled(false);
        getViewBinding().foregroundRecyclerView.setCutToSize((int) getResources().getDimension(R.dimen.time_cell_size));

        mBgLayoutManager.setAnimateScroll(true);
    }

    private void initViews() {
        getViewBinding().selectTimeButton.setOnClickListener(mOnClickListener);
        getViewBinding().searchForLocationsButton.setOnClickListener(mOnClickListener);
    }

    private void populateTimeList() {
        mTimeSpanList.clear();
        if (mWorkingDayInfo == null) {
            return;
        }
        long halfOfhour = 30 * 60 * 1000;
        for (int i = 0; i < 48; i++) {
            EHITimeSpan span = new EHITimeSpan(i * halfOfhour);
            if (mWorkingDayInfo.getStandardTime().isEffective24Hours()
                    || (mWorkingDayInfo != null
                    && mWorkingDayInfo.isOpenAtTime(span.getTime()))) {
                span.setWorkingSpanType(EHITimeSpan.TYPE_WORKING_TIME);
            }
            if (mSelectionMode == MODE_RETURN_TIME
                    && ((mWorkingDayInfo.getDropTime() != null && mWorkingDayInfo.getDropTime().isEffective24Hours())
                    || (mWorkingDayInfo != null && mWorkingDayInfo.isAfterHoursAtTime(span.getTime())))) {
                span.setClosedSpanType(EHITimeSpan.TYPE_AFTER_HOURS_TIME);
            }
            mTimeSpanList.add(span);
        }
    }

    @Nullable
    private EHITimeSpan getCenteredTimeSpan() {
        return mBgAdapter.getItemForPosition(mBgLayoutManager.getCenterElementPosition());
    }

    private void onTimeSpanSelected(EHITimeSpan span) {
        if (mListener != null && span != null) {
            if (span.getWorkingSpanType() == EHITimeSpan.TYPE_WORKING_TIME
                    || (span.getClosedSpanType() == EHITimeSpan.TYPE_AFTER_HOURS_TIME))
                mListener.onTimeSelected(span.getTime());
        }
    }

    private boolean isWarningAreaVisible() {
        return getViewBinding().warningArea.getAlpha() == 1.0f;
    }

    private void hideWarningAreas() {
        if (isWarningAreaVisible()) {
            getViewBinding().warningArea.setClickable(false);
            getViewBinding().warningArea.animate().setDuration(150).alpha(0);
        }
        getViewBinding().invalidReturnTimeContainer.setVisibility(GONE);
    }

    private void showWarningArea() {
        getViewBinding().warningArea.setClickable(true);
        getViewBinding().warningArea.animate().setDuration(150).alpha(1);
    }

    private void showWarningForCentralSpan() {
        EHITimeSpan span = getCenteredTimeSpan();
        if (!isWarningAreaVisible() && span != null) {

            if (span.getClosedSpanType() == EHITimeSpan.TYPE_AFTER_HOURS_TIME
                    && span.getWorkingSpanType() != EHITimeSpan.TYPE_WORKING_TIME) {
                getViewBinding().warningIcon.setImageResource(R.drawable.icon_info_01);
                getViewBinding().warningMessage.setText(R.string.time_selection_location_after_hours_title);
                getViewBinding().warningMessage.setTextColor(getResources().getColor(R.color.time_selection_foreground_available));
                showWarningArea();

                getViewBinding().warningArea.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (mListener != null) {
                            mListener.onAfterHoursTitleClicked();
                        }
                    }
                });
            }

            int positionInList = mTimeSpanList.indexOf(span);

            EHITimeSpan nextSpan = null;
            if (positionInList + 1 < mTimeSpanList.size()) {
                nextSpan = mTimeSpanList.get(positionInList + 1);
            }

            boolean nextSpanIsClosed = nextSpan != null
                    && nextSpan.getWorkingSpanType() == EHITimeSpan.TYPE_NOT_WORKING_TIME
                    && (nextSpan.getClosedSpanType() == EHITimeSpan.TYPE_NOT_WORKING_TIME
                    || nextSpan.getClosedSpanType() == EHITimeSpan.TYPE_AFTER_HOURS_TIME);

            if (!mWorkingDayInfo.getStandardTime().isEffective24Hours()
                    && span.getWorkingSpanType() == EHITimeSpan.TYPE_WORKING_TIME
                    && nextSpanIsClosed) {
                //last item in list
                getViewBinding().warningIcon.setImageResource(R.drawable.icon_info_01);
                if (mSelectionMode == MODE_PICKUP_TIME) {
                    getViewBinding().warningMessage.setText(R.string.time_selection_location_pickup_close_time_title);
                    getViewBinding().warningMessage.setTextColor(getResources().getColor(R.color.ehi_primary_dark));
                    showWarningArea();
                    getViewBinding().warningArea.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (mListener != null) {
                                if (mSelectionMode == MODE_PICKUP_TIME) {
                                    mListener.onLastPickupTimeClicked();
                                } else {
                                    mListener.onLastReturnTimeClicked();
                                }
                            }
                        }
                    });
                } else {
                    getViewBinding().invalidReturnTimeContainer.setVisibility(VISIBLE);
                }
            }
        }
    }

    private boolean isSelectButtonVisible() {
        return getViewBinding().selectTimeButton.getAlpha() == 1.0f;
    }

    private void hideWhenScrolling() {
        if (isSelectButtonVisible()) {
            getViewBinding().selectTimeButton.animate().setDuration(150).alpha(0.1f);
            getViewBinding().arrow.animate().setDuration(150).alpha(0.1f);
        }
    }

    private void checkTimeSpan() {
        final EHITimeSpan span = getCenteredTimeSpan();
        if (span != null) {
            if (span.getWorkingSpanType() == EHITimeSpan.TYPE_NOT_WORKING_TIME
                    && span.getClosedSpanType() != EHITimeSpan.TYPE_AFTER_HOURS_TIME) {
                getViewBinding().selectTimeButton.setText(R.string.location_details_hours_closed);
                getViewBinding().arrow.animate().setDuration(150).alpha(0);
                animateSearchOpenLocationsContainerUp();
            } else {
                getViewBinding().arrow.animate().setDuration(150).alpha(1);
                getViewBinding().selectTimeButton.setText(R.string.time_picker_button_title);
                animateSearchOpenLocationsContainerDown();
            }
        }
        getViewBinding().selectTimeButton.animate().setDuration(150).alpha(1);
    }

    private void animateSearchOpenLocationsContainerUp() {
        if (getViewBinding().searchOpenLocationsContainer.getVisibility() != View.VISIBLE) {
            final int startingY = getViewBinding().getRoot().getBottom();
            getViewBinding().searchOpenLocationsContainer.setY(startingY);
            getViewBinding().searchOpenLocationsContainer
                    .animate()
                    .setDuration(300)
                    .translationYBy(-1 * (getViewBinding().searchOpenLocationsContainer.getHeight()))
                    .setListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animator) {
                            getViewBinding().searchOpenLocationsContainer.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onAnimationEnd(@NonNull Animator animator) {
                        }

                        @Override
                        public void onAnimationCancel(Animator animator) {
                        }

                        @Override
                        public void onAnimationRepeat(Animator animator) {
                        }
                    });
        }
    }

    private void animateSearchOpenLocationsContainerDown() {
        if (getViewBinding().searchOpenLocationsContainer.getVisibility() != View.INVISIBLE) {
            final int finalY = getViewBinding().getRoot().getBottom();
            getViewBinding().searchOpenLocationsContainer
                    .animate()
                    .setDuration(300)
                    .translationYBy(finalY)
                    .setListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animator) {
                        }

                        @Override
                        public void onAnimationEnd(@NonNull Animator animator) {
                            getViewBinding().searchOpenLocationsContainer.setVisibility(View.INVISIBLE);
                        }

                        @Override
                        public void onAnimationCancel(Animator animator) {
                        }

                        @Override
                        public void onAnimationRepeat(Animator animator) {
                        }
                    });
        }
    }

    public void setOnTimeSelectListener(OnTimeSelectListener listener) {
        mListener = listener;
    }

    public void setWorkingDayInfo(EHISolrWorkingDayInfo workingDayInfo) {
        if (workingDayInfo.equals(mWorkingDayInfo)) {
            return;
        }
        mWorkingDayInfo = workingDayInfo;
        mBgAdapter.setAllDayLocation(mWorkingDayInfo.getStandardTime().isEffective24Hours());
        mFgAdapter.setAllDayLocation(mWorkingDayInfo.getStandardTime().isEffective24Hours());
        populateTimeList();

        mBgAdapter.notifyDataSetChanged();
        mFgAdapter.notifyDataSetChanged();

        checkTimeSpan();
        hideWarningAreas();

        int noonPosition = mBgAdapter.getItemCount() / 2;
        int closestOpenendToNoon = noonPosition;
        for (int i = 0; i < noonPosition; i++) {
            EHITimeSpan span = mBgAdapter.getItemForPosition(noonPosition + i);
            if (span != null && span.getWorkingSpanType() == EHITimeSpan.TYPE_WORKING_TIME) {
                closestOpenendToNoon = noonPosition + i;
                break;
            }
            span = mBgAdapter.getItemForPosition(noonPosition - i);
            if (span != null && span.getWorkingSpanType() == EHITimeSpan.TYPE_WORKING_TIME) {
                closestOpenendToNoon = noonPosition - i;
                break;
            }
        }
        if (mBgLayoutManager.getCenterElementPosition() == closestOpenendToNoon) {
            showWarningForCentralSpan();
        }
        final int finalClosestOpenendToNoon = closestOpenendToNoon;
        getViewBinding().backgroundRecyclerView.post(new Runnable() {
            @Override
            public void run() {
                getViewBinding().backgroundRecyclerView.smoothScrollToPosition(finalClosestOpenendToNoon);
            }
        });
    }

    public void setSelectionMode(int selectionMode) {
        mSelectionMode = selectionMode;
    }
}
