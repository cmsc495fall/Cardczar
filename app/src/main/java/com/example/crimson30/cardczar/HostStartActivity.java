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

import org.apache.http.HttpEntity;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * File: HostStartActivity
 * Author: Group 2 - Card Czar
 * Date: October 4, 2015
 * Class: CMSC495 6381
 * Instructor: Paul Comitz
 * Project: Card Czar Android App
 * Purpose: The class will handle the functionality when a Card Czar player decides to host a new game
 * Status: Ready
 * Notes: None
 */
public class HostStartActivity extends Activity {
    /** Holds the response that comes back from the middleware when database interactions are performed**/
    String result;

    /**
     * This method is called on creation of the activity.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host_start);
    }

    /**
     * The method is required by the interface. It currently has no unique functionality for the app
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_host_start, menu);
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
     * The method will capture the game/room information entered by the user, bundle
     * the information to send to the next activity and direct the user to the room activity.
     * @param view
     */
    public void intentToRoom(View view) {
        //Get the user entered information
        EditText roomnameEditText = (EditText) findViewById(R.id.roomnameEditText);
        EditText hostnameEditText = (EditText) findViewById(R.id.hostnameEditText);
        TextView serverStatusTextView = (TextView) findViewById(R.id.serverStatusTextView);

        //The roomname will be used to create the database and can only contain
        //certain information. Use a regular expression match to ensure the
        //roomname contains no illegal characters. If it does display an error message
        //to the user and stay on this sctivity
        String pattern = "\\w+";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher((roomnameEditText.getText()));
        if (!m.matches()){
            Log.d("Room start error", "No match found");
            serverStatusTextView.setText("The roomname can only contain the characters a-z,A-Z,0-9,_");
            return;
        }else{
            System.out.println(m.group(0));
        }


        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        // Make an HTTP call that will create the database, create the game tables in the
        //database, and populate the tables with their initial values
        try {
            String url = "http://ec2-52-3-241-249.compute-1.amazonaws.com/ccz_create_db.php";
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost post = new HttpPost(url);
            List<NameValuePair> urlParameters = new ArrayList<>();
            urlParameters.add(new BasicNameValuePair("roomname",roomnameEditText.getText().toString()));
            urlParameters.add(new BasicNameValuePair("username",hostnameEditText.getText().toString()));
            post.setEntity(new UrlEncodedFormEntity(urlParameters));
            HttpResponse response = httpclient.execute(post);
            // Log.d("Response of request", response.toString());
            result = EntityUtils.toString(response.getEntity());
            response.getEntity().consumeContent();
            Log.d("Result of create_db", result);
        } catch (IOException e) { e.printStackTrace(); }

        // If server result=OK, direct the user to the room activity
        if (Objects.equals(result, "OK")) {
            Intent roomIntent = new Intent(this, RoomActivity.class);
            Bundle extras = new Bundle();
            extras.putString("roomname", roomnameEditText.getText().toString());
            extras.putString("username",hostnameEditText.getText().toString());
            roomIntent.putExtras(extras);
            startActivity(roomIntent);
        } else {
            serverStatusTextView.setText(result);
        }
    } // end intentToRoom()

}
