package org.canvas.servlet;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.PostMethod;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class OAuthServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public static final String ACCESS_TOKEN = "ACCESS_TOKEN";
	public static final String INSTANCE_URL = "INSTANCE_URL";

	private String clientId     = null;
	private String clientSecret = null;
	private String redirectUri  = null;
	private String authUrl      = null;
	private String tokenUrl     = null;

	public void init() throws ServletException {

        String environment;

		clientId = this.getInitParameter("clientId");
		clientSecret = this.getInitParameter("clientSecret");
		redirectUri = this.getInitParameter("redirectUri");    // https://canvas.herokuapp.com/oauth/_callback
		environment = this.getInitParameter("environment");    // https://login.salesforce.com

		try {
			authUrl = environment
					+ "/services/oauth2/authorize?response_type=code&client_id="
					+ clientId + "&redirect_uri="
					+ URLEncoder.encode(redirectUri, "UTF-8");

            // Nots: &scope=email,read_chatter,... would be added here for oauth scope

		} catch (UnsupportedEncodingException e) {
			throw new ServletException(e);
		}

		tokenUrl = environment + "/services/oauth2/token";
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        System.out.println("Begin OAuth");

		String accessToken = (String) request.getSession().getAttribute(ACCESS_TOKEN);

		if (accessToken == null) {

			String instanceUrl = null;

			if (request.getRequestURI().endsWith("oauth")) {
				// we need to send the user to authorize
				response.sendRedirect(authUrl);
				return;
			} else {
				System.out.println("Auth successful - got callback");

				String code = request.getParameter("code");

				HttpClient httpclient = new HttpClient();

				PostMethod post = new PostMethod(tokenUrl);
				post.addParameter("code", code);
				post.addParameter("grant_type", "authorization_code");
				post.addParameter("client_id", clientId);
				post.addParameter("client_secret", clientSecret);
				post.addParameter("redirect_uri", redirectUri);

				try {
					httpclient.executeMethod(post);

					try {
						JSONObject authResponse = new JSONObject(
								new JSONTokener(new InputStreamReader(
										post.getResponseBodyAsStream())));
						System.out.println("xAuth response: "
								+ authResponse.toString(2));

						accessToken = authResponse.getString("access_token");

                        // Instance URL is Salesforce specific.
						instanceUrl = authResponse.getString("instance_url");

						System.out.println("Got access token: " + accessToken);
					} catch (JSONException e) {
						e.printStackTrace();
						throw new ServletException(e);
					}
                } catch (HttpException e) {
                    e.printStackTrace();
                    throw new ServletException(e);
                }
				finally {
					post.releaseConnection();
				}
			}

			// Set a session attribute so that other servlets can get the access
			// token
			request.getSession().setAttribute(ACCESS_TOKEN, accessToken);

			// We also get the instance URL from the OAuth response, so set it
			// in the session too
			request.getSession().setAttribute(INSTANCE_URL, instanceUrl);
		}

		response.sendRedirect(request.getContextPath() + "/index.html");
	}
}
