// Math classes

class Vector {
    constructor(x, y) {
        if (typeof x != 'number')
            throw new Error('X is not a number')
        if (typeof y != 'number')
            throw new Error('Y is not a number')

        this.x = x
        this.y = y
    }

    // Vector-scalar operations

    addScalar(scalar) {
        if (typeof scalar != 'number')
            throw new Error('Scalar is not a number')

        return new Vector(this.x + scalar, this.y + scalar)
    }
    substractScalar(scalar) {
        if (typeof scalar != 'number')
            throw new Error('Scalar is not a number')

        return new Vector(this.x - scalar, this.y - scalar)
    }
    multiplyScalar(scalar) {
        if (typeof scalar != 'number')
            throw new Error('Scalar is not a number')

        return new Vector(this.x * scalar, this.y * scalar)
    }
    divideScalar(scalar) {
        if (typeof scalar != 'number')
            throw new Error('Scalar is not a number')

        return new Vector(this.x / scalar, this.y / scalar)
    }

    // Vector-vector operations

    addVector(other) {
        if (!(other instanceof Vector))
            throw new Error('Other is not a vector')

        return new Vector(this.x + other.x, this.y + other.y)
    }
    substractVector(other) {
        if (!(other instanceof Vector))
            throw new Error('Other is not a vector')

        return new Vector(this.x - other.x, this.y - other.y)
    }
    multiplyVector(other) {
        if (!(other instanceof Vector))
            throw new Error('Other is not a vector')

        return this.x * other.x + this.y * other.y
    }

    // Vector operations

    length() {
        return Math.sqrt(this.multiplyVectorDot(this))
    }
    normalize() {
        return this.divideScalar(this.length())
    }
}

class Line {
    constructor(source, target) {
        if (!(source instanceof Vector))
            throw new Error('Source is not a vector')
        if (!(target instanceof Vector))
            throw new Error('Target is not a vector')

        this.source = source
        this.target = target

        this.direction = this.target.substractVector(this.source)
    }

    point(scalar) {
        if (typeof scalar != 'number')
            throw new Error('Scalar is not a number')

        return this.source.addVector(this.direction.multiplyScalar(scalar))
    }

    intersectScalar(other) {
        if (!(other instanceof Line))
            throw new Error('Other is not a line')

        // See https://en.wikipedia.org/wiki/Line%E2%80%93line_intersection
        
        const x1 = this.source.x
        const y1 = this.source.y

        const x2 = this.target.x
        const y2 = this.target.y

        const x3 = other.source.x
        const y3 = other.source.y

        const x4 = other.target.x
        const y4 = other.target.y

        const a = (x1 - x3) * (y3 - y4) - (y1 - y3) * (x3 - x4)
        const b = (x1 - x2) * (y3 - y4) - (y1 - y2) * (x3 - x4)

        return a / b
    }
    intersectVector(other) {
        return this.point(this.intersectScalar(other))
    }
}

class Bezier {

    calculateBezierPoint(t) {
        throw new Error("calculateBezierPoint() must be implemented by subclass");
    }

}

class CubicBezier extends Bezier {
    constructor(P0, P1, P2, P3) {
        super()
        // if (!(P0 instanceof Vector))
        //     throw new Error('P0 is not a vector')
        // if (!(P1 instanceof Vector))
        //     throw new Error('P1 is not a vector')
        // if (!(P2 instanceof Vector))
        //     throw new Error('P2 is not a vector')
        // if (!(P3 instanceof Vector))
        //     throw new Error('P3 is not a vector')

        this.P0 = P0
        this.P1 = P1
        this.P2 = P2
        this.P3 = P3
    }

    calculateBezierPoint(t) {
        if (typeof t != 'number')
            throw new Error('t is not a number')

        let x = Math.pow((1 - t), 3) * this.P0.x + 3 * Math.pow((1 - t), 2) * t * this.P1.x + 3 * (1 - t) * Math.pow(t, 2) * this.P2.x + Math.pow(t, 3) * this.P3.x;
        let y = Math.pow((1 - t), 3) * this.P0.y + 3 * Math.pow((1 - t), 2) * t * this.P1.y + 3 * (1 - t) * Math.pow(t, 2) * this.P2.y + Math.pow(t, 3) * this.P3.y;
        return { x, y };
    }
}

