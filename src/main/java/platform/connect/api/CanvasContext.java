/*
 * Copyright, 1999-2012, salesforce.com
 * All Rights Reserved
 * Company Confidential
 */
package platform.connect.api;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * Describes all contextual information related to canvas applications.
 * 
 * <p>
 * Some information within the context depends on what oauth scopes are allowed
 * on the canvas application. Some/all items may be null if the oauth scope is
 * not set accordingly.
 *
 * @author spepper
 * @since 180
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class CanvasContext {
    
    private CanvasUserContext userContext = null;
    private CanvasOrganizationContext orgContext = null;
    private CanvasLinkContext linkContext = null;
    private CanvasEnvironmentContext envContext = null;

    
    /**
     * Provides the context about the current user.
     * 
     * @return The current user context, or null if the oauth scope
     * will not allow.
     */
    @JsonProperty("user")
    public CanvasUserContext getUserContext() {
        return this.userContext;
    }    
    
    /**
     * Sets the context about the current user.
     */
    @JsonProperty("user")
    public void setUserContext(CanvasUserContext userContext)
    {
        this.userContext = userContext;
    }
    
    /**
     * Provides the context about the current organization.
     * 
     * @return The current organization context, or null if the oauth scope
     * will not allow.
     */
    @JsonProperty("organization")
    public CanvasOrganizationContext getOrganizationContext() {
        return orgContext;
    }    
    
    /**
     * Sets the context about the current organization.
     */
    @JsonProperty("organization")
    public void setOrganizationContext(CanvasOrganizationContext orgContext)
    {
        this.orgContext = orgContext;
    }
    
    /**
     * Provides the context about the current environment (page, url, etc).
     */
    @JsonProperty("environment")
    public CanvasEnvironmentContext getEnvironmentContext() {
        return envContext;
    }
    
    @JsonProperty("environment")
    public void setEnvironmentContext(CanvasEnvironmentContext envContext){
        this.envContext = envContext;
    }
    
    /**
     * Provides links to external resources within sfdc.
     */
    @JsonProperty("links")
    public CanvasLinkContext getLinkContext() {
        return linkContext;
    }
    
    /**
     * Sets the link context for this request.
     * @param linkContext
     */
    @JsonProperty("links")
    public void setLinkContext(CanvasLinkContext linkContext)
    {
        this.linkContext = linkContext;
    }
    
    @Override
    public String toString()
    {
        return String.format("Canvas Context:\n\t" + 
                             "User Context:\n\t\t%s\n\t"+
                             "Org Context:\n\t\t%s\n\t"+
                             "Environment Context:\n\t\t%s\n\t"+
                             "Link Context:\n\t\t%s\n", 
                             null != userContext?userContext.toString():"null",
                             null != orgContext?orgContext.toString():"null",
                             null != envContext?envContext.toString():"null",
                             null != linkContext?linkContext.toString():"null");
    }
}
