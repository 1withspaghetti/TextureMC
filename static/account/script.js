const packFormat = {
    "1": "1.8.9",
    "2": "1.10.2",
    "3": "1.12.2",
    "4": "1.14.4",
    "5": "1.15.2",
    "6": "1.16.5",
    "7": "1.17.1",
    "8": "1.18.1"
}




async function loadPacks() {
    asyncGET("/packs/get", true, (res) => {
        if (res.success != true) {
            console.log("Error getting packs: "+res.reason);
        }
        document.getElementById("packs").innerHTML = "";
        for (pack of res.packs) {
            document.getElementById("packs").innerHTML += 
            "<div id=\""+pack.id+"\" onclick=\"openPack(event);\"><v>"+pack.name+"<img src=\"images/edit.svg\" onclick=\"confirmChangePackname(event)\"></v><v>"+pack.version+"<img src=\"images/trash.svg\" onclick=\"confirmDelete(event)\"><img src=\"images/export.svg\" onclick=\"confirmExport(event)\"><img src=\"images/copy.svg\" onclick=\"confirmCopy(event)\"></v></div>";
        }
        if (res.packs.length >= 5)
            document.getElementById("newpack").style.display = "none";
    }, (res) => {
        console.log("Error getting packs: "+res.reason);
    })
}

async function loadUser() {
    res = awaitGET("/auth/user", true);
    if (res.success != true) {
        console.log("Error getting packs");
    } else {
        document.getElementById("username").innerText = res.username;
    }
}

function openPack(event) {
    var obj = event.target;
    if (obj.tagName == "IMG") return;
    if (obj.tagName != "DIV") obj = obj.parentElement;
    if (obj.tagName != "DIV") obj = obj.parentElement;
    location.href = "/editor/?pack="+obj.id;
}

function logout() {
    document.cookie = "session_token=null;expires=Thu, 01 Jan 1970 00:00:01 GMT;path=/;";
    window.location = "/";
}

function openPopup(name) {
    closeAll();
    document.getElementById(name).style.display="block";
    document.getElementById("popupbg").style.display="block";
}

function closePopup(name) {
    document.getElementById(name).style.display="none";
    document.getElementById("popupbg").style.display="none";
}

function closeAll() {
    for (let e of document.getElementById("popupbg").children) {
      e.style.display="none";
    }
    document.getElementById("popupbg").style.display="none";
  }

function createPack() {
    var name = document.getElementById("packName");
    if (name.value.length < 1 || name.value.length > 24) {
        document.getElementById("createPackNotification").innerText = "Pack name must be within 1-24 charecters"
        return;
    }
    document.getElementById("createPackNotification").innerText = "";

    asyncPOST("/packs/new", {
        "name": name.value,
        "version": document.getElementById("packVersion").value
    }, true, (res) => {
        if (res.success == true) {
            closePopup("createpack");
            document.getElementById("createPackNotification").innerText = "";
            loadPacks();
        } else {
            document.getElementById("createPackNotification").innerText = res.reason;
        }
    });
    
}

function confirmDelete(event) {
    var obj = event.target;
    document.getElementById("confirmDeleteId").innerText = obj.parentElement.parentElement.id;
    document.getElementById("confirmDeleteName").innerText = obj.parentElement.parentElement.children[0].firstChild.innerText
    openPopup("confirmDelete");
}

function deletePack() {
    var id = document.getElementById("confirmDeleteId").innerText;
    asyncGET("/packs/delete"+"?pack="+id, true, (res) => {
        if (res.success == true) {
            closePopup("confirmDelete");
            loadPacks();
        } else
            console.log("Failed to delete pack: "+res.reason);
    });
}

function confirmChangeUsername() {
    document.getElementById("changeUsernameNotification").innerText = "";
    document.getElementById("newUsername").value = "";
    openPopup("changeUsernamePopup");
}

function changeUsername() {
    var input = document.getElementById("newUsername").value;
    if (!/^[a-zA-Z0-9_]{3,16}$/.test(input)) {
        document.getElementById("changeUsernameNotification").innerText = "Invalid Username";
        return;
    }
    asyncGET("/auth/rename"+"?name="+input, true, (res) => {
        if (res.success == true) {
            loadUser();
            closePopup("changeUsernamePopup");
        } else {
            document.getElementById("changeUsernameNotification").innerText = res.reason;
        }
    });
}

function confirmChangePassword() {
    document.getElementById("changePasswordNotification").innerText = "";
    document.getElementById("oldPassword").value = "";
    document.getElementById("newPassword").value = "";
    openPopup("changePasswordPopup");
}

