<%@ page import="platform.connect.api.SignedRequest" %>
<%@ page import="java.util.Map" %>
<%
    // Pull the signed request out of the request body and unsign it.
    Map<String, String[]> parameters = request.getParameterMap();
    String[] signedRequest = parameters.get("signed_request");
    if (signedRequest == null) {%>
        This App must be invoked via a signed request!<%
        return;
    }
    SignedRequest.unsign(signedRequest[0], "");
    SignedRequest.unsignToString(signedRequest[0], "");
%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" lang="en">
<head>

<title>Hello World Canvas Example</title>

<link rel="stylesheet" type="text/css" href="/sdk/css/connect.css" />
<!-- These can all be combined/compressed into a single file. -->
<script type="text/javascript" src="/sdk/js/connect.js"></script>
<script type="text/javascript" src="/sdk/js/xd.js"></script>
<script type="text/javascript" src="/sdk/js/cookies.js"></script>
<script type="text/javascript" src="/sdk/js/oauth.js"></script>
<script type="text/javascript" src="/scripts/client.js"></script>

</head>
<body>
    Hello World
</body>
</html>
