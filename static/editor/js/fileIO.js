"use strict";
function exportPack(id, name, fail) {
    $.ajax(`/packs/data/${id}/full`, {
        method: "GET",
        dataType: "json",
    }).done(res => {
        if (res.success) {
            // @ts-ignore
            var zip = new JSZip();
            zip.file("pack.mcmeta", JSON.stringify({
                pack: {
                    pack_format: res.format,
                    description: `§e${name}\n§r§3§b${res.version}§r§a - Made with TextureMC.com`
                }
            }, undefined, 2));
            var folder = (path, obj) => {
                if (obj.img) {
                    zip.file(path + ".png", obj.img, { base64: true });
                    if (obj.meta)
                        zip.file(path + ".png.mcmeta", JSON.stringify(obj.meta));
                }
                else {
                    for (let name of Object.keys(obj)) {
                        folder(path + "/" + name, obj[name]);
                    }
                }
            };
            folder("assets/minecraft/textures", res.data);
            zip.generateAsync({ type: "blob" })
                .then(function (blob) {
                // @ts-ignore
                saveAs(blob, name + ".zip");
            });
        }
        else {
            if (fail)
                fail(res.reason);
        }
    }).fail(res => {
        if (res.responseJSON.reason && fail)
            fail(res.responseJSON.reason);
        else if (fail)
            fail(`Unknown Error ${res.status}: ${res.statusText}`);
    });
}
function importPack(name, file) {
}
