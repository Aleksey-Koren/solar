package io.solar.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlanetDto {

    private Long id;
    private Float aldebo;
    private Float aphelion; //TODO it was Long.  Might be that it will bring some troubles at frontend)))
    private String axialTilt;
    private String eccentricity;
    private String escapeVelocity;
    private String inclination;
    private String mass;
    private Float meanAnomaly;
    private String meanOrbitRadius;
    private String meanRadius;
    private String title;
    private Float orbitalPeriod;
    private String perihelion;
    private String siderealRotationPeriod;
    private String surfaceGravity;
    private String surfacePressure;
    private String volume;
    private Long parent;
    private Float angle;
    private String type;

    public static PlanetDtoBuilder builder() {
        return new PlanetDtoBuilder();
    }

    public static class PlanetDtoBuilder {
        private Long id;
        private Float aldebo;
        private Float aphelion;
        private String axialTilt;
        private String eccentricity;
        private String escapeVelocity;
        private String inclination;
        private String mass;
        private Float meanAnomaly;
        private String meanOrbitRadius;
        private String meanRadius;
        private String title;
        private Float orbitalPeriod;
        private String perihelion;
        private String siderealRotationPeriod;
        private String surfaceGravity;
        private String surfacePressure;
        private String volume;
        private Long parent;
        private Float angle;
        private String type;

        PlanetDtoBuilder() {
        }

        public PlanetDtoBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public PlanetDtoBuilder aldebo(Float aldebo) {
            this.aldebo = aldebo;
            return this;
        }

        public PlanetDtoBuilder aphelion(Float aphelion) {
            this.aphelion = aphelion;
            return this;
        }

        public PlanetDtoBuilder axialTilt(String axialTilt) {
            this.axialTilt = axialTilt;
            return this;
        }

        public PlanetDtoBuilder eccentricity(String eccentricity) {
            this.eccentricity = eccentricity;
            return this;
        }

        public PlanetDtoBuilder escapeVelocity(String escapeVelocity) {
            this.escapeVelocity = escapeVelocity;
            return this;
        }

        public PlanetDtoBuilder inclination(String inclination) {
            this.inclination = inclination;
            return this;
        }

        public PlanetDtoBuilder mass(String mass) {
            this.mass = mass;
            return this;
        }

        public PlanetDtoBuilder meanAnomaly(Float meanAnomaly) {
            this.meanAnomaly = meanAnomaly;
            return this;
        }

        public PlanetDtoBuilder meanOrbitRadius(String meanOrbitRadius) {
            this.meanOrbitRadius = meanOrbitRadius;
            return this;
        }

        public PlanetDtoBuilder meanRadius(String meanRadius) {
            this.meanRadius = meanRadius;
            return this;
        }

        public PlanetDtoBuilder title(String title) {
            this.title = title;
            return this;
        }

        public PlanetDtoBuilder orbitalPeriod(Float orbitalPeriod) {
            this.orbitalPeriod = orbitalPeriod;
            return this;
        }

        public PlanetDtoBuilder perihelion(String perihelion) {
            this.perihelion = perihelion;
            return this;
        }

        public PlanetDtoBuilder siderealRotationPeriod(String siderealRotationPeriod) {
            this.siderealRotationPeriod = siderealRotationPeriod;
            return this;
        }

        public PlanetDtoBuilder surfaceGravity(String surfaceGravity) {
            this.surfaceGravity = surfaceGravity;
            return this;
        }

        public PlanetDtoBuilder surfacePressure(String surfacePressure) {
            this.surfacePressure = surfacePressure;
            return this;
        }

        public PlanetDtoBuilder volume(String volume) {
            this.volume = volume;
            return this;
        }

        public PlanetDtoBuilder parent(Long parent) {
            this.parent = parent;
            return this;
        }

        public PlanetDtoBuilder angle(Float angle) {
            this.angle = angle;
            return this;
        }

        public PlanetDtoBuilder type(String type) {
            this.type = type;
            return this;
        }

        public PlanetDto build() {
            return new PlanetDto(id, aldebo, aphelion, axialTilt, eccentricity, escapeVelocity, inclination, mass, meanAnomaly, meanOrbitRadius, meanRadius, title, orbitalPeriod, perihelion, siderealRotationPeriod, surfaceGravity, surfacePressure, volume, parent, angle, type);
        }

        public String toString() {
            return "PlanetDto.PlanetDtoBuilder(id=" + this.id + ", aldebo=" + this.aldebo + ", aphelion=" + this.aphelion + ", axialTilt=" + this.axialTilt + ", eccentricity=" + this.eccentricity + ", escapeVelocity=" + this.escapeVelocity + ", inclination=" + this.inclination + ", mass=" + this.mass + ", meanAnomaly=" + this.meanAnomaly + ", meanOrbitRadius=" + this.meanOrbitRadius + ", meanRadius=" + this.meanRadius + ", title=" + this.title + ", orbitalPeriod=" + this.orbitalPeriod + ", perihelion=" + this.perihelion + ", siderealRotationPeriod=" + this.siderealRotationPeriod + ", surfaceGravity=" + this.surfaceGravity + ", surfacePressure=" + this.surfacePressure + ", volume=" + this.volume + ", parent=" + this.parent + ", angle=" + this.angle + ", type=" + this.type + ")";
        }
    }
}