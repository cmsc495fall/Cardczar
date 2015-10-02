<?php

// PULL POST DATA
$db_name = urlencode($_POST["roomname"]);
$response = urlencode($_POST["response"]);

// LINK TO SQL
$link = mysqli_connect('localhost', 'root', 'cmsc495fall');
if (!$link) { die('Could not connect: ' . mysqli_connect_error()); }

// SELECT THAT DB
mysqli_select_db($link, $db_name) or die("process winner Select DB Error: ".mysqli_error());

// GET user values

$prepared_statment = mysqli_prepare($link, "SELECT * FROM users WHERE submission=?");
mysqli_stmt_bind_param($prepared_statment, 's', $r);
$r = $response;
mysqli_stmt_execute($prepared_statment);
$result = mysqli_stmt_get_result($prepared_statment);
$row = mysqli_fetch_array($result, MYSQLI_ASSOC);

$selected_response = $row[submission];
$points = intval($row[points]);
$user = intval($row[id]);
$points++;
if ($points==5) { $turn_progress = "gameover"; } else { $turn_progress = "winnerpicked"; }

// SET points in users
$query = "UPDATE users SET points=$points WHERE id=$user;";
mysqli_query($link, $query) or die("process winner Set points in users error: ".mysqli_error());


// GET user values
$query = "SELECT * FROM gamestate";
$contents = mysqli_query($link, $query);
$row = mysqli_fetch_assoc($contents);
$round = $row[round];
$round++;

// SET round, dealer, winner, selectedresponse, and turnprogress IN gamestate
$prepared_statment = mysqli_prepare($link, "UPDATE gamestate SET round=?, dealer=?, winner=?, selectedresponse=?, turnprogress=? WHERE id=1;");
mysqli_stmt_bind_param($prepared_statment, 'iiiss', $r, $d, $w, $s, $t);
$r = $round;
$d = $user;
$w = $user;
$s = $selected_response;
$t = $turn_progress;
mysqli_stmt_execute($prepared_statment);
mysqli_stmt_close($prepared_statment);

echo $turn_progress;

// CLOSE DATABASE
mysqli_close($link);

?>
