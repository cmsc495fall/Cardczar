<?php

// LINK TO SQL
$link = mysql_connect('localhost', 'root', 'cmsc495fall');
if (!$link) { die('Could not connect: ' . mysql_error()); }

// PULL POST DATA
// $db_name = $_SERVER['QUERY_STRING'];
$db_name = urlencode($_POST["roomname"]);
$username = urlencode($_POST["username"]);
$submission = urlencode($_POST["submission"]);

// SELECT THAT DB
mysql_select_db($db_name , $link) or die("Select DB Error: ".mysql_error());


// SET submission IN users
$query = "UPDATE users SET submission='$submission' WHERE username='user3';";
mysql_query($query, $link) or die("Set submission error: ".mysql_error()); 

// CLOSE DATABASE
mysql_close($link);

?>