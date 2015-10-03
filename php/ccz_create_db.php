<?php
// This PHP file is for creating the database that facilitates communication between the players.
// The database stores 4 tables:
//  gamestate: id, started, timecreated, round, numusers, dealer, host, winner, activebait, selectedresponse, turnprogress
//  users: id, username, points, submission, timelastcontact
//  bait: id, text
//  responses: id, text


// THE FOLLOWING FUNCTION IS FOR URLENCODING ARRAYS (PHP URLENCODE EXPECTS SINGLE STRING, NOT ARRAY) 
// FUNCTION TAKEN FROM AMB: http://stackoverflow.com/questions/8734626/how-to-urlencode-a-multidimensional-array
function urlencode_array($array) {
    $out_array = array();
    foreach($array as $key => $value) {
    $out_array[urlencode($key)] = urlencode(str_replace(array("\r", "\n"), "", $value));
    }
return $out_array;
}


// GET POST DATA FROM APACHE (DATA PASSED FROM APP) AND URLENCODE IT
$db_name = urlencode($_POST["roomname"]);
$username = urlencode($_POST["username"]);

// CHECK TO SEE IF DATABASE ALREADY EXISTS
// CREATE A LINK TO MYSQL
$temp_link = mysqli_connect('localhost', 'root', 'cmsc495fall', $db_name);

// IF DB EXISTS, DELETE AND CREATE IF OLD; IF DB DOESN'T EXIST, CREATE IT
if ($temp_link)
  { // IF DATABASE ALREADY EXISTS, CHECK AGE
    // GET gamestate timecreated
    $query = "SELECT timecreated, turnprogress FROM gamestate";
    $result = mysqli_query($temp_link, $query);
    $row = mysqli_fetch_assoc($result);
    $time_created = $row[timecreated];
    $time_since_created = time()-$time_created;

    if ($time_since_created > 3600 || $row[turnprogress] == "gameover")
      {  // DATABASE IS MORE THAN AN HOUR OLD OR TURNPROGRESS=GAMEOVER
         // DROP DATABASE AND READY FOR ANOTHER INSTANCE
         $query = "DROP DATABASE ".$db_name;
         $result = mysqli_query($temp_link, $query);
         $create_db = true;
      } else
      {  // DATABASE IS LESS THAN AN HOUR OLD AND NOT GAMEOVER
         // DO NOT CREATE DB SINCE IT'S STILL ACTIVE
         echo "Room already exists.";
         $create_db = false;
      }

  }
  else // $temp_link did not work because database doesn't exist
  { // IF DATABASE DOES NOT EXIST, OK TO CREATE A NEW ONE
    $create_db = true;
  }

// CLOSE TEMP LINK
mysqli_close($temp_link);


if ($create_db) { // ONLY DO THE FOLLOWING CODE (DB CREATION) IF DB DOESN'T EXIST (SEE ABOVE)

  // LINK TO MYSQL
  $link = mysqli_connect('localhost', 'root', 'cmsc495fall');
  if (!$link) { die('Could not connect: ' . mysqli_connect_error()); }

  // CREATE DB FROM ROOM NAME PASSED FROM APP
  $query = 'CREATE DATABASE '.$db_name;
  mysqli_query($link, $query) or die("Create DB Error: ".mysqli_error());

  // SELECT THE DB CORRESPONDING TO THE ROOM NAME
  mysqli_select_db($link, $db_name) or die("Select DB Error: ".mysqli_error());

  // CREATE TABLES (GAMESTATE, USERS, Q & A)
  $query = 'CREATE TABLE gamestate (id INT NOT NULL AUTO_INCREMENT, PRIMARY KEY(id), started BOOLEAN, timecreated INT, round INT, numusers INT, dealer INT, host INT, winner INT, activebait VARCHAR(128), selectedresponse VARCHAR(128), turnprogress VARCHAR(15));';
  mysqli_query($link, $query) or die("Create gamestate Error: ".mysql_error());

  $query = 'CREATE TABLE users (id INT NOT NULL AUTO_INCREMENT, PRIMARY KEY(id), username VARCHAR(30) UNIQUE, points INT, submission VARCHAR(128), timelastcontact TIMESTAMP);';
  mysqli_query($link, $query) or die("Create users Error: ".mysql_error());

  $query = 'CREATE TABLE bait (id INT NOT NULL AUTO_INCREMENT, PRIMARY KEY(id), text VARCHAR(128));';
  mysqli_query($link, $query) or die("Create bait Error: ".mysql_error());

  $query = 'CREATE TABLE responses (id INT NOT NULL AUTO_INCREMENT, PRIMARY KEY(id), text VARCHAR(128));';
  mysqli_query($link, $query) or die("Create responses Error: ".mysql_error());


  // INSERT INITIAL GAME DATA INTO TABLES
  // INSERT GAMESTATE DATA
  $query = "INSERT INTO gamestate (started, timecreated, round, numusers, dealer, host, activebait, turnprogress) VALUES (FALSE,".time().", 0, 1, 0, 1, 'NO_BAIT_YET', 'NONE');";
  mysqli_query($link, $query) or die("gamestate INSERT Error: ".mysql_error());

  // INSERT USER DATA
  // BECAUSE username COULD CONTAIN SQL INJECTION ATTACK,
  // USE PREPARED STATEMENT TO SEPERATE QUERY CODE FROM DATA TO PREVENT SQL INJECTION
  $prepared_statment = mysqli_prepare($link, "INSERT INTO users (username, points, submission) VALUES (?, ?, ?)");
  mysqli_stmt_bind_param($prepared_statment, 'sis', $u, $p, $s);
  $u = $username;
  $p = 0;
  $s = "WAIT FOR RESPONSE";
  mysqli_stmt_execute($prepared_statment);
  mysqli_stmt_close($prepared_statment);

  // INSERT BAIT DATA
  // PULL BAIT FILE FROM APACHE HTML DIRECTORY AND STORE AS URLENCODED DATA IN DATABASE (IN RANDOM ORDER)
  $bait=file('/var/www/html/b.txt');
  shuffle($bait);
  $bait = urlencode_array($bait);
  $bait = "('" .implode("'),('",$bait)."')";
  $query = "INSERT INTO bait (text) VALUES $bait;";
  mysqli_query($link, $query) or die("Error adding bait: ".mysql_error());

  // INSERT RESPONSES DATA
  // PULL RESPONSES FILE FROM APACHE HTML DIRECTORY AND STORE AS URLENCODED DATA IN DATABASE (IN RANDOM ORDER)
  $responses=file('/var/www/html/r.txt');
  shuffle($responses);
  $responses = urlencode_array($responses);
  $responses = "('" .implode("'),('",$responses)."')";
  $query = "INSERT INTO responses (text) VALUES $responses;";
  if (mysqli_query($link, $query)) {
      echo "OK";
  } else {
      echo 'Error adding responses: ' . mysql_error() . "\n";
  }

  // CLOSE MYSQL LINK
  mysqli_close($link);

} // end if $create_db

?>
