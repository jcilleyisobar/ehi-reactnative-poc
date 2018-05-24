package com.ehi.enterprise.android.ui.reservation.widget;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.SparseArray;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.databinding.EditAdditionalInfoViewBinding;
import com.ehi.enterprise.android.models.reservation.EHIAdditionalInformation;
import com.ehi.enterprise.android.ui.view.DataBindingViewModelView;
import com.isobar.android.viewmodel.ViewModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ViewModel(EditAdditionalInfoViewModel.class)
public class EditAdditionalInfoView extends DataBindingViewModelView<EditAdditionalInfoViewModel, EditAdditionalInfoViewBinding> {

    private List<AdditionalInfoViewHolder> mHolders;

    private AdditionalInfoViewHolder.OnDataChangedListener onDataChangedListener;

    //region constructors
    public EditAdditionalInfoView(Context context) {
        this(context, null, 0);
    }

    public EditAdditionalInfoView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EditAdditionalInfoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (!isInEditMode()) {
            createViewBinding(R.layout.v_edit_additional_info_view);
        }
    }
    //endregion

    public void setInformation(String contractDescription, List<EHIAdditionalInformation> additionalInformation, List<EHIAdditionalInformation> alreadyAddedInformation) {
        getViewBinding().containerViewGroup.removeAllViews();

        if (contractDescription == null || contractDescription.length() == 0) {
            getViewBinding().descriptionView.setVisibility(GONE);
        } else {
            getViewBinding().descriptionView.setText(contractDescription);
        }

        Map<String, EHIAdditionalInformation> mapsAddedInformation = new HashMap<>();

        if (alreadyAddedInformation != null && alreadyAddedInformation.size() > 0) {
            for (EHIAdditionalInformation added : alreadyAddedInformation) {
                mapsAddedInformation.put(added.getId(), added);
            }
        }

        if (additionalInformation.size() == 0) {
            setVisibility(GONE);
        }

        mHolders = new ArrayList<>(additionalInformation.size());

        SparseArray<EHIAdditionalInformation> temporary = new SparseArray<>();
        for (int i = 0; i < additionalInformation.size(); i++) {
            temporary.append(additionalInformation.get(i).getSequence(), additionalInformation.get(i));
        }

        additionalInformation = new ArrayList<>();
        for (int i = 0; i < temporary.size(); i++) {
            additionalInformation.add(temporary.valueAt(i));
        }

        for (int i = 0; i < additionalInformation.size(); i++) {
            EHIAdditionalInformation information = additionalInformation.get(i);
            EHIAdditionalInformation addedValue = mapsAddedInformation.get(information.getId());
            if (addedValue != null) {
                information.setValue(addedValue.getValue());
            }
            AdditionalInfoViewHolder holder = AdditionalInfoViewHolder.create(this, information);
            holder.setOnDataChangedListener(onDataChangedListener);
            getViewBinding().containerViewGroup.addView(holder.getViewBinding().getRoot());
            mHolders.add(holder);
        }

    }

    /**
     * Used to check to see if fields are valid, if not it also ensures they check
     * themselves to know if they are invalid and display visual cue to user.
     *
     * @return
     */
    public boolean isValid() {
        if (mHolders == null || mHolders.size() == 0) {
            return true;
        }
        boolean isValid = true;
        for (AdditionalInfoViewHolder holder : mHolders) {
            if (!holder.isValid()) {
                isValid = false;
            }
        }
        return isValid;
    }

    public boolean isValidEntrySet() {
        if (mHolders == null || mHolders.size() == 0) {
            return true;
        }
        for (AdditionalInfoViewHolder holder : mHolders) {
            if (!holder.isValidEntry()) {
                return false;
            }
        }
        return true;
    }

    public List<EHIAdditionalInformation> getAdditionalInformation() {
        if (mHolders == null || mHolders.size() == 0) {
            return null;
        }
        List<EHIAdditionalInformation> list = new ArrayList<>(mHolders.size());
        for (int i = 0; i < mHolders.size(); i++) {
            EHIAdditionalInformation info = mHolders.get(i).getInformation();
            if (!TextUtils.isEmpty(info.getValue())) {
                list.add(info);
            }
        }
        return list;
    }

    public void hideSectionHeader() {
        getViewBinding().sectionHeader.setVisibility(GONE);
    }

    public void setOnDataChangedListener(AdditionalInfoViewHolder.OnDataChangedListener onDataChangedListener) {
        this.onDataChangedListener = onDataChangedListener;
    }
}
