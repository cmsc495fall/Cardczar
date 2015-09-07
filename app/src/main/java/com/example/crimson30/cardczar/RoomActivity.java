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

public class RoomActivity extends Activity {
    String result;   // LAMP server result
    String roomname; // room name (database name)
    String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);

        // Get vars from previous activity
        Bundle extras = getIntent().getExtras();
        roomname = extras.getString("roomname");
        username = extras.getString("username");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_room, menu);
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

    public void intentToGameplay(View view) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        // Call appropriate PHP file to prepare LAMP server for gameplay
        try {
            String url = "http://ec2-52-3-241-249.compute-1.amazonaws.com/ccz_start.php";
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost post = new HttpPost(url);
            List<NameValuePair> urlParameters = new ArrayList<>();
            urlParameters.add(new BasicNameValuePair("room",roomname));
            post.setEntity(new UrlEncodedFormEntity(urlParameters));
            HttpResponse response = httpclient.execute(post);
            result = EntityUtils.toString(response.getEntity());
            Log.d("Result of start request", result);
        } catch (IOException e) { e.printStackTrace(); }

        // If server result=OK, intend to Gameplay
        if (Objects.equals(result, "OK")) {
            Intent roomIntent = new Intent(this, GameplayActivity.class);
            Bundle extras = new Bundle();
            extras.putString("roomname", roomname);
            extras.putString("username", username);
            extras.putBoolean("host", true);
            roomIntent.putExtras(extras);
            startActivity(roomIntent); }

    } // end intentToGameplay

    // Get list of users from LAMP and update ListView
    public void refreshList(View view) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        // Get list of users from LAMP
        try {
            String url = "http://ec2-52-3-241-249.compute-1.amazonaws.com/ccz_user_list.php";
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost post = new HttpPost(url);
            List<NameValuePair> urlParameters = new ArrayList<>();
            urlParameters.add(new BasicNameValuePair("room",roomname));
            post.setEntity(new UrlEncodedFormEntity(urlParameters));
            HttpResponse response = httpclient.execute(post);
            result = EntityUtils.toString(response.getEntity());
            Log.d("Result of user list", result);
        } catch (IOException e) { e.printStackTrace(); }

       // Update ListView with users
        List<String> userList = new ArrayList<>(Arrays.asList(result.split("\\|")));
        ListView userListView = (ListView) findViewById(R.id.userListView);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, userList);
        userListView.setAdapter(arrayAdapter);

    } // end refreshList()
}
