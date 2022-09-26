var packId: number;
var packName: string;
var packVersion: string;

var currentPath;
var fileMeta: any;

$("#save").on("click", e=>{
    $(document).trigger("canvas.save")
})
$(document).on("canvas.save", e => {
    $("#save_status").text("Saving...");
    $.ajax(`/packs/data/${packId}/upload`, {
        method: "POST",
        dataType: "json",
        contentType: "application/json; charset=utf-8",
        data: {
            meta: fileMeta,
            img: canvas.getImage()
        }
    }).done((res) => {
        if (res.success) {
            $(document).trigger("canvas.saved", true);
        } else {
            $(document).trigger("canvas.saved", false);
            $(document).trigger("canvas.error", "Could not save asset: "+res.reason)
        }
    }).fail((res) => {
        $(document).trigger(`canvas.error", "Could not save asset: ${res.status} ${res.responseJSON?.reason || res.statusText}`)
    });
}).on("canvas.saved", (e, s: boolean) => {
    if (s) {
        $("#save_status").text("Saved")
    } else {
        $("#save_status").text("Unsaved")
    }
});

function getPath(elm: JQuery<HTMLElement>): string {
    var str = "";
    elm.closest(".folder").each((i,e)=>{
        str = str+e.getAttribute("name")+"/";
    })
    return str;
}

function onFileChange(e: JQuery.ClickEvent<HTMLElement, undefined, HTMLElement, HTMLElement>) {
    var name = e.currentTarget.getAttribute("name") || "";
    currentPath = name;
    $("file.selected").removeClass("selected");
    e.currentTarget.classList.add("selected");
    $.getJSON(`/packs/data/${packId}/get?path=${encodeURIComponent(name)}`, (json) => {
        if (json.data.meta) fileMeta = json.meta;
        else fileMeta = {};
        canvas.setImage(json.data.img);
    })
}

$(()=>{
    function genFolder(json: any, folder: JQuery<HTMLElement>, name: string) {
        for (let i of Object.keys(json)) {
            if (i != "files") {
                var label = $("<folder-label/>").attr("for", name+i).text(i);
                label.on("click", (e) => {
                    var t = $(e.currentTarget);
                    t.toggleClass("open").parent().children(`[name="${t.attr("for")}"]`).toggleClass("open")
                })
                folder.append(label)
                var inner = $("<folder/>").attr("name", name+i);
                genFolder(json[i], inner, name+i+"/");
                folder.append(inner)
            }
        }
        if (json["files"] instanceof Array) {
            for (let file of json["files"]) {
                var img = $("<img/>").attr("src",`/assets/${packVersion}/${name}${file}.png`).attr("loading","lazy");
                folder.append($("<file/>").attr("name", name+file).append(img).append($("<span/>").text(file)).on("click", onFileChange));
            }
        }
    }
    $.getJSON(`/assets/${packVersion}.json`, (tree) => {
        console.log(tree);
        genFolder(tree, $(".filetree"), "");
        $(".filetree").css("visibility", "visible")
    })
})