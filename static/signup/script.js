$('cform input[type="submit"]').on("click", evt => {
    var form = $(evt.currentTarget).closest("cform");

    var req = {};

    for (let elm of form.children("input")) {
        var e = $(elm);
        var v = e.val();
        if (e.attr("regex")) {
            if (!new RegExp(e.attr("regex")).test(v)) {
                $(form.attr("messages") || "").text(e.attr("msg") || "Invalid Input").fadeIn(250).delay(3000).fadeOut(250);
                return;
            }
        }
        if (e.attr("matches")) {
            var match = form.children(`[name="${e.attr("matches")}"]`);
            if (v != match.val()) {
                $(form.attr("messages") || "").text(e.attr("msg") || "Input does not match").fadeIn(250).delay(3000).fadeOut(250);
                return;
            }
        }
        req[e.attr("name")] = v;
    }

    $.ajax(form.attr("action") || location.href, {
        method: "POST",
        data: JSON.stringify(req),
        contentType: "application/json; charset=utf-8",
        dataType: "json",
    }).done((re,h,res) => {
        if (re.success && form.attr("success")) location.href = form.attr("success");
    }).fail((res) => {
        $(form.attr("messages") || "").text(res.responseJSON?.reason || "Unknown Server Error: "+res.statusText).fadeIn(250).delay(3000).fadeOut(250);
    })
    console.log("Set up form")
})