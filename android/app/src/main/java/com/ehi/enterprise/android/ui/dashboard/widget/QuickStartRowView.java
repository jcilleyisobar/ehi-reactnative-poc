package com.ehi.enterprise.android.ui.dashboard.widget;

import android.content.Context;
import android.support.annotation.DrawableRes;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.SwipeDismissBehavior;
import android.util.AttributeSet;
import android.view.View;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.databinding.QuickStartRowBinding;
import com.ehi.enterprise.android.models.reservation.ReservationInformation;
import com.ehi.enterprise.android.ui.view.DataBindingViewModelView;
import com.ehi.enterprise.android.ui.viewmodel.ManagersAccessViewModel;
import com.ehi.enterprise.android.utils.TimeUtils;
import com.isobar.android.viewmodel.ViewModel;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Calendar;


@ViewModel(ManagersAccessViewModel.class)
public class QuickStartRowView extends DataBindingViewModelView<ManagersAccessViewModel, QuickStartRowBinding> {


    @Retention(RetentionPolicy.SOURCE)
    @IntDef({ABANDONED, FAVORITE, RECENT})
    public @interface RowType {
    }

    public static final int ABANDONED = 0;
    public static final int FAVORITE = 1;
    public static final int RECENT = 2;

    private ReservationInformation mHolder;
    private OnClickListener mCallbackOnClickListener;
    private QuickStartRowDismissListener mOnDismissListener;

    private int mRowType;

    private OnClickListener mOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            if (mCallbackOnClickListener != null) {
                mCallbackOnClickListener.onClick(QuickStartRowView.this);
            }
        }
    };

    public QuickStartRowView(Context context) {
        this(context, null, 0);
    }

    public QuickStartRowView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public QuickStartRowView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        createViewBinding(R.layout.v_dashboard_root_cell);
        initViews();
    }

    private void initViews() {
        getViewBinding().cellRootView.setOnClickListener(mOnClickListener);

        SwipeDismissBehavior<View> swipeDismissBehavior = new SwipeDismissBehavior<>();
        swipeDismissBehavior.setListener(new SwipeDismissBehavior.OnDismissListener() {
            @Override
            public void onDismiss(View view) {
                if (mOnDismissListener != null) {
                    mOnDismissListener.onDismiss(view, mHolder);
                }
            }

            @Override
            public void onDragStateChanged(int i) {
                switch (i) {
                    case SwipeDismissBehavior.STATE_DRAGGING:
                        mOnDismissListener.onDrag(true);
                        break;
                    case SwipeDismissBehavior.STATE_IDLE:
                        mOnDismissListener.onDrag(false);
                        break;
                }
            }
        });
        ((CoordinatorLayout.LayoutParams) getViewBinding().cellRootView.getLayoutParams()).setBehavior(swipeDismissBehavior);
    }

    public void setupView(@NonNull ReservationInformation holder, @RowType int rowType) {
        mRowType = rowType;
        mHolder = holder;

        String headerText = "";
        String locationText = "";
        String subText = "";
        @DrawableRes int iconResId = 0;

        if (holder.getPickupLocation() == null) {
            setVisibility(GONE);
            return;
        }

        switch (rowType) {
            case ABANDONED:
                if (holder.getReturnLocation() == null) {
                    locationText = holder.getPickupLocation().getTranslatedLocationName();
                } else {
                    locationText = holder.getPickupLocation().getTranslatedLocationName() + " - " + holder.getReturnLocation().getTranslatedLocationName();
                }
                if (holder.getPickupDate() != null && TimeUtils.mergeDateTime(holder.getPickupDate(), holder.getPickupTime()).after(Calendar.getInstance())) {
                    subText = TimeUtils.getDayYear(getContext(), holder.getPickupDate());

                    if (holder.getReturnDate() != null) {
                        subText += " - " + TimeUtils.getDayYear(getContext(), holder.getReturnDate());
                    }
                }
                iconResId = R.drawable.icon_search_03;
                headerText = getResources().getString(R.string.quickstart_abandoned_type);
                break;
            case FAVORITE:
                iconResId = R.drawable.icon_favorites_03;
                headerText = getResources().getString(R.string.quickstart_favorite_type);
                locationText = holder.getPickupLocation().getTranslatedLocationName();
                break;
            case RECENT:
                iconResId = holder.getPickupLocation().getMapPinDrawable(false);
                headerText = getResources().getString(R.string.quickstart_past_type);
                locationText = holder.getPickupLocation().getTranslatedLocationName();
                break;
            default:
                break;
        }

        getViewBinding().cellHeader.setText(headerText);
        getViewBinding().cellLocationName.setText(locationText);
        getViewBinding().cellSubtext.setText(subText);
        getViewBinding().cellIcon.setImageResource(iconResId);
        cleanView();
    }

    private void cleanView() {
        getViewBinding().cellHeader.setVisibility((getViewBinding().cellHeader.getText().toString().length() == 0)
                ? GONE
                : VISIBLE);

        getViewBinding().cellSubtext.setVisibility((getViewBinding().cellSubtext.getText().toString().length() == 0)
                ? GONE
                : VISIBLE);

        getViewBinding().cellLocationName.setVisibility((getViewBinding().cellLocationName.getText().toString().length() == 0)
                ? GONE
                : VISIBLE);
    }

    public void setCustomOnClickListener(OnClickListener clickListener) {
        mCallbackOnClickListener = clickListener;
    }

    public void setOnDismissListener(QuickStartRowDismissListener onDismissListener) {
        mOnDismissListener = onDismissListener;
    }

    public ReservationInformation getHolder() {
        return mHolder;
    }

    public View getContainer() {
        return getViewBinding().cellRootView;
    }

    public
    @RowType
    int getRowType() {
        return mRowType;
    }

    public interface QuickStartRowDismissListener {
        void onDismiss(View view, ReservationInformation reservationInformation);

        void onDrag(boolean isDragging);
    }
}
