<!-- This PHP file is for updating game variables in database to indicate that game has started -->

<?php

// GET POST DATA FROM APACHE (DATA PASSED FROM APP) AND URLENCODE IT
$db_name = urlencode($_POST["roomname"]);

// LINK TO MYSQL
$link = mysqli_connect('localhost', 'root', 'cmsc495fall');
if (!$link) { die('Could not connect: ' . mysqli_connect_error()); }

// SELECT THE DB CORRESPONDING TO THE ROOM NAME
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

// CLOSE MYSQL LINK
mysqli_close($link);

?>