var session_token;

{
    var token = document.cookie.match('(^|;)\\s*session_token\\s*=\\s*([^;]+)')?.pop() || null;
    if (token != null) {
        console.log("Existing session token: "+token)
        session_token = token;
    } else {
        console.log("No existing session token")
        session_token = null;
    }
}
