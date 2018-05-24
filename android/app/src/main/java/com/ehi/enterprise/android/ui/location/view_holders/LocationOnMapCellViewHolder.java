package com.ehi.enterprise.android.ui.location.view_holders;

import android.content.Context;
import android.content.res.Resources;
import android.support.v4.content.res.ResourcesCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewGroup;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.app.EHIStringToken;
import com.ehi.enterprise.android.databinding.LocationMapDetailsViewBinding;
import com.ehi.enterprise.android.models.location.solr.EHISolrLocation;
import com.ehi.enterprise.android.ui.viewholder.DataBindingViewHolder;
import com.ehi.enterprise.android.utils.CustomTypefaceSpan;
import com.isobar.android.tokenizedstring.TokenizedString;

import java.util.Date;

public class LocationOnMapCellViewHolder extends DataBindingViewHolder<LocationMapDetailsViewBinding> {

    protected LocationOnMapCellViewHolder(LocationMapDetailsViewBinding viewBinding) {
        super(viewBinding);
    }

    public static LocationOnMapCellViewHolder create(Context context, ViewGroup parent) {
        return new LocationOnMapCellViewHolder((LocationMapDetailsViewBinding) createViewBinding(context,
                R.layout.v_location_map_bubble_details,
                parent));
    }

    public static void bind(LocationOnMapCellViewHolder holder,
                            final EHISolrLocation location,
                            Date pickupDate,
                            Date dropoffDate,
                            Resources resources,
                            final LocationMapListListener listener,
                            String searchArea,
                            int flow) {
        boolean isTimeConflict = location.isInvalidForDropoff() || location.isInvalidForPickup();
        boolean shouldShowAfterHoursDropoff = location.isDropoffAfterHours();

        holder.getViewBinding().timeConflictView.setData(pickupDate, dropoffDate, location, searchArea, flow, true);

        holder.getViewBinding().headerTitle.setText(location.getLocationDetailsTitle());
        if (location.getReadableAddress() != null) {
            holder.getViewBinding().subheaderTitle.setText(location.getReadableAddress());
        }

        holder.getViewBinding().leftContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onMoreInfoMapDetailsClick(location);
                }
            }
        });
        holder.getViewBinding().rightContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onLocationSelectedClick(location);
                }
            }
        });

        if (isTimeConflict) {
            holder.getViewBinding().rightContainer.setBackground(ResourcesCompat.getDrawable(resources, R.drawable.green_border_white_background_button_overlay, null));
            holder.getViewBinding().arrowView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.arrow_green, null));
            holder.getViewBinding().flexibleTravelText.setVisibility(View.VISIBLE);
        } else {
            holder.getViewBinding().rightContainer.setBackground(ResourcesCompat.getDrawable(resources, R.drawable.green_button_touch_overlay, null));
            holder.getViewBinding().arrowView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.arrow_white, null));
            holder.getViewBinding().flexibleTravelText.setVisibility(View.GONE);
        }

        if (shouldShowAfterHoursDropoff) {
            holder.getViewBinding().afterHoursReturn.setVisibility(View.VISIBLE);
        } else {
            holder.getViewBinding().afterHoursReturn.setVisibility(View.GONE);
        }

        if (shouldShowAfterHoursDropoff) {
            SpannableString aboutText = new SpannableString(resources.getString(R.string.locations_map_after_hours_about_button));
            aboutText.setSpan(
                    new CustomTypefaceSpan("", ResourcesCompat.getFont(holder.itemView.getContext(), R.font.source_sans_light_italic)),
                    0,
                    aboutText.length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            );
            aboutText.setSpan(new ClickableSpan() {
                @Override
                public void onClick(View widget) {
                    listener.onAfterHoursClick(location);
                }
            }, 0, aboutText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            aboutText.setSpan(new ForegroundColorSpan(resources.getColor(R.color.ehi_primary)), 0, aboutText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            final CharSequence formattedTitle = new TokenizedString.Formatter<EHIStringToken>(resources)
                    .formatString(R.string.locations_map_after_hours_return_label)
                    .addTokenAndValue(EHIStringToken.ABOUT, aboutText)
                    .format();

            holder.getViewBinding().afterHoursReturn.setText(formattedTitle);
            holder.getViewBinding().afterHoursReturn.setMovementMethod(LinkMovementMethod.getInstance());
            holder.getViewBinding().afterHoursReturn.setVisibility(View.VISIBLE);
        } else {
            holder.getViewBinding().afterHoursReturn.setVisibility(View.GONE);
        }
    }

    public interface LocationMapListListener {
        void onMoreInfoMapDetailsClick(EHISolrLocation location);
        void onLocationSelectedClick(EHISolrLocation location);
        void onAfterHoursClick(EHISolrLocation location);
    }
}
