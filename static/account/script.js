"use strict";
var currentPackId = "";
$(document).on("click", e => {
    if ($(e.target).closest("[data-pack].pack-settings").length == 0) {
        $("#pack_context_menu").attr("data-pack", "").offset({ top: 0, left: 0 }).hide();
    }
});
$("[open-modal]").on("click", e => {
    $(`[modal="${$(e.currentTarget).attr("open-modal")}"]`).show();
    $("#modal-bg").show();
});
$("#modal-bg, [modal-close]").on("click", e => {
    if (e.target == e.currentTarget) {
        $("#modal-bg").hide();
        $("[modal]").hide();
    }
});
$("[data-pack].pack-settings").on("click", e => {
    let c = $(e.currentTarget);
    let p = c.position();
    if (c.attr("data-pack") != currentPackId) {
        $("#pack_context_menu").offset({ top: p.top, left: p.left + (c.width() || 0) }).show();
        currentPackId = c.attr("data-pack") || "";
    }
    else {
        $("#pack_context_menu").offset({ top: 0, left: 0 }).hide();
        currentPackId = "";
    }
});
$(this).on("load-packs", () => {
    $.ajax("/packs/list", { dataType: "json" }).done((res) => {
        for (let pack of res.packs) {
            $("#packs").append(`<div data-pack="${pack.id}"><v>"+pack.name+"<img src="images/edit.svg" onclick="confirmChangePackname(event)"></v><v>"+pack.version+"<img src="images/trash.svg" onclick="confirmDelete(event)"><img src="images/export.svg\" onclick="confirmExport(event)"><img src="images/copy.svg" onclick="confirmCopy(event)"></v></div>`);
        }
        if (res.packs.length == 0) {
            $("#packs_empty").show();
        }
        else {
            $("#packs_empty").hide();
        }
    });
});
