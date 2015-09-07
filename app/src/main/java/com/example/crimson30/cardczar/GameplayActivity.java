package com.example.crimson30.cardczar;

import android.app.Activity;
import android.os.Bundle;
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

public class GameplayActivity extends Activity {
    String result;   // LAMP server result
    String roomname; // room name (database name)
    String username;
    Boolean host;     // if role="host", then user can boot other users
    Boolean dealer;
    String [] cards;
    String [] usernames;
    int numusers;
    Boolean game_over;
    Thread turnThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gameplay);

        // Get vars from previous activity
        Bundle extras = getIntent().getExtras();
        roomname = extras.getString("roomname");
        username = extras.getString("username");
        host = extras.getBoolean("host");
        if (host) { dealer=true; } else { dealer=false; }

        // Draw 8
        try {
            String url = "http://ec2-52-3-241-249.compute-1.amazonaws.com/ccz_draw_eight.php";
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost post = new HttpPost(url);
            List<NameValuePair> urlParameters = new ArrayList<>();
            urlParameters.add(new BasicNameValuePair("room",roomname));
            post.setEntity(new UrlEncodedFormEntity(urlParameters));
            HttpResponse response = httpclient.execute(post);
            // Log.d("Response of request", response.toString());
            result = EntityUtils.toString(response.getEntity());
            Log.d("Result of create_db", result);
        } catch (IOException e) { e.printStackTrace(); }

        // Add blank value, so that 1st card is cards[1], not cards[0], etc.
        // Makes it easier for developer to deal with
        result = "empty|"+result;
        cards = result.split("\\|");

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

        // Add blank value, so that 1st card is usernames[1], not usernames[0], etc.
        // Makes it easier for developer to deal with
        result = "empty|"+result;
        usernames = result.split("\\|");
        numusers = usernames.length-1;

        Button button1 = (Button) findViewById(R.id.button1);
        Button button2 = (Button) findViewById(R.id.button2);
        Button button3 = (Button) findViewById(R.id.button3);
        Button button4 = (Button) findViewById(R.id.button4);
        Button button5 = (Button) findViewById(R.id.button5);
        Button button6 = (Button) findViewById(R.id.button6);
        Button button7 = (Button) findViewById(R.id.button7);
        Button button8 = (Button) findViewById(R.id.button8);

        if (dealer) {
            if (numusers>1) { button1.setText("WAIT FOR RESPONSE"); } else { button1.setVisibility(View.GONE);}
            if (numusers>2) { button2.setText("WAIT FOR RESPONSE"); } else { button2.setVisibility(View.GONE);}
            if (numusers>3) { button3.setText("WAIT FOR RESPONSE"); } else { button3.setVisibility(View.GONE);}
            if (numusers>4) { button4.setText("WAIT FOR RESPONSE"); } else { button4.setVisibility(View.GONE);}
            if (numusers>5) { button5.setText("WAIT FOR RESPONSE"); } else { button5.setVisibility(View.GONE);}
            if (numusers>6) { button6.setText("WAIT FOR RESPONSE"); } else { button6.setVisibility(View.GONE);}
            if (numusers>7) { button7.setText("WAIT FOR RESPONSE"); } else { button7.setVisibility(View.GONE);}
            if (numusers>8) { button8.setText("WAIT FOR RESPONSE"); } else { button8.setVisibility(View.GONE);}

        } else {
            button1.setText(cards[1]);
            button2.setText(cards[2]);
            button3.setText(cards[3]);
            button4.setText(cards[4]);
            button5.setText(cards[5]);
            button6.setText(cards[6]);
            button7.setText(cards[7]);
            button8.setText(cards[8]);
        }

        // game_over = false;
        // turnThread=new Thread(new turn());
        // turnThread.start();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_gameplay, menu);
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

    class turn implements Runnable {

        @Override
        public void run() {

            // EITHER SET OR GET BAIT
            if (dealer) {
                // SET BAIT
                try {
                    String url = "http://ec2-52-3-241-249.compute-1.amazonaws.com/ccz_set_bait.php";
                    HttpClient httpclient = new DefaultHttpClient();
                    HttpPost post = new HttpPost(url);
                    List<NameValuePair> urlParameters = new ArrayList<>();
                    urlParameters.add(new BasicNameValuePair("room", roomname));
                    post.setEntity(new UrlEncodedFormEntity(urlParameters));
                    HttpResponse response = httpclient.execute(post);
                    result = EntityUtils.toString(response.getEntity());
                    Log.d("Result of set bait", result);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            } else { // !dealer
                // GET BAIT
                try {
                    String url = "http://ec2-52-3-241-249.compute-1.amazonaws.com/ccz_get_bait.php";
                    HttpClient httpclient = new DefaultHttpClient();
                    HttpPost post = new HttpPost(url);
                    List<NameValuePair> urlParameters = new ArrayList<>();
                    urlParameters.add(new BasicNameValuePair("room", roomname));
                    post.setEntity(new UrlEncodedFormEntity(urlParameters));
                    HttpResponse response = httpclient.execute(post);
                    result = EntityUtils.toString(response.getEntity());
                    Log.d("Result of set bait", result);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } // end if (SET OR GET BAIT)

            // WAIT FOR ACTION
            if (dealer) {
                // wait until all users set their submission (all submissions NOT "WAIT FOR RESPONSE")
                // then wait again for button
            } else {
                // WAIT FOR BUTTON (button notifies thread)
                try {
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            } // end if (WAIT FOR ACTION)
        } // end run()

    } // end turn()

    public void buttonClick(View view) {
        turn.class.notify();
    } // end buttonClick


}
