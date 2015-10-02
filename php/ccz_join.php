<!-- This PHP file is for adding a user to the database -->

<?php

// GET POST DATA FROM APACHE (DATA PASSED FROM APP) AND URLENCODE IT
$db_name = urlencode($_POST["roomname"]);
$username = urlencode($_POST["username"]);

// LINK TO MYSQL
$link = mysqli_connect('localhost', 'root', 'cmsc495fall');
if (!$link) { die('Could not connect: ' . mysqli_connect_error()); }

// SELECT THE DB CORRESPONDING TO THE ROOM NAME
if (mysqli_select_db($link, $db_name)) {
    $connected = TRUE;
} else {
    echo 'Error selecting database: '.mysqli_error();
    $connected = FALSE;
}

// UPDATE gamestate and users TABLES
if (connected) {

 // CHECK TO SEE IF USER ALREADY EXISTS USING PREPARED STATEMENT TO GUARD AGAINST SQL INJECTION ATTACKS
 $prepared_statment = mysqli_prepare($link, "SELECT count(*) from users where username=?");
 mysqli_stmt_bind_param($prepared_statment, 's', $u);
 $u = $username;
 mysqli_stmt_execute($prepared_statment);
 $result = mysqli_stmt_get_result($prepared_statment);
 $row = mysqli_fetch_array($result, MYSQLI_ASSOC);

 // CHECK FOR EXISTING USERNAME
 $usercount = $row['count(*)'];
 if ($usercount > 0){
     die("Username '$username' has already been taken. Choose another username");
  }

  // CHECK TO SEE IF ROOM IS FULL
  $query = "SELECT count(*) from users";
  $result = mysqli_query($link, $query);
  $row = mysqli_fetch_assoc($result);
  $usercount = $row['count(*)'];
  if ($usercount == 6){
    die("Room $db_name is already full. Please join another room");
  }

  // CHECK TO SEE IF GAME HAS STARTED
  $query = "SELECT started from gamestate";
  $result = mysqli_query($link, $query);
  $row = mysqli_fetch_assoc($result);
  if ($row[started]){
     die("The game has already started so you will not be able to join. Please join another room");
  }

  // ADD USER TO users (DIE KEEPS BAD CASES [like too many users] FROM GETTING HERE)
  $prepared_statment = mysqli_prepare($link, "INSERT INTO users (username, points, submission) VALUES (?, ?, ?)");
  mysqli_stmt_bind_param($prepared_statment, 'sis', $u, $p, $s);
  $u = $username;
  $p = 0;
  $s = "WAIT FOR RESPONSE";
  if (mysqli_stmt_execute($prepared_statment)) { echo "OK"; $inserted = true; }
  mysqli_stmt_close($prepared_statment);

  // INCREMENT NUMUSERS IN gamestate
  if ($inserted) {
  $query = "SELECT * FROM gamestate";
  $result = mysqli_query($link, $query);
    if ($myrow = mysqli_fetch_array($result))
        $numusers = $myrow["numusers"];
        $numusers++;
        $query = "UPDATE gamestate SET numusers=".$numusers." WHERE id=1;  ";
        mysqli_query($link, $query) or die("Update DB Error: ".mysqli_error());
  }
}

// CLOSE MYSQL LINK
mysqli_close($link);

?>