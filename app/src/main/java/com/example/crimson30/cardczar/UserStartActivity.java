package com.example.crimson30.cardczar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
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
import java.util.List;
import java.util.Objects;

/**
 * File: UserStartActivity
 * Author: Group 2 - Card Czar
 * Date: October 4, 2015
 * Class: CMSC495 6381
 * Instructor: Paul Comitz
 * Project: Card Czar Android App
 * Purpose: The class will handle the functionality when a user chooses to join a game.
 * Status: Ready
 * Notes: None
 */
public class UserStartActivity extends Activity {
    /** Holds the response that comes back from the middleware when database interactions are performed**/
    String result;

    /**
     * This method is called on creation of the activity.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_start);
    }

    /**
     * The method is required by the interface. It currently has no unique functionality for the app
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_user_start, menu);
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
     * The method will direct the user to the waiting room activity if they click the Join Game button
     * @param view
     */
    public void intentToWaitingRoom(View view) {
        //Get the text that the user entered on the screen
        EditText roomJoinEditText = (EditText) findViewById(R.id.roomJoinEditText);
        EditText usernameEditText = (EditText) findViewById(R.id.usernameEditText);
        TextView roomStatusTextView = (TextView) findViewById(R.id.roomStatusTextView);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        // Call appropriate PHP file to add the user to the users table in the database
        try {
            String url = "http://ec2-52-3-241-249.compute-1.amazonaws.com/ccz_join.php";
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost post = new HttpPost(url);
            List<NameValuePair> urlParameters = new ArrayList<>();
            urlParameters.add(new BasicNameValuePair("roomname",roomJoinEditText.getText().toString()));
            urlParameters.add(new BasicNameValuePair("username",usernameEditText.getText().toString()));
            post.setEntity(new UrlEncodedFormEntity(urlParameters));
            HttpResponse response = httpclient.execute(post);
            result = EntityUtils.toString(response.getEntity());
            Log.d("Result of join request", result);
        } catch (IOException e) { e.printStackTrace(); }

        // If the user was successfully added to the database then direct the user to the waiting room activity
        if (Objects.equals(result, "OK")) {
            Intent roomIntent = new Intent(this, WaitingRoomActivity.class);
            Bundle extras = new Bundle();
            extras.putString("roomname", roomJoinEditText.getText().toString());
            extras.putString("username",usernameEditText.getText().toString());
            roomIntent.putExtras(extras);
            startActivity(roomIntent);
        } else {
            roomStatusTextView.setText(result);
        }

    } // end intentToWaitingRoom

}
