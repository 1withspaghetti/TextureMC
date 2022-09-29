(function() {
    var pen_canvas = $("#pen_canvas")[0] as HTMLCanvasElement;
    var ctx = pen_canvas.getContext("2d");
    if (!ctx) throw new TypeError("Could not get CanvasRenderingContext");
    ctx.fillStyle = "#000";
    $("#pen_size").on("input", (e)=>{
        var size = ($("#pen_size")[0] as HTMLInputElement).valueAsNumber * 2 - 1;
        if (!ctx) throw new TypeError("Could not get CanvasRenderingContext");
        ctx.clearRect(0, 0, pen_canvas.width, pen_canvas.height)
        var pixels = drawCircle(size, ctx, null, 7, 4);
        $(document).trigger("tool.size_change", {size: size, pixels: pixels});
    });
    $("#pen_size").trigger("input");
    $(document).on("color.change", (e, color: tinycolor.Instance) => {
        var size = ($("#pen_size")[0] as HTMLInputElement).valueAsNumber * 2 - 1;
        if (!ctx) throw new TypeError("Could not get CanvasRenderingContext");
        ctx.fillStyle = (color.getAlpha() < 1 ? "#cce5eb" : color.toString());
        ctx.clearRect(0, 0, pen_canvas.width, pen_canvas.height)
       drawCircle(size, ctx, null, 7, 4);
    })
})();
(function() {
    $(".tool").on("click", function (e) {
        $(".tool.selected").removeClass("selected");
        this.classList.add("selected");
        if (this.hasAttribute("data-cursor")) {
            canvas.camera.css("cursor", `url(${this.getAttribute("data-cursor")}) 0 16, default`);
        } else {
            canvas.camera.css("cursor", "auto");
        }
        $(document).trigger("tool.change", this.getAttribute("data-tool") || "pen");
    });
    $("#image_upload").on("change",e=>{
        var files = ($("#image_upload")[0] as HTMLInputElement).files;
        if (!files || !files[0]) return;
        var reader = new FileReader();
        reader.readAsDataURL(files[0]);
        reader.onload = e => {
            var content = e.target?.result;
            if (typeof content != "string") throw "Invalid result type";
            console.log("Data url: "+content)
            var img = document.createElement("img");
            img.src = content;
            img.onload = () => {
                // TODO prompt user for model to either replace the image or add onto it


            }
        }
    })
})();
(function() {
    const MAX_PALETTE = 9;
    var palette: string[] = [];

    $("#color_selection").spectrum({
        color: "#000000",
        preferredFormat: "hex",
        showPalette: true,
        showSelectionPalette: false,
        hideAfterPaletteSelect: true,
        clickoutFiresChange : true,
        showButtons: false,
        showInput: true,
        containerClassName: 'sp-override',
        replacerClassName: 'sp-override',
        palette: [
            ['rgba(0,0,0,0)']
        ]
    }).on("move.spectrum change.spectrum", (e, color: tinycolor.Instance)=>{
        $(document).trigger("color.change", color)
    }).on("change.spectrum", (e, color: tinycolor.Instance) => {
        var c = color.toHexString();
        if (!palette.includes(c)) {
            palette.unshift(c);
            if (palette.length > MAX_PALETTE) palette.pop();
            var newPalette = palette.map(c => [c]);
            newPalette.unshift(["rgba(0,0,0,0)"])
            $("#color_selection").spectrum("option", "palette", newPalette);
        }
    });
})();