class QuadraticBezier extends Bezier {

    constructor(P0, P1, P2) {
        super()
        if (!(P0 instanceof Vector))
            throw new Error('P0 is not a vector')
        if (!(P1 instanceof Vector))
            throw new Error('P1 is not a vector')
        if (!(P2 instanceof Vector))
            throw new Error('P2 is not a vector')

        this.P0 = P0
        this.P1 = P1
        this.P2 = P2
    }

    calculateBezierPoint(t) {
        if (typeof t != 'number')
            throw new Error('t is not a number')

        let x = Math.pow((1 - t), 2) * this.P0.x + 2 * (1 - t) * t * this.P1.x + Math.pow(t, 2) * this.P2.x;
        let y = Math.pow((1 - t), 2) * this.P0.y + 2 * (1 - t) * t * this.P1.y + Math.pow(t, 2) * this.P2.y;
        return { x, y };
    }
}

// Model classes

class Intersection {
    constructor(key, x, y) {
        if (typeof x != 'number')
            throw new Error('X is not a number')
        if (typeof y != 'number')
            throw new Error('Y is not a number')

        this.key = key
        
        this.x = x
        this.y = y

        this.incoming = []
        this.outgoing = []
    }
}

class Segment {
    constructor(source, target, lanes) {
        if (!(source instanceof Intersection))
            throw new Error('Source is not an intersection')
        if (!(target instanceof Intersection))
            throw new Error('Target is not an intersection')
        if (typeof lanes != 'number')
            throw new Error('Lanes is not a number')

        this.source = source
        this.target = target

        this.source.outgoing.push(this)
        this.target.incoming.push(this)
        
        this.lanes = lanes
    }
}

// Allow for connected bezier paths
class BezierSpline {
    constructor() {
        this.beziers = []
    }

    addBezier(bezier) {
        if (!(bezier instanceof Bezier))
            throw new Error('Bezier is not an bezier')

        this.beziers.push(bezier)
    }

    checkIntegrity() {
        if (Object.keys(this.beziers).length === 0) 
            throw new Error('Beziers are empty')

        for (bezier in this.beziers) {
            // Consider C0 positional continuity for now
            // TODO check if one beziers start/end point are start/end points of another
        }
    }
}

class Infrastructure {
    constructor() {
        this.intersections = {}
        this.segments = []
    }
    addIntersection(key, x, y) {
        // Check key
        if (key in this.intersections)
            throw new Error('Intersection already exists')
        // Create, store, and return intersection
        return this.intersections[key] = new Intersection(key, x, y)
    }
    addSegment(source, target, lanes) {
        // Resolve source and target
        source = typeof source == 'string' ? this.intersections[source] : source
        target = typeof target == 'string' ? this.intersections[target] : target
        // Create, store, and return segment
        const segment = new Segment(source, target, lanes)
        this.segments.push(segment)
        return segment
    }
}

// Drawing functionality

// Basic Drawing Shapes

function drawCircle(context, posX, posY, radius, startAngle, endAngle, color, borderColor, filled) {
    context.save()
    context.beginPath()
    context.arc(posX, posY, radius, startAngle, endAngle)
    context.fillStyle = color
    context.lineWidth = 5;
    context.strokeStyle = borderColor
    if (filled) {
        context.fill();
    }
    context.restore()
}

function drawLine(context, startX, startY, endX, endY, lineWidth, strokeColor) {
    context.save()
    context.beginPath()
    context.moveTo(startX, startY)
    context.lineTo(endX, endY)
    context.lineWidth = lineWidth
    context.strokeStyle = strokeColor
    context.stroke()
    context.restore()
}

