"use strict";
class PenTool {
    onMouseDown(e, pos, canvas) {
        canvas.lockHistory();
        this.lastPosition = pos;
        drawCircleFromPixels(canvas.toolPixels, canvas.main, canvas.color, pos.x, pos.y);
        if (canvas.isTransparent)
            drawCircleFromPixels(canvas.toolPixels, canvas.highlight, null, pos.x, pos.y);
    }
    onMouseMove(e, pos, canvas) {
        if (canvas.active) {
            for (let pixel of canvas.toolPixels) {
                if (this.lastPosition)
                    drawLine(canvas.main, canvas.color, this.lastPosition.x + pixel.x, this.lastPosition.y + pixel.y, pos.x + pixel.x, pos.y + pixel.y);
            }
            //drawCircleFromPixels(canvas.toolPixels, canvas.main, canvas.color, pos.x, pos.y);
        }
        if (canvas.isTransparent || !canvas.active)
            drawCircleFromPixels(canvas.toolPixels, canvas.highlight, null, pos.x, pos.y);
        this.lastPosition = pos;
    }
    onMouseUp(e, pos, canvas) {
        canvas.saveHistory();
        this.lastPosition = undefined;
    }
}
class EraserTool {
    onMouseDown(e, pos, canvas) {
        canvas.lockHistory();
        this.lastPosition = pos;
        drawCircleFromPixels(canvas.toolPixels, canvas.main, tinycolor("#00000000"), pos.x, pos.y);
        drawCircleFromPixels(canvas.toolPixels, canvas.highlight, null, pos.x, pos.y);
    }
    onMouseMove(e, pos, canvas) {
        if (canvas.active) {
            for (let pixel of canvas.toolPixels) {
                if (this.lastPosition)
                    drawLine(canvas.main, tinycolor("#00000000"), this.lastPosition.x + pixel.x, this.lastPosition.y + pixel.y, pos.x + pixel.x, pos.y + pixel.y);
            }
            //drawCircleFromPixels(canvas.toolPixels, canvas.main, canvas.color, pos.x, pos.y);
        }
        drawCircleFromPixels(canvas.toolPixels, canvas.highlight, null, pos.x, pos.y);
        this.lastPosition = pos;
    }
    onMouseUp(e, pos, canvas) {
        canvas.saveHistory();
        this.lastPosition = undefined;
    }
}
class PaintBucket {
    onMouseDown(e, pos, canvas) {
        canvas.lockHistory();
        var target = canvas.main.getImageData(pos.x, pos.y, 1, 1).data;
        var c = canvas.color.toRgb();
        if (target[0] == c.r && target[1] == c.g && target[2] == c.b && target[3] == c.a * 255)
            return; // Same color detection
        this.spread(canvas.main, canvas.color, pos.x, pos.y, target);
        canvas.saveHistory();
    }
    onMouseMove(e, pos, canvas) {
        if (!canvas.active)
            setPixel(canvas.highlight, null, pos.x, pos.y);
    }
    onMouseUp(e, pos, canvas) {
    }
    spread(ctx, color, x, y, target) {
        if (x < 0 || x >= canvas.width || y < 0 || y >= canvas.height)
            return;
        if (this.check(canvas.main.getImageData(x, y, 1, 1).data, target)) {
            setPixel(ctx, color, x, y);
            this.spread(ctx, color, x + 1, y, target);
            this.spread(ctx, color, x - 1, y, target);
            this.spread(ctx, color, x, y + 1, target);
            this.spread(ctx, color, x, y - 1, target);
        }
    }
    check(a, b) {
        for (let i = 0; i < Math.min(a.length, b.length); i++) {
            if (a[i] != b[i])
                return false;
        }
        return true;
    }
}
class LineTool {
    onMouseDown(e, pos, canvas) {
        canvas.lockHistory();
        this.lastPosition = pos;
        drawCircleFromPixels(canvas.toolPixels, canvas.preview, canvas.color, pos.x, pos.y);
    }
    onMouseMove(e, pos, canvas) {
        if (canvas.active) {
            if (!this.lastPosition)
                return;
            var ctx = (canvas.isTransparent ? canvas.highlight : canvas.preview);
            if (e.shiftKey)
                pos = squareCoords(this.lastPosition, pos);
            for (let pixel of canvas.toolPixels) {
                drawLine(ctx, null, this.lastPosition.x + pixel.x, this.lastPosition.y + pixel.y, pos.x + pixel.x, pos.y + pixel.y);
            }
        }
        else
            drawCircleFromPixels(canvas.toolPixels, canvas.highlight, null, pos.x, pos.y);
    }
    onMouseUp(e, pos, canvas) {
        if (!this.lastPosition)
            return;
        if (e.shiftKey)
            pos = squareCoords(this.lastPosition, pos);
        for (let pixel of canvas.toolPixels) {
            drawLine(canvas.main, canvas.color, this.lastPosition.x + pixel.x, this.lastPosition.y + pixel.y, pos.x + pixel.x, pos.y + pixel.y);
        }
        this.lastPosition = undefined;
        canvas.saveHistory();
    }
}
class CircleTool {
    onMouseDown(e, pos, canvas) {
        canvas.lockHistory();
        this.lastPosition = pos;
        drawCircleFromPixels(canvas.toolPixels, canvas.preview, canvas.color, pos.x, pos.y);
    }
    onMouseMove(e, pos, canvas) {
        if (canvas.active) {
            if (!this.lastPosition)
                return;
            var ctx = (canvas.isTransparent ? canvas.highlight : canvas.preview);
            if (e.shiftKey)
                pos = squareCoords(this.lastPosition, pos);
            for (let pixel of drawOval(this.lastPosition.x, this.lastPosition.y, pos.x, pos.y)) {
                drawCircleFromPixels(canvas.toolPixels, ctx, null, pixel.x, pixel.y);
            }
        }
        else
            drawCircleFromPixels(canvas.toolPixels, canvas.highlight, null, pos.x, pos.y);
    }
    onMouseUp(e, pos, canvas) {
        if (!this.lastPosition)
            return;
        if (e.shiftKey)
            pos = squareCoords(this.lastPosition, pos);
        for (let pixel of drawOval(this.lastPosition.x, this.lastPosition.y, pos.x, pos.y)) {
            drawCircleFromPixels(canvas.toolPixels, canvas.main, canvas.color, pixel.x, pixel.y);
        }
        this.lastPosition = undefined;
        canvas.saveHistory();
    }
}
class BoxTool {
    onMouseDown(e, pos, canvas) {
        canvas.lockHistory();
        this.lastPosition = pos;
        drawCircleFromPixels(canvas.toolPixels, canvas.preview, canvas.color, pos.x, pos.y);
    }
    onMouseMove(e, pos, canvas) {
        if (canvas.active) {
            if (!this.lastPosition)
                return;
            var last = this.lastPosition;
            var ctx = (canvas.isTransparent ? canvas.highlight : canvas.preview);
            if (e.shiftKey)
                pos = squareCoords(this.lastPosition, pos);
            for (let pixel of canvas.toolPixels) {
                drawLine(ctx, null, last.x + pixel.x, last.y + pixel.y, pos.x + pixel.x, last.y + pixel.y);
                drawLine(ctx, null, pos.x + pixel.x, last.y + pixel.y, pos.x + pixel.x, pos.y + pixel.y);
                drawLine(ctx, null, pos.x + pixel.x, pos.y + pixel.y, last.x + pixel.x, pos.y + pixel.y);
                drawLine(ctx, null, last.x + pixel.x, pos.y + pixel.y, last.x + pixel.x, last.y + pixel.y);
            }
        }
        else
            drawCircleFromPixels(canvas.toolPixels, canvas.highlight, null, pos.x, pos.y);
    }
    onMouseUp(e, pos, canvas) {
        if (!this.lastPosition)
            return;
        var last = this.lastPosition;
        if (e.shiftKey)
            pos = squareCoords(this.lastPosition, pos);
        for (let pixel of canvas.toolPixels) {
            drawLine(canvas.main, canvas.color, last.x + pixel.x, last.y + pixel.y, pos.x + pixel.x, last.y + pixel.y);
            drawLine(canvas.main, canvas.color, pos.x + pixel.x, last.y + pixel.y, pos.x + pixel.x, pos.y + pixel.y);
            drawLine(canvas.main, canvas.color, pos.x + pixel.x, pos.y + pixel.y, last.x + pixel.x, pos.y + pixel.y);
            drawLine(canvas.main, canvas.color, last.x + pixel.x, pos.y + pixel.y, last.x + pixel.x, last.y + pixel.y);
        }
        this.lastPosition = undefined;
        canvas.saveHistory();
    }
}
class VariationTool {
    constructor() {
        this.appliedPixels = [];
    }
    onMouseDown(e, pos, canvas) {
        canvas.lockHistory();
        var newAppliedPixels = [];
        for (let pixel of canvas.toolPixels) {
            var point = { x: pos.x + pixel.x, y: pos.y + pixel.y };
            if (point.x >= 0 && point.y >= 0 && point.x < canvas.width && point.y < canvas.height) {
                newAppliedPixels.push(point);
                var imgData = canvas.main.getImageData(point.x, point.y, 1, 1);
                this.randomize(imgData.data);
                canvas.main.putImageData(imgData, point.x, point.y);
            }
        }
        if (canvas.isTransparent)
            drawCircleFromPixels(canvas.toolPixels, canvas.highlight, null, pos.x, pos.y);
        this.appliedPixels = newAppliedPixels;
    }
    onMouseMove(e, pos, canvas) {
        if (canvas.active) {
            var newAppliedPixels = [];
            for (let pixel of canvas.toolPixels) {
                var point = { x: pos.x + pixel.x, y: pos.y + pixel.y };
                newAppliedPixels.push(point);
                if (point.x >= 0 && point.y >= 0 && point.x < canvas.width && point.y < canvas.height && !this.pixelIncludes(this.appliedPixels, point)) {
                    var imgData = canvas.main.getImageData(point.x, point.y, 1, 1);
                    this.randomize(imgData.data);
                    canvas.main.putImageData(imgData, point.x, point.y);
                }
            }
            if (canvas.isTransparent)
                drawCircleFromPixels(canvas.toolPixels, canvas.highlight, null, pos.x, pos.y);
            this.appliedPixels = newAppliedPixels;
        }
        else
            drawCircleFromPixels(canvas.toolPixels, canvas.highlight, null, pos.x, pos.y);
    }
    onMouseUp(e, pos, canvas) {
        this.appliedPixels = [];
        canvas.saveHistory();
    }
    pixelIncludes(list, pixel) {
        for (let test of list) {
            if (test.x == pixel.x && test.y == pixel.y)
                return true;
        }
        return false;
    }
    randomize(d) {
        if (Math.random() * 2 > 1) {
            d[0] = Math.min(d[0] + 1, 255);
            d[1] = Math.min(d[1] + 1, 255);
            d[2] = Math.min(d[2] + 1, 255);
        }
        else {
            d[0] = Math.max(d[0] - 1, 0);
            d[1] = Math.max(d[1] - 1, 0);
            d[2] = Math.max(d[2] - 1, 0);
        }
        return d;
    }
}
function squareCoords(c1, c2) {
    var change = Math.max(Math.abs(c2.x - c1.x), Math.abs(c2.y - c1.y));
    c2.x = c1.x + (change * (c2.x > c1.x ? 1 : -1));
    c2.y = c1.y + (change * (c2.y > c1.y ? 1 : -1));
    return c2;
}
function drawLine(ctx, color, x1, y1, x2, y2) {
    var dx = Math.abs(x2 - x1);
    var dy = Math.abs(y2 - y1);
    var sx = (x1 < x2) ? 1 : -1;
    var sy = (y1 < y2) ? 1 : -1;
    var err = dx - dy;
    while (true) {
        setPixel(ctx, color, x1, y1);
        if ((x1 === x2) && (y1 === y2))
            break;
        var e2 = 2 * err;
        if (e2 > -dy) {
            err -= dy;
            x1 += sx;
        }
        if (e2 < dx) {
            err += dx;
            y1 += sy;
        }
    }
}
function drawOval(x0, y0, x1, y1) {
    var c = {
        x0: Math.min(x0, x1),
        y0: Math.min(y0, y1),
        x1: Math.max(x0, x1),
        y1: Math.max(y0, y1)
    };
    var pixels = [];
    var xC = Math.round((c.x0 + c.x1) / 2);
    var yC = Math.round((c.y0 + c.y1) / 2);
    var evenX = (c.x0 + c.x1) % 2;
    var evenY = (c.y0 + c.y1) % 2;
    var rX = c.x1 - xC;
    var rY = c.y1 - yC;
    var x;
    var y;
    var angle;
    for (x = c.x0; x <= xC; x++) {
        angle = Math.acos((x - xC) / rX);
        y = Math.round(rY * Math.sin(angle) + yC);
        pixels.push({ x: x - evenX, y: y });
        pixels.push({ x: x - evenX, y: 2 * yC - y - evenY });
        pixels.push({ x: 2 * xC - x, y: y });
        pixels.push({ x: 2 * xC - x, y: 2 * yC - y - evenY });
    }
    for (y = c.y0; y <= yC; y++) {
        angle = Math.asin((y - yC) / rY);
        x = Math.round(rX * Math.cos(angle) + xC);
        pixels.push({ x: x, y: y - evenY });
        pixels.push({ x: 2 * xC - x - evenX, y: y - evenY });
        pixels.push({ x: x, y: 2 * yC - y });
        pixels.push({ x: 2 * xC - x - evenX, y: 2 * yC - y });
    }
    return pixels;
}
function drawCircle(diameter, ctx, color, centerX, centerY) {
    var pixels = generateCircle(diameter);
    centerX = centerX || diameter / 2;
    centerY = centerY || diameter / 2;
    for (let pixel of pixels) {
        setPixel(ctx, color, centerX + pixel.x, centerY + pixel.y);
    }
    return pixels;
}
function drawCircleFromPixels(pixels, ctx, color, centerX, centerY) {
    for (let pixel of pixels) {
        setPixel(ctx, color, centerX + pixel.x, centerY + pixel.y);
    }
}
function setPixel(ctx, color, x, y) {
    if (!color) {
        ctx.fillRect(x, y, 1, 1);
    }
    else if (color.getAlpha() < 1)
        ctx.clearRect(x, y, 1, 1);
    else {
        ctx.fillRect(x, y, 1, 1);
    }
}
function generateCircle(diameter) {
    if (diameter == 1)
        return [{ x: 0, y: 0 }]; // Shortcut for speed
    var radius = diameter / 2;
    var maxblocks_x, maxblocks_y;
    if ((radius * 2) % 2 == 0) {
        maxblocks_x = Math.ceil(radius - 0.5) * 2 + 1;
        maxblocks_y = Math.ceil(radius - 0.5) * 2 + 1;
    }
    else {
        maxblocks_x = Math.ceil(radius) * 2;
        maxblocks_y = Math.ceil(radius) * 2;
    }
    var pixels = [];
    for (var y = -maxblocks_y / 2 + 1; y <= maxblocks_y / 2 - 1; y++) {
        for (var x = -maxblocks_x / 2 + 1; x <= maxblocks_x / 2 - 1; x++) {
            var xfilled = (Math.sqrt((Math.pow(y, 2)) + Math.pow(x, 2))) <= radius;
            if (xfilled) {
                var nudge = (radius * 2) % 2 == 0 ? 0.5 : 0;
                pixels.push({
                    x: x - nudge,
                    y: y - nudge
                });
            }
        }
    }
    return pixels;
}
