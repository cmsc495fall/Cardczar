<?php

// LINK TO DB
$link = mysql_connect('localhost', 'root', 'password');
if (!$link) { die('Could not connect: ' . mysql_error()); }

// GET POST DATA
$db_name = urlencode($_POST["room"]);
$user_name = urlencode($_POST["user"]);

// SELECT DB
if (mysql_select_db($db_name, $link)) {
    $connected = TRUE;
} else {
    echo 'Error selecting database: '.mysql_error();
    $connected = FALSE;
}

// UPDATE TABLES
if (connected) {

 // ADD USER TO users
 $query = "INSERT INTO users (username, points, quit, submission, timelastcontact) VALUES ('".$user_name."', 0, FALSE, 'NO_USER_SUBMISSION_YET', ".time().");";
 if (mysql_query($query, $link)) {
     echo "OK";
     $inserted = TRUE;
 } else {
     echo 'Error: ' . mysql_error() . "\n";
     $inserted = FALSE;
 }

 // INCREMENT NUMUSERS IN gamestate
 if ($inserted) {
  $tablecontents = mysql_query("SELECT * FROM gamestate");
  if ($myrow = mysql_fetch_array($tablecontents))
  $numusers = $myrow["numusers"];
  $numusers++;
  $query = "UPDATE gamestate SET numusers=".$numusers." WHERE id=1;  ";
  mysql_query($query, $link) or die("Update DB Error: ".mysql_error());
 }
}

// CLOSE DATABASE
mysql_close($link);

?>