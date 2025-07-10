const canvas = document.getElementById("canvas")
const context = canvas.getContext("2d")
const ctx = context

let state = 0

function next() {
    state++
    draw()
}

function drawRectangle(ctx, posX, posY, width, height, color, borderColor, filled) {
    // Draw rectangle
    ctx.save()
    ctx.fillStyle = color
    ctx.lineWidth = 5;
    ctx.strokeStyle = borderColor
    ctx.rect(posX, posY, width, height)
    if (filled) {
        ctx.fill();
    }
    ctx.stroke()
    ctx.restore()
}

function drawCircle(ctx, posX, posY, radius, startAngle, endAngle, color, borderColor, filled) {
    ctx.save()
    ctx.beginPath()
    ctx.arc(posX, posY, radius, startAngle, endAngle)
    ctx.fillStyle = color
    ctx.lineWidth = 5;
    ctx.strokeStyle = borderColor
    if (filled) {
        ctx.fill();
    }
    ctx.restore()
}

function drawExample(context) {
    // Draw rectangle
    context.save()
    context.fillStyle = 'red'
    context.fillRect(10 + state, 10, 20, 20)
    context.restore()

    // Draw circle
    context.save()
    context.beginPath()
    context.arc(100, 100, 20, 0, 2 * Math.PI)
    context.fillStyle = 'blue'
    context.fill()
    context.restore()

    // Draw line
    context.save()
    context.beginPath()
    context.moveTo(50, 100)
    context.lineTo(200, 300)
    context.lineTo(400, 100)
    context.lineWidth = 5
    context.strokeStyle = 'black'
    context.stroke()
    context.restore()
}

function drawNode(ctx, posX, posY) {
    drawCircle(ctx, posX, posY, 20, 0, 2*Math.PI, 'blue', '', true)
}

// Use Inbuilt JS Functions
function drawQuadraticBezier(ctx, startX, startY, controlX, controlY, endX, endY) {
    ctx.beginPath();
    ctx.lineWidth = 5
    ctx.strokeStyle = 'grey'
    ctx.moveTo(startX, startY);
    ctx.quadraticCurveTo(controlX, controlY, endX, endY);
    ctx.stroke();
}

// Use Inbuilt JS Functions
function drawCubicBezier(ctx, startX, startY, control1X, control1Y, control2X, control2Y, endX, endY) {
    ctx.beginPath();
    ctx.moveTo(startX, startY);
    ctx.bezierCurveTo(control1X, control1Y, control2X, control2Y, endX, endY);
    ctx.stroke();
}

function drawVehicle(ctx, posX, posY, length, width, rotation) {
    ctx.save()
    ctx.fillStyle = 'orange'
    ctx.moveTo(posX, posY);
    ctx.translate(posX, posY)
    ctx.rotate((Math.PI / 180) * rotation);
    ctx.translate(-posX, -posY);
    ctx.fillRect(posX, posY, length, width)
    ctx.restore()
}

function draw() {

    // Resize canvas
    canvas.width = canvas.offsetWidth
    canvas.height = canvas.offsetHeight

    function drawSketch1(){

        // Clear canvas
        context.clearRect(0, 0, canvas.width, canvas.height)

        var node1X = 270
        var node1Y = 20

        var node2X = 520
        var node2Y = 270

        var node3X = 270
        var node3Y = 520


        // Draw Quadratic Bezier
        // ctx, startX, startY, controlX, controlY, endX, endY) 
        drawQuadraticBezier(context, node1X, node1Y, 270, 270, node2X, node2Y)
        drawQuadraticBezier(context, node3X, node3Y, 270, 270, node2X, node2Y)

        // Draw Cubic Bezier
        // ctx, startX, startY, control1X, control1Y, control2X, control2Y, endX, endY) 
        //drawCubicBezier(context, node1X, node1Y, 270, 270, 270, 270, node2X, node2Y)

        // Draw Vehicle
        drawVehicle(context, 330, 170, 60, 40, 45)

        // Draw Nodes
        drawNode(context, node1X, node1Y)
        drawNode(context, node2X, node2Y)
        drawNode(context, node3X, node3Y)
    }

    function drawSketchX(){

        // Draw Background
        drawRectangle(context, 20, 20, 500, 500, 'blue', 'blue', false)

        var node1X = 270
        var node1Y = 20

        var node2X = 520
        var node2Y = 270

        var node3X = 270
        var node3Y = 520

        var node4X = 20
        var node4Y = 270

        drawQuadraticBezier(context, node1X, node1Y, 270, 270, node2X, node2Y)
        drawQuadraticBezier(context, node3X, node3Y, node1X, node1Y, node1X, node1Y)
        drawQuadraticBezier(context, node4X, node4Y, node2X, node2Y, node2X, node2Y)
        drawQuadraticBezier(context, node3X, node3Y, 270, 270, node2X, node2Y)
        drawQuadraticBezier(context, node4X, node4Y, 270, 270, node3X, node3Y)
        drawQuadraticBezier(context, node1X, node1Y, 270, 270, node4X, node4Y)

        // Draw Nodes
        drawNode(context, node1X, node1Y)
        drawNode(context, node2X, node2Y)
        drawNode(context, node3X, node3Y)
        drawNode(context, node4X, node4Y)

    }

    function drawSketchX2(){
        var node1X = 270
        var node1Y = 20

        var node2X = 520
        var node2Y = 270

        var node3X = 270
        var node3Y = 520

        var node4X = 20
        var node4Y = 270

        // Draw Nodes
        drawNode(context, node1X, node1Y)
        drawNode(context, node3X, node3Y)
        drawNode(context, node4X, node4Y)

        drawCubicBezier(context, node1X, node1Y, node4X-85, node4Y, node4X-85, node4Y, node3X, node3Y)

    }

    //drawExample(context)
    drawSketch1()
    drawSketchX()
    //drawSketchX2()

}

window.addEventListener('load', draw)
window.addEventListener('resize', draw)
window.addEventListener('keypress', next)