function drawBezier(context, bezier) {
    if (!(context instanceof CanvasRenderingContext2D))
        throw new Error('Context is not a canvas rendering context 2D')
    if (!(bezier instanceof Bezier))
        throw new Error('Bezier is not an bezier')
    
    context.save()
    context.lineWidth = 5
    context.strokeStyle = 'grey'
    context.beginPath();
    context.moveTo(bezier.P0.x, bezier.P0.y);

    for (let t = 0; t <= 1; t += 0.01) {
        let point = bezier.calculateBezierPoint(t);
        context.lineTo(point.x, point.y);
    }

    context.stroke();
    context.restore()
}

// Advanced (Model) Drawing

function drawBezierSegment(context, segment, segment2) {
    if (!(context instanceof CanvasRenderingContext2D))
        throw new Error('Context is not a canvas rendering context 2D')
    if (!(segment instanceof Segment))
        throw new Error('Segment is not a segment')
    if (!(segment2 instanceof Segment))
        throw new Error('Segment2 is not a segment')

    const bezier = new CubicBezier(segment.source, segment.target, segment.target, segment2.target)
    drawBezier(context, bezier)
}

function drawSegment(context, segment) {
    if (!(context instanceof CanvasRenderingContext2D))
        throw new Error('Context is not a canvas rendering context 2D')
    if (!(segment instanceof Segment))
        throw new Error('Segment is not a segment')

    drawLine(context, segment.source.x, segment.source.y, segment.target.x, segment.target.y, 5, 'black')
}

function drawIntersection(context, intersection) {
    if (!(context instanceof CanvasRenderingContext2D))
        throw new Error('Context is not a canvas rendering context 2D')
    if (!(intersection instanceof Intersection))
        throw new Error('Intersection is not an intersection')

    drawCircle(context, intersection.x, intersection.y, 20, 0, 2*Math.PI, 'blue', '', true)
}

function drawInfrastructure(context, infrastructure) {
    if (!(context instanceof CanvasRenderingContext2D))
        throw new Error('Context is not a canvas rendering context 2D')
    if (!(infrastructure instanceof Infrastructure))
        throw new Error('Infrastructure is not an infrastructure')

    for (const segment of infrastructure.segments)
        // Check if multiple segments regarding one intersection to calculate offset between them
        for (const segment2 of infrastructure.segments)
        {
            if (segment.target.key == segment2.source.key)
                drawBezierSegment(context, segment, segment2)
            else 
                drawSegment(context, segment)
        }

    for (const intersection of Object.values(infrastructure.intersections))
        drawIntersection(context, intersection)
}

// Deprecated
function drawExample() {

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


    // Define Test Bezier
    let P0 = new Vector(10, 10);
    let P1 = new Vector(100, 100);
    let P2 = new Vector(200, 100);
    let P3 = new Vector(300, 10);

    const bezier0 = new CubicBezier(P0, P1, P2, P3)

    context.save()
    context.lineWidth = 5
    context.strokeStyle = 'grey'
    drawBezier(context, bezier0)
    context.restore()
}


// Test cases

// Test case 0

const tc0 = new Infrastructure()
tc0.addIntersection('A', 0, 0)
tc0.addIntersection('B', 100, 100)
tc0.addSegment('A', 'B', 1)

// Test case 1

const tc1 = new Infrastructure()
tc1.addIntersection('A', 30, 30)
tc1.addIntersection('B', 100, 100)
tc1.addIntersection('C', 500, 220)
tc1.addIntersection('D', 100, 700)
tc1.addSegment('A', 'B', 1)
tc1.addSegment('A', 'C', 1)
tc1.addSegment('A', 'D', 1)



const canvas = document.getElementById("canvas")
const context = canvas.getContext("2d")

// Resize canvas
canvas.width = canvas.offsetWidth
canvas.height = canvas.offsetHeight

// Clear canvas
context.clearRect(0, 0, canvas.width, canvas.height)


let state = 0

function next() {
    state++
    draw()
}

function draw() {
    //drawExample(context)
    drawInfrastructure(context, tc1)
}


window.addEventListener('load', draw)
window.addEventListener('resize', draw)
window.addEventListener('keypress', next)