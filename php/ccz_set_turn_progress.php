<?php

// PULL POST DATA
$db_name = urlencode($_POST["roomname"]);
$turn_progress = urlencode($_POST["turnprogress"]);

// LINK TO SQL
$link = mysqli_connect('localhost', 'root', 'cmsc495fall');
if (!$link) { die('Could not connect: ' . mysqli_connect_error()); }

// SELECT THAT DB
mysqli_select_db($link, $db_name) or die("set turn progress Select DB Error: ".mysqli_error());

// SET turnprogress IN GAMESTATE
$prepared_statment = mysqli_prepare($link, "UPDATE gamestate SET turnprogress=? WHERE id=1;");
mysqli_stmt_bind_param($prepared_statment, 's', $t);
$t = $turn_progress;
mysqli_stmt_execute($prepared_statment);
mysqli_stmt_close($prepared_statment);


// CLOSE DATABASE
mysqli_close($link);

?>