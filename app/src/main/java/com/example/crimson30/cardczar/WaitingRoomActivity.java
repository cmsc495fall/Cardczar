package com.example.crimson30.cardczar;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Movie;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
import java.util.Timer;
import java.util.TimerTask;

public class WaitingRoomActivity extends Activity {
    String result;
    String roomName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        roomName = getIntent().getExtras().getString("roomName", "ERROR: ROOM NOT RECEIVED");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiting_room);
    }

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

    public void intentToGameplay() {

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        try {
            String url = "http://ec2-52-3-241-249.compute-1.amazonaws.com/ccz_check_start.php";
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost post = new HttpPost(url);
            List<NameValuePair> urlParameters = new ArrayList<>();
            urlParameters.add(new BasicNameValuePair("room",roomName));
            post.setEntity(new UrlEncodedFormEntity(urlParameters));
            HttpResponse response = httpclient.execute(post);
            // Log.d("Response of request", response.toString());
            result = EntityUtils.toString(response.getEntity());
            Log.d("Result of request", result);
        } catch (IOException e) { e.printStackTrace(); }

        if (Objects.equals(result, "OK")) {
            Intent roomIntent = new Intent(this, GameplayActivity.class);
            roomIntent.putExtra("roomName",roomName);
            startActivity(roomIntent);
        } else {
            // waitingTextView.setText(result);
        }

    }
}
