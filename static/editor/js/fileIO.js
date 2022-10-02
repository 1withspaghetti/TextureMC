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
function importPack(file, done, fail) {
    // @ts-ignore
    JSZip.loadAsync(file).then((zip) => {
        var metaFile = zip.file("pack.mcmeta");
        if (!metaFile) {
            if (fail)
                fail("Could not read pack.mcmeta file");
            return;
        }
        metaFile.async("text").then((str) => {
            var _a;
            var meta = JSON.parse(str);
            var format = (_a = meta.pack) === null || _a === void 0 ? void 0 : _a.pack_format;
            if (!meta || !format) {
                if (fail)
                    fail("Invalid pack.mcmeta file");
                return;
            }
            var name = file.name.replace(".zip", "").substring(0, 25);
            var pack = {};
            var promises = [];
            var textures = zip.folder("assets/minecraft/textures");
            if (textures == null) {
                if (fail)
                    fail("Could not find textures folder");
                return;
            }
            textures.forEach((rPath, obj) => {
                var _a, _b;
                var name = rPath.substring(rPath.lastIndexOf('/') + 1).replace('/', '');
                if (name.endsWith(".png")) {
                    var promise = (_a = textures.file(rPath)) === null || _a === void 0 ? void 0 : _a.async("base64").then((base64) => {
                        var p = rPath.replace(".png", "");
                        if (!pack[p])
                            pack[p] = {};
                        pack[p].img = base64;
                    });
                    if (promise)
                        promises.push(promise);
                }
                else if (name.endsWith(".png.mcmeta")) {
                    var promise = (_b = textures.file(rPath)) === null || _b === void 0 ? void 0 : _b.async("text").then((text) => {
                        var meta = JSON.parse(text);
                        var p = rPath.replace(".png.mcmeta", "");
                        if (!pack[p])
                            pack[p] = {};
                        pack[p].meta = meta;
                    });
                    if (promise)
                        promises.push(promise);
                }
            });
            Promise.all(promises).then(() => {
                done(name, format, pack);
            });
        });
    });
}
function setEmbedded(obj, pathStr, value) {
    var path = pathStr.split("/");
    path.reduce((a, b, level) => {
        if (typeof a[b] === "undefined" && level !== path.length - 1) {
            a[b] = {};
            return a[b];
        }
        if (level == path.length - 1) {
            a[b] = value;
            return value;
        }
        return a[b];
    }, obj);
}
