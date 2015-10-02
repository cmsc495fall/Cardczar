<?php

// PULL POST DATA
$db_name = urlencode($_POST["roomname"]);

// LINK TO SQL
$link = mysqli_connect('localhost', 'root', 'cmsc495fall');
if (!$link) { die('Could not connect: ' . mysqli_connect_error()); }

// SELECT THAT DB
mysqli_select_db($link, $db_name) or die("draw eight Select DB Error: ".mysqli_error());

$query = "SELECT * FROM responses";
$tablecontents = mysqli_query($link, $query);

for ($i = 1; $i <= 8; $i++) {
  if ($myrow = mysqli_fetch_array($tablecontents)) {
    $response_text = $myrow[text];
    echo urldecode($response_text)."|";
    $query = "DELETE FROM responses where text = '$response_text'";
    mysqli_query($link, $query) or die("draw eight Delete row error: ".mysqli_error()); 
  }  else  { echo "Select rows error"; }
} // end for

// CLOSE DATABASE
mysqli_close($link);

?>