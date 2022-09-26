$('form').on("submit", (e)=>{
    e.preventDefault()
    e.stopPropagation()
    var form = $(e.currentTarget);

    var messages = $(form.attr("messages") || "").add(form.children("[messages]"));

    var req = {};

    for (let elm of form[0]) {
        if (elm.type != "submit") {
            var e = $(elm);
            var v = e.val();
            if (e.attr("regex")) {
                if (!new RegExp(e.attr("regex")).test(v)) {
                    messages.text(e.attr("msg") || "Invalid Input").fadeIn(250).delay(3000).fadeOut(250);
                    return;
                }
            }
            if (e.attr("matches")) {
                var match = form.children(`[name="${e.attr("matches")}"]`);
                if (v != match.val()) {
                    messages.text(e.attr("msg") || "Input does not match").fadeIn(250).delay(3000).fadeOut(250);
                    return;
                }
            }
            req[e.attr("name")] = v;
        }
    }

    $.ajax(form.attr("action") || location.href, {
        method: "POST",
        data: JSON.stringify(req),
        contentType: "application/json; charset=utf-8",
        dataType: "json",
    }).done((res,t,req) => {
        if (res.success) {
            if (form.attr("success")) location.href = form.attr("success");
            form.trigger("done", res)
        } else {
            messages.text(res.reason || "Unknown Error").fadeIn(250).delay(3000).fadeOut(250);
        }
    }).fail((res,t,req) => {
        messages.text(res.responseJSON?.reason || "Unknown Server Error: "+res.status).fadeIn(250).delay(3000).fadeOut(250);
    });
})