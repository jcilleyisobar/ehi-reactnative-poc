package com.ehi.enterprise.android.app;

import java.security.cert.Certificate;
import java.util.List;

public enum Environment implements CharSequence {

    //ex xqa1
    SVCSQA("www-gbo-location-svcsqa.gbo.csdev.ehiaws-nonprod.com/", "gbo-location/",
            "www-gbo-profile-svcsqa.gbo.csdev.ehiaws-nonprod.com/", "gbo-profile/",
            "www-gbo-rental-svcsqa.gbo.csdev.ehiaws-nonprod.com/", "gbo-rental/",
            "eLTm8lS6ISvPhqgjOVU1+50Dnxc91zsLiNm7cc7wZHY=",
            null,
            Certificates.getGboCertificates(),
            Certificates.getFareOfficeCertificates()),

    //ex xqa2
    RCQA("www-gbo-location-rcqa.gbo.csdev.ehiaws-nonprod.com/", "gbo-location/",
            "www-gbo-profile-rcqa.gbo.csdev.ehiaws-nonprod.com/", "gbo-profile/",
            "www-gbo-rental-rcqa.gbo.csdev.ehiaws-nonprod.com/", "gbo-rental/",
            "eLTm8lS6ISvPhqgjOVU1+50Dnxc91zsLiNm7cc7wZHY=",
            null,
            Certificates.getGboCertificates(),
            Certificates.getFareOfficeCertificates()),

    //ex xqa3
    PRDSUPQA("www-gbo-location-prdsupqa.gbo.csdev.ehiaws-nonprod.com/", "gbo-location/",
            "www-gbo-profile-prdsupqa.gbo.csdev.ehiaws-nonprod.com/", "gbo-profile/",
            "www-gbo-rental-prdsupqa.gbo.csdev.ehiaws-nonprod.com/", "gbo-rental/",
            "eLTm8lS6ISvPhqgjOVU1+50Dnxc91zsLiNm7cc7wZHY=",
            null,
            Certificates.getGboCertificates(),
            Certificates.getFareOfficeCertificates()),

    //ex int1
    DEV("www-gbo-location-dev.gbo.csdev.ehiaws-nonprod.com/", "gbo-location/",
            "www-gbo-profile-dev.gbo.csdev.ehiaws-nonprod.com/", "gbo-profile/",
            "www-gbo-rental-dev.gbo.csdev.ehiaws-nonprod.com/", "gbo-rental/",
            "eLTm8lS6ISvPhqgjOVU1+50Dnxc91zsLiNm7cc7wZHY=",
            null,
            Certificates.getGboCertificates(),
            Certificates.getFareOfficeCertificates()),

    DEVQA("www-gbo-location-devqa.gbo.csdev.ehiaws-nonprod.com/", "gbo-location/",
            "www-gbo-profile-devqa.gbo.csdev.ehiaws-nonprod.com/", "gbo-profile/",
            "www-gbo-rental-devqa.gbo.csdev.ehiaws-nonprod.com/", "gbo-rental/",
            "eLTm8lS6ISvPhqgjOVU1+50Dnxc91zsLiNm7cc7wZHY=",
            null,
            Certificates.getGboCertificates(),
            Certificates.getFareOfficeCertificates()),

    //ex int2
    RCDEV("www-gbo-location-rcdev.csdev.ehiaws.com/", "gbo-location/",
            "www-gbo-profile-rcdev.csdev.ehiaws.com/", "gbo-profile/",
            "www-gbo-rental-rcdev.csdev.ehiaws.com/", "gbo-rental/",
            "eLTm8lS6ISvPhqgjOVU1+50Dnxc91zsLiNm7cc7wZHY=",
            null,
            Certificates.getGboCertificates(),
            Certificates.getFareOfficeCertificates()),

    //ex int3
    PRDSUPDEV("www-gbo-location-prdsupdev.csdev.ehiaws.com/", "gbo-location/",
            "www-gbo-profile-prdsupdev.csdev.ehiaws.com/", "gbo-profile/",
            "www-gbo-rental-prdsupdev.csdev.ehiaws.com/", "gbo-rental/",
            "eLTm8lS6ISvPhqgjOVU1+50Dnxc91zsLiNm7cc7wZHY=",
            null,
            Certificates.getGboCertificates(),
            Certificates.getFareOfficeCertificates()),

    PRDSUP("www-gbo-location-prdsup.csdev.ehiaws.com/", "gbo-location/",
            "www-gbo-profile-prdsup.csdev.ehiaws.com/", "gbo-profile/",
            "www-gbo-rental-prdsup.csdev.ehiaws.com/", "gbo-rental/",
            "eLTm8lS6ISvPhqgjOVU1+50Dnxc91zsLiNm7cc7wZHY=",
            null,
            Certificates.getGboCertificates(),
            Certificates.getFareOfficeCertificates()),

    //pen testing
    PEN_TESTING("www-gbo-location-prdsupqa.csdev.ehiaws.com/", "gbo-location/",
            "www-gbo-profile-prdsupqa.csdev.ehiaws.com/", "gbo-profile/",
            "www-gbo-rental-prdsupqa.csdev.ehiaws.com/", "gbo-rental/",
            "eLTm8lS6ISvPhqgjOVU1+50Dnxc91zsLiNm7cc7wZHY=",
            null,
            Certificates.getGboCertificates(),
            Certificates.getFareOfficeCertificates()),

