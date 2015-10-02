<?php

// PULL POST DATA
$db_name = urlencode($_POST["roomname"]);
$username = urlencode($_POST["username"]);

// LINK TO SQL
$link = mysqli_connect('localhost', 'root', 'cmsc495fall');
if (!$link) { die('Could not connect: ' . mysqli_connect_error()); }

// SELECT THAT DB
mysqli_select_db($link, $db_name) or die("set bait Select DB Error: ".mysqli_error());

// DELETE user in table for username specified
$prepared_statment = mysqli_prepare($link, "DELETE FROM users where username=?");
mysqli_stmt_bind_param($prepared_statment, 's', $u);
$u = $username;
mysqli_stmt_execute($prepared_statment);
mysqli_stmt_close($prepared_statment);


// GET gamestate value for numusers
$query = "SELECT numusers FROM gamestate LIMIT 1";
$result = mysqli_query($link, $query);
$row = mysqli_fetch_assoc($result);
$numusers = intval($row[numusers]);
$numusers--;

// SET gamestate value for numusers
$prepared_statment = mysqli_prepare($link, "UPDATE gamestate SET numusers=? WHERE id=1;");
mysqli_stmt_bind_param($prepared_statment, 's', $n);
$n = $numusers;
mysqli_stmt_execute($prepared_statment);
mysqli_stmt_close($prepared_statment);


// CLOSE DATABASE
mysqli_close($link);

?>
