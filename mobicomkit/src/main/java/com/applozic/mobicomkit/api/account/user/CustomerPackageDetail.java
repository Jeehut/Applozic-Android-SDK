package com.applozic.mobicomkit.api.account.user;

import com.applozic.mobicommons.json.JsonMarker;

//Cleanup: default
/**
 * @deprecated This class is no longer used and will be removed soon.
 */
@Deprecated
public class CustomerPackageDetail extends JsonMarker {
    private String applicationKey;
    private String packageName;
    private String bundleIdentifier;
    private String webInfo;

    public String getApplicationKey() {
        return applicationKey;
    }

    public void setApplicationKey(String applicationKey) {
        this.applicationKey = applicationKey;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getBundleIdentifier() {
        return bundleIdentifier;
    }

    public void setBundleIdentifier(String bundleIdentifier) {
        this.bundleIdentifier = bundleIdentifier;
    }

    public String getWebInfo() {
        return webInfo;
    }

    public void setWebInfo(String webInfo) {
        this.webInfo = webInfo;
    }
}
