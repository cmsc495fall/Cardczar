<?php
// This PHP file checks to see if game has started by looking at gamestate boolean variable started and passing
// the database value to the app


// GET POST DATA FROM APACHE (DATA PASSED FROM APP) AND URLENCODE IT
$db_name = urlencode($_POST["roomname"]);

// LINK TO MYSQL
$link = mysqli_connect('localhost', 'root', 'cmsc495fall');
if (!$link) { die('Could not connect: ' . mysqli_connect_error()); }

// SELECT THE DB CORRESPONDING TO THE ROOM NAME
mysqli_select_db($link, $db_name) or die("Select DB Error: ".mysqli_error());

// GET started value and echo OK if gamestate started = TRUE
$query = "SELECT started from gamestate";
$result = mysqli_query($link, $query);
$row = mysqli_fetch_assoc($result);
if ($row[started]) { echo "OK"; }

// CLOSE MYSQL LINK
mysqli_close($link);

?>