<?php

// LINK TO DB
$link = mysql_connect('localhost', 'root', 'password');
if (!$link) { die('Could not connect: ' . mysql_error()); }

// PULL POST DATA
// $db_name = $_SERVER['QUERY_STRING'];
$db_name = $_POST["room"];
$user_name = $_POST["name"];
$submission = $_POST["submission"];

// SELECT THAT DB
mysql_select_db($db_name , $link) or die("Select DB Error: ".mysql_error());


// SET submission IN GAMESTATE
$query = "UPDATE gamestate SET submission=$submission WHERE username=$username;";
mysql_query($query, $link) or die("Set submission error: ".mysql_error()); 

// CLOSE DATABASE
mysql_close($link);

?>