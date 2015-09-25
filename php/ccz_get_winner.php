<?php

// LINK TO SQL
$link = mysql_connect('localhost', 'root', 'cmsc495fall');
if (!$link) { die('Could not connect: ' . mysql_error()); }

// PULL POST DATA
// $db_name = $_SERVER['QUERY_STRING'];
$db_name = urlencode($_POST["roomname"]);

// SELECT THAT DB
mysql_select_db($db_name , $link) or die("Select DB Error: ".mysql_error());

// GET gamestate dealer (int)
$result = mysql_query("SELECT round, winner, selectedresponse FROM gamestate LIMIT 1") or die(mysql_error());
$row = mysql_fetch_assoc($result);
$round = $row[round];
$winner = $row[winner];
$selectedresponse = $row[selectedresponse];
echo urldecode($round."|".$winner."|".$selectedresponse);


// CLOSE DATABASE
mysql_close($link);

?>