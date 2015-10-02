<?php

// PULL POST DATA
$db_name = urlencode($_POST["roomname"]);
$user = intval($_POST["user"]);

// LINK TO SQL
$link = mysqli_connect('localhost', 'root', 'cmsc495fall');
if (!$link) { die('Could not connect: ' . mysqli_connect_error()); }

// SELECT THAT DB
mysqli_select_db($link, $db_name) or die("process winner Select DB Error: ".mysqli_error());

// SET points in users
$query = "UPDATE users SET submission='WAIT FOR RESPONSE' WHERE id=$user;";
mysqli_query($link, $query) or die("change single response clear submission in users error: ".mysqli_error());

echo "OK";

// CLOSE DATABASE
mysqli_close($link);

?>