package com.ehi.enterprise.android.ui.location;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.MenuItem;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.databinding.LocationDetailsActivityBinding;
import com.ehi.enterprise.android.models.location.EHICityLocation;
import com.ehi.enterprise.android.models.location.EHILocation;
import com.ehi.enterprise.android.models.location.solr.EHISolrLocation;
import com.ehi.enterprise.android.network.responses.location.solr.EHIPostalCodeLocation;
import com.ehi.enterprise.android.ui.fragment.DataBindingViewModelActivity;
import com.ehi.enterprise.android.ui.location.interfaces.IToolbarContainer;
import com.ehi.enterprise.android.ui.location.interfaces.OnSolrLocationInfoClickListener;
import com.ehi.enterprise.android.ui.reservation.ItineraryActivityHelper;
import com.ehi.enterprise.android.ui.reservation.modify.ModifyItineraryActivityHelper;
import com.ehi.enterprise.android.utils.FragmentUtils;
import com.ehi.enterprise.android.utils.IntentUtils;
import com.ehi.enterprise.android.utils.locations.EHITransferDatesManager;
import com.isobar.android.newinstancer.Extra;
import com.isobar.android.viewmodel.ViewModel;

import java.util.Date;

@ViewModel(LocationDetailsActivityViewModel.class)
public class LocationDetailsActivity
        extends DataBindingViewModelActivity<LocationDetailsActivityViewModel, LocationDetailsActivityBinding>
        implements OnSolrLocationInfoClickListener, IToolbarContainer {
    private static final String TAG = LocationDetailsActivity.class.getSimpleName();

    @Extra(value = EHISolrLocation.class, required = false)
    public static final String LOCATION = "ehi.EXTRA_LOCATION";
    @Extra(value = EHILocation.class, required = false)
    public static final String GBO_LOCATION = "ehi.EXTRA_GBO_LOCATION";
    @Extra(value = int.class, required = false)
    public static final String FLOW = "ehi.EXTRA_FLOW";
    @Extra(value = boolean.class, required = false)
    public static final String SHOW_START_RESERVATION = "ehi.EXTRA_SHOW_START_RESERVATION";
    @Extra(value = boolean.class, required = false)
    public static final String IS_MODIFY = "ehi.EXTRA_IS_MODIFY";
    @Extra(value = Date.class, required = false)
    public static final String EXTRA_PICKUP_DATE = "ehi.EXTRA_PICKUP_DATE";
    @Extra(value = Date.class, required = false)
    public static final String EXTRA_DROPOFF_DATE = "ehi.EXTRA_DROPOFF_DATE";
    @Extra(value = Date.class, required = false)
    public static final String EXTRA_PICKUP_TIME = "ehi.EXTRA_PICKUP_TIME";
    @Extra(value = Date.class, required = false)
    public static final String EXTRA_DROPOFF_TIME = "ehi.EXTRA_DROPOFF_TIME";


    private EHISolrLocation mSelectedLocation;
    private Date mPickupDate;
    private Date mDropoffDate;
    private Date mPickupTime;
    private Date mDropoffTime;

    private EHILocation mDetailsLocation;

    private Drawable mToolbarBackgroundDrawable;

    private
    @SearchLocationsActivity.Flow
    int mFlow;

    private boolean mShowStartReservation;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setDataBindingContentView(R.layout.ac_location_details);
        LocationDetailsActivityHelper.Extractor extractor = new LocationDetailsActivityHelper.Extractor(this);

        mSelectedLocation = extractor.location();
        mPickupDate = extractor.extraPickupDate();
        mDropoffDate = extractor.extraDropoffDate();
        mPickupTime = extractor.extraPickupTime();
        mDropoffTime = extractor.extraDropoffTime();

        if (extractor.flow() != null) {
            //noinspection ResourceType
            mFlow = extractor.flow();
        }
        //noinspection ResourceType
        if (extractor.showStartReservation() != null) {
            mShowStartReservation = extractor.showStartReservation();
        }
        mDetailsLocation = extractor.gboLocation();
        if (extractor.isModify() != null) {
            getViewModel().setIsModify(extractor.isModify());
        }

        initViews();
        commitFragments();
    }

    private void initViews() {
        getViewBinding().toolbarInclude.toolbar.setTitle("");
        getViewBinding().toolbarInclude.title.setText(R.string.location_details_title);
        setSupportActionBar(getViewBinding().toolbarInclude.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mToolbarBackgroundDrawable = getResources().getDrawable(R.drawable.toolbar_background_drawable);
        mToolbarBackgroundDrawable.setAlpha(0);
        getViewBinding().toolbarInclude.toolbar.setBackground(mToolbarBackgroundDrawable);
        getViewBinding().toolbarInclude.title.setAlpha(0);
    }

    private void commitFragments() {
        if (mDetailsLocation != null) {
            new FragmentUtils.Transaction(getSupportFragmentManager(), FragmentUtils.ADD)
                    .fragment(new LocationDetailsFragmentHelper.Builder()
                            .detailsLocation(mDetailsLocation)
                            .extraPickupDate(mPickupDate)
                            .extraDropoffDate(mDropoffDate)
                            .build())
                    .into(R.id.ac_single_fragment_container)
                    .commit();
        } else {
            new FragmentUtils.Transaction(getSupportFragmentManager(), FragmentUtils.ADD)
                    .fragment(
                            new LocationDetailsFragmentHelper.Builder()
                                    .location(mSelectedLocation)
                                    .showStartReservation(mShowStartReservation)
                                    .flow(mFlow)
                                    .extraPickupDate(mPickupDate)
                                    .extraDropoffDate(mDropoffDate)
                                    .build())
                    .into(R.id.ac_single_fragment_container)
                    .commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onShowLocationDetails(EHISolrLocation location) {
        //no need to implement this
    }

    @Override
    public void onSelectLocation(final EHISolrLocation location) {
        if (location.isNalmo()) {
            final String linkOutUrl;
            if (location.getBrand().contains(EHILocation.BRAND_ALAMO)) {
                linkOutUrl = getViewModel().getValidAlamoReservationUrl();
            } else {
                linkOutUrl = getViewModel().getValidNationalReservationUrl();
            }
            new AlertDialog.Builder(this)
                    .setMessage(getString(R.string.alert_open_browser_text))
                    .setPositiveButton(getString(R.string.standard_ok_text), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            IntentUtils.openUrlViaExternalApp(LocationDetailsActivity.this, linkOutUrl);
                        }
                    })
                    .setNegativeButton(getString(R.string.alert_cancel_title), null)
                    .setCancelable(true)
                    .create()
                    .show();
            return;
        }

        redirectToLDT(location);
    }

    private void redirectToLDT(EHISolrLocation location) {
        final Intent intent;
        final EHITransferDatesManager ehiTransferDatesManager = new EHITransferDatesManager(location,
                mPickupDate,
                mDropoffDate,
                mPickupTime,
                mDropoffTime,
                mFlow);

        if (getViewModel().isModify()) {
            final ModifyItineraryActivityHelper.Builder builder = new ModifyItineraryActivityHelper.Builder();
            if (ehiTransferDatesManager.shouldSendPickupDate()) {
                builder.extraPickupDate(mPickupDate);
                if (ehiTransferDatesManager.shouldSendPickupTime()) {
                    builder.extraPickupTime(mPickupTime);
                }
            }
            if (ehiTransferDatesManager.shouldSendDropoffDate()) {
                builder.extraReturnDate(mDropoffDate);
                if (ehiTransferDatesManager.shouldSendDropoffTime()) {
                    builder.extraReturnTime(mDropoffTime);
                }
            }

            if (mFlow != SearchLocationsActivity.FLOW_DROP_OFF_LOCATION_ONE_WAY) {
                builder.extraPickupLocation(location);
            } else {
                builder.extraReturnLocation(location);
            }
            intent = builder.extraFlow(mFlow).build(this);
        } else {
            final ItineraryActivityHelper.Builder builder = new ItineraryActivityHelper.Builder();
            if (ehiTransferDatesManager.shouldSendPickupDate()) {
                builder.extraPickupDate(mPickupDate);
                if (ehiTransferDatesManager.shouldSendPickupTime()) {
                    builder.extraPickupTime(mPickupTime);
                }
            }
            if (ehiTransferDatesManager.shouldSendDropoffDate()) {
                builder.extraDropoffDate(mDropoffDate);
                if (ehiTransferDatesManager.shouldSendDropoffTime()) {
                    builder.extraDropoffTime(mDropoffTime);
                }
            }

            if (mFlow != SearchLocationsActivity.FLOW_DROP_OFF_LOCATION_ONE_WAY) {
                builder.extraPickupLocation(location);
            } else {
                builder.extraReturnLocation(location);
            }
            intent = builder.extraFlow(mFlow).build(this);
        }

        getViewModel().addRecentLocation(location);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }


    @Override
    public void onShowCityLocation(EHICityLocation cityLocation) {
    }

    @Override
    public void onShowPostalLocation(EHIPostalCodeLocation postalCodeLocation) {
    }

    @Override
    public int getToolbarHeight() {
        return getViewBinding().toolbarInclude.toolbar.getHeight();
    }

    @Override
    public void setToolbarBackgroundAlpha(int alpha) {
        mToolbarBackgroundDrawable.setAlpha(alpha);
    }

    @Override
    public void setToolbarTitleAlpha(float alpha) {
        getViewBinding().toolbarInclude.title.setAlpha(alpha);
    }


}