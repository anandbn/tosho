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
    String yourConsumerSecret="3470819724070098100";
    String signedRequestJson = SignedRequest.unsignAsJson(signedRequest[0], yourConsumerSecret);
%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" lang="en">
<head>

    <title>Hello World Canvas Example</title>

    <%--<link rel="stylesheet" type="text/css" href="/sdk/css/connect.css" />--%>

    <!-- These can all be combined/compressed into a single file. -->
    <script type="text/javascript" src="/sdk/js/connect.js"></script>
    <%--<script type="text/javascript" src="/sdk/js/xd.js"></script>--%>
    <%--<script type="text/javascript" src="/sdk/js/cookies.js"></script>--%>
    <%--<script type="text/javascript" src="/sdk/js/oauth.js"></script>--%>
    <%--<script type="text/javascript" src="/sdk/js/client.js"></script>--%>

    <!-- Third part libraries, substitute with your own -->
    <script type="text/javascript" src="/scripts/json2.js"></script>

    <script>
        if (self === top) {
            // Not in Iframe
            alert("This canvas app must be included within an iframe");
        }

        connect(function() {
            var sr = JSON.parse('<%=signedRequestJson%>');
            connect.byId('username').innerHTML = sr.context.user.fullName;
        });

    </script>
</head>
<body>
    <br/>
    <h1>Hello <span id='username'></span></h1>
</body>
</html>
