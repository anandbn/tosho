/*
 * Copyright, 1999-2012, salesforce.com All Rights Reserved Company Confidential
 */
package platform.connect.api;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * Describes all contextual information around external references, or links to external resources.
 * 
 * @author spepper
 * @since 180
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class CanvasLinkContext {
    private String enterpriseUrl;
    private String metadataUrl;
    private String partnerUrl;
    private String restUrl;
    private String sobjectUrl;
    private String searchUrl;
    private String queryUrl;
    private String recentItemsUrl;
    private String userProfileUrl;
    private String chatterFeedsUrl;
    private String chatterGroupsUrl;
    private String chatterUsersUrl;
    private String chatterFeedItemsUrl;

    /**
     * Provide the url for enterprise-wide external clients.
     */
    @JsonProperty("enterpriseUrl")
    public String getEnterpriseUrl() {
        return this.enterpriseUrl;
    }

    public void setEnterpriseUrl(String enterpriseUrl) {
        this.enterpriseUrl = enterpriseUrl;
    }

    /**
     * Provide the base url for the metadata api to access information about custom objects, apex classes, etc.
     */
    @JsonProperty("metadataUrl")
    public String getMetadataUrl() {
        return this.metadataUrl;
    }

    public void setMetadataUrl(String metadataUrl) {
        this.metadataUrl = metadataUrl;
    }

    /**
     * Access to the partner api for developing client applications for multiple organizations.
     */
    @JsonProperty("partnerUrl")
    public String getPartnerUrl() {
        return this.partnerUrl;
    }

    public void setPartnerUrl(String partnerUrl) {
        this.partnerUrl = partnerUrl;
    }

    /**
     * Access to the base url for RESTful services.
     */
    @JsonProperty("restUrl")
    public String getRestUrl() {
        return this.restUrl;
    }

    public void setRestUrl(String restUrl) {
        this.restUrl = restUrl;
    }

    /**
     * Access to custom sobject definitions.
     */
    @JsonProperty("sobjectUrl")
    public String getSobjectUrl() {
        return this.sobjectUrl;
    }

    public void setSobjectUrl(String sobjectUrl) {
        this.sobjectUrl = sobjectUrl;
    }

    /**
     * Access to search api.
     */
    @JsonProperty("searchUrl")
    public String getSearchUrl() {
        return this.searchUrl;
    }

    public void setSearchUrl(String searchUrl) {
        this.searchUrl = searchUrl;
    }

    /**
     * Access to the SOQL query api.
     */
    @JsonProperty("queryUrl")
    public String getQueryUrl() {
        return this.queryUrl;
    }

    public void setQueryUrl(String queryUrl) {
        this.queryUrl = queryUrl;
    }

    /**
     * Access to the recent items feed.
     */
    @JsonProperty("recentItemsUrl")
    public String getRecentItemsUrl() {
        return this.recentItemsUrl;
    }

    public void setRecentItemsUrl(String recentItemsUrl) {
        this.recentItemsUrl = recentItemsUrl;
    }

    /**
     * Retrieve more information about the current user.
     */
    @JsonProperty("userUrl")
    public String getUserUrl() {
        return this.userProfileUrl;
    }

    public void setUserUrl(String userProfileUrl) {
        this.userProfileUrl = userProfileUrl;
    }

    /**
     * Access to Chatter Feeds. Note: Requires user profile permissions, otherwise this will be null.
     */
    @JsonProperty("chatterFeedsUrl")
    public String getChatterFeedsUrl() {
        return this.chatterFeedsUrl;
    }

    public void setChatterFeedsUrl(String chatterFeedsUrl) {
        this.chatterFeedsUrl = chatterFeedsUrl;
    }

    /**
     * Access to Chatter Groups. Note: Requires user profile permissions, otherwise this will be null.
     */
    @JsonProperty("chatterGroupsUrl")
    public String getChatterGroupsUrl() {
        return this.chatterGroupsUrl;
    }

    public void setChatterGroupsUrl(String chatterGroupsUrl) {
        this.chatterGroupsUrl = chatterGroupsUrl;
    }

    /**
     * Access to Chatter Users. Note: Requires user profile permissions, otherwise this will be null.
     */
    @JsonProperty("chatterUsersUrl")
    public String getChatterUsersUrl() {
        return this.chatterUsersUrl;
    }

    public void setChatterUsersUrl(String chatterUsersUrl) {
        this.chatterUsersUrl = chatterUsersUrl;
    }

    /**
     * Access to individual Chatter Feed items. Note: Requires user profile permissions, otherwise this will be null.
     */
    @JsonProperty("chatterFeedItemsUrl")
    public String getChatterFeedItemsUrl() {
        return this.chatterFeedItemsUrl;
    }

    public void setChatterFeedItemsUrl(String chatterFeedItemsUrl) {
        this.chatterFeedItemsUrl = chatterFeedItemsUrl;
    }
    
    @Override
    public String toString()
    {
        return   enterpriseUrl+ ", " +
                 metadataUrl+ ", " +
                 partnerUrl+ ", " +
                 restUrl+ ", " +
                 sobjectUrl+ ", " +
                 searchUrl+ ", " +
                 queryUrl+ ", " +
                 recentItemsUrl+ ", " +
                 userProfileUrl+ ", " +
                 chatterFeedsUrl+ ", " +
                 chatterGroupsUrl+ ", " +
                 chatterUsersUrl+ ", " +
                 chatterFeedItemsUrl;
    }

}
