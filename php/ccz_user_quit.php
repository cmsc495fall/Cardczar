<?php

// LINK TO SQL
$link = mysql_connect('localhost', 'root', 'cmsc495fall');
if (!$link) { die('Could not connect: ' . mysql_error()); }

// PULL POST DATA
// $db_name = $_SERVER['QUERY_STRING'];
$db_name = urlencode($_POST["roomname"]);
$username = urlencode($_POST["username"]);

// SELECT THAT DB
mysql_select_db($db_name , $link) or die("set bait Select DB Error: ".mysql_error());

// SET quit to true in users table for username specified
$query = "UPDATE users SET quit=1 WHERE username='$username';";
mysql_query($query, $link) or die("set user quit. Error: ".mysql_error());

// CLOSE DATABASE
mysql_close($link);

?>