<?php

// LINK TO DB
$link = mysql_connect('localhost', 'root', 'password');
if (!$link) { die('Could not connect: ' . mysql_error()); }

// PULL POST DATA
// $db_name = $_SERVER['QUERY_STRING'];
$db_name = $_POST["room"];
$user = intval($_POST["user"]);

// SELECT THAT DB
mysql_select_db($db_name , $link) or die("process winner Select DB Error: ".mysql_error());

// GET bait TEXT, ECHO bait TEXT, then DELETE ROW
$textcontents = mysql_query("SELECT * FROM users WHERE id=$user") or die("process winner Select from users Error: ".mysql_error());
$row = mysql_fetch_assoc($textcontents);
$selected_response = $row[submission];
$points = intval($row[points]);
$points++;
if ($points==2) { $turn_progress = "gameover"; } else { $turn_progress = "winnerpicked"; }

// SET points in users
$query = "UPDATE users SET points=$points WHERE id=$user;";
mysql_query($query, $link) or die("process winner Set points in users error: ".mysql_error());

// SET selectedresponse and turnprogress IN gamestate
$query = "UPDATE gamestate SET selectedresponse='$selected_response', turnprogress='$turn_progress' WHERE id=1;";
mysql_query($query, $link) or die("process winner Set selectedresponse/turnprogress error: ".mysql_error()); 

echo $turn_progress;

// CLOSE DATABASE
mysql_close($link);

?>