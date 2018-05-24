package com.ehi.enterprise.android.network.responses.enroll;

import com.ehi.enterprise.android.models.profile.EHIProfile;
import com.ehi.enterprise.android.models.profile.EHIProfileResponse;

public class PostEnrollProfileResponse extends EHIProfileResponse {

    public EHIProfile getProfile() {
        return mProfile;
    }
}
