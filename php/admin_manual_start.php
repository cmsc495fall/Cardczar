<?php

// LINK TO DB
$link = mysql_connect('localhost', 'root', 'cmsc495fall');
if (!$link) { die('Could not connect: ' . mysql_error()); }

// GET POST DATA
$db_name = $_SERVER['QUERY_STRING'];

// SELECT DB
if (mysql_select_db($db_name, $link)) {
    $connected = TRUE;
} else {
    echo 'Error selecting database: '.mysql_error();
    $connected = FALSE;
}

// UPDATE TABLES

if (connected) {
  $tablecontents = mysql_query("SELECT * FROM gamestate");
  if ($myrow = mysql_fetch_array($tablecontents))
  $query = "UPDATE gamestate SET started=TRUE, round=1, dealer=1 WHERE id=1;  ";
  if (mysql_query($query, $link)) {
    echo "OK";
  } else {
    echo 'Error updating DB: ' . mysql_error() . "\n";
  }
}

// CLOSE DATABASE
mysql_close($link);

?>