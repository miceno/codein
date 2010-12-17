-- phpMyAdmin SQL Dump
-- version 3.2.5
-- http://www.phpmyadmin.net
--
-- Host: localhost
-- Generation Time: Dec 17, 2010 at 11:56 AM
-- Server version: 5.1.44
-- PHP Version: 5.2.13

SET SQL_MODE="NO_AUTO_VALUE_ON_ZERO";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- Database: `socialcoding`
--

-- --------------------------------------------------------

--
-- Table structure for table `entries`
--

DROP TABLE IF EXISTS `entries`;
CREATE TABLE IF NOT EXISTS `entries` (
  `entryId` int(11) NOT NULL AUTO_INCREMENT COMMENT 'Identify uniquelly an entry',
  `date` datetime NOT NULL COMMENT 'Date and time of the entry',
  `body` text NOT NULL COMMENT 'Body of the entry',
  `userId` int(11) NOT NULL COMMENT 'User that created the entry',
  UNIQUE KEY `id` (`entryId`),
  KEY `fecha` (`date`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COMMENT='Table for activity entries';

-- --------------------------------------------------------

--
-- Table structure for table `feeds`
--

DROP TABLE IF EXISTS `feeds`;
CREATE TABLE IF NOT EXISTS `feeds` (
  `feedId` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `url` tinytext NOT NULL,
  `frequency` int(10) unsigned NOT NULL,
  KEY `urlindex` (`url`(333)),
  KEY `feedid` (`feedId`)
) ENGINE=MyISAM  DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `userfeeds`
--

DROP TABLE IF EXISTS `userfeeds`;
CREATE TABLE IF NOT EXISTS `userfeeds` (
  `userId` int(11) NOT NULL,
  `feedId` int(11) NOT NULL,
  UNIQUE KEY `user feeds` (`userId`,`feedId`),
  KEY `feedid` (`feedId`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
CREATE TABLE IF NOT EXISTS `users` (
  `userId` int(11) NOT NULL AUTO_INCREMENT COMMENT 'Unique id',
  `UUID` varchar(32) NOT NULL COMMENT 'UUID of the user, may be duplicate across domains',
  `domain` varchar(255) DEFAULT NULL COMMENT 'Domain name',
  UNIQUE KEY `id` (`UUID`,`domain`),
  KEY `userid` (`userId`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
