<?php

// LINK TO DB
$link = mysql_connect('localhost', 'root', 'password');
if (!$link) { die('Could not connect: ' . mysql_error()); }

// PULL POST DATA
$db_name = $_SERVER['QUERY_STRING'];
// $db_name = $_POST["room"];

// SELECT THAT DB
mysql_select_db($db_name , $link) or die("Select DB Error: ".mysql_error());

// GET USERS (do while because for mysql_feth_array gets first user)
$tablecontents = mysql_query("SELECT * FROM users");
if ($myrow = mysql_fetch_array($tablecontents))
  {  echo "OK";
     $num = 1;
     do
        { $query = "UPDATE users SET submission='TESTING$myrow[id]' WHERE id=$myrow[id];";
          mysql_query($query, $link) or die("users UPDATE Error: ".mysql_error());
          $num++;
        } 
     while ($myrow = mysql_fetch_array($tablecontents));
  } 
  else  { echo "User list SELECT * FROM users Error"; }

// CLOSE DATABASE
mysql_close($link);


?>