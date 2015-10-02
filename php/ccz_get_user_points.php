<?php

// PULL POST DATA
$db_name = urlencode($_POST["roomname"]);
$user = intval($_POST["user"]);

// LINK TO SQL
$link = mysqli_connect('localhost', 'root', 'cmsc495fall');
if (!$link) { die('Could not connect: ' . mysqli_connect_error()); }

// SELECT THAT DB
mysqli_select_db($link, $db_name) or die("get user points Select DB Error: ".mysqli_error());

// select points in users
$query = "SELECT points FROM users WHERE id=$user";
$result = mysqli_query($link, $query);
$row = mysqli_fetch_assoc($result);
echo urldecode($row[points]);

// CLOSE DATABASE
mysqli_close($link);

?>