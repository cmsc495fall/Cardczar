<?php

// LINK TO SQL
$link = mysql_connect('localhost', 'root', 'cmsc495fall');
if (!$link) { die('Could not connect: ' . mysql_error()); }

// GET POST DATA
$db_name = urlencode($_POST["roomname"]);
$username = urlencode($_POST["username"]);

// SELECT DB
if (mysql_select_db($db_name, $link)) {
    $connected = TRUE;
} else {
    echo 'Error selecting database: '.mysql_error();
    $connected = FALSE;
}

// UPDATE TABLES
if (connected) {

 //Check to see if the user already exists
 $result = mysql_query("SELECT count(*) from users where username='$username'");
 $row = mysql_fetch_assoc($result);
 $usercount = $row['count(*)'];
 if ($usercount > 0){
     die("Username '$username' has already been taken. Choose another username");
  }

  $result = mysql_query("SELECT count(*) from users");
  $row = mysql_fetch_assoc($result);
  $usercount = $row['count(*)'];
  if ($usercount == 6){
    die("Room $db_name is alreay full. Please join another room");
  }

  $result = mysql_query("SELECT started from gamestate");
  $row = mysql_fetch_assoc($result);
  if ($row[started]){
     die("The game has already started so you will not be able to join. Please join another room");
  }


  // ADD USER TO users
  $query = "INSERT INTO users (username, points, submission, timelastcontact) VALUES ('".$username."', 0, 'WAIT FOR RESPONSE', ".time().");";
  if (mysql_query($query, $link)) {
    echo "OK";
    $inserted = TRUE;
  } else {
      echo 'Error: ' . mysql_error() . "\n";
      $inserted = FALSE;
  }

  // INCREMENT NUMUSERS IN gamestate
  if ($inserted) {
    $tablecontents = mysql_query("SELECT * FROM gamestate");
    if ($myrow = mysql_fetch_array($tablecontents))
        $numusers = $myrow["numusers"];
        $numusers++;
        $query = "UPDATE gamestate SET numusers=".$numusers." WHERE id=1;  ";
        mysql_query($query, $link) or die("Update DB Error: ".mysql_error());
  }
}

// CLOSE DATABASE
mysql_close($link);

?>