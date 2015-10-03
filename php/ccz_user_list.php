<?php
// This PHP file returns a user list from the database to the app
// Output data is formatted as pipe delimited text with user numbers, like so:
//    1 joe|2 mary|3 sue|


// GET POST DATA FROM APACHE (DATA PASSED FROM APP) AND URLENCODE IT
$db_name = urlencode($_POST["roomname"]);

// LINK TO MYSQL
$link = mysqli_connect('localhost', 'root', 'cmsc495fall');
if (!$link) { die('Could not connect: ' . mysqli_connect_error()); }

// SELECT THE DB CORRESPONDING TO THE ROOM NAME
mysqli_select_db($link, $db_name) or die("user list Select DB Error: ".mysqli_error());

// GET USERS (do while because for mysql_fetch_array gets first user) FROM DATABASE AND ECHO TO APP
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

// CLOSE MYSQL LINK
mysqli_close($link);


?>