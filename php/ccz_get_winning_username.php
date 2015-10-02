<?php

// PULL POST DATA
$db_name = urlencode($_POST["roomname"]);

// LINK TO SQL
$link = mysqli_connect('localhost', 'root', 'cmsc495fall');
if (!$link) { die('Could not connect: ' . mysqli_connect_error()); }

// SELECT THAT DB
mysqli_select_db($link, $db_name) or die("Select DB Error: ".mysqli_error());

// GET gamestate dealer (int)
$query = "select username from users u, gamestate g where g.winner=u.id";
$result = mysqli_query($link, $query);
$row = mysqli_fetch_assoc($result);
$winner = $row[username];

echo urldecode($winner);

// CLOSE DATABASE
mysqli_close($link);

?>