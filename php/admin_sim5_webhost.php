<?php

// GET POST DATA
$db_name = urlencode($_POST["roomname"]);

?>

<h2>As dealer, copy and paste a response to select it.</h2>

<iframe  name="jack" src="admin_sim_get_users_responses.php?<?php echo $db_name;?>"></iframe>
<br>
Paste an answer below:
<br>
<iframe  name="jack" src="admin_sim_process_winner.php?<?php echo $db_name;?>"></iframe>

<br>
The emulator should show the winner and the users points.<br>

Click Submit to view the game status:

<form action="admin_view_all.php?<?php echo $db_name;?>" method="post" target="_self">
<input type="submit" name="submit" value="Submit" />
</form>