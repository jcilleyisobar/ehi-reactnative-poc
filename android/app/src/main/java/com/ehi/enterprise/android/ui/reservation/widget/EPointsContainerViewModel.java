package com.ehi.enterprise.android.ui.reservation.widget;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.app.EHIStringToken;
import com.ehi.enterprise.android.models.reservation.EHICarClassDetails;
import com.ehi.enterprise.android.ui.viewmodel.ManagersAccessViewModel;
import com.ehi.enterprise.android.utils.reactor_extensions.viewstate.ReactorTextViewState;
import com.ehi.enterprise.android.utils.reactor_extensions.viewstate.ReactorViewState;
import com.isobar.android.tokenizedstring.TokenizedString;

import java.text.NumberFormat;

import io.dwak.reactor.unbinder.annotation.AutoUnbindAll;

@AutoUnbindAll
public class EPointsContainerViewModel extends ManagersAccessViewModel {
    //region ReactorVars
    final ReactorViewState root = new ReactorViewState();
    final ReactorTextViewState pointsPerDay = new ReactorTextViewState();
    final ReactorTextViewState title = new ReactorTextViewState();
    final ReactorTextViewState subtitle = new ReactorTextViewState();
    //endregion

    private boolean mExpanded = false;
    private boolean mKeepHidden = false;
    private boolean mPreventAnimations = false;

    public boolean isExpanded() {
        return mExpanded;
    }

    public void setExpanded(final boolean expanded) {
        mExpanded = expanded;
        root.setVisibility(mExpanded ? ReactorViewState.VISIBLE : ReactorViewState.GONE);
    }

    public boolean isKeepHidden() {
        return mKeepHidden;
    }

    public void setKeepHidden(final boolean keepHidden) {
        mKeepHidden = keepHidden;
        if (keepHidden) {
            root.setVisibility(ReactorViewState.GONE);
        }
    }

    public void setRootVisibility(int visibility) {
        if (!mKeepHidden) {
            root.setVisibility(visibility);
        } else {
            root.setVisibility(ReactorViewState.GONE);
        }
    }

    public void setCarClassDetails(EHICarClassDetails details) {
        parseCarClassDetails(details);
    }

    public void parseCarClassDetails(final EHICarClassDetails details) {
        pointsPerDay.setText(NumberFormat.getNumberInstance().format(details.getRedemptionPoints()));

        if (details.getMaxRedemptionDays() != 0) {
            TokenizedString.Formatter<EHIStringToken> freeDaysString = new TokenizedString.Formatter<>(getResources());
            freeDaysString.formatString(details.getMaxRedemptionDays() > 1
                    ? R.string.redemption_free_days_subtitle
                    : R.string.redemption_free_day_subtitle);
            freeDaysString.addTokenAndValue(EHIStringToken.NUMBER_OF_DAYS, Integer.toString(details.getMaxRedemptionDays()));

            title.setText(getResources().getString(R.string.redemption_free_days_title));
            title.setVisibility(ReactorViewState.VISIBLE);

            subtitle.setText(freeDaysString.format());
            subtitle.setVisibility(ReactorViewState.VISIBLE);
        } else {
            title.setVisibility(ReactorViewState.VISIBLE);
            title.setText(getResources().getString(R.string.redemption_not_enough_points_title));
            subtitle.setVisibility(ReactorViewState.GONE);
        }
    }

    public boolean shouldPreventAnimations() {
        return mPreventAnimations;
    }

    public void setPreventAnimations(final boolean preventAnimations) {
        mPreventAnimations = preventAnimations;
    }
}
