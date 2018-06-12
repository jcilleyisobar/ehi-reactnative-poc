package com.ehi.enterprise.android.ui.reservation;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.models.reservation.EHICarClassDetails;
import com.ehi.enterprise.android.models.reservation.EHICharge;
import com.ehi.enterprise.android.models.reservation.EHIExtraItem;
import com.ehi.enterprise.android.ui.reservation.interfaces.OnExtraActionListener;
import com.ehi.enterprise.android.ui.reservation.interfaces.OnHeaderActionListener;
import com.ehi.enterprise.android.ui.reservation.interfaces.ReservationFlowListener;
import com.ehi.enterprise.android.ui.reservation.view_controllers.CarClassSelectViewController;
import com.ehi.enterprise.android.ui.reservation.view_holders.CarClassSelectViewHolder;
import com.ehi.enterprise.android.ui.reservation.view_holders.CustomizeExtrasHeaderViewHolder;
import com.ehi.enterprise.android.ui.reservation.view_holders.ExtraIncludedViewHolder;
import com.ehi.enterprise.android.ui.reservation.view_holders.ExtraMandatoryViewHolder;
import com.ehi.enterprise.android.ui.reservation.view_holders.ExtraPlaceholderViewHolder;
import com.ehi.enterprise.android.ui.reservation.view_holders.ExtraViewHolder;

import java.util.ArrayList;
import java.util.List;

