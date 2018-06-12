package com.ehi.enterprise.android.utils.reactor_extensions.viewstate;

import java.util.List;

import io.dwak.reactor.ReactorVar;

public class ReactorSpinnerViewState extends ReactorViewState{

    public static final class PopulateMethodParamsBox extends ReactorVar{

        private List<CharSequence> mItemsToShow;
        private Integer mSelectedValueIndex;
        private CharSequence mTitle;

        public List<CharSequence> getItemsToShow() {
            return mItemsToShow;
        }

        public void setItemsToShow(List<CharSequence> itemsToShow) {
            mItemsToShow = itemsToShow;
        }

        public Integer getSelectedValueIndex() {
            return mSelectedValueIndex;
        }

        public void setSelectedValueIndex(Integer selectedValueIndex) {
            mSelectedValueIndex = selectedValueIndex;
        }

        public CharSequence getTitle() {
            return mTitle;
        }

        public void setTitle(CharSequence title) {
            mTitle = title;
        }
    }

    private PopulateMethodParamsBox mPopulateMethodParamsBox = new PopulateMethodParamsBox();

    public PopulateMethodParamsBox getPopulateMethodParamsBox() {
        return mPopulateMethodParamsBox;
    }

    public void populateView(List<CharSequence> items, int selectedValueIndex, CharSequence title) {
        mPopulateMethodParamsBox.setItemsToShow(items);
        mPopulateMethodParamsBox.setSelectedValueIndex(selectedValueIndex);
        mPopulateMethodParamsBox.setTitle(title);
        mPopulateMethodParamsBox.setValue(new Object());
    }

}
