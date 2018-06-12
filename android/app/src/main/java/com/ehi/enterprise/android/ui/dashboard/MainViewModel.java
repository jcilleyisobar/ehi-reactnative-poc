package com.ehi.enterprise.android.ui.dashboard;

import com.ehi.enterprise.android.app.EHILocale;
import com.ehi.enterprise.android.app.Environment;
import com.ehi.enterprise.android.app.SolrEnvironment;
import com.ehi.enterprise.android.ui.viewmodel.ManagersAccessViewModel;
import com.ehi.enterprise.android.utils.manager.LocalDataManager;

public class MainViewModel extends ManagersAccessViewModel {

    private boolean mHasRentals = false;

    public void logOut() {
        getManagers().getLoginManager().logOut();
        getManagers().getReservationManager().deleteDriverInfo();
        mHasRentals = false;
    }

    public void emeraldClubLogOut() {
        getManagers().getReservationManager().removeEmeraldClubAccount();
    }

    public boolean requiresReLogin() {
        return getManagers().getLoginManager().isNeedToRelogin();
    }

    public String getEnvironmentEndpoint() {
        final String envName = getManagers().getLocalDataManager().getSelectedEnvironmentName();
        return Environment.fromString(envName).getGboRentalEndpoint();
    }

    public void setEnvironment(Environment env) {
        getManagers().getLocalDataManager().setSelectedEnvironmentName(env.name());
        clearDataForRestart();
        logOut();
    }

    public String getSolrEnvironmentEndpoint() {
        final String solrEnvName = getManagers().getLocalDataManager().getSelectedSolrEnvironmentName();
        return SolrEnvironment.fromString(solrEnvName).getSolrEndpoint();
    }

    public void setSolrEnvironment(SolrEnvironment solrEnv) {
        getManagers().getLocalDataManager().setSelectedSolrEnvironmentName(solrEnv.name());
    }

    public void setLocale(EHILocale locale) {
        getManagers().getLocalDataManager().setSelectedLocaleName(locale.name());
    }

    public void clearDataForRestart() {
        final LocalDataManager localDataManager = getManagers().getLocalDataManager();
        final String selectedEnv = localDataManager.getSelectedEnvironmentName();
        final String selectedSolrEnv = localDataManager.getSelectedSolrEnvironmentName();
        final String selectedLocale = localDataManager.getSelectedLocaleName();

        localDataManager.clear();

        localDataManager.setSelectedEnvironmentName(selectedEnv);
        localDataManager.setSelectedLocaleName(selectedLocale);
        localDataManager.setSelectedSolrEnvironmentName(selectedSolrEnv);
    }

    public boolean hasRentals() {
        return mHasRentals;
    }

    public void setHasRentals(boolean hasRentals) {
        mHasRentals = hasRentals;
    }
}