public class CarClassExtrasAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int VIEW_TYPE_HEADER = 0;
    public static final int VIEW_TYPE_HEADER_CUSTOMIZE = 5;
    public static final int VIEW_TYPE_FOOTER = 6;
    private static final int VIEW_TYPE_INCLUDED = 1;
    private static final int VIEW_TYPE_PLAIN = 2;
    private static final int VIEW_TYPE_MANDATORY = 3;
    private static final int VIEW_TYPE_PLACEHOLDER = 4;

    private List<EHIExtraItem> mExtraItems = new ArrayList<>();
    private OnExtraActionListener mListener;
    private OnHeaderActionListener mHeaderActionListener;
    private View.OnClickListener mRentalTermsConditionsListener;
    private EHICarClassDetails mCarClass;
    private CarClassSelectViewController mReservationCarClassSelectViewController;
    private boolean mShowPoints = false;
    private boolean mAnimatedOnce = false;
    private ReservationFlowListener.PayState mPayState;
    private boolean mFromChooseYourRate;
    private List<EHICharge> mEhiCharges;

    public CarClassExtrasAdapter(List<EHIExtraItem> extraItemsList,
                                 boolean needShowPoints,
                                 final ReservationFlowListener.PayState payState,
                                 final boolean fromChooseYourRate,
                                 final List<EHICharge> ehiCharges) {
        mExtraItems = extraItemsList;
        mPayState = payState;
        mFromChooseYourRate = fromChooseYourRate;
        mReservationCarClassSelectViewController = new CarClassSelectViewController();
        mShowPoints = needShowPoints;
        mEhiCharges = ehiCharges;
    }

    public void setCarClass(EHICarClassDetails carClass) {
        mCarClass = carClass;
    }

    public void setOnExtraActionListener(OnExtraActionListener listener) {
        mListener = listener;
    }

    public void setOnHeaderActionListener(OnHeaderActionListener headerActionListener) {
        mHeaderActionListener = headerActionListener;
    }

    public void setOnRentalTermsConditionsListener(View.OnClickListener rentalTermsConditionsListener) {
        mRentalTermsConditionsListener = rentalTermsConditionsListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View cell;
        switch (viewType) {
            case VIEW_TYPE_HEADER:
                CarClassSelectViewHolder header = CarClassSelectViewHolder.create(parent.getContext(), parent, false);
                header.getViewBinding().negotiatedRateContainer.setVisibility(View.GONE);
                header.getViewBinding().bigPriceContainer.setAlpha(0);
                return header;
            case VIEW_TYPE_HEADER_CUSTOMIZE:
                cell = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_customize_extras_header, parent, false).getRoot();
                return new CustomizeExtrasHeaderViewHolder(cell);
            case VIEW_TYPE_INCLUDED:
                return ExtraIncludedViewHolder.create(parent);
            case VIEW_TYPE_MANDATORY:
                return ExtraMandatoryViewHolder.create(parent);
            case VIEW_TYPE_PLACEHOLDER:
                return ExtraPlaceholderViewHolder.create(parent);
            case VIEW_TYPE_FOOTER:
                return FooterExtrasViewHolder.create(parent);
            case VIEW_TYPE_PLAIN:
            default:
                return ExtraViewHolder.create(parent.getContext(), parent);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case VIEW_TYPE_HEADER: {
                final CarClassSelectViewHolder extraHolder = (CarClassSelectViewHolder) holder;
                extraHolder.getViewBinding().classDetailsContainer.setVisibility(View.GONE);
                extraHolder.getViewBinding().priceContainer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (mHeaderActionListener != null) {
                            mHeaderActionListener.onTotalCostClicked();
                        }
                    }
                });

                mReservationCarClassSelectViewController.fillCellWithData(
                        extraHolder.getViewBinding().getRoot().getContext(),
                        extraHolder,
                        mCarClass,
                        null,
                        0,
                        false,
                        mShowPoints,
                        true,
                        mAnimatedOnce,
                        false,
                        true,
                        mPayState,
                        mFromChooseYourRate,
                        mEhiCharges,
                        false
                );
                break;
            }
            case VIEW_TYPE_HEADER_CUSTOMIZE:
                View itemView = holder.itemView;
                if (mShowPoints) {
                    itemView.setPadding(itemView.getPaddingLeft(),
                            itemView.getPaddingBottom(),
                            itemView.getPaddingRight(),
                            itemView.getPaddingBottom());
                } else {
                    itemView.setPadding(itemView.getPaddingLeft(),
                            0,
                            itemView.getPaddingRight(),
                            itemView.getPaddingBottom());
                }
                break;
            case VIEW_TYPE_INCLUDED: {
                ExtraIncludedViewHolder extraHolder = (ExtraIncludedViewHolder) holder;
                final EHIExtraItem item = mExtraItems.get(position - 2);
                extraHolder.getViewBinding().title.setText(item.getName());
                extraHolder.getViewBinding().about.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (mListener != null) {
                            mListener.onClick(item);
                        }
                    }
                });
                break;
            }
            case VIEW_TYPE_MANDATORY: {
                ExtraMandatoryViewHolder extraHolder = (ExtraMandatoryViewHolder) holder;
                final EHIExtraItem item = mExtraItems.get(position - 2);
                extraHolder.getViewBinding().title.setText(item.getName());
                extraHolder.getViewBinding().totalItemAmount.setText(item.getTotalAmountView().getFormattedPrice(false));
                extraHolder.getViewBinding().about.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (mListener != null) {
                            mListener.onClick(item);
                        }
                    }
                });
                break;
            }
            case VIEW_TYPE_PLACEHOLDER: {
                ExtraPlaceholderViewHolder extraHolder = (ExtraPlaceholderViewHolder) holder;
                final EHIExtraItem item = mExtraItems.get(position - 2);
                extraHolder.getViewBinding().title.setText(item.getName());
                break;
            }
            case VIEW_TYPE_FOOTER: {
                FooterExtrasViewHolder footerHolder = (FooterExtrasViewHolder) holder;
                footerHolder.getViewBinding().rentalTermsConditionsView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (mRentalTermsConditionsListener != null) {
                            mRentalTermsConditionsListener.onClick(null);
                        }
                    }
                });

                break;
            }
            case VIEW_TYPE_PLAIN:
            default: {
                ExtraViewHolder extraHolder = (ExtraViewHolder) holder;
                EHIExtraItem item = mExtraItems.get(position - 2);
                extraHolder.getViewBinding().extraItemView.setExtraItem(item);
                extraHolder.getViewBinding().extraItemView.setOnExtraActionListener(mListener);
                break;
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return VIEW_TYPE_HEADER;
        }
        if (position == 1) {
            return VIEW_TYPE_HEADER_CUSTOMIZE;
        }
        if (position == getItemCount() - 1) {
            return VIEW_TYPE_FOOTER;
        }
        if (mExtraItems.get(position - 2).getStatus().equals(EHIExtraItem.INCLUDED)) {
            return VIEW_TYPE_INCLUDED;
        } else if (mExtraItems.get(position - 2).getStatus().equals(EHIExtraItem.MANDATORY)) {
            return VIEW_TYPE_MANDATORY;
        } else if (mExtraItems.get(position - 2).getStatus().equals(EHIExtraItem.EMPTY)) {
            return VIEW_TYPE_PLACEHOLDER;
        } else {
            return VIEW_TYPE_PLAIN;
        }
    }

    @Override
    public int getItemCount() {
        return mExtraItems.size() + 3;
    }

    public void setAnimatedOnce(boolean animatedOnce) {
        this.mAnimatedOnce = animatedOnce;
    }

}
