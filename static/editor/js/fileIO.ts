import JSZip from "jszip";

function exportPack(id: string, name: string, fail?: (str: string) => void): void {
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
            
            var folder = (path: string, obj: any) => {
                if (obj.img) {
                    zip.file(path+".png", obj.img, {base64: true})
                    if (obj.meta)
                        zip.file(path+".png.mcmeta", JSON.stringify(obj.meta))
                } else {
                    for (let name of Object.keys(obj)) {
                        folder(path+"/"+name, obj[name])
                    }
                }
            };
            folder("assets/minecraft/textures",res.data);
            zip.generateAsync({type:"blob"})
                .then(function (blob: any) {
                    // @ts-ignore
                    saveAs(blob, name+".zip");
            });

        } else {
            if (fail) fail(res.reason)
        }
    }).fail(res => {
        if (res.responseJSON.reason && fail) fail(res.responseJSON.reason)
        else if (fail) fail(`Unknown Error ${res.status}: ${res.statusText}`)
    })
}

function importPack(name: string, file: File, fail?: (str: string) => void): any {
    // @ts-ignore
    JSZip.loadAsync(file).then((zip: JSZip) => {
        var metaFile = zip.file("pack.mcmeta");
        if (!metaFile) {if (fail) fail("Could not read pack.mcmeta file"); return;}
        metaFile.async("text").then(str=>{
            var meta = JSON.parse(str);
            var format = meta?.pack?.pack_format;
            var name = file.name.replace(".zip","");
            if (meta || format) {if (fail) fail("Invalid pack.mcmeta file"); return;}

            var textures = zip.folder("assets/minecraft/textures");
            var promises: Promise<void>[] = [];
            var folder = (path: string, obj: JSZip.JSZipObject) => {
                if (!obj.dir) {
                    
                } else {

                }
            }
        });
    });
}