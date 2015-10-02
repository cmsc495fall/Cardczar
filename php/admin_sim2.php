<?php

// PULL POST DATA
$emuhost = urlencode($_POST["emuhost"]);

if ($emuhost=="yes") {

echo '

<h2>On the emulator, select HOST GAME (leave username as host), then START ROOM, then click the Submit button below.</h2>
<form action="admin_sim3_emuhost.php" method="post" target="_top">
Room: <INPUT size="30" name="roomname"><br>
<br>
<input type="submit" name="submit" value="Submit" />
</form>

';


} else {

echo '

<h2>Enter a room name, then click Submit.</h2>
<form action="admin_sim3_webhost.php" method="post" target="_top">
Room: <INPUT size="30" name="roomname"><br>
<br>
<input type="submit" value="Submit">
</form>

';


}



?>