    TMP_ENV("www-gbo-location-tmpenv.gbo.csdev.ehiaws-nonprod.com/", "gbo-location/",
            "www-gbo-profile-tmpenv.gbo.csdev.ehiaws-nonprod.com/", "gbo-profile/",
            "www-gbo-rental-tmpenv.gbo.csdev.ehiaws-nonprod.com/", "gbo-rental/",
            "eLTm8lS6ISvPhqgjOVU1+50Dnxc91zsLiNm7cc7wZHY=",
            null,
            Certificates.getGboCertificates(),
            Certificates.getFareOfficeCertificates()),

    //PROD

    PROD_EAST("www-gbo-location-east.enterprise.ehiaws.com/", "gbo-location/",
            "www-gbo-profile-east.enterprise.ehiaws.com/", "gbo-profile/",
            "www-gbo-rental-east.enterprise.ehiaws.com/", "gbo-rental/",
            "B6aNeXMCEMP6hHH7B38mcKcDyaLgVfYBpykns0s6Kes=",
            "Lex5My7H",
            Certificates.getGboCertificates(),
            Certificates.getFareOfficeCertificates()),

    PROD_WEST("www-gbo-location-west.enterprise.ehiaws.com/", "gbo-location/",
            "www-gbo-profile-west.enterprise.ehiaws.com/", "gbo-profile/",
            "www-gbo-rental-west.enterprise.ehiaws.com/", "gbo-rental/",
            "B6aNeXMCEMP6hHH7B38mcKcDyaLgVfYBpykns0s6Kes=",
            "Lex5My7H",
            Certificates.getGboCertificates(),
            Certificates.getFareOfficeCertificates()),

    BETA("www-gbo-location.enterprise.ehiaws.com/", "gbo-location/",
            "www-gbo-profile.enterprise.ehiaws.com/", "gbo-profile/",
            "www-gbo-rental.enterprise.ehiaws.com/", "gbo-rental/",
            "B6aNeXMCEMP6hHH7B38mcKcDyaLgVfYBpykns0s6Kes=",
            "Lex5My7H",
            Certificates.getGboCertificates(),
            Certificates.getFareOfficeCertificates());

    private String mGboLocationHostName;
    private String mGboLocationPath;
    private String mGboProfileHostName;
    private String mGboProfilePath;
    private String mGboRentalHostName;
    private String mGboRentalPath;
    private String mGboApiKey;
    private final List<Certificate> mCertificates;

    private final List<Certificate> mFareOfficeCertificates;

    private final String mPassword;
    private String mPaymentEnvironmentHost = "";

    Environment(final String gboLocationHostName, final String gboLocationPath,
                final String gboProfileHostName, final String gboProfilePath,
                final String gboRentalHostName, final String gboRentalPath,
                final String gboApiKey,
                final String password,
                final List<Certificate> certificates,
                final List<Certificate> fareOfficeCertificates) {
        mGboLocationHostName = gboLocationHostName;
        mGboLocationPath = gboLocationPath;
        mGboProfileHostName = gboProfileHostName;
        mGboProfilePath = gboProfilePath;
        mGboRentalHostName = gboRentalHostName;
        mGboRentalPath = gboRentalPath;
        mGboApiKey = gboApiKey;
        mPassword = password;
        mCertificates = certificates;
        mFareOfficeCertificates = fareOfficeCertificates;
    }

    public String getGboApiKey() {
        return mGboApiKey;
    }

    public boolean validatePassword(final String password) {
        return !requiresPassword() || this.mPassword.equals(password);
    }

    public boolean requiresPassword() {
        return this.mPassword != null;
    }

    public List<Certificate> getCertificates() {
        return mCertificates;
    }

    public String getGboLocationEndpoint() {
        final StringBuilder builder = new StringBuilder().append("https://")
                .append(mGboLocationHostName)
                .append(mGboLocationPath);
        return builder.toString();
    }

    public String getGboProfileEndpoint() {
        final StringBuilder builder = new StringBuilder().append("https://")
                .append(mGboProfileHostName)
                .append(mGboProfilePath);
        return builder.toString();
    }

    public String getGboRentalEndpoint() {
        final StringBuilder builder = new StringBuilder().append("https://")
                .append(mGboRentalHostName)
                .append(mGboRentalPath);
        return builder.toString();
    }

    public String getPaymentEnvironmentHost() {
        return mPaymentEnvironmentHost;
    }

    public void setPaymentEnvironmentHost(String paymentEnvironmentHost) {
        mPaymentEnvironmentHost = paymentEnvironmentHost;
    }

    public List<Certificate> getFareOfficeCertificates() {
        return mFareOfficeCertificates;
    }

    @Override
    public int length() {
        return getGboRentalEndpoint().length();
    }

    @Override
    public char charAt(int i) {
        return getGboRentalEndpoint().charAt(i);
    }

    @Override
    public CharSequence subSequence(int i, int i1) {
        return getGboRentalEndpoint().subSequence(i, i1);
    }

    public static Environment fromString(String selectedEnvName) {
        Environment defaultEnv = RCQA;
        for (Environment e : Environment.values()) {
            if (e.toString().equalsIgnoreCase(selectedEnvName)) {
                defaultEnv = e;
                break;
            }
        }
        return defaultEnv;
    }

}
