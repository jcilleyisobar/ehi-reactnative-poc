package com.ehi.enterprise.android.ui.support;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.models.profile.EHIPhone;
import com.ehi.enterprise.android.ui.adapter.SectionHeader;
import com.ehi.enterprise.android.ui.adapter.SectionHeaderViewHolder;
import com.ehi.enterprise.android.ui.support.interfaces.OnSupportItemClickListener;
import com.ehi.enterprise.android.ui.support.view_holders.CustomerSupportItemViewHolder;
import com.ehi.enterprise.android.ui.support.view_holders.CustomerSupportMainHeaderViewHolder;

import java.util.ArrayList;
import java.util.List;

public class CustomerSupportAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private OnSupportItemClickListener mOnItemClickListener;
    @NonNull
    private List<CallContactItem> mListItems;
    private boolean mPhoneNumbersHeaderAdded = false;
    private boolean mMoreOptionsHeaderAdded = false;
    private int mHeaderInsertionPointer = 0;
    private final SectionHeader mPhoneNumbersHeader;
    private final SectionHeader mMoreContactOptionsHeader;

    public CustomerSupportAdapter(@NonNull Context context) {
        mContext = context;
        mListItems = new ArrayList<>();
        mPhoneNumbersHeader = SectionHeader.Builder
                .atPosition(0)
                .setTitle(context.getResources().getString(R.string.customer_settings_call_section_header))
                .build();

        final String title = context.getResources().getString(R.string.customer_settings_more_options_section_header_prefix);
        String suffix = context.getResources().getString(R.string.customer_settings_more_options_section_header_suffix);

        mMoreContactOptionsHeader = SectionHeader.Builder
                .atPosition(0)
                .setTitle(title)
                .setSecondaryTitle(suffix)
                .build();
    }

    private boolean addPhoneNumbersHeader() {
        boolean addedHeader = false;
        if (!mPhoneNumbersHeaderAdded) {
            mListItems.add(1, new CallContactItem<>(mPhoneNumbersHeader, CallContactItem.HEADER));
            addedHeader = true;
            mPhoneNumbersHeaderAdded = true;
            mHeaderInsertionPointer++;
        }
        return addedHeader;
    }

    private boolean addMoreOptionsHeader() {
        boolean addedHeader = false;
        if (!mMoreOptionsHeaderAdded) {
            mListItems.add(new CallContactItem<>(mMoreContactOptionsHeader, CallContactItem.HEADER));
            mHeaderInsertionPointer = mListItems.size() - 1;
            addedHeader = true;
            mMoreOptionsHeaderAdded = true;
        }
        return addedHeader;
    }

    public void addMainHeader() {
        mListItems.add(0, new CallContactItem<>(null, CallContactItem.MAIN_HEADER));
        notifyItemInserted(mListItems.size());
    }

    public void addPhoneNumberItem(EHIPhone phone, String phoneTitle, String phoneDesc) {
        boolean addedHeader = addPhoneNumbersHeader();
        mListItems.add(new CallContactItem<>(phone, phoneTitle, phoneDesc, CallContactItem.CALL_ITEM));
        mHeaderInsertionPointer++;
        if (addedHeader) {
            notifyItemRangeInserted(mListItems.size(), 2);
        } else {
            notifyItemInserted(mListItems.size());
        }
    }

    public void addMoreOptionsMessageItem(String messageUrl) {
        boolean addedHeader = addMoreOptionsHeader();
        mListItems.add(new CallContactItem<>(messageUrl, CallContactItem.MESSAGE_ITEM));
        if (addedHeader) {
            notifyItemRangeInserted(mHeaderInsertionPointer, 2);
        } else {
            notifyItemInserted(mListItems.size());
        }
    }

    public void addMoreOptionsSearchItem(String moreContactOptionsItem) {
        boolean addedHeader = addMoreOptionsHeader();
        mListItems.add(new CallContactItem<>(moreContactOptionsItem, CallContactItem.SEARCH_ITEM));
        if (addedHeader) {
            notifyItemRangeInserted(mHeaderInsertionPointer, 2);
        } else {
            notifyItemInserted(mListItems.size());
        }
    }

    public void setSupportPhoneNumbers(@NonNull List<EHIPhone> ehiPhones) {
        for (EHIPhone phoneNumber : ehiPhones) {
            if (!phoneNumber.getPhoneType().equals(EHIPhone.PhoneType.OTHER)) {
                if (phoneNumber.getPhoneType().equals(EHIPhone.PhoneType.CONTACT_US)) {
                    addPhoneNumberItem(phoneNumber,
                            mContext.getResources().getString(R.string.customer_support_contact_us_title),
                            mContext.getResources().getString(R.string.customer_support_contact_us_details));
                } else if (phoneNumber.getPhoneType().equals(EHIPhone.PhoneType.ROADSIDE_ASSISTANCE)) {
                    addPhoneNumberItem(phoneNumber,
                            mContext.getResources().getString(R.string.customer_support_roadside_title),
                            mContext.getResources().getString(R.string.customer_support_roadside_details));
                } else if (phoneNumber.getPhoneType().equals(EHIPhone.PhoneType.EPLUS)) {
                    addPhoneNumberItem(phoneNumber,
                            mContext.getResources().getString(R.string.customer_support_eplus_title),
                            mContext.getResources().getString(R.string.customer_support_eplus_details));
                } else if (phoneNumber.getPhoneType().equals(EHIPhone.PhoneType.DISABILITES)) {
                    addPhoneNumberItem(phoneNumber,
                            mContext.getResources().getString(R.string.customer_support_disabilities),
                            mContext.getResources().getString(R.string.customer_support_disabilities_details));
                }
                notifyItemInserted(mListItems.size());
            }
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case CallContactItem.HEADER:
                return SectionHeaderViewHolder.create(mContext, parent);
            case CallContactItem.MAIN_HEADER:
                return CustomerSupportMainHeaderViewHolder.create(parent.getContext(), parent);
            case CallContactItem.CALL_ITEM:
            case CallContactItem.MESSAGE_ITEM:
            case CallContactItem.SEARCH_ITEM:
            default:
                return CustomerSupportItemViewHolder.create(parent.getContext(), parent);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        switch (getItemViewType(position)) {
            case CallContactItem.MAIN_HEADER:
                CustomerSupportMainHeaderViewHolder.bind((CustomerSupportMainHeaderViewHolder) holder);
                break;
            case CallContactItem.HEADER:
                SectionHeaderViewHolder.bind((SectionHeaderViewHolder) holder, (SectionHeader) getItem(position).getObject());
                break;
            case CallContactItem.CALL_ITEM:
                CustomerSupportItemViewHolder phoneViewHolder = (CustomerSupportItemViewHolder) holder;
                phoneViewHolder.getViewBinding().imagePhone.setVisibility(View.VISIBLE);
                phoneViewHolder.getViewBinding().imageSearch.setVisibility(View.GONE);
                phoneViewHolder.getViewBinding().itemTitle.setText(mListItems.get(position).getTitle());
                phoneViewHolder.getViewBinding().itemNumber.setText(mListItems.get(position).getNumber());
                phoneViewHolder.getViewBinding().itemDescription.setText(mListItems.get(position).getDesc());
                phoneViewHolder.getViewBinding().itemContainer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mOnItemClickListener != null) {
                            mOnItemClickListener.onCallSupportNumber(mListItems.get(position).getPhoneNumber());
                        }
                    }
                });
                break;
            case CallContactItem.MESSAGE_ITEM:
                CustomerSupportItemViewHolder messageViewHolder = (CustomerSupportItemViewHolder) holder;
                final String messageUrl = mListItems.get(position).getStringUrl();
                messageViewHolder.getViewBinding().imagePhone.setVisibility(View.GONE);
                messageViewHolder.getViewBinding().imageSearch.setVisibility(View.GONE);
                messageViewHolder.getViewBinding().itemNumber.setVisibility(View.GONE);
                messageViewHolder.getViewBinding().itemTitle.setText(mContext.getResources().getString(R.string.customer_support_send_message_header));
                messageViewHolder.getViewBinding().itemDescription.setText(mContext.getResources().getString(R.string.customer_support_send_message_details));
                messageViewHolder.getViewBinding().itemContainer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mOnItemClickListener != null) {
                            if (!TextUtils.isEmpty(messageUrl)) {
                                mOnItemClickListener.onMessageLinkOut(messageUrl);
                            }
                        }
                    }
                });
                break;
            case CallContactItem.SEARCH_ITEM:
                CustomerSupportItemViewHolder searchViewHolder = (CustomerSupportItemViewHolder) holder;
                final String searchUrl = mListItems.get(position).getStringUrl();
                searchViewHolder.getViewBinding().imagePhone.setVisibility(View.GONE);
                searchViewHolder.getViewBinding().imageSearch.setVisibility(View.VISIBLE);
                searchViewHolder.getViewBinding().itemNumber.setVisibility(View.GONE);
                searchViewHolder.getViewBinding().itemTitle.setText(mContext.getResources().getString(R.string.customer_support_search_answer_header));
                searchViewHolder.getViewBinding().itemDescription.setText(mContext.getResources().getString(R.string.customer_support_search_answers_details));
                searchViewHolder.getViewBinding().itemContainer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mOnItemClickListener != null) {
                            if (!TextUtils.isEmpty(searchUrl)) {
                                mOnItemClickListener.onSearchLinkOut(searchUrl);
                            }
                        }
                    }
                });
                break;
        }
    }

    private CallContactItem getItem(int position) {
        return mListItems.get(position);
    }

    @Override
    public int getItemViewType(int position) {
        return mListItems.get(position).getViewType();
    }

    @Override
    public int getItemCount() {
        return mListItems.size();
    }

    public void setOnItemClickListener(OnSupportItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    public void clear() {
        int count = getItemCount();
        mListItems.clear();
        notifyItemRangeRemoved(0, count);
    }

}