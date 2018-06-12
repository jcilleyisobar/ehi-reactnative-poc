package com.ehi.enterprise.android.ui.location.widgets;

import android.content.Context;
import android.support.v4.content.res.ResourcesCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.AttributeSet;
import android.view.View;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.app.EHIStringToken;
import com.ehi.enterprise.android.databinding.LocationDetailsViewBinding;
import com.ehi.enterprise.android.models.location.EHILocation;
import com.ehi.enterprise.android.ui.location.LocationDetailsViewModel;
import com.ehi.enterprise.android.ui.location.interfaces.OnLocationDetailEventsListener;
import com.ehi.enterprise.android.ui.view.DataBindingViewModelView;
import com.isobar.android.tokenizedstring.TokenizedString;
import com.isobar.android.viewmodel.ViewModel;

@ViewModel(LocationDetailsViewModel.class)
public class LocationDetailsView extends DataBindingViewModelView<LocationDetailsViewModel, LocationDetailsViewBinding> {

    public static final String TAG = LocationDetailsView.class.getSimpleName();

    private OnLocationDetailEventsListener mListener;
    private ClickableSpan mClickableSpan = new ClickableSpan() {
        @Override
        public void onClick(View widget) {
            mListener.onShowAfterHoursDialog();
        }
    };

    private OnClickListener mOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v == getViewBinding().favoriteButton) {
                if (mListener != null) {
                    mListener.onFavoriteStateChanged();
                }
            } else if (v == getViewBinding().locationPhone) {
                if (mListener != null) {
                    mListener.onCallLocation(null);
                }
            } else if (v == getViewBinding().getDirectionsButton) {
                if (mListener != null) {
                    mListener.onShowDirection();
                }
            } else if (v == getViewBinding().directionsFromTerminalButton) {
                if (mListener != null) {
                    mListener.onShowDirectionFromTerminal();
                }
            }
        }
    };

    public LocationDetailsView(Context context) {
        this(context, null, 0);
    }

    public LocationDetailsView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LocationDetailsView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (!isInEditMode()) {
            createViewBinding(R.layout.v_location_details);
            initViews();
        }
    }

    private void initViews() {
        getViewBinding().locationPhone.setOnClickListener(mOnClickListener);
        getViewBinding().favoriteButton.setOnClickListener(mOnClickListener);
        getViewBinding().getDirectionsButton.setOnClickListener(mOnClickListener);
        getViewBinding().directionsFromTerminalButton.setOnClickListener(mOnClickListener);
    }

    public void setOnLocationDetailEventsListener(OnLocationDetailEventsListener listener) {
        mListener = listener;
    }

    public void setLocation(EHILocation location, boolean shouldShowAfterHoursDropoffView) {
        getViewBinding().locationName.setText(getNameWithAirportCode(location.getName(), location.getAirportCode()));
        if (location.getAddress() != null) {
            getViewBinding().locationAddress.setText(location.getAddress().getReadableAddress());
            getViewBinding().locationPhone.setText(location.getFormattedPhoneNumber(true));
        }

        if (!location.isNalmo()) {
            getViewBinding().favoriteButton.setSelected(location.isFavorite());
            if (location.isFavorite()) {
                getViewBinding().locationFavoriteText.setText(R.string.location_details_favorited_title);
            } else {
                getViewBinding().locationFavoriteText.setText(R.string.location_details_not_favorited_title);
            }
        } else {
            getViewBinding().favoriteButton.setVisibility(View.GONE);
        }

        if (shouldShowAfterHoursDropoffView) {
            SpannableString aboutText = new SpannableString(getResources().getString(R.string.locations_map_after_hours_about_button));
            aboutText.setSpan(
                    ResourcesCompat.getFont(getContext(), R.font.source_sans_light_italic),
                    0,
                    aboutText.length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            );
            aboutText.setSpan(mClickableSpan, 0, aboutText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            aboutText.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.ehi_primary)), 0, aboutText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            final CharSequence formattedTitle = new TokenizedString.Formatter<EHIStringToken>(getResources())
                    .formatString(R.string.locations_map_after_hours_return_label)
                    .addTokenAndValue(EHIStringToken.ABOUT, aboutText)
                    .format();

            getViewBinding().afterHoursReturn.setText(formattedTitle);
            getViewBinding().afterHoursReturn.setMovementMethod(LinkMovementMethod.getInstance());
            getViewBinding().afterHoursReturn.setVisibility(View.VISIBLE);
        } else {
            getViewBinding().afterHoursReturn.setVisibility(View.GONE);
        }

        if (location.getWayfindings() != null
                && location.getWayfindings().size() > 0
                && !location.isNalmo()) {
            getViewBinding().directionsFromTerminalButton.setVisibility(View.VISIBLE);
        } else {
            getViewBinding().directionsFromTerminalButton.setVisibility(View.GONE);
        }

        if (location.haveGpsCoordinates()) {
            getViewBinding().getDirectionsButton.setVisibility(View.VISIBLE);
        } else {
            getViewBinding().getDirectionsButton.setVisibility(View.GONE);
        }

        if (getViewBinding().getDirectionsButton.getVisibility() == View.VISIBLE
                || getViewBinding().directionsFromTerminalButton.getVisibility() == View.VISIBLE) {
            getViewBinding().bottomPaddingPlate.setVisibility(View.VISIBLE);
        } else {
            getViewBinding().bottomPaddingPlate.setVisibility(View.GONE);
        }

        getViewBinding().locationImage.setVisibility(location.isExotic() ? View.VISIBLE : View.GONE);
    }

    public Spannable getNameWithAirportCode(String locationName, String code) {
        SpannableStringBuilder bld = new SpannableStringBuilder();
        if (locationName != null) {
            bld.append(locationName);
        }
        if (code != null
                && !code.isEmpty()) {
            bld.append(" ");
            SpannableString textToShow = new SpannableString(code);
            textToShow.setSpan(new RelativeSizeSpan(0.75f), 0, code.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            textToShow.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.ehi_gray)), 0, code.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            bld.append(textToShow);
        }
        return bld;
    }

    public void setFavoriteButtonVisible(boolean visible) {
        if (visible) {
            getViewBinding().favoriteButton.setVisibility(View.VISIBLE);
        } else {
            getViewBinding().favoriteButton.setVisibility(View.GONE);
        }
    }
}