package com.ehi.enterprise.android.ui.reservation.key_facts.widget;

import android.view.View;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.models.reservation.EHIExtraItem;
import com.ehi.enterprise.android.models.reservation.EHIExtras;
import com.ehi.enterprise.android.ui.viewmodel.ManagersAccessViewModel;
import com.ehi.enterprise.android.utils.ListUtils;
import com.ehi.enterprise.android.utils.reactor_extensions.viewstate.ReactorTextViewState;
import com.ehi.enterprise.android.utils.reactor_extensions.viewstate.ReactorViewState;

import java.util.List;

import io.dwak.reactor.ReactorVar;
import io.dwak.reactor.unbinder.annotation.AutoUnbindAll;

@AutoUnbindAll
public class EquipmentProductsViewModel extends ManagersAccessViewModel {
    final ReactorTextViewState includedSubtitle = new ReactorTextViewState();
    final ReactorViewState includedSubtitleDivider = new ReactorViewState();
    final ReactorViewState includedContainer = new ReactorViewState();
    final ReactorTextViewState optionalSubtitle = new ReactorTextViewState();
    final ReactorViewState optionalSubtitleDivider = new ReactorViewState();
    final ReactorViewState optionalContainer = new ReactorViewState();
    final ReactorVar<List<EHIExtraItem>> mIncludedExtras = new ReactorVar<>();
    final ReactorVar<List<EHIExtraItem>> mOptionalExtras = new ReactorVar<>();
    private EHIExtras mExtras;

    public void setExtras(final EHIExtras extras) {
        mExtras = extras;
        final List<EHIExtraItem> includedExtras = extras.getIncludedEquipment();
        final List<EHIExtraItem> optionalExtras = extras.getOptionalAndWaivedEquipment();

        if(ListUtils.isEmpty(includedExtras)){
            includedSubtitle.setText(R.string.key_facts_no_included_equipment);
            includedSubtitleDivider.setVisibility(View.GONE);
            includedContainer.setVisibility(View.GONE);
        }
        else {
            mIncludedExtras.setValue(includedExtras);
        }

        if(ListUtils.isEmpty(optionalExtras)){
            optionalSubtitle.setText(R.string.key_facts_no_equipment);
            optionalSubtitleDivider.setVisibility(View.GONE);
            optionalContainer.setVisibility(View.GONE);
        }
        else {
            mOptionalExtras.setValue(optionalExtras);
        }
    }

    public List<EHIExtraItem> getIncludedExtras() {
        return mIncludedExtras.getValue();
    }

    public List<EHIExtraItem> getOptionalExtras() {
        return mOptionalExtras.getValue();
    }
}
