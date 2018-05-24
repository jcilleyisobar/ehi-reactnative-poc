package com.ehi.enterprise.android.app;

import java.util.List;
import java.security.cert.Certificate;

public enum SolrEnvironment implements CharSequence {

    XQA1("xqa1.location.enterprise.com/", "enterprise-sls/search/location/mobile/",
            "31b5fdfe-2edd-46d3-bda0-a500875a01b0",
            Certificates.getSolrQACertificates()),

    XQA2("xqa2.location.enterprise.com/", "enterprise-sls/search/location/mobile/",
            "31b5fdfe-2edd-46d3-bda0-a500875a01b0",
            Certificates.getSolrQACertificates()),

    XQA3("xqa3.location.enterprise.com/", "enterprise-sls/search/location/mobile/",
            "31b5fdfe-2edd-46d3-bda0-a500875a01b0",
            Certificates.getSolrQACertificates()),

    INT1("int1.location.enterprise.com/", "enterprise-sls/search/location/mobile/",
            "31b5fdfe-2edd-46d3-bda0-a500875a01b0",
            Certificates.getSolrQACertificates()),

    INT2("int2.location.enterprise.com/", "enterprise-sls/search/location/mobile/",
            "31b5fdfe-2edd-46d3-bda0-a500875a01b0",
            Certificates.getSolrQACertificates()),


    PROD("prd.location.enterprise.com/", "enterprise-sls/search/location/mobile/",
            "bd6dfa74-8881-4db5-8268-1de81dd504e8",
            Certificates.getSolrProdCertificates());

    private final String mSolrHost;
    private final String mSolrPath;
    private final String mSolrApiKey;
    private final List<Certificate> mSolrCertificates;

    SolrEnvironment(final String solrHostName,
                    final String solrPath,
                    final String solrApiKey,
                    final List<Certificate> solrCertificates) {
        mSolrHost = solrHostName;
        mSolrPath = solrPath;
        mSolrApiKey = solrApiKey;
        mSolrCertificates = solrCertificates;
    }

    public String getSolrApiKey() {
        return mSolrApiKey;
    }

    public String getSolrHost() {
        return mSolrHost;
    }

    public String getSolrEndpoint() {
        return "https://" + mSolrHost + mSolrPath;
    }

    public List<Certificate> getSolrCertificates() {
        return mSolrCertificates;
    }

    public boolean hasSolrCertificates() {
        return mSolrCertificates != null && mSolrCertificates.size() > 0;
    }

    public static SolrEnvironment fromString(String selectedEnvName) {
        SolrEnvironment defaultEnv = XQA1;
        for (SolrEnvironment e : SolrEnvironment.values()) {
            if (e.toString().contains(selectedEnvName)) {
                defaultEnv = e;
                break;
            }
        }
        return defaultEnv;
    }

    @Override
    public int length() {
        return getSolrEndpoint().length();
    }

    @Override
    public char charAt(int index) {
        return getSolrEndpoint().charAt(index);
    }

    @Override
    public CharSequence subSequence(int start, int end) {
        return getSolrEndpoint().subSequence(start, end);
    }
}
