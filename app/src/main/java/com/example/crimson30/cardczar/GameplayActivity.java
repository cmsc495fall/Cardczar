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
    int myUserNum;
    int buttonClicked;
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
                urlParameters.add(new BasicNameValuePair("roomname", roomname));
                urlParameters.add(new BasicNameValuePair("turnprogress", "baitset"));
                post.setEntity(new UrlEncodedFormEntity(urlParameters));
                HttpResponse response = httpclient.execute(post);
                result = EntityUtils.toString(response.getEntity());
                Log.d("GP set_bait result:", result);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } // end if (SET BAIT)

        // Draw 8
        try {
            String url = "http://ec2-52-3-241-249.compute-1.amazonaws.com/ccz_draw_eight.php";
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost post = new HttpPost(url);
            List<NameValuePair> urlParameters = new ArrayList<>();
            urlParameters.add(new BasicNameValuePair("roomname",roomname));
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
            urlParameters.add(new BasicNameValuePair("roomname",roomname));
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
        for (String value : usernames) {
            int i=0;
            i++;
            if (value.equals(username)) {
                myUserNum = i;
                break;
            }
        }
        System.out.println("myUserNum = "+myUserNum);

        final Button button1 = (Button) findViewById(R.id.button1);
        final Button button2 = (Button) findViewById(R.id.button2);
        final Button button3 = (Button) findViewById(R.id.button3);
        final Button button4 = (Button) findViewById(R.id.button4);
        final Button button5 = (Button) findViewById(R.id.button5);
        final Button button6 = (Button) findViewById(R.id.button6);
        final Button button7 = (Button) findViewById(R.id.button7);
        final Button button8 = (Button) findViewById(R.id.button8);

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
                Bundle bundle = msg.getData();
                String operation = bundle.getString("operation");
                String data = bundle.getString("data");
                if (operation.equals("displayBait")) {
                    TextView baitTextView = (TextView) findViewById(R.id.baitTextView);
                    baitTextView.setText(result);
                } else
                if (operation.equals("displayResponses")) {
                    data = "empty|"+data;
                    String [] responses = data.split("\\|");
                    int numresponses = responses.length-1;
                    if (numresponses>0) { button1.setText(responses[1]); }
                    if (numresponses>1) { button2.setText(responses[2]); }
                    if (numresponses>2) { button3.setText(responses[3]); }
                    if (numresponses>3) { button4.setText(responses[4]); }
                    if (numresponses>4) { button5.setText(responses[5]); }
                    if (numresponses>5) { button6.setText(responses[6]); }
                    if (numresponses>6) { button7.setText(responses[7]); }
                    if (numresponses>7) { button8.setText(responses[8]); }
                }
            } // end handleMessage()
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

                // STEP -1: GET BAIT

                try {
                    String url = "http://ec2-52-3-241-249.compute-1.amazonaws.com/ccz_get_bait.php";
                    HttpClient httpclient = new DefaultHttpClient();
                    HttpPost post = new HttpPost(url);
                    List<NameValuePair> urlParameters = new ArrayList<>();
                    urlParameters.add(new BasicNameValuePair("roomname", roomname));
                    post.setEntity(new UrlEncodedFormEntity(urlParameters));
                    HttpResponse response = httpclient.execute(post);
                    result = EntityUtils.toString(response.getEntity());
                    Log.d("GP get bait res-1:", result);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Message msg = Message.obtain();
                handler.removeCallbacks(this);  // Clear message queue
                Bundle bundle = new Bundle();
                bundle.putString("operation","displayBait");
                bundle.putString("data", result);
                msg.setData(bundle);
                handler.sendMessage(msg);


                // STEP 0: SEE IF DEALER
                // ###### SEE IF DEALER GOES HERE ######

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
                            String url = "http://ec2-52-3-241-249.compute-1.amazonaws.com/ccz_get_users_responses.php";
                            HttpClient httpclient = new DefaultHttpClient();
                            HttpPost post = new HttpPost(url);
                            List<NameValuePair> urlParameters = new ArrayList<>();
                            urlParameters.add(new BasicNameValuePair("roomname", roomname));
                            urlParameters.add(new BasicNameValuePair("action", "waitforallfull"));
                            post.setEntity(new UrlEncodedFormEntity(urlParameters));
                            HttpResponse response = httpclient.execute(post);
                            result = EntityUtils.toString(response.getEntity());
                            Log.d("GP dealer getresp res1:", result);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        // When desired result, move past while and to step 2
                        if (!result.equals("Submissions are neither empty nor full")) {
                            msg = Message.obtain();
                            handler.removeCallbacks(this);  // Clear message queue
                            Bundle bundle2 = new Bundle();
                            bundle.putString("operation","displayResponses");
                            bundle.putString("data", result);
                            msg.setData(bundle);
                            handler.sendMessage(msg);
                            waitForAllSubmissions=false;
                        }
                    } // end while (waitForAllSubmissions)


                    // STEP 2: SET TURNPROGRESS TO allreponsesin (don't use underscores... URLencoding, remember?)
                    try {
                        String url = "http://ec2-52-3-241-249.compute-1.amazonaws.com/ccz_set_turn_progress.php";
                        HttpClient httpclient = new DefaultHttpClient();
                        HttpPost post = new HttpPost(url);
                        List<NameValuePair> urlParameters = new ArrayList<>();
                        urlParameters.add(new BasicNameValuePair("roomname", roomname));
                        urlParameters.add(new BasicNameValuePair("turnprogress", "allresponsesin"));
                        post.setEntity(new UrlEncodedFormEntity(urlParameters));
                        HttpResponse response = httpclient.execute(post);
                        result = EntityUtils.toString(response.getEntity());
                        Log.d("GP dealer tprog res2:", result);
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

                    // STEP 4: PROCESS WINNER
                    try {
                        String url = "http://ec2-52-3-241-249.compute-1.amazonaws.com/ccz_process_winner.php";
                        HttpClient httpclient = new DefaultHttpClient();
                        HttpPost post = new HttpPost(url);
                        List<NameValuePair> urlParameters = new ArrayList<>();
                        urlParameters.add(new BasicNameValuePair("roomname", roomname));
                        urlParameters.add(new BasicNameValuePair("user", String.valueOf(buttonClicked)));
                        post.setEntity(new UrlEncodedFormEntity(urlParameters));
                        HttpResponse response = httpclient.execute(post);
                        result = EntityUtils.toString(response.getEntity());
                        Log.d("GP process winner res3:", result);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    // STEP 5: PROCESS GAME OVER
                    //  #############
                    // GAME OVER CODE
                    // ##############

                    // STEP 6: WAIT FOR ALL SUBMISSIONS TO EMPTY
                    waitForAllSubmissions=true;
                    while (waitForAllSubmissions) {
                        try {
                            Thread.sleep(1210);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        // See if server is OK yet for given php file and paramater (see examples about 10 lines up)
                        try {
                            String url = "http://ec2-52-3-241-249.compute-1.amazonaws.com/ccz_get_users_responses.php";
                            HttpClient httpclient = new DefaultHttpClient();
                            HttpPost post = new HttpPost(url);
                            List<NameValuePair> urlParameters = new ArrayList<>();
                            urlParameters.add(new BasicNameValuePair("roomname", roomname));
                            urlParameters.add(new BasicNameValuePair("action", "waitforallempty"));
                            post.setEntity(new UrlEncodedFormEntity(urlParameters));
                            HttpResponse response = httpclient.execute(post);
                            result = EntityUtils.toString(response.getEntity());
                            Log.d("GP dealer getresp res6:", result);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        if (!result.equals("Submissions are neither empty nor full")) {
                            System.out.println("Dealer moving to set bait");
                            waitForAllSubmissions=false;
                        }

                    }  // end while waitForAllSubmissions

                    // STEP 7: SET BAIT
                    try {
                        String url = "http://ec2-52-3-241-249.compute-1.amazonaws.com/ccz_set_bait.php";
                        HttpClient httpclient = new DefaultHttpClient();
                        HttpPost post = new HttpPost(url);
                        List<NameValuePair> urlParameters = new ArrayList<>();
                        urlParameters.add(new BasicNameValuePair("roomname", roomname));
                        urlParameters.add(new BasicNameValuePair("turnprogress", "baitset"));
                        post.setEntity(new UrlEncodedFormEntity(urlParameters));
                        HttpResponse response = httpclient.execute(post);
                        result = EntityUtils.toString(response.getEntity());
                        Log.d("GP dealer setbait res7:", result);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    // STEP 8: START OVER (as !dealer)

                } else { // !dealer
                    // GET BAIT
                    try {
                        String url = "http://ec2-52-3-241-249.compute-1.amazonaws.com/ccz_get_bait.php";
                        HttpClient httpclient = new DefaultHttpClient();
                        HttpPost post = new HttpPost(url);
                        List<NameValuePair> urlParameters = new ArrayList<>();
                        urlParameters.add(new BasicNameValuePair("roomname", roomname));
                        post.setEntity(new UrlEncodedFormEntity(urlParameters));
                        HttpResponse response = httpclient.execute(post);
                        result = EntityUtils.toString(response.getEntity());
                        Log.d("GP !dealer gbait res1:", result);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


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
        buttonClicked=1;
        synchronized (turnThread) {
            turnThread.notify(); }

    } // end buttonClick

    public void button2Click(View view) {
        System.out.println("button2Click");
        buttonClicked=2;
        synchronized (turnThread) {
            turnThread.notify(); }
    } // end buttonClick

    public void button3Click(View view) {
        System.out.println("button3Click");
        buttonClicked=3;
        synchronized (turnThread) {
            turnThread.notify(); }
    } // end buttonClick

    public void button4Click(View view) {
        System.out.println("button4Click");
        buttonClicked=4;
        synchronized (turnThread) {
            turnThread.notify(); }
    } // end buttonClick

    public void button5Click(View view) {
        System.out.println("button5Click");
        buttonClicked=5;
        synchronized (turnThread) {
            turnThread.notify(); }
    } // end buttonClick

    public void button6Click(View view) {
        System.out.println("button6Click");
        buttonClicked=6;
        synchronized (turnThread) {
            turnThread.notify(); }
    } // end buttonClick

    public void button7Click(View view) {
        System.out.println("button7Click");
        buttonClicked=7;
        synchronized (turnThread) {
            turnThread.notify(); }
    } // end buttonClick

    public void button8Click(View view) {
        System.out.println("button8Click");
        buttonClicked=8;
        synchronized (turnThread) {
            turnThread.notify(); }
    } // end buttonClick

}
