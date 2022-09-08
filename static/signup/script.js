function signup() {
    if (!/^[a-zA-Z0-9_]{3,16}$/.test(document.getElementById("username").value)) {
        notify("Invalid Username, it may only contain letters, numbers, underscores, and be 3 to 16 characters long");
        return;
    }
    if (!/^[a-zA-Z0-9_@#$%^&-+=()]{5,24}$/.test(document.getElementById("password").value)) {
        notify("Invalid Password, it may only contain letters, numbers, symbols, and be 5 to 24 characters long");
        return;
    }
    if (document.getElementById("password").value != document.getElementById("password2").value) {
        notify("Passwords must match");
        return;
    }
    if (!/^[a-zA-Z0-9_@#$%^&-+=()]{5,24}$/.test(document.getElementById("betakey").value)) {
        notify("Invalid Beta-Tester Key");
        return;
    }
    asyncPOST("/auth/register", {
        "username": document.getElementById("username").value,
        "password": document.getElementById("password").value,
        "betakey": document.getElementById("betakey").value
    }, false, (res) => {
        if (res.success == true) {
            session_token = res.token;
            startHeartbeat();
            location.href = '/account';
        } 
        else {
            notify(res.reason);
        }
    });
}

function notify(text) {
    document.getElementById("notification").innerText = "⚠ " + text
}