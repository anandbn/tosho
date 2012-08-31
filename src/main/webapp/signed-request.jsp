<%--
Copyright (c) 2011, salesforce.com, inc.
All rights reserved.

Redistribution and use in source and binary forms, with or without modification, are permitted provided
that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the
following disclaimer.

Redistributions in binary form must reproduce the above copyright notice, this list of conditions and
the following disclaimer in the documentation and/or other materials provided with the distribution.

Neither the name of salesforce.com, inc. nor the names of its contributors may be used to endorse or
promote products derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED
WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED
TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
POSSIBILITY OF SUCH DAMAGE.
--%>

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
        <title>Force.com Canvas Java Quick Start</title>

        <link rel="stylesheet" type="text/css" href="/sdk/css/canvas.css" />
        <link rel="stylesheet" media="screen" href="css/bootstrap.min.css">
        <link rel="stylesheet" media="screen" href="css/bootstrap-responsive.min.css">

        <script type="text/javascript" src="/sdk/js/canvas-all.js"></script>
        <script type="text/javascript" src="/scripts/json2.js"></script>
        <script type="text/javascript" src="/scripts/chatter-talk.js"></script>
		<script type="text/javascript" src="/scripts/jquery-1.7.1.min.js"></script>
		<script type="text/javascript" src="/scripts/bootstrap.min.js"></script>
		<script src="https://raw.github.com/jonnyreeves/jquery-Mustache/master/src/jquery-Mustache.js"></script>
		<script src="https://raw.github.com/janl/mustache.js/master/mustache.js"></script>
        
		<style type="text/css">
		
		.thumbnail{
		  width: 200px;
		  height: 200px;
		}
		.thumbnail p {
			overflow: scroll;
		}
		blockquote p{
		   font-size:14px;
		}

		h5 a:hover{
			background-color:none;
		}
		</style>
        <script>
        	$.noConflict();
            if (self === top) {
                // Not in Iframe
                alert("This canvas app must be included within an iframe");
            }

            Sfdc.canvas(function() {
                var sr = JSON.parse('<%=signedRequestJson%>');
                // Save the token
                Sfdc.canvas.oauth.token(sr.oauthToken);
               
                var queryUrl = sr.instanceUrl+sr.context.links.queryUrl;
                var soqlQuery="SELECT Id,Name,Author__c,Famous_Line__c from Book__c limit 10";
                Sfdc.canvas.client.ajax(queryUrl+"?q="+soqlQuery,
				{
					token : sr.oauthToken,
					method: 'GET',
					contentType: "application/json",
					success : function(data) {
							
							  	var template = '<ul class="thumbnails">{{#records}}<li><div class="thumbnail"><img src="/barcode?code={{Id}}" alt=""><h5>'+
							  				   '<a href="#">{{Name}}</a></h5>'+
							  				   '<blockquote class="pull-right"><p>{{Famous_Line__c}}</p><small>{{Author__c}}</small></blockquote></div></li>{{/records}}</ul>';
								var html = Mustache.to_html(template, data.payload);
								jQuery("#contactBadges").html(html);
							  }
				});
            });

        </script>
    </head>
    <body>
    <div id="page">
        <div id="content">
            <div id="header">
                <h1 >Hello <span id='fullname'></span>!</h1>
                <h2>Force.com Canvas-Heroku - Library Book label Printer Sample</h2>
            </div>

            <div id="container">
                 <div class="row">
					<div class="span1">
					</div>
					<div class="span11" id="contactBadges">
						
					</div>
				</div>
            </div>
        </div>

        <div id="footercont">
            <div id="footerleft">
                <p>Powered By: <a title="Heroku" href="#" onclick="window.top.location.href='http://www.heroku.com'"><strong>Heroku</strong></a></p>
            </div>
            <div id="footerright">
                <p>Salesforce: <a title="Safe Harbor" href="http://www.salesforce.com/company/investor/safe_harbor.jsp"><strong>SafeHarbor</strong></a></p>
            </div>
        </div>    </div>

    </body>
 </html>
