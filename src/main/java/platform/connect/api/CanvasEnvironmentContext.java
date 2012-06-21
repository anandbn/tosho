/*
 * Copyright, 1999-2012, salesforce.com All Rights Reserved Company Confidential
 */
package platform.connect.api;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * Environmental information about the canvas application.
 * 
 * @author spepper
 * @since 180
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class CanvasEnvironmentContext {
    private String locationId;
    private String referrerUrl;
    private String uiTheme;

    /**
     * The page/tab id of the current location.
     */
    @JsonProperty("locationId")
    public String getLocationId() {
        return this.locationId;
    }

    public void setLocationId(String locationId) {
        this.locationId = locationId;
    }

    /**
     * Returns the url of the current location.
     */
    @JsonProperty("referrerUrl")
    public String getReferrerUrl() {
        return this.referrerUrl;
    }

    public void setReferrerUrl(String referrerUrl) {
        this.referrerUrl = referrerUrl;
    }

    /**
     * Returns the value Theme2 if the user is using the newer user interface theme of the online application, labeled
     * \u201cSalesforce.\u201d Returns Theme1 if the user is using the older user interface theme, labeled
     * \u201cSalesforce Classic.\u201d
     * 
     * @see common.html.styles.UiSkin
     */
    @JsonProperty("uiTheme")
    public String getUiTheme() {
        return this.uiTheme;
    }

    public void setUiTheme(String uiTheme) {
        this.uiTheme = uiTheme;
    }

    @Override
    public String toString()
    {
        return locationId + ", " +
               referrerUrl + ", " +
               uiTheme;
    }

}
