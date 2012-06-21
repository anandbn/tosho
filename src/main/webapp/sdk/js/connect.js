(function (global) {

    "use strict";

    if (global.connect) {
        return;
    }

    // cached references
    //------------------

    var oproto = Object.prototype,
        aproto = Array.prototype,
        doc = document,

        // $ functions
        // The connect global object is made available in the global scope.  The reveal to the global scope is done later.
        $ = {

            // type utilities
            //---------------

            hasOwn: function (obj, prop) {
                return oproto.hasOwnProperty.call(obj, prop);
            },

            isUndefined: function (value) {
                var undef;
                return value === undef;
            },

            isNil: function (value) {
                return $.isUndefined(value) || value === null || value === "";
            },

            isNumber: function (value) {
                return !!(value === 0 || (value && value.toExponential && value.toFixed));
            },

            isFunction: function (value) {
                return !!(value && value.constructor && value.call && value.apply);
            },

            isArray: Array.isArray || function (value) {
                return oproto.toString.call(value) === '[object Array]';
            },

            isArguments: function (value) {
                // @todo does this still work in strict mode?
                return !!(value && $.hasOwn(value, 'callee'));
            },

            isObject: function (value) {
                return value !== null && typeof value === 'object';
            },

            // common functions
            //-----------------

            nop: function () {
                /* no-op */
            },

            invoker: function (fn) {
                if ($.isFunction(fn)) {
                    fn();
                }
            },

            identity: function (obj) {
                return obj;
            },

            // @todo consider additional tests for: null, boolean, string, nan, element, regexp... as needed

            each: function (obj, it, ctx) {
                if ($.isNil(obj)) {
                    return;
                }
                var nativ = aproto.forEach, i = 0, l, key;
                l = obj.length;
                ctx = ctx || obj;
                // @todo: looks like native method will not break on return false; maybe throw breaker {}
                if (nativ && nativ === obj.forEach) {
                    obj.forEach(it, ctx);  // obj is an Array with its own "forEach" function
                }
                // @todo should we check $.isArray(obj) too?  A Snake object with length=3 will pass this test:
                else if ($.isNumber(l)) { // obj is an array-like object
                    while (i < l) {
                        if (it.call(ctx, obj[i], i, obj) === false) {
                            return;
                        }
                        i += 1;
                    }
                }
                else {  // obj is not array-like, it is a map of properties
                    for (key in obj) {
                        if ($.hasOwn(obj, key) && it.call(ctx, obj[key], key, obj) === false) {
                            return;
                        }
                    }
                }
            },

            map: function (obj, it, ctx) {
                var results = [], nativ = aproto.map;
                if ($.isNil(obj)) {
                    return results;
                }
                if (nativ && obj.map === nativ) {
                    return obj.map(it, ctx);
                }
                ctx = ctx || obj;
                $.each(obj, function (value, i, list) {
                    results.push(it.call(ctx, value, i, list));
                });
                return results;
            },

            values: function (obj) {
                return $.map(obj, $.identity);
            },

            slice: function (array, begin, end) {
                /* FF doesn't like undefined args for slice so ensure we call with args */
                return aproto.slice.call(array, $.isUndefined(begin) ? 0 : begin, $.isUndefined(end) ? array.length : end);
            },

            toArray: function (iterable) {
                if (!iterable) {
                    return [];
                }
                if (iterable.toArray) {
                    return iterable.toArray;
                }
                if ($.isArray(iterable)) {
                    return iterable;
                }
                if ($.isArguments(iterable)) {
                    return $.slice(iterable);
                }
                return $.values(iterable);
            },

            size: function (obj) {
                return $.toArray(obj).length;
            },

            indexOf: function (array, item) {
                var nativ = aproto.indexOf, i, l;
                if (!array) {
                    return -1;
                }
                if (nativ && array.indexOf === nativ) {
                    return array.indexOf(item);
                }
                for (i = 0, l = array.length; i < l; i += 1) {
                    if (array[i] === item) {
                        return i;
                    }
                }
                return -1;
            },

            remove: function (array, item) {
                var i = $.indexOf(array, item);
                if (i >= 0) {
                    array.splice(i, 1);
                }
            },

            param: function (a, encode) {
                var s = [];

                encode = encode || false;

                function add( key, value ) {

                    if ($.isNil(value)) {return};
                    value = $.isFunction(value) ? value() : value;
                    if ($.isArray(value)) {
                        $.each( value, function(v, n) {
                            add( key, v );
                        });
                    }
                    else {
                        if (encode) {
                            s[ s.length ] = encodeURIComponent(key) + "=" + encodeURIComponent(value);
                        }
                        else {
                            s[ s.length ] = key + "=" + value;
                        }
                    }
                }

                if ( $.isArray(a)) {
                    $.each( a, function(v, n) {
                        add( n, v );
                    });
                } else {
                    for ( var p in a ) {
                        if ($.hasOwn(a, p)) {
                            add( p, a[p]);
                        }
                    }
                }
                return s.join("&").replace(/%20/g, "+");
            },


            // strings
            //--------

            trim: function (str) {
                return str.replace(/^\s+|\s+$/g, '');
            },

            classify: function (str) {
                return str.replace(/^([a-z])|(?:[\s_\-]+([a-z]))/g, function ($0, $1, $2) {
                    return ($1 || $2).toUpperCase();
                });
            },

            extend: function (dest /*, mixin1, mixin2, ... */) {
                $.each($.slice(arguments, 1), function (mixin, i) {  // add each arg after dest to dest as a mixin
                    $.each(mixin, function (value, key) {  // for each property in this mixin object, add its key/value to the dest
                        dest[key] = value;
                    });
                });
                return dest;
            },

            beget: function (sire, mixin) {
                var progeny;
                // @todo Object.create doesn't seem to be working in jsc like i'd expect; debug this
                /* if (Object.create) {
                 progeny = Object.create(sire, mixin);
                 }
                 else {
                 */
                function F() {
                }

                F.prototype = sire;
                progeny = new F();
                progeny.constructor = F;

                $.extend(progeny, mixin);
                /* } */
                return progeny;
            },

            prototypeOf: function (obj) {
                // @todo: What about using only obj.constructor.prototype?  (restriction would be that prototypeOf should only be used on nos2 "beget" objects?)
                //        Native getPrototypeOf might be fast enough that it is worth keeping it?

                var nativ = Object.getPrototypeOf,
                    proto = '__proto__';
                if ($.isFunction(nativ)) {
                    return nativ.call(Object, obj);
                }
                else {
                    if (typeof {}[proto] === 'object') {
                        return obj[proto];
                    }
                    else {
                        return obj.constructor.prototype;
                    }
                }
            },

            module: function(ns, decl) {
                var parts = ns.split('.'), parent = global.connect, i, length;

                // strip redundant leading global
                if (parts[0] === 'connect') {
                    parts = parts.slice(1);
                }

                length = parts.length;
                for (i = 0; i < length; i += 1) {
                    // create a property if it doesn't exist
                    if ($.isUndefined(parent[parts[i]])) {
                        parent[parts[i]] = {};
                    }
                    parent = parent[parts[i]];
                }

                if ($.isFunction(decl)) {
                    decl = decl();
                }
                return $.extend(parent, decl);
            },

            // dom
            //----

            byId: function (id) {
                return doc.getElementById(id);
            },
            byClass: function (clazz) {
                return doc.getElementsByClassName(clazz);
            },
            attr : function(el, name) {
                var a = el.attributes, i;
                for (i = 0; i < a.length; i += 1) {
                    if (name === a[i].name) {
                        return a[i].value;
                    }
                }
           },

            browser: {
                msie: /*@cc_on!@*/false
            }

        },   // End of $

        readyHandlers = [],

        ready = function () {
            ready = $.nop; // only execute once
            $.each(readyHandlers, $.invoker);
            readyHandlers = null; // release to gc
        },

        connect = function (cb) {
            if ($.isFunction(cb)) {
                readyHandlers.push(cb);
            }
//            else if (selector.indexOf('#') === 0) {  // naive selector support; embed a real one if requirements here get complex
//                return $.byId(selector.slice(1));
//            }
        };


    // on dom ready, derived from <http://dean.edwards.name/weblog/2006/06/again/#comment367184/>

    // @todo unknown if this works in IE9
    (function () {
        var ael = 'addEventListener',
            tryReady = function () {
                if ($.browser.msie) {
                    try {
                        doc.body.doScroll('up'); // relying on IE to throw an error here if not ready
                        ready();
                    } catch(e) {
                    }
                }
                else if (/loaded|complete/.test(doc.readyState)) {
                    ready();
                }
                else if (readyHandlers) {
                    setTimeout(tryReady, 30);
                }
            };

        if (doc[ael]) {
            doc[ael]('DOMContentLoaded', ready, false);
        }

        tryReady();

        if (global[ael]) {
            global[ael]('load', ready, false);
        }
        else if (global.attachEvent) {
            global.attachEvent('onload', ready);
        }
    }());

    $.each($, function (fn, name) {
        connect[name] = fn;
    });

    global.connect = connect;


}(this));



