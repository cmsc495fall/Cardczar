<?php
$db_name = $_SERVER['QUERY_STRING'];
?>

Draw Eight for <?php echo $db_name;?><br>
<form action="ccz_draw_eight.php" method="post" target="_self">
<INPUT type="hidden" name="roomname" value="<?php echo $db_name;?>">
<br>
<input type="submit" name="submit" value="Submit" />
</form>
