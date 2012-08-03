// Sfdc.canvas.xd
// concept lifted from Josh Fraser - http://www.onlineaspect.com/2010/01/15/backwards-compatible-postmessage
(function ($$, window) {

    "use strict";

    var module =   (function() {

        var intervalId,
            lastHash,
            cacheBust = 1,
            internalCallback;

        function postMessage(message, target_url, target) {
            if (!target_url) {
                return;
            }
            target = target || parent;  // default to parent
            if (window.postMessage) {
                // the browser supports window.postMessage, so call it with a targetOrigin
                // set appropriately, based on the target_url parameter.

                // strip  out just the {scheme}://{host}:{port} - remove any path and query string information
                target.postMessage(message, target_url.replace( /([^:]+:\/\/[^\/]+).*/, '$1'));
            } else if (target_url) {
                // the browser does not support window.postMessage, so use the window.location.hash fragment hack
                target.location = target_url.replace(/#.*$/, '') + '#' + (+new Date()) + (cacheBust++) + '&' + message;
            }
        }

        function receiveMessage(callback, source_origin) {

            // browser supports window.postMessage
            if (window.postMessage) {
                // bind the callback to the actual event associated with window.postMessage
                if (callback) {
                    internalCallback = function(e) {
                        if ((typeof source_origin === 'string' && e.origin !== source_origin)
                            || ($$.isFunction(source_origin) && source_origin(e.origin) === false)) {
                                return false;
                        }
                        callback(e);
                    };
                }
                if (window.addEventListener) {
                    window.addEventListener('message', internalCallback, false);
                } else {
                    window.attachEvent('onmessage', internalCallback);
                }
            } else {
                // a polling loop is started & callback is called whenever the location.hash changes
                if (intervalId) {clearInterval(intervalId);}
                intervalId = null;
                if (callback) {
                    intervalId = setInterval(function() {
                        var hash = document.location.hash,
                            re = /^#?\d+&/;
                        if (hash !== lastHash && re.test(hash)) {
                            lastHash = hash;
                            callback({data: hash.replace(re, '')});
                        }
                    }, 100);
                }
            }
        }

        function removeListener() {

            // browser supports window.postMessage
            if (window.postMessage) {
                if (window.removeEventListener) {
                    window.removeEventListener('message', internalCallback, false);
                } else {
                    window.detachEvent('onmessage', internalCallback);
                }
            } else {
                // a polling loop is started & callback is called whenever the location.hash changes
                if (intervalId) {clearInterval(intervalId);}
                intervalId = null;
            }
        }

        return {
            post : postMessage,
            receive : receiveMessage,
            remove : removeListener
        };
    }());

    $$.module('Sfdc.canvas.xd', module);

}(Sfdc.canvas, this));