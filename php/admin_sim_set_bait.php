<?php
$db_name = $_SERVER['QUERY_STRING'];
?>

Set bait <br>
<form action="ccz_set_bait.php" method="post" target="_self">
<br>
<input type="hidden" name="roomname" value="<?php echo $db_name;?>" />
<input type="hidden" name="turnprogress" value="baitset" />
<input type="submit" name="submit" value="Submit" />
</form>
