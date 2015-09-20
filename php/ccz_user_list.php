<?php

// LINK TO SQL
$link = mysql_connect('localhost', 'root', 'cmsc495fall');
if (!$link) { die('Could not connect: ' . mysql_error()); }

// PULL POST DATA
// $db_name = $_SERVER['QUERY_STRING'];
$db_name = urlencode($_POST["roomname"]);

// SELECT THAT DB
mysql_select_db($db_name , $link) or die("user list Select DB Error: ".mysql_error());

// GET USERS (do while because for mysql_feth_array gets first user)
$tablecontents = mysql_query("SELECT * FROM users where quit=0");
if ($myrow = mysql_fetch_array($tablecontents))
  {  $num = 1;
     do
        { echo $myrow["username"]."|";
          $num++;
        } 
     while ($myrow = mysql_fetch_array($tablecontents));
  } 
  else  { echo "User list SELECT * FROM users Error"; }

// CLOSE DATABASE
mysql_close($link);


?>