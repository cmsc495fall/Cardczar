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

/**
 * File: WaitingRoomActivity
 * Author: Group 2 - Card Czar
 * Date: October 4, 2015
 * Class: CMSC495 6381
 * Instructor: Paul Comitz
 * Problem: Card Czar Android App
 * Purpose: The class will handle the functionality of checking if the game as started and then
 *          direct the user to the game.
 * Status: Ready
 * Notes: None
 */
public class WaitingRoomActivity extends Activity {
    /** Holds the response that comes back from the middleware when database interactions are preformed**/
    String result;
    /** The name of the room for the game. It represents the game being played and is also used to as the database name to hold game information**/
    String roomname;
    /** The name of the user playing the game **/
    String username;
    /** Thread that runs and continually checks whether the game has started. **/
    Thread serverWaitThread;   // thread auto-checks server for game start
    /** Processes messages sent while waiting for the game to start and updates the display the user sees **/
    Handler handler; // handler intends to GameplayActivity


     /**
     *  This method is called on creation of the activity. It will display the specified layout to the user
     * and perform some initial data retrieval and setup
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiting_room);

        // Get a reference to the guit button in the view
        final Button quitButton = (Button) findViewById(R.id.quit_button);

        /** Handled for the quit button */
        quitButton.setOnClickListener(
                new Button.OnClickListener(){
                    public void onClick(View v){
                        //If the check for start thread is running or in a waiting mode
                        //interrupt the thread, delete user from the database tell the user
                        //and direct them to the quit game activity
                        if (serverWaitThread != null && (serverWaitThread.getState() == Thread.State.RUNNABLE ||
                                serverWaitThread.getState() == Thread.State.TIMED_WAITING)){
                            serverWaitThread.interrupt();
                        }

                        deleteUser();

                        Intent quitIntent = new Intent(getApplicationContext(), QuitGameActivity.class);
                        Bundle extras = new Bundle();
                        quitIntent.putExtras(extras);
                        startActivity(quitIntent);
                    }
                }
        );

        // Get variables from previous activity
        Bundle extras = getIntent().getExtras();
        roomname = extras.getString("roomname");
        username = extras.getString("username");

        //Create and start the thread that will wait for the game to start
        serverWaitThread=new Thread(new CheckStartThread());
        serverWaitThread.start();

        //The handler will receive amessage sent when the thread detects the game has started and
        //will direct the user to the game play activity
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

    /**
     * This class is a thread that, when it is running, will continually check the database to
     * see if the game has started. Once the game has started the thread will stop running and
     * send a message to the handler.
     */
    class CheckStartThread implements Runnable {
       /** Set to true and should stay true until the game start is detected */
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

                // Query the database to see if the game has started
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

                // If the game has started, send message to handler and stop thread
                Message msg = Message.obtain();
                if (Objects.equals(result, "OK")) {
                    handler.removeCallbacks(this);  // Clear message queue
                    handler.sendMessage(msg);
                    this.running=false;
                }

            } // end while
        } // end run()

    } // end userPollThread

    /**
     * THe method is required by the interface. It currently has no unique functionslity for the app
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_waiting_room, menu);
        return true;
    }

    /**
     * THe method is required by the interface. It currently has no unique functionslity for the app
     */
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

    /**
     * The method will make an HTTP call that deletes this user from the database
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
