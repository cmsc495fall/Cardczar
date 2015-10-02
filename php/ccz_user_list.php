<?php

// PULL POST DATA
$db_name = urlencode($_POST["roomname"]);

// LINK TO SQL
$link = mysqli_connect('localhost', 'root', 'cmsc495fall');
if (!$link) { die('Could not connect: ' . mysqli_connect_error()); }

// SELECT THAT DB
mysqli_select_db($link, $db_name) or die("user list Select DB Error: ".mysqli_error());

// GET USERS (do while because for mysql_fetch_array gets first user)
$query = "SELECT * FROM users";
$tablecontents = mysqli_query($link, $query);
if ($myrow = mysqli_fetch_array($tablecontents))
  {  $num = 1;
     do
        { echo $num." ".$myrow["username"]."|";  // DON'T URLDECODE HERE! (done in app because spaces)
          $num++;
        } 
     while ($myrow = mysqli_fetch_array($tablecontents));
  } 
  else  { echo "User list SELECT * FROM users Error"; }

// CLOSE DATABASE
mysqli_close($link);


?>