// Sfdc.canvas.client
(function ($$) {

    "use strict";

    var module =   (function() {

        var purl, cbs = {}, seq = 0;

        function getParentUrl() {
            // This relies on the parent passing it in.
            purl = purl || decodeURIComponent(document.location.hash.replace(/^#/, ''));
            return purl;
        }

        function callbacker(message) {
            if (message && message.data) {
                // If the server is telling us the access_token is invalid, wipe it clean.
                if (message.data.status === 401 &&
                    $$.isArray(message.data.payload) &&
                    message.data.payload[0].errorCode &&
                    message.data.payload[0].errorCode === "INVALID_SESSION_ID") {
                    // Session has expired logout.
                    $$.oauth.logout();
                }
                if ($$.isFunction(cbs[message.data.seq])) {
                    cbs[message.data.seq](message.data);
                }
                else {
                    // This can happen when the user switches out canvas apps real quick,
                    // before the request from the last canvas app have finish processing.
                    // We will ignore any of these results as the canvas app is no longer active to
                    // respond to the results.
                }
            }
        }

        function postit(clientscb, message) {
            // need to keep a mapping from request to callback, otherwise
            // wrong callbacks get called. Unfortunately, this is the only
            // way to handle this as postMessage acts more like topic/queue.
            // limit the sequencers to 100 avoid out of memory errors
            seq = (seq > 100) ? 0 : seq + 1;
            cbs[seq] = clientscb;
            var wrapped = {seq : seq, body : message};
            $$.xd.post(wrapped, getParentUrl(), parent);
        }

        function getContext(clientscb, token) {
            token = token || $$.oauth.token();
            postit(clientscb, {type : "ctx", accessToken : token});
        }

         function ajax(url, settings) {

            var config,
                defaults = {
                    method: 'GET',
                    async: true,
                    contentType: "application/json",
                    headers: {"Authorization" : "OAuth "  + (settings.token || $$.oauth.token()),
                        "Accept" : "application/json"},
                    data: null
                };

            if (!url) {
                throw {name : "illegalArgumentException" , message : "url required"};
            }
            if (!settings || !$$.isFunction(settings.success)) {
                throw {name : "illegalArgumentException" , message : "setting.success missing."};
            }

            var ccb = settings.success;
            config = $$.extend(defaults, settings || {});
            // Remove any listeners as functions cannot get marshaled.
            config.success = undefined;
            config.failure = undefined;
            postit(ccb, {type : "ajax", url : url, config : config});
        }

        $$.xd.receive(callbacker, getParentUrl());

        return {
            ctx : getContext,
            ajax : ajax
        };
    }());


    $$.module('Sfdc.canvas.client', module);

}(Sfdc.canvas));