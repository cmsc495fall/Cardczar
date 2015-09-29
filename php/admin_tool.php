<?php
// PULL POST DATA
$db_name = $_SERVER['QUERY_STRING']
?>
<html>
<body>
<table border=1 cellpadding=2>

 <tr>
  <td>
  ccz_create_db.php<br>
  <form action="ccz_create_db.php" method="post" target="_top">
  Room: &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<INPUT size="30" name="roomname" value="<?php echo $db_name;?>"><br>
  Hostname: <INPUT size="30" name="username">
  <br>
  <input type="submit" name="submit" value="Submit" />
  </form>
  </td>

  <td>
  ccz_join.php<br>
  <form action="ccz_join.php" method="post" target="_top">
  Room: &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<INPUT size="30" name="roomname" value="<?php echo $db_name;?>"><br>
  Username: <INPUT size="30" name="username">
  <br>
  <input type="submit" name="submit" value="Submit" />
  </form>
  </td>

  <td>
  ccz_start.php<br>
  <form action="ccz_start.php" method="post" target="_top">
  Room: <INPUT size="30" name="roomname" value="<?php echo $db_name;?>">
  <br>
  <input type="submit" name="submit" value="Submit" />
  </form>
  </td>

  <td>
  ccz_check_start.php<br>
  <form action="ccz_check_start.php" method="post" target="_top">
  Room: <INPUT size="30" name="roomname" value="<?php echo $db_name;?>">
  <br>
  <input type="submit" name="submit" value="Submit" />
  </form>
  </td>


 </tr>

 <tr>

  <td>
  ccz_set_bait.php<br>
  <form action="ccz_set_bait.php" method="post" target="_top">
  Room: &nbsp;<INPUT size="30" name="roomname" value="<?php echo $db_name;?>"><br>
  Progress: 
  <select name="turnprogress">
  <option value="baitset" selected>baitset</option>
  <option value="allresponsesin">allresponsesin</option>
  <option value="winnerpicked">winnerpicked</option>
  <option value="gameover">gameover</option>
  </select>
  <br>
  <input type="submit" name="submit" value="Submit" />
  </form>
  </td>

  <td>
  ccz_draw_eight.php<br>
  <form action="ccz_draw_eight.php" method="post" target="_top">
  Room: <INPUT size="30" name="roomname" value="<?php echo $db_name;?>">
  <br>
  <input type="submit" name="submit" value="Submit" />
  </form>
  </td>

  <td>
  ccz_user_list.php<br>
  <form action="ccz_user_list.php" method="post" target="_top">
  Room: <INPUT size="30" name="roomname" value="<?php echo $db_name;?>"><br>
  <br>
  <input type="submit" name="submit" value="Submit" />
  </form>
  </td>

  <td>
  ccz_get_bait.php<br>
  <form action="ccz_get_bait.php" method="post" target="_top">
  Room: <INPUT size="30" name="roomname" value="<?php echo $db_name;?>">
  <br>
  <input type="submit" name="submit" value="Submit" />
  </form>
  </td>

 </tr>

 <tr>

  <td>
  ccz_get_users_responses.php<br>
  <form action="ccz_get_users_responses.php" method="post" target="_top">
  Room: &nbsp;<INPUT size="30" name="roomname" value="<?php echo $db_name;?>"><br>
  Action: 
  <select name="action">
  <option value="waitforallfull" selected>waitforallfull</option>
  <option value="waitforallempty">waitforallempty</option>
  </select>
  <br>
  <input type="submit" name="submit" value="Submit" />
  </form>
  </td>

  <td>
  ccz_user_submit.php<br>
  <form action="ccz_user_submit.php" method="post" target="_top">
  Room: &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<INPUT size="30" name="roomname" value="<?php echo $db_name;?>"><br>
  Username: &nbsp;&nbsp;<INPUT size="30" name="username"><br>
  Submission: <INPUT size="30" name="submission">
  <br>
  <input type="submit" name="submit" value="Submit" />
  </form>
  </td>

  <td>
  ccz_get_turn_progress.php<br>
  <form action="ccz_get_turn_progress.php" method="post" target="_top">
  Room: <INPUT size="30" name="roomname" value="<?php echo $db_name;?>">
  <br>
  <input type="submit" name="submit" value="Submit" />
  </form>
  </td>

  <td>
  ccz_set_turn_progress.php<br>
  <form action="ccz_set_turn_progress.php" method="post" target="_top">
  Room: &nbsp;<INPUT size="30" name="roomname" value="<?php echo $db_name;?>"><br>
  Progress: 
  <select name="turnprogress">
  <option value="baitset" selected>baitset</option>
  <option value="allresponsesin">allresponsesin</option>
  <option value="winnerpicked">winnerpicked</option>
  <option value="gameover">gameover</option>
  </select>
  <br>
  <input type="submit" name="submit" value="Submit" />
  </form>
  </td>

 </tr>

 <tr>

  <td>
  ccz_process_winner.php<br>
  <form action="ccz_process_winner.php" method="post" target="_top">
  Room: &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<INPUT size="30" name="roomname" value="<?php echo $db_name;?>"><br>
  Chosen response: <INPUT size="30" name="response">
  <br>
  <input type="submit" name="submit" value="Submit" />
  </form>
  </td>

  <td>
  ccz_get_winning_response.php<br>
  <form action="ccz_get_winning_response.php" method="post" target="_top">
  Room: <INPUT size="30" name="roomname" value="<?php echo $db_name;?>">
  <br>
  <input type="submit" name="submit" value="Submit" />
  </form>
  </td>

  <td>
  ccz_change_single_response.php<br>
  <form action="ccz_change_single_response.php" method="post" target="_top">
  Room: &nbsp;<INPUT size="30" name="roomname" value="<?php echo $db_name;?>"><br>
  User: 
  <select name="user">
  <option value="1" selected>1</option>
  <option value="2">2</option>
  <option value="3">3</option>
  <option value="4">4</option>
  <option value="5">5</option>
  <option value="6">6</option>
  <option value="7">7</option>
  <option value="8">8</option>
  <option value="9">9</option>
  </select>
  <br>
  <input type="submit" name="submit" value="Submit" />
  </form>
  </td>

   <td>
  ccz_get_winner.php<br>
  <form action="ccz_get_winner.php" method="post" target="_top">
  Room: &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<INPUT size="30" name="roomname" value="<?php echo $db_name;?>"><br>
  <br>
  <input type="submit" name="submit" value="Submit" />
  </form>
  </td>


 </tr>

 <tr>

  <td>
    ccz_get_user_points.php<br>
    <form action="ccz_get_user_points.php" method="post" target="_top">
    Room: &nbsp;<INPUT size="30" name="roomname" value="<?php echo $db_name;?>"><br>
    User:
    <select name="user">
    <option value="1" selected>1</option>
    <option value="2">2</option>
    <option value="3">3</option>
    <option value="4">4</option>
    <option value="5">5</option>
    <option value="6">6</option>
    <option value="7">7</option>
    <option value="8">8</option>
    </select>
    <br>
    <input type="submit" name="submit" value="Submit" />
    </form>
    </td>

  <td>
  ccz_user_quit.php<br>
  <form action="ccz_user_quit.php" method="post" target="_top">
  Room: &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<INPUT size="30" name="roomname" value="<?php echo $db_name;?>"><br>
  Username: &nbsp;&nbsp;<INPUT size="30" name="username"><br>
  <br>
  <input type="submit" name="submit" value="Submit" />
  </form>
  </td>


   <td>
    ccz_get_user_count.php<br>
    <form action="ccz_get_user_count.php" method="post" target="_top">
    Room: &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<INPUT size="30" name="roomname" value="<?php echo $db_name;?>"><br>
    <br>
    <input type="submit" name="submit" value="Submit" />
    </form>
    </td>


   <td>
       ccz_delete_db.php<br>
       <form action="ccz_delete_db.php" method="post" target="_top">
       Room: &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<INPUT size="30" name="roomname" value="<?php echo $db_name;?>"><br>
       <br>
       <input type="submit" name="submit" value="Submit" />
       </form>
   </td>



 </tr>

 <tr>
    <td>
        ccz_get_winning_username.php<br>
        <form action="ccz_get_winning_username.php" method="post" target="_top">
             Room: &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<INPUT size="30" name="roomname" value="<?php echo $db_name;?>"><br>
             <br>
             <input type="submit" name="submit" value="Submit" />
        </form>
    </td>
 </tr>

</table>
