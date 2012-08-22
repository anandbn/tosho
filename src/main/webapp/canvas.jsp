<%@ page import="java.util.Map" %>
<%
    // Pull the signed request out of the request body and unsign it.
    Map<String, String[]> parameters = request.getParameterMap();
    String[] signedRequest = parameters.get("signed_request");
    if ("GET".equals(request.getMethod()) || signedRequest == null) {%>
    <jsp:forward page="welcome.jsp"/><%
    }
    else {%>
    <jsp:forward page="signed-request.jsp"/><%
    }
%>
