package com.example.crimson30.cardczar;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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

public class GameplayActivity extends Activity {
    String result;   // LAMP server result
    String roomname; // room name (database name)
    String username;
    Boolean host;     // if role="host", then user can boot other users
    Boolean dealer;
    String [] cards;
    String [] usernames;
    int numusers;
    Turn turnThread;
    Handler handler; // handler for GUI activities


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gameplay);

        // Get vars from previous activity
        // SET DEALER
        Bundle extras = getIntent().getExtras();
        roomname = extras.getString("roomname");
        username = extras.getString("username");
        host = extras.getBoolean("host");
        if (host) { dealer=true; } else { dealer=false; }

        // SET BAIT
        if (dealer) {
            // SET BAIT
            try {
                String url = "http://ec2-52-3-241-249.compute-1.amazonaws.com/ccz_set_bait.php";
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost post = new HttpPost(url);
                List<NameValuePair> urlParameters = new ArrayList<>();
                urlParameters.add(new BasicNameValuePair("room", roomname));
                urlParameters.add(new BasicNameValuePair("turnprogress", "baitset"));
                post.setEntity(new UrlEncodedFormEntity(urlParameters));
                HttpResponse response = httpclient.execute(post);
                result = EntityUtils.toString(response.getEntity());
                Log.d("GP set_bait result:", result);
            } catch (IOException e) {
                e.printStackTrace();
            }
            TextView baitTextView = (TextView) findViewById(R.id.baitTextView);
            baitTextView.setText(result);
        } // end if (SET BAIT)

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
            Log.d("GP draw_eight result", result);
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
            Log.d("GP user_list result:", result);
        } catch (IOException e) { e.printStackTrace(); }

        // Add blank value, so that 1st card is usernames[1], not usernames[0], etc.
        // Makes it easier for developer to deal with
        result = "empty|"+result;
        usernames = result.split("\\|");
        numusers = usernames.length-1;

        final Button button1 = (Button) findViewById(R.id.button1);
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

        turnThread=new Turn();
        turnThread.start();


        handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                button1.setText("test");
            }
        }; // end handler
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



    class Turn extends Thread {
        private volatile boolean running = true;

        @Override
        public void run() {

            while (running) {

                if (dealer) {

                    // STEP 1: WAIT FOR SUBMISSIONS TO FILL UP
                    boolean waitForAllSubmissions = true;
                    while (waitForAllSubmissions) {
                        try {
                            Thread.sleep(1210);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        // See if server is OK yet for given php file and paramater (see examples about 10 lines up)
                        try {
                            String url = "http://ec2-52-3-241-249.compute-1.amazonaws.com/ccz_get_user_responses.php";
                            HttpClient httpclient = new DefaultHttpClient();
                            HttpPost post = new HttpPost(url);
                            List<NameValuePair> urlParameters = new ArrayList<>();
                            urlParameters.add(new BasicNameValuePair("room", roomname));
                            urlParameters.add(new BasicNameValuePair("action", "waitforallfull"));
                            post.setEntity(new UrlEncodedFormEntity(urlParameters));
                            HttpResponse response = httpclient.execute(post);
                            result = EntityUtils.toString(response.getEntity());
                            Log.d("GP dealer wait result1:", result);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        // When desired result, move past while and to step 2
                        Message message = Message.obtain();
                        if (!result.equals("Submissions are neither empty nor full")) {
                            handler.removeCallbacks(this);  // Clear message queue
                            handler.sendMessage(message);
                            waitForAllSubmissions=false;
                        }
                    } // end while (waitForAllSubmissions)


                    // STEP 2: SET TURNPROGRESS TO all_reponses_in
                    try {
                        String url = "http://ec2-52-3-241-249.compute-1.amazonaws.com/ccz_set_turn_progress.php";
                        HttpClient httpclient = new DefaultHttpClient();
                        HttpPost post = new HttpPost(url);
                        List<NameValuePair> urlParameters = new ArrayList<>();
                        urlParameters.add(new BasicNameValuePair("room", roomname));
                        urlParameters.add(new BasicNameValuePair("progress", "all_responses_in"));
                        post.setEntity(new UrlEncodedFormEntity(urlParameters));
                        HttpResponse response = httpclient.execute(post);
                        result = EntityUtils.toString(response.getEntity());
                        Log.d("GP dealer wait result1:", result);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                    // STEP 3: WAIT FOR BUTTON
                    synchronized (this) {
                        try {
                            wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
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
                        Log.d("GP get_bait result:", result);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    TextView baitTextView = (TextView) findViewById(R.id.baitTextView);
                    baitTextView.setText(result);

                    // WAIT FOR SOMETHING
                    try {
                        wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }  // end if (dealer or !dealer)


            } // end while running
        } // end run()

    } // end turn()


    public void button1Click(View view) {
        System.out.println("button1Click");
        synchronized (turnThread) {
            turnThread.notify(); }

    } // end buttonClick

    public void button2Click(View view) {
        System.out.println("button2Click");
        synchronized (turnThread) {
            turnThread.notify(); }
    } // end buttonClick

    public void button3Click(View view) {
        System.out.println("button3Click");
    } // end buttonClick

    public void button4Click(View view) {
        System.out.println("button4Click");
    } // end buttonClick

    public void button5Click(View view) {
        System.out.println("button5Click");
    } // end buttonClick

    public void button6Click(View view) {
        System.out.println("button6Click");
    } // end buttonClick

    public void button7Click(View view) {
        System.out.println("button7Click");
    } // end buttonClick

    public void button8Click(View view) {
        System.out.println("button8Click");
    } // end buttonClick

}
