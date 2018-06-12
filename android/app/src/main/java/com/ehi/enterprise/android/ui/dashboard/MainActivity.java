package com.ehi.enterprise.android.ui.dashboard;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.ehi.enterprise.android.BuildConfig;
import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.app.EHILocale;
import com.ehi.enterprise.android.app.Environment;
import com.ehi.enterprise.android.app.Settings;
import com.ehi.enterprise.android.app.SolrEnvironment;
import com.ehi.enterprise.android.databinding.MainActivityBinding;
import com.ehi.enterprise.android.models.geofence.EHIGeofence;
import com.ehi.enterprise.android.models.location.EHIPolicy;
import com.ehi.enterprise.android.models.location.EHIWayfindingStep;
import com.ehi.enterprise.android.models.notification.EHINotification;
import com.ehi.enterprise.android.ui.dashboard.debug.DebugMenuFragmentHelper;
import com.ehi.enterprise.android.ui.dashboard.interfaces.RootNavigationListener;
import com.ehi.enterprise.android.ui.enroll.EnrollActivity;
import com.ehi.enterprise.android.ui.fragment.DataBindingViewModelActivity;
import com.ehi.enterprise.android.ui.fragment.ModalTextDialogFragmentHelper;
import com.ehi.enterprise.android.ui.location.DirectionsFromTerminalActivityHelper;
import com.ehi.enterprise.android.ui.location.SearchLocationsActivity;
import com.ehi.enterprise.android.ui.location.SearchLocationsActivityHelper;
import com.ehi.enterprise.android.ui.login.LogoutFragment;
import com.ehi.enterprise.android.ui.login.LogoutFragmentHelper;
import com.ehi.enterprise.android.ui.login.VerifyLoginFragmentHelper;
import com.ehi.enterprise.android.ui.navigation.NavigationDrawerFragment;
import com.ehi.enterprise.android.ui.navigation.NavigationDrawerItem;
import com.ehi.enterprise.android.ui.profile.MyProfileFragment;
import com.ehi.enterprise.android.ui.profile.MyProfileFragmentHelper;
import com.ehi.enterprise.android.ui.profile.interfaces.ISignOutDelegate;
import com.ehi.enterprise.android.ui.reservation.history.MyRentalsFragmentHelper;
import com.ehi.enterprise.android.ui.reservation.history.MyRentalsUnauthFragment;
import com.ehi.enterprise.android.ui.reservation.promotions.WeekendSpecialDetailsFragmentHelper;
import com.ehi.enterprise.android.ui.rewards.JoinEnterpriseModalFragment;
import com.ehi.enterprise.android.ui.rewards.JoinEnterpriseModalFragmentHelper;
import com.ehi.enterprise.android.ui.rewards.RewardsFragmentHelper;
import com.ehi.enterprise.android.ui.rewards.RewardsLearnMoreFragment;
import com.ehi.enterprise.android.ui.rewards.UnauthRewardsAndBenefitsFragment;
import com.ehi.enterprise.android.ui.support.CustomerSupportFragment;
import com.ehi.enterprise.android.ui.support.CustomerSupportFragmentHelper;
import com.ehi.enterprise.android.utils.DLog;
import com.ehi.enterprise.android.utils.DialogUtils;
import com.ehi.enterprise.android.utils.FragmentUtils;
import com.ehi.enterprise.android.utils.IntentUtils;
import com.ehi.enterprise.android.utils.ListUtils;
import com.ehi.enterprise.android.utils.LocaleUtils;
import com.ehi.enterprise.android.utils.PermissionUtils;
import com.ehi.enterprise.android.utils.analytics.EHIAnalytics;
import com.ehi.enterprise.android.utils.analytics.EHIAnalyticsDictionaryUtils;
import com.ehi.enterprise.android.utils.analytics.EHIAnalyticsEvent;
import com.ehi.enterprise.android.utils.analytics.IRootMenuScreen;
import com.ehi.enterprise.android.utils.manager.EHINotificationManager;
import com.ehi.enterprise.android.utils.manager.LocalDataManager;
import com.ehi.enterprise.android.utils.manager.ReservationManager;
import com.ehi.enterprise.android.utils.payment.SystemPayUtils;
import com.ehi.enterprise.android.utils.permission.PermissionRequestHandler;
import com.ehi.enterprise.android.utils.permission.PermissionRequester;
import com.isobar.android.newinstancer.Extra;
import com.isobar.android.viewmodel.ViewModel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ViewModel(MainViewModel.class)
public class MainActivity extends DataBindingViewModelActivity<MainViewModel, MainActivityBinding>
        implements NavigationDrawerFragment.NavigationDrawerCallbacks,
        ISignOutDelegate,
        MyRentalsUnauthFragment.MyRentalsUnauthFragmentListener,
        PermissionRequestHandler,
        RootNavigationListener,
        DashboardFragment.RentalsUpdateListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    public static final String SCREEN_NAME = "MainActivity";


    @Extra(value = EHINotification.class, required = false)
    public static final String NOTIFICATION = "NOTIFICATION";
    @Extra(value = String.class, required = false)
    public static final String EXTRA_INTENT_TYPE = "ehi.EXTRA_INTENT_TYPE";
    @Extra(value = IntentUtils.NotificationIntents.class, required = false)
    public static final String INTENT_ACTION = "INTENT_ACTION";
    @Extra(value = EHIGeofence.class, required = false)
    public static final String GEOFENCE = "GEOFENCE";
    @Extra(value = Intent.class, required = false)
    public static final String SHOW_JOIN_MODAL = "SHOW_JOIN_MODAL";



    private static final int REWARDS_REDIRECT_CODE = 987;
    public static final int AUTHENTICATE_PROFILE_CODE = 323;
    private static final int SIGN_OUT_REQUEST_CODE = 322;
    private static final int SIGN_IN_CODE = 324;
    private static final int JOIN_ENTERPRISE = 325;


    private Map<Integer, PermissionRequester> mPermissionRequesterMap = new HashMap<>();
    private NavigationDrawerFragment mNavigationDrawerFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setDataBindingContentView(R.layout.ac_main);
        checkIntentFromNotification();
        initViews();

        if (BuildConfig.DEBUG) {
            buildEnvironmentNotification(getViewModel().getEnvironmentEndpoint(), getViewModel().getSolrEnvironmentEndpoint());
        }

        SystemPayUtils.checkIfAndroidPayIsEnabled(this);
        SystemPayUtils.checkIfSamsungPayIsEnabled(this);

        if (getIntent().hasExtra(SHOW_JOIN_MODAL)){
                showModalDialogForResult(new JoinEnterpriseModalFragmentHelper.Builder().build(), JOIN_ENTERPRISE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_dashboard, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_call) {
            EHIAnalyticsEvent.create()
                    .action(EHIAnalytics.Motion.MOTION_TAP.value, EHIAnalytics.Action.ACTION_PHONE.value)
                    .addDictionary(EHIAnalyticsDictionaryUtils.signIn())
                    .smartTrackAction(true)
                    .tagScreen()
                    .tagEvent();


            showModalDialog(new DialOutFragmentHelper.Builder()
                    .currentRental(getViewModel().hasRentals())
                    .build());

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        checkIntentFromNotification();
    }

    @SuppressWarnings("ConstantConditions")
    private void checkIntentFromNotification() {
        if (getIntent().getExtras() != null) {
            MainActivityHelper.Extractor extractor = new MainActivityHelper.Extractor(this);
            if (extractor.intentAction() != null) {
                switch (extractor.intentAction()) {
                    case CALL:
                        if (extractor.notification() != null) {
                            ReservationManager.getInstance().setSessionSource("Call");
                            EHIAnalyticsEvent.create()
                                    .screen(EHIAnalytics.Screen.SCREEN_DASHBOARD.value, DashboardFragment.SCREEN_NAME)
                                    .state(EHIAnalytics.State.STATE_UPCOMING_RENTALS.value)
                                    .addCustomDimensions(EHIAnalyticsDictionaryUtils.customDimensions())
                                    .addDictionary(EHIAnalyticsDictionaryUtils.reservation())
                                    .tagScreen()
                                    .tagEvent();

                            IntentUtils.callNumber(this, extractor.notification().getLocationPhone());
                        }
                        break;
                    case GAS_STATIONS:
                        if (extractor.notification() != null) {
                            ReservationManager.getInstance().setSessionSource("GasStations");
                            EHIAnalyticsEvent.create()
                                    .screen(EHIAnalytics.Screen.SCREEN_DASHBOARD.value, DashboardFragment.SCREEN_NAME)
                                    .state(EHIAnalytics.State.STATE_CURRENT_RENTALS.value)
                                    .addCustomDimensions(EHIAnalyticsDictionaryUtils.customDimensions())
                                    .addDictionary(EHIAnalyticsDictionaryUtils.reservation())
                                    .tagScreen()
                                    .tagEvent();

                            IntentUtils.findNearbyGasStations(this, extractor.notification().getLocationLatLng());
                        }
                        break;
                    case GET_DIRECTIONS:
                        if (extractor.notification() != null) {
                            ReservationManager.getInstance().setSessionSource("GetDirections");
                            String directionsState;
                            if (extractor.notification().isCurrentTrip()) {
                                directionsState = EHIAnalytics.State.STATE_CURRENT_RENTALS.value;
                            } else {
                                directionsState = EHIAnalytics.State.STATE_UPCOMING_RENTALS.value;
                            }
                            EHIAnalyticsEvent.create()
                                    .screen(EHIAnalytics.Screen.SCREEN_DASHBOARD.value, DashboardFragment.SCREEN_NAME)
                                    .state(directionsState)
                                    .addCustomDimensions(EHIAnalyticsDictionaryUtils.customDimensions())
                                    .addDictionary(EHIAnalyticsDictionaryUtils.reservation())
                                    .tagScreen()
                                    .tagEvent();

                            IntentUtils.getDirectionsFromCurrentLocation(this,
                                    extractor.notification().getLocationLatLng().getLatitude(),
                                    extractor.notification().getLocationLatLng().getLongitude(),
                                    extractor.notification().getLocationName());
                        }
                        break;
                    case RETURN_INSTRUCTIONS:
                        if (extractor.geofence() != null) {
                            if (extractor.geofence().isAfterHours()) {
                                ReservationManager.getInstance().setSessionSource("ReturnInstructions");
                                EHIAnalyticsEvent.create()
                                        .screen(EHIAnalytics.Screen.SCREEN_DASHBOARD.value, DashboardFragment.SCREEN_NAME)
                                        .state(EHIAnalytics.State.STATE_CURRENT_RENTALS.value)
                                        .addCustomDimensions(EHIAnalyticsDictionaryUtils.customDimensions())
                                        .addDictionary(EHIAnalyticsDictionaryUtils.reservation())
                                        .tagScreen()
                                        .tagEvent();

                                final EHIPolicy policy = extractor.geofence().getAfterHoursPolicy();
                                showModalDialog(new ModalTextDialogFragmentHelper.Builder().title(policy.getDescription())
                                        .text(policy.getPolicyText())
                                        .buttonText(getString(R.string.modal_default_dismiss_title))
                                        .build());
                            }
                        }
                        break;
                    case TERMINAL_DIRECTIONS:
                        if (extractor.geofence() != null) {
                            final List<EHIWayfindingStep> wayfindingSteps = extractor.geofence().getWayfindingSteps();
                            if (!ListUtils.isEmpty(wayfindingSteps)) {
                                ReservationManager.getInstance().setSessionSource("TerminalDirections");
                                EHIAnalyticsEvent.create()
                                        .screen(EHIAnalytics.Screen.SCREEN_DASHBOARD.value, DashboardFragment.SCREEN_NAME)
                                        .state(EHIAnalytics.State.STATE_CURRENT_RENTALS.value)
                                        .addCustomDimensions(EHIAnalyticsDictionaryUtils.customDimensions())
                                        .addDictionary(EHIAnalyticsDictionaryUtils.reservation())
                                        .tagScreen()
                                        .tagEvent();

                                startActivity(new DirectionsFromTerminalActivityHelper.Builder().wayfindingSteps(wayfindingSteps)
                                        .build(MainActivity.this));
                            }
                        }
                        break;
                    case OPEN_APP:
                        if (extractor.notification() != null) {
                            ReservationManager.getInstance().setSessionSource("OpenDashboard");
                            String appState;
                            if (extractor.notification().isCurrentTrip()) {
                                appState = EHIAnalytics.State.STATE_CURRENT_RENTALS.value;
                            } else {
                                appState = EHIAnalytics.State.STATE_UPCOMING_RENTALS.value;
                            }
                            EHIAnalyticsEvent.create()
                                    .screen(EHIAnalytics.Screen.SCREEN_DASHBOARD.value, DashboardFragment.SCREEN_NAME)
                                    .state(appState)
                                    .addCustomDimensions(EHIAnalyticsDictionaryUtils.customDimensions())
                                    .addDictionary(EHIAnalyticsDictionaryUtils.reservation())
                                    .tagScreen()
                                    .tagEvent();
                        }
                        break;
                }
            }
        }
    }

    private void initViews() {
        getViewBinding().toolbarInclude.toolbar.setTitle("");
        setSupportActionBar(getViewBinding().toolbarInclude.toolbar);
        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);


        mNavigationDrawerFragment.setUp(
                R.id.navigation_container,
                getViewBinding().drawerLayout);
    }

    public Toolbar getToolbar() {
        return getViewBinding().toolbarInclude.toolbar;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SIGN_OUT_REQUEST_CODE && resultCode == RESULT_OK
                && data.hasExtra(LogoutFragment.SIGNOUT_BOOLEAN_KEY)) {
            getViewModel().logOut();
        }
        if (requestCode == AUTHENTICATE_PROFILE_CODE) {
            if (resultCode == RESULT_OK) {
                new FragmentUtils.Transaction(getSupportFragmentManager(), FragmentUtils.REPLACE)
                        .fragment(new MyProfileFragmentHelper.Builder().build())
                        .into(R.id.ac_single_fragment_container)
                        .commit();
            } else {
                if (getSupportFragmentManager().findFragmentById(R.id.ac_single_fragment_container) instanceof MyProfileFragment) {
                    IntentUtils.goToHomeScreen(this);
                } else {
                    mNavigationDrawerFragment.revertSelection();
                }
            }
        }
        if (requestCode == REWARDS_REDIRECT_CODE) {
            if (resultCode == RESULT_OK) {
                new FragmentUtils.Transaction(getSupportFragmentManager(), FragmentUtils.REPLACE)
                        .fragment(new RewardsFragmentHelper.Builder().build())
                        .into(R.id.ac_single_fragment_container)
                        .commit();
            }
        }
        if (SIGN_IN_CODE == requestCode) {
            if (getSupportFragmentManager().findFragmentByTag(RewardsLearnMoreFragment.TAG) != null) {
                new FragmentUtils.Transaction(getSupportFragmentManager(), FragmentUtils.REPLACE)
                        .fragment(new RewardsFragmentHelper.Builder().build())
                        .into(R.id.ac_single_fragment_container)
                        .commit();
            }
        }

        if (requestCode == JOIN_ENTERPRISE){
            if (resultCode == Activity.RESULT_CANCELED) {
                LocalDataManager.getInstance().setShownRegisterModal();
            } else if (resultCode == JoinEnterpriseModalFragment.RESULT_ENROLL){
                Intent intent = new Intent(this, EnrollActivity.class);
                startActivity(intent);
            } else {
                startActivity((Intent) getIntent().getParcelableExtra(SHOW_JOIN_MODAL));
            }

        }
    }

    @Override
    public void onNavigationDrawerItemSelected(final NavigationDrawerItem drawerItem) {
        if (drawerItem.getFragment() == null) {
            if (drawerItem.getId() == NavigationDrawerItem.ID_SIGN_OUT) {
                onSignOut();
            } else if (drawerItem.getId() == NavigationDrawerItem.TYPE_TOGGLE) {
                toggleServicesEndpoint();
            } else if (drawerItem.getId() == NavigationDrawerItem.TYPE_TOGGLE_SOLR) {
                toggleSolrServiceEndpoint();
            } else if (drawerItem.getId() == NavigationDrawerItem.TYPE_TOGGLE_LANGUAGE) {
                toggleLanguage();
            } else if (drawerItem.getId() == NavigationDrawerItem.ID_MY_PROFILE) {
                if (!(getSupportFragmentManager().findFragmentById(R.id.ac_single_fragment_container) instanceof MyProfileFragment)) {
                    if (getViewModel().requiresReLogin()) {
                        showModalForResult(new VerifyLoginFragmentHelper.Builder().build(), AUTHENTICATE_PROFILE_CODE);
                    } else {
                        new FragmentUtils.Transaction(getSupportFragmentManager(), FragmentUtils.REPLACE)
                                .fragment(new MyProfileFragmentHelper.Builder().build())
                                .into(R.id.ac_single_fragment_container)
                                .commit();
                    }
                }
            } else if (drawerItem.getId() == NavigationDrawerItem.ID_EC_SIGN_OUT) {
                doEmeraldClubSignOut();
            } else if (drawerItem.getId() == NavigationDrawerItem.ID_CUSTOMER_SUPPORT) {
                new FragmentUtils.Transaction(getSupportFragmentManager(), FragmentUtils.REPLACE)
                        .fragment(new CustomerSupportFragmentHelper.Builder().build(), CustomerSupportFragment.TAG)
                        .into(R.id.ac_single_fragment_container)
                        .commit();
            } else if (drawerItem.getId() == NavigationDrawerItem.ID_SHARE_FEEDBACK) {
                IntentUtils.openUrlViaCustomTab(this, getViewModel().getUserFeedbackUrl());
            } else if (drawerItem.getId() == NavigationDrawerItem.TYPE_DEBUG_MENU) {
                showModalDialog(new DebugMenuFragmentHelper.Builder().build());
            }
        } else {
            if (drawerItem.getType() == NavigationDrawerItem.TYPE_LOCATION_ITEM
                    || drawerItem.getType() == NavigationDrawerItem.TYPE_BUTTON_ITEM) {
                startActivity(new SearchLocationsActivityHelper.Builder()
                        .extraFlow(SearchLocationsActivity.FLOW_PICKUP_LOCATION_ROUND_TRIP)
                        .extraShowStartReservation(true)
                        .isModify(false)
                        .build(this));
            } else if (drawerItem.getType() == NavigationDrawerItem.TYPE_SECONDARY_ITEM) {
                showModal(drawerItem.getFragment());
            } else if (drawerItem.getType() == NavigationDrawerItem.TYPE_SIGN_IN) {
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            showModalForResult(drawerItem.getFragment(), SIGN_IN_CODE);
                        } catch (Exception e) {
                        }
                    }
                }, 300);
            } else if (drawerItem.getType() == NavigationDrawerItem.TYPE_WEEKEND_SPECIAL_VIEW) {
                if (mNavigationDrawerFragment.isDrawerOpen()) {
                    mNavigationDrawerFragment.closeDrawer();
                }
                showModal(new WeekendSpecialDetailsFragmentHelper.Builder().build());
            } else {
                if (drawerItem.getId() == NavigationDrawerItem.ID_MY_REWARDS &&
                        !getViewModel().isUserLoggedIn()) {
                    new FragmentUtils.Transaction(getSupportFragmentManager(), FragmentUtils.REPLACE)
                            .fragment(new UnauthRewardsAndBenefitsFragment(), UnauthRewardsAndBenefitsFragment.TAG)
                            .into(R.id.ac_single_fragment_container)
                            .commit();

                } else {
                    new FragmentUtils.Transaction(getSupportFragmentManager(), FragmentUtils.REPLACE)
                            .fragment(drawerItem.getFragment())
                            .into(R.id.ac_single_fragment_container)
                            .commit();
                }
            }
        }
    }

    private void doEmeraldClubSignOut() {
        DialogUtils.showOkCancelDialog(this, getString((R.string.menu_emerald_club_sign_out)), getString(R.string.signout_emerald_club_confirmation_text), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getViewModel().emeraldClubLogOut();
            }
        });
    }

    @Override
    public void selectDrawerItem(int itemId) {
        if (mNavigationDrawerFragment != null) {
            mNavigationDrawerFragment.selectDrawerItem(itemId);
        }
    }

    @Override
    public void onBackPressed() {
        Fragment f = getSupportFragmentManager().findFragmentById(R.id.ac_single_fragment_container);

        if (mNavigationDrawerFragment.isDrawerOpen()) {
            mNavigationDrawerFragment.closeDrawer();
        } else if (f != null && f instanceof DashboardFragment && ((DashboardFragment) f).isAnimating()) {
        } else {
            super.onBackPressed();
        }
    }

    // please - forgive me
    public void toggleServicesEndpoint() {
        new AlertDialog.Builder(this)
                .setTitle("Select Environment")
                .setItems(Environment.values(), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Environment env = Environment.values()[i];
                        if (env.requiresPassword()) {
                            verifyPasswordAndSetServicesEndpoint(env);
                        } else {
                            setServicesEndpoint(env);
                        }
                    }
                })
                .show();
    }

    public void toggleSolrServiceEndpoint() {
        new AlertDialog.Builder(this)
                .setTitle("Select Solr Environment")
                .setItems(SolrEnvironment.values(), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        final SolrEnvironment env = SolrEnvironment.values()[i];
                        setSolrEndpoint(env);
                    }
                })
                .show();
    }

    private void verifyPasswordAndSetServicesEndpoint(final Environment env) {
        final EditText password = new EditText(this);
        password.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);

        new AlertDialog.Builder(this)
                .setTitle("Please confirm password")
                .setView(password)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (env.validatePassword(password.getText().toString())) {
                            setServicesEndpoint(env);
                        } else {
                            Toast.makeText(MainActivity.this, "Invalid Password", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .show();
    }

    private void setServicesEndpoint(Environment env) {
        if (BuildConfig.FLAVOR.equalsIgnoreCase("dev") || BuildConfig.FLAVOR.equalsIgnoreCase("uat")) {
            String currentEndpoint = Settings.setServicesEndpoint(env);
            getViewModel().setEnvironment(env);

            Toast.makeText(this, currentEndpoint, Toast.LENGTH_SHORT).show();

            IntentUtils.goToSplashScreen(this);
        }
    }

    private void setSolrEndpoint(SolrEnvironment env) {
        if (BuildConfig.FLAVOR.equalsIgnoreCase("dev") || BuildConfig.FLAVOR.equalsIgnoreCase("uat")) {
            final String currentEndpoint = getViewModel().getEnvironmentEndpoint();
            final String currentSolrEndpoint = Settings.setSolrEndpoint(env);

            getViewModel().setSolrEnvironment(env);

            Toast.makeText(this, currentSolrEndpoint, Toast.LENGTH_SHORT).show();

            buildEnvironmentNotification(currentEndpoint, currentSolrEndpoint);
        }
    }

    private void buildEnvironmentNotification(String currentEndpoint, String currentSolrEndpoint) {
        final String enviromentDescription = new StringBuilder()
                .append("Endpoint: ")
                .append(currentEndpoint)
                .append("\n")
                .append("SOLR endpoint: ")
                .append(currentSolrEndpoint)
                .toString();

        NotificationManagerCompat
                .from(MainActivity.this)
                .notify(0, new NotificationCompat.Builder(MainActivity.this, EHINotificationManager.DEBUG_CHANNEL)
                .setSmallIcon(R.drawable.icon_notifications)
                .setContentTitle("Current Environment")
                .setContentText(enviromentDescription)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(enviromentDescription))
                .setColor(getResources().getColor(R.color.ehi_primary))
                .build());
    }

    private void toggleLanguage() {
        new AlertDialog.Builder(this)
                .setTitle("Select language and country")
                .setItems(EHILocale.values(), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        EHILocale ehiLocale = EHILocale.values()[i];
                        getViewModel().setLocale(ehiLocale);
                        LocaleUtils.updateAppLocale(MainActivity.this, ehiLocale.getLocale());
                        IntentUtils.goToHomeScreen(MainActivity.this);
                    }
                })
                .show();
    }

    @Override
    public void setTitle(CharSequence title) {
        if (getViewBinding().toolbarInclude.title != null) {
            getViewBinding().toolbarInclude.title.setText(title);
        }
    }

    @Override
    public void setTitle(int titleId) {
        if (getViewBinding().toolbarInclude.title != null) {
            getViewBinding().toolbarInclude.title.setText(titleId);
        }
    }

    public void setToolbarTitleVisible(boolean isVisible) {
        getViewBinding().toolbarInclude.title.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }

    public void updateMenuVisibility() {
        MenuItem callButton = getToolbar().getMenu().findItem(R.id.action_call);
        MenuItem signOutButton = getToolbar().getMenu().findItem(R.id.action_sign_out);
        if (callButton != null) {
            if (!callButton.isVisible()) {
                callButton.setVisible(true);
            }
        }
        if (signOutButton != null) {
            if (signOutButton.isVisible()) {
                signOutButton.setVisible(false);
            }
        }
    }

    @Override
    public void onSignOut() {
        showModalDialogForResult(new LogoutFragmentHelper.Builder().build(), SIGN_OUT_REQUEST_CODE);
    }

    @Override
    public void showAuthMyRentals() {
        new FragmentUtils.Transaction(getSupportFragmentManager(), FragmentUtils.REPLACE)
                .fragment(new MyRentalsFragmentHelper.Builder().build())
                .into(R.id.ac_single_fragment_container)
                .commit();
    }

    @Override
    public void requestPermissions(final int permissionRequestCode,
                                   final PermissionRequester requester,
                                   final String... permissions) {
        final List<String> checkedPermissions = PermissionUtils.checkPermissions(this, permissions);
        if (checkedPermissions.isEmpty()) {
            requester.onRequestPermissionResult(permissionRequestCode, permissions, new int[]{PackageManager.PERMISSION_GRANTED});
        } else {
            PermissionUtils.requestCheckedPermissions(this,
                    checkedPermissions,
                    permissionRequestCode);
            mPermissionRequesterMap.put(permissionRequestCode, requester);
        }
    }

    @Override
    public void onRequestPermissionsResult(final int requestCode,
                                           @NonNull final String[] permissions,
                                           @NonNull final int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mPermissionRequesterMap.get(requestCode)
                .onRequestPermissionResult(requestCode,
                        permissions,
                        grantResults);
    }

    @Override
    public void SearchAnimationEnded() {
        startActivity(new SearchLocationsActivityHelper.Builder()
                .extraFlow(SearchLocationsActivity.FLOW_PICKUP_LOCATION_ROUND_TRIP)
                .extraShowStartReservation(false)
                .isModify(false)
                .build(this));
        overridePendingTransition(R.anim.immediate_fade_in, R.anim.immediate_fade_out);
    }

    public void trackCurrentScreenAnalytics() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.ac_single_fragment_container);
        try {
            (((IRootMenuScreen) fragment)).trackScreenChange();
        } catch (Exception e) {
            //no fragment found or root fragment is not implements IRootMenuScreen
            DLog.e(TAG, "", e);
        }
    }

    @Override
    public void updateRentals(boolean hasRentals) {
        getViewModel().setHasRentals(hasRentals);
    }
}