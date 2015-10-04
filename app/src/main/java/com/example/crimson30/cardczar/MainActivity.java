package com.example.crimson30.cardczar;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PictureDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import android.content.Intent;
import android.view.View;

import android.widget.LinearLayout;

import com.caverock.androidsvg.SVG;
import com.caverock.androidsvg.SVGImageView;
import com.caverock.androidsvg.SVGParseException;

/**
 * File: MainActivity
 * Author: Group 2 - Card Czar
 * Date: October 4, 2015
 * Class: CMSC495 6381
 * Instructor: Paul Comitz
 * Project: Card Czar Android App
 * Purpose: The class will handle the functionality when a Card Czar starts the app
 * Status: Ready
 * Notes: None
 */

public class MainActivity extends Activity {

    /**
     * This method is called on creation of the activity.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // ADD SVG COAT OF ARMS BY SETTING LAYOUT BACKGROUND
        // SVG SOURCE: https://commons.wikimedia.org/wiki/File:Coat_of_Arms_of_the_Russian_Federation_bw2.svg
        // SVG AUTHOR: Федеральный конституционный закон «О Государственном гербе Российской Федерации»
        LinearLayout coatLayout = (LinearLayout) findViewById(R.id.coatLayout);
        SVG svg = null;
        try {
            svg = SVG.getFromResource(this, R.raw.coat);
        } catch (SVGParseException e) {
            e.printStackTrace();
        }
        SVGImageView svgImageView = new SVGImageView(this);
        svgImageView.setImageAsset("coat.svg");
        Drawable drawableCoat = new PictureDrawable(svg.renderToPicture());
        coatLayout.setBackground(drawableCoat.getCurrent());

    }

    /**
     * The method is required by the interface. It currently has no unique functionality for the app
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    /**
     * The method will direct the user to host start activity if they click the Host Game button
     * @param view
     */
    public void intentToHostStart(View view) {
        Intent hostIntent = new Intent(this, HostStartActivity.class);
        startActivity(hostIntent);
    }

    /**
     * The method will direct the user to user start activity if they click the Join Game button
     * @param view
     */
    public void intentToUserStart(View view) {
        Intent userIntent = new Intent(this, UserStartActivity.class);
        startActivity(userIntent);
    }
}
