<?php
// This PHP file is for setting database variables after a user has gracefully quit


// GET POST DATA FROM APACHE (DATA PASSED FROM APP) AND URLENCODE IT
$db_name = urlencode($_POST["roomname"]);
$username = urlencode($_POST["username"]);

// LINK TO MYSQL
$link = mysqli_connect('localhost', 'root', 'cmsc495fall');
if (!$link) { die('Could not connect: ' . mysqli_connect_error()); }

// SELECT THE DB CORRESPONDING TO THE ROOM NAME
mysqli_select_db($link, $db_name) or die("set bait Select DB Error: ".mysqli_error());

// DELETE USER IN USERS TABLE FOR USERNAME PASSED FROM APP
$prepared_statement = mysqli_prepare($link, "DELETE FROM users where username=?");
mysqli_stmt_bind_param($prepared_statement, 's', $u);
$u = $username;
mysqli_stmt_execute($prepared_statement);
mysqli_stmt_close($prepared_statement);

// GET gamestate VALUE FOR numusers
$query = "SELECT numusers FROM gamestate LIMIT 1";
$result = mysqli_query($link, $query);
$row = mysqli_fetch_assoc($result);
$numusers = intval($row[numusers]);
$numusers--;

// SET gamestate VALUE FOR numusers
$prepared_statement = mysqli_prepare($link, "UPDATE gamestate SET numusers=? WHERE id=1;");
mysqli_stmt_bind_param($prepared_statement, 's', $n);
$n = $numusers;
mysqli_stmt_execute($prepared_statement);
mysqli_stmt_close($prepared_statement);

// CLOSE MYSQL LINK
mysqli_close($link);

?>
