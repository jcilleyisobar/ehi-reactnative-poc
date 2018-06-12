package com.ehi.enterprise.android.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

import com.ehi.enterprise.android.ui.adapter.view_holders.AnimatingDataBindingViewHolder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SectionedRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final int SECTION_TYPE = 0;
    private final Context mContext;
    private ItemBoundCallback mItemCallback;
    private boolean mValid = true;
    private RecyclerView.Adapter mBaseAdapter;
    private SparseArray<SectionHeader> mSections = new SparseArray<>();
    private List<Integer> mExemptViewTypes;
    private boolean mShouldGetMockedViewHolder = false;

    public SectionedRecyclerViewAdapter(@NonNull Context context, @NonNull RecyclerView.Adapter baseAdapter) {
        this(context, baseAdapter, null);
    }

    public SectionedRecyclerViewAdapter(@NonNull Context context, @NonNull RecyclerView.Adapter baseAdapter, ItemBoundCallback itemBoundCallback) {
        mContext = context;
        mBaseAdapter = baseAdapter;
        mItemCallback = itemBoundCallback;
        mBaseAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                mValid = mBaseAdapter.getItemCount() > 0;
                notifyDataSetChanged();
            }

            @Override
            public void onItemRangeChanged(int positionStart, int itemCount) {
                mValid = mBaseAdapter.getItemCount() > 0;
                notifyItemRangeChanged(positionStart, itemCount);
            }

            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                mValid = mBaseAdapter.getItemCount() > 0;
                notifyItemRangeInserted(positionStart, itemCount);
            }

            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                mValid = mBaseAdapter.getItemCount() > 0;
                notifyItemRangeRemoved(positionStart, itemCount);
            }

        });
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int typeView) {
        if (typeView == SECTION_TYPE) {
            return SectionHeaderViewHolder.create(mContext, parent);
        }

        return mBaseAdapter.onCreateViewHolder(parent, typeView - 1);
    }

    public void hideItems(List<Integer> exemptViewTypes) {
        mExemptViewTypes = exemptViewTypes;
    }

    public void revealItems() {
        mExemptViewTypes = null;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder sectionViewHolder, int position) {
        if (isSectionHeaderPosition(position)) {
            SectionHeaderViewHolder.bind((SectionHeaderViewHolder) sectionViewHolder, mSections.get(position));
        }
        else {
            mBaseAdapter.onBindViewHolder(sectionViewHolder, sectionedPositionToPosition(position));
        }

        if (mExemptViewTypes != null && !mExemptViewTypes.contains(getItemViewType(position) - 1) && !mShouldGetMockedViewHolder) {
            sectionViewHolder.itemView.setVisibility(View.GONE);
        }
        else {
            sectionViewHolder.itemView.setVisibility(View.VISIBLE);
        }

        if (mItemCallback != null &&
                !mItemCallback.recyclerItemBound(sectionViewHolder, position)) {
            mItemCallback = null;
        }

    }

    @Override
    public int getItemViewType(int position) {
        return isSectionHeaderPosition(position)
                ? SECTION_TYPE
                : mBaseAdapter.getItemViewType(sectionedPositionToPosition(position)) + 1;
    }

    public void setSections(SectionHeader header) {
        ArrayList<SectionHeader> sections = new ArrayList<>();
        sections.add(header);
        setSections(sections);
    }

    public void setSections(List<SectionHeader> sections) {

        mSections.clear();

        if (sections.isEmpty()) {
            return;
        }

        Collections.sort(sections, new Comparator<SectionHeader>() {
            @Override
            public int compare(SectionHeader o, SectionHeader o1) {
                return (o.mFirstPosition == o1.mFirstPosition)
                        ? 0
                        : ((o.mFirstPosition < o1.mFirstPosition) ? -1 : 1);
            }
        });

        int offset = 0; // offset positions for the headers adding
        for (SectionHeader section : sections) {
            section.mSectionedPosition = section.mFirstPosition + offset;
            mSections.append(section.mSectionedPosition, section);
            ++offset;
        }

        notifyDataSetChanged();
    }

    public int sectionedPositionToPosition(int sectionedPosition) {
        if (isSectionHeaderPosition(sectionedPosition)) {
            return RecyclerView.NO_POSITION;
        }

        int offset = 0;
        for (int i = 0, size = mSections.size(); i < size; i++) {
            if (mSections.valueAt(i).mSectionedPosition > sectionedPosition) {
                break;
            }
            --offset;
        }
        return sectionedPosition + offset;
    }

    public boolean isSectionHeaderPosition(int position) {
        return mSections.get(position) != null;
    }

    @Override
    public long getItemId(int position) {
        return isSectionHeaderPosition(position)
                ? Integer.MAX_VALUE - mSections.indexOfKey(position)
                : mBaseAdapter.getItemId(sectionedPositionToPosition(position));
    }

    @Override
    public int getItemCount() {
        return (mValid ? mBaseAdapter.getItemCount() + mSections.size() : 0);
    }

    public RecyclerView.ViewHolder getViewHolder(int position, ViewGroup parent, boolean preventClickListeners) {
        mShouldGetMockedViewHolder = true;
        final RecyclerView.ViewHolder holder = onCreateViewHolder(parent, getItemViewType(position));
        onBindViewHolder(holder, position);

        if (preventClickListeners && !isSectionHeaderPosition(position) && holder instanceof AnimatingDataBindingViewHolder) {
            ((AnimatingDataBindingViewHolder) holder).startAnimation();
        }
        mShouldGetMockedViewHolder = false;
        return holder;
    }



    public interface ItemBoundCallback {
        /**
         * @param holder
         * @return false if no longer listening to callback, true if listener is still valid
         */
        boolean recyclerItemBound(RecyclerView.ViewHolder holder, int position);
    }
}