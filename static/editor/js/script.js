"use strict";
var packId;
var packName;
var packVersion;
var assetList = [];
var currentPath = "";
var fileMeta;
var saveStatus = true;
$("#save").on("click", e => {
    saveAsset();
});
function saveAsset(done) {
    if (saveStatus || currentPath == "") {
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
            canvas.savedHistoryPosition = canvas.historyPosition;
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
        $("#texture_search")[0].blur();
        var name = e.currentTarget.getAttribute("name") || "";
        currentPath = name;
        $("file.selected").removeClass("selected");
        $(`file[name="${name}"]`).addClass("selected").parents("folder").each((i, e) => {
            var id = $(e).addClass("open").attr("name");
            $(`folder-label[for="${id}"]`).addClass("open");
        });
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
                assetList.push(name + file);
            }
        }
    }
    $.getJSON(`/assets/${packVersion}.json`, (tree) => {
        genFolder(tree, $("#filetree"), "");
        $("#filetree").css("visibility", "visible");
    });
});
$(this).on("beforeunload", (e) => {
    if (!saveStatus && currentPath != "") {
        e.preventDefault();
        e.returnValue = "If you leave this page, you may lose unsaved changes";
        return e.returnValue;
    }
});
function openModal(name) {
    $("#modal_bg").show();
    $(`[data-modal="${name}"]`).show();
}
function closeModal() {
    $("#modal_bg").hide();
    $("[data-modal]").hide();
}
$("#modal_bg, [data-modal-close]").on("click", e => {
    if (e.target == e.currentTarget) {
        $("#modal_bg").hide();
        $("[data-modal]").hide();
    }
});
$("#texture_search").on("input", e => {
    var term = $("#texture_search").val();
    if (!term || term == "") {
        $("#filetree").show();
        $("#searchtree").hide().html("");
    }
    else {
        $("#filetree").hide();
        $("#searchtree").show().html("");
        // @ts-ignore
        var result = stringSimilarity.findBestMatch(term, assetList);
        result.ratings.sort((a, b) => { return b.rating - a.rating; });
        for (let item of result.ratings.splice(0, 25)) {
            var name = item.target.substring(item.target.lastIndexOf("/") + 1);
            var img = $("<img/>").attr("src", `/assets/${packVersion}/${item.target}.png`).attr("loading", "lazy");
            $("#searchtree").append($("<file/>").attr("name", item.target).append(img).append($("<span/>").text(name)).on("click", onFileChange));
        }
    }
});
