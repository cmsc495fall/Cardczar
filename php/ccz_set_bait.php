<?php

// PULL POST DATA
$db_name = urlencode($_POST["roomname"]);
$turn_progress = urlencode($_POST["turnprogress"]);

// LINK TO SQL
$link = mysqli_connect('localhost', 'root', 'cmsc495fall');
if (!$link) { die('Could not connect: ' . mysqli_connect_error()); }

// SELECT THAT DB
mysqli_select_db($link, $db_name) or die("set bait Select DB Error: ".mysqli_error());

// GET bait TEXT, ECHO bait TEXT, then DELETE ROW
$query = "SELECT text FROM bait LIMIT 1";
$textcontents = mysqli_query($link, $query);
$row = mysqli_fetch_assoc($textcontents);
$bait_text = $row[text];
echo urldecode($bait_text);
$query = "DELETE FROM bait where text = '$bait_text'";
mysqli_query($link, $query) or die("set bait Delete row error: ".mysqli_error()); 

// SET activebait and turnprogress IN GAMESTATE
$prepared_statment = mysqli_prepare($link, "UPDATE gamestate SET activebait=?, turnprogress=? WHERE id=1;");
mysqli_stmt_bind_param($prepared_statment, 'ss', $a, $t);
$a = $bait_text;
$t = $turn_progress;
mysqli_stmt_execute($prepared_statment);
mysqli_stmt_close($prepared_statment);

// CLOSE DATABASE
mysqli_close($link);

?>