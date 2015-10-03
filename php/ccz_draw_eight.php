<?php
// This PHP file pulls 8 response cards from the database, deletes them from the database, and displays them
// (app will take html output and load card data into cards[])


// GET POST DATA FROM APACHE (DATA PASSED FROM APP) AND URLENCODE IT
$db_name = urlencode($_POST["roomname"]);

// LINK TO MYSQL
$link = mysqli_connect('localhost', 'root', 'cmsc495fall');
if (!$link) { die('Could not connect: ' . mysqli_connect_error()); }

// SELECT THE DB CORRESPONDING TO THE ROOM NAME
mysqli_select_db($link, $db_name) or die("draw eight Select DB Error: ".mysqli_error());

// GET ALL RESPONSES FROM DATABASE
$query = "SELECT * FROM responses";
$tablecontents = mysqli_query($link, $query);

// ECHO RESPONSES TO APP AND THEN DELETE USED RESPONSES FROM DATABASE
for ($i = 1; $i <= 8; $i++) {
  if ($myrow = mysqli_fetch_array($tablecontents)) {
    $response_text = $myrow[text];
    echo urldecode($response_text)."|";
    $query = "DELETE FROM responses where text = '$response_text'";
    mysqli_query($link, $query) or die("draw eight Delete row error: ".mysqli_error()); 
  }  else  { echo "Select rows error"; }
} // end for

// CLOSE MYSQL LINK
mysqli_close($link);

?>