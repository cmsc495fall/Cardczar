<?php

// GET POST DATA
$db_name = urlencode($_POST["roomname"]);

// LINK TO SQL
$link = mysqli_connect('localhost', 'root', 'cmsc495fall');
if (!$link) { die('Could not connect: ' . mysqli_connect_error()); }

// Delete DB
$query = 'DROP DATABASE '.$db_name;
mysqli_query($link, $query) or die("Delete DB Error: ".mysqli_error());

// CLOSE DATABASE
mysqli_close($link);

?>