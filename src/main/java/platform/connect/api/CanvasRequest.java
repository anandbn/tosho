/*
 * Copyright, 1999-2012, salesforce.com
 * All Rights Reserved
 * Company Confidential
 */
package platform.connect.api;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

/**
 *
 * The canvas request is what is sent to the client on the very first request. In this canvas
 * request is information for authenticating and context about the user, organization and environment.
 * <p>
 * This class is serialized into JSON on then signed by the signature service to prevent tampering.
 *
 *
 * @author cjolley
 * @since 180
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class CanvasRequest {

    private String  algorithm;
    private Integer issuedAt;
    private String  userId;
    private String  OAuthToken;
    private String  params;
    private String  clientId;
    private String  refreshToken;
    private String  instanceUrl;
    private CanvasContext canvasContext;

    /**
     * The algorithm used to sign the request. typically HMAC-SHA256
     * @see platform.connect.service.SignRequestService.ALGORITHM
     */
    public String getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }

    /**
     * The unix time this request was issued at.
     */
    public Integer getIssuedAt() {
        return issuedAt;
    }

    public void setIssuedAt(Integer issuedAt) {
        this.issuedAt = issuedAt;
    }

    /**
     * The Salesforce unique id for this user.
     * @return
     */
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * The scoped OAuth token to be used to subsequent REST calls
     */
    public String getOAuthToken() {
        return OAuthToken;
    }

    public void setOAuthToken(String OAuthToken) {
        this.OAuthToken = OAuthToken;
    }

    /**
     * URL Parameters, put here to prevent tampering
     */
    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }

    /**
     * The Connect app client key.
     * @REVIEW: should we be sending this. they should already have this?
     * @return
     */
    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    /**
     * Scoped refresh token used to obtain an new access_token
     * @REVIEW - should we be sending this?
     * @return
     */
    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    /**
     * Context information about the user, org and environment.
     */
    public CanvasContext getContext() {
        return canvasContext;
    }

    public void setContext(CanvasContext canvasContext) {
        this.canvasContext = canvasContext;
    }

    /**
     * The base url for all subsequent REST call, this has the correct
     * Salesforce instance this organization is pinned to.
     */
    public String getInstanceUrl() {
        return instanceUrl;
    }

    public void setInstanceUrl(String instanceUrl) {
        this.instanceUrl = instanceUrl;
    }
}
