<!-- This PHP file passes the user points data from the database to the app -->

<?php

// GET POST DATA FROM APACHE (DATA PASSED FROM APP) AND URLENCODE IT
$db_name = urlencode($_POST["roomname"]);
$user = intval($_POST["user"]);

// LINK TO MYSQL
$link = mysqli_connect('localhost', 'root', 'cmsc495fall');
if (!$link) { die('Could not connect: ' . mysqli_connect_error()); }

// SELECT THE DB CORRESPONDING TO THE ROOM NAME
mysqli_select_db($link, $db_name) or die("get user points Select DB Error: ".mysqli_error());

// SELECT POINTS FROM USER TABLE AND ECHO TO APP
$query = "SELECT points FROM users WHERE id=$user";
$result = mysqli_query($link, $query);
$row = mysqli_fetch_assoc($result);
echo urldecode($row[points]);

// CLOSE MYSQL LINK
mysqli_close($link);

?>