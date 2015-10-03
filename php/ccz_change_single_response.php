<?php
// This PHP file is for changing a user's response (in the DB) to WAIT FOR RESPONSE


// GET POST DATA FROM APACHE (DATA PASSED FROM APP) AND URLENCODE IT
$db_name = urlencode($_POST["roomname"]);
$user = intval($_POST["user"]);

// LINK TO MYSQL
$link = mysqli_connect('localhost', 'root', 'cmsc495fall');
if (!$link) { die('Could not connect: ' . mysqli_connect_error()); }

// SELECT THE DB CORRESPONDING TO THE ROOM NAME
mysqli_select_db($link, $db_name) or die("process winner Select DB Error: ".mysqli_error());

// SET points in users
$query = "UPDATE users SET submission='WAIT FOR RESPONSE' WHERE id=$user;";
mysqli_query($link, $query) or die("change single response clear submission in users error: ".mysqli_error());

// APP CHECKS FOR OK.  IF mysqli ERROR, IT WILL ASSUME NOT OK
echo "OK";

// CLOSE MYSQL LINK
mysqli_close($link);

?>