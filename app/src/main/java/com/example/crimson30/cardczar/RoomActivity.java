package com.example.crimson30.cardczar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

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
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * File: RoomActivity
 * Author: Group 2 - Card Czar
 * Date: October 4, 2015
 * Class: CMSC495 6381
 * Instructor: Paul Comitz
 * Project: Card Czar Android App
 * Purpose: The class will handle the functionality when a user has hosted a game and is waiting for enough users
 *          to join and start a game
 * Status: Ready
 * Notes: None
 */
public class RoomActivity extends Activity {
    /** Holds the response that comes back from the middleware when database interactions are preformed**/
    String result;
    /** The name of the room for the game. It represents the game being played and is also used to as the database name to hold game information**/
    String roomname;
    /** The name of the user playing the game **/
    String username;
    /** The number of users currently in the game */
    int numusers;

    /**
     * This method is called on creation of the activity. It will display the specified layout to the user
     * and perform some initial data retrieval and setup
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);

        // Get variables from previous activity
        Bundle extras = getIntent().getExtras();
        roomname = extras.getString("roomname");
        username = extras.getString("username");

        // Change app bar title to show the room name
        getActionBar().setTitle("Room = " + roomname);
    }

    /**
     * The method is required by the interface. It currently has no unique functionality for the app
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_room, menu);
        return true;
    }

    /**
     * The method is required by the interface. It currently has no unique functionality for the app
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
     * The method will direct the user to game play activity if they click the Start Game button
     * @param view
     */
    public void intentToGameplay(View view) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        // CHECK FOR VALID NUMBER OF USERS
        System.out.print("Number of users = "+numusers);
        if (numusers > 2 && numusers < 7) {

            // Call appropriate PHP file to update the gamestarte table to show that the game has started
            try {
                String url = "http://ec2-52-3-241-249.compute-1.amazonaws.com/ccz_start.php";
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost post = new HttpPost(url);
                List<NameValuePair> urlParameters = new ArrayList<>();
                urlParameters.add(new BasicNameValuePair("roomname", roomname));
                post.setEntity(new UrlEncodedFormEntity(urlParameters));
                HttpResponse response = httpclient.execute(post);
                result = EntityUtils.toString(response.getEntity());
                Log.d("RoomA start result:", result);
            } catch (IOException e) {
                e.printStackTrace();
            }

            // If server result=OK, direct the user to the game play activity
            if (Objects.equals(result, "OK")) {
                Intent roomIntent = new Intent(this, GameplayActivity.class);
                Bundle extras = new Bundle();
                extras.putString("roomname", roomname);
                extras.putString("username", username);
                extras.putBoolean("host", true);
                roomIntent.putExtras(extras);
                startActivity(roomIntent);
            } // end if server result=OK
        //If the there aren't enough players or too many players than display an error to the user
        } else { // wrong number of users
            TextView statusTextView = (TextView) findViewById(R.id.statusTextView);
            statusTextView.setText("  3-6 users ("+numusers+")");
        } // end if (numusers > 3 && numusers < 7)

    } // end intentToGameplay

    /**
     * This method will query the database for the current list of users and display them to the game host
     * @param view
     */
    public void refreshList(View view) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        // Get list of users from the database
        try {
            String url = "http://ec2-52-3-241-249.compute-1.amazonaws.com/ccz_user_list.php";
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost post = new HttpPost(url);
            List<NameValuePair> urlParameters = new ArrayList<>();
            urlParameters.add(new BasicNameValuePair("roomname",roomname));
            post.setEntity(new UrlEncodedFormEntity(urlParameters));
            HttpResponse response = httpclient.execute(post);
            result = EntityUtils.toString(response.getEntity());
            Log.d("Result of user list", result);
        } catch (IOException e) { e.printStackTrace(); }

        // Get number of users to check before game start
        numusers =  result.split("\\|").length;

       // Update ListView with users
        List<String> userList = new ArrayList<>(Arrays.asList(result.split("\\|")));
        ListView userListView = (ListView) findViewById(R.id.userListView);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, userList);
        userListView.setAdapter(arrayAdapter);

    } // end refreshList()
}
