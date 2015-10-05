package com.example.crimson30.cardczar;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PictureDrawable;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.caverock.androidsvg.SVG;
import com.caverock.androidsvg.SVGImageView;
import com.caverock.androidsvg.SVGParseException;

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

/**
 * File: GameOverActivity
 * Author: Group 2 - Card Czar
 * Date: October 4, 2015
 * Class: CMSC495 6381
 * Instructor: Paul Comitz
 * Project: Card Czar Android App
 * Purpose: This class will handle the functionality when a Card Czar game is completed. The class will display
 *          to the player that the game is over, who won the game and let them go back to the main
 *          game page to start a new game.
 * Status: Ready
 * Notes: None
 */

public class GameOverActivity extends Activity {
    /** Holds the response that comes back from the middleware when database interactions are performed**/
    String result;
    /** The name of the room for the game. It represents the game being played and is also used to as the database name to hold game information**/
    String roomname;
    /** The name of the user who won the game and will be displayed to the user**/
    String winningUser;

    @Override
    /**
     * This method is called on creation of the activity. It will display the specified layout to the user
     * and perform some initial data retrieval and setup
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Specify the activity game over layout
        setContentView(R.layout.activity_game_over);

        // ADD SVG CROWN BY SETTING ImageView in the layout
        // SVG SOURCE: https://commons.wikimedia.org/wiki/File:Heraldic_Royal_Crown_%28Common%29.svg
        // SVG AUTHOR: "Heralder"
       SVG svg = null;
        try {
            svg = SVG.getFromResource(this, R.raw.crown);
        } catch (SVGParseException e) {
            e.printStackTrace();
        }
        SVGImageView svgImageView = new SVGImageView(this);
        Drawable drawableCoat = new PictureDrawable(svg.renderToPicture());
        ImageView coatImage = (ImageView) findViewById(R.id.crownImage);
        coatImage.setImageDrawable(drawableCoat);

        //Get the bundle with information passed from the previous activity
        Bundle extras = getIntent().getExtras();
        roomname = extras.getString("roomname");

        //Make an HTTP call to the database to get the name of the user that won the game
        try {
            String url = "http://ec2-52-3-241-249.compute-1.amazonaws.com/ccz_get_winning_username.php";
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost post = new HttpPost(url);
            List<NameValuePair> urlParameters = new ArrayList<>();
            urlParameters.add(new BasicNameValuePair("roomname", roomname));
            post.setEntity(new UrlEncodedFormEntity(urlParameters));
            HttpResponse response = httpclient.execute(post);
            result = EntityUtils.toString(response.getEntity());
            winningUser = result;
            Log.d("GameOver winner:", result);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Create the message that will be displayed to the user and set the text view
        //in the layout to the message
        TextView gameoverMessage = (TextView) findViewById(R.id.gameOverText);
        StringBuilder displayMessage = new StringBuilder("All hail "+winningUser+",\n");
        displayMessage.append("the new Card Czar!\n\n");
        // displayMessage.append("Game Over");
        gameoverMessage.setText(displayMessage.toString());
        gameoverMessage.setTextSize(TypedValue.COMPLEX_UNIT_DIP,30);
    }

    /**
     * The method will send the user to main activity when the start new button is clicked.
     */
    public void intentToRestartStart(View view) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);


        Intent restartIntent = new Intent(this, MainActivity.class);
        startActivity(restartIntent);
    }

    @Override
    /**
     * The method is required by the interface. It currently has no unique functionality for the app
     */
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_game_over, menu);
        return true;
    }

    @Override
    /**
     * The method is required by the interface. It currently has no unique functionality for the app
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
}
