<?php
$db_name = $_SERVER['QUERY_STRING'];
?>

Add Jack<br>
<form action="ccz_join.php" method="post" target="_self">
Room: &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<INPUT size="30" name="roomname" value="<?php echo $db_name;?>"><br>
<INPUT type="hidden" name="username" value="Jack">
<br>
<input type="submit" name="submit" value="Submit" />
</form>
