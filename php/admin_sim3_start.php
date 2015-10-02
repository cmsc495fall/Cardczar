<?php
$db_name = $_SERVER['QUERY_STRING'];
?>

Start the game<br>
<form action="ccz_start.php" method="post" target="_self">
<INPUT type="hidden" name="roomname" value="<?php echo $db_name;?>">
<br>
<input type="submit" name="submit" value="Submit" />
</form>
