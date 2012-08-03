// Sfdc.canvas.parent
(function ($$) {

    "use strict";

    var module =   (function() {

        var listening = false,  url;

        function getDomain(url) {
            if ($$.isNil(url)) {
                return null;
            }
            // Strip any path or query strings.
            return url.replace( /([^:]+:\/\/[^\/]+).*/, '$1');
        }

        function getFrame() {
            // Make more robust
            return frames[0];
        }

        function callback(message) {

            if (message && message.data) {

                var type = message.data.body.type,
                    seq  = message.data.seq;

                var  lhandler = function(data, xmlHttp, config) {
                    var obj = {};
                    obj.seq = seq;
                    obj.payload = (xmlHttp.getResponseHeader("Content-Type").indexOf("application/json")) >= 0 ? Sfdc.JSON.parse(data) : data;
                    obj.status = xmlHttp.status;
                    obj.statusText = xmlHttp.statusText;
                    obj.responseHeaders = xmlHttp.getAllResponseHeaders();
                    $$.xd.post(obj, getDomain(url), getFrame());
                };

                if (type === 'ctx') {
                    Sfdc.Ajax.get("/services/data/v26.0/platformconnect/canvascontext",
                        lhandler,
                        {failure : lhandler, headers : {Authorization : "OAuth " + message.data.body.accessToken, Accept : "application/json"}});
                }
                else if (type === 'ajax') {
                    message.data.body.config.success = lhandler;
                    message.data.body.config.failure = lhandler;
                    Sfdc.Ajax.request(message.data.body.url, message.data.body.config);
                }

            }
        }

        function listen() {

            if (listening) {
                $$.xd.remove();
            }

            $$.xd.receive(callback, getDomain(url));
            listening = true;
        }

        function getSignedRequest(appid, elem, uid, canvasUrl, options) {

            var xuid = uid;
            var xelem = elem;
            var xurl =canvasUrl;
            var xoptions = options;
            var url = "/services/data/v26.0/platformconnect/signedrequest?appManifestId=" + appid;
            var sid = Sfdc.Cookie.getCookie("sid");

            Sfdc.Ajax.get(url, function(data) {
                var obj = Sfdc.JSON.parse(data);
                addSignedRequestFrame(obj.value, xelem, xuid, xurl, xoptions);
            }, {headers : {Authorization : "OAuth " + sid, Accept : "application/json"}});
        }

        function addSignedRequestFrame(sr, parent, uid, options) {

            var tmpForm, tmpInput, tmpSubmit, tmpFrame;

            tmpForm = document.createElement('form');
            tmpForm.setAttribute('name', "canvas-hidden-form");
            tmpForm.setAttribute('style', "display:none");
            tmpForm.setAttribute('action', url);
            tmpForm.setAttribute('target', "canvas-frame");
            tmpForm.setAttribute('method', "post");

            tmpInput = document.createElement('input');
            tmpInput.setAttribute('type', "text");
            tmpInput.setAttribute('name', "signed_request");
            tmpInput.setAttribute('id', "signed_request");
            tmpInput.value =sr;

            tmpSubmit = document.createElement('input');
            tmpSubmit.setAttribute('type', "submit");
            tmpSubmit.setAttribute('post', "post");

            tmpForm.appendChild(tmpInput);
            tmpForm.appendChild(tmpSubmit);

            tmpFrame=document.createElement('iframe');
            tmpFrame.setAttribute('id', uid);
            tmpFrame.setAttribute('name', 'canvas-frame');
            tmpFrame.style.border=options && options.frameborder || '0';
            tmpFrame.style.width = options && options.width || '800px';
            tmpFrame.style.height = options && options.height || '900px';
            tmpFrame.style.scrolling = options && options.scrolling || 'no';

            // Remove all child elements from the last time we rendered a canvas app
            while ( parent.firstChild ) {parent.removeChild( parent.firstChild );}
            parent.appendChild(tmpForm);
            parent.appendChild(tmpFrame);
            document.forms["canvas-hidden-form"].submit();
        }

        function addFrame(parent, uid, options) {
            var frameDoc, tmpFrame, frameObj;

            tmpFrame=document.createElement('iframe');
            tmpFrame.setAttribute('id', uid);
            tmpFrame.setAttribute('name', 'canvas-frame');
            tmpFrame.style.border=options && options.frameborder || '0';
            tmpFrame.style.width = options && options.width || '800px';
            tmpFrame.style.height = options && options.height || '900px';
            tmpFrame.style.scrolling = options && options.scrolling || 'no';

            // Remove all child elements from the last time we rendered a canvas app
            while ( parent.firstChild ) {parent.removeChild( parent.firstChild );}

            frameObj = parent.appendChild(tmpFrame);
            if (frameObj.contentDocument) {
                frameDoc = frameObj.contentDocument;
            }
            else if (frameObj.contentWindow) {
                frameDoc = frameObj.contentWindow.document;
            }
            frameDoc.location.replace(url);
        }

        function render(container, elem, options) {

            var id, uid, q, parentUrl, loginUrl;

            if ($$.isNil(container) || $$.isNil(container.app) || $$.isNil(container.app.canvasUrl) || $$.isNil(container.app.id)) {
                throw {name : "illegalArgumentException" , message : "App or App's contents missing."};
            }

            url = decodeURIComponent(container.app.canvasUrl);
            id  = container.app.id;
            loginUrl = container.login; // should we default here, also should already be encoded.

            uid = "if-" + id;

            elem = $$.isObject(elem) ? elem : $$.byId(elem);

            parentUrl = document.location.protocol + "//" + document.location.host;

            // Build the query string, strip off any existing hash tags, but preserve any parameters
            q = $$.param({loginUrl : loginUrl}) + "#" + encodeURIComponent(parentUrl);
            url = url.replace(/#.*$/, '');
            url += (/\?/.test( url ) ? "&" : "?") + q;

            if (container.app.authenticationType === "OAUTH") {
                addFrame(elem, uid, url, options);
            }
            else {
                getSignedRequest(container.app.id, elem, uid, url, options);
            }

            listen(url);
        }

        return {
            render : render
        };
    }());

    $$.module('Sfdc.canvas.parent', module);

}(Sfdc.canvas));