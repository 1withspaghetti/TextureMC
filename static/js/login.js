"use strict";
var _a;
var session_token;
{
    var token = ((_a = document.cookie.match('(^|;)\\s*session_token\\s*=\\s*([^;]+)')) === null || _a === void 0 ? void 0 : _a.pop()) || null;
    if (token != null) {
        console.log("Existing session token: " + token);
        session_token = token;
    }
    else {
        console.log("No existing session token");
        session_token = null;
    }
}
