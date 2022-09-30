"use strict";
var packId;
var packName;
var packVersion;
var currentPath = "";
var fileMeta;
var saveStatus = true;
$("#save").on("click", e => {
    saveAsset();
});
function saveAsset(done) {
    if (currentPath == "" || canvas.historyPosition == -1) {
        if (done)
            done({});
        return;
    }
    $("#save_status").text("Saving...");
    $.ajax(`/packs/data/${packId}/upload?path=${encodeURIComponent(currentPath)}`, {
        method: "POST",
        dataType: "json",
        contentType: "application/json; charset=utf-8",
        data: JSON.stringify({
            meta: fileMeta,
            img: canvas.getImage()
        })
    }).done((res) => {
        if (res.success) {
            $(document).trigger("canvas.saved", true);
            if (done)
                done(res);
        }
        else {
            $(document).trigger("canvas.saved", false);
            $(document).trigger("canvas.error", "Could not save asset: " + res.reason);
        }
    }).fail((res) => {
        var _a;
        $(document).trigger("canvas.saved", false);
        $(document).trigger(`canvas.error", "Could not save asset: ${res.status} ${((_a = res.responseJSON) === null || _a === void 0 ? void 0 : _a.reason) || res.statusText}`);
    });
}
$(document).on("canvas.saved", (e, s) => {
    saveStatus = s;
    if (s) {
        $("#save_status").text("Saved");
    }
    else {
        $("#save_status").text("Unsaved");
    }
});
function getPath(elm) {
    var str = "";
    elm.closest(".folder").each((i, e) => {
        str = str + e.getAttribute("name") + "/";
    });
    return str;
}
function onFileChange(e) {
    saveAsset(() => {
        var name = e.currentTarget.getAttribute("name") || "";
        currentPath = name;
        $("file.selected").removeClass("selected");
        e.currentTarget.classList.add("selected");
        $.getJSON(`/packs/data/${packId}/get?path=${encodeURIComponent(name)}`, (json) => {
            if (json.data.meta)
                fileMeta = json.meta;
            else
                fileMeta = {};
            canvas.setImage(json.data.img);
        });
    });
}
$(() => {
    function genFolder(json, folder, name) {
        for (let i of Object.keys(json)) {
            if (i != "files") {
                var label = $("<folder-label/>").attr("for", name + i).text(i);
                label.on("click", (e) => {
                    var t = $(e.currentTarget);
                    t.toggleClass("open").parent().children(`[name="${t.attr("for")}"]`).toggleClass("open");
                });
                folder.append(label);
                var inner = $("<folder/>").attr("name", name + i);
                genFolder(json[i], inner, name + i + "/");
                folder.append(inner);
            }
        }
        if (json["files"] instanceof Array) {
            for (let file of json["files"]) {
                var img = $("<img/>").attr("src", `/assets/${packVersion}/${name}${file}.png`).attr("loading", "lazy");
                folder.append($("<file/>").attr("name", name + file).append(img).append($("<span/>").text(file)).on("click", onFileChange));
            }
        }
    }
    $.getJSON(`/assets/${packVersion}.json`, (tree) => {
        console.log(tree);
        genFolder(tree, $(".filetree"), "");
        $(".filetree").css("visibility", "visible");
    });
});
$(this).on("beforeunload", (e) => {
    if (!saveStatus) {
        e.preventDefault();
        e.returnValue = "If you leave this page, you may lose unsaved changes";
        return e.returnValue;
    }
});
function openModal(name) {
    $("#modal_bg").show();
    $(`[data-modal="${name}"]`).show();
}
$("#modal_bg, [data-modal-close]").on("click", e => {
    if (e.target == e.currentTarget) {
        $("#modal_bg").hide();
        $("[data-modal]").hide();
    }
});
