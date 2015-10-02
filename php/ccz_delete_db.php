<!-- This PHP file is for deleting databases -->

<?php

// GET POST DATA FROM APACHE (DATA PASSED FROM APP) AND URLENCODE IT
$db_name = urlencode($_POST["roomname"]);

// CREATE A LINK TO MYSQL
$link = mysqli_connect('localhost', 'root', 'cmsc495fall');
if (!$link) { die('Could not connect: ' . mysqli_connect_error()); }

// DELETE DB
$query = 'DROP DATABASE '.$db_name;
mysqli_query($link, $query) or die("Delete DB Error: ".mysqli_error());

// CLOSE MYSQL LINK
mysqli_close($link);

?>