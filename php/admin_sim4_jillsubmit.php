<?php
$db_name = $_SERVER['QUERY_STRING'];
?>

Submit Jack's answer<br>
<form action="ccz_user_submit.php" method="post" target="_self">
Answer: &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<INPUT size="30" name="submission"><br>
<INPUT type="hidden" name="username" value="Jill">
<INPUT type="hidden" name="roomname" value="<?php echo $db_name;?>">
<br>
<input type="submit" name="submit" value="Submit" />
</form>
