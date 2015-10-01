package com.example.crimson30.cardczar;

import android.app.Activity;
import android.content.Intent;
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
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * File: GameplayActivity
 * Author: Group 2 - Card Czar
 * Date: October 4, 2015
 * Class: CMSC495 6381
 * Instructor: Paul Comitz
 * Problem: Card Czar Android App
 * Purpose: The class handles the game play between pleyers. The game play will dertmine who the dealer is for each
 *          round of the game, determine the view (dealer or non-dealer) that the user will see, handle when a round
 *          winner is picked (save winner, add a point to the winner's total, advance to the next round), and get the
 *          bait for the next round or handle game over when a user wins game.
 * Status: Ready
 * Notes: None
 */

public class GameplayActivity extends Activity {
    /** Holds the response that comes back from the middleware when database interactions are preformed**/
    String result;
    /** The name of the room for the game. It represents the game being played and is also used to as the database name to hold game information**/
    String roomname;
    /** The anme of the user playing the game **/
    String username;
    /** True if this used is the game host, false otherwise **/
    Boolean host;
    /** True if this user is the dealer for the round, false otherwise **/
    Boolean dealer;
    /** The repsonse cards that the user is holding and can play during each round.
     *  As a card is played a new one will be drawn
     */
    String [] cards;
    /** List of users playing the game **/
    String [] users;
    /** response cards played by all users in the last round**/
    String responses;
    /** Number of users playing the game **/
    int numusers;
    /** The user id, from the users table in the database, for his user **/
    int myUserNum;
    /** The button clicked either for the response card played or the winning response picked **/
    int buttonClicked;
    /** THe round currently being played**/
    int round;
    /** The text of the button that was clicked either for the response card played ot thw winning response picked **/
    String buttonClickedString;

    /** The thread that runs during the dame and handles game play after initial set up **/
    Turn turnThread;
    /** Processes messages sent during the game that updates the display the user sees **/
    Handler handler;


    @Override
    /**
     * This method is called on creation of the activity. It will display the specified layout to the user
     * and perform some initial data retrieval and setup
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gameplay);

        // Get variables from previous activity
        Bundle extras = getIntent().getExtras();
        roomname = extras.getString("roomname");
        username = extras.getString("username");
        host = extras.getBoolean("host");
        round = 0;

        //INITIAL GAMEPLAY SETUP: Step 1
        //This is the first round of the game so the if the user is the host they
        //are also the dealer
        if (host) { dealer=true; } else { dealer=false; }

        //INITIAL GAMEPLAY SETUP: Step 2
        //If the user is the dealer get the set the bait question in the database
        if (dealer) {
            // Draw the first bait question from the database and store it in the gamestate table
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
        } // end if delaer set bait

        //INITIAL GAMEPLAY SETUP: Step 3
        // Every player will draw their initial 8 response cards from the database and store
        // them in the cards array variable
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

        //INITIAL GAMEPLAY SETUP: Step 4
        // Every users will get the list of user playing the game from the database
        // and store them in the users array variable. The numusers variable is set
        // based of the number of user returned. The myUserNum varaible is determined
        // from the list of users returned and the user's username
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
        result = "0 empty|"+result;
        users = result.split("\\|");
        numusers = users.length-1;
        System.out.println("numusers = "+numusers);
        String [] substrings;

        //Loop through the list of user and see if one of them matches this user's username
        //if so the index in the array is the user id number
        for (String value : users) {

            substrings = value.split(" ");
            try {
                substrings[1]=java.net.URLDecoder.decode(substrings[1], "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            System.out.println("Substrings[0] = "+substrings[0]+", substrings[1] = "+substrings[1]);
            if (substrings[1].equals(username)) {
                myUserNum = Integer.parseInt(substrings[0]);
                break;
            }
        }
        System.out.println("myUserNum = "+myUserNum);


        //INITIAL GAMEPLAY SETUP: Step 5
        //Get a reference to the buttons on in the view and set their text
        //depending on whether the user is the deal or the player
        final Button button1 = (Button) findViewById(R.id.button1);
        final Button button2 = (Button) findViewById(R.id.button2);
        final Button button3 = (Button) findViewById(R.id.button3);
        final Button button4 = (Button) findViewById(R.id.button4);
        final Button button5 = (Button) findViewById(R.id.button5);
        final Button button6 = (Button) findViewById(R.id.button6);
        final Button button7 = (Button) findViewById(R.id.button7);
        final Button button8 = (Button) findViewById(R.id.button8);

        final Drawable originalBackground = button1.getBackground();

        //If the user is the dealer, set the text for all the buttons to "WAIT FOR REPSONSE". There should only be
        //enough buttons for all the players in the game minus the dealer. If the user is not the dealer than their
        //response cards, from the cards array variable, will be used to set the buttons' text.
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

        //INITIAL GAMEPLAY SETUP: Step 6
        //Create the Turn thread, that handles the game play, and start the thread
        turnThread=new Turn();
        turnThread.start();

        //The handle will recieve messages sent but the Turn thread and update the widgets on the view
        //depending on the date in the message
        handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                //When a message is recieved get the bundle holding the data to be processed. From the
                //bundle retrieve the operation and data information
                Bundle bundle = msg.getData();
                String operation = bundle.getString("operation");
                String data = bundle.getString("data");

                //If the operation is to display the bait then the data information should be the bait
                //question for the round and will be displayed in the baitTextView widget
                if (operation.equals("displayBait")) {
                    System.out.println("in handler display Bait");
                    TextView baitTextView = (TextView) findViewById(R.id.baitTextView);
                    baitTextView.setText(data);
                //If the operation is to display the buttons then the data is a pipe delimited list string where
                //where each delimited value is the text for one of the buttons on the screen. The number of buttons
                //that will be displayed is based on the number of delimited values in the data string
                } else if (operation.equals("displayButtons")) {
                    // Note: Data for this arrives as something like "empty|button1Text|button2Text"
                    // The displayButtons code below breaks up the data to figure out how many buttons to display
                    // If winner is being highlighted, winner will contain a string other than "0"
                    String [] buttonArray = data.split("\\|");
                    int numButtons = buttonArray.length-1;
                    if (numButtons>0) { // BUTTON 1 DISPLAY
                        button1.setVisibility(View.VISIBLE);
                        button1.setText(buttonArray[1]);
                    } else {
                        button1.setVisibility(View.GONE);
                    } // end BUTTON 1 DISPLAY

                    if (numButtons>1) { // BUTTON 2 DISPLAY
                        button2.setVisibility(View.VISIBLE);
                        button2.setText(buttonArray[2]);
                    } else {
                        button2.setVisibility(View.GONE);
                    } // end BUTTON 2 DISPLAY

                    if (numButtons>2) { // BUTTON 3 DISPLAY
                        button3.setVisibility(View.VISIBLE);
                        button3.setText(buttonArray[3]);
                    } else {
                        button3.setVisibility(View.GONE);
                    } // end BUTTON 3 DISPLAY

                    if (numButtons>3) { // BUTTON 4 DISPLAY
                        button4.setVisibility(View.VISIBLE);
                        button4.setText(buttonArray[4]);
                    } else {
                        button4.setVisibility(View.GONE);
                    } // end BUTTON 4 DISPLAY

                    if (numButtons>4) { // BUTTON 5 DISPLAY
                        button5.setVisibility(View.VISIBLE);
                        button5.setText(buttonArray[5]);
                    } else {
                        button5.setVisibility(View.GONE);
                    } // end BUTTON 5 DISPLAY

                    if (numButtons>5) { // BUTTON 6 DISPLAY
                        button6.setVisibility(View.VISIBLE);
                        button6.setText(buttonArray[6]);
                    } else {
                        button6.setVisibility(View.GONE);
                    } // end BUTTON 6 DISPLAY

                    if (numButtons>6) { // BUTTON 7 DISPLAY
                        button7.setVisibility(View.VISIBLE);
                        button7.setText(buttonArray[7]);
                    } else {
                        button7.setVisibility(View.GONE);
                    } // end BUTTON 7 DISPLAY

                    if (numButtons>7) { // BUTTON 8 DISPLAY
                        button8.setVisibility(View.VISIBLE);
                        button8.setText(buttonArray[8]);
                    } else {
                        button8.setVisibility(View.GONE);
                    } // end BUTTON 8 DISPLAY
                //If the operation is new response the set the button that was clicked by
                //the user to be unclicked and set the text for that button to the newly
                //drawn response card
                } else if (operation.equals("newResponse")) { // FOR JUST CHANGING BUTTON LAST CLICKED
                    if (buttonClicked==1) { button1.setText(data); } else
                    if (buttonClicked==2) { button2.setText(data); } else
                    if (buttonClicked==3) { button3.setText(data); } else
                    if (buttonClicked==4) { button4.setText(data); } else
                    if (buttonClicked==5) { button5.setText(data); } else
                    if (buttonClicked==6) { button6.setText(data); } else
                    if (buttonClicked==7) { button7.setText(data); } else
                    if (buttonClicked==8) { button8.setText(data); }
                //IF the operation is to display the winner than the data contains information about
                //what response card won the round and this user's current point total. Display this
                //information in the winnnerTextView widget in the view
                } else if (operation.equals("displayWinner")){
                    TextView winnerTextView = (TextView) findViewById(R.id.winnerText);
                    winnerTextView.setText(data);
                //If the operation is to update the application title then update the title bar
                //to display the current round
                } else if (operation.equals("updateAppBarTitle")){
                    getActionBar().setTitle("Round " + round);
                }
            } // end handleMessage()
        }; // end handler
    }

    @Override
    /**
     * THe method is required by the interface. It currently has no unique functionslity for the app
     */
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_gameplay, menu);
        return true;
    }

    @Override
    /**
     * THe method is required by the interface. It currently has no unique functionslity for the app
     */
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
     * This class is a thread that handles the game play and what should be displayed to the user
     * after initial game play set up. The thread will stop when the game is over or the user
     * quits the game
     */
    class Turn extends Thread {
        /** Set to true and should stay true so the game keeps running unless the game is
         * over or the user quits the game
         */
        private volatile boolean running = true;

        @Override
        public void run() {
            while (running) {
                Message msg;

                //GAMEPLAY All: Step 1
                //All users will wait until the turn progress query returns baitset and then
                //will retrieve the bait from the database and send a message to the handler
                //to display the bait

                //The user will wait until the bait question has been set. The user will query the
                //the database for the bait eabout every second until the bait is set.
                boolean baitNotSet = true;
                while (baitNotSet) {
                    //if the bait has not been set sleep for about 1 second
                    try {
                        Thread.sleep(1210);
                    } catch (InterruptedException e) {
                        Log.d("Thread", "Thread interrupt encountered, -1a");
                        running = false;
                        break;
                    }

                    //Query the database for the turn progress
                    try {
                        String url = "http://ec2-52-3-241-249.compute-1.amazonaws.com/ccz_get_turn_progress.php";
                        HttpClient httpclient = new DefaultHttpClient();
                        HttpPost post = new HttpPost(url);
                        List<NameValuePair> urlParameters = new ArrayList<>();
                        urlParameters.add(new BasicNameValuePair("roomname", roomname));
                        post.setEntity(new UrlEncodedFormEntity(urlParameters));
                        HttpResponse response = httpclient.execute(post);
                        result = EntityUtils.toString(response.getEntity());
                        Log.d("GP !dealer step0:", result);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    //If turn progress inicated the bait has been set then
                    //set the variable to stop the wait loop from running
                    if (result.equals("baitset")) {
                        baitNotSet=false;
                    }
                } // end while (baitNotSet)

                //Get the bait from the database
                try {
                    String url = "http://ec2-52-3-241-249.compute-1.amazonaws.com/ccz_get_bait.php";
                    HttpClient httpclient = new DefaultHttpClient();
                    HttpPost post = new HttpPost(url);
                    List<NameValuePair> urlParameters = new ArrayList<>();
                    urlParameters.add(new BasicNameValuePair("roomname", roomname));
                    post.setEntity(new UrlEncodedFormEntity(urlParameters));
                    HttpResponse response = httpclient.execute(post);
                    result = EntityUtils.toString(response.getEntity());
                    Log.d("GP step -1d, get_bait:", result);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                //Send the bait question in a message to the handle to be displayed
                msg = Message.obtain();
                Bundle gpDisplayBaitBundle = new Bundle();
                handler.removeCallbacks(this);  // Clear message queue
                gpDisplayBaitBundle.putString("operation", "displayBait");
                gpDisplayBaitBundle.putString("data", result);
                msg.setData(gpDisplayBaitBundle);
                handler.sendMessage(msg);


                //GAMEPLAY All: Step 2
                //Increment the round and send a message to the handled to update the title bar
                //with the round information
                round++;

                msg = Message.obtain();
                Bundle actionBarBundle = new Bundle();
                handler.removeCallbacks(this);  // Clear message queue
                actionBarBundle.putString("operation", "updateAppBarTitle");
                actionBarBundle.putString("data", "N/A");
                msg.setData(actionBarBundle);
                handler.sendMessage(msg);

                //GAMEPLAY All: Step 3
                //Get the current dealer's user id from the database and see if this user is the dealer
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

                //If the user is the deal one set op steps will be followed. If the user is not
                //the dealler than another set of steps will be followed
                if (dealer) { // DEALER CODE
                    //GAMEPLAY DEALER: Step 1
                    //Send a message to the handler so that all the dealer's button will display "WAIT FOR RESPONSE".
                    //This is the default until all the users have submitted their reponses to the bait question
                    String buttonText = "empty|";
                    for (int i = 1; i < numusers; i++) {
                        buttonText=buttonText+"WAIT FOR RESPONSE|";
                    }
                    msg = Message.obtain();
                    Bundle dealerDisplayDefaultButtonBundle = new Bundle();
                    handler.removeCallbacks(this);  // Clear message queue
                    dealerDisplayDefaultButtonBundle.putString("operation", "displayButtons");
                    dealerDisplayDefaultButtonBundle.putString("data", buttonText);
                    msg.setData(dealerDisplayDefaultButtonBundle);
                    handler.sendMessage(msg);


                    //GAMEPLAY DEALER: Step 2
                    //Keep querying the database to see if all the users have submitted their responses. If not sleep for awhile.
                    //If there is a response other than "Submissions are neither empty nor full" than all the answers are
                    //in so break the loop and proceed to the next step in the game
                    boolean waitForAllSubmissions = true;
                    while (waitForAllSubmissions) {
                        try {
                            Thread.sleep(1210);
                        } catch (InterruptedException e) {
                            Log.d("Thread","Thread interrupt encountered, d2a");
                            running = false;
                            break;
                        }

                        // STEP 2A: CHECK SERVER
                        // Query the database to get the user's responses
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
                            Log.d("GP dealer step2a:", result);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        // STEP 2B: DISPLAY RESPONSES
                        // Send a message to the handled with the users' responses so they will be
                        // displayed as the text of the button widgets on the dealer's view
                        if (!result.equals("Submissions are neither empty nor full")) {
                            // Pad result, since display function does not use responses[0]
                            result = "empty|"+result;

                            msg = Message.obtain();
                            Bundle dealerDisplayUserButtonBundle = new Bundle();
                            handler.removeCallbacks(this);  // Clear message queue
                            dealerDisplayUserButtonBundle.putString("operation", "displayButtons");
                            dealerDisplayUserButtonBundle.putString("data", result);
                            msg.setData(dealerDisplayUserButtonBundle);
                            handler.sendMessage(msg);
                            waitForAllSubmissions=false;
                        }
                    } // end while (waitForAllSubmissions)

                    //If not running (need to stop the Turn thread, the stop rest of loop processing
                    //and go back to the beginning of the loop
                    if (!running) continue;


                    //GAMEPLAY DEALER: Step 3
                    //Update the database. Set turnprogress in the gamestate table to allresponses in
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
                        Log.d("GP dealer step3:", result);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                    //GAMEPLAY DEALER: Step 4
                    //Wait for the dealer to select the winning response for the round
                    synchronized (this) {
                        try {
                            wait();
                        } catch (InterruptedException e) {
                            Log.d("Thread","Thread interrupt encountered, d4");
                            e.printStackTrace();
                        }
                    }


                    //GAMEPLAY DEALER: Step 5
                    // This will update the database to give the winning user another point. It will also update the game state
                    // information in the database to set the winning response and user, increment the round, set the dealer
                    // to be the round winner and set turn progress to winnerpicked or gameover depending on if a player
                    // has reached 5 points
                    try {
                        String url = "http://ec2-52-3-241-249.compute-1.amazonaws.com/ccz_process_winner.php";
                        HttpClient httpclient = new DefaultHttpClient();
                        HttpPost post = new HttpPost(url);
                        List<NameValuePair> urlParameters = new ArrayList<>();
                        urlParameters.add(new BasicNameValuePair("roomname", roomname));
                        // Note: user number, not username
                        urlParameters.add(new BasicNameValuePair("response", buttonClickedString));
                        post.setEntity(new UrlEncodedFormEntity(urlParameters));
                        HttpResponse response = httpclient.execute(post);
                        result = EntityUtils.toString(response.getEntity());
                        Log.d("GP dealer step5:", result);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                    //GAMEPLAY DEALER: Step 6
                    // Get the round, round winner and winning response from the database to display to the user
                    String[] winnerData;
                    String winner = "-1";
                    String winningResponse = "Not set";
                    int round = -1;

                    //STEP 6A: GET WINNER
                    //Query the database to get the round and winner information
                    try {
                        String url = "http://ec2-52-3-241-249.compute-1.amazonaws.com/ccz_get_winner.php";
                        HttpClient httpclient = new DefaultHttpClient();
                        HttpPost post = new HttpPost(url);
                        List<NameValuePair> urlParameters = new ArrayList<>();
                        urlParameters.add(new BasicNameValuePair("roomname", roomname));
                        post.setEntity(new UrlEncodedFormEntity(urlParameters));
                        HttpResponse response = httpclient.execute(post);
                        result = EntityUtils.toString(response.getEntity());
                        Log.d("GP dealer step6a:", result);
                        winnerData = result.split("\\|");
                        round = Integer.parseInt(winnerData[0]) -1;
                        winner = winnerData[1];
                        winningResponse = winnerData[2];
                        Log.d("GP dealer step6a:", result);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    // STEP 6B: DISPLAY WINNER
                    //Get the this user's points from the database to display to the user
                    String userPoints = "-1";
                    try {
                        String url = "http://ec2-52-3-241-249.compute-1.amazonaws.com/ccz_get_user_points.php";
                        HttpClient httpclient = new DefaultHttpClient();
                        HttpPost post = new HttpPost(url);
                        List<NameValuePair> urlParameters = new ArrayList<>();
                        urlParameters.add(new BasicNameValuePair("roomname", roomname));
                        urlParameters.add(new BasicNameValuePair("user", Integer.toString(myUserNum)));
                        post.setEntity(new UrlEncodedFormEntity(urlParameters));
                        HttpResponse response = httpclient.execute(post);
                        result = EntityUtils.toString(response.getEntity());
                        userPoints = result;
                        Log.d("GP dealer step6b:", result);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    // STEP 6C: Send Message to display round information
                    // Send a message to the handler so the winning response and this user's points are displayed
                    msg = Message.obtain();
                    Bundle dealerWinnerBundle = new Bundle();
                    handler.removeCallbacks(this);  // Clear message queue
                    dealerWinnerBundle.putString("operation", "displayWinner");
                    dealerWinnerBundle.putString("data", "Round " + round + " winner: " + winningResponse + ". Your points:  " + userPoints);
                    msg.setData(dealerWinnerBundle);
                    handler.sendMessage(msg);


                    //GAMEPLAY DEALER: Step 7
                    //Check to see if turn progress is gameover. If so a player has won the game and the
                    //dealer should be directed to the game over activity
                    try {
                        String url = "http://ec2-52-3-241-249.compute-1.amazonaws.com/ccz_get_turn_progress.php";
                        HttpClient httpclient = new DefaultHttpClient();
                        HttpPost post = new HttpPost(url);
                        List<NameValuePair> urlParameters = new ArrayList<>();
                        urlParameters.add(new BasicNameValuePair("roomname", roomname));
                        post.setEntity(new UrlEncodedFormEntity(urlParameters));
                        HttpResponse response = httpclient.execute(post);
                        result = EntityUtils.toString(response.getEntity());
                        Log.d("GP dealer step5:", result);
                        if (result.equals("gameover")){
                            Log.i("Dealer:", "Game Over");
                            intentToGameOver();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    //If not running (need to stop the Turn thread, the stop rest of loop processing
                    //and go back to the beginning of the loop
                    if (!running) continue;

                    //GAMEPLAY DEALER: Step 8
                    //After the round is over query the database to get all the user's responses. Wait
                    //until all the responses have been set to the default. Then get the next
                    //bait qustion and set it the gamestate table

                    // STEP 8: wAIT FOR USER'S SUBNISSION TO BE RESET
                    //Keep querying the submissions in the users table until all submisions
                    //habve been reset, Sleep for about 1 second between queries
                    waitForAllSubmissions=true;
                    while (waitForAllSubmissions) {
                        try {
                            Thread.sleep(1210);
                        } catch (InterruptedException e) {
                            Log.d("Thread","Thread interrupt encountered, d8a");
                            e.printStackTrace();
                        }


                        // Query the database for the user's submissions.
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
                            Log.d("GP dealer step8b:", result);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        if (!result.equals("Submissions are neither empty nor full")) {
                            System.out.println("Dealer moving to set bait");
                            waitForAllSubmissions=false;
                        }

                    }  // end while waitForAllSubmissions


                    // STEP 8B: SET BAIT
                    //Query the database for the next bait question and set value
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
                        Log.d("GP dealer step9:", result);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else { // NON-DEALER CODE
                    //GAMEPLAY NON-DEALER: STEP 1
                    //Send a message to the handler to set the player's buttons
                    //to display their response cards, stored in the cards array variable
                    msg = Message.obtain();
                    Bundle notDealerDefaultButtonBundle = new Bundle();
                    handler.removeCallbacks(this);  // Clear message queue
                    notDealerDefaultButtonBundle.putString("operation", "displayButtons");
                    notDealerDefaultButtonBundle.putString("data", TextUtils.join("|", cards));
                    msg.setData(notDealerDefaultButtonBundle);
                    handler.sendMessage(msg);

                    //If not running (need to stop the Turn thread, the stop rest of loop processing
                    //and go back to the beginning of the loop
                    if (!running) continue;


                    //GAMEPLAY NON-DEALER: STEP 2
                    //User select their repsonse card to the bati question

                    //STEP 2A: Wait for user to select response card
                    synchronized (this) {
                        try {
                            wait();
                        } catch (InterruptedException e) {
                            Log.d("Thread","Thread interrupt encountered, !d2a");
                            running = false;
                        }
                    }

                    //If not running (need to stop the Turn thread, the stop rest of loop processing
                    //and go back to the beginning of the loop
                    if (!running) continue;

                    // STEP 2B: Store the user's response in the users table and get the next reponse
                    // card from the table.
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
                        Log.d("GP !dealer step2B:", result);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    // STEP 2C: Update the cards array variable to replace the response played with
                    // the newly drawn response from step 2B
                    cards[buttonClicked]=result;

                    // STEP 2D: send a message to the handle to unclick the user's reponse
                    // button and set the text to the new reponse card
                    msg = Message.obtain();
                    Bundle notDealerNewCardBundle = new Bundle();
                    handler.removeCallbacks(this);  // Clear message queue
                    notDealerNewCardBundle.putString("operation", "newResponse");
                    notDealerNewCardBundle.putString("data", result);
                    msg.setData(notDealerNewCardBundle);
                    handler.sendMessage(msg);

                    //GAMEPLAY NON-DEALER: STEP 3
                    // Wait until all users have submitted their responses. Query the database about
                    // ever 1 seconds to check if all responses have been submitted.
                    boolean waitForAllSubmissions = true;
                    while (waitForAllSubmissions) {
                        try {
                            Thread.sleep(1210);
                        } catch (InterruptedException e) {
                            Log.d("Thread","Thread interrupt encountered, !d3a");
                            running = false;
                            break;
                        }

                        // Query the databse to get turn progress and see if it indicats all
                        // response ahve been submitted
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

                        if (result.equals("allresponsesin")) {
                            waitForAllSubmissions=false;
                        }
                    } // end while (waitForAllSubmissions)

                    //If not running (need to stop the Turn thread, the stop rest of loop processing
                    //and go back to the beginning of the loop
                    if (!running) continue;

                    //GAMEPLAY NON-DEALER: STEP 4
                    //Get all the users' response. These will be displayed with a future enhancement to the game
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

                    //GAMEPLAY NON-DEALER: STEP 5
                    //Wait for the dealer to pick the wining response, indicated by the turn progress. Turn
                    //progress will either be winner picked or game over. Turn progress will be queried about
                    //every 1 seconds until the appropriate reposnse is returned.
                    boolean waitForDealer = true;
                    while (waitForDealer) {
                        try {
                            Thread.sleep(1210);
                        } catch (InterruptedException e) {
                            Log.d("Thread","Thread interrupt encountered, !d5a");
                            running = false;
                            break;
                        }

                        //Query the databse to get the turn progress
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

                        // STEP 5C: IF TURN PROGRESS IS winnerpicked || gameover, CONTINUE
                        // When desired result, move past while and to step 6
                        if (result.equals("winnerpicked") || (result.equals("gameover"))) {
                            waitForDealer=false;
                        }
                    } // end while (waitForDealer)

                    //If not running (need to stop the Turn thread, the stop rest of loop processing
                    //and go back to the beginning of the loop
                    if (!running) continue;

                    //GAMEPLAY NON-DEALER: STEP 6
                    //If the game is over, as indicated by game progress, then direct the user
                    //to the game over activity
                    try {
                        String url = "http://ec2-52-3-241-249.compute-1.amazonaws.com/ccz_get_turn_progress.php";
                        HttpClient httpclient = new DefaultHttpClient();
                        HttpPost post = new HttpPost(url);
                        List<NameValuePair> urlParameters = new ArrayList<>();
                        urlParameters.add(new BasicNameValuePair("roomname", roomname));
                        post.setEntity(new UrlEncodedFormEntity(urlParameters));
                        HttpResponse response = httpclient.execute(post);
                        result = EntityUtils.toString(response.getEntity());
                        Log.d("GP !dealer step5C:", result);
                        if (result.equals("gameover")){
                            Log.i("!Dealer:", "Game Over");
                            intentToGameOver();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                    //GAMEPLAY NON-DEALER: STEP 7
                    // Get the round, round winner and winning response from the database to display to the user
                    String[] winnerData;
                    String winner = "-1";
                    String winningResponse = "Not set";

                    //STEP 7A: GET WINNER
                    //Query the database to get the round and winner information
                    int round = -1;
                    try {
                        String url = "http://ec2-52-3-241-249.compute-1.amazonaws.com/ccz_get_winner.php";
                        HttpClient httpclient = new DefaultHttpClient();
                        HttpPost post = new HttpPost(url);
                        List<NameValuePair> urlParameters = new ArrayList<>();
                        urlParameters.add(new BasicNameValuePair("roomname", roomname));
                        post.setEntity(new UrlEncodedFormEntity(urlParameters));
                        HttpResponse response = httpclient.execute(post);
                        result = EntityUtils.toString(response.getEntity());
                        Log.d("GP !dealer step6:", result);
                        winnerData = result.split("\\|");
                        round = Integer.parseInt(winnerData[0]) -1;
                        winner = winnerData[1];
                        winningResponse = winnerData[2];
                        Log.d("GP !dealer step6:", result);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    // STEP 7B: DISPLAY WINNER
                    //Get the this user's points from the database to display to the user
                    String userPoints = "-1";
                    try {
                        String url = "http://ec2-52-3-241-249.compute-1.amazonaws.com/ccz_get_user_points.php";
                        HttpClient httpclient = new DefaultHttpClient();
                        HttpPost post = new HttpPost(url);
                        List<NameValuePair> urlParameters = new ArrayList<>();
                        urlParameters.add(new BasicNameValuePair("roomname", roomname));
                        urlParameters.add(new BasicNameValuePair("user", Integer.toString(myUserNum)));
                        post.setEntity(new UrlEncodedFormEntity(urlParameters));
                        HttpResponse response = httpclient.execute(post);
                        result = EntityUtils.toString(response.getEntity());
                        userPoints = result;
                        Log.d("GP !dealer user points:", result);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    // STEP 7C: Send Message to display round information
                    // Send a message to the handler so the winning response and this user's points are displayed
                    msg = Message.obtain();
                    Bundle notDealerWinnerBundle = new Bundle();
                    handler.removeCallbacks(this);  // Clear message queue
                    notDealerWinnerBundle.putString("operation", "displayWinner");
                    notDealerWinnerBundle.putString("data", "Round " + round + " winner: " + winningResponse + ". Your points:  " + userPoints);
                    msg.setData(notDealerWinnerBundle);
                    handler.sendMessage(msg);

                    //GAMEPLAY NON-DEALER: STEP 8
                    // Set this user response submission to the defaul value in the users table
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
                }  // end if (dealer or NON dealer)

            } // end while running
        } // end run()

    } // end turn()


    /**
     * There is a button click bethod for each button. Each method will get the button number and the button's text balue
     */
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
        Button button = (Button) findViewById(R.id.button2);
        buttonClickedString=button.getText().toString();
        synchronized (turnThread) {
            turnThread.notify(); }
    } // end buttonClick

    public void button3Click(View view) {
        System.out.println("button3Click");
        buttonClicked=3;
        Button button = (Button) findViewById(R.id.button3);
        buttonClickedString=button.getText().toString();
        synchronized (turnThread) {
            turnThread.notify(); }
    } // end buttonClick

    public void button4Click(View view) {
        System.out.println("button4Click");
        buttonClicked=4;
        Button button = (Button) findViewById(R.id.button4);
        buttonClickedString=button.getText().toString();
        synchronized (turnThread) {
            turnThread.notify(); }
    } // end buttonClick

    public void button5Click(View view) {
        System.out.println("button5Click");
        buttonClicked=5;
        Button button = (Button) findViewById(R.id.button5);
        buttonClickedString=button.getText().toString();
        synchronized (turnThread) {
            turnThread.notify(); }
    } // end buttonClick

    public void button6Click(View view) {
        System.out.println("button6Click");
        buttonClicked=6;
        Button button = (Button) findViewById(R.id.button6);
        buttonClickedString=button.getText().toString();
        synchronized (turnThread) {
            turnThread.notify(); }
    } // end buttonClick

    public void button7Click(View view) {
        System.out.println("button7Click");
        buttonClicked=7;
        Button button = (Button) findViewById(R.id.button7);
        buttonClickedString=button.getText().toString();
        synchronized (turnThread) {
            turnThread.notify(); }
    } // end buttonClick

    public void button8Click(View view) {
        System.out.println("button8Click");
        buttonClicked=8;
        Button button = (Button) findViewById(R.id.button8);
        buttonClickedString=button.getText().toString();
        synchronized (turnThread) {
            turnThread.notify(); }
    } // end buttonClick

    /**
     * If the guit button is clicked it will stop the Turn thread from running, delete the user
     * from the database and direct the user to the quit game activity
     * @param view
     */
    public void quitButtonClick(View view) {
        Button quitButton = (Button) findViewById(R.id.quit_button);

        //INterrupt the Turn thread and stop it running
        Log.d("Thread", "Interrupting turn thread");
        synchronized (turnThread) {
            turnThread.interrupt();
            turnThread.running=false; }
        //delete the user from the database
        deleteUser();

        //Go to the quit game activity
        Intent quitIntent = new Intent(getApplicationContext(), QuitGameActivity.class);
        Bundle extras = new Bundle();
        quitIntent.putExtras(extras);
        startActivity(quitIntent);
    } // end quitButtonClick

    /**
     * The method will make an HTTP call htat deletes this user from the database
     */
    private void deleteUser(){
        try {
            String url = "http://ec2-52-3-241-249.compute-1.amazonaws.com/ccz_user_quit.php";
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost post = new HttpPost(url);
            List<NameValuePair> urlParameters = new ArrayList<>();
            urlParameters.add(new BasicNameValuePair("roomname", roomname));
            urlParameters.add(new BasicNameValuePair("username", username));
            post.setEntity(new UrlEncodedFormEntity(urlParameters));
            HttpResponse response = httpclient.execute(post);
            result = EntityUtils.toString(response.getEntity());
            Log.d("Result of user quit", result);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * The method will stop the Turn thread from running and direct the user
     * to the game over activity
     */
    public void intentToGameOver() {
        Log.d("Thread", "Gameover, Interrupting turn thread");
        synchronized (turnThread) {
            turnThread.interrupt();
            turnThread.running=false;
        }

        Intent gameoverIntent = new Intent(this, GameOverActivity.class);
        Bundle extras = new Bundle();
        extras.putString("roomname", roomname);
        gameoverIntent.putExtras(extras);
        startActivity(gameoverIntent);
    }
}
