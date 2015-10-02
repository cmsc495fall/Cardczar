<?php
$db_name = $_SERVER['QUERY_STRING'];
?>

Create room <?php echo $db_name;?><br>
<form action="ccz_create_db.php" method="post" target="_self">
<INPUT type="hidden" name="roomname" value="<?php echo $db_name;?>">
<INPUT type="hidden" name="username" value="host">
<br>
<input type="submit" name="submit" value="Submit" />
</form>
