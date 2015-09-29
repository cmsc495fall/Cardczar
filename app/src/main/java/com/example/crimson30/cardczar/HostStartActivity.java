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
import android.widget.TextView;

import org.apache.http.HttpEntity;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

        EditText roomnameEditText = (EditText) findViewById(R.id.roomnameEditText);
        EditText hostnameEditText = (EditText) findViewById(R.id.hostnameEditText);
        TextView serverStatusTextView = (TextView) findViewById(R.id.serverStatusTextView);

        String pattern = "\\w+";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher((roomnameEditText.getText()));
        if (!m.matches()){
            Log.d("Room start error", "No match found");
            serverStatusTextView.setText("The roomname can only contain the characters a-z,A-Z,0-9,_");
            return;
        }else{
            System.out.println(m.group(0));
        }


        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        // Tell LAMP to make database
        try {
            String url = "http://ec2-52-3-241-249.compute-1.amazonaws.com/ccz_create_db.php";
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost post = new HttpPost(url);
            List<NameValuePair> urlParameters = new ArrayList<>();
            urlParameters.add(new BasicNameValuePair("roomname",roomnameEditText.getText().toString()));
            urlParameters.add(new BasicNameValuePair("username",hostnameEditText.getText().toString()));
            post.setEntity(new UrlEncodedFormEntity(urlParameters));
            HttpResponse response = httpclient.execute(post);
            // Log.d("Response of request", response.toString());
            result = EntityUtils.toString(response.getEntity());
            response.getEntity().consumeContent();
            Log.d("Result of create_db", result);
        } catch (IOException e) { e.printStackTrace(); }

        // If server result=OK, intend to Room
        if (Objects.equals(result, "OK")) {
            Intent roomIntent = new Intent(this, RoomActivity.class);
            Bundle extras = new Bundle();
            extras.putString("roomname", roomnameEditText.getText().toString());
            extras.putString("username",hostnameEditText.getText().toString());
            roomIntent.putExtras(extras);
            startActivity(roomIntent);
        } else {
            serverStatusTextView.setText(result);
        }
    } // end intentToRoom()

}
