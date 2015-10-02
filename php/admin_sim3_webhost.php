<h2>Create and start room below.</h2>

<?php

// GET POST DATA
$db_name = urlencode($_POST["roomname"]);


?>

<iframe name="create" src="admin_sim3_create.php?<?php echo $db_name;?>"></iframe>
<br>
<iframe name="jack" src="admin_sim3_joinjack.php?<?php echo $db_name;?>"></iframe>

<br>When the above steps are complete, <b>join the room via the emulator</b>.
<br>
When the waiting room appears on the emulator, click the buttons below to start the game:
<br>
(note: with emulator game, bait is set AFTER starting the game)<br>
<iframe name="set_bait" src="admin_sim_set_bait.php?<?php echo $db_name;?>"></iframe>
<br>
<iframe name="start" src="admin_sim3_start.php?<?php echo $db_name;?>"></iframe>
<br>
<b>On the emulator, select a response.</b><br>
Click submit button below to proceed to the next step.

<form action="admin_sim4_webhost.php" method="post" target="_top">
<input type="hidden" name="roomname" value="<?php echo $db_name;?>" />
<input type="submit" name="submit" value="Submit" />
</form>
