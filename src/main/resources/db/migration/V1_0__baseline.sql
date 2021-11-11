-- --------------------------------------------------------
-- Host:                         127.0.0.1
-- Server version:               10.6.5-MariaDB - mariadb.org binary distribution
-- Server OS:                    Win64
-- HeidiSQL Version:             11.3.0.6295
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;


-- Dumping database structure for solar
DROP DATABASE IF EXISTS `solar`;
CREATE DATABASE IF NOT EXISTS `solar` /*!40100 DEFAULT CHARACTER SET utf8mb3 */;
USE `solar`;

-- Dumping structure for table solar.flyway_schema_history
DROP TABLE IF EXISTS `flyway_schema_history`;
CREATE TABLE IF NOT EXISTS `flyway_schema_history` (
  `installed_rank` int(11) NOT NULL,
  `version` varchar(50) DEFAULT NULL,
  `description` varchar(200) NOT NULL,
  `type` varchar(20) NOT NULL,
  `script` varchar(1000) NOT NULL,
  `checksum` int(11) DEFAULT NULL,
  `installed_by` varchar(100) NOT NULL,
  `installed_on` timestamp NOT NULL DEFAULT current_timestamp(),
  `execution_time` int(11) NOT NULL,
  `success` tinyint(1) NOT NULL,
  PRIMARY KEY (`installed_rank`),
  KEY `flyway_schema_history_s_idx` (`success`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Data exporting was unselected.

-- Dumping structure for table solar.inventory_item
DROP TABLE IF EXISTS `inventory_item`;
CREATE TABLE IF NOT EXISTS `inventory_item` (
  `id` int(11) NOT NULL,
  `item_type` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Data exporting was unselected.

-- Dumping structure for table solar.objects
DROP TABLE IF EXISTS `objects`;
CREATE TABLE IF NOT EXISTS `objects` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `planet` int(11) DEFAULT NULL,
  `population` int(11) DEFAULT NULL,
  `fraction` varchar(255) DEFAULT NULL,
  `title` varchar(255) DEFAULT NULL,
  `x` float DEFAULT NULL,
  `y` float DEFAULT NULL,
  `aphelion` float DEFAULT NULL,
  `orbital_period` float DEFAULT NULL,
  `angle` float DEFAULT NULL,
  `hull_id` int(11) DEFAULT NULL,
  `user_id` int(11) DEFAULT NULL,
  `active` int(11) DEFAULT NULL,
  `durability` int(11) DEFAULT NULL,
  `attached_to_ship` int(11) DEFAULT NULL,
  `attached_to_socket` int(11) DEFAULT NULL,
  `status` enum('in_space','attached_to','in_container') DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `planet` (`planet`),
  KEY `hull_id` (`hull_id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `objects_ibfk_1` FOREIGN KEY (`planet`) REFERENCES `planet` (`id`),
  CONSTRAINT `objects_ibfk_2` FOREIGN KEY (`hull_id`) REFERENCES `object_type_description` (`id`),
  CONSTRAINT `objects_ibfk_3` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

-- Data exporting was unselected.

-- Dumping structure for table solar.object_modification
DROP TABLE IF EXISTS `object_modification`;
CREATE TABLE IF NOT EXISTS `object_modification` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `item_id` int(11) NOT NULL,
  `modification_id` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `item_id` (`item_id`),
  KEY `modification_id` (`modification_id`),
  CONSTRAINT `object_modification_ibfk_1` FOREIGN KEY (`item_id`) REFERENCES `object_type_description` (`id`),
  CONSTRAINT `object_modification_ibfk_2` FOREIGN KEY (`modification_id`) REFERENCES `object_modification_type` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

-- Data exporting was unselected.

-- Dumping structure for table solar.object_modification_type
DROP TABLE IF EXISTS `object_modification_type`;
CREATE TABLE IF NOT EXISTS `object_modification_type` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `title` varchar(255) DEFAULT NULL,
  `data` varchar(255) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

-- Data exporting was unselected.

-- Dumping structure for table solar.object_type
DROP TABLE IF EXISTS `object_type`;
CREATE TABLE IF NOT EXISTS `object_type` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `title` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

-- Data exporting was unselected.

-- Dumping structure for table solar.object_type_description
DROP TABLE IF EXISTS `object_type_description`;
CREATE TABLE IF NOT EXISTS `object_type_description` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `inventory_type` int(11) DEFAULT NULL,
  `title` varchar(255) DEFAULT NULL,
  `power_min` float DEFAULT NULL,
  `power_max` float DEFAULT NULL,
  `power_degradation` float DEFAULT NULL,
  `cooldown` float DEFAULT NULL,
  `distance` float DEFAULT NULL,
  `energy_consumption` int(11) DEFAULT NULL,
  `durability` int(11) DEFAULT NULL,
  `description` text DEFAULT NULL,
  `mass` int(11) DEFAULT NULL,
  `price` int(11) DEFAULT NULL,
  `type` enum('station','ship','item','asteroid') DEFAULT NULL,
  `sub_type` enum('static','mining','military','science','production','asylum') DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `inventory_type` (`inventory_type`),
  CONSTRAINT `object_type_description_ibfk_1` FOREIGN KEY (`inventory_type`) REFERENCES `object_type` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

-- Data exporting was unselected.

-- Dumping structure for table solar.object_type_socket
DROP TABLE IF EXISTS `object_type_socket`;
CREATE TABLE IF NOT EXISTS `object_type_socket` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `item_id` int(11) NOT NULL,
  `item_type_id` int(11) NOT NULL,
  `sort_order` int(11) DEFAULT NULL,
  `alias` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `item_id` (`item_id`),
  KEY `item_type_id` (`item_type_id`),
  CONSTRAINT `object_type_socket_ibfk_1` FOREIGN KEY (`item_id`) REFERENCES `object_type_description` (`id`),
  CONSTRAINT `object_type_socket_ibfk_2` FOREIGN KEY (`item_type_id`) REFERENCES `object_type` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

-- Data exporting was unselected.

-- Dumping structure for table solar.permission
DROP TABLE IF EXISTS `permission`;
CREATE TABLE IF NOT EXISTS `permission` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `permission_type` int(11) DEFAULT NULL,
  `user_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `permission_type` (`permission_type`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `permission_ibfk_1` FOREIGN KEY (`permission_type`) REFERENCES `permission_type` (`id`),
  CONSTRAINT `permission_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

-- Data exporting was unselected.

-- Dumping structure for table solar.permission_type
DROP TABLE IF EXISTS `permission_type`;
CREATE TABLE IF NOT EXISTS `permission_type` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `title` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

-- Data exporting was unselected.

-- Dumping structure for table solar.planet
DROP TABLE IF EXISTS `planet`;
CREATE TABLE IF NOT EXISTS `planet` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `aldebo` float DEFAULT NULL,
  `aphelion` int(11) DEFAULT NULL,
  `axial_tilt` varchar(255) DEFAULT NULL,
  `eccentricity` varchar(255) DEFAULT NULL,
  `escape_velocity` varchar(255) DEFAULT NULL,
  `inclination` varchar(255) DEFAULT NULL,
  `mass` varchar(255) DEFAULT NULL,
  `mean_anomaly` varchar(255) DEFAULT NULL,
  `mean_orbit_radius` varchar(255) DEFAULT NULL,
  `mean_radius` varchar(255) DEFAULT NULL,
  `title` varchar(255) DEFAULT NULL,
  `orbital_period` varchar(255) DEFAULT NULL,
  `perihelion` varchar(255) DEFAULT NULL,
  `sidereal_rotation_period` varchar(255) DEFAULT NULL,
  `surface_gravity` varchar(255) DEFAULT NULL,
  `surface_pressure` varchar(255) DEFAULT NULL,
  `volume` varchar(255) DEFAULT NULL,
  `parent` int(11) DEFAULT NULL,
  `angle` float DEFAULT NULL,
  `type` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `parent` (`parent`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

-- Data exporting was unselected.

-- Dumping structure for table solar.product
DROP TABLE IF EXISTS `product`;
CREATE TABLE IF NOT EXISTS `product` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `title` varchar(255) DEFAULT NULL,
  `image` varchar(255) DEFAULT NULL,
  `bulk` float DEFAULT NULL,
  `mass` float DEFAULT NULL,
  `price` float DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

-- Data exporting was unselected.

-- Dumping structure for table solar.production
DROP TABLE IF EXISTS `production`;
CREATE TABLE IF NOT EXISTS `production` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `power` float DEFAULT NULL,
  `product` int(11) DEFAULT NULL,
  `station` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `product` (`product`),
  KEY `station` (`station`),
  CONSTRAINT `production_ibfk_1` FOREIGN KEY (`product`) REFERENCES `product` (`id`),
  CONSTRAINT `production_ibfk_2` FOREIGN KEY (`station`) REFERENCES `objects` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

-- Data exporting was unselected.

-- Dumping structure for table solar.user
DROP TABLE IF EXISTS `user`;
CREATE TABLE IF NOT EXISTS `user` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `login` varchar(255) DEFAULT NULL,
  `title` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `money` int(11) DEFAULT 0,
  `hack_block` datetime DEFAULT NULL,
  `hack_attempts` int(11) DEFAULT NULL,
  `planet` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `user_planet_fk_idx` (`planet`),
  CONSTRAINT `user_planet_fk` FOREIGN KEY (`planet`) REFERENCES `planet` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb3;

-- Data exporting was unselected.

/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;
