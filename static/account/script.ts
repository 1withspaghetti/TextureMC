
$(this).on("load-packs", () => {
    $.ajax("/packs/list", {dataType: "json"}).done((res) => {
        for (let pack of res.packs) {
            $("#packs").append(`<div data-pack="${pack.id}"><v>"+pack.name+"<img src="images/edit.svg" onclick="confirmChangePackname(event)"></v><v>"+pack.version+"<img src="images/trash.svg" onclick="confirmDelete(event)"><img src="images/export.svg\" onclick="confirmExport(event)"><img src="images/copy.svg" onclick="confirmCopy(event)"></v></div>`);
        }
    })
})