package com.ehi.enterprise.android.ui.reservation.widget;

import com.ehi.enterprise.android.ui.viewmodel.ManagersAccessViewModel;

import java.text.NumberFormat;
import java.util.Locale;

import io.dwak.reactor.ReactorVar;
import io.dwak.reactor.unbinder.annotation.AutoUnbindAll;

@AutoUnbindAll
public class EPointsHeaderViewModel extends ManagersAccessViewModel{
    //region ReactorVars
    final ReactorVar<String> topRightText = new ReactorVar<>();
    final ReactorVar<String> topLeftPointsText = new ReactorVar<>();
    final ReactorVar<Boolean> topLeftPointsVisibility = new ReactorVar<>(true);
    final ReactorVar<String> topLeftHeaderText = new ReactorVar<>();
    final ReactorVar<Boolean> topLeftHeaderVisibility = new ReactorVar<>(true);
    final ReactorVar<Boolean> dividerVisibility = new ReactorVar<>(true);
    //endregion

    public void setTopRightText(String topRightText){
        this.topRightText.setValue(topRightText);
    }

    public void setTopLeftPointsText(long topLeftPointsText){
        this.topLeftPointsText.setValue(NumberFormat.getNumberInstance(Locale.getDefault()).format(topLeftPointsText));
        this.topLeftPointsVisibility.setValue(true);
    }

    public void setTopLeftHeaderText(String topLeftHeaderText){
        this.topLeftHeaderText.setValue(topLeftHeaderText);
        topLeftHeaderVisibility.setValue(true);
    }

    public void setDividerVisibility(boolean visibility){
        dividerVisibility.setValue(visibility);
    }
}