function changePassword() {
    var oldPass = document.getElementById("oldPassword").value;
    var newPass = document.getElementById("newPassword").value;
    var notif = document.getElementById("changePasswordNotification");
    if (!/^[a-zA-Z0-9_@#$%^&-+=()]{5,24}$/.test(oldPass)) {
        notif.innerText = "Invalid Password";
        return;
    }
    if (!/^[a-zA-Z0-9_@#$%^&-+=()]{5,24}$/.test(newPass)) {
        notif.innerText = "Invalid Password";
        return;
    }
    asyncPOST("/auth/changepass", {
        "oldPass": oldPass,
        "newPass": newPass
    }, true, (res) => {
        if (res.success == true) {
            closePopup("changePasswordPopup");
        } else {
            notif.innerText = res.reason;
        }
    });
}

function confirmChangePackname(event) {
    var obj = event.target;
    document.getElementById("changePacknameNotification").innerText = "";
    document.getElementById("newPackname").value = "";
    document.getElementById("confirmPacknameId").innerText = obj.parentElement.parentElement.id;
    openPopup("changePacknamePopup");
}

function changePackname() {
    var input = document.getElementById("newPackname").value;
    var id = document.getElementById("confirmPacknameId").innerText;
    if (input.length < 1 || input.length > 24) {
        document.getElementById("changePacknameNotification").innerText = "Invalid Pack name";
        return;
    }
    asyncPOST("/packs/rename"+"?pack="+id, {
        "name": input
    }, true, (res) => {
        if (res.success == true) {
            loadPacks()
            closePopup("changePacknamePopup");
        } else {
            document.getElementById("changePacknameNotification").innerText = res.reason;
        }
    })
}

function confirmCopy(event) {
    var obj = event.target;
    document.getElementById("confirmCopyId").innerText = obj.parentElement.parentElement.id;
    document.getElementById("confirmCopyName").innerText = obj.parentElement.parentElement.children[0].firstChild.innerText
    document.getElementById("confirmCopyNotification").innerText = "";
    openPopup("confirmCopy");
}

async function copyPack() {
        var id = document.getElementById("confirmCopyId").innerText;
        asyncGET("/packs/duplicate"+"?pack="+id, true, (res) => {
            if (res.success == true) {
                loadPacks();
                closePopup("confirmExport");
            } else
                document.getElementById("confirmExportCopy").innerText = res.reason;
        });
}

function confirmExport(event) {
    var obj = event.target;
    document.getElementById("confirmExportId").innerText = obj.parentElement.parentElement.id;
    document.getElementById("confirmExportName").innerText = obj.parentElement.parentElement.children[0].firstChild.innerText
    document.getElementById("confirmExportNotification").innerText = "";
    openPopup("confirmExport");
}

async function exportPack() {
        var id = document.getElementById("confirmExportId").innerText;

        var res = awaitGET("/packs/full"+"?pack="+id, true);
        if (res.success != true) {
            document.getElementById("confirmExportNotification").innerText = res.reason;
            return;
        }

        var zip = new JSZip();
        zip.file("pack.mcmeta", JSON.stringify({
            pack: {
                pack_format: res.format,
                description: res.name+"\nMade with TextureMC.com"
            }
        }));

        var textures = zip.folder("assets").folder("minecraft").folder("textures");
        for (let key of Object.keys(res.pack))
            await exportPackFolder(textures.folder(key), res.pack[key]);
        zip.generateAsync({type:"blob"})
            .then(function (blob) {
                saveAs(blob, res.name+".zip");
                closePopup("confirmExport");
        });
}

function exportPackFolder(folder, data) {
    var promises = [];
    for (const [key, value] of Object.entries(data)) {
        const piskel = value.piskel;
        promises.push(new Promise((resolve, reject) => {
            if (piskel.layers[0].frameCount > 1) {
                var canvas = document.createElement("canvas");
                var ctx = canvas.getContext("2d");
                var canvas2 = document.createElement("canvas");
                canvas2.width = piskel.width;
                canvas2.height = piskel.height * piskel.layers[0].frameCount;
                var ctx2 = canvas2.getContext("2d");
    
                let image = new Image();
                image.onload = function() {
                    ctx.drawImage(image, 0, 0);
                    for (let i = 0; i < piskel.layers[0].frameCount; i++) {
                        let imgData = ctx.getImageData(i * piskel.width, 0, piskel.width, piskel.height);
                        ctx2.putImageData(imgData, 0, i * piskel.height);
                    }

                    var meta = {"animation": {"frametime": piskel.fps}};
                    if (value.meta) {
                        meta.texture = {};
                        meta.texture.blur = value.meta.blur || false;
                        meta.texture.clamp = value.meta.clamp || false;
                        meta.animation.interpolate = value.meta.interpolate || false;
                    }
                    folder.file(key.replaceAll("~","/")+".png", canvas2.toDataURL("image/png").replace("data:image/png;base64,",""), {base64: true});
                    folder.file(key.replaceAll("~","/")+".png.mcmeta", JSON.stringify(meta));
                    resolve();
                }
                exportPackFolderBase64Layers(piskel).then((value) => {
                    console.log("Promise: "+value);
                    image.src = value;
                })
                
            } else {
                if (value.meta) {
                    meta = {"texture": {}};
                    meta.texture.blur = value.meta.blur || false;
                    meta.texture.clamp = value.meta.clamp || false;
                    folder.file(key.replaceAll("~","/")+".png.mcmeta", JSON.stringify(meta));
                }
                exportPackFolderBase64Layers(piskel).then((value) => {
                    console.log("Promise: "+value);
                    folder.file(key.replaceAll("~","/")+".png", value.replace("data:image/png;base64,",""), {base64: true});
                    resolve();
                })
                
            }
        }));
    }
    return Promise.allSettled(promises);
}

function exportPackFolderBase64Layers(piskelJson) {
    if (piskelJson.layers.length == 1) {
        return new Promise((resolve, reject) => {
            console.log("Rendered layer: "+piskelJson.layers[0].chunks[0].base64PNG);
            resolve(piskelJson.layers[0].chunks[0].base64PNG);
        });
        
    } else {
        var canvas = document.createElement("canvas");
        /*canvas.width = piskelJson.width;
        canvas.height = piskelJson.height;*/
        var ctx = canvas.getContext("2d");

        var promises = [];
        for (let layer of piskelJson.layers) {
            promises.push(new Promise((resolve, reject) => {
                let image = new Image();
                image.onload = () => {
                    ctx.drawImage(image, 0, 0);
                    resolve();
                };
                image.onerror = reject
                image.src = layer.chunks[0].base64PNG;
            }));
        }
        return Promise.allSettled(promises).then((resolve, reject) => {
            console.log("Rendered multi-layer: "+canvas.toDataURL("image/png"));
            return canvas.toDataURL("image/png");
        });
        
    }
}

var packFile = null;
var packName = null;

function confirmImportPack() {
    packFile = null;
    packName = null;
    const files = document.getElementById("importPackFile").files;
    if (files.length == 0) return;
    document.getElementById("importPackName").innerText = "";
    document.getElementById("importPackDesc").innerText = "Could not read pack description";
    document.getElementById("importPackVersion").innerText = "";
    document.getElementById("confirmImportNotification").innerText = "";
    JSZip.loadAsync(files[0]).then((zip) => {
        var file = zip.file("pack.mcmeta");
        if (file == undefined)
            document.getElementById("confirmImportNotification").innerText = "Invalid Pack File";
        else {
            file.async("text").then(function (text) {
                if (!text) document.getElementById("confirmImportNotification").innerText = "Invalid Pack File";
                else {
                    try {
                        document.getElementById("importPackName").innerText = files[0].name;
                        var mcmeta = JSON.parse(text);
                        document.getElementById("importPackDesc").innerText = parseDesc(mcmeta.pack.description);
                        document.getElementById("importPackVersion").innerText = "Version: "+packFormat[mcmeta.pack.pack_format.toString()];
                        packFile = zip;
                        packName = files[0].name.replace(".zip","");
                    } catch (e) {
                        console.error(e);
                        document.getElementById("confirmImportNotification").innerText = "Invalid Pack File";
                    }
                }
            });
        }
    });
    openPopup("confirmImport");
}

async function importPack() {
    if (packFile == null || packName == null) {
        document.getElementById("confirmImportNotification").innerText = "Unable to import invalid pack!";
    }

    const mcmeta = JSON.parse(await packFile.file("pack.mcmeta").async("text"));
    const version = packFormat[mcmeta.pack.pack_format.toString()];

    trueAssets = awaitGET("https://cdn.texturemc.com/versions/"+version+".json");

    var imp = {
        "name": (packName.length > 24 ? packName.substring(0,24) : packName),
        "version": version,
        "data": {}
    };

    const textures = packFile.folder("assets/minecraft/textures");
    var promises = [];
    textures.forEach(function(path, file) {
        try {
            if (path.endsWith(".png")) {
                var type = path.substring(0, path.indexOf("/"))
                var asset = path.substring(path.indexOf("/")+1).replaceAll("/","~").replace(".png","");
                if (trueAssets[type].includes(asset)) {
                    if (imp.data[type] == undefined) imp.data[type] = {};
                    promises.push(
                        new Promise((resolve) => {
                            importImage(asset, file, textures.file(path+".mcmeta"), function(piskel) {imp.data[type][asset] = piskel;resolve()})
                        })
                    );
                }
            }
        } catch (e) {
            console.error("Could not parse `"+path+"`: "+e);
        }
    });
    Promise.allSettled(promises).then(() => {
        if (Object.keys(imp.data).length > 0) {
            asyncPOST("/packs/import", imp, true, (res) => {
                if (res.success == true) {
                    loadPacks()
                    closePopup("changePacknamePopup");
                } else
                    document.getElementById("confirmImport").innerText = res.reason;
            });
        } else {
            document.getElementById("confirmImportNotification").innerText = "No valid textures to import";
        }
    });
}

async function importImage(name, file, metaFile, resolve) {
    var base64 = await file.async("base64");
    var meta = (metaFile ? JSON.parse(await metaFile.async("text")) : undefined)

    if (meta != undefined && meta.animation != undefined) {
        var canvas = document.createElement("canvas");
        var ctx = canvas.getContext("2d");
        var canvas2 = document.createElement("canvas");
        var ctx2 = canvas2.getContext("2d");

        let image = new Image();
        image.onload = function() {
            ctx.drawImage(image, 0, 0);
            canvas2.width = image.height;
            canvas2.height = canvas.width;
            var layout = [];
            for (let i = 0; i < image.height/image.width; i++) {
                let imgData = ctx.getImageData(i * canvas2.height, 0, canvas2.height, canvas2.width);
                ctx2.putImageData(imgData, 0, i * canvas2.width);
                layout.push([i]);
            }
            var obj = {
                "modelVersion": 2,
                "piskel": {
                    "name": name.split("~").pop(), "description": "",
                    "fps": (meta.animation.frametime ? 20/meta.animation.frametime : 20),
                    "height": image.width, "width": image.width,
                    "layers": [
                        JSON.stringify({
                            "name": "Layer 1", "opacity": 1,
                            "frameCount": image.height/image.width,
                            "chunks": [{
                                "layout": layout,
                                "base64PNG": "data:image/png;base64,"+canvas2.toDataURL("image/png")
                            }]
                        })
                    ],
                    "hiddenFrames": []
                }
            };
            var resMeta = {};
            if (meta.texture) {
                if (meta.texture.blur) resMeta.blur = true;
                if (meta.texture.clamp) resMeta.clamp = true;
            }
            if (meta.animation.interpolate) resMeta.interpolate = true;
            if (resMeta != {}) obj.meta = resMeta;
            resolve(obj);
        }
        image.src = "data:image/png;base64,"+base64;
        
    } else {
        let image = new Image();
        image.onload = function() {
            var obj = {
                "modelVersion": 2,
                "piskel": {
                    "name": name.split("~").pop(), "description": "",
                    "fps": 1,
                    "height": image.height, "width": image.width,
                    "layers": [
                        JSON.stringify({
                            "name": "Layer 1", "opacity": 1,
                            "frameCount": 1,
                            "chunks": [{
                                "layout": [[0]],
                                "base64PNG": "data:image/png;base64,"+base64
                            }]
                        })
                    ],
                    "hiddenFrames": []
                }
            };
            
            if (meta) {
                var resMeta = {};
                if (meta.texture) {
                    if (meta.texture.blur) resMeta.blur = true;
                    if (meta.texture.clamp) resMeta.clamp = true;
                }
                if (meta.animation)
                    if (meta.animation.interpolate) resMeta.interpolate = true;
                if (resMeta != {}) obj.meta = resMeta;
            }
            resolve(obj);
        }
        image.src = "data:image/png;base64,"+base64;
    }
}

function confirmDelUser() {
    document.getElementById("delUserPassword").value = "";
    document.getElementById("delUserNotification").innerText = "This CANNOT be undone!";
    openPopup("delUserPopup");
}

function delUser() {
    var pass = document.getElementById("delUserPassword").value;
    if (!/^[a-zA-Z0-9_@#$%^&-+=()]{5,24}$/.test(pass)) {
        document.getElementById("delUserNotification").innerText = "Invalid Password";
        return;
    }
    asyncPOST("/auth/deluser", {
        "pass": pass
    }, true, (res) => {
        if (res.success == true) {
            logout();
        } else {
            document.getElementById("delUserNotification").innerText = res.reason;
        }
    });
}

function parseDesc(desc) {
    if (typeof desc === 'string') {
        return desc.replaceAll(/§./ig,"");
    } else if (Array.isArray(desc)) {
        var str = "";
        for (let obj of desc) {
            if (obj.text != undefined)
                str = str + obj.text;
        }
        return str;
    } else {
        throw new Error("Could not parse description: "+desc);
    }
}