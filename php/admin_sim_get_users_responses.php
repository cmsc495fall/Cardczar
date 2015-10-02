<?php
$db_name = $_SERVER['QUERY_STRING'];
?>

Get users responses <br>
<form action="ccz_get_users_responses.php" method="post" target="_self">
<br>
<input type="hidden" name="roomname" value="<?php echo $db_name;?>" />
<input type="hidden" name="action" value="waitforallfull">
<input type="submit" name="submit" value="Submit" />
</form>
