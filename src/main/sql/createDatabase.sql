-- Open terminal and start mySQL;
-- mysql -u root -p

-- Run this file;
-- source /<path to file>/createDatabase.sql

-- Create the database if it does not already exist
CREATE DATABASE IF NOT EXISTS history;

-- Switch to the history database
USE history;

-- Create the table builds if it does not already exist
CREATE TABLE IF NOT EXISTS builds (
  commitHash VARCHAR(40) UNIQUE,
  commitDate INT(6),
  buildLog VARCHAR(800)
);
