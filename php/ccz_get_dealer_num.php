<?php
// This PHP file passes the dealer number from the database table gamestate to the app


// GET POST DATA FROM APACHE (DATA PASSED FROM APP) AND URLENCODE IT
$db_name = urlencode($_POST["roomname"]);

// LINK TO MYSQL
$link = mysqli_connect('localhost', 'root', 'cmsc495fall');
if (!$link) { die('Could not connect: ' . mysqli_connect_error()); }

// SELECT THE DB CORRESPONDING TO THE ROOM NAME
mysqli_select_db($link, $db_name) or die("Select DB Error: ".mysqli_error());

// GET gamestate dealer (int) and echo to the app
$query = "SELECT dealer FROM gamestate LIMIT 1";
$result = mysqli_query($link, $query);
$row = mysqli_fetch_assoc($result);
$dealer = $row[dealer];
echo urldecode($dealer);

// CLOSE MYSQL LINK
mysqli_close($link);

?>