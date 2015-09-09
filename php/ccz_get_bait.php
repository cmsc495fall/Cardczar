<?php

// LINK TO DB
$link = mysql_connect('localhost', 'root', 'password');
if (!$link) { die('Could not connect: ' . mysql_error()); }

// PULL POST DATA
// $db_name = $_SERVER['QUERY_STRING'];
$db_name = $_POST["room"];

// SELECT THAT DB
mysql_select_db($db_name , $link) or die("Select DB Error: ".mysql_error());

// GET bait TEXT, ECHO bait TEXT, then DELETE ROW
$textcontents = mysql_query("SELECT text FROM gamestate LIMIT 1") or die(mysql_error());
$row = mysql_fetch_assoc($textcontents);
$bait_text = $row[text];
echo urldecode($bait_text);
$query = "DELETE FROM bait where text = '$bait_text'";
mysql_query($query, $link) or die("Delete row error: ".mysql_error()); 

// CLOSE DATABASE
mysql_close($link);

?>