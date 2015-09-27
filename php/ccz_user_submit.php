<?php

// LINK TO SQL
$link = mysql_connect('localhost', 'root', 'cmsc495fall');
if (!$link) { die('Could not connect: ' . mysql_error()); }

// PULL POST DATA
// $db_name = $_SERVER['QUERY_STRING'];
$db_name = urlencode($_POST["roomname"]);
$username = urlencode($_POST["username"]);
$submission = urlencode($_POST["submission"]);


// SELECT THAT DB
mysql_select_db($db_name , $link) or die("Select DB Error: ".mysql_error());


// SET submission IN users
$query = "UPDATE users SET submission='$submission' WHERE username='$username';";
mysql_query($query, $link) or die("User submit--Set submission error: ".mysql_error()); 

// GET new response, ECHO, then delete from responses
$result = mysql_query("SELECT text FROM responses LIMIT 1") or die(mysql_error());
$row = mysql_fetch_assoc($result);
$response_text = $row[text];
echo urldecode($response_text);
$query = "DELETE FROM responses where text = '$response_text'";
mysql_query($query, $link) or die("User submit--get new card error: ".mysql_error());

// CLOSE DATABASE
mysql_close($link);

?>