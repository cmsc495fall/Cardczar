<?php

// LINK TO SQL
$link = mysql_connect('localhost', 'root', 'cmsc495fall');
if (!$link) { die('Could not connect: ' . mysql_error()); }

// PULL POST DATA
// $db_name = $_SERVER['QUERY_STRING'];
$db_name = urlencode($_POST["roomname"]);
$user = intval($_POST["user"]);

// SELECT THAT DB
mysql_select_db($db_name , $link) or die("process winner Select DB Error: ".mysql_error());


// select points in users
$result = mysql_query("SELECT points FROM users WHERE id=$user") or die(mysql_error());
$row = mysql_fetch_assoc($result);
echo urldecode($row[points]);

// CLOSE DATABASE
mysql_close($link);

?>