<?php

// LINK TO SQL
$link = mysql_connect('localhost', 'root', 'cmsc495fall');
if (!$link) { die('Could not connect: ' . mysql_error()); }

// PULL POST DATA
//$db_name = $_SERVER['QUERY_STRING'];
$db_name = urlencode($_POST["roomname"]);
$turnprogress = urlencode($_POST["turnprogress"]);

// SELECT THAT DB
mysql_select_db($db_name , $link) or die("set turn progress Select DB Error: ".mysql_error());

// SET turnprogress IN GAMESTATE
$query = "UPDATE gamestate SET turnprogress='$turnprogress' WHERE id=1;";
mysql_query($query, $link) or die("set turnprogress error: ".mysql_error()); 

// CLOSE DATABASE
mysql_close($link);

?>