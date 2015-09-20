<?php

// FUNCTION TAKEN FROM AMB: http://stackoverflow.com/questions/8734626/how-to-urlencode-a-multidimensional-array
function urlencode_array($array) {
    $out_array = array();
    foreach($array as $key => $value) {
    $out_array[urlencode($key)] = urlencode($value);
    }
return $out_array;
}


// LINK TO SQL
$link = mysql_connect('localhost', 'root', 'cmsc495fall');
if (!$link) { die('Could not connect: ' . mysql_error()); }

// GET POST DATA
$db_name = urlencode($_POST["roomname"]);

// Delete DB
$query = 'DROP DATABASE '.$db_name;
mysql_query($query, $link) or die("Delete DB Error: ".mysql_error());

// CLOSE DATABASE
mysql_close($link);

?>