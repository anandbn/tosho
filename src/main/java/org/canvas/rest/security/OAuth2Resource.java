package org.canvas.rest.security;

import com.sun.jersey.api.view.Viewable;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.PostMethod;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;

/**
 * OAuth2Resource
 *
 * @author cjolley
 */
@Path("/oauth2")
public class OAuth2Resource {

    // For working on Heroku
//    private final static String CLIENT_ID       = "3MVG9QDx8IX8nP5QYEJ48pkpxl49Vlpu_WpuXp0.159BvxH2HYRb7jFZaihagf0R.DvHm.GL92oyIQ8ZxW_.6";
//    private final static String CLIENT_SECRET   = "5337471331272406440";
//    private final static String REDIRECT_URI    = "https://canvas.herokuapp.com/rest/oauth2/_callback";

    // For localhost debugging
//    private final static String CLIENT_ID       = "3MVG9QDx8IX8nP5QYEJ48pkpxlzvIvsxmQrPlfLa7pXfvLS5w.Qq60K.Qpu_DRaJ7XmAbTrH6azkh.pGB1l6A";
//    private final static String CLIENT_SECRET   = "4960486608388807153";
//    private final static String REDIRECT_URI    = "https://localhost:8443/rest/oauth2/_callback";


    // Salesforce information  http://wiki.developerforce.com/page/Digging_Deeper_into_OAuth_2.0_on_Force.com
    private final static String AUTHORIZE_URL   = "%s/services/oauth2/authorize?response_type=code&client_id=%s&redirect_uri=%s&scope=%s&display=%s&state=%s";
    private final static String ENVIRONMENT     = "https://login.salesforce.com";
//    private final static String ENVIRONMENT     = "http://cjolley-wsl.internal.salesforce.com:8080";
    //private final static String ENVIRONMENT     = "https://localhost:8443";
    private final static String SCOPE           = "full"; //"api%20refresh_token"; //chatter_api
    private final static String DISPLAY         = "popup";   // page, popup, touch
    private final static String TOKEN_URL       = ENVIRONMENT + "/services/oauth2/token";

    // TODO: Do we want to cache the access token in the session and on subsequent requests just return that, if available
    // TODO: Better validating/defaulting. Rather not pass anything if nothing supplied for optional params

    @GET
    @Produces("text/html")
    public Response getRedirect(@DefaultValue(SCOPE) @QueryParam("scope") String scope,
                            @DefaultValue(DISPLAY) @QueryParam("display") String display,
                            @DefaultValue(ENVIRONMENT) @QueryParam("environment") String environment,
                            @QueryParam("client_id") String clientId,
                            @QueryParam("client_secret") String clientSecret,
                            @QueryParam("redirect_uri") String redirectUri,
                            @QueryParam("state") String state,
                            @Context HttpServletRequest httpRequest) {

//        @DefaultValue(CLIENT_ID) @QueryParam("client_id") String clientId,
//        @DefaultValue(CLIENT_SECRET) @QueryParam("client_secret") String clientSecret,
//        @DefaultValue(REDIRECT_URI) @QueryParam("redirect_uri") String redirectUri,

        System.out.println("STATE1: " + state);

        // @TODO: this is just temporary until I get client side flow going.
        httpRequest.getSession().setAttribute("CLIENT_ID", clientId);
        httpRequest.getSession().setAttribute("CLIENT_SECRET", clientSecret);
        httpRequest.getSession().setAttribute("REDIRECT_URI", redirectUri);

        if (clientId == null || clientSecret == null || redirectUri == null) {
            Response response = Response.status(400).entity("client_id, client_secret, and/or redirect_uri null").type("text/plain").build();
            throw new WebApplicationException(response);
        }

        // @TODO: will the environment ever change?

        try {
            // @TODO: Are we double encoding redirectUri and State?
            String url = String.format(AUTHORIZE_URL, environment, clientId, URLEncoder.encode(redirectUri, "UTF-8"), scope, display, URLEncoder.encode(state, "UTF-8"));
            System.out.println("Debug URL: " + url);
            URI uri = URI.create(url);
            return Response.temporaryRedirect(uri).build();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            Response response = Response.status(500).entity(e.getMessage()).type("text/plain").build();
            throw new WebApplicationException(response);
        }
    }

    @GET
    @Produces("text/html")
    @Path("/_callback")
    public Response getCallback(@QueryParam("code") String code,
                                @QueryParam("state") String state,
                                @Context HttpServletRequest httpRequest) {

        System.out.println("inside callback");

        // @TODO create a CodeParam and validate everything there.
        if (code == null || code.trim().length() == 0) {
            Response response = Response.status(400).entity("query param 'code' required").type("text/plain").build();
            throw new WebApplicationException(response);
        }

        HttpSession session = httpRequest.getSession(false);
        if (session == null) {
            Response response = Response.status(400).entity("client id and secret not available").type("text/plain").build();
            throw new WebApplicationException(response);
        }
        String clientId = (String)session.getAttribute("CLIENT_ID");
        String clientSecret = (String)session.getAttribute("CLIENT_SECRET");
        String redirectUri = (String)session.getAttribute("REDIRECT_URI");
        if (clientId == null || clientSecret == null || redirectUri == null) {
            Response response = Response.status(400).entity("client id and secret null").type("text/plain").build();
            throw new WebApplicationException(response);
        }

        PostMethod post = new PostMethod(TOKEN_URL);
        post.addParameter("code", code);
        post.addParameter("grant_type", "authorization_code");
        post.addParameter("client_id", clientId);
        post.addParameter("client_secret", clientSecret);
        post.addParameter("redirect_uri", redirectUri);

        try {
            HttpClient httpclient = new HttpClient();
            httpclient.executeMethod(post);

            try {
                JSONObject authResponse = new JSONObject(
                        new JSONTokener(new InputStreamReader(post.getResponseBodyAsStream())));
                System.out.println("Auth response: " + authResponse.toString(2));

                String accessToken = authResponse.getString("access_token");
                String instanceUrl = authResponse.getString("instance_url");

                // Set the access_token as a cookie on the browser. Need to check with security to see if this is
                // a valid thing to do (at least set the max age).
//                String redirect = (state != null) ? state : "/child.html";

                System.out.println("STATE: " + state);

                String redirect = state; //"/crazyrefresh.html";
                //return Response.seeOther(redirectUri).build();}

                String domain = new NewCookie("hack", accessToken).getDomain();
                //System.out.println("Setting cookies with domain: " + domain);
                return Response.ok(new Viewable(redirect))
                               .cookie(new NewCookie("access_token", accessToken, "/", domain, null, NewCookie.DEFAULT_MAX_AGE, true))
                               .cookie(new NewCookie("instance_url", instanceUrl, "/", domain, null, NewCookie.DEFAULT_MAX_AGE, true))
                               .build();

            } catch (JSONException e) {
                e.printStackTrace();
                Response response = Response.status(500).entity(e.getMessage()).type("text/plain").build();
                throw new WebApplicationException(response);
            }
        } catch (HttpException e) {
            e.printStackTrace();
            Response response = Response.status(500).entity(e.getMessage()).type("text/plain").build();
            throw new WebApplicationException(response);
        } catch (IOException e) {
            e.printStackTrace();
            Response response = Response.status(500).entity(e.getMessage()).type("text/plain").build();
            throw new WebApplicationException(response);
        }
        finally {
            post.releaseConnection();
        }
    }
}
