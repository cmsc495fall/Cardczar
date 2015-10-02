<?php
$db_name = $_SERVER['QUERY_STRING'];
?>

Get bait <br>
<form action="ccz_get_bait.php" method="post" target="_self">
Room: &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<INPUT size="30" name="roomname" value="<?php echo $db_name;?>"><br>
<br>
<input type="submit" name="submit" value="Submit" />
</form>
