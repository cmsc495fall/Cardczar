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


// SET points in users
$query = "UPDATE users SET submission='WAIT FOR RESPONSE' WHERE id=$user;";
mysql_query($query, $link) or die("process winner Set points in users error: ".mysql_error());

echo "OK";

// CLOSE DATABASE
mysql_close($link);

?>