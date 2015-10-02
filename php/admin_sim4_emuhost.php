<h2>Get bait, get 8 responses, submit responses for each user.</h2>

<?php

// GET POST DATA
$db_name = urlencode($_POST["roomname"]);


?>
<iframe  name="bait" src="admin_sim_get_bait.php?<?php echo $db_name;?>"></iframe>
<br>
<iframe  name="d81" src="admin_sim4_draw_eight.php?<?php echo $db_name;?>"></iframe>
<iframe  name="d82" src="admin_sim4_draw_eight.php?<?php echo $db_name;?>"></iframe>
<br>
<iframe  name="jack" src="admin_sim4_jacksubmit.php?<?php echo $db_name;?>"></iframe>
<iframe  name="jill" src="admin_sim4_jillsubmit.php?<?php echo $db_name;?>"></iframe>
<br>
<iframe  name="bait" src="admin_sim_set_bait.php?<?php echo $db_name;?>"></iframe>


<br>When above steps are done, select an answer in the emulator.
<br>The emulator should show the new bait and host's answers.

Click Submit to view the game status:

<form action="admin_view_all.php?<?php echo $db_name;?>" method="post" target="_self">
<input type="submit" name="submit" value="Submit" />
</form>


