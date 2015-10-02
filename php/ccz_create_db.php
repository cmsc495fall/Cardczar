<?php

// FUNCTION TAKEN FROM AMB: http://stackoverflow.com/questions/8734626/how-to-urlencode-a-multidimensional-array
function urlencode_array($array) {
    $out_array = array();
    foreach($array as $key => $value) {
    $out_array[urlencode($key)] = urlencode(str_replace(array("\r", "\n"), "", $value));
    }
return $out_array;
}


// GET POST DATA
$db_name = urlencode($_POST["roomname"]);
$username = urlencode($_POST["username"]);

// LINK TO SQL
$link = mysqli_connect('localhost', 'root', 'cmsc495fall');
if (!$link) { die('Could not connect: ' . mysqli_connect_error()); }

// CREATE DB
$query = 'CREATE DATABASE '.$db_name;
mysqli_query($link, $query) or die("Create DB Error: ".mysqli_error());


// SELECT THAT DB
mysqli_select_db($link, $db_name) or die("Select DB Error: ".mysqli_error());

// CREATE TABLES (GAMESTATE, USERS, Q & A)
$query = 'CREATE TABLE gamestate (id INT NOT NULL AUTO_INCREMENT, PRIMARY KEY(id), started BOOLEAN, timecreated INT, round INT, numusers INT, dealer INT, host INT, winner INT, activebait VARCHAR(128), selectedresponse VARCHAR(128), turnprogress VARCHAR(15));';
mysqli_query($link, $query) or die("Create gamestate Error: ".mysql_error());

$query = 'CREATE TABLE users (id INT NOT NULL AUTO_INCREMENT, PRIMARY KEY(id), username VARCHAR(30) UNIQUE, points INT, submission VARCHAR(128), timelastcontact TIMESTAMP);';
mysqli_query($link, $query) or die("Create users Error: ".mysql_error());

$query = 'CREATE TABLE bait (id INT NOT NULL AUTO_INCREMENT, PRIMARY KEY(id), text VARCHAR(128));';
mysqli_query($link, $query) or die("Create bait Error: ".mysql_error());

$query = 'CREATE TABLE responses (id INT NOT NULL AUTO_INCREMENT, PRIMARY KEY(id), text VARCHAR(128));';
mysqli_query($link, $query) or die("Create responses Error: ".mysql_error());


// INSERT INTO TABLES
$query = "INSERT INTO gamestate (started, timecreated, round, numusers, dealer, host, activebait, turnprogress) VALUES (FALSE,".time().", 0, 1, 0, 1, 'NO_BAIT_YET', 'NONE');";
mysqli_query($link, $query) or die("gamestate INSERT Error: ".mysql_error());


$prepared_statment = mysqli_prepare($link, "INSERT INTO users (username, points, submission) VALUES (?, ?, ?)");
mysqli_stmt_bind_param($prepared_statment, 'sis', $u, $p, $s);
$u = $username;
$p = 0;
$s = "WAIT FOR RESPONSE";
mysqli_stmt_execute($prepared_statment);
mysqli_stmt_close($prepared_statment);


$bait=file('/var/www/html/b.txt');
shuffle($bait);
$bait = urlencode_array($bait);
$bait = "('" .implode("'),('",$bait)."')";
$query = "INSERT INTO bait (text) VALUES $bait;";
mysqli_query($link, $query) or die("Error adding bait: ".mysql_error());

$responses=file('/var/www/html/r.txt');
shuffle($responses);
$responses = urlencode_array($responses);
$responses = "('" .implode("'),('",$responses)."')";
$query = "INSERT INTO responses (text) VALUES $responses;";
if (mysqli_query($link, $query)) {
    echo "OK";
} else {
    echo 'Error adding responses: ' . mysql_error() . "\n";
}

// CLOSE DATABASE
mysqli_close($link);

?>
