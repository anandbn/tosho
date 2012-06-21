package org.canvas.servlet;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

/**
 * ServerSide proxy for proxying request to remote server to get around cross domain issues.
 * This Proxy is only provided as an example and is provided as-is.
 */
public class ProxyServlet extends HttpServlet {

    // web.xml config parameter names
    private static final String INIT_PARAM_CONNECTION_TIMEOUT   = "connection_timeout";
    private static final String INIT_PARAM_FOLLOW_REDIRECTS     = "follow_redirects";
    private static final String INIT_PARAM_READ_TIMEOUT         = "read_timeout";
    private static final String INIT_PARAM_BUFFER_SIZE          = "buffer_size";
    private static final String INIT_PARAM_REMOTE_HOST          = "remote_host";

    // Configurable variables in web.xml
    private int     connectionTimeout;
    private boolean followRedirects;
    private int     readTimeout;
    private int     bufferSize;
    private String  remoteHost;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        connectionTimeout = getConfigParam(INIT_PARAM_CONNECTION_TIMEOUT, 5000);
        followRedirects   = getConfigParam(INIT_PARAM_FOLLOW_REDIRECTS, true);
        readTimeout       = getConfigParam(INIT_PARAM_READ_TIMEOUT, 0);
        bufferSize        = getConfigParam(INIT_PARAM_BUFFER_SIZE, 8 * 1024);
        remoteHost        = getConfigParam(INIT_PARAM_REMOTE_HOST, null);
    }

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if ("PATCH".equals(request.getMethod())) {
            invoke("PATCH", request, response);
        }
        else {
            super.service(request, response);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        invoke("GET", req, resp);
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        invoke("PUT", req, resp);
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        invoke("DELETE", req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        invoke("POST", req, resp);
    }

    @Override
    protected void doHead(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        invoke("HEAD", req, resp);
    }

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        invoke("OPTIONS", req, resp);
    }

    protected void invoke(String verb, HttpServletRequest request, HttpServletResponse response) throws IOException
    {
        if (remoteHost == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "remote_host not configured");
            return;
        }

        String uri = request.getRequestURI();
        String remoteUrl = remoteHost + uri;

        if (request == null || remoteUrl == null || remoteUrl.equalsIgnoreCase(request.getRequestURL().toString())) {
            String.format("Bad preconditions remoteUrl [%s] request.getRequestURL() [%s]", remoteUrl, request.getRequestURL().toString());
            return;
        }

        // Good to Go....

        URL url;
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try
        {
            //System.out.println("Producer URL: " + remoteUrl);

            url = new URL(remoteUrl);
            urlConnection = (HttpURLConnection)url.openConnection();

            urlConnection.setRequestMethod(verb);
            urlConnection.setInstanceFollowRedirects(followRedirects);
            urlConnection.setConnectTimeout(connectionTimeout);
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);
            if (readTimeout != 0) urlConnection.setReadTimeout(readTimeout);

            copyRequestHeaders(request, urlConnection);

            if (verb.equals("PUT") || verb.equals("PATCH") || verb.equals("POST") ) {
                copyBody(urlConnection, request.getInputStream());
            }

            response.setContentType(urlConnection.getContentType());
            if (! "gzip".equalsIgnoreCase(urlConnection.getContentEncoding())) {
                response.setCharacterEncoding(urlConnection.getContentEncoding());
            }

            int responseCode = urlConnection.getResponseCode();
            copyResponseHeaders(response, urlConnection);

            response.setStatus(responseCode);

            int contentLength = urlConnection.getContentLength();
            response.setContentLength(contentLength);

            if (responseCode != HttpURLConnection.HTTP_NOT_MODIFIED) {
                OutputStream outputStream = null;
                try
                {
                    inputStream = urlConnection.getInputStream();
                    outputStream = response.getOutputStream();
                    byte buffer[] = new byte[bufferSize];
                    while (true) {
                        int len = inputStream.read(buffer, 0, bufferSize);
                        if (len < 0) {
                            break;
                        }
                        outputStream.write(buffer, 0, len);
                    }
                } finally {
                    try { if (outputStream != null) outputStream.flush(); } catch (IOException e) {}
                    try { if (outputStream != null) outputStream.close(); } catch (IOException e) {}
                }
            }

        }
        finally {
            try { if (inputStream != null) inputStream.close(); } catch (IOException e) {}
            try { if (urlConnection != null) urlConnection.disconnect(); }  catch (Exception e) {}
        }
    }

    protected void copyBody(HttpURLConnection httpURLConnection, InputStream body) throws IOException
    {
        OutputStream outputStream = null;
        try {
            outputStream = httpURLConnection.getOutputStream();
            int val;
            while ((val = body.read()) != -1) {
                outputStream.write(val);
            }
        }
        finally {
            try { if (body != null) body.close(); } catch (IOException e) {}
            try { if (outputStream != null) outputStream.flush(); } catch (IOException e) {}
            try { if (outputStream != null) outputStream.close(); } catch (IOException e) {}
        }
    }

    protected void copyRequestHeaders(HttpServletRequest request, HttpURLConnection connection) {

        Enumeration headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {

            String headerName = (String) headerNames.nextElement();
            Enumeration headerValues = request.getHeaders(headerName);
            while (headerValues.hasMoreElements()) {

                String headerValue = (String) headerValues.nextElement();
                if (headerValue != null) {
                    connection.addRequestProperty(headerName, headerValue);
                }
            }
        }
    }

    protected void copyResponseHeaders(HttpServletResponse response, HttpURLConnection connection) {

        Map<String, List<String>> headers  = connection.getHeaderFields();
        for (String name : headers.keySet()) {
            List<String> values  = headers.get(name);

            for (int i = 0; i < values.size(); i++) {
                String value = values.get(i);
                if (name == null || value == null) {
                    continue;
                }
                response.addHeader(name, value);
            }
        }
    }

    // Helper methods
    private String getConfigParam(String name, String defaultValue) {
        String value = getServletConfig().getInitParameter(name);
        return (value == null || value.trim().length() == 0) ? defaultValue : value;
    }

    private int getConfigParam(String name, int defaultValue) {
        String value = getServletConfig().getInitParameter(name);
        return (value == null || value.trim().length() == 0) ? defaultValue : Integer.valueOf(value);
    }

    private boolean getConfigParam(String name, boolean defaultValue) {
        String value = getServletConfig().getInitParameter(name);
        return (value == null || value.trim().length() == 0) ? defaultValue : Boolean.valueOf(value);
    }
}
