var NavigationUtils = {
    /**
     * @param map {SolarMap}
     * @param e {MouseEvent}
     */
    layCourse: function(map, e) {
        var spaceShip = map.context.spaceShip;
        if(!spaceShip) {
            console.log("can't lay course, spaceship not found")
            return;
        }
        var destination = map.getAbsPoint(e.clientX, e.clientY);
        var center = {
            x: spaceShip.x + ((destination.x - spaceShip.x) / 2),
            y: spaceShip.y + ((destination.y - spaceShip.y) / 2)
        };
        destination.type = 'point';
        center.type = 'point';
        map.staticObjects = [destination, center];
        //distance to center in kkm
        var distanceVector = {x: (center.x - spaceShip.x), y: (center.y - spaceShip.y)};
        //distance in kkm
        var distance = Math.sqrt(Math.pow(distanceVector.x, 2) + Math.pow(distanceVector.y, 2));
        //max linear acceleration in kkm/sec^2
        var spaceShipEnginePower = 100 / 1000;
        var vectorPositiveSpeed = {
            x: distanceVector.x * (spaceShipEnginePower / distance),
            y: distanceVector.y * (spaceShipEnginePower / distance),
        };
        function calculateTime(dist, acc) {
            return Math.sqrt(2 * dist / acc);
        }
        //time in seconds
        var time = calculateTime(distanceVector.x, vectorPositiveSpeed.x);
        if(time === 0) {
            time = calculateTime(distanceVector.y, vectorPositiveSpeed.y);
        }
        if(time === 0) {
            return;
        }
        var tasks = [
            {accelerationX: vectorPositiveSpeed.x, accelerationY: vectorPositiveSpeed.y, time: time, objectId: spaceShip.id},
            {accelerationX: -vectorPositiveSpeed.x, accelerationY: -vectorPositiveSpeed.y, time: time, objectId: spaceShip.id},
        ];
        Rest.doPost("api/navigate/course", tasks);
    }
}