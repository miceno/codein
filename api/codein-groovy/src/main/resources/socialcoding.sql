/*
 Navicat Premium Data Transfer

 Source Server         : local
 Source Server Type    : MySQL
 Source Server Version : 50144
 Source Host           : localhost
 Source Database       : socialcoding

 Target Server Type    : MySQL
 Target Server Version : 50144
 File Encoding         : utf-8

 Date: 01/18/2011 14:00:01 PM
*/

SET NAMES utf8;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
--  Table structure for `Entry`
-- ----------------------------
DROP TABLE IF EXISTS `Entry`;
CREATE TABLE `Entry` (
  `entryId` int(11) NOT NULL AUTO_INCREMENT COMMENT 'Identify uniquelly an entry',
  `title` varchar(255) NOT NULL,
  `date` datetime NOT NULL COMMENT 'Date and time of the entry',
  `body` text NOT NULL COMMENT 'Body of the entry',
  `source` varchar(255) NOT NULL,
  `userId` int(11) NOT NULL COMMENT 'User that created the entry',
  `link` tinytext NOT NULL,
  UNIQUE KEY `id` (`entryId`),
  KEY `fecha` (`date`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COMMENT='Table for activity entries';

-- ----------------------------
--  Table structure for `Feed`
-- ----------------------------
DROP TABLE IF EXISTS `Feed`;
CREATE TABLE `Feed` (
  `feedId` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `url` tinytext NOT NULL,
  `frequency` int(10) unsigned NOT NULL,
  KEY `urlindex` (`url`(333)),
  KEY `feedid` (`feedId`)
) ENGINE=MyISAM AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;

-- ----------------------------
--  Table structure for `User`
-- ----------------------------
DROP TABLE IF EXISTS `User`;
CREATE TABLE `User` (
  `userId` int(11) NOT NULL AUTO_INCREMENT COMMENT 'Unique id',
  `UUID` varchar(32) NOT NULL COMMENT 'UUID of the user, may be duplicate across domains',
  `domain` varchar(255) DEFAULT '' COMMENT 'Domain name',
  `urls` text NOT NULL COMMENT 'URL of a feed',
  UNIQUE KEY `id` (`UUID`,`domain`),
  KEY `userid` (`userId`)
) ENGINE=MyISAM AUTO_INCREMENT=9 DEFAULT CHARSET=utf8;

-- ----------------------------
--  Table structure for `UserFeed`
-- ----------------------------
DROP TABLE IF EXISTS `UserFeed`;
CREATE TABLE `UserFeed` (
  `userId` int(11) NOT NULL,
  `feedId` int(11) NOT NULL,
  UNIQUE KEY `user feeds` (`userId`,`feedId`),
  KEY `feedid` (`feedId`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- ----------------------------
--  View structure for `userfeedview`
-- ----------------------------
DROP VIEW IF EXISTS `userfeedview`;
CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER VIEW `userfeedview` AS select `User`.`userId` AS `userId`,`Feed`.`feedId` AS `feedId`,`User`.`UUID` AS `UUID`,`User`.`domain` AS `domain`,`Feed`.`name` AS `name`,`Feed`.`url` AS `url`,`Feed`.`frequency` AS `frequency` from ((`UserFeed` join `User`) join `Feed` on(((`UserFeed`.`userId` = `User`.`userId`) and (`UserFeed`.`feedId` = `Feed`.`feedId`))));

