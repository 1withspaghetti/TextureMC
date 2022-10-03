var currentPackId: string = "";
var currentPackName: string = "";

$(document).on("click", e => {
    if ($(e.target).closest(".pack-settings").length == 0) {
        $("#pack_context_menu").attr("data-pack", "").offset({top: 0, left: 0}).hide();
        currentPackId = "";
    }
})
$("[open-modal]").on("click", e => {
    var modal = $(`[modal="${$(e.currentTarget).attr("open-modal")}"]`).show();
    modal.children(`input[name="id"]`).val(currentPackId);
    $(`[modal-pack-name]`).text(currentPackName);
    $("#modal-bg").show();
})
$("#modal-bg, [modal-close]").on("click", e => {
    if (e.target == e.currentTarget) {
        $("#modal-bg").hide();
        $("[modal]").hide();
    }
})
$("form").on("done", (e)=>{
    $("#modal-bg").hide();
    $("[modal]").hide();
})
$("form.refresh-packs-on-complete").on("done", (e)=>{
    refreshPacks();
})
$("#logout").on("click", e=> {
    $.ajax("/auth/logout").done(res=>{
        if (res.success) {
            document.cookie = "session_token=null;expires=Thu, 01 Jan 1970 00:00:01 GMT;path=/;";
            location.href = "/";
        }
    })
})

function refreshPacks() {
    $.ajax("/packs/list", {dataType: "json"}).done((res) => {
        $("#packs").html("");
        for (let pack of res.packs) {
            $("#packs").append(`<div class="pack" data-pack="${pack.id}" data-pack-name="${pack.name}">
                <div class="pack-row"><span class="pack-name">${pack.name}</span><img class="pack-settings" src="/account/imgs/set.svg"></div>
                <div class="pack-row">${pack.version}</div>
            </div>`);
        }
        addPackEventListeners();
        if (res.packs.length == 0) {
            $("#packs_empty").show();
        } else {
            $("#packs_empty").hide();
        }
    })
}

function addPackEventListeners() {
    $(".pack").on("click", e=>{
        if (!$(e.target).hasClass("pack-settings")) location.href = `/editor/${e.currentTarget.getAttribute("data-pack")}/`
    })
    $(".pack-settings").on("click", e => {
        let c = $(e.currentTarget);
        let p = c.position();
    
        let id = c.closest(".pack").attr("data-pack") || "";
    
        if (id != currentPackId) {
            $("#pack_context_menu").offset({top: p.top, left: p.left + (c.width() || 0)}).show();
            currentPackId = id;
            currentPackName = c.closest(".pack").attr("data-pack-name") || "";
        } else {
            $("#pack_context_menu").offset({top: 0, left: 0}).hide();
            currentPackId = "";
            currentPackName = "";
        }
    })
}
addPackEventListeners();

$("#export_pack").on("click", e=>{
    exportPack(currentPackId, currentPackName, (e)=>{alert("Error downloading pack: "+e)})
})
$("#import_pack").on("change", e=>{
    var files = ($("#import_pack")[0] as HTMLInputElement).files;
    if (!files || !files[0]) return;
    importPack(files[0], (name, format, pack)=>{
        $.ajax("/packs/upload", {
            method: "POST",
            data: JSON.stringify({
                name: name,
                format: format,
                data: pack
            }),
            contentType: "application/json; charset=utf-8",
            dataType: "json"
        }).done(res=>{
            if (res.success) {
                refreshPacks();
            } else {
                console.error(res.reason || "Unknown error while uploading pack")
            }
        }).fail(res=>{
            console.error(res.responseJSON?.reason || "Unknown Server Error: "+res.status);
        })
    }, msg=>{
        console.error(msg);
    })
})