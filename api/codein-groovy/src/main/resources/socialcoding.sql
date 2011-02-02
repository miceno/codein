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

 Date: 02/02/2011 11:07:24 AM
*/

SET NAMES utf8;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
--  Table structure for `Entry`
-- ----------------------------
DROP TABLE IF EXISTS `Entry`;
CREATE TABLE `Entry` (
  `entryId` int(11) NOT NULL AUTO_INCREMENT COMMENT 'Identify uniquelly an entry',
  `id` tinytext NOT NULL COMMENT 'Text ID',
  `authorId` varchar(32) NOT NULL COMMENT 'User that created the entry',
  `authorLink` tinytext NOT NULL,
  `title` varchar(255) NOT NULL COMMENT 'Title of the entry',
  `link` tinytext NOT NULL COMMENT 'Link to the original entry',
  `updated` bigint(15) DEFAULT NULL COMMENT 'Updated Date and time of the entry',
  `published` bigint(15) DEFAULT NULL COMMENT 'Published date',
  `content` text NOT NULL COMMENT 'Body of the entry',
  `source` varchar(255) DEFAULT NULL COMMENT 'Source of the entry',
  `ownerId` varchar(255) NOT NULL COMMENT 'User that requested the entry',
  `ownerDomain` varchar(64) NOT NULL COMMENT 'Domain of the user that requested the entry',
  PRIMARY KEY (`entryId`),
  UNIQUE KEY `id` (`entryId`),
  KEY `fecha` (`updated`),
  KEY `titulo` (`title`)
) ENGINE=MyISAM AUTO_INCREMENT=2121 DEFAULT CHARSET=utf8 COMMENT='Table for activity entries';

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
  `UUID` varchar(64) NOT NULL COMMENT 'UUID of the user, may be duplicate across domains',
  `domain` varchar(255) DEFAULT '' COMMENT 'Domain name',
  `urls` text NOT NULL COMMENT 'URL of a feed',
  PRIMARY KEY (`userId`),
  UNIQUE KEY `id` (`UUID`,`domain`),
  KEY `userid` (`userId`)
) ENGINE=MyISAM AUTO_INCREMENT=26 DEFAULT CHARSET=utf8;

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

