-- MySQL dump 10.13  Distrib 8.0.27, for Win64 (x86_64)
--
-- Host: localhost    Database: solar
-- ------------------------------------------------------
-- Server version	8.0.27

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `flyway_schema_history`
--

DROP TABLE IF EXISTS `flyway_schema_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `flyway_schema_history` (
  `installed_rank` int NOT NULL,
  `version` varchar(50) DEFAULT NULL,
  `description` varchar(200) NOT NULL,
  `type` varchar(20) NOT NULL,
  `script` varchar(1000) NOT NULL,
  `checksum` int DEFAULT NULL,
  `installed_by` varchar(100) NOT NULL,
  `installed_on` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `execution_time` int NOT NULL,
  `success` tinyint(1) NOT NULL,
  PRIMARY KEY (`installed_rank`),
  KEY `flyway_schema_history_s_idx` (`success`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `inventory_item`
--

DROP TABLE IF EXISTS `inventory_item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `inventory_item` (
  `id` int NOT NULL,
  `item_type` int NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `object_modification`
--

DROP TABLE IF EXISTS `object_modification`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `object_modification` (
  `id` int NOT NULL AUTO_INCREMENT,
  `item_id` int NOT NULL,
  `modification_id` int NOT NULL,
  PRIMARY KEY (`id`),
  KEY `item_id` (`item_id`),
  KEY `modification_id` (`modification_id`),
  CONSTRAINT `object_modification_ibfk_1` FOREIGN KEY (`item_id`) REFERENCES `object_type_description` (`id`),
  CONSTRAINT `object_modification_ibfk_2` FOREIGN KEY (`modification_id`) REFERENCES `object_modification_type` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `object_modification_type`
--

DROP TABLE IF EXISTS `object_modification_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `object_modification_type` (
  `id` int NOT NULL AUTO_INCREMENT,
  `title` varchar(255) DEFAULT NULL,
  `data` varchar(255) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `object_type`
--

DROP TABLE IF EXISTS `object_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `object_type` (
  `id` int NOT NULL AUTO_INCREMENT,
  `title` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `object_type_description`
--

DROP TABLE IF EXISTS `object_type_description`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `object_type_description` (
  `id` int NOT NULL AUTO_INCREMENT,
  `inventory_type` int DEFAULT NULL,
  `title` varchar(255) DEFAULT NULL,
  `power_min` float DEFAULT NULL,
  `power_max` float DEFAULT NULL,
  `power_degradation` float DEFAULT NULL,
  `cooldown` float DEFAULT NULL,
  `distance` float DEFAULT NULL,
  `energy_consumption` int DEFAULT NULL,
  `durability` int DEFAULT NULL,
  `description` text,
  `mass` int DEFAULT NULL,
  `price` int DEFAULT NULL,
  `type` enum('station','ship','item','asteroid') DEFAULT NULL,
  `sub_type` enum('static','mining','military','science','production','asylum') DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `inventory_type` (`inventory_type`),
  CONSTRAINT `object_type_description_ibfk_1` FOREIGN KEY (`inventory_type`) REFERENCES `object_type` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `object_type_socket`
--

DROP TABLE IF EXISTS `object_type_socket`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `object_type_socket` (
  `id` int NOT NULL AUTO_INCREMENT,
  `item_id` int NOT NULL,
  `item_type_id` int NOT NULL,
  `sort_order` int DEFAULT NULL,
  `alias` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `item_id` (`item_id`),
  KEY `item_type_id` (`item_type_id`),
  CONSTRAINT `object_type_socket_ibfk_1` FOREIGN KEY (`item_id`) REFERENCES `object_type_description` (`id`),
  CONSTRAINT `object_type_socket_ibfk_2` FOREIGN KEY (`item_type_id`) REFERENCES `object_type` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `objects`
--

DROP TABLE IF EXISTS `objects`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `objects` (
  `id` int NOT NULL AUTO_INCREMENT,
  `planet` int DEFAULT NULL,
  `population` int DEFAULT NULL,
  `fraction` varchar(255) DEFAULT NULL,
  `title` varchar(255) DEFAULT NULL,
  `x` float DEFAULT NULL,
  `y` float DEFAULT NULL,
  `aphelion` float DEFAULT NULL,
  `orbital_period` float DEFAULT NULL,
  `angle` float DEFAULT NULL,
  `hull_id` int DEFAULT NULL,
  `user_id` int DEFAULT NULL,
  `active` int DEFAULT NULL,
  `durability` int DEFAULT NULL,
  `attached_to_ship` int DEFAULT NULL,
  `attached_to_socket` int DEFAULT NULL,
  `status` enum('in_space','attached_to','in_container') DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `planet` (`planet`),
  KEY `hull_id` (`hull_id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `objects_ibfk_1` FOREIGN KEY (`planet`) REFERENCES `planet` (`id`),
  CONSTRAINT `objects_ibfk_2` FOREIGN KEY (`hull_id`) REFERENCES `object_type_description` (`id`),
  CONSTRAINT `objects_ibfk_3` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `permission`
--

DROP TABLE IF EXISTS `permission`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `permission` (
  `id` int NOT NULL AUTO_INCREMENT,
  `permission_type` int DEFAULT NULL,
  `user_id` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `permission_type` (`permission_type`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `permission_ibfk_1` FOREIGN KEY (`permission_type`) REFERENCES `permission_type` (`id`),
  CONSTRAINT `permission_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `permission_type`
--

DROP TABLE IF EXISTS `permission_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `permission_type` (
  `id` int NOT NULL AUTO_INCREMENT,
  `title` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `planet`
--

DROP TABLE IF EXISTS `planet`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `planet` (
  `id` int NOT NULL AUTO_INCREMENT,
  `aldebo` float DEFAULT NULL,
  `aphelion` int DEFAULT NULL,
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
  `parent` int DEFAULT NULL,
  `angle` float DEFAULT NULL,
  `type` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `parent` (`parent`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `product`
--

DROP TABLE IF EXISTS `product`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `product` (
  `id` int NOT NULL AUTO_INCREMENT,
  `title` varchar(255) DEFAULT NULL,
  `image` varchar(255) DEFAULT NULL,
  `bulk` float DEFAULT NULL,
  `mass` float DEFAULT NULL,
  `price` float DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `production`
--

DROP TABLE IF EXISTS `production`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `production` (
  `id` int NOT NULL AUTO_INCREMENT,
  `power` float DEFAULT NULL,
  `product` int DEFAULT NULL,
  `station` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `product` (`product`),
  KEY `station` (`station`),
  CONSTRAINT `production_ibfk_1` FOREIGN KEY (`product`) REFERENCES `product` (`id`),
  CONSTRAINT `production_ibfk_2` FOREIGN KEY (`station`) REFERENCES `objects` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user` (
  `id` int NOT NULL AUTO_INCREMENT,
  `login` varchar(255) DEFAULT NULL,
  `title` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `money` int DEFAULT '0',
  `hack_block` datetime DEFAULT NULL,
  `hack_attempts` int DEFAULT NULL,
  `planet` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `user_planet_fk_idx` (`planet`),
  CONSTRAINT `user_planet_fk` FOREIGN KEY (`planet`) REFERENCES `planet` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2021-11-09 17:44:03
