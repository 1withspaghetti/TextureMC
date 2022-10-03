"use strict";
setInterval(() => {
    $.get("/auth/heartbeat");
}, 900000);
