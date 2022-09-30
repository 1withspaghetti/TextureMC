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

function importPack(file: File, done: (pack: any) => void, fail?: (str: string) => void) {
    // @ts-ignore
    JSZip.loadAsync(file).then((zip: JSZip) => {
        var metaFile = zip.file("pack.mcmeta");
        if (!metaFile) {if (fail) fail("Could not read pack.mcmeta file"); return;}
        metaFile.async("text").then((str: string)=>{
            var meta = JSON.parse(str);
            console.log(meta)
            var format = meta.pack?.pack_format;
            if (!meta || !format) {if (fail) fail("Invalid pack.mcmeta file"); return;}

            var pack = {};
            var promises: Promise<any>[] = [];

            var textures = zip.folder("assets/minecraft/textures");
            if (textures == null) {if (fail) fail("Could not find textures folder"); return;}

            textures.forEach((rPath: string, obj: any) => {
                var name = rPath.substring(rPath.lastIndexOf('/')+1).replace('/','');
                if (name.endsWith(".png")) {
                    var promise = textures.file(rPath)?.async("base64").then((base64: string)=>{
                        setEmbedded(pack, rPath.replace(".png","/img"), base64)
                    });
                    if (promise) promises.push(promise)
                } else if (name.endsWith(".png.mcmeta")) {
                    var promise = textures.file(rPath)?.async("text").then((text: string)=>{
                        var meta = JSON.parse(text);
                        setEmbedded(pack, rPath.replace(".png.mcmeta","/meta"), meta)
                    });
                    if (promise) promises.push(promise);
                }
            })

            Promise.all(promises).then(()=>{
                done(pack);
            })
        });
    });
}

function setEmbedded(obj: any, pathStr: string, value: any): any {
    var path = pathStr.split("/");
    path.reduce((a, b, level) => {
        if (typeof a[b] === "undefined" && level !== path.length - 1){
            a[b] = {};
            return a[b];
        }
        if (level == path.length - 1){
            a[b] = value;
            return value;
        } 
        return a[b];
    }, obj);
}