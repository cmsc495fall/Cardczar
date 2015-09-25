<?php

// LINK TO SQL
$link = mysql_connect('localhost', 'root', 'cmsc495fall');
if (!$link) { die('Could not connect: ' . mysql_error()); }

// PULL POST DATA
// $db_name = $_SERVER['QUERY_STRING'];
$db_name = urlencode($_POST["roomname"]);
$action = urlencode($_POST["action"]);


// SELECT THAT DB
mysql_select_db($db_name , $link) or die("Select DB Error: ".mysql_error());

// NOTE: "WAIT FOR RESPONSE" is considered empty -- don't use null, because this string is displayed to dealer!
$fullOfWAIT_FOR_RESPONSE = true; // if remains true, then is true
$hasNoWAIT_FOR_RESPONSE = true;  // if remains true, then is true
$response_string="";

// GET gamestate dealer (int) so that dealer response is skipped
$result = mysql_query("SELECT dealer FROM gamestate LIMIT 1") or die(mysql_error());
$row = mysql_fetch_assoc($result);
$dealer = $row[dealer];

// GET USERS (do while because for mysql_feth_array gets first user)
$tablecontents = mysql_query("SELECT * FROM users WHERE id<>$dealer");
if ($myrow = mysql_fetch_array($tablecontents))
  {
     $num = 1;
     do
        { $response_string=$response_string.urldecode($myrow["submission"])."|";
          if ($myrow["submission"] == "WAIT FOR RESPONSE") { $hasNoWAIT_FOR_RESPONSE = false; }
          if ($myrow["submission"] != "WAIT FOR RESPONSE") { $fullOfWAIT_FOR_RESPONSE = false; }
          $num++;
        } 
     while ($myrow = mysql_fetch_array($tablecontents));
  } 
  else  { echo "User list SELECT * FROM users Error"; }

if ($action == "waitforallfull" && $hasNoWAIT_FOR_RESPONSE) {echo $response_string;} else
 if ($action == "waitforallempty" && $fullOfWAIT_FOR_RESPONSE) {echo "OK";} else
  { echo "Submissions are neither empty nor full";}

// CLOSE DATABASE
mysql_close($link);


?>