package com.ehi.enterprise.android.ui.reservation;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.ehi.enterprise.android.models.reservation.EHIAvailableCarFilters;
import com.ehi.enterprise.android.models.reservation.EHICarClassDetails;
import com.ehi.enterprise.android.models.reservation.EHICharge;
import com.ehi.enterprise.android.ui.adapter.view_holders.AnimatingDataBindingViewHolder;
import com.ehi.enterprise.android.ui.reservation.interfaces.ReservationFlowListener;
import com.ehi.enterprise.android.ui.reservation.view_controllers.CarClassSelectViewController;
import com.ehi.enterprise.android.ui.reservation.view_holders.CarClassInfoItemViewHolder;
import com.ehi.enterprise.android.ui.reservation.view_holders.CarClassSelectViewHolder;
import com.ehi.enterprise.android.utils.BaseAppUtils;

import java.util.ArrayList;
import java.util.List;

public class CarClassListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<EHIAvailableCarFilters> mFilters;
    private Context mContext;
    private CarClassListAdapterListener mListener;
    private String mCidName;
    private String mContractType;
    private boolean mIsAnimatingSelectedCar = false;
    private boolean mIsPromotionAvailable;
    private boolean mIsContractAvailable;
    private ReservationFlowListener.PayState mPayState = ReservationFlowListener.PayState.PAY_LATER;
    private boolean mShowClassTotalCostAsterisks;


    @NonNull
    private List<CarClassListItem> mListItems;
    private List<CarClassListItem> mBackUpList = new ArrayList<>();
    private List<EHICarClassDetails> mVanListItems = new ArrayList<>();

    private CarClassSelectViewController mReservationCarClassSelectViewController;

    private boolean mShowingPoints = false;
    private boolean mShowPointsAnimation = false;
    private View.OnClickListener mTermsAndConditionsListener;
    private String currency;

    public CarClassListAdapter(@NonNull Context context,
                               @NonNull CarClassListAdapterListener listener,
                               boolean showClassTotalCostAsterisks) {
        mContext = context;
        mListener = listener;
        mReservationCarClassSelectViewController = new CarClassSelectViewController();
        mListItems = new ArrayList<>();
        mShowPointsAnimation = false;
        mShowClassTotalCostAsterisks = showClassTotalCostAsterisks;
    }

    public void setPayState(ReservationFlowListener.PayState mPayState) {
        this.mPayState = mPayState;
    }

    public void setFilters(List<EHIAvailableCarFilters> availableCarFilters) {
        mFilters = availableCarFilters;
        if (mListItems.size() != 0) {
            applyFilters();
            notifyDataSetChanged();
        }
    }

    public void setCIDInfo(String cidName) {
        mCidName = cidName;
    }

    public void setContractType(String contractType) {
        mContractType = contractType;
    }

    public void setTermsAndConditionsListener(View.OnClickListener listener) {
        mTermsAndConditionsListener = listener;
    }

    public void setPromotionAvailability(boolean isPromotionAvailable) {
        mIsPromotionAvailable = isPromotionAvailable;
    }

    public void setContractAvailability(boolean isContractAvailable) {
        mIsContractAvailable = isContractAvailable;
    }

    private ArrayList<CarClassListItem> copyItems(List<CarClassListItem> items) {
        ArrayList<CarClassListItem> returnItems = new ArrayList<>();
        for (CarClassListItem item : items) {
            returnItems.add(item);
        }
        return returnItems;
    }

    public void applyFilters() {
        if (mBackUpList.size() == 0) {
            mBackUpList = copyItems(mListItems);
        }
        if (mFilters.size() == 0) {
            return;
        }

        List<EHICarClassDetails> list = new ArrayList<>(mBackUpList.size());
        for (int i = 0, size = mBackUpList.size(); i < size; i++) {
            if (mBackUpList.get(i).getViewType() == CarClassListItem.ITEM) {
                list.add((EHICarClassDetails) mBackUpList.get(i).getObject());
            }
        }
        list = EHICarClassDetails.applyFilters(list, list, mFilters);
        mListItems = new ArrayList<>(mBackUpList.size());
        CarClassListItem item;
        boolean contains;
        for (int i = 0, size = mBackUpList.size(); i < size; i++) {
            item = mBackUpList.get(i);
            if (item.getViewType() == CarClassListItem.ITEM) {
                contains = BaseAppUtils.contains(((EHICarClassDetails) item.getObject()).getCode(), list, new BaseAppUtils.CompareTwo<String, EHICarClassDetails>() {
                    @Override
                    public boolean equals(String first, EHICarClassDetails second) {
                        //Return true if first == null because this implies a services error for filtering (Code on EHICarClassDetails should never be null)
                        return first == null || first.equalsIgnoreCase(second.getCode());
                    }
                });
                if (contains) {
                    mListItems.add(item);
                }
            } else {
                mListItems.add(item);
            }
        }
        notifyDataSetChanged();
    }

    public void resetFilters() {
        mListItems = copyItems(mBackUpList);
        mBackUpList.clear();
        for (int i = 0, size = mFilters.size(); i < size; i++) {
            mFilters.get(i).clearFilters();
        }
        notifyDataSetChanged();
    }

    public List<EHIAvailableCarFilters> getFilters() {
        return mFilters;
    }

    public void addCarClasses(@NonNull List<EHICarClassDetails> ehiCarClasses, boolean shouldMoveVansToEndOfList) {
        mVanListItems = new ArrayList<>();
        for (EHICarClassDetails carClass : ehiCarClasses) {
            if (!carClass.getStatus().equalsIgnoreCase(EHICarClassDetails.SOLD_OUT)) {
                // vans go at bottom of the car class list if pickup
                // location locale is not United States or Canada
                // and the class was not previously selected
                if (shouldMoveVansToEndOfList
                        && carClass.getCategory() != null
                        && EHICarClassDetails.CAR_CODE_VAN.equals(carClass.getCategory().getCode())
                        && !carClass.isPreviouslySelected()) {
                    mVanListItems.add(carClass);
                }
                // previously selected car goes at the top
                else if (carClass.isPreviouslySelected()) {
                    mListItems.add(0, new CarClassListItem<>(carClass, CarClassListItem.ITEM));
                } else {
                    mListItems.add(new CarClassListItem<>(carClass, CarClassListItem.ITEM));
                }
            }
        }

        for (EHICarClassDetails vanClass : mVanListItems) {
            mListItems.add(new CarClassListItem<>(vanClass, CarClassListItem.ITEM));
        }

        applyFilters();
    }

    public void addHeader() {
        mListItems.add(0, new CarClassListItem<>(CarClassListItem.HEADER));
        notifyItemInserted(0);
    }

    public void addInfoMessage(String message) {
        mListItems.add(0, new CarClassListItem<>(message, CarClassListItem.INFO_ITEM));
        notifyItemInserted(0);
    }

    public void addFooter() {
        mListItems.add(getItemCount(), new CarClassListItem<>(CarClassListItem.FOOTER));
        notifyItemInserted(getItemCount());
    }

    public void clear() {
        int count = getItemCount();
        mListItems.clear();
        notifyItemRangeRemoved(0, count);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case CarClassListItem.INFO_ITEM:
                return CarClassInfoItemViewHolder.create(mContext, parent);
            case CarClassListItem.HEADER:
                return HeaderViewHolder.create(mContext, parent);
            case CarClassListItem.FOOTER:
                return FooterViewHolder.create(mContext, parent);
            case CarClassListItem.ITEM:
            default:
                return CarClassSelectViewHolder.create(parent.getContext(), parent, true);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        CarClassListItem reservationListItem = mListItems.get(position);
        switch (reservationListItem.getViewType()) {
            case CarClassListItem.INFO_ITEM:
                CarClassInfoItemViewHolder.bind((CarClassInfoItemViewHolder) holder,
                        (String) reservationListItem.getObject());
                break;
            case CarClassListItem.ITEM:
                final CarClassSelectViewHolder viewHolder = (CarClassSelectViewHolder) holder;
                final EHICarClassDetails carClass = (EHICarClassDetails) mListItems.get(position).getObject();
                mReservationCarClassSelectViewController.fillCellWithData(
                        mContext,
                        viewHolder,
                        carClass,
                        mListener,
                        position,
                        true,
                        mShowingPoints,
                        mIsAnimatingSelectedCar,
                        false,
                        true,
                        true,
                        mPayState,
                        false,
                        carClass.getCharge(),
                        mShowPointsAnimation
                );
                break;
            case CarClassListItem.HEADER:
                HeaderViewHolder.bind(
                        (HeaderViewHolder) holder,
                        mListener,
                        mFilters,
                        mCidName,
                        mContractType,
                        mIsContractAvailable,
                        mIsPromotionAvailable,
                        mTermsAndConditionsListener,
                        currency);
                break;
            case CarClassListItem.FOOTER:
                FooterViewHolder.bind((FooterViewHolder) holder, mListener, mShowClassTotalCostAsterisks);
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        return mListItems.get(position).getViewType();
    }

    @Override
    public int getItemCount() {
        return mListItems.size();
    }

    public ArrayList<EHICarClassDetails> getCarClasses() {
        ArrayList<EHICarClassDetails> listOfItems = new ArrayList<>();
        for (int a = 0; a < mListItems.size(); a++) {
            if (mListItems.get(a).getViewType() == CarClassListItem.ITEM) {
                listOfItems.add((EHICarClassDetails) mListItems.get(a).getObject());
            }
        }
        return listOfItems;
    }

    public ArrayList<EHICarClassDetails> getUnfilteredCarClasses() {
        if (mBackUpList.size() == 0) {
            return getCarClasses();
        }
        ArrayList<EHICarClassDetails> listOfItems = new ArrayList<>();
        for (int a = 0; a < mBackUpList.size(); a++) {
            if (mBackUpList.get(a).getViewType() == CarClassListItem.ITEM) {
                listOfItems.add((EHICarClassDetails) mBackUpList.get(a).getObject());
            }
        }
        return listOfItems;
    }

    public RecyclerView.ViewHolder getViewHolder(int position, ViewGroup parent, boolean preventClickListeners) {
        mIsAnimatingSelectedCar = true;
        RecyclerView.ViewHolder holder = onCreateViewHolder(parent, getItemViewType(position));
        onBindViewHolder(holder, position);

        if (preventClickListeners && holder instanceof AnimatingDataBindingViewHolder) {
            ((AnimatingDataBindingViewHolder) holder).startAnimation();
        }

        mIsAnimatingSelectedCar = false;
        return holder;
    }

    public void showPoints(boolean showingPoints, boolean showPointsAnimation) {
        mShowPointsAnimation = showPointsAnimation;
        mShowingPoints = showingPoints;
        notifyDataSetChanged();
    }

    public void setUpAnotherCurrencyText(EHICharge charge) {
        if (charge != null) {
            currency = charge.getFormattedCurrency();
        }
    }

    public interface CarClassListAdapterListener {
        void onTotalCostClicked(EHICarClassDetails carClasses, int position);
        void onClassImageClicked(EHICarClassDetails carClasses, int position);
        void onMoreDetailsClicked(EHICarClassDetails carClasses);
        void onFilterClearButtonClicked();
        void onRentalTermsConditionsClicked();
    }

}