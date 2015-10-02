<!-- This PHP file passes the current bait (activebait) to the app -->

<?php

// GET POST DATA FROM APACHE (DATA PASSED FROM APP) AND URLENCODE IT
$db_name = urlencode($_POST["roomname"]);

// LINK TO MYSQL
$link = mysqli_connect('localhost', 'root', 'cmsc495fall');
if (!$link) { die('Could not connect: ' . mysqli_connect_error()); }

// SELECT THE DB CORRESPONDING TO THE ROOM NAME
mysqli_select_db($link, $db_name) or die("Select DB Error: ".mysqli_error());

// GET gamestate activebait, ECHO activebait from database to app
$query = "SELECT activebait FROM gamestate LIMIT 1";
$result = mysqli_query($link, $query);
$row = mysqli_fetch_assoc($result);
$bait_text = $row[activebait];
echo urldecode($bait_text);

// CLOSE MYSQL LINK
mysqli_close($link);

?>