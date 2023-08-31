"use strict";
(function () {
    var pen_canvas = $("#pen_canvas")[0];
    var ctx = pen_canvas.getContext("2d");
    if (!ctx)
        throw new TypeError("Could not get CanvasRenderingContext");
    ctx.fillStyle = "#000";
    $("#pen_size").on("input", (e) => {
        var size = $("#pen_size")[0].valueAsNumber * 2 - 1;
        if (!ctx)
            throw new TypeError("Could not get CanvasRenderingContext");
        ctx.clearRect(0, 0, pen_canvas.width, pen_canvas.height);
        var pixels = drawCircle(size, ctx, null, 7, 4);
        $(document).trigger("tool.size_change", { size: size, pixels: pixels });
    });
    $("#pen_size").trigger("input");
    $(document).on("color.change", (e, color) => {
        var size = $("#pen_size")[0].valueAsNumber * 2 - 1;
        if (!ctx)
            throw new TypeError("Could not get CanvasRenderingContext");
        ctx.fillStyle = (color.getAlpha() < 1 ? "#cce5eb" : color.toString());
        ctx.clearRect(0, 0, pen_canvas.width, pen_canvas.height);
        drawCircle(size, ctx, null, 7, 4);
    });
})();
(function () {
    $(".tool").on("click", function (e) {
        $(".tool.selected").removeClass("selected");
        this.classList.add("selected");
        if (this.hasAttribute("data-cursor")) {
            canvas.camera.css("cursor", `url(${this.getAttribute("data-cursor")}) 0 16, default`);
        }
        else {
            canvas.camera.css("cursor", "auto");
        }
        $(document).trigger("tool.change", this.getAttribute("data-tool") || "pen");
    });
    var current_image = null;
    $("#image_upload").on("change", e => {
        var files = $("#image_upload")[0].files;
        if (!files || !files[0])
            return;
        var reader = new FileReader();
        reader.readAsDataURL(files[0]);
        reader.onload = e => {
            var _a;
            var content = (_a = e.target) === null || _a === void 0 ? void 0 : _a.result;
            if (typeof content != "string")
                throw "Invalid result type";
            var img = document.createElement("img");
            img.onload = () => {
                current_image = img;
                openModal("image_upload");
            };
            img.src = content;
        };
    });
    $("#image_upload_add").on("click", e => {
        if (!current_image)
            throw "Image upload fired with no uploaded image";
        canvas.lockHistory();
        canvas.main.drawImage(current_image, 0, 0);
        canvas.saveHistory();
        closeModal();
    });
    $("#image_upload_replace").on("click", e => {
        if (!current_image)
            throw "Image upload fired with no uploaded image";
        canvas.lockHistory();
        canvas.setSize(current_image.width, current_image.height);
        canvas.main.clearRect(0, 0, canvas.width, canvas.height);
        canvas.main.drawImage(current_image, 0, 0);
        canvas.saveHistory();
        closeModal();
    });
    $("#image_resize").on("click", e => {
        openModal("image_resize");
        $("#image_resize_width").val(canvas.width);
        $("#image_resize_height").val(canvas.height);
    });
    $("#image_resize_width").on("input", e => {
        var elm = $("#image_resize_width");
        elm.val(elm.val().replace(/[^0-9]/g, "").substring(0, 3));
        if ($("#image_resize_ratio").hasClass("checked"))
            $("#image_resize_height").val(elm.val());
    });
    $("#image_resize_height").on("input", e => {
        var elm = $("#image_resize_height");
        elm.val(elm.val().replace(/[^0-9]/g, "").substring(0, 3));
        if ($("#image_resize_ratio").hasClass("checked"))
            $("#image_resize_width").val(elm.val());
    });
    $("#image_resize_ratio").on("click", e => {
        $("#image_resize_ratio").toggleClass("checked");
    });
    $("#image_resize_scale").on("click", e => {
        $("#image_resize_scale").toggleClass("checked");
    });
    $("#image_resize_done").on("click", e => {
        var width = parseInt($("#image_resize_width").val());
        var height = parseInt($("#image_resize_height").val());
        canvas.lockHistory();
        canvas.setSizeKeep(Math.min(width, 256), Math.min(height, 256), $("#image_resize_scale").hasClass("checked"));
        canvas.saveHistory();
    });
})();
(function () {
    const MAX_PALETTE = 21;
    var palette = [];
    $("#color_selection").spectrum({
        color: "#000000",
        preferredFormat: "hex",
        showPalette: true,
        showSelectionPalette: false,
        hideAfterPaletteSelect: true,
        clickoutFiresChange: true,
        showButtons: false,
        showInput: true,
        containerClassName: 'sp-override',
        replacerClassName: 'sp-override',
        palette: [
            ['rgba(0,0,0,0)']
        ],
    }).on("move.spectrum change.spectrum", (e, color) => {
        $(document).trigger("color.change", color);
    }).on("change.spectrum", (e, color) => {
        $(document).trigger("color.paletteAdd", color);
    });
    $(document).on("color.paletteAdd", (e, color) => {
        var c = color.toHexString();
        if (palette.includes(c))
            palette.splice(palette.indexOf(c), 1);
        palette.unshift(c);
        if (palette.length > MAX_PALETTE)
            palette.splice(MAX_PALETTE);
        var newPalette = [];
        for (var i = 0; i < palette.length; i += 3) {
            newPalette.push(palette.slice(i, i + 3));
        }
        newPalette.unshift(["rgba(0,0,0,0)"]);
        $("#color_selection").spectrum("option", "palette", newPalette);
    });
})();
