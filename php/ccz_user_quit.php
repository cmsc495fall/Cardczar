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

// DELETE user in table for username specified
$query = "DELETE FROM users where username = '$username'";
mysql_query($query, $link) or die("set user quit. Error: ".mysql_error());

// GET gamestate value for numusers
$result = mysql_query("SELECT numusers FROM gamestate LIMIT 1") or die(mysql_error());
$row = mysql_fetch_assoc($result);
$numusers = intval($row[numusers]);
$numusers--;

// SET gamestate value for numusers
$query = "UPDATE gamestate SET numusers=$numusers WHERE id=1;";
mysql_query($query, $link) or die("process winner Set selectedresponse/turnprogress error: ".mysql_error()); 


// CLOSE DATABASE
mysql_close($link);

?>
