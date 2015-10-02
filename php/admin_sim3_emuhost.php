<h2>Create users below.</h2>

<?php

// GET POST DATA
$db_name = urlencode($_POST["roomname"]);


?>

<iframe  name="jack" src="admin_sim3_joinjack.php?<?php echo $db_name;?>"></iframe>
<iframe  name="jill" src="admin_sim3_joinjill.php?<?php echo $db_name;?>"></iframe>

<br>When both users have been created, click the Start button in the emulator.
<br>
When the emulator shows the bait, click the Submit button below.
<form action="admin_sim4_emuhost.php" method="post" target="_top">
<input type="hidden" name="roomname" value="<?php echo $db_name;?>" />
<input type="submit" name="submit" value="Submit" />
</form>
