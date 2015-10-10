<?php
// This PHP file is for changing the user response submission in the database
// and echoing a new response to the user (to refill their hand)


// GET POST DATA FROM APACHE (DATA PASSED FROM APP) AND URLENCODE IT
$db_name = urlencode($_POST["roomname"]);
$username = urlencode($_POST["username"]);
$submission = urlencode($_POST["submission"]);

// LINK TO MYSQL
$link = mysqli_connect('localhost', 'root', 'cmsc495fall');
if (!$link) { die('Could not connect: ' . mysqli_connect_error()); }

// SELECT THE DB CORRESPONDING TO THE ROOM NAME
mysqli_select_db($link, $db_name) or die("Select DB Error: ".mysqli_error());

// SET submission IN users USING PREPARED STATEMENT TO FOIL SQL INJECTION ATTACKS
$prepared_statement = mysqli_prepare($link, "UPDATE users SET submission=? WHERE username=?;");
mysqli_stmt_bind_param($prepared_statement, 'ss', $s, $u);
$s = $submission;
$u = $username;
mysqli_stmt_execute($prepared_statement);
mysqli_stmt_close($prepared_statement);

// GET NEW RESPONSE, ECHO TO APP FOR USER, THEN DELETE RESPONSE FROM DATABASE
$query = "SELECT text FROM responses LIMIT 1";
$result = mysqli_query($link, $query);
$row = mysqli_fetch_assoc($result);
$response_text = $row[text];
echo urldecode($response_text);
$query = "DELETE FROM responses where text = '$response_text'";
mysqli_query($link, $query) or die("User submit--get new card error: ".mysqli_error());

// CLOSE MYSQL LINK
mysqli_close($link);

?>