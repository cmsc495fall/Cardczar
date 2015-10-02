<?php
$db_name = $_SERVER['QUERY_STRING'];
?>

Get bait <br>
<form action="ccz_process_winner.php" method="post" target="_self">
Selected response: <INPUT size="30" name="response"><br>
<br>
<input type="hidden" name="roomname" value="<?php echo $db_name;?>" />
<input type="submit" name="submit" value="Submit" />
</form>
