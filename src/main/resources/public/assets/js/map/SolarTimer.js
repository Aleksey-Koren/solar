var SolarTimer = {
    EPOCH: Date.UTC(2019, 11, 12, 0, 0, 0, 0),
    DELTA: null,
    iteration: function(){
        SolarTimer.DELTA = Math.PI * 2 * (new Date().getTime() - SolarTimer.EPOCH) / (1000 * 60 * 60 * 24);
    },
    angle: function(planet) {
        var da = SolarTimer.DELTA / planet.orbitalPeriod;
        return isFinite(da) ? planet.angle + da : 0;
    }
};
