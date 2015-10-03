<?php
// This PHP file is for changing turnprogress in gamestate table

// GET POST DATA FROM APACHE (DATA PASSED FROM APP) AND URLENCODE IT
$db_name = urlencode($_POST["roomname"]);
$turn_progress = urlencode($_POST["turnprogress"]);

// LINK TO MYSQL
$link = mysqli_connect('localhost', 'root', 'cmsc495fall');
if (!$link) { die('Could not connect: ' . mysqli_connect_error()); }

// SELECT THE DB CORRESPONDING TO THE ROOM NAME
mysqli_select_db($link, $db_name) or die("set turn progress Select DB Error: ".mysqli_error());

// SET turnprogress IN GAMESTATE
$prepared_statment = mysqli_prepare($link, "UPDATE gamestate SET turnprogress=? WHERE id=1;");
mysqli_stmt_bind_param($prepared_statment, 's', $t);
$t = $turn_progress;
mysqli_stmt_execute($prepared_statment);
mysqli_stmt_close($prepared_statment);

// CLOSE MYSQL LINK
mysqli_close($link);

?>