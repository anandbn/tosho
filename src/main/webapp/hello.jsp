<%@ page import="canvas.SignedRequest" %>
<%@ page import="java.util.Map" %>
<%
    // Pull the signed request out of the request body and unsign it.
    Map<String, String[]> parameters = request.getParameterMap();
    String[] signedRequest = parameters.get("signed_request");
    if (signedRequest == null) {%>
        This App must be invoked via a signed request!<%
        return;
    }
    String yourConsumerSecret=System.getenv("CANVAS_CONSUMER_SECRET");
    String signedRequestJson = SignedRequest.unsignAsJson(signedRequest[0], yourConsumerSecret);
%>
<!DOCTYPE html>
<html>
    <head>
        <title>Hello World Canvas Example</title>

        <link rel="stylesheet" type="text/css" href="/sdk/css/canvas.css" />

        <script type="text/javascript" src="/sdk/js/canvas-all.js"></script>
        <script type="text/javascript" src="/scripts/json2.js"></script>

        <script>
            if (self === top) {
                // Not in Iframe
                alert("This canvas app must be included within an iframe");
            }

            Sfdc.canvas(function() {
                var sr = JSON.parse('<%=signedRequestJson%>');
                // Save the token
                Sfdc.canvas.oauth.token(sr.oauthToken);
                Sfdc.canvas.byId('fullname').innerHTML = sr.context.user.fullName;
                Sfdc.canvas.byId('profile').src = sr.instanceUrl + sr.context.user.profileThumbnailUrl +  "?oauth_token=" + sr.oauthToken;
                Sfdc.canvas.byId('firstname').innerHTML = sr.context.user.firstName;
                Sfdc.canvas.byId('lastname').innerHTML = sr.context.user.lastName;
                Sfdc.canvas.byId('username').innerHTML = sr.context.user.userName;
                Sfdc.canvas.byId('email').innerHTML = sr.context.user.email;
                Sfdc.canvas.byId('company').innerHTML = sr.context.organization.name;
            });

        </script>
    </head>
    <body>
    <div id="page">
        <div id="content">
            <div id="header">
                <h1 >Hello <span id='fullname'></span>!</h1>
                <h2>Welcome to the Hello World Java example!</h2>
            </div>

            <div id="canvas-content">
                <h1>Canvas Request</h1>
                <h2>Below is some information received in the Canvas Request:</h2>
                <div id="canvas-request">
                    <table border="0" width="100%">
                        <tr>
                            <td></td>
                            <td><b>First Name: </b><span id='firstname'></span></td>
                            <td><b>Last Name: </b><span id='lastname'></span></td>
                        </tr>
                        <tr>
                            <td><img id='profile' border="0" src="" /></td>
                            <td><b>Username: </b><span id='username'></span></td>
                            <td colspan="2"><b>Email Address: </b><span id='email'></span></td>
                        </tr>
                        <tr>
                            <td></td>
                            <td colspan="3"><b>Company: </b><span id='company'></span></td>
                        </tr>
                    </table>
                </div>

            </div>
        </div>

        <div id="footercont">
            <div id="footerleft">
                <p>Powered By: <a href="#" onclick="window.top.location.href='http://www.heroku.com'"><strong>Heroku</strong></a></p>
            </div>
            <div id="footerright">
                <p>Salesforce: <a title="Fafe Harbor" href="http://www.salesforce.com/company/investor/safe_harbor.jsp"><strong>SafeHarbor</strong></a></p>
            </div>
        </div>    </div>

    </body>
 </html>
