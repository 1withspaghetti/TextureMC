class Canvas {
    public highlight: CanvasRenderingContext2D;
    public preview: CanvasRenderingContext2D;
    public main: CanvasRenderingContext2D;

    scaleFactor = 1.05;
    camera = $("#camera");
    container = $("#pos_container");
    dragging = false;
    active = false;
    scale = 16;
    width = 16;
    height = 16;
    containerPos = {
        x: 0,
        y: 0
    };
    tools: any = {
        pen: new PenTool(),
        eraser: new EraserTool(),
        paintBucket: new PaintBucket(),
        line: new LineTool(),
        circle: new CircleTool(),
        box: new BoxTool(),
        variation: new VariationTool()
    }

    color: tinycolor.Instance = new tinycolor("#000000");
    isTransparent: boolean = false;
    currentTool: Tool = this.tools.pen;
    toolSize: number = 1;
    toolPixels: Array<{x:number,y:number}> = [];
    historyLock: Uint8ClampedArray | null = null;
    historyLockSize: {x: number, y: number} | null = null;
    historyPosition: number = -1;
    savedHistoryPosition: number = -1;
    history: HistoryElement[] = [];

    constructor() {
        var highlight = ($("#highlight")[0] as HTMLCanvasElement).getContext("2d");
        var preview = ($("#preview")[0] as HTMLCanvasElement).getContext("2d");
        var main = ($("#main")[0] as HTMLCanvasElement).getContext("2d");
        if (!preview || !main || !highlight) throw new TypeError("CanvasRenderingContext cannot be null");
        this.highlight = highlight;
        this.preview = preview;
        this.main = main;
        main.getContextAttributes().willReadFrequently = true;
        main.imageSmoothingEnabled = false
        preview.fillStyle = "rgb(0, 0, 0)";
        main.fillStyle = "rgb(0, 0, 0)";
        highlight.fillStyle = "rgb(255, 255, 255)";

        this.updateTransform();

        $(document).on("tool.change", (e, p: string)=>{
            var tool = this.tools[p];
            if (!tool) throw new TypeError("Unknown Tool Id: "+p);
            this.currentTool = tool;
            this.main.fillStyle = this.color.toString();
            this.main.strokeStyle = this.color.toString();
        }).on("tool.size_change", (e, data: {size: number, pixels: Array<{x:number,y:number}>}) => {
            this.toolPixels = data.pixels;
            this.toolSize = data.size;
        }).on("color.change", (e, color: tinycolor.Instance) => {
            this.color = color;
            this.isTransparent = (this.color.getAlpha() < 1)

            this.main.fillStyle = this.preview.fillStyle = color.toString();

            if (this.isTransparent) this.highlight.fillStyle = "rgb(160, 215, 240)"
            else this.highlight.fillStyle = "rgb(255, 255, 255)";
        })

        this.camera.on('DOMMouseScroll mousewheel', (evt) => {
            var event = evt.originalEvent as any;
            var delta = event.wheelDelta ? event.wheelDelta/40 : (event.detail ? -event.detail : 0);
            if (delta) {
                var factor = Math.pow(this.scaleFactor, delta);
    
                factor = Math.max(Math.min(this.scale * factor, 100), 2) / this.scale;
    
                const scaleChange = (this.scale * factor) - this.scale;
                const changeX = scaleChange * this.width;
                const changeY = scaleChange * this.width;
    
                var rect = this.container[0].getBoundingClientRect();
                const mouseX = (event.clientX - rect.left) / (this.width * this.scale);
                const mouseY = (event.clientY - rect.top) / (this.height * this.scale);
    
                this.containerPos.x = this.containerPos.x - (mouseX * changeX);
                this.containerPos.y = this.containerPos.y - (mouseY * changeY);
    
    
                this.scale *= factor;
                this.updateTransform();
            }
            evt.preventDefault();
        }).on('mousedown', (evt) => {
            if (evt.button == 2)
                this.dragging = true;
            else {
                this.active = true;
                if (!evt.originalEvent) throw new Error("Undefined Event");
                var p = this.getCoords(evt.originalEvent);
                this.main.fillStyle = this.preview.fillStyle = this.color.toString();
                this.clearPreview();
                this.currentTool.onMouseDown(evt.originalEvent, p, this);
            }
        })
        .on('mousemove', (evt) => {
            if (this.dragging) {
                this.containerPos.x += evt.originalEvent?.movementX || 0;
                this.containerPos.y += evt.originalEvent?.movementY || 0;
                this.updateTransform();
            }
            if (!evt.originalEvent) throw new Error("Undefined Event");
            var p = this.getCoords(evt.originalEvent);
            this.main.fillStyle = this.preview.fillStyle = this.color.toString();
            this.clearPreview();
            this.currentTool.onMouseMove(evt.originalEvent, p, this);
        })
        .on('mouseup', (evt) => {
            this.dragging = false;
            this.active = false;
            if (evt.button != 2) {
                if (!evt.originalEvent) throw new Error("Undefined Event");
                var p = this.getCoords(evt.originalEvent);
                this.main.fillStyle = this.preview.fillStyle = this.color.toString();
                this.clearPreview();
                this.currentTool.onMouseUp(evt.originalEvent, p, this);
            }
        })
        .on('contextmenu', e => e.preventDefault());

        $(document).on('mouseup', () => {
            this.dragging = false;
            this.active = false;
        }).on("keydown", (e) => {
            
            if (e.key == 'z' && e.ctrlKey) {
                e.preventDefault();
                if (this.historyPosition >= 0) {
                    var changes = this.history[this.historyPosition];
                    for (let pixel of changes.pixels) {
                        this.main.fillStyle = pixel.o.toString();
                        setPixel(this.main, pixel.o, pixel.x, pixel.y);
                    }
                    if (changes.size)
                        this.setSize(changes.size.x1,changes.size.y1)
                    this.historyPosition -= 1;
                }
                $(document).trigger("canvas.saved", this.historyPosition == this.savedHistoryPosition)
            }
            else if (e.key == 'y' && e.ctrlKey) {
                e.preventDefault();
                if (this.historyPosition < this.history.length-1) {
                    this.historyPosition += 1;
                    var changes = this.history[this.historyPosition];
                    if (changes.size)
                        this.setSize(changes.size.x2,changes.size.y2)
                    for (let pixel of changes.pixels) {
                        this.main.fillStyle = pixel.n.toString();
                        setPixel(this.main, pixel.n, pixel.x, pixel.y);
                    }
                }
                $(document).trigger("canvas.saved", this.historyPosition == this.savedHistoryPosition)
            }
        })
        window.addEventListener('resize', () => {
            this.updateTransform();
        }, false);

        $(() => {
            this.containerPos = {
                "x": (this.camera.width()||0)/2 - ((this.width||0)*this.scale/2), 
                "y": (this.camera.height()||0)/2 - ((this.width||0)*this.scale/2)
            };
            this.updateTransform();
        });
    }

    setImage = (base64: string) => {
        var img = document.createElement("img");
        img.src = "data:image/png;base64,"+base64;
        img.onload = () => {
            this.historyLock = null;
            this.historyPosition = -1;
            this.history = [];
            this.width = img.width;
            this.height = img.height;
            $("#pos_container canvas").each((i,e)=>{
                var c = e as HTMLCanvasElement;
                c.width = this.width;
                c.height = this.height;
            })
            this.containerPos = {
                "x": (this.camera.width()||0)/2 - ((this.width||0)*this.scale/2), 
                "y": (this.camera.height()||0)/2 - ((this.height||0)*this.scale/2)
            };
            this.main.drawImage(img, 0, 0);
            this.updateTransform();
        }
    }

    getImage = (): string => {
        return this.main.canvas.toDataURL("image/png").replace("data:image/png;base64,","")
    }

    setSize = (width: number, height: number) => {
        this.width = width;
        this.height = height;
        $("#pos_container canvas").each((i,e)=>{
            var c = e as HTMLCanvasElement;
            c.width = width;
            c.height = height;
        })
        this.containerPos = {
            "x": (this.camera.width()||0)/2 - ((this.width||0)*this.scale/2), 
            "y": (this.camera.height()||0)/2 - ((this.width||0)*this.scale/2)
        };
        this.updateTransform();
    };
    
    setSizeKeep = (width: number, height: number, scale: Boolean) => {
        var old = document.createElement("img")
        var oldWidth = this.width
        var oldHeight = this.height
        old.onload = ()=>{
            this.setSize(width, height)
            if (scale) {
                this.main.drawImage(old, 0, 0, this.width, this.height)
            } else {
                this.main.drawImage(old, 0, 0, oldWidth, oldHeight)
            }
            old.remove();
        }
        old.src = this.main.canvas.toDataURL("image/png")
    }

    clearPreview = () => {
        this.preview.clearRect(0, 0, this.width, this.height)
        this.highlight.clearRect(0, 0, this.width, this.height)
    };
    lockHistory = () => {
        this.historyLock = this.main.getImageData(0, 0, this.width, this.height).data;
        this.historyLockSize = {x: canvas.width, y: canvas.height};
    };
    saveHistory = () => {
        if (!this.historyLock || !this.historyLockSize) throw "No save state to compare too";
        var history: {x: number, y: number, o: tinycolor.Instance, n: tinycolor.Instance}[] = [];
        var historySize: {x1: number, x2: number, y1: number, y2: number} | undefined = undefined;
        var current = this.main.getImageData(0, 0, this.width, this.height).data;
        var currentSize = {x: this.width, y: this.height};
        var lock = this.historyLock;
        var lockSize = this.historyLockSize;

        for (let y = 0; y < Math.max(lockSize.y, this.height); y++) {
            for (let x = 0; x < Math.max(lockSize.x, this.width); x++) {
                var o = (x < lockSize.x && y < lockSize.y ? (y * lockSize.x * 4) + (x * 4) : -1);
                var n = (x < currentSize.x && y < currentSize.y ? (y * currentSize.x * 4) + (x * 4) : -1);
                var oArr = o != -1 ? lock.subarray(o,o+4) : null;
                var nArr = n != -1 ? current.subarray(n,n+4) : null;
                if (oArr != nArr) {
                    history.push({
                        x: x,
                        y: y,
                        o: oArr ? tinycolor({r: oArr[0], g: oArr[1], b: oArr[2], a: oArr[3]}) : tinycolor({r: 255, g: 0, b: 0, a: 255}),
                        n: nArr ? tinycolor({r: nArr[0], g: nArr[1], b: nArr[2], a: nArr[3]}) : tinycolor({r: 255, g: 0, b: 0, a: 255})
                    })
                }
            }
        }

        if (this.historyLockSize.x != canvas.width || this.historyLockSize.y != canvas.height) {
            historySize = {
                x1: this.historyLockSize.x,
                x2: canvas.width,
                y1: this.historyLockSize.y,
                y2: canvas.height
            }
        }
        
        if (history.length > 0 || historySize) {
            if (this.historyPosition < this.history.length-1) {
                this.history = this.history.slice(0, this.historyPosition+1);
            }
            this.history.push(new HistoryElement(history, historySize));
            this.historyPosition++;
            $(document).trigger("canvas.saved", false);
        }
    }

    updateTransform = () => {
        var camBox = {width: this.camera.width() || 0, height: this.camera.height() || 0};

        this.containerPos.x = Math.min(this.containerPos.x, camBox.width * (1/2));
        this.containerPos.x = Math.max(this.containerPos.x + (this.width * this.scale), camBox.width * (1/2)) - (this.width * this.scale);
        
        this.containerPos.y = Math.min(this.containerPos.y, camBox.height * (1/2));
        this.containerPos.y = Math.max(this.containerPos.y + (this.height * this.scale), camBox.height * (1/2)) - (this.height * this.scale);

        var newTrans = "translateX("+this.containerPos.x+"px) translateY("+this.containerPos.y+"px) scale("+this.scale+")";
        if (this.container.css("transform") != newTrans) this.container.css("transform", newTrans);
    }

    getCoords = (evt: MouseEvent | undefined) => {
        if (!evt) throw new TypeError("evt cannot be undefined");
        var c: HTMLCanvasElement = this.main.canvas;
        var rect = c.getBoundingClientRect(); 
        var x = Math.floor((evt.clientX - rect.left) / (c.width / c.clientWidth * this.scale));
        var y = Math.floor((evt.clientY - rect.top) / (c.height / c.clientHeight * this.scale));
        return {x: x, y: y};
    }
}
class HistoryElement {
    pixels: {x: number, y: number, o: tinycolor.Instance, n: tinycolor.Instance}[];
    size: {x1: number, x2: number, y1: number, y2: number} | undefined;

    constructor(pixels: {x: number, y: number, o: tinycolor.Instance, n: tinycolor.Instance}[], size: {x1: number, x2: number, y1: number, y2: number} | undefined) {
        this.pixels = pixels;
        this.size = size;
    }
}
var canvas = new Canvas();