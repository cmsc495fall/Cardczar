<?php

// LINK TO DB
$link = mysql_connect('localhost', 'root', 'password');
if (!$link) { die('Could not connect: ' . mysql_error()); }

// PULL POST DATA
$db_name = $_SERVER['QUERY_STRING'];


// SELECT THAT DB
mysql_select_db($db_name , $link) or die("Select DB Error: ".mysql_error());

$tablecontents = mysql_query("SELECT * FROM gamestate");
if ($myrow = mysql_fetch_array($tablecontents))

  {  echo "gamestate<table border=1><tr><td>id</td><td>started</td><td>timecreated</td><td>round</td><td>numusers</td><td>dealer</td><td>host</td><td>activebait</td><td>selectedresponse</td><td>turnprogress</td></tr>";
     $num = 1;
     do
        { echo "<tr><td>".$myrow["id"]."</td><td>".$myrow["started"]."</td><td>".$myrow["timecreated"]."</td><td>".$myrow["round"]."</td><td>".$myrow["numusers"]."</td><td>".$myrow["dealer"]."</td><td>".$myrow["host"]."</td><td>".$myrow["activebait"]."</td><td>".$myrow["selectedresponse"]."</td><td>".$myrow["turnprogress"]."</td></tr>";
          $num++;
        } 
     while ($myrow = mysql_fetch_array($tablecontents));
  } 

  else  { echo "<br><br><center>Sorry, no records were found!</center><br>"; }

  echo "</table><br>";


$tablecontents2 = mysql_query("SELECT * FROM users");
if ($myrow = mysql_fetch_array($tablecontents2))

  {  echo "users<table border=1><tr><td>id</td><td>username</td><td>points</td><td>submission</td><td>timelastcontact</td></tr>";
     $num = 1;
     do
        { echo "<tr><td>".$myrow["id"]."</td><td>".$myrow["username"]."</td><td>".$myrow["points"]."</td><td>".$myrow["submission"]."</td><td>".$myrow["timelastcontact"]."</td></tr>";
          $num++;
        } 
     while ($myrow = mysql_fetch_array($tablecontents2));
  } 

  else  { echo "<br><br><center>Sorry, no records were found!</center><br>"; }

  echo "</table><br>";

$tablecontents3 = mysql_query("SELECT * FROM bait");
if ($myrow = mysql_fetch_array($tablecontents3))

  {  echo "bait<table border=1><tr><td>id</td><td>text</td></tr>";
     $num = 1;
     do
        { echo "<tr><td>".$myrow["id"]."</td><td>".$myrow["text"]."</td></tr>";
          $num++;
        } 
     while ($myrow = mysql_fetch_array($tablecontents3));
  } 

  else  { echo "<br><br><center>Sorry, no records were found!</center><br>"; }

  echo "</table><br>";


$tablecontents4 = mysql_query("SELECT * FROM responses");
if ($myrow = mysql_fetch_array($tablecontents4))

  {  echo "responses<table border=1><tr><td>id</td><td>text</td></tr>";
     $num = 1;
     do
        { echo "<tr><td>".$myrow["id"]."</td><td>".$myrow["text"]."</td></tr>";
          $num++;
        } 
     while ($myrow = mysql_fetch_array($tablecontents4));
  } 

  else  { echo "<br><br><center>Sorry, no records were found!</center><br>"; }

  echo "</table>";

// CLOSE DATABASE
mysql_close($link);

?>