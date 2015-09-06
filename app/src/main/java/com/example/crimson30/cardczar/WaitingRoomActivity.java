package com.example.crimson30.cardczar;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.WaitingThreadAborter;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class WaitingRoomActivity extends Activity {
    String result;
    String roomname;
    String username;
    Thread thread;
    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        roomname = extras.getString("roomname");
        username = extras.getString("username");
        setContentView(R.layout.activity_waiting_room);

        thread=new Thread(new userPollThread());
        thread.start();

        handler=new Handler(){
            @Override
             public void handleMessage(Message msg) {
                Intent roomIntent = new Intent(getApplicationContext(), GameplayActivity.class);
                Bundle extras = new Bundle();
                extras.putString("roomname", roomname);
                extras.putString("username", username);
                roomIntent.putExtras(extras);
                startActivity(roomIntent);
            }
        };

    }

    class userPollThread implements Runnable {
        private volatile boolean running = true;

        @Override
        public void run() {

            while (running) {
                try {
                    Thread.sleep(1210);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                try {
                    String url = "http://ec2-52-3-241-249.compute-1.amazonaws.com/ccz_check_start.php";
                    HttpClient httpclient = new DefaultHttpClient();
                    HttpPost post = new HttpPost(url);
                    List<NameValuePair> urlParameters = new ArrayList<>();
                    urlParameters.add(new BasicNameValuePair("room", roomname));
                    post.setEntity(new UrlEncodedFormEntity(urlParameters));
                    HttpResponse response = httpclient.execute(post);
                    result = EntityUtils.toString(response.getEntity());
                    Log.d("Result of check start", result);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Message message = Message.obtain();
                if (Objects.equals(result, "OK")) {
                    handler.removeCallbacks(this);  // Clear message queue!
                    handler.sendMessage(message);
                    this.running=false;
                }


            }
        }

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
            urlParameters.add(new BasicNameValuePair("room",roomname));
            post.setEntity(new UrlEncodedFormEntity(urlParameters));
            HttpResponse response = httpclient.execute(post);
            // Log.d("Response of request", response.toString());
            result = EntityUtils.toString(response.getEntity());
            Log.d("Result of request", result);
        } catch (IOException e) { e.printStackTrace(); }

        if (Objects.equals(result, "OK")) {
            Intent roomIntent = new Intent(this, GameplayActivity.class);
            roomIntent.putExtra("roomname",roomname);
            startActivity(roomIntent);
        } else {
            // waitingTextView.setText(result);
        }

    }
}
