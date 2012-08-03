// Sfdc.canvas.cookies
(function ($$) {

    "use strict";

    var module =  (function() {

       function set(name, value, days) {
           var expires = "", date;
           if (days) {
               date = new Date();
               date.setTime(date.getTime() + (days * 24 * 60 * 60 * 1000));
               expires = "; expires=" + date.toGMTString();
           }
           else {
               expires = "";
           }
           document.cookie = name + "=" + value + expires + "; path=/";
       }

       function get(name) {
           var nameEQ, ca, c, i;

           if ($$.isUndefined(name)) {
               return document.cookie.split(';');
           }

           nameEQ = name + "=";
           ca = document.cookie.split(';');
           for (i = 0; i < ca.length; i += 1) {
               c = ca[i];
               while (c.charAt(0) === ' ') {c = c.substring(1, c.length);}
               if (c.indexOf(nameEQ) === 0) {
                   return c.substring(nameEQ.length, c.length);
               }
           }
           return null;
       }

       function remove(name) {
           set(name, "", -1);
       }

       return {
            set : set,
            get : get,
            remove : remove
        };
    }());


    $$.module('Sfdc.canvas.cookies', module);

}(Sfdc.canvas));
