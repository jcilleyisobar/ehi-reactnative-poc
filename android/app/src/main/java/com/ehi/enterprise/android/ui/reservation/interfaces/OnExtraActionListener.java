package com.ehi.enterprise.android.ui.reservation.interfaces;

import com.ehi.enterprise.android.models.reservation.EHIExtraItem;

public interface OnExtraActionListener {

	void onChangeExtraCount(EHIExtraItem item, int newCount);

	void onClick(EHIExtraItem item);

}
