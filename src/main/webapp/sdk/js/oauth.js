// Sfdc.canvas.oauth
(function ($$) {

    "use strict";

    var module =   (function() {

        var accessToken,
            instanceUrl,
            childWindow;

        function query(params) {
            var r = [], n;
            if (!$$.isUndefined(params)) {
                for (n in params) {
                    if (params.hasOwnProperty(n)) {
                        // probably should encode these
                        r.push(n + "=" + params[n]);
                    }
                }
                return "?" + r.join('&');
            }
            return '';
        }

        function login(ctx) {
            var uri;

            ctx = ctx || {};
            uri = ctx.uri || "/rest/oauth2";
            ctx.params = ctx.params || {state : ""};
            ctx.params.state = ctx.params.state || ctx.callback || window.location.pathname;  // @TODO REVIEW THIS
            ctx.params.display= ctx.params.display || 'popup';
            uri = uri + query(ctx.params);
            childWindow = window.open(uri, 'OAuth', 'status=0,toolbar=0,menubar=0,resizable=0,scrollbars=1,top=50,left=50,height=500,width=680');
        }

        function refresh() {
            self.location.reload();
        }

        function token(t) {
            if (arguments.length === 0) {
                if (!$$.isNil(accessToken)) {return accessToken;}
                accessToken = $$.cookies.get("access_token");
            }
            else if (t === null) {
                $$.cookies.remove("access_token");
                accessToken = null;
            }
            else {
                $$.cookies.set("access_token", t);
                accessToken = t;
            }
            return accessToken;
        }

        function instance(i) {
            if (arguments.length === 0) {
                if (!$$.isNil(instanceUrl)) {return instanceUrl;}
                instanceUrl = $$.cookies.get("instance_url");
            }
            else if (i === null) {
                $$.cookies.remove("instance_url");
                instanceUrl = null;
            }
            else {
                $$.cookies.set("instance_url", i);
                instanceUrl = i;
            }
            return instanceUrl;
        }

        // Example Results of tha hash....
        // Name [access_token] Value [00DU0000000Xthw!ARUAQMdYg9ScuUXB5zPLpVyfYQr9qXFO7RPbKf5HyU6kAmbeKlO3jJ93gETlJxvpUDsz3mqMRL51N1E.eYFykHpoda8dPg_z]
        // Name [instance_url] Value [https://na12.salesforce.com]
        // Name [id] Value [https://login.salesforce.com/id/00DU0000000XthwMAC/005U0000000e6PoIAI]
        // Name [issued_at] Value [1331000888967]
        // Name [signature] Value [LOSzVZIF9dpKvPU07icIDOf8glCFeyd4vNGdj1dhW50]
        // Name [state] Value [/crazyrefresh.html]
        function parseHash(hash) {
            var i, nv, nvp, n, v;

            if (! $$.isNil(hash)) {
                if (hash.indexOf('#') === 0) {
                    hash = hash.substr(1);
                }
                nvp = hash.split("&");

                for (i = 0; i < nvp.length; i += 1) {
                    nv = nvp[i].split("=");
                    n = nv[0];
                    v = decodeURIComponent(nv[1]);
                    if ("access_token" === n) {
                        token(v);
                    }
                    else if ("instance_url" === n) {
                         instance(v);
                    }
                }
            }
        }

        function checkChildWindowStatus() {
            if (!childWindow || childWindow.closed) {
                refresh();
            }
        }

        function childWindowUnloadNotification(hash) {
            // Here we get notification from child window. Here we can decide if such notification is
            // raised because user closed child window, or because user is playing with F5 key.
            // NOTE: We can not trust on "onUnload" event of child window, because if user reload or refresh
            // such window in fact he is not closing child. (However "onUnload" event is raised!)
            //checkChildWindowStatus();
            parseHash(hash);
            setTimeout(window.Sfdc.canvas.oauth.checkChildWindowStatus, 50);
        }

        function logout() {
            // Remove the cookie and refresh the browser
            token(null);
            var home = $$.cookies.get("home");
            window.location = home || window.location;
        }

        function loggedin() {
            return !$$.isNil(token());
        }

        function loginUrl() {
            var i, nvs, nv, q = self.location.search;

            if (q) {
                q = q.substring(1);
                nvs = q.split("&");
                for (i = 0; i < nvs.length; i += 1)
                {
                    nv = nvs[i].split("=");
                    if ("loginUrl" === nv[0]) {
                        return decodeURIComponent(nv[1]) + "/services/oauth2/authorize";
                    }
                }
            }
            // Maybe throw exception here, otherwise default to something better
            return null;
        }

        return {
             login : login,
             logout : logout,
             loggedin : loggedin,
             loginUrl : loginUrl,
             token : token,
             instance : instance,
             checkChildWindowStatus : checkChildWindowStatus,
             childWindowUnloadNotification: childWindowUnloadNotification
         };
    }());

    $$.module('Sfdc.canvas.oauth', module);

}(Sfdc.canvas));
