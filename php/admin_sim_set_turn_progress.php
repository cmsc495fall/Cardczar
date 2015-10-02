<?php
$db_name = $_SERVER['QUERY_STRING'];
?>

Set turn progress <br>
<form action="ccz_set_turn_progress.php" method="post" target="_self">
<br>
  <select name="turnprogress">
  <option value="baitset" selected>baitset</option>
  <option value="allresponsesin">allresponsesin</option>
  <option value="winnerpicked">winnerpicked</option>
  <option value="gameover">gameover</option>
  </select>
<input type="hidden" name="roomname" value="<?php echo $db_name;?>" />
<input type="submit" name="submit" value="Submit" />
</form>
