"use strict";
var currentPackId = "";
var currentPackName = "";
$(document).on("click", e => {
    if ($(e.target).closest(".pack-settings").length == 0) {
        $("#pack_context_menu").attr("data-pack", "").offset({ top: 0, left: 0 }).hide();
    }
});
$("[open-modal]").on("click", e => {
    var modal = $(`[modal="${$(e.currentTarget).attr("open-modal")}"]`).show();
    modal.children(`input[name="id"]`).val(currentPackId);
    modal.children(`[modal-pack-name]`).text(currentPackName);
    $("#modal-bg").show();
});
$("#modal-bg, [modal-close]").on("click", e => {
    if (e.target == e.currentTarget) {
        $("#modal-bg").hide();
        $("[modal]").hide();
    }
});
$(".pack-settings").on("click", e => {
    let c = $(e.currentTarget);
    let p = c.position();
    let id = c.closest(".pack").attr("data-pack") || "";
    if (id != currentPackId) {
        $("#pack_context_menu").offset({ top: p.top, left: p.left + (c.width() || 0) }).show();
        currentPackId = id;
        currentPackName = c.closest(".pack").attr("data-pack-name") || "";
    }
    else {
        $("#pack_context_menu").offset({ top: 0, left: 0 }).hide();
        currentPackId = "";
        currentPackName = "";
    }
});
$("#logout").on("click", e => {
    document.cookie = "session_token=null;expires=Thu, 01 Jan 1970 00:00:01 GMT;path=/;";
    location.href = "/";
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
