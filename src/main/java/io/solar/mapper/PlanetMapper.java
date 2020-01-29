package io.solar.mapper;

import io.solar.entity.Planet;
import io.solar.utils.db.DbMapper;
import io.solar.utils.db.SafeResultSet;

public class PlanetMapper implements DbMapper<Planet> {

    @Override
    public Planet map(SafeResultSet resultSet) {
        Planet out = new Planet();

        out.setId(resultSet.fetchLong("id"));
        out.setAldebo(resultSet.fetchFloat("aldebo"));
        out.setAphelion(resultSet.fetchLong("aphelion"));
        out.setAxialTilt(resultSet.getString("axial_tilt"));
        out.setEccentricity(resultSet.getString("eccentricity"));
        out.setEscapeVelocity(resultSet.getString("escape_velocity"));
        out.setInclination(resultSet.getString("inclination"));
        out.setMass(resultSet.getString("mass"));
        out.setMeanAnomaly(resultSet.fetchFloat("mean_anomaly"));
        out.setAngle(resultSet.fetchFloat("angle"));
        out.setMeanOrbitRadius(resultSet.getString("mean_orbit_radius"));
        out.setMeanRadius(resultSet.getString("mean_radius"));
        out.setTitle(resultSet.getString("title"));
        out.setType(resultSet.getString("type"));
        out.setOrbitalPeriod(resultSet.getString("orbital_period"));
        out.setPerihelion(resultSet.getString("perihelion"));
        out.setSiderealRotationPeriod(resultSet.getString("sidereal_rotation_period"));
        out.setSurfaceGravity(resultSet.getString("surface_gravity"));
        out.setSurfacePressure(resultSet.getString("surface_pressure"));
        out.setVolume(resultSet.getString("volume"));
        out.setParent(resultSet.fetchLong("parent"));

        return out;
    }
}
