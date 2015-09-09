<?php

// LINK TO DB
$link = mysql_connect('localhost', 'root', 'cmsc495fall');
if (!$link) { die('Could not connect: ' . mysql_error()); }

// PULL POST DATA
// $db_name = $_SERVER['QUERY_STRING'];
$db_name = $_POST["room"];

// SELECT THAT DB
mysql_select_db($db_name , $link) or die("Select DB Error: ".mysql_error());

$tablecontents = mysql_query("SELECT * FROM responses");

for ($i = 1; $i <= 8; $i++) {
  if ($myrow = mysql_fetch_array($tablecontents)) {
    $response_text = $myrow[text];
    echo urldecode($response_text)."|";
    $query = "DELETE FROM responses where text = '$response_text'";
    mysql_query($query, $link) or die("Delete row error: ".mysql_error()); 
  }  else  { echo "Select rows error"; }
} // end for

// CLOSE DATABASE
mysql_close($link);

?>