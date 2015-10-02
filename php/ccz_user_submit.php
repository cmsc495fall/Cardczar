<?php

// PULL POST DATA
$db_name = urlencode($_POST["roomname"]);
$username = urlencode($_POST["username"]);
$submission = urlencode($_POST["submission"]);

// LINK TO SQL
$link = mysqli_connect('localhost', 'root', 'cmsc495fall');
if (!$link) { die('Could not connect: ' . mysqli_connect_error()); }

// SELECT THAT DB
mysqli_select_db($link, $db_name) or die("Select DB Error: ".mysqli_error());

// SET submission IN users
$prepared_statment = mysqli_prepare($link, "UPDATE users SET submission=? WHERE username=?;");
mysqli_stmt_bind_param($prepared_statment, 'ss', $s, $u);
$s = $submission;
$u = $username;
mysqli_stmt_execute($prepared_statment);
mysqli_stmt_close($prepared_statment);

// GET new response, ECHO, then delete from responses
$query = "SELECT text FROM responses LIMIT 1";
$result = mysqli_query($link, $query);
$row = mysqli_fetch_assoc($result);
$response_text = $row[text];
echo urldecode($response_text);
$query = "DELETE FROM responses where text = '$response_text'";
mysqli_query($link, $query) or die("User submit--get new card error: ".mysqli_error());

// CLOSE DATABASE
mysqli_close($link);

?>