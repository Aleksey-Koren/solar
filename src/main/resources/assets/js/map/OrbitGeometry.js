var OrbitGeometry = {
    _lineIntersectCircle: function(line, circle) {
        var pointB = line.b;
        var pointA = line.a;
        if(pointA.x === pointB.x && pointA.y === pointB.y) {
            if (Math.pow(pointA.x - circle.x, 2) + Math.pow(pointA.y - circle.y, 2) === circle.r * circle.r) {
                return [pointA];
            } else {
                return [];
            }
        }
        var baX = pointB.x - pointA.x;
        var baY = pointB.y - pointA.y;
        var caX = circle.x - pointA.x;
        var caY = circle.y - pointA.y;

        var a = baX * baX + baY * baY;
        var bBy2 = baX * caX + baY * caY;
        var c = caX * caX + caY * caY - circle.r * circle.r;

        var pBy2 = bBy2 / a;
        var q = c / a;

        var disc = pBy2 * pBy2 - q;
        if(disc <= 0.0001 && disc >= -0.0001) {
            disc = 0;
        }
        if (disc < 0) {
            return [];
        }
        var tmpSqrt = Math.sqrt(disc);
        var abScalingFactor1 = -pBy2 + tmpSqrt;
        var abScalingFactor2 = -pBy2 - tmpSqrt;

        var p1 = {x: pointA.x - baX * abScalingFactor1, y: pointA.y - baY * abScalingFactor1};
        var out1 = OrbitGeometry._getMiddle(pointA, pointB, p1);
        if (disc === 0) {
            if(out1 === p1) {
                return [p1];
            } else {
                return [];
            }
        }
        var p2 = {x: pointA.x - baX * abScalingFactor2, y: pointA.y - baY * abScalingFactor2};
        var out2 = OrbitGeometry._getMiddle(pointA, pointB, p2);
        if(out1 === p1) {
            if(out2 === p2) {
                return [p1, p2];
            } else {
                return [p1];
            }
        } else {
            if(out2 === p2) {
                return [p2];
            } else {
                return [];
            }
        }
    },
    getIntersections: function(planet, lines, zero) {
        var circle = {x:zero.x, y: zero.y, r: planet.aphelion};
        var out = [];
        for (var i = 0; i < 4; i++) {
            var line = lines[i];
            var points = OrbitGeometry._lineIntersectCircle(line, circle);
            if (points.length > 0) {
                out = out.concat(points);
            }
        }
        return out;
    },
    _getMiddle: function(pointA, pointB, pointC){
        var ab = OrbitGeometry._getDistance(pointA.x, pointA.y, pointB.x, pointB.y);
        var bc = OrbitGeometry._getDistance(pointB.x, pointB.y, pointC.x, pointC.y);
        var ca = OrbitGeometry._getDistance(pointC.x, pointC.y, pointA.x, pointA.y);
        if(ab >= bc && ab >= ca) {
            return pointC;
        } else if(ca >= ab && ca >= bc) {
            return pointB;
        } else {
            return pointA;
        }
    },
    _getDistance: function(x1, y1, x2, y2) {
        return Math.sqrt(Math.pow(x2-x1, 2) + Math.pow(y2-y1, 2));
    }
};