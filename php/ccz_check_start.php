<?php

// PULL POST DATA
$db_name = urlencode($_POST["roomname"]);

// LINK TO SQL
$link = mysqli_connect('localhost', 'root', 'cmsc495fall');
if (!$link) { die('Could not connect: ' . mysqli_connect_error()); }

// SELECT THAT DB
mysqli_select_db($link, $db_name) or die("Select DB Error: ".mysqli_error());

// GET started value and echo OK if gamestate started = TRUE
$query = "SELECT started from gamestate";
$result = mysqli_query($link, $query);
$row = mysqli_fetch_assoc($result);

if ($row[started]) { echo "OK"; }

// CLOSE DATABASE
mysqli_close($link);

?>