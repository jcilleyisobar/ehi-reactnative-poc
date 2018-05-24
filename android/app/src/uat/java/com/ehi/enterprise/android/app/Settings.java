package com.ehi.enterprise.android.app;

import com.ehi.enterprise.android.network.services.EnterpriseNetworkService;

public class Settings {

    public static final boolean SHOW_LOGS = true;
    public static final boolean LOG_CRASHES = true;

    public static final Class API_SERVICE = EnterpriseNetworkService.class;

    // CHANGE THIS TO CHANGE DEFAULT ENVIRONMENT
    public static Environment ENVIRONMENT = Environment.RCQA;
    public static SolrEnvironment SOLR_ENVIRONMENT = SolrEnvironment.XQA1;

    public static String EHI_GBO_API_KEY = ENVIRONMENT.getGboApiKey();
    public static String EHI_GBO_LOCATION = ENVIRONMENT.getGboLocationEndpoint();
    public static String EHI_GBO_PROFILE = ENVIRONMENT.getGboProfileEndpoint();
    public static String EHI_GBO_RENTAL  = ENVIRONMENT.getGboRentalEndpoint();

    public static final String EHI_GBO_ENDPOINT_API = "api/v2";
    public static final String CHANNEL = "mobile";
    public static final String SOURCE_CODE = "EMOBILEAPP";
    public static final String ENROLL_SOURCE_CODE = "EMBLAPPMEM";
    public static final String BRAND = "ENTERPRISE";

    public static String SOLR_ENDPOINT_URL = SOLR_ENVIRONMENT.getSolrEndpoint();
    public static String SOLR_API_KEY = SOLR_ENVIRONMENT.getSolrApiKey();
    public static final String SOLR_BRAND = "ENTERPRISE";
    public static final String SOLR_FALLBACK_LANGUAGE = "en_GB"; //for R1

	public static final String ENTERPRISE_PLUS = "EP";
	public static final String EMERALD_CLUB = "EC";

	public static final int MAX_CREDIT_CARDS = 4;

    public static final String NATIONAL_REDIRECT = "https://www.nationalcar.com/en_US/car-rental/home.html";
    public static final String ALAMO_REDIRECT = "https://www.alamo.com/en_US/car-rental/home.html";
	public static final String JOIN_NOW_WEBSITE = "https://www.enterprise.com/en/enroll.html";
	public static final String FORCE_UPGRADE_URL = "https://play.google.com/store/apps/details?id=com.ehi.enterprise.android";
    public static final String USER_FEEDBACK_URL = "https://secure.opinionlab.com/ccc01/o.asp?id=mOSGxAOb";

	public static final String FORESEE_PRIVACY_POLICE = "http://www.foresee.com/about-us/privacy-policy";

	public static final String LOCALYTICS_KEY = "e876f4671e5a080d66e5e8a-20772a38-1f49-11e5-44ff-006918dcf667";

	public static final String CRITTERCISM_KEY = "52fb93e98b2e332c30000001";

	public static final String APPSEE_KEY = "75eb76b3b2234965a4f149b9a1295a9a";

    public static String setServicesEndpoint(Environment env) {
        ENVIRONMENT = env;
        EHI_GBO_LOCATION = env.getGboLocationEndpoint();
        EHI_GBO_PROFILE = env.getGboProfileEndpoint();
        EHI_GBO_RENTAL = env.getGboRentalEndpoint();
        EHI_GBO_API_KEY = env.getGboApiKey();
        return EHI_GBO_RENTAL;
    }

    public static String setSolrEndpoint(SolrEnvironment solrEnv) {
        SOLR_ENDPOINT_URL = solrEnv.getSolrEndpoint();
        SOLR_API_KEY = solrEnv.getSolrApiKey();
        return SOLR_ENDPOINT_URL;
    }
}