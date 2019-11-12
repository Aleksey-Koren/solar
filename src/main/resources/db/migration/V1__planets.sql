create table planets (
    id int not null AUTO_INCREMENT,
    aldebo float,
    aphelion int,
    axialTilt varchar(255),
    eccentricity varchar(255),
    escapeVelocity varchar(255),
    inclination varchar(255),
    mass varchar(255),
    meanAnomaly float,
    meanOrbitRadius varchar(255),
    meanRadius varchar(255),
    title varchar(255),
    orbitalPeriod varchar(255),
    perihelion varchar(255),
    siderealRotationPeriod varchar(255),
    surfaceGravity varchar(255),
    surfacePressure varchar(255),
    volume varchar(255),
    parent int,
    primary key (id),
    foreign key (parent) references planets(id)
)CHARACTER SET utf8;

alter table planets add column angle float;

alter table planets add column type varchar(255);

alter table planets CHANGE column axialTilt axial_tilt varchar(255);
alter table planets CHANGE column escapeVelocity escape_velocity varchar(255);
alter table planets CHANGE column meanAnomaly mean_anomaly varchar(255);
alter table planets CHANGE column meanOrbitRadius mean_orbit_radius varchar(255);
alter table planets CHANGE column meanRadius mean_radius varchar(255);
alter table planets CHANGE column orbitalPeriod orbital_period varchar(255);
alter table planets CHANGE column siderealRotationPeriod sidereal_rotation_period varchar(255);
alter table planets CHANGE column surfaceGravity surface_gravity varchar(255);
alter table planets CHANGE column surfacePressure surface_pressure varchar(255);

create table products (
                          id int not null AUTO_INCREMENT,
                          title varchar(255),
                          image varchar(255),
                          bulk float,
                          mass float,
                          price float,
                          primary key (id)
)CHARACTER SET utf8;

