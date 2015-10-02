<!-- This PHP file is for passing user reponses from the database to the app for dealer display.
     In addition to the room name parameter, this takes and action parameter of "waitforallfull" or "waitfor allempty"
     and returns data dependant on the parameter.  Following are example inputs and outputs:
      waitforallfull: if all responses are in, pipe delimited text is echoed, like so: Batman|Spartacus|Skeletor|
      waitforallempty: if all responses are empty (WAIT FOR RESPONSE is considered empty) then echo "OK"
      If either of the desired states (full or empty) are not met, this PHP file returns:
        "Submissions are not desired state (empty or full)"
-->

<?php

// GET POST DATA FROM APACHE (DATA PASSED FROM APP) AND URLENCODE IT
$db_name = urlencode($_POST["roomname"]);
$action = urlencode($_POST["action"]);

// LINK TO MYSQL
$link = mysqli_connect('localhost', 'root', 'cmsc495fall');
if (!$link) { die('Could not connect: ' . mysqli_connect_error()); }

// SELECT THE DB CORRESPONDING TO THE ROOM NAME
mysqli_select_db($link, $db_name) or die("Select DB Error: ".mysqli_error());

// SET DEFAULT STATES FOR FIGURING OUT IF EMPTY OR FULL
// NOTE: "WAIT FOR RESPONSE" is considered empty -- don't use null, because this string is displayed to dealer!
$fullOfWAIT_FOR_RESPONSE = true; // if remains true, then is true
$hasNoWAIT_FOR_RESPONSE = true;  // if remains true, then is true
$response_string="";

// GET gamestate dealer (int) SO THAT DEALER RESPONSE IS SKIPPED (DEALER RESPONSE SHOULD BE WAIT FOR RESPONSE)
$query = "SELECT dealer FROM gamestate LIMIT 1";
$result = mysqli_query($link, $query);
$row = mysqli_fetch_assoc($result);
$dealer = $row[dealer];

// GET USERS OTHER THAN DEALER (do while because mysqli_fetch_array gets first user)
// FIGURE OUT IF ALL RESPONSES ARE 'WAIT FOR RESPONSE' OR FULL OF RESPONSE DATA
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

// RETURN APPROPRIATE OUTPUT TO APP DEPENDING ON INPUT ACTION
if ($action == "waitforallfull" && $hasNoWAIT_FOR_RESPONSE) {echo $response_string;} else
 if ($action == "waitforallempty" && $fullOfWAIT_FOR_RESPONSE) {echo "OK";} else
  { echo "Submissions are not desired state (empty or full)";}

// CLOSE MYSQL LINK
mysqli_close($link);

?>