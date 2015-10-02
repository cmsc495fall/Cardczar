package com.example.crimson30.cardczar;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

/**
 * File: QuitGameActivity
 * Author: Group 2 - Card Czar
 * Date: October 4, 2015
 * Class: CMSC495 6381
 * Instructor: Paul Comitz
 * Project: Card Czar Android App
 * Purpose: The class will handle the functionality when a user chooses to quit the game
 * Status: Ready
 * Notes: None
 */
public class QuitGameActivity extends Activity {

    /**
     * This method is called on creation of the activity.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quit_game);
    }

    /**
     * The method is required by the interface. It currently has no unique functionality for the app
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_quit_game, menu);
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
}
