<?php

// LINK TO DB
$link = mysql_connect('localhost', 'root', 'password');
if (!$link) { die('Could not connect: ' . mysql_error()); }

// PULL POST DATA
// $db_name = $_SERVER['QUERY_STRING'];
$db_name = $_POST["room"];
$turn_progress = $_POST["turnprogress"];

// SELECT THAT DB
mysql_select_db($db_name , $link) or die("set bait Select DB Error: ".mysql_error());

// GET bait TEXT, ECHO bait TEXT, then DELETE ROW
$textcontents = mysql_query("SELECT text FROM bait LIMIT 1") or die("set bait Select from bait Error: ".mysql_error());
$row = mysql_fetch_assoc($textcontents);
$bait_text = $row[text];
echo urldecode($bait_text);
$query = "DELETE FROM bait where text = '$bait_text'";
mysql_query($query, $link) or die("set bait Delete row error: ".mysql_error()); 

// SET activebait and turnprogress  IN GAMESTATE
$query = "UPDATE gamestate SET activebait='$bait_text', turnprogress='$turn_progress' WHERE id=1;";
mysql_query($query, $link) or die("set bait Set activebait/turnprogress error: ".mysql_error()); 

// CLOSE DATABASE
mysql_close($link);

?>