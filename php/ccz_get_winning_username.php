<!-- This PHP file is for passing the winner's name from the database to the app -->

<?php

// GET POST DATA FROM APACHE (DATA PASSED FROM APP) AND URLENCODE IT
$db_name = urlencode($_POST["roomname"]);

// LINK TO MYSQL
$link = mysqli_connect('localhost', 'root', 'cmsc495fall');
if (!$link) { die('Could not connect: ' . mysqli_connect_error()); }

// SELECT THE DB CORRESPONDING TO THE ROOM NAME
mysqli_select_db($link, $db_name) or die("Select DB Error: ".mysqli_error());

// GET winner's name FROM DATABASE AND ECHO TO APP
$query = "select username from users u, gamestate g where g.winner=u.id";
$result = mysqli_query($link, $query);
$row = mysqli_fetch_assoc($result);
$winner = $row[username];
echo urldecode($winner);

// CLOSE MYSQL LINK
mysqli_close($link);

?>