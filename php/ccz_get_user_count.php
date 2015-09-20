<?php

// LINK TO SQL
$link = mysql_connect('localhost', 'root', 'cmsc495fall');
if (!$link) { die('Could not connect: ' . mysql_error()); }

// PULL POST DATA
// $db_name = $_SERVER['QUERY_STRING'];
$db_name = urlencode($_POST["roomname"]);

// SELECT THAT DB
mysql_select_db($db_name , $link) or die("Select DB Error: ".mysql_error());

// GET number of users in the game room
$results = mysql_query("SELECT count(*) FROM users where quit=0");
if ($myrow = mysql_fetch_assoc($results))
  {
     echo $myrow['count(*)'];
  } 
  else  { echo "Get number of users ERROR"; }

// CLOSE DATABASE
mysql_close($link);

?>