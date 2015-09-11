<?php

// LINK TO DB
$link = mysql_connect('localhost', 'root', 'password');
if (!$link) { die('Could not connect: ' . mysql_error()); }

// PULL POST DATA
$db_name = $_SERVER['QUERY_STRING'];
// $db_name = $_POST["room"];

// SELECT THAT DB
mysql_select_db($db_name , $link) or die("Select DB Error: ".mysql_error());

// GET bait TEXT, ECHO bait TEXT, then DELETE ROW
$textcontents = mysql_query("SELECT text FROM bait LIMIT 1") or die(mysql_error());
$row = mysql_fetch_assoc($textcontents);
$bait_text = $row[text];
echo urldecode($bait_text);
$query = "DELETE FROM bait where text = '$bait_text'";
mysql_query($query, $link) or die("Delete row error: ".mysql_error()); 

// SET activebait IN GAMESTATE
$tablecontents = mysql_query("SELECT * FROM gamestate");
if ($myrow = mysql_fetch_array($tablecontents))
$query = "UPDATE gamestate SET activebait=$bait_text WHERE id=1;  ";

// CLOSE DATABASE
mysql_close($link);

?>