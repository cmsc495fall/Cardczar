package com.example.crimson30.cardczar;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
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
import java.util.Objects;

public class GameplayActivity extends Activity {
    String result;   // LAMP server result
    String roomname; // room name (database name)
    String username;
    Boolean host;     // if role="host", then user can boot other users
    Boolean dealer;
    String [] cards;
    String [] usernames;
    String responses;
    int numusers;
    int myUserNum;
    int buttonClicked;
    String buttonClickedString;
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
                Log.d("GP oncreate set_bait:", result);
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
            Log.d("GP onCreate draw_eight:", result);
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
            Log.d("GP onCreate user_list:", result);
        } catch (IOException e) { e.printStackTrace(); }

        // Add blank value, so that 1st card is usernames[1], not usernames[0], etc.
        // Makes it easier for developer to deal with
        result = "empty|"+result;
        usernames = result.split("\\|");
        numusers = usernames.length-1;
        System.out.println("numusers = "+numusers);
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

        final Drawable originalBackground = button1.getBackground();

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
                int intdata = bundle.getInt("intdata");
                if (operation.equals("displayBait")) {
                    TextView baitTextView = (TextView) findViewById(R.id.baitTextView);
                    baitTextView.setText(data);
                } else
                if (operation.equals("displayButtons")) {
                    // Note: Data for this arrives as something like "empty|button1Text|button2Text"
                    // The displayButtons code below breaks up the data to figure out how many buttons to display
                    // If winner is being highlighted, winner will contain a string other than "0"
                    String [] buttonArray = data.split("\\|");
                    int numButtons = buttonArray.length-1;
                    if (numButtons>0) { // BUTTON 1 DISPLAY
                        button1.setVisibility(View.VISIBLE);
                        button1.setText(buttonArray[1]);
                        if (intdata==1) { button1.setBackgroundColor(Color.BLUE);} else { button1.setBackground(originalBackground); }
                    } else { button1.setVisibility(View.GONE);
                    } // end BUTTON 1 DISPLAY

                    if (numButtons>1) { // BUTTON 2 DISPLAY
                        button2.setVisibility(View.VISIBLE);
                        button2.setText(buttonArray[2]);
                        if (intdata==2) { button2.setBackgroundColor(Color.BLUE);} else { button2.setBackground(originalBackground); }
                    }    else { button2.setVisibility(View.GONE);
                    } // end BUTTON 2 DISPLAY

                    if (numButtons>2) { // BUTTON 3 DISPLAY
                        button3.setVisibility(View.VISIBLE);
                        button3.setText(buttonArray[3]);
                        if (intdata==3) { button3.setBackgroundColor(Color.BLUE);} else { button3.setBackground(originalBackground); }
                    }    else { button3.setVisibility(View.GONE);
                    } // end BUTTON 3 DISPLAY

                    if (numButtons>3) { // BUTTON 4 DISPLAY
                        button4.setVisibility(View.VISIBLE);
                        button4.setText(buttonArray[4]);
                        if (intdata==4) { button4.setBackgroundColor(Color.BLUE);} else { button4.setBackground(originalBackground); }
                    }    else { button4.setVisibility(View.GONE);
                    } // end BUTTON 4 DISPLAY

                    if (numButtons>4) { // BUTTON 5 DISPLAY
                        button5.setVisibility(View.VISIBLE);
                        button5.setText(buttonArray[5]);
                        if (intdata==5) { button5.setBackgroundColor(Color.BLUE);} else { button5.setBackground(originalBackground); }
                    }    else { button5.setVisibility(View.GONE);
                    } // end BUTTON 5 DISPLAY

                    if (numButtons>5) { // BUTTON 6 DISPLAY
                        button6.setVisibility(View.VISIBLE);
                        button6.setText(buttonArray[6]);
                        if (intdata==6) { button6.setBackgroundColor(Color.BLUE);} else { button6.setBackground(originalBackground); }
                    }    else { button6.setVisibility(View.GONE);
                    } // end BUTTON 6 DISPLAY

                    if (numButtons>6) { // BUTTON 7 DISPLAY
                        button7.setVisibility(View.VISIBLE);
                        button7.setText(buttonArray[7]);
                        if (intdata==7) { button7.setBackgroundColor(Color.BLUE);} else { button7.setBackground(originalBackground); }
                    }    else { button7.setVisibility(View.GONE);
                    } // end BUTTON 7 DISPLAY

                    if (numButtons>7) { // BUTTON 8 DISPLAY
                        button8.setVisibility(View.VISIBLE);
                        button8.setText(buttonArray[8]);
                        if (intdata==8) { button8.setBackgroundColor(Color.BLUE);} else { button8.setBackground(originalBackground); }
                    }    else { button8.setVisibility(View.GONE);
                    } // end BUTTON 8 DISPLAY

                } else if (operation.equals("newResponse")) { // FOR JUST CHANGING BUTTON LAST CLICKED
                    if (buttonClicked==1) { button1.setText(data); } else
                    if (buttonClicked==2) { button2.setText(data); } else
                    if (buttonClicked==3) { button3.setText(data); } else
                    if (buttonClicked==4) { button4.setText(data); } else
                    if (buttonClicked==5) { button5.setText(data); } else
                    if (buttonClicked==6) { button6.setText(data); } else
                    if (buttonClicked==7) { button7.setText(data); } else
                    if (buttonClicked==8) { button8.setText(data); }

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
                    Log.d("GP step -1, get_bait:", result);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Message msg = Message.obtain();
                handler.removeCallbacks(this);  // Clear message queue
                Bundle bundle = new Bundle();
                bundle.putString("operation","displayBait");
                bundle.putString("data", result);
                bundle.putInt("intdata", 0);
                msg.setData(bundle);
                handler.sendMessage(msg);


                // STEP 0: SEE IF DEALER
                try {
                    String url = "http://ec2-52-3-241-249.compute-1.amazonaws.com/ccz_get_dealer_num.php";
                    HttpClient httpclient = new DefaultHttpClient();
                    HttpPost post = new HttpPost(url);
                    List<NameValuePair> urlParameters = new ArrayList<>();
                    urlParameters.add(new BasicNameValuePair("roomname", roomname));
                    post.setEntity(new UrlEncodedFormEntity(urlParameters));
                    HttpResponse response = httpclient.execute(post);
                    result = EntityUtils.toString(response.getEntity());
                    Log.d("GP step 0, get_dealer:", result);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (myUserNum==Integer.parseInt(result)) { dealer = true; } else { dealer = false; }

                System.out.println("Dealer = "+result+"; myUserNum = "+myUserNum+"; so me being dealer = "+dealer);

                if (dealer) { // DEALER CODE

                    // DEALER POPULATES BUTTONS
                    String buttonText = "empty|";
                    for (int i = 0; i < numusers; i++) { buttonText=buttonText+"WAIT FOR RESPONSE|"; }
                    msg = Message.obtain();
                    handler.removeCallbacks(this);  // Clear message queue
                    bundle.putString("operation", "displayButtons");
                    bundle.putString("data", buttonText);
                    bundle.putInt("intdata", 0); // intdata is not used until later (for winner highlight)
                    msg.setData(bundle);
                    handler.sendMessage(msg);


                    // STEP 1: WAIT FOR SUBMISSIONS TO FILL UP
                    boolean waitForAllSubmissions = true;
                    while (waitForAllSubmissions) {
                        try {
                            Thread.sleep(1210);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        // See if server is OK yet for given php file and parameter
                        // Note: ccz_get_users_responses.php returns "Submissions are neither empty nor full"
                        //       when responses aren't all ready.
                        //       When ready, it returns the actual responses from all users.
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
                            Log.d("GP dealer step1:", result);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        // System.out.println("Data about to be passed to handler: "+result);

                        // When desired result, pass all responses to handler for display, move past while and to step 2
                        if (!result.equals("Submissions are neither empty nor full")) {
                            // Pad result, since display function does not use responses[0]
                            result = "empty|"+result;

                            msg = Message.obtain();
                            handler.removeCallbacks(this);  // Clear message queue
                            bundle.putString("operation", "displayButtons");
                            bundle.putString("data", result);
                            bundle.putInt("intdata", 0); // intdata is not used until later (for winner highlight)
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
                        Log.d("GP dealer step2:", result);
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
                        // Note: user number, not username
                        urlParameters.add(new BasicNameValuePair("user", String.valueOf(buttonClicked)));
                        post.setEntity(new UrlEncodedFormEntity(urlParameters));
                        HttpResponse response = httpclient.execute(post);
                        result = EntityUtils.toString(response.getEntity());
                        Log.d("GP dealer step4:", result);
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

                        // See if server is OK yet for given php file and parameter
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
                            Log.d("GP dealer step6:", result);
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
                        Log.d("GP dealer step7:", result);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                    // STEP 8: START OVER (as !dealer)

                } else { // !DEALER CODE

                    // NOT-DEALER POPULATES BUTTONS
                    msg = Message.obtain();
                    handler.removeCallbacks(this);  // Clear message queue
                    bundle.putString("operation", "displayButtons");
                    bundle.putString("data", TextUtils.join("|", cards));
                    bundle.putInt("intdata", 0); // intdata is not used until later (for winner highlight)
                    msg.setData(bundle);
                    handler.sendMessage(msg);


                    // STEP 1: WAIT FOR BUTTON (USER SUBMISSION)
                    synchronized (this) {
                        try {
                            wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }


                    // STEP 2: SUBMIT RESPONSE TO SERVER AND DRAW CARD
                    try {
                        String url = "http://ec2-52-3-241-249.compute-1.amazonaws.com/ccz_user_submit.php";
                        HttpClient httpclient = new DefaultHttpClient();
                        HttpPost post = new HttpPost(url);
                        List<NameValuePair> urlParameters = new ArrayList<>();
                        urlParameters.add(new BasicNameValuePair("roomname", roomname));
                        urlParameters.add(new BasicNameValuePair("username", username));
                        // Note: user number, not username
                        urlParameters.add(new BasicNameValuePair("submission", buttonClickedString));
                        post.setEntity(new UrlEncodedFormEntity(urlParameters));
                        HttpResponse response = httpclient.execute(post);
                        result = EntityUtils.toString(response.getEntity());
                        Log.d("GP !dealer step2:", result);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    // Set local array to result for new card (replace old/submitted card)
                    cards[buttonClicked]=result;

                    // Result from ccz_user_submit.php is replacement response (replace old card)
                    msg = Message.obtain();
                    handler.removeCallbacks(this);  // Clear message queue
                    bundle.putString("operation", "newResponse");
                    bundle.putString("data", result);
                    bundle.putInt("intdata", 0); // intdata is not used until later (for winner highlight)
                    msg.setData(bundle);
                    handler.sendMessage(msg);


                    // STEP 3: WAIT FOR TURN PROGRESS TO CHANGE TO allresponsesin
                    boolean waitForAllSubmissions = true;
                    while (waitForAllSubmissions) {
                        try {
                            Thread.sleep(1210);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        // See if server is OK yet for given php file and parameter
                        try {
                            String url = "http://ec2-52-3-241-249.compute-1.amazonaws.com/ccz_get_turn_progress.php";
                            HttpClient httpclient = new DefaultHttpClient();
                            HttpPost post = new HttpPost(url);
                            List<NameValuePair> urlParameters = new ArrayList<>();
                            urlParameters.add(new BasicNameValuePair("roomname", roomname));
                            post.setEntity(new UrlEncodedFormEntity(urlParameters));
                            HttpResponse response = httpclient.execute(post);
                            result = EntityUtils.toString(response.getEntity());
                            Log.d("GP !dealer step3:", result);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        // When desired result, move past while and to step 4
                        if (result.equals("allresponsesin")) {
                            waitForAllSubmissions=false;
                        }
                    } // end while (waitForAllSubmissions)


                    // STEP 4: GET RESPONSES, STORE IN LOCAL ARRAY
                    // Note: ccz_get_users_responses.php returns "Submissions are neither empty nor full" when responses aren't all ready.
                    //       When ready, it returns the actual responses from all users.
                    // In this case, it should be full, since turnprogress was set to allresponsesin
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
                        Log.d("GP !dealer step4:", result);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    // Put responses in array so all users can see all responses (in step 7 SHOW RESPONSES)
                    // Add blank value, so that 1st response is responses[1], not responses[0], etc.
                    // Makes it easier for developer to deal with
                    responses = "empty|"+result;


                    // STEP 5: WAIT ON DEALER SELECTION (TURN PROGRESS winnerpicked or gameover)
                    boolean waitForDealer = true;
                    while (waitForDealer) {
                        try {
                            Thread.sleep(1210);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        // See if server is OK yet for given php file and parameter
                        try {
                            String url = "http://ec2-52-3-241-249.compute-1.amazonaws.com/ccz_get_turn_progress.php";
                            HttpClient httpclient = new DefaultHttpClient();
                            HttpPost post = new HttpPost(url);
                            List<NameValuePair> urlParameters = new ArrayList<>();
                            urlParameters.add(new BasicNameValuePair("roomname", roomname));
                            post.setEntity(new UrlEncodedFormEntity(urlParameters));
                            HttpResponse response = httpclient.execute(post);
                            result = EntityUtils.toString(response.getEntity());
                            Log.d("GP !dealer step5:", result);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        // When desired result, move past while and to step 6
                        if (result.equals("winnerpicked") || (result.equals("gameover"))) {
                            waitForDealer=false;
                        }
                    } // end while (waitForDealer)


                    // STEP 6: GET WINNER
                    // See if server is OK yet for given php file and parameter
                    try {
                        String url = "http://ec2-52-3-241-249.compute-1.amazonaws.com/ccz_get_winner_num.php";
                        HttpClient httpclient = new DefaultHttpClient();
                        HttpPost post = new HttpPost(url);
                        List<NameValuePair> urlParameters = new ArrayList<>();
                        urlParameters.add(new BasicNameValuePair("roomname", roomname));
                        post.setEntity(new UrlEncodedFormEntity(urlParameters));
                        HttpResponse response = httpclient.execute(post);
                        result = EntityUtils.toString(response.getEntity());
                        Log.d("GP !dealer step6:", result);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                    // STEP 7: SHOW RESPONSES
                    msg = Message.obtain();
                    handler.removeCallbacks(this);  // Clear message queue
                    bundle.putString("operation","displayButtons");
                    bundle.putString("data", responses);
                    // Pass winner number to highlight when displaying
                    bundle.putInt("intdata", Integer.valueOf(result));
                    msg.setData(bundle);
                    handler.sendMessage(msg);



                    // STEP 8: IF GAME OVER, THEN DO GAME OVER CODE
                    // GAME OVER CODE HERE


                    // STEP 9: CHANGE RESPONSE BACK TO "WAIT FOR RESPONSE"
                    // This is so that dealer knows to set bait
                    try {
                        String url = "http://ec2-52-3-241-249.compute-1.amazonaws.com/ccz_change_single_response.php";
                        HttpClient httpclient = new DefaultHttpClient();
                        HttpPost post = new HttpPost(url);
                        List<NameValuePair> urlParameters = new ArrayList<>();
                        urlParameters.add(new BasicNameValuePair("roomname", roomname));
                        // Note: user number, not username
                        urlParameters.add(new BasicNameValuePair("user", String.valueOf(myUserNum)));
                        post.setEntity(new UrlEncodedFormEntity(urlParameters));
                        HttpResponse response = httpclient.execute(post);
                        result = EntityUtils.toString(response.getEntity());
                        Log.d("GP !dealer step9:", result);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    // STEP 10: WAIT FOR TURN PROGRESS TO GO TO baitset
                    waitForDealer = true;
                    while (waitForDealer) {
                        try {
                            Thread.sleep(1210);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        // See if server is OK yet for given php file and parameter
                        try {
                            String url = "http://ec2-52-3-241-249.compute-1.amazonaws.com/ccz_get_turn_progress.php";
                            HttpClient httpclient = new DefaultHttpClient();
                            HttpPost post = new HttpPost(url);
                            List<NameValuePair> urlParameters = new ArrayList<>();
                            urlParameters.add(new BasicNameValuePair("roomname", roomname));
                            post.setEntity(new UrlEncodedFormEntity(urlParameters));
                            HttpResponse response = httpclient.execute(post);
                            result = EntityUtils.toString(response.getEntity());
                            Log.d("GP !dealer step5:", result);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        // When desired result, move past while and to step 6
                        if (result.equals("baitset")) {
                            waitForDealer=false;
                        }
                    } // end while (waitForDealer)


                    // STEP 11: START OVER (Go back to step -1)

                }  // end if (dealer or !dealer)

            } // end while running
        } // end run()

    } // end turn()


    public void button1Click(View view) {
        System.out.println("button1Click");
        buttonClicked=1;
        Button button = (Button) findViewById(R.id.button1);
        buttonClickedString=button.getText().toString();
        synchronized (turnThread) {
            turnThread.notify(); }

    } // end buttonClick

    public void button2Click(View view) {
        System.out.println("button2Click");
        buttonClicked=2;
        Button button = (Button) findViewById(R.id.button1);
        buttonClickedString=button.getText().toString();
        synchronized (turnThread) {
            turnThread.notify(); }
    } // end buttonClick

    public void button3Click(View view) {
        System.out.println("button3Click");
        buttonClicked=3;
        Button button = (Button) findViewById(R.id.button1);
        buttonClickedString=button.getText().toString();
        synchronized (turnThread) {
            turnThread.notify(); }
    } // end buttonClick

    public void button4Click(View view) {
        System.out.println("button4Click");
        buttonClicked=4;
        Button button = (Button) findViewById(R.id.button1);
        buttonClickedString=button.getText().toString();
        synchronized (turnThread) {
            turnThread.notify(); }
    } // end buttonClick

    public void button5Click(View view) {
        System.out.println("button5Click");
        buttonClicked=5;
        Button button = (Button) findViewById(R.id.button1);
        buttonClickedString=button.getText().toString();
        synchronized (turnThread) {
            turnThread.notify(); }
    } // end buttonClick

    public void button6Click(View view) {
        System.out.println("button6Click");
        buttonClicked=6;
        Button button = (Button) findViewById(R.id.button1);
        buttonClickedString=button.getText().toString();
        synchronized (turnThread) {
            turnThread.notify(); }
    } // end buttonClick

    public void button7Click(View view) {
        System.out.println("button7Click");
        buttonClicked=7;
        Button button = (Button) findViewById(R.id.button1);
        buttonClickedString=button.getText().toString();
        synchronized (turnThread) {
            turnThread.notify(); }
    } // end buttonClick

    public void button8Click(View view) {
        System.out.println("button8Click");
        buttonClicked=8;
        Button button = (Button) findViewById(R.id.button1);
        buttonClickedString=button.getText().toString();
        synchronized (turnThread) {
            turnThread.notify(); }
    } // end buttonClick

}
