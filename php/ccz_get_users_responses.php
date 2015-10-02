<?php

// PULL POST DATA
$db_name = urlencode($_POST["roomname"]);
$action = urlencode($_POST["action"]);

// LINK TO SQL
$link = mysqli_connect('localhost', 'root', 'cmsc495fall');
if (!$link) { die('Could not connect: ' . mysqli_connect_error()); }

// SELECT THAT DB
mysqli_select_db($link, $db_name) or die("Select DB Error: ".mysqli_error());

// NOTE: "WAIT FOR RESPONSE" is considered empty -- don't use null, because this string is displayed to dealer!
$fullOfWAIT_FOR_RESPONSE = true; // if remains true, then is true
$hasNoWAIT_FOR_RESPONSE = true;  // if remains true, then is true
$response_string="";

// GET gamestate dealer (int) so that dealer response is skipped
$query = "SELECT dealer FROM gamestate LIMIT 1";
$result = mysqli_query($link, $query);
$row = mysqli_fetch_assoc($result);
$dealer = $row[dealer];

// GET USERS (do while because mysqli_fetch_array gets first user)

$query = "SELECT * FROM users WHERE id<>$dealer";
$tablecontents = mysqli_query($link, $query);
if ($myrow = mysqli_fetch_array($tablecontents))
  {
     $num = 1;
     do
        { $response_string=$response_string.urldecode($myrow["submission"])."|";
          if ($myrow["submission"] == "WAIT FOR RESPONSE") { $hasNoWAIT_FOR_RESPONSE = false; }
          if ($myrow["submission"] != "WAIT FOR RESPONSE") { $fullOfWAIT_FOR_RESPONSE = false; }
          $num++;
        } 
     while ($myrow = mysqli_fetch_array($tablecontents));
  } 
  else  { echo "User list SELECT * FROM users Error"; }

if ($action == "waitforallfull" && $hasNoWAIT_FOR_RESPONSE) {echo $response_string;} else
 if ($action == "waitforallempty" && $fullOfWAIT_FOR_RESPONSE) {echo "OK";} else
  { echo "Submissions are not desired state (empty or full)";}

// CLOSE DATABASE
mysqli_close($link);


?>