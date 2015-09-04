package com.example.crimson30.cardczar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

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

public class HostStartActivity extends Activity {

    String result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host_start);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_host_start, menu);
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

    public void intentToRoom(View view) {

        // EditText roomnameEditText = (EditText) findViewById(R.id.roomnameEditText);
        // EditText hostnameEditText = (EditText) findViewById(R.id.hostnameEditText);


        // StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        // StrictMode.setThreadPolicy(policy);

        /*
        try {
            String url = "http://ec2-52-3-241-249.compute-1.amazonaws.com/ccz_create_db.php";
            HttpClient httpclient = HttpClientBuilder.create().build();
            HttpPost post = new HttpPost(url);
            List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
            urlParameters.add(new BasicNameValuePair("room", roomnameEditText.getText().toString()));
            urlParameters.add(new BasicNameValuePair("host", hostnameEditText.getText().toString()));
            post.setEntity(new UrlEncodedFormEntity(urlParameters));
            HttpResponse response = httpclient.execute(post);
            Log.d("Response of request", response.toString());
            result = EntityUtils.toString(response.getEntity());
        } catch (IOException e) { e.printStackTrace(); }
        */

        EditText roomnameEditText = (EditText) findViewById(R.id.roomnameEditText);
        EditText hostnameEditText = (EditText) findViewById(R.id.hostnameEditText);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        try {
            String url = "http://ec2-52-3-241-249.compute-1.amazonaws.com/ccz_create_db.php";
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost post = new HttpPost(url);
            List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
            urlParameters.add(new BasicNameValuePair("room", "test456"));
            //urlParameters.add(new BasicNameValuePair("host","test456"));
            post.setEntity(new UrlEncodedFormEntity(urlParameters));
            HttpResponse response = httpclient.execute(post);
            Log.d("Response of request", response.toString());
            result = EntityUtils.toString(response.getEntity());
        } catch (IOException e) { e.printStackTrace(); }


        Intent roomIntent = new Intent(this, RoomActivity.class);
        startActivity(roomIntent);
    }

}
