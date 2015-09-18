<?php

// FUNCTION TAKEN FROM AMB: http://stackoverflow.com/questions/8734626/how-to-urlencode-a-multidimensional-array
function urlencode_array($array) {
    $out_array = array();
    foreach($array as $key => $value) {
    $out_array[urlencode($key)] = urlencode($value);
    }
return $out_array;
}


// LINK TO SQL
$link = mysql_connect('localhost', 'root', 'cmsc495fall');
if (!$link) { die('Could not connect: ' . mysql_error()); }

// GET POST DATA
$db_name = urlencode($_POST["roomname"]);
$username = urlencode($_POST["username"]);

// CREATE DB
$query = 'CREATE DATABASE '.$db_name;
mysql_query($query, $link) or die("Create DB Error: ".mysql_error());


// SELECT THAT DB
mysql_select_db($db_name , $link) or die("Select DB Error: ".mysql_error());

// CREATE TABLES (GAMESTATE, USERS, Q & A)
$query = 'CREATE TABLE gamestate (id INT NOT NULL AUTO_INCREMENT, PRIMARY KEY(id), started BOOLEAN, timecreated INT, round INT, numusers INT, dealer INT, host INT, activebait VARCHAR(128), selectedresponse VARCHAR(128), turnprogress VARCHAR(15));';
mysql_query($query, $link) or die("Create gamestate Error: ".mysql_error());

$query = 'CREATE TABLE users (id INT NOT NULL AUTO_INCREMENT, PRIMARY KEY(id), username VARCHAR(30) UNIQUE, points INT, quit BOOLEAN, submission VARCHAR(128), timelastcontact TIMESTAMP);';
mysql_query($query, $link) or die("Create users Error: ".mysql_error());

$query = 'CREATE TABLE bait (id INT NOT NULL AUTO_INCREMENT, PRIMARY KEY(id), text VARCHAR(128));';
mysql_query($query, $link) or die("Create bait Error: ".mysql_error());

$query = 'CREATE TABLE responses (id INT NOT NULL AUTO_INCREMENT, PRIMARY KEY(id), text VARCHAR(128));';
mysql_query($query, $link) or die("Create responses Error: ".mysql_error());


// INSERT INTO TABLES
$query = "INSERT INTO gamestate (started, timecreated, round, numusers, dealer, host, activebait, turnprogress) VALUES (FALSE,".time().", 0, 1, 0, 1, 'NO_BAIT_YET', 'NONE');";
mysql_query($query, $link) or die("gamestate INSERT Error: ".mysql_error());

$query = "INSERT INTO users (username, points, quit, submission, timelastcontact) VALUES ('$username', 0, FALSE, 'WAIT FOR RESPONSE', ".time().");";
mysql_query($query, $link) or die("users INSERT Error: ".mysql_error());

$bait=file('/var/www/html/b.txt');
shuffle($bait);
$bait = urlencode_array($bait);
$bait = "('" .implode("'),('",$bait)."')";
$query = "INSERT INTO bait (text) VALUES $bait;";
mysql_query($query, $link) or die("Error adding bait: ".mysql_error());

$responses=file('/var/www/html/r.txt');
shuffle($responses);
$responses = urlencode_array($responses);
$responses = "('" .implode("'),('",$responses)."')";
$query = "INSERT INTO responses (text) VALUES $responses;";
if (mysql_query($query, $link)) {
    echo "OK";
} else {
    echo 'Error adding responses: ' . mysql_error() . "\n";
}

// CLOSE DATABASE
mysql_close($link);

?>