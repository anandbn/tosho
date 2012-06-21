/*
 * Copyright, 1999-2012, salesforce.com All Rights Reserved Company Confidential
 */
package platform.connect.api;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * Describes contextual information about the current organization/company.
 * 
 * @author spepper
 * @since 180
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class CanvasOrganizationContext {
    private String organizationId;
    private String name;
    private boolean multicurrencyEnabled;
    private String currencyISOCode;

    /**
     * The organization id of the organization.
     */
    @JsonProperty("organizationId")
    public String getOrganizationId() {
        return this.organizationId;
    }

    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }

    /**
     * The name of the company or organization.
     */
    @JsonProperty("name")
    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * Indicates whether the user\u2019s organization uses multiple currencies (true) or not (false).
     */
    @JsonProperty("multicurrencyEnabled")
    public boolean isMulticurrencyEnabled() {
        return this.multicurrencyEnabled;
    }

    public void setMulticurrencyEnabled(boolean multicurrencyEnabled) {
        this.multicurrencyEnabled = multicurrencyEnabled;
    }

    /**
     * Current company's default currency ISO code (applies only if multi-currency is disabled for the org).
     */
    @JsonProperty("currencyIsoCode")
    public String getCurrencyISOCode() {
        return this.currencyISOCode;
    }

    public void setCurrencyISOCode(String currencyISOCode) {
        this.currencyISOCode = currencyISOCode;
    }

    @Override
    public String toString()
    {
        return organizationId + ","+
               name + ","+
               multicurrencyEnabled + ","+
               currencyISOCode;
    }
}
