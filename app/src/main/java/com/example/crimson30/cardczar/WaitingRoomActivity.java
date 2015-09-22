package com.example.crimson30.cardczar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ProgressBar;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static android.view.View.INVISIBLE;

public class WaitingRoomActivity extends Activity {
    String result;   // LAMP server result
    String roomname; // room name (database name)
    String username;
    Thread serverWaitThread;   // thread auto-checks server for game start
    Handler handler; // handler intends to GameplayActivity

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiting_room);

        final Button quitButton = (Button) findViewById(R.id.quit_button);

        quitButton.setOnClickListener(
                new Button.OnClickListener(){
                    public void onClick(View v){
                        //If the check for start thread is running or in a waiting mode
                        //interrupt the thread, delete user from the database tell the user
                        // they have quit the game and remove all input widgrts
                        if (serverWaitThread != null && (serverWaitThread.getState() == Thread.State.RUNNABLE ||
                                serverWaitThread.getState() == Thread.State.TIMED_WAITING)){
                            Log.d("Thread", "Interrupting check start thread");
                            serverWaitThread.interrupt();
                        }

                        deleteUser();

                        TextView textMessage = (TextView) findViewById(R.id.waitingTextView);
                        textMessage.setText("You Have Quit The Game");

                        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
                        progressBar.setVisibility(INVISIBLE);

                        quitButton.setVisibility(INVISIBLE);
                    }
                }
        );

        // Get vars from previous activity
        Bundle extras = getIntent().getExtras();
        roomname = extras.getString("roomname");
        username = extras.getString("username");

        serverWaitThread=new Thread(new CheckStartThread());
        serverWaitThread.start();

        handler=new Handler(){
            @Override
             public void handleMessage(Message msg) {
                Intent roomIntent = new Intent(getApplicationContext(), GameplayActivity.class);
                Bundle extras = new Bundle();
                extras.putString("roomname", roomname);
                extras.putString("username", username);
                extras.putBoolean("host", false);
                roomIntent.putExtras(extras);
                startActivity(roomIntent);
            }
        }; // end handler

    } // end onCreate()

    // When host starts game, gamestate:started in SQL is set to true
    // Every 1.21 seconds, this thread checks to see if the host has started game
    // When gamestate:started = true, this method calls the handler that intends Gameplay
    class CheckStartThread implements Runnable {
        private volatile boolean running = true;

        @Override
        public void run() {

            while (running) {
                try {
                    Thread.sleep(1210);
                } catch (InterruptedException e) {
                   Log.d("Thread","Thread interrupt encountered");
                    running = false;
                }

                // Get gamestate:started value from LAMP
                try {
                    String url = "http://ec2-52-3-241-249.compute-1.amazonaws.com/ccz_check_start.php";
                    HttpClient httpclient = new DefaultHttpClient();
                    HttpPost post = new HttpPost(url);
                    List<NameValuePair> urlParameters = new ArrayList<>();
                    urlParameters.add(new BasicNameValuePair("roomname", roomname));
                    post.setEntity(new UrlEncodedFormEntity(urlParameters));
                    HttpResponse response = httpclient.execute(post);
                    result = EntityUtils.toString(response.getEntity());
                    Log.d("Result of check start", result);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                // If gamestate:started is true, send message to handler and stop thread
                Message msg = Message.obtain();
                if (Objects.equals(result, "OK")) {
                    handler.removeCallbacks(this);  // Clear message queue
                    handler.sendMessage(msg);
                    this.running=false;
                }

            } // end while
        } // end run()

    } // end userPollThread

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_waiting_room, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /*
    HTTP call to delete the user from the database.
     */
    private void deleteUser(){
        try {
            String url = "http://ec2-52-3-241-249.compute-1.amazonaws.com/ccz_user_quit.php";
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost post = new HttpPost(url);
            List<NameValuePair> urlParameters = new ArrayList<>();
            urlParameters.add(new BasicNameValuePair("roomname", roomname));
            urlParameters.add(new BasicNameValuePair("username", username));
            post.setEntity(new UrlEncodedFormEntity(urlParameters));
            HttpResponse response = httpclient.execute(post);
            result = EntityUtils.toString(response.getEntity());
            Log.d("Result of user quit", result);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
