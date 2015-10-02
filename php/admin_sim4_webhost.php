<h2>Select answer for Jack.</h2>

<?php

// GET POST DATA
$db_name = urlencode($_POST["roomname"]);


?>

<iframe  name="bait" src="admin_sim_get_bait.php?<?php echo $db_name;?>"></iframe>
<br>
<iframe  name="d81" src="admin_sim4_draw_eight.php?<?php echo $db_name;?>"></iframe>
<br>
<iframe  name="jack" src="admin_sim4_jacksubmit.php?<?php echo $db_name;?>"></iframe>
<br>

When you've selected Jack's answer, set turnprogress to allresponsesin.<br>
<iframe  name="jack" src="admin_sim_set_turn_progress.php?<?php echo $db_name;?>"></iframe>
<br>
Now click submit to continue:
<form action="admin_sim5_webhost.php" method="post" target="_top">
<input type="hidden" name="roomname" value="<?php echo $db_name;?>" />
<input type="submit" name="submit" value="Submit" />
</form>
