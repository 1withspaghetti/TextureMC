function login() {
    if (!/^[a-zA-Z0-9_]{3,16}$/.test(document.getElementById("username").value)) {
        notify("Invalid Username");
        return;
    }
    if (!/^[a-zA-Z0-9_@#$%^&-+=()]{5,24}$/.test(document.getElementById("password").value)) {
        notify("Invalid Password");
        return;
    }
    asyncPOST("/auth/login", {
        "username": document.getElementById("username").value,
        "password": document.getElementById("password").value
    }, false, (res) => {
        if (res.success == true) {
            session_token = res.token;
            startHeartbeat();
            location.href = '/account';
        } 
        else {
            notify(res.reason);
        }
    })
}

function notify(text) {
    document.getElementById("notification").innerText = "⚠ " + text
}