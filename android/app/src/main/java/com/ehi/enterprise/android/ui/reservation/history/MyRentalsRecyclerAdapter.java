package com.ehi.enterprise.android.ui.reservation.history;

import android.content.Context;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.v4.util.Pair;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.ehi.enterprise.android.models.reservation.EHIReservation;
import com.ehi.enterprise.android.models.reservation.EHITripSummary;
import com.ehi.enterprise.android.ui.reservation.history.viewholder.MyRentalsEmptyListCellViewHolder;
import com.ehi.enterprise.android.ui.reservation.history.viewholder.MyRentalsFooterViewHolder;
import com.ehi.enterprise.android.ui.reservation.history.viewholder.MyRentalsInfoMessageViewHolder;
import com.ehi.enterprise.android.ui.reservation.history.viewholder.MyRentalsLoadMoreButtonViewHolder;
import com.ehi.enterprise.android.ui.reservation.history.viewholder.MyRentalsMissingPastRentalsButtonViewHolder;
import com.ehi.enterprise.android.ui.reservation.history.viewholder.MyRentalsPastTripViewHolder;
import com.ehi.enterprise.android.ui.reservation.history.viewholder.MyRentalsSelectorViewHolder;
import com.ehi.enterprise.android.ui.reservation.history.viewholder.MyRentalsTripViewHolder;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MyRentalsRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    @NonNull
    private Context mContext;
    @NonNull
    private ReservationAdapterListener mListener;
    @NonNull
    private List<MyRentalsRecyclerItem> mList;
    private Set<String> mAddedUpcomingRentals;
    private Set<String> mAddedPastRentals;
    private Set<String> mAddedCurrentRentals;
    private Set<Integer> mFooters;
    private int mFirstFooterPosition = 1;
    private int mLastInfoMessagePosition = 0;
    private boolean mLoadMoreButtonAdded;
    private boolean mMissingRentalsButtonAdded;
    private int mEmptyCellPosition = -1;

    public MyRentalsRecyclerAdapter(@NonNull Context context, @NonNull ReservationAdapterListener listener) {
        mContext = context;
        mListener = listener;
        mList = new ArrayList<>();
        mAddedUpcomingRentals = new HashSet<>();
        mAddedPastRentals = new HashSet<>();
        mAddedCurrentRentals = new HashSet<>();
        mFooters = new HashSet<>();
    }

    public void showEmptyCell(@NonNull String message, boolean showLookUp) {
        if (mEmptyCellPosition == -1) {
            mEmptyCellPosition = mLastInfoMessagePosition + 1;
            mList.add(
                    mEmptyCellPosition,
                    new MyRentalsRecyclerItem<>(
                            new MyRentalsEmptyListCellViewHolder.EmptyListCellData(message, showLookUp),
                            MyRentalsRecyclerItem.EMPTY_CELL
                    )
            );
            notifyItemInserted(mEmptyCellPosition);
            mFirstFooterPosition++;
        }
    }

    public void removeEmptyCell() {
        if (mEmptyCellPosition > 0) {
            mList.remove(mEmptyCellPosition);
            notifyItemRemoved(mEmptyCellPosition);
            mEmptyCellPosition = -1;
        }
    }

    public void addSelector() {
        //noinspection unchecked
        mList.add(0, new MyRentalsRecyclerItem(null, MyRentalsRecyclerItem.SELECTOR));
        notifyItemInserted(0);
    }

    public void addInfoMessage(@NonNull String message, @DrawableRes int iconResId) {
        Pair<String, Integer> infoPair = new Pair<>(message, iconResId);
        final int insertPosition = mLastInfoMessagePosition + 1;
        mList.add(
                insertPosition,
                new MyRentalsRecyclerItem<>(infoPair, MyRentalsRecyclerItem.INFO_MESSAGE)
        );
        notifyItemInserted(insertPosition);
        mLastInfoMessagePosition++;
        mFirstFooterPosition++;
    }

    public void addCurrentTrip(EHITripSummary ehiTripSummary) {
        if (!mAddedCurrentRentals.contains(ehiTripSummary.getConfirmationNumber())) {
            removeEmptyCell();
            final int insertPosition = mLastInfoMessagePosition + 1;
            mList.add(
                    insertPosition,
                    new MyRentalsRecyclerItem<>(ehiTripSummary, MyRentalsRecyclerItem.CURRENT_TRIP)
            );
            mAddedCurrentRentals.add(ehiTripSummary.getConfirmationNumber());
            notifyItemInserted(insertPosition);
            mFirstFooterPosition++;
        }
    }


    public void addPastTrip(@NonNull EHITripSummary tripSummary) {
        if (!mAddedPastRentals.contains(tripSummary.getInvoiceNumber())) {
            removeEmptyCell();
            final int insertPosition = mFirstFooterPosition;
            mList.add(
                    insertPosition,
                    new MyRentalsRecyclerItem<>(tripSummary, MyRentalsRecyclerItem.PAST_TRIP)
            );
            mAddedPastRentals.add(tripSummary.getInvoiceNumber());
            notifyItemInserted(insertPosition);
            mFirstFooterPosition++;
        }
    }

    public void addUpcomingTrip(@NonNull EHITripSummary tripSummary) {
        if (!mAddedUpcomingRentals.contains(tripSummary.getConfirmationNumber())) {
            removeEmptyCell();
            final int insertPosition = mFirstFooterPosition;
            mList.add(
                    insertPosition,
                    new MyRentalsRecyclerItem<>(tripSummary, MyRentalsRecyclerItem.UPCOMING_TRIP)
            );
            mAddedUpcomingRentals.add(tripSummary.getConfirmationNumber());
            notifyItemInserted(insertPosition);
            mFirstFooterPosition++;
        }
    }

    public void removeLoadMoreButton() {
        int removalIndex = -1;
        for (MyRentalsRecyclerItem myRentalsRecyclerItem : mList) {
            if (myRentalsRecyclerItem.viewType == MyRentalsRecyclerItem.LOAD_MORE) {
                removalIndex = mList.indexOf(myRentalsRecyclerItem);
                break;
            }
        }

        if (removalIndex > 0) {
            mList.remove(removalIndex);
            notifyItemRemoved(removalIndex);
            mFirstFooterPosition--;
            mLoadMoreButtonAdded = false;
        }
    }

    public void addLoadMoreButton(@NonNull String buttonText) {
        if (!mLoadMoreButtonAdded) {
            mLoadMoreButtonAdded = true;
            final int insertPosition = mFirstFooterPosition;
            mList.add(
                    insertPosition,
                    new MyRentalsRecyclerItem<>(buttonText, MyRentalsRecyclerItem.LOAD_MORE)
            );
            notifyItemInserted(insertPosition);
            mFirstFooterPosition++;
        }
    }

    public void addFooter(MyRentalsFooter footer) {
        if (!mFooters.contains(footer.buttonText)) {
            mList.add(new MyRentalsRecyclerItem<>(footer, MyRentalsRecyclerItem.FOOTER));
            notifyItemInserted(mList.size());
            mFooters.add(footer.buttonText);
        }
    }

    public void addFooter(int footerPosition, MyRentalsFooter footer) {
        if (!mFooters.contains(footer.buttonText)) {
            final int insertPosition = mFirstFooterPosition + footerPosition;
            mList.add(insertPosition, new MyRentalsRecyclerItem<>(footer, MyRentalsRecyclerItem.FOOTER));
            notifyItemInserted(insertPosition);

            mFooters.add(footer.buttonText);

        }
    }

    public void addMissingRentalsButton() {
        if (!mMissingRentalsButtonAdded) {
            mMissingRentalsButtonAdded = true;
            final int insertPosition = mFirstFooterPosition;
            mList.add(
                    insertPosition,
                    new MyRentalsRecyclerItem<>(null, MyRentalsRecyclerItem.MISSING_PAST_RENTAL)
            );
            notifyItemInserted(insertPosition);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, @MyRentalsRecyclerItem.ViewType int viewType) {
        switch (viewType) {
            case MyRentalsRecyclerItem.SELECTOR:
                return MyRentalsSelectorViewHolder.create(mContext, parent);
            case MyRentalsRecyclerItem.INFO_MESSAGE:
                return MyRentalsInfoMessageViewHolder.create(mContext, parent);
            case MyRentalsRecyclerItem.FOOTER:
                return MyRentalsFooterViewHolder.create(mContext, parent);
            case MyRentalsRecyclerItem.PAST_TRIP:
                return MyRentalsPastTripViewHolder.create(mContext, parent);
            case MyRentalsRecyclerItem.CURRENT_TRIP:
                return MyRentalsTripViewHolder.create(mContext, parent);
            case MyRentalsRecyclerItem.UPCOMING_TRIP:
                return MyRentalsTripViewHolder.create(mContext, parent);
            case MyRentalsRecyclerItem.LOAD_MORE:
                return MyRentalsLoadMoreButtonViewHolder.create(mContext, parent);
            case MyRentalsRecyclerItem.EMPTY_CELL:
                return MyRentalsEmptyListCellViewHolder.create(mContext, parent);
            case MyRentalsRecyclerItem.MISSING_PAST_RENTAL:
                return MyRentalsMissingPastRentalsButtonViewHolder.create(mContext, parent);
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case MyRentalsRecyclerItem.SELECTOR:
                MyRentalsSelectorViewHolder.bind((MyRentalsSelectorViewHolder) holder, mListener);
                break;
            case MyRentalsRecyclerItem.INFO_MESSAGE:
                //noinspection unchecked
                MyRentalsInfoMessageViewHolder.bind(
                        (MyRentalsInfoMessageViewHolder) holder,
                        (Pair<String, Integer>) mList.get(position).object
                );
                break;
            case MyRentalsRecyclerItem.FOOTER:
                MyRentalsFooterViewHolder.bind(
                        (MyRentalsFooterViewHolder) holder,
                        (MyRentalsFooter) mList.get(position).object,
                        position == getItemCount() - 1,
                        mListener
                );
                break;
            case MyRentalsRecyclerItem.CURRENT_TRIP:
                MyRentalsTripViewHolder.bind(
                        mContext,
                        (MyRentalsTripViewHolder) holder,
                        (EHITripSummary) mList.get(position).object,
                        mList.get(position).viewType,
                        true,
                        mListener
                );
                break;
            case MyRentalsRecyclerItem.UPCOMING_TRIP:
                MyRentalsTripViewHolder.bind(
                        mContext,
                        (MyRentalsTripViewHolder) holder,
                        (EHITripSummary) mList.get(position).object,
                        mList.get(position).viewType,
                        false,
                        mListener
                );
                break;
            case MyRentalsRecyclerItem.PAST_TRIP:
                MyRentalsPastTripViewHolder.bind(
                        mContext,
                        (MyRentalsPastTripViewHolder) holder,
                        (EHITripSummary) mList.get(position).object,
                        mListener
                );
                break;
            case MyRentalsRecyclerItem.LOAD_MORE:
                MyRentalsLoadMoreButtonViewHolder.bind(
                        (MyRentalsLoadMoreButtonViewHolder) holder,
                        mListener
                );
                break;
            case MyRentalsRecyclerItem.EMPTY_CELL:
                MyRentalsEmptyListCellViewHolder.bind(
                        (MyRentalsEmptyListCellViewHolder) holder,
                        (MyRentalsEmptyListCellViewHolder.EmptyListCellData) mList.get(position).object,
                        mListener
                );
                break;
            case MyRentalsRecyclerItem.MISSING_PAST_RENTAL:
                MyRentalsMissingPastRentalsButtonViewHolder.bind(
                        (MyRentalsMissingPastRentalsButtonViewHolder) holder,
                        mListener
                );
                break;
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    @Override
    public
    @MyRentalsRecyclerItem.ViewType
    int getItemViewType(int position) {
        return mList.get(position).viewType;
    }

    public interface ReservationAdapterListener {
        void onSelectorChanged(int position);

        void onReservationActionClicked(EHIReservation reservation);

        void onUpcomingRentalClicked(EHITripSummary ehiTripSummary);

        void onTripClicked(EHITripSummary tripSummary);

        void onLoadMoreClicked();

        void onLookUpRentalClicked();

        void onMissingRentalsClicked();
    }

}
