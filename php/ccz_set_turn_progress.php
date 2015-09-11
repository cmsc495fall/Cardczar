<?php

// LINK TO DB
$link = mysql_connect('localhost', 'root', 'password');
if (!$link) { die('Could not connect: ' . mysql_error()); }

// PULL POST DATA
// $db_name = $_SERVER['QUERY_STRING'];
$db_name = $_POST["room"];
$turn_progress = $_POST["progress"];

// SELECT THAT DB
mysql_select_db($db_name , $link) or die("set turn progress Select DB Error: ".mysql_error());

// SET turnprogress IN GAMESTATE
if ($myrow = mysql_fetch_array($tablecontents))
$query = "UPDATE gamestate SET turnprogress=$turn_progress WHERE id=1;  ";

// CLOSE DATABASE
mysql_close($link);

?>