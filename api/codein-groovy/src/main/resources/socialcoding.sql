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

 Date: 01/20/2011 14:36:22 PM
*/

SET NAMES utf8;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
--  Table structure for `Entry`
-- ----------------------------
DROP TABLE IF EXISTS `Entry`;
CREATE TABLE `Entry` (
  `entryId` int(11) NOT NULL AUTO_INCREMENT COMMENT 'Identify uniquelly an entry',
  `userId` varchar(32) NOT NULL COMMENT 'User that created the entry',
  `title` varchar(255) NOT NULL COMMENT 'Title of the entry',
  `link` tinytext NOT NULL COMMENT 'Link to the original entry',
  `updated` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Updated Date and time of the entry',
  `published` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `content` text NOT NULL COMMENT 'Body of the entry',
  `source` varchar(255) NOT NULL COMMENT 'Source of the entry',
  UNIQUE KEY `id` (`entryId`),
  KEY `fecha` (`updated`)
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
) ENGINE=MyISAM AUTO_INCREMENT=10 DEFAULT CHARSET=utf8;

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

