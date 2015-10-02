<!-- This PHP file is for passing the winning response string (selectedresponse) from the database to the app -->

<?php

// GET POST DATA FROM APACHE (DATA PASSED FROM APP) AND URLENCODE IT
$db_name = urlencode($_POST["roomname"]);

// LINK TO MYSQL
$link = mysqli_connect('localhost', 'root', 'cmsc495fall');
if (!$link) { die('Could not connect: ' . mysqli_error()); }

// SELECT THE DB CORRESPONDING TO THE ROOM NAME
mysqli_select_db($link, $db_name) or die("Select DB Error: ".mysqli_error());

// GET gamestate selectedresponse (string) FROM DATABASE AND ECHO TO APP
$query = "SELECT selectedresponse FROM gamestate LIMIT 1";
$result = mysqli_query($link, $query);
$row = mysqli_fetch_assoc($result);
$selectedresponse = $row[selectedresponse];
echo urldecode($selectedresponse);

// CLOSE MYSQL LINK
mysqli_close($link);

?>