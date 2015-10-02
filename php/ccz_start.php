<?php

// GET POST DATA
$db_name = urlencode($_POST["roomname"]);

// LINK TO SQL
$link = mysqli_connect('localhost', 'root', 'cmsc495fall');
if (!$link) { die('Could not connect: ' . mysqli_connect_error()); }

// SELECT DB
if (mysqli_select_db($link, $db_name)) {
    $connected = TRUE;
} else {
    echo 'Error selecting database: '.mysqli_error();
    $connected = FALSE;
}

// UPDATE gamestate (started, round=1, dealer=1 (host))
if (connected) {
  $query = "SELECT * FROM gamestate";
  $tablecontents = mysqli_query($link, $query);
  if ($myrow = mysqli_fetch_array($tablecontents))
  $query = "UPDATE gamestate SET started=TRUE, round=1, dealer=1 WHERE id=1;";
  if (mysqli_query($link, $query)) {
    echo "OK";
  } else {
    echo 'Error updating DB: ' . mysqli_error() . "\n";
  }
}

// CLOSE DATABASE
mysqli_close($link);

?